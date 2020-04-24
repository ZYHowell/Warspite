package Assemb.LOperand;

public class VirtualReg extends Reg {

    private int size;
    public String name;
    public VirtualReg(int size, int name) {
        super();
        this.size = size;
        this.name = name + "%";
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        if (color == null) return name + "(" + hashCode() + ")";
        return color.toString();
    }
}
