package MIR.IRoperand;

import MIR.IRinst.Inst;
import MIR.IRtype.*;

public class Null extends Operand {

    public Null() {
        super(new Pointer(new VoidType(), false));
    }

    @Override
    public void addUse(Inst inst) {}

    @Override
    public void removeUse(Inst inst) {}

    @Override
    public Operand copy() {
        return new Null();
    }

    @Override
    public String toString() {
        return "null";
    }
}
