package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.LOperand;
import Assemb.LOperand.Reg;
import Assemb.LOperand.VirtualReg;

import java.util.HashSet;

public class RType extends RISCInst {

    private LOperand src1, src2;
    private CalCategory opCode;

    public RType(LOperand src1, LOperand src2, CalCategory opCode, Reg dest, LIRBlock block) {
        super(dest, block);
        this.src1 = src1;
        this.src2 = src2;
        this.opCode = opCode;
    }

    public CalCategory opCode() {
        return opCode;
    }

    @Override
    public HashSet<LOperand> uses() {
        HashSet<LOperand> ret = new HashSet<>();
        if (src1 instanceof VirtualReg) ret.add(src1);
        if (src2 instanceof VirtualReg) ret.add(src2);
        return ret;
    }
}
