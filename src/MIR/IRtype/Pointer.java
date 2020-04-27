package MIR.IRtype;

public class Pointer extends IRBaseType {

    private IRBaseType pointTo;
    private int dim;
    private boolean isResolvable;

    public Pointer(IRBaseType pointTo, boolean isResolvable) {
        super();
        this.pointTo = pointTo;
        this.dim = pointTo.dim() + 1;
        this.isResolvable = isResolvable;
    }

    public IRBaseType pointTo() {
        return pointTo;
    }

    @Override
    public boolean isResolvable() {
        return isResolvable;
    }
    @Override
    public int dim() {
        return dim;
    }
    @Override
    public int size() {
        return 32;
    }
    @Override
    public String toString() {
        if (pointTo instanceof VoidType) return "";
        return pointTo.toString() + "*";
    }

    @Override
    public boolean sameType(IRBaseType o) {
        return (o instanceof Pointer &&
                (((Pointer) o).pointTo() instanceof VoidType || ((Pointer) o).pointTo().sameType(pointTo)))
                || (o instanceof ArrayType && o.sameType(this));
    }
}
