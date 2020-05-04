package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.Imm;
import Assemb.LOperand.Reg;

import java.util.Collections;
import java.util.HashSet;

public class lui extends RISCInst{

    private Imm address;    //only used in store global->lui
    public lui(Imm address, Reg dest, LIRBlock block) {
        super(dest, block);
        this.address = address;
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
    public void stackLengthAdd(int stackLength) {}

    @Override
    public String toString() {
        return "lui " + dest() + ", " + address;
    }
}
