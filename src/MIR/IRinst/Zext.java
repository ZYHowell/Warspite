package MIR.IRinst;

import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.IRtype.IRBaseType;

public class Zext extends Inst{

    private Operand origin;
    private Register dest;

    public Zext(Operand origin, Register dest) {
        super();
        this.origin = origin;
        this.dest = dest;
    }

    public IRBaseType originType() {
        return origin.type();
    }
    public IRBaseType destType() {
        return dest.type();
    }

    @Override
    public String toString() {
        return dest.toString()  + " = zext " + originType().toString() + origin.toString() +
                "to " + destType().toString();
    }
}
