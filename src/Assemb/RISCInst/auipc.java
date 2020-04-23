package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.Imm;
import Assemb.LOperand.Reg;

import java.util.HashSet;

public class auipc extends RISCInst{

    private Imm address;    //only used in store global->auipc
    public auipc(Imm address, Reg dest, LIRBlock block) {
        super(dest, block);
        this.address = address;
    }

    @Override
    public HashSet<Reg> uses() {
        return null;
    }

    @Override
    public void replaceUse(Reg origin, Reg replaced) {}

    @Override
    public void stackLengthAdd(int stackLength) {}

    @Override
    public String toString() {
        return "auipc " + dest() + ", " + address;
    }
}
