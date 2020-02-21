package MIR.IRoperand;

import MIR.IRtype.IRBaseType;
import MIR.IRinst.Inst;

//all virtual register
public class Register extends Operand {

    private String name;

    public Register(IRBaseType type, String name) {
        super(type);
        this.name = name;
    }
}
