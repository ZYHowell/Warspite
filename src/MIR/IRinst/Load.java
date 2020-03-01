package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;

public class Load extends Inst{

    private Operand address;

    public Load(Register dest, Operand address, IRBlock block) {
        super(dest, block);
        this.address = address;
        address.addUse(this);
        dest.setDef(this);
    }

    public Operand address() {
        return address;
    }

    @Override
    public String toString() {
        return dest().name() + " = load " + dest().type().toString() + ", " +
                address.type().toString() + " " + address.toString() + ", align" +
                dest().type().size() / 8;
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        if (address == replaced) address = replaceTo;
    }
    @Override
    public void removeSelf() {
        block().remove(this);
    }
}
