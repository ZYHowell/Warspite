package MIR.IRoperand;


import MIR.IRtype.IRBaseType;

public class Param extends Operand {

    private String name;

    public Param(IRBaseType type, String name) {
        super(type);
        this.name = name;
    }

    @Override
    public String toString() {
        return "%" + name;
    }
}
