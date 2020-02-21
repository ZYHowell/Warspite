package MIR.IRtype;

public class Pointer extends IRBaseType {

    IRBaseType pointTo;

    public Pointer(IRBaseType pointTo) {
        super();
        this.pointTo = pointTo;
    }

    public IRBaseType pointTo() {
        return pointTo;
    }

    @Override
    public String toString() {
        return pointTo.toString() + "*";
    }
}
