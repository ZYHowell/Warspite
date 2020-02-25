package MIR.IRinst;

import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;

public class Malloc extends Inst {

    private Operand length;
    private Register dest;

    public Malloc(Operand length, Register dest) {
        super();
        this.length = length;
        this.dest = dest;
    }

    @Override
    public String toString() {
        return dest.toString() + " = call noalias i8* @malloc(" +
                length.type().toString() + " " + length.toString() + ")";
    }
}
