package MIR.IRinst;

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
    private Register dest;

    public Cmp(Operand src1, Operand src2, Register dest, CmpOpCategory opCode) {
        super();
        this.src1 = src1;
        this.src2 = src2;
        this.dest = dest;
        this.opCode = opCode;
    }

    @Override
    public String toString() {
        String typeString;
        if (src1 instanceof Null) {
            if (src2 instanceof Null) typeString = "int*";
            else typeString = src2.type().toString();
        } else typeString = src1.type().toString();
        return dest.toString() + " = " + "icmp " + opCode.toString() + " " + typeString +
                src1.toString()  + ", " + src2.toString();
    }
}
