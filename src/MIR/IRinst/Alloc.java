package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.IRtype.Pointer;
import Util.MIRMirror;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Alloc extends Inst {

    public Alloc(Register dest, IRBlock block) {
        super(dest, block);
        assert dest.type() instanceof Pointer;
        dest.setDef(this);
    }

    @Override
    public String toString() {
        return dest().toString() + " = " + "alloca " +
                ((Pointer)dest().type()).pointTo().toString() + ", align " +
                ((Pointer)dest().type()).pointTo().size() / 8;
    }

    @Override
    public void addMirror(IRBlock destBlock, MIRMirror mirror) {
        destBlock.addInst(new Alloc((Register)mirror.opMir(dest()), destBlock));
    }

    @Override
    public HashSet<Operand> uses() {
        return new HashSet<>();
    }

    @Override
    public boolean sameMeaning(Inst inst) {
        return false;
    }

    @Override
    public boolean canHoist() {
        return false;
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {}
    @Override
    public void removeSelf(boolean removeFromBlock) {
        if (removeFromBlock) block().remove(this);
    }
    @Override
    public boolean isTerminal() {
        return false;
    }
}
