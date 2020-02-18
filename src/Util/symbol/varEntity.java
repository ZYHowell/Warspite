package Util.symbol;

import MIR.IRoperand.*;

public class varEntity extends Entity {

    private Type type;
    private boolean isOuter;    //indicate if the variable is {member of a class, global variable}
    private boolean isGlobal;   //in some way similar with the one above, but used for IR
    private Operand asOperand;  //this can be a globalReg/reg/heap mem.

    public varEntity(String name, Type type, boolean isOuter, boolean isGlobal) {
        super(name);
        this.type = type;
        this.isOuter = isOuter;
        this.isGlobal = isGlobal;
    }

    public Type type() { return type; }
    public boolean isOuter() {
        return isOuter;
    }
    public boolean isGlobal() { return isGlobal; }

    public void setOperand(Operand ope) {
        asOperand = ope;
    }
    public Operand asOperand() {
        return asOperand;
    }
}
