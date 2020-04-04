package MIR.IRtype;

public class ArrayType extends IRBaseType {

    private int num;
    private IRBaseType type;

    public ArrayType(int num, IRBaseType type) {
        super();
        this.num = num;
        this.type = type;
    }
    @Override
    public int size() {
        return 8;
    }

    @Override
    public String toString() {
        return "[ " + num + " * " + type.toString() + " ]";
    }

    @Override
    public boolean sameType(IRBaseType o) {
        return o instanceof Pointer && (((Pointer)o).pointTo().sameType(type) ||
                    ((Pointer) o).pointTo() instanceof VoidType) ||
                o instanceof ArrayType && ((ArrayType)o).type.sameType(type);
    }
}
