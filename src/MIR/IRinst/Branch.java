package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;

public class Branch extends Inst {

    private Operand condition;
    private IRBlock trueDest, falseDest;

    public Branch(Operand condition, IRBlock trueDest, IRBlock falseDest) {
        super();
        this.condition = condition;
        this.trueDest = trueDest;
        this.falseDest = falseDest;
    }

    public Operand condition() {
        return condition;
    }
    public IRBlock trueDest() {
        return trueDest;
    }
    public IRBlock falseDest() {
        return falseDest;
    }
}
