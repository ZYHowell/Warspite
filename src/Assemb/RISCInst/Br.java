package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.Reg;

import java.util.HashSet;

public class Br extends RISCInst{

    public enum BrCategory {
        eq, ne, lt, ge
    }
    private Reg src1, src2;
    private BrCategory opCode;
    LIRBlock destBlock;
    public Br(Reg src1, Reg src2, BrCategory opCode, LIRBlock destBlock, LIRBlock block) {
        super(null, block);
        this.src1 = src1;
        this.src2 = src2;
        this.opCode = opCode;
        this.destBlock = destBlock;
    }

    public LIRBlock destBlock() {
        return destBlock;
    }
    public BrCategory opCode() {
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
        return "b" + opCode + " " + src1 + ", " + src2 + ", " + destBlock;
    }
}
