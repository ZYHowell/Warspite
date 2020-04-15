package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.LOperand;
import Assemb.LOperand.Reg;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;

import java.util.HashSet;

public abstract class RISCInst {

    public enum CalCategory {
        add, sub, slt, sltU, xor, or, and , sll, srl, sra, mul, mulH, div, rem
    }
    private Reg dest;
    private LIRBlock block;
    public HashSet<LOperand> liveIn = new HashSet<>(), liveOut = new HashSet<>();

    public RISCInst(Reg dest, LIRBlock block) {
        this.dest = dest;
        this.block = block;
    }

    public Reg dest() {
        return dest;
    }
    public LIRBlock block() {
        return block;
    }

    public abstract HashSet<LOperand> uses();
}
