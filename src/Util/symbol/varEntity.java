package Util.symbol;

import MIR.IRoperand.ConstInt;
import MIR.IRoperand.Operand;

public class varEntity extends Entity {

    private Type type;
    private boolean isGlobal, isMember;   //in some way similar with the one above, but used for IR
    private Operand asOperand;  //if it is a member, is this able to be used as the offset?
    private ConstInt index;

    public varEntity(String name, Type type, boolean isGlobal) {
        super(name);
        this.type = type;
        this.isGlobal = isGlobal;
        this.isMember = false;
    }

    public ConstInt elementIndex() {
        return index;
    }
    public void setElementIndex(int index) {
        this.index = new ConstInt(index, 32);
    }
    public Type type() { return type; }
    public boolean isGlobal() { return isGlobal; }
    public void setIsMember() {
        isMember = true;
    }
    public boolean isMember() {
        return isMember;
    }
    public void setOperand(Operand ope) {
        asOperand = ope;
    }
    public Operand asOperand() {
        return asOperand;
    }
}
