package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.IRtype.IRBaseType;
import Util.MIRMirror;

import java.util.HashSet;

public class Zext extends Inst{

    private Operand origin;

    public Zext(Operand origin, Register dest, IRBlock block) {
        super(dest, block);
        this.origin = origin;
        origin.addUse(this);
        dest.setDef(this);
    }

    public Operand origin() {
        return origin;
    }
    public IRBaseType originType() {
        return origin.type();
    }
    public IRBaseType destType() {
        return dest().type();
    }

    @Override
    public String toString() {
        return dest().toString()  + " = zext " + originType().toString() + origin.toString() +
                "to " + destType().toString();
    }

    @Override
    public void addMirror(IRBlock destBlock, MIRMirror mirror) {
        destBlock.addInst(new Zext(mirror.opMir(origin), (Register)mirror.opMir(dest()), destBlock));
    }
    @Override
    public HashSet<Operand> uses() {
        HashSet<Operand> ret = new HashSet<>();
        ret.add(origin);
        return ret;
    }

    @Override
    public boolean sameMeaning(Inst inst) {
        if (inst instanceof Zext) {
            Zext instr = (Zext) inst;
            return instr.origin().equals(origin) && instr.destType().sameType(destType());
        }
        else return false;
    }

    @Override
    public boolean canHoist() {
        return true;
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        if (origin == replaced) origin = replaceTo;
    }
    @Override
    public void removeSelf(boolean removeFromBlock) {
        if (removeFromBlock) block().remove(this);
        origin.removeUse(this);
    }
    @Override
    public boolean isTerminal() {
        return false;
    }
}
