package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Null;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.IRtype.IRBaseType;

public class Cmp extends Inst{
    public enum CmpOpCategory {
        slt, sgt, sle, sge, eq, ne
    }
    private CmpOpCategory opCode;
    private Operand src1, src2;

    public Cmp(Operand src1, Operand src2, Register dest, CmpOpCategory opCode, IRBlock block) {
        super(dest, block);
        this.src1 = src1;
        this.src2 = src2;
        this.opCode = opCode;
        src1.addUse(this);
        src2.addUse(this);
        dest.setDef(this);
    }

    @Override
    public String toString() {
        String typeString;
        if (src1 instanceof Null) {
            if (src2 instanceof Null) typeString = "int*";
            else typeString = src2.type().toString();
        } else typeString = src1.type().toString();
        return dest().toString() + " = " + "icmp " + opCode.toString() + " " + typeString +
                src1.toString()  + ", " + src2.toString();
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        if (src1 == replaced) src1 = replaceTo;
        if (src2 == replaced) src2 = replaceTo;
    }
    @Override
    public void removeSelf() {
        block().remove(this);
    }
}
