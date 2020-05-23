package MIR.IRoperand;

import MIR.IRinst.Inst;
import MIR.IRtype.Pointer;
import MIR.IRtype.VoidType;

import java.util.HashSet;

public class Null extends Operand {

    public Null() {
        super(new Pointer(new VoidType(), false));
    }

    @Override
    public HashSet<Inst> uses() {
        return new HashSet<>();
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

    @Override
    public boolean equals(Object o) {
        return o instanceof Null;
    }
}
