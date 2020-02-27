package MIR.IRinst;

import MIR.IRoperand.Operand;

public class Store extends Inst{

    private Operand value;
    private Operand address;

    public Store(Operand address, Operand value) {
        super();
        this.value = value;
        this.address = address;
    }

    @Override
    public String toString() {
        return "store " + value.type().toString() + ", " + value.toString() +
                address.type().toString() + " " + address.toString() +
                ", align " + value.type().size() / 8;
    }
}
