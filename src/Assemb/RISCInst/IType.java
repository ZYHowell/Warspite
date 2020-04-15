package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.*;

import java.util.HashSet;

public class IType extends RISCInst {

    private LOperand src;
    private Imm imm;
    private CalCategory opCode;

    public IType(LOperand src, Imm imm, CalCategory opCode, Reg dest, LIRBlock block) {
        super(dest, block);
        this.src = src;
        this.imm = imm;
        this.opCode = opCode;
    }
    public Imm imm() {
        return imm;
    }
    public CalCategory opCode() {
        return opCode;
    }

    @Override
    public HashSet<LOperand> uses() {
        HashSet<LOperand> ret = new HashSet<>();
        if (src instanceof VirtualReg) ret.add(src);
        return ret;
    }
}
