package MIR.IRoperand;

import MIR.IRtype.IntType;

public class ConstInt extends Operand {

    private int value;

    public ConstInt(int value) {
        super(new IntType());
        this.value = value;
    }

    public int value() {
        return value;
    }
}
