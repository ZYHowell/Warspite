package Assemb.RISCInst;

import Assemb.LFn;
import Assemb.LIRBlock;
import Assemb.LOperand.PhyReg;
import Assemb.LOperand.Reg;

import java.util.HashSet;

public class Cal extends RISCInst{
    private LFn callee;
    private PhyReg x6;

    public Cal(PhyReg x6, LFn callee, LIRBlock block) {
        super(x6, block);
        this.callee = callee;
        this.x6 = x6;
    }

    public LFn callee() {
        return callee;
    }

    @Override
    public HashSet<Reg> uses() {
        HashSet<Reg> ret = new HashSet<>();
        ret.add(x6);
        return ret;
    }

    @Override
    public void replaceUse(Reg origin, Reg replaced) {}

    @Override
    public void stackLengthAdd(int stackLength) {}

    @Override
    public String toString() {
        return "call " + callee.name();
    }
}
