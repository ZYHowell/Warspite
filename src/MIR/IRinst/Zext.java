package MIR.IRinst;

import MIR.IRoperand.Operand;
import MIR.IRtype.IRBaseType;

public class Zext extends Inst{

    private Operand origin, dest;

    public Zext(Operand origin, Operand dest) {
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
        return dest.toString() + originType().toString() + origin.toString() +
                "= zext " +  "to " + destType().toString();
    }
}
