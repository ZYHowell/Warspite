package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;

public class Jump extends Inst {

    private IRBlock destBlock;

    public Jump(IRBlock destBlock, IRBlock block) {
        super(null, block);
        this.destBlock = destBlock;
    }

    public IRBlock destBlock() {
        return destBlock;
    }

    @Override
    public String toString() {
        return "br label %" + destBlock.name();
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {}
    @Override
    public void removeSelf() {
        block().remove(this);
    }
}
