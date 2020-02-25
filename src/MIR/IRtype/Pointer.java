package MIR.IRtype;

public class Pointer extends IRBaseType {

    private IRBaseType pointTo;
    private int dim;
    private boolean isReference;

    public Pointer(IRBaseType pointTo, boolean isReference) {
        super();
        this.pointTo = pointTo;
        this.dim = pointTo.dim() + 1;
        this.isReference = isReference;
    }

    public IRBaseType pointTo() {
        return pointTo;
    }

    public boolean isReference() {
        return isReference;
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
        return pointTo.toString() + "*";
    }
}
