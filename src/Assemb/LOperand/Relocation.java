package Assemb.LOperand;

public class Relocation extends Imm {
    private GReg relocated;
    private boolean hi;
    public Relocation(GReg relocated, boolean hi) {
        super(0);
        this.relocated = relocated;
        this.hi = hi;
    }

    @Override
    public String toString() {
        return "%" + (hi ? "hi" : "lo") + "(" + relocated + ")";
    }
}
