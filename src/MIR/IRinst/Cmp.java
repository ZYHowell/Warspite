package MIR.IRinst;

import MIR.IRoperand.Operand;

public class Cmp extends Inst{
    public enum CmpOpCategory {
        lessThan, greaterThan, lessEqual, greaterEqual, logicalAnd, logicalOr,
        equal, notEqual
    }
    private CmpOpCategory opCode;
    private Operand src1, src2, dest;

    public Cmp(Operand src1, Operand src2, Operand dest, CmpOpCategory opCode) {
        super();
        this.src1 = src1;
        this.src2 = src2;
        this.dest = dest;
        this.opCode = opCode;
    }
    //the type is always i1
}
