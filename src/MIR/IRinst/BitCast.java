package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.IRtype.IRBaseType;
import Util.MIRMirror;

import java.util.HashSet;

public class BitCast extends Inst {

    private Operand it;

    public BitCast(Operand it, Register dest, IRBlock block) {
        super(dest, block);
        this.it = it;
        it.addUse(this);
        dest.setDef(this);
    }

    public Operand origin() {
        return it;
    }

    public IRBaseType originType() {
        return it.type();
    }
    public IRBaseType terminalType() {
        return dest().type();
    }

    @Override
    public String toString() {
        return dest().toString() + " = " + it.type().toString() + " " + it.toString() +
                " to " + dest().type().toString();
    }

    @Override
    public void addMirror(IRBlock destBlock, MIRMirror mirror) {
        destBlock.addInst(new BitCast(mirror.opMir(it),
                (Register)mirror.opMir(dest()), destBlock));
    }

    @Override
    public HashSet<Operand> uses() {
        HashSet<Operand> ret = new HashSet<>();
        ret.add(it);
        return ret;
    }

    @Override
    public boolean sameMeaning(Inst inst) {
        if (inst instanceof BitCast) {
            BitCast cast = (BitCast) inst;
            return cast.origin().equals(it) && cast.terminalType().sameType(dest().type());
        } else return false;
    }

    @Override
    public boolean canHoist() {
        return true;
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        if (it == replaced) it = replaceTo;
    }
    @Override
    public void removeSelf(boolean removeFromBlock) {
        if (removeFromBlock) block().remove(this);
        it.removeUse(this);
    }
    @Override
    public boolean isTerminal() {
        return false;
    }
}
