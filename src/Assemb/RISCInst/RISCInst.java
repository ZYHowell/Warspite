package Assemb.RISCInst;

import Assemb.LIRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;

import java.util.HashSet;

public abstract class RISCInst {

    public enum CalCategory {
        add, sub, slt, sltU, xor, or, and , sll, srl, sra, mul, mulH, div, rem
    }
    private Register dest;
    private LIRBlock block;
    public HashSet<Operand> liveIn = new HashSet<>(), liveOut = new HashSet<>();

    public RISCInst(Register dest, LIRBlock block) {
        this.dest = dest;
        this.block = block;
    }

    public Register dest() {
        return dest;
    }
    public LIRBlock block() {
        return block;
    }

    public abstract HashSet<Operand> uses();
}
