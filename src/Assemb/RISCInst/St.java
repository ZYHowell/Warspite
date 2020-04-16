package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.*;

import java.util.HashSet;

public class St extends RISCInst{

    private Reg address, value;
    private Imm offset;
    private int size;

    public St(Reg address, Reg value, Imm offset, int size, LIRBlock block) {
        super(null, block);
        this.address = address;
        this.value = value;
        this.offset = offset;
        this.size = size;
    }

    public Reg address() {
        return address;
    }
    public Reg value() {
        return value;
    }
    public Imm offset() {
        return offset;
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
}
