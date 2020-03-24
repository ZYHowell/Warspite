package MIR.IRtype;

public class BoolType extends IRBaseType{

    public BoolType() {
        super();
    }

    @Override
    public int size() {
        return 8;
    }
    @Override
    public String toString() {
        return "i1";
    }
    @Override
    public boolean sameType(IRBaseType o) {
        return o instanceof BoolType;
    }
}
