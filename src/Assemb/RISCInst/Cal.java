package Assemb.RISCInst;

import Assemb.LFn;
import Assemb.LIRBlock;
import Assemb.LOperand.LOperand;
import Assemb.LOperand.Reg;

import java.util.HashSet;

public class Cal extends RISCInst{
    private LFn callee;

    public Cal(LFn callee, LIRBlock block) {
        super(null, block);
        this.callee = callee;
    }

    public LFn callee() {
        return callee;
    }

    @Override
    public HashSet<Reg> uses() {
        return new HashSet<>();
    }

    @Override
    public void replaceUse(Reg origin, Reg replaced) {}
}
