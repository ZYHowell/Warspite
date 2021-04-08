package MIR.IRoperand;

import MIR.IRinst.Inst;
import MIR.IRtype.ArrayType;
import MIR.IRtype.IntType;
import MIR.IRtype.Pointer;

import java.util.HashSet;

public class ConstString extends Operand {

    public String name;
    private String value;

    public ConstString(String name, String value) {
        super(new Pointer(new ArrayType(value.length(), new IntType(8)), true));
        this.name = name;
        this.value = value;
    }

    public String value() {
        return value;
    }
    public String irValue() {
        String ret = value.replace("\\", "\\5C");
        ret = ret.replace("\n", "\\0A");
        ret = ret.replace("\0", "\\00");
        ret = ret.replace("\t", "\\09");
        ret = ret.replace("\"", "\\22");
        return ret;
    }

    @Override
    public HashSet<Inst> uses() {
        return new HashSet<>();
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
