package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.Imm;
import Assemb.LOperand.LOperand;
import Assemb.LOperand.Reg;
import Assemb.LOperand.VirtualReg;

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
    public HashSet<LOperand> uses() {
        HashSet<LOperand> ret = new HashSet<>();
        if (address instanceof VirtualReg) ret.add(address);
        if (value instanceof VirtualReg) ret.add(value);
        return ret;
    }
}
