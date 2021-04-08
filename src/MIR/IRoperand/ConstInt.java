package MIR.IRoperand;

import MIR.IRinst.Inst;
import MIR.IRtype.IntType;

import java.util.HashSet;

public class ConstInt extends Operand {

    private int value;

    public ConstInt(int value, int size) {
        super(new IntType(size));
        this.value = value;
    }

    public int value() {
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
        return new ConstInt(value, type().size());
    }

    @Override
    public String toString() {
        return "" + value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ConstInt &&
                ((ConstInt) o).value == value &&
                ((ConstInt) o).type().sameType(type());
    }
}
