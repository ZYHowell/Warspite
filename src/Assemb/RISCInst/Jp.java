package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.LOperand;

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

    @Override
    public HashSet<LOperand> uses() {
        return new HashSet<>();
    }
}