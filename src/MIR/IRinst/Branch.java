package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;

public class Branch extends Inst {

    private Operand condition;
    private IRBlock trueDest, falseDest;

    public Branch(Operand condition, IRBlock trueDest, IRBlock falseDest, IRBlock block) {
        super(null, block);
        this.condition = condition;
        this.trueDest = trueDest;
        this.falseDest = falseDest;
        condition.addUse(this);
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

    @Override
    public String toString() {
        return "br " + condition.type().toString() + " " + condition.toString() + ", %" +
                trueDest.name() + ", %" + falseDest.name();
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        if (condition == replaced) condition = replaceTo;
    }
    @Override
    public void removeSelf() {
        block().remove(this);
    }
}
