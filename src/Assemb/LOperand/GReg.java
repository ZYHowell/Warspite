package Assemb.LOperand;

//GReg is actually a pointer pointing to data whose size is "int size"
public class GReg extends Reg {

    private int size;
    public GReg(int size) {
        super();
        this.size = size;
    }

    public int size() {
        return size;
    }
}
