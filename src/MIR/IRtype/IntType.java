package MIR.IRtype;

public class IntType extends IRBaseType {

    private int size;

    public IntType(int size) {
        super();
        this.size = size;
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return "i" + size;
    }
}
