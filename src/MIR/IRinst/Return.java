package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import org.jetbrains.annotations.Nullable;

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
        return "ret " + value.type().toString() + " " + value.toString();
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        if (value == replaced) value = replaceTo;
    }
    @Override
    public void removeSelf() {
        block().remove(this);
    }
}
