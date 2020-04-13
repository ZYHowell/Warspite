package Assemb.RISCInst;

import Assemb.LIRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;

import java.util.HashSet;

public class RType extends RISCInst {

    private Operand src1, src2;
    private CalCategory opCode;

    public RType(Operand src1, Operand src2, CalCategory opCode, Register dest, LIRBlock block) {
        super(dest, block);
        this.src1 = src1;
        this.src2 = src2;
    }
    public CalCategory opCode() {
        return opCode;
    }

    @Override
    public HashSet<Operand> uses() {
        HashSet<Operand> ret = new HashSet<>();
        ret.add(src1);
        ret.add(src2);
        return ret;
    }
}
