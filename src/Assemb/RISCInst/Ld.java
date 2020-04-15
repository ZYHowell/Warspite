package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.Imm;
import Assemb.LOperand.LOperand;
import Assemb.LOperand.Reg;
import Assemb.LOperand.VirtualReg;

import java.util.HashSet;

public class Ld extends RISCInst {

    private Reg address;
    private int size;
    private Imm offset;
    public Ld(Reg address, Reg dest, Imm offset, int size, LIRBlock block) {
        super(dest, block);
        this.address = address;
        this.size = size;
        this.offset = offset;
    }
    public int size() {
        return size;
    }
    public Imm offset() {
        return offset;
    }

    @Override
    public HashSet<Reg> uses() {
        HashSet<Reg> ret = new HashSet<>();
        if (address instanceof VirtualReg) ret.add(address);
        return ret;
    }
}
