package MIR.IRoperand;

import MIR.IRtype.IRBaseType;
import MIR.IRtype.IntType;
import MIR.IRtype.Pointer;

public class ConstString extends Operand {

    String value;

    public ConstString(String value) {
        super(new Pointer(new IntType(8)));
        this.value = value;
    }
}
