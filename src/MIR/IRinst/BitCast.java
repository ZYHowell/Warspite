package MIR.IRinst;

import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.IRtype.IRBaseType;

public class BitCast extends Inst {

    private Operand it;
    private Register dest;

    public BitCast(Operand it, Register dest) {
        super();
        this.it = it;
        this.dest = dest;
    }

    public IRBaseType originType() {
        return it.type();
    }
    public IRBaseType terminalType() {
        return dest.type();
    }

    @Override
    public String toString() {

    }
}
