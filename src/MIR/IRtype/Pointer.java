package MIR.IRtype;

public class Pointer extends IRBaseType {

    IRBaseType pointTo;

    public Pointer(IRBaseType pointTo) {
        super();
        this.pointTo = pointTo;
    }
}
