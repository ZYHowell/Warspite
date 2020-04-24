package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.GReg;
import Assemb.LOperand.Reg;

import java.util.HashSet;

public class Mv extends RISCInst{

    private Reg origin;
    public Mv(Reg origin, Reg dest, LIRBlock block) {
        super(dest, block);
        this.origin = origin;
    }

    public Reg origin() {
        return origin;
    }

    @Override
    public HashSet<Reg> uses() {
        HashSet<Reg> ret = new HashSet<>();
        ret.add(origin);
        return ret;
    }

    @Override
    public void replaceUse(Reg origin, Reg replaced) {
        if (this.origin == origin) this.origin = replaced;
    }

    @Override
    public void stackLengthAdd(int stackLength) {}

    @Override
    public String toString() {
        return "addi " + dest() + ", " + origin + ", 0";
    }
}
