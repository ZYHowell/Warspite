package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import Util.MIRMirror;

import java.util.HashMap;

abstract public class Inst {

    private Register dest;
    private IRBlock block;

    public Inst(Register dest, IRBlock block) {
        this.dest = dest;
        this.block = block;
    }

    public IRBlock block() {
        return block;
    }
    public void setCurrentBlock(IRBlock block) {
        this.block = block;
    }
    public Register dest() {
        return dest;
    }

    public abstract void removeSelf();
    public abstract void ReplaceUseWith(Register replaced, Operand replaceTo);
    public abstract String toString();
    public abstract void addMirror(IRBlock destBlock, MIRMirror mirror);

}
