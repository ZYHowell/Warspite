package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.LOperand;

import java.util.HashSet;

public class Ret extends RISCInst{
    public Ret(LIRBlock block) {
        super(null, block);
    }

    @Override
    public HashSet<LOperand> uses() {
        return new HashSet<>();
    }
}
