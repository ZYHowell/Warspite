package MIR.IRoperand;

import MIR.IRtype.IRBaseType;
import MIR.IRtype.IntType;
import MIR.IRtype.Pointer;

public class ConstString extends Operand {

    String name;

    public ConstString(String name) {
        super(new Pointer(new IntType(8), false));
        this.name = name;
    }

    @Override
    public String toString() {
        return "@" + name;
    }
}
