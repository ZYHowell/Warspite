package MIR.IRoperand;

import MIR.IRinst.Inst;
import MIR.IRtype.IRBaseType;

abstract public class Operand {

    private IRBaseType type;

    public Operand(IRBaseType type) {
        this.type = type;
    }


    public void addUse(Inst inst) {}
    public Inst defInst() {
        return null;
    }
    public IRBaseType type() {
        return type;
    }

    public abstract Operand copy();
    public abstract String toString();
}
