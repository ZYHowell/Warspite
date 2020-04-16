package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.LOperand;
import Assemb.LOperand.Reg;
import Assemb.LOperand.VirtualReg;

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
}
