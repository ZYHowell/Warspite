package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import Util.MIRMirror;
import Util.error.internalError;
import Util.position;

import java.util.HashSet;

public class Move extends Inst{

    private Operand origin;
    public Move(Operand origin, Register dest, IRBlock block, boolean addUse) { //addUse is false for para copy
        super(dest, block);
        this.origin = origin;
        if (addUse) origin.addUse(this);
    }

    public Operand origin() {
        return origin;
    }
    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public void removeSelf(boolean removeFromBlock) {
        if (removeFromBlock) block().remove(this);
        origin.removeUse(this);
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        if (origin == replaced) origin = replaceTo;
    }

    @Override
    public String toString() {
        return "mv " + origin.type().toString() + " " + dest().toString() + " " + origin.toString();
    }

    @Override
    public void addMirror(IRBlock destBlock, MIRMirror mirror) {
        throw new internalError("call addMirror of Move inst", new position(0,0));
    }

    @Override
    public HashSet<Operand> uses() {
        HashSet<Operand> ret = new HashSet<>();
        ret.add(origin);
        return ret;
    }

    @Override
    public boolean sameMeaning(Inst inst) {
        return inst instanceof Move && ((Move) inst).origin.equals(origin);
    }

    @Override
    public boolean noSideEffect() {
        return true;
    }
}
