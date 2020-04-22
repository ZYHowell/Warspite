package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.*;

import java.util.HashSet;

public class IType extends RISCInst {

    private Reg src;
    private Imm imm;
    private CalCategory opCode;

    public IType(Reg src, Imm imm, CalCategory opCode, Reg dest, LIRBlock block) {
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
    public HashSet<Reg> uses() {
        HashSet<Reg> ret = new HashSet<>();
        if (src instanceof VirtualReg) ret.add(src);
        return ret;
    }

    @Override
    public void replaceUse(Reg origin, Reg replaced) {
        if (src == origin) src = replaced;
    }
}
