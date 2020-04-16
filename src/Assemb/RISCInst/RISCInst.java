package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.LOperand;
import Assemb.LOperand.Reg;
import Assemb.LOperand.VirtualReg;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;

import java.util.HashSet;

public abstract class RISCInst {

    public enum CalCategory {
        add, sub, slt, sltU, xor, or, and , sll, srl, sra, mul, mulH, div, rem
    }
    public enum EzCategory {
        eq, ne, le, ge, lt, gt
    }
    private Reg dest;
    private LIRBlock block;

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

    public abstract HashSet<Reg> uses();
}
