package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.LOperand;
import Assemb.LOperand.Reg;
import Assemb.LOperand.VirtualReg;

import java.util.HashSet;

public class Sz extends RISCInst{

    private Reg src;
    private EzCategory opCode;

    public Sz(Reg src, EzCategory opCode, Reg dest, LIRBlock block) {
        super(dest, block);
        this.src = src;
        this.opCode = opCode;
    }

    @Override
    public HashSet<Reg> uses() {
        HashSet<Reg> ret = new HashSet<>();
        ret.add(src);
        return ret;
    }
}
