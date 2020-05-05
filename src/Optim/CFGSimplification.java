package Optim;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.Branch;
import MIR.IRinst.Inst;
import MIR.IRinst.Jump;
import MIR.IRoperand.ConstBool;
import MIR.IRoperand.ConstInt;
import MIR.IRoperand.ConstString;
import MIR.IRoperand.Operand;
import MIR.Root;
import Util.DomGen;

import java.util.ArrayList;
import java.util.HashSet;

/*
 * Patterns:
 * 1. removeBB: remove unreachable blocks.
 *      Check if const folding makes some blocks unreachable also;
 * 2. mergeBB: 1)merge "easy" blocks.
 *              BUT NOT IN THIS ONE, THOUGH IT IS EASY. (since simulator has no stages)
 *             2)merge of unnecessary jump(always followed by remove unreachable if)
 * 3. jump threading: too difficult, need to judge condition relations
 * 4. instruction replacement or modification: yes, it is interesting, but NOT THAT EASY.
 */
public class CFGSimplification extends Pass {

    private Root irRoot;
    private boolean change;
    private boolean doStraightening;

    public CFGSimplification(Root irRoot, boolean doStraightening) {
        super();
        this.irRoot = irRoot;
        this.doStraightening = doStraightening;
    }

    private boolean isConst(Operand src) {
        return src instanceof ConstInt || src instanceof ConstBool || src instanceof ConstString;
    }
    private boolean canRemove(IRBlock block) {
        ArrayList<IRBlock> precursors = block.precursors();
        return precursors.isEmpty() || (precursors.size() == 1 && precursors.get(0) == block);
    }
    private boolean removeBB(Function fn) {
        HashSet<IRBlock> removedCollection = new HashSet<>();
        boolean newChange, changed = false;
        do {
            newChange = false;
            removedCollection.clear();
            for (IRBlock block : fn.blocks()) {
                if (!block.terminated()) {
                    newChange = true;
                    removedCollection.add(block);
                    block.removeTerminator();
                    continue;
                }
                Inst terminator = block.terminator();
                if (terminator instanceof Branch) {
                    if (isConst(((Branch) terminator).condition())) {
                        Operand src = ((Branch) terminator).condition();
                        assert src instanceof ConstBool;
                        block.removeTerminator();
                        if (((ConstBool) src).value())
                            block.addTerminator(new Jump(((Branch) terminator).trueDest(), block));
                        else
                            block.addTerminator(new Jump(((Branch) terminator).falseDest(), block));
                    } else if (((Branch) terminator).trueDest() == ((Branch) terminator).falseDest()) {
                        block.removeTerminator();
                        block.addTerminator(new Jump(((Branch) terminator).trueDest(), block));
                    }
                }
                if (canRemove(block) && block != fn.entryBlock()) {
                    newChange = true;
                    removedCollection.add(block);
                    block.removeTerminator();
                }
            }
            fn.blocks().removeAll(removedCollection);
            changed = changed || newChange;
        } while(newChange);
        return changed;
    }
//    private boolean mergeBB_1(Function fn) {
//        HashSet<IRBlock> mergeSet = new HashSet<>();
//        fn.blocks().forEach(block -> {
//            if (block.instructions().size() == 1 && block.terminator() instanceof Jump)
//                mergeSet.add(block);
//        });
//        mergeSet.forEach(merged -> {
//            IRBlock jumpDest = ((Jump) merged.terminator()).destBlock();
//            jumpDest.mergeEmptyBlock(merged);
//        });
//        fn.blocks().removeAll(mergeSet);
//        return !mergeSet.isEmpty();
//    }
    private boolean mergeBB_2(Function fn) {    //T1 trans, or called straightening
        HashSet<IRBlock> mergeSet = new HashSet<>();
        fn.blocks().forEach(block -> {
            if (block.precursors().size() == 1 && block.precursors().get(0).successors().size() == 1)
                mergeSet.add(block);
        });
        mergeSet.forEach(block -> {
            IRBlock pre = block.precursors().get(0);
            pre.removeTerminator();
            pre.mergeBlock(block);
            if (fn.exitBlock() == block) fn.setExitBlock(pre);
        });
        fn.blocks().removeAll(mergeSet);
        return !mergeSet.isEmpty();
    }
//    private void InstModify(Function fn) {
//        //modify instructions. hard to judge
//        //to consider: add this?
//    }

    private void simplify(Function fn) {
        boolean newChange;
        boolean changed = false;
        do {
            newChange = removeBB(fn);
            //newChange = mergeBB_1(fn) || newChange;
            if (doStraightening) newChange = mergeBB_2(fn) || newChange;
            changed = changed || newChange;
        } while(newChange);
        if (changed) new DomGen(fn, true).runForFn();
        //re-calculate the dom relationship
        change = change || changed;
    }

    @Override
    public boolean run() {
        change = false;
        irRoot.functions().forEach((name, fn) -> simplify(fn));
        return change;
    }
}
