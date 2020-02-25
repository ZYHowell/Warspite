package MIR.IRinst;

import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;

public class Load extends Inst{

    private Register dest;
    private Operand address;

    public Load(Register dest, Operand address) {
        super();
        this.dest = dest;
        this.address = address;
    }
    @Override
    public String toString() {
        return dest.name() + " = load " + dest.type().toString() + ", " +
                address.type().toString() + " " + address.toString() + ", align" +
                dest.type().size() / 8;
    }
}
