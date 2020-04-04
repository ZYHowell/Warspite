package MIR.IRoperand;

import MIR.IRinst.Inst;
import MIR.IRtype.IntType;
import MIR.IRtype.ArrayType;
import MIR.IRtype.Pointer;

import java.util.HashSet;

public class ConstString extends Operand {

    private String name;
    private String value;

    public ConstString(String name, String value) {
        super(new Pointer(new ArrayType(value.length() + 1, new IntType(8)), true));
        this.name = name;
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public HashSet<Inst> uses() {
        return null;
    }

    @Override public void addUse(Inst inst) {}
    @Override public void removeUse(Inst inst) {}

    @Override
    public Operand copy() {
        return this;
    }

    @Override
    public String toString() {
        return "@" + name;
    }
}
