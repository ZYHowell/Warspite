package MIR.IRoperand;

import MIR.IRtype.IRBaseType;

public class GlobalReg extends Operand {

    private String name;

    public GlobalReg(IRBaseType type, String name) {
        super(type);
        this.name = name;
    }

    @Override
    public String toString() {
        return "%" + name;
    }
}
