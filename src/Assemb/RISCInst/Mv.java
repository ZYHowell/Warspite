package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.LOperand;
import Assemb.LOperand.Reg;
import Assemb.LOperand.VirtualReg;

import java.util.HashSet;

public class Mv extends RISCInst{

    private LOperand origin;
    public Mv(LOperand origin, Reg dest, LIRBlock block) {
        super(dest, block);
        this.origin = origin;
    }

    @Override
    public HashSet<LOperand> uses() {
        HashSet<LOperand> ret = new HashSet<>();
        if (origin instanceof VirtualReg) ret.add(origin);
        return ret;
    }
}
