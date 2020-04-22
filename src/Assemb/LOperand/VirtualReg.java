package Assemb.LOperand;

public class VirtualReg extends Reg {

    private int size;
    public VirtualReg(int size) {
        super();
        this.size = size;
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        if (color == null) return this.hashCode() + "";
        return color.toString();
    }
}
