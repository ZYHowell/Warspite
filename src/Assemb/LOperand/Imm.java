package Assemb.LOperand;

public class Imm extends LOperand{

    public int value;

    public Imm(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
