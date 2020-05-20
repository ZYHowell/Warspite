package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.*;

import java.util.HashSet;

public class St extends RISCInst{

    public Reg address, value;
    public Imm offset;
    private int size;

    public St(Reg address, Reg value, Imm offset, int size, LIRBlock block) {
        super(null, block);
        this.address = address;
        this.value = value;
        this.offset = offset;
        this.size = size;
    }

    public Reg value() {
        return value;
    }
    public int size() {
        return size;
    }

    @Override
    public HashSet<Reg> uses() {
        HashSet<Reg> ret = new HashSet<>();
        if (!(address instanceof GReg)) ret.add(address);
        ret.add(value);
        return ret;
    }
    @Override
    public HashSet<Reg> defs() {
        return new HashSet<>();
    }

    @Override
    public void replaceUse(Reg origin, Reg replaced) {
        if (address == origin) address = replaced;
        if (value == origin) value = replaced;
    }

    @Override
    public void stackLengthAdd(int stackLength) {
        if (offset instanceof SLImm) offset = new Imm(stackLength + offset.value);
    }

    @Override
    public String toString() {
        return "s" + ((size == 1) ? "b" : ((size == 4) ? "w" : "h")) + " " + value + ", "
                + offset + "(" + address + ")";
    }
}
