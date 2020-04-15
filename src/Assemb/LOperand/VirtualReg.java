package Assemb.LOperand;

public class VirtualReg extends Reg {

    private int stackOffset;
    private int size;
    public VirtualReg(int size) {
        this.size = size;
    }

    public int size() {
        return size;
    }
    public void setStackOffset(int offset){
        stackOffset = offset;
    }
    public int stackOffset() {
        return stackOffset;
    }
}
