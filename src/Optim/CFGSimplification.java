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
    }

    private boolean isConst(Operand src) {
        return src instanceof ConstInt || src instanceof ConstBool || src instanceof ConstString;
    }
    private boolean canRemove(IRBlock block) {
        ArrayList<IRBlock> precursors = block.precursors();
        return precursors.isEmpty() || (precursors.size() == 1 && precursors.get(0) == block);
    }
    private void removeBB(Function fn) {
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
    }
    private void mergeBB(Function fn) {
        //for those contains only a jump, very rare.
    }
    private void InstModify(Function fn) {
        //modify instructions. hard to judge
    }

    private void simplify(Function fn) {
        removeBB(fn);
        mergeBB(fn);
    }

    @Override
    public boolean run() {
        change = false;
        irRoot.functions().forEach((name, fn) -> simplify(fn));
        return change;
    }
}
