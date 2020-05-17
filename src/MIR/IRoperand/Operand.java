package MIR.IRoperand;

import MIR.IRinst.Inst;
import MIR.IRtype.ArrayType;
import MIR.IRtype.IRBaseType;
import MIR.IRtype.Pointer;

import java.util.HashSet;

abstract public class Operand {

    private IRBaseType type;

    public Operand(IRBaseType type) {
        this.type = type;
    }


    public Inst defInst() {
        return null;
    }
    public IRBaseType type() {
        return type;
    }
    public boolean isPointer() {
        return type instanceof Pointer || type instanceof ArrayType;
    }

    public abstract HashSet<Inst> uses();
    public abstract void addUse(Inst inst);
    public abstract void removeUse(Inst inst);
    public abstract Operand copy();
    public abstract String toString();
}
