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
}
