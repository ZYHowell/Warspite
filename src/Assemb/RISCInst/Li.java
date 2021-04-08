package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.Imm;
import Assemb.LOperand.Reg;
import Assemb.LOperand.SLImm;

import java.util.Collections;
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
    @Override
    public HashSet<Reg> defs() {
        return new HashSet<>(Collections.singletonList(dest()));
    }
    @Override
    public void replaceUse(Reg origin, Reg replaced) {}

    @Override
    public void stackLengthAdd(int stackLength) {
        if (value instanceof SLImm) value = new Imm(value.value + stackLength);
    }

    @Override
    public String toString() {
        return "li " + dest() + ", " + value;
    }
}
