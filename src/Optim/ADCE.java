package Optim;

import MIR.Function;
import MIR.IRinst.*;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.IRtype.Pointer;
import MIR.Root;
import Util.MIRFnGraph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/*
 * principle: I/O, "outer" store and side effect call are necessary
 * "outer" store: the address is "outer"
 * "outer" address: init by globalReg and parameters, propagated by bitCast, getElementPtr
 * notice that an address loaded from outer address is also outer
 * sideEffectCall: a function containing no IO & outer store & sideEffectCall is not side effective.
 * assume all functions are not side effective at the beginning.
 *
 * This is not the most precise one. an improvement is:
 *      record which parameter is affected, if the parameter is not a necessary one,
 *      the call is still not side effective. however, this is too hard to complete; consider:
 *          class a{ b t;} global; class b{int it};
 *          f(b entity) {global.t = entity;}
 *          int main(){
 *              b x;f(x);
 *              printInt(global.t.it);
 *          }
 *      although x SEEMS not useful so f(x) can be eliminated, the fact is not so.
 */

public class ADCE extends Pass {

    private Root irRoot;
    private boolean change, MoreLive;
    private HashSet<Inst> liveCode = new HashSet<>();
    private HashSet<Operand> outerOp = new HashSet<>();
    private MIRFnGraph CallGraph;

    public ADCE(Root irRoot) {
        super();
        this.irRoot = irRoot;
        CallGraph = new MIRFnGraph(irRoot, true);
    }

    private void initFnCollect(Function fn) {
        change = true;
        while(change) {
            change = false;
            fn.blocks().forEach(block -> {
                block.instructions().forEach(inst -> {
                    HashSet<Operand> uses = inst.uses();
                    uses.retainAll(outerOp);
                    if (!uses.isEmpty() &&
                        (inst instanceof BitCast || inst instanceof GetElementPtr ||
                            (inst instanceof Call && inst.dest().type() instanceof Pointer) ||
                            (inst instanceof Load && inst.dest().type() instanceof Pointer))) {
                        change = true;
                        outerOp.add(inst.dest());
                    } else assert inst instanceof Load || inst instanceof Call || uses.isEmpty();
                    //i've never seen such judgement from any textbook, so check if my idea is correct...
                    if (inst.isTerminal()) liveCode.add(inst);
                });
                block.phiInst().forEach((dest, inst) -> {
                    HashSet<Operand> uses = inst.uses();
                    uses.retainAll(outerOp);
                    if (!uses.isEmpty()) {
                        change = true;
                        outerOp.add(inst.dest());
                    }
                });
            });
        }
    }
    private void initCollect() {
        CallGraph.build();
        outerOp.clear();
        //collect "outer" address of each function
        outerOp.addAll(irRoot.globalVar());
        irRoot.functions().forEach((name, fn) -> {
            outerOp.addAll(fn.params());
            initFnCollect(fn);
            fn.setSideEffect(false);
        });
        //collect I/O and side effect functions and outer stores
        irRoot.functions().forEach((name, fn) -> {
            if (!fn.hasSideEffect())
                fn.blocks().forEach(block -> block.instructions().forEach(inst -> {
                    if (inst instanceof Call && ((Call)inst).callee().hasSideEffect()){
                        liveCode.add(inst);
                        fn.setSideEffect(true);
                        CallGraph.callerOf(fn).forEach(func -> func.setSideEffect(true));
                    }
                    else if (inst instanceof Store && outerOp.contains(((Store)inst).address())){
                        liveCode.add(inst);
                        fn.setSideEffect(true);
                        CallGraph.callerOf(fn).forEach(func -> func.setSideEffect(true));
                    }
                }));
        });
    }

    private void tryAdd(Inst inst) {
        inst.uses().forEach(opr -> {
            if (opr.defInst() != null && !liveCode.contains(opr.defInst())) {
                MoreLive = true;
                liveCode.add(opr.defInst());
            }
        });
    }

    private void collect() {
        irRoot.functions().forEach((name, fn) -> {
            MoreLive = true;
            while(MoreLive) {
                MoreLive = false;
                fn.blocks().forEach(block -> {
                    block.instructions().forEach(inst -> {
                        if (liveCode.contains(inst)) tryAdd(inst);
                    });
                    block.phiInst().forEach(((reg, inst) -> {
                        if (liveCode.contains(inst)) tryAdd(inst);
                    }));
                });
            }
        });
    }

    public void clean() {
        irRoot.functions().forEach((name, fn) -> fn.blocks().forEach(block -> {
            for (Iterator<Inst> iter = block.instructions().iterator();iter.hasNext();) {
                Inst inst = iter.next();
                if (!liveCode.contains(inst)) {
                    iter.remove();
                    inst.removeSelf(false);
                    change = true;
                }
            }
            for (Iterator<Map.Entry<Register, Phi>> iter = block.phiInst().entrySet().iterator(); iter.hasNext();) {
                Phi inst = iter.next().getValue();
                if (!liveCode.contains(inst)) {
                    iter.remove();
                    inst.removeSelf(false);
                    change = true;
                }
            }
        }));
    }

    @Override
    public boolean run() {
        liveCode.clear();
        change = false;
        initCollect();
        collect();
        clean();
        return change;
    }
}
