package Assemb.RISCInst;

import Assemb.LIRBlock;
import MIR.IRoperand.ConstInt;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;

import java.util.HashSet;

public class IType extends RISCInst {

    private Operand src;
    private ConstInt imm;
    private CalCategory opCode;

    public IType(Operand src, ConstInt imm, CalCategory opCode, Register dest, LIRBlock block) {
        super(dest, block);
        this.src = src;
        this.imm = imm;
        this.opCode = opCode;
    }
    public ConstInt imm() {
        return imm;
    }
    public CalCategory opCode() {
        return opCode;
    }

    @Override
    public HashSet<Operand> uses() {
        HashSet<Operand> ret = new HashSet<>();
        ret.add(src);
        return ret;
    }
}
