package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.LOperand;
import Assemb.LOperand.Reg;

import java.util.HashSet;

public class Bz extends RISCInst {
    private Reg judged;
    private LIRBlock jumpTo;
    private EzCategory opCode;
    public Bz(Reg judged, EzCategory opCode, LIRBlock jumpTo, LIRBlock block) {
        super(null, block);
        this.judged = judged;
        this.jumpTo = jumpTo;
        this.opCode = opCode;
    }

    @Override
    public HashSet<Reg> uses() {
        HashSet<Reg> ret = new HashSet<>();
        ret.add(judged);
        return ret;
    }

    @Override
    public void replaceUse(Reg origin, Reg replaced) {
        if (judged == origin) judged = replaced;
    }

    @Override
    public void stackLengthAdd(int stackLength) {}

    @Override
    public String toString() {
        return "b" + opCode + "z " + judged + ", " + jumpTo;
    }
}
