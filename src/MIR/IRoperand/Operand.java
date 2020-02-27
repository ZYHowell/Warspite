package MIR.IRoperand;

import MIR.IRtype.IRBaseType;

abstract public class Operand {

    private IRBaseType type;

    public Operand(IRBaseType type) {
        this.type = type;
    }

    public IRBaseType type() {
        return type;
    }

    public abstract String toString();
}
