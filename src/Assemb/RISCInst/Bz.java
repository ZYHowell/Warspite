package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.Reg;

import java.util.HashSet;

public class Bz extends RISCInst {
    private Reg judged;
    private LIRBlock destBlock;
    private EzCategory opCode;
    public Bz(Reg judged, EzCategory opCode, LIRBlock destBlock, LIRBlock block) {
        super(null, block);
        this.judged = judged;
        this.destBlock = destBlock;
        this.opCode = opCode;
    }

    public void replaceDest(LIRBlock origin, LIRBlock dest) {
        if (destBlock == origin) destBlock = dest;
    }

    @Override
    public HashSet<Reg> uses() {
        HashSet<Reg> ret = new HashSet<>();
        ret.add(judged);
        return ret;
    }
    @Override
    public HashSet<Reg> defs() {
        return new HashSet<>();
    }

    @Override
    public void replaceUse(Reg origin, Reg replaced) {
        if (judged == origin) judged = replaced;
    }

    @Override
    public void stackLengthAdd(int stackLength) {}

    @Override
    public String toString() {
        return "b" + opCode + "z " + judged + ", " + destBlock;
    }
}
