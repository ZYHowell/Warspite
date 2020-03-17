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

    public CFGSimplification(Root irRoot) {
        super();
        this.irRoot = irRoot;
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
        boolean newChange = true;
        while(newChange){
            newChange = false;
            for (IRBlock block : fn.blocks()) {
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
                    } else if (((Branch) terminator).trueDest() ==
                               ((Branch) terminator).falseDest()) {
                        block.removeTerminator();
                        block.addTerminator(new Jump(((Branch) terminator).trueDest(), block));
                    }
                }
                if (canRemove(block)) {
                    newChange = true;
                    removedCollection.add(block);
                    block.removeTerminator();
                }
            }
        }
        fn.blocks().removeAll(removedCollection);
        return !removedCollection.isEmpty();
    }
    private boolean mergeBB(Function fn) {
        HashSet<IRBlock> mergeSet = new HashSet<>();
        fn.blocks().forEach(block -> {
            if (block.phiInst().size() == 0 && block.instructions().size() == 1
                    && block.terminator() instanceof Jump)
                mergeSet.add(block);
        });
        mergeSet.forEach(merged -> {
            IRBlock jumpDest = ((Jump) merged.terminator()).destBlock();
            merged.precursors().forEach(pre -> {
                if (pre.terminator() instanceof Jump) {
                    pre.removeTerminator();
                    pre.addTerminator(new Jump(jumpDest, pre));
                } else {
                    assert pre.terminator() instanceof Branch;
                    Branch terminator = (Branch)pre.terminator(), newTerm;
                    if (terminator.trueDest() == merged)
                        newTerm = new Branch(terminator.condition(), jumpDest, terminator.falseDest(), pre);
                    else newTerm = new Branch(terminator.condition(), terminator.trueDest(), jumpDest, pre);
                    pre.removeTerminator();
                    pre.addTerminator(newTerm);
                }
            });
        });
        fn.blocks().removeAll(mergeSet);
        return !mergeSet.isEmpty();
    }
    private void InstModify(Function fn) {
        //modify instructions. hard to judge
    }

    private void simplify(Function fn) {
        boolean newChange = true;
        while(newChange){
            newChange = removeBB(fn);
            newChange = mergeBB(fn) || newChange;
            if (newChange) change = true;
        }
    }

    @Override
    public boolean run() {
        change = false;
        irRoot.functions().forEach((name, fn) -> simplify(fn));
        return change;
    }
}
