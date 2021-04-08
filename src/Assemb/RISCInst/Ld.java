package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.GReg;
import Assemb.LOperand.Imm;
import Assemb.LOperand.Reg;
import Assemb.LOperand.SLImm;

import java.util.Collections;
import java.util.HashSet;

public class Ld extends RISCInst {

    public Reg address;
    private int size;
    public Imm offset;
    public Ld(Reg address, Reg dest, Imm offset, int size, LIRBlock block) {
        super(dest, block);
        this.address = address;
        this.size = size;
        this.offset = offset;
    }
    public int size() {
        return size;
    }

    @Override
    public HashSet<Reg> uses() {
        HashSet<Reg> ret = new HashSet<>();
        if (!(address instanceof GReg)) ret.add(address);
        return ret;
    }
    @Override
    public HashSet<Reg> defs() {
        return new HashSet<>(Collections.singletonList(dest()));
    }
    @Override
    public void replaceUse(Reg origin, Reg replaced) {
        if (address == origin) address = replaced;
    }

    @Override
    public void stackLengthAdd(int stackLength) {
        if (offset instanceof SLImm) offset = new Imm(stackLength + offset.value);
    }

    @Override
    public String toString() {
        return "l" + ((size == 1) ? "b" : ((size == 4) ? "w" : "h")) + " " + dest() + ", "
                + ((address instanceof GReg) ? address : (offset + "(" + address + ")"));
    }
}
