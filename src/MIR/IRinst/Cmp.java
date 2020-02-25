package MIR.IRinst;

import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;

public class Cmp extends Inst{
    public enum CmpOpCategory {
        slt, sgt, sle, sge, logicalAnd, logicalOr,
        eq, ne
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
    //the type is always i1

    @Override
    public String toString() {

    }
}
