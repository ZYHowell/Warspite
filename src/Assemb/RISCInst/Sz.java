package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.Reg;

import java.util.Collections;
import java.util.HashSet;

public class Sz extends RISCInst{

    private Reg src;
    private EzCategory opCode;

    public Sz(Reg src, EzCategory opCode, Reg dest, LIRBlock block) {
        super(dest, block);
        this.src = src;
        this.opCode = opCode;
    }

    @Override
    public HashSet<Reg> uses() {
        HashSet<Reg> ret = new HashSet<>();
        ret.add(src);
        return ret;
    }

    @Override
    public void replaceUse(Reg origin, Reg replaced) {
        if (src == origin) src = replaced;
    }

    @Override
    public void stackLengthAdd(int stackLength) {}

    @Override
    public HashSet<Reg> defs() {
        return new HashSet<>(Collections.singletonList(dest()));
    }

    @Override
    public String toString() {
        return "s" + opCode + "z " + dest() + ", " + src;
    }
}
