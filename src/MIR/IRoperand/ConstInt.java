package MIR.IRoperand;

import MIR.IRtype.IntType;

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
    public String toString() {
        return "" + value;
    }
}
