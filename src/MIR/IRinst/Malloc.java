package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;

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
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        if (length == replaced) length = replaceTo;
    }
    @Override
    public void removeSelf() {
        block().remove(this);
    }
}
