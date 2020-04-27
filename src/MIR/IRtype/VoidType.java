package MIR.IRtype;

import Util.error.internalError;
import Util.position;

public class VoidType extends IRBaseType{

    public VoidType() {
        super();
    }

    @Override
    public int size() {
        throw new internalError("call the size of void ir type", new position(0, 0));
    }
    @Override
    public String toString() {
        return "void";
    }

    @Override
    public boolean sameType(IRBaseType o) {
        return o instanceof VoidType;
    }
}
