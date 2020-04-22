package Assemb.LOperand;

public class PhyReg extends Reg{
    private String name;

    public PhyReg(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
