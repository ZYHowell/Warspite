package MIR.IRtype;

abstract public class IRBaseType {

    public IRBaseType(){}

    public boolean isResolvable() {
        return false;
    }
    public int dim() {
        return 0;
    }

    public abstract int size();
    public abstract String toString();
}
