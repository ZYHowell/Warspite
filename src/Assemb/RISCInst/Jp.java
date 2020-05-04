package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.LOperand;
import Assemb.LOperand.Reg;

import java.util.HashSet;

public class Jp extends RISCInst{
    private LIRBlock destBlock;

    public Jp(LIRBlock destBlock, LIRBlock block) {
        super(null, block);
        this.destBlock = destBlock;
    }

    public LIRBlock destBlock() {
        return destBlock;
    }
    public void replaceDest(LIRBlock origin, LIRBlock dest) {
        if (destBlock == origin) destBlock = dest;
    }

    @Override
    public HashSet<Reg> uses() {
        return new HashSet<>();
    }
    @Override
    public HashSet<Reg> defs() {
        return new HashSet<>();
    }

    @Override
    public void replaceUse(Reg origin, Reg replaced) {}

    @Override
    public void stackLengthAdd(int stackLength) {}

    @Override
    public String toString() {
        return "j " + destBlock;
    }
}
