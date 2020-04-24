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
    public CalCategory opCode() {
        return opCode;
    }

    @Override
    public HashSet<Reg> uses() {
        HashSet<Reg> ret = new HashSet<>();
        ret.add(src);
        return ret;
    }

    @Override
    public void replaceUse(Reg origin, Reg replaced) {
        if (src == origin) src = replaced;
    }

    @Override
    public void stackLengthAdd(int stackLength) {
        if (imm instanceof SLImm) imm = new Imm(stackLength * (((SLImm)imm).reverse ? -1 : 1) + imm.value);
    }

    @Override
    public String toString() {
        return opCode + "i " + dest() + ", " + src + ", " + imm.value;
    }
}
