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
}
