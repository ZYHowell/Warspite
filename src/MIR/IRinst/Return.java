package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;

public class Return extends Inst {

    private Operand value;
    private IRBlock currentBlock;

    public Return(IRBlock currentBlock, Operand value) {
        super();
        this.currentBlock = currentBlock;
        this.value = value;
    }

    public IRBlock currentBlock() {
        return currentBlock;
    }
    public Operand value() {
        return value;
    }

    @Override
    public String toString() {
        return "ret " + value.type().toString() + " " + value.toString();
    }
}
