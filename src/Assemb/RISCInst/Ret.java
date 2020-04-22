package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.Reg;

import java.util.HashSet;

public class Ret extends RISCInst{
    public Ret(LIRBlock block) {
        super(null, block);
    }

    @Override
    public HashSet<Reg> uses() {
        return new HashSet<>();
    }

    @Override
    public void replaceUse(Reg origin, Reg replaced) {}
}
