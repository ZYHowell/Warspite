package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.Reg;

import java.util.HashSet;

public class RType extends RISCInst {

    private Reg src1, src2;
    private CalCategory opCode;

    public RType(Reg src1, Reg src2, CalCategory opCode, Reg dest, LIRBlock block) {
        super(dest, block);
        this.src1 = src1;
        this.src2 = src2;
        this.opCode = opCode;
    }

    public CalCategory opCode() {
        return opCode;
    }

    @Override
    public HashSet<Reg> uses() {
        HashSet<Reg> ret = new HashSet<>();
        ret.add(src1);
        ret.add(src2);
        return ret;
    }

    @Override
    public void replaceUse(Reg origin, Reg replaced) {
        if (src1 == origin) src1 = replaced;
        if (src2 == origin) src2 = replaced;
    }

    @Override
    public void stackLengthAdd(int stackLength) {}

    @Override
    public String toString() {
        return opCode + " " + dest() + ", " + src1 + ", " + src2;
    }
}
