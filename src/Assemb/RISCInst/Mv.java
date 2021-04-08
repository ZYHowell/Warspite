package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.Reg;

import java.util.Collections;
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
    public HashSet<Reg> defs() {
        return new HashSet<>(Collections.singletonList(dest()));
    }

    @Override
    public void replaceUse(Reg origin, Reg replaced) {
        if (this.origin == origin) this.origin = replaced;
    }

    @Override
    public void stackLengthAdd(int stackLength) {}

    @Override
    public String toString() {
        return "mv " + dest() + ", " + origin;
    }
}
