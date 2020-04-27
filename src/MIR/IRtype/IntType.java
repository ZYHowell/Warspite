package MIR.IRtype;

public class IntType extends IRBaseType {

    private int size;

    public IntType(int size) {
        super();
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return "i" + size;
    }

    @Override
    public boolean sameType(IRBaseType o) {
        return o instanceof IntType && o.size() == size;
    }
}
