package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.Imm;
import Assemb.LOperand.Reg;

import java.util.HashSet;

public class Li extends RISCInst {

    private Imm value;
    public Li(Imm value, Reg dest, LIRBlock block) {
        super(dest, block);
        this.value = value;
    }

    public Imm value() {
        return value;
    }
    @Override
    public HashSet<Reg> uses() {
        return new HashSet<>();
    }
}
