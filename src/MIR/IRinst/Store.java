package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;

public class Store extends Inst{

    private Operand value;
    private Operand address;

    public Store(Operand address, Operand value, IRBlock block) {
        super(null, block);
        this.value = value;
        this.address = address;
        value.addUse(this);
        address.addUse(this);
        //notice that the address is a "def" in mem2reg if it is an allocated reg
    }

    public Operand address() {
        return address;
    }

    public Operand value() {
        return value;
    }

    @Override
    public String toString() {
        return "store " + value.type().toString() + ", " + value.toString() +
                address.type().toString() + " " + address.toString() +
                ", align " + value.type().size() / 8;
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        if (value == replaced) value = replaceTo;
        if (address == replaced) address = replaceTo;
    }
    @Override
    public void removeSelf() {
        block().remove(this);
    }
}
