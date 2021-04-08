package Optim;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.*;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.IRtype.Pointer;
import MIR.Root;
import Util.DomGen;
import Util.MIRFnGraph;
import Util.MIRReachable;

import java.util.*;

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
    private boolean change;
    private HashSet<Inst> liveCode = new HashSet<>();
    private HashSet<Operand> outerOp = new HashSet<>();
    private MIRFnGraph CallGraph;
    private Queue<Inst> handleQueue = new LinkedList<>();

    public ADCE(Root irRoot) {
        super();
        this.irRoot = irRoot;
        CallGraph = new MIRFnGraph(irRoot);
    }

    private void testOp(Operand op, HashSet<Operand> addedOp) {
        HashSet<Inst> uses = op.uses();
        if (!op.isPointer()){
            if (op.defInst() != null) System.err.println(op.defInst().toString());
            else System.err.println(op.toString());
            throw new RuntimeException();
        }
        if (uses != null) {
            uses.forEach(inst -> {
                if (inst instanceof Store && ((Store) inst).value().type() instanceof Pointer) {
                    Store st = (Store) inst;
                    if (!addedOp.contains(st.address()) && !outerOp.contains(st.address())){
                        addedOp.add(st.address());
                        testOp(st.address(), addedOp);
                    }
                    if (!addedOp.contains(st.value()) && !outerOp.contains(st.value())){
                        addedOp.add(st.value());
                        testOp(st.value(), addedOp);
                    }
                } else if (inst instanceof BitCast || inst instanceof GetElementPtr ||
                    (inst instanceof Call && inst.dest() != null && inst.dest().isPointer()) ||
                    (inst instanceof Load && inst.dest() != null && inst.dest().isPointer()) ||
                    (inst instanceof Phi)) {
                    if (!addedOp.contains(inst.dest()) && !outerOp.contains(inst.dest())){
                        addedOp.add(inst.dest());
                        testOp(inst.dest(), addedOp);
                    }
                } else if (!(inst instanceof Return || inst instanceof Load || inst instanceof Call || inst instanceof Store || inst instanceof Cmp)) {
                    System.err.println(inst.toString());
                    throw new RuntimeException();
                }
            });
        }
    }
    private void setSideEffectTrue(Function fn) {
        if (!fn.hasSideEffect()){
            fn.setSideEffect(true);
            CallGraph.callerOf(fn).forEach(this::setSideEffectTrue);
        }
    }

    private void initCollect() {
        CallGraph.build();
        outerOp.clear();
        //collect "outer" address of each function
        outerOp.addAll(irRoot.globalVar());
        outerOp.addAll(irRoot.constStrings().values());
        irRoot.functions().forEach((name, fn) -> {
            fn.params().forEach(param -> {
                if (param.isPointer()) outerOp.add(param);
            });
            fn.setSideEffect(false);
        });

        HashSet<Operand> added = new HashSet<>();
        outerOp.forEach(op -> testOp(op, added));
        outerOp.addAll(added);
        //collect I/O and side effect functions and outer stores and returns
        irRoot.functions().forEach((name, fn) ->
            fn.blocks.forEach(block -> {
                for (Inst inst = block.headInst; inst != null; inst = inst.next)
                    if (inst instanceof Call && ((Call)inst).callee().hasSideEffect()){
                        //this includes I/O
                        liveCode.add(inst);
                        fn.setSideEffect(true);
                        CallGraph.callerOf(fn).forEach(this::setSideEffectTrue);
                    }
                    else if (inst instanceof Store && outerOp.contains(((Store)inst).address())){
                        liveCode.add(inst);
                        fn.setSideEffect(true);
                        CallGraph.callerOf(fn).forEach(this::setSideEffectTrue);
                    } else if (inst instanceof Return) liveCode.add(inst);
            }));
        irRoot.functions().forEach((name, fn) -> fn.blocks.forEach(block -> {
                for (Inst inst = block.headInst; inst != null; inst = inst.next)
                    if (inst instanceof Call && ((Call)inst).callee().hasSideEffect())
                        liveCode.add(inst);
        }));
    }

    private void tryAdd(Inst inst) {
        inst.uses().forEach(opr -> {
            if (opr.defInst() != null && !liveCode.contains(opr.defInst())) {
                liveCode.add(opr.defInst());
                handleQueue.offer(opr.defInst());
            }
            if (opr.isPointer())
                opr.uses().forEach(use -> {
                    if ((use instanceof Store || use instanceof BitCast || use instanceof GetElementPtr ||
                         use instanceof Phi || (use instanceof Load && use.dest().type() instanceof Pointer))
                            && !liveCode.contains(use)) {
                        liveCode.add(use);
                        handleQueue.offer(use);
                    }
                });
        });
        if (inst.dest() != null && inst.dest().isPointer()) {
            inst.dest().uses().forEach(use -> {
                if ((use instanceof Store || use instanceof BitCast || use instanceof GetElementPtr ||
                        use instanceof Phi || (use instanceof Load && use.dest().type() instanceof Pointer))
                        && !liveCode.contains(use)) {
                    liveCode.add(use);
                    handleQueue.offer(use);
                }
            });
        }
        inst.block().precursors.forEach(pre -> {
            if (!liveCode.contains(pre.terminator())) {
                liveCode.add(pre.terminator());
                handleQueue.offer(pre.terminator());
            }
        });
    }

    private void collect() {
        irRoot.functions().forEach((name, fn) -> fn.blocks.forEach(block -> {
            for (Inst inst = block.headInst; inst != null; inst = inst.next) {
                if (liveCode.contains(inst)) handleQueue.add(inst);
            }
            block.PhiInst.forEach(((reg, inst) -> {
                if (liveCode.contains(inst)) handleQueue.add(inst);
            }));
        }));
        while(!handleQueue.isEmpty()) {
            tryAdd(handleQueue.poll());
        }
    }

    public void clean() {
        irRoot.functions().forEach((name, fn) -> {
            for (IRBlock block : fn.blocks) {
                for (Inst inst = block.headInst; inst != null; inst = inst.next) {
                    if (!liveCode.contains(inst)) {
                        change = true;
                        if (inst.isTerminal()) {
                            //this block is not effectively reachable
                            block.removeTerminator();   //to make CFG simplification faster
                            block.addTerminator(new Jump(block, block));
                            break;
                        } else inst.removeSelf(true);
                    }
                }
                for (Iterator<Map.Entry<Register, Phi>> iter = block.PhiInst.entrySet().iterator(); iter.hasNext(); ) {
                    Phi inst = iter.next().getValue();
                    if (!liveCode.contains(inst)) {
                        iter.remove();
                        inst.removeSelf(false);
                        change = true;
                    }
                }
            }
            MIRReachable reachable = new MIRReachable(fn);
            fn.blocks.forEach(block -> {
                if (!reachable.reachable.contains(block)) {
                    block.removeTerminator();
                    block.addTerminator(new Jump(block, block));
                }
            });
            new DomGen(fn).runForFn();
        });
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
