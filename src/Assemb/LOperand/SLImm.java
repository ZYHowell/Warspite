package Assemb.LOperand;

//this will be turned to Imm(value + stackLength)
public class SLImm extends Imm {
    public boolean reverse = false;
    public SLImm(int value) {
        super(value);
    }
}
