package MIR.IRoperand;

import MIR.IRinst.Inst;
import MIR.IRtype.BoolType;

public class ConstBool extends Operand{

    boolean value;

    public ConstBool(boolean value) {
        super(new BoolType());
        this.value = value;
    }

    public boolean value() {
        return value;
    }

    @Override
    public void addUse(Inst inst) {}

    @Override
    public void removeUse(Inst inst) {}

    @Override
    public Operand copy() {
        return new ConstBool(value);
    }

    @Override
    public String toString() {
        return value ? "1" : "0";
    }
}
