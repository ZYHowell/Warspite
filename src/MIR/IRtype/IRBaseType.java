package MIR.IRtype;

abstract public class IRBaseType {

    public IRBaseType(){}

    public int dim() {
        return 0;
    }

    public abstract int size();
    public abstract String toString();
}
