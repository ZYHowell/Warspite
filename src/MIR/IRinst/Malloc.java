package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import Util.MIRMirror;

import java.util.HashSet;

public class Malloc extends Inst {

    private Operand length;

    public Malloc(Operand length, Register dest, IRBlock block) {
        super(dest, block);
        this.length = length;
        length.addUse(this);
        dest.setDef(this);
    }

    @Override
    public String toString() {
        return dest().toString() + " = call noalias i8* @malloc(" +
                length.type().toString() + " " + length.toString() + ")";
    }

    @Override
    public void addMirror(IRBlock destBlock, MIRMirror mirror) {
        destBlock.addInst(new Malloc(mirror.opMir(length), (Register)mirror.opMir(dest()), destBlock));
    }
    @Override
    public HashSet<Operand> uses() {
        HashSet<Operand> ret = new HashSet<>();
        ret.add(length);
        return ret;
    }

    @Override
    public boolean sameMeaning(Inst inst) {
        return false;
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        if (length == replaced) length = replaceTo;
    }
    @Override
    public void removeSelf(boolean removeFromBlock) {
        if (removeFromBlock) block().remove(this);
        length.removeUse(this);
    }
    @Override
    public boolean isTerminal() {
        return false;
    }
}
