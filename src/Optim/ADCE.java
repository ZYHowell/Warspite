package Optim;

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
//O(n)!O(n)!
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

    private void testOp(Operand op, HashSet<Operand> addedOp) {
        HashSet<Inst> uses = op.uses();
        if (uses != null) {
            uses.forEach(inst -> {
                if (inst instanceof BitCast || inst instanceof GetElementPtr ||
                        (inst instanceof Call && inst.dest().type() instanceof Pointer) ||
                        (inst instanceof Load && inst.dest().type() instanceof Pointer) ||
                        (inst instanceof Phi)) {
                    if (!addedOp.contains(inst.dest()) && !outerOp.contains(inst.dest())){
                        addedOp.add(inst.dest());
                        testOp(inst.dest(), addedOp);
                    }
                } else assert (inst instanceof Load || inst instanceof Call
                    || (inst instanceof Binary && ((Binary)inst).opCode() == Binary.BinaryOpCat.sub));
                    //the last one is to get the size.
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
            fn.setSideEffect(false);
        });

        HashSet<Operand> added = new HashSet<>();
        outerOp.forEach(op -> testOp(op, added));
        outerOp.addAll(added);
        //collect I/O and side effect functions and outer stores and returns
        irRoot.functions().forEach((name, fn) -> {
            if (!fn.hasSideEffect())
                fn.blocks().forEach(block -> block.instructions().forEach(inst -> {
                    if (inst instanceof Call && ((Call)inst).callee().hasSideEffect()){
                        //this includes I/O
                        liveCode.add(inst);
                        fn.setSideEffect(true);
                        CallGraph.callerOf(fn).forEach(func -> func.setSideEffect(true));
                    }
                    else if (inst instanceof Store && outerOp.contains(((Store)inst).address())){
                        liveCode.add(inst);
                        fn.setSideEffect(true);
                        CallGraph.callerOf(fn).forEach(func -> func.setSideEffect(true));
                    } else if (inst instanceof Return) {
                        liveCode.add(inst);
                    }
                }));
        });
    }

    private void tryAdd(Inst inst) {
        inst.uses().forEach(opr -> {
            if (opr.defInst() != null && !liveCode.contains(opr.defInst())) {
                liveCode.add(opr.defInst());
                tryAdd(opr.defInst());
            }
        });
        inst.block().precursors().forEach(pre -> {
            if (!liveCode.contains(pre.terminator())) {
                liveCode.add(pre.terminator());
                tryAdd(pre.terminator());
            }
        });
    }

    private void collect() {
        irRoot.functions().forEach((name, fn) -> {
            MoreLive = true;
            fn.blocks().forEach(block -> {
                block.instructions().forEach(inst -> {
                    if (liveCode.contains(inst)) tryAdd(inst);
                });
                block.phiInst().forEach(((reg, inst) -> {
                    if (liveCode.contains(inst)) tryAdd(inst);
                }));
            });
        });
    }

    public void clean() {
        irRoot.functions().forEach((name, fn) -> fn.blocks().forEach(block -> {
            for (Iterator<Inst> iter = block.instructions().iterator();iter.hasNext();) {
                Inst inst = iter.next();
                if (!liveCode.contains(inst)) {
                    change = true;
                    if (inst.isTerminal()) {
                        //this block is not effectively reachable
                        block.removeTerminator();   //to make CFG simplification faster
                        block.addTerminator(new Jump(block, block));    //formal terminator
                        //but maybe I can assert that there is no such block
                        break;
                    } else {
                        iter.remove();
                        inst.removeSelf(false);
                    }
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
