package MIR.IRoperand;

import MIR.IRtype.*;

public class Null extends Operand {

    public Null() {
        super(new Pointer(new VoidType()));
    }
}
