package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.IRtype.IRBaseType;
import Util.MIRMirror;

public class Zext extends Inst{

    private Operand origin;

    public Zext(Operand origin, Register dest, IRBlock block) {
        super(dest, block);
        this.origin = origin;
        origin.addUse(this);
        dest.setDef(this);
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
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        if (origin == replaced) origin = replaceTo;
    }
    @Override
    public void removeSelf() {
        block().remove(this);
    }
}
