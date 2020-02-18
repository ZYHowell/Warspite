package MIR.IRoperand;

import MIR.IRtype.IRBaseType;

public class ConstString extends Operand {

    String value;

    public ConstString(IRBaseType stringClassType, String value) {
        super(stringClassType);
        this.value = value;
    }
}
