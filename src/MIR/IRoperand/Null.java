package MIR.IRoperand;

import MIR.IRtype.*;

public class Null extends Operand {

    public Null() {
        super(new Pointer(new VoidType(), false));
    }

    @Override
    public Operand copy() {
        return new Null();
    }

    @Override
    public String toString() {
        return "null";
    }
}
