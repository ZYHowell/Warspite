package MIR.IRoperand;

import MIR.IRinst.Inst;
import MIR.IRtype.BoolType;

import java.util.HashSet;

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
    public HashSet<Inst> uses() {
        return new HashSet<>();
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

    @Override
    public boolean equals(Object o) {
        return o instanceof ConstBool && ((ConstBool) o).value() == value;
    }
}
