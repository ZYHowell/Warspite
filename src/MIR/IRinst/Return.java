package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import Util.MIRMirror;

import java.util.HashSet;

public class Return extends Inst {

    private Operand value;

    public Return(IRBlock currentBlock, Operand value) {
        super(null, currentBlock);
        this.value = value;
        if (value != null) value.addUse(this);
    }

    public IRBlock currentBlock() {
        return block();
    }
    public Operand value() {
        return value;
    }

    @Override
    public String toString() {
        return "ret" + (value == null ? " void" :
                        (" " + value.type().toString() + " " + value.toString()));
    }

    @Override
    public void addMirror(IRBlock destBlock, MIRMirror mirror) {
        destBlock.addInst(new Return(destBlock, mirror.opMir(value)));
    }
    @Override
    public HashSet<Operand> uses() {
        HashSet<Operand> ret = new HashSet<>();
        ret.add(value);
        return ret;
    }

    @Override
    public boolean sameMeaning(Inst inst) {
        return false;
    }

    @Override
    public boolean canHoist() {
        return false;
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        if (value == replaced) value = replaceTo;
    }
    @Override
    public void removeSelf(boolean removeFromBlock) {
        if (removeFromBlock) block().removeTerminator();
        if (value != null) value.removeUse(this);
    }
    @Override
    public boolean isTerminal() {
        return true;
    }
}
