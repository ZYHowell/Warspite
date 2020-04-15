package Assemb.LOperand;

public class PhyReg extends Reg{
    private String name;

    public PhyReg(String name) {
        super();
        this.name = name;
    }

    public String name() {
        return name;
    }
}
