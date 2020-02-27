package MIR.IRinst;

import MIR.IRoperand.Register;
import MIR.IRtype.ClassType;
import MIR.IRtype.IRBaseType;
import MIR.IRtype.Pointer;

public class Alloc extends Inst {

    private Register dest;

    public Alloc(Register dest) {
        super();
        this.dest = dest;
        assert dest.type() instanceof Pointer;
    }

    @Override
    public String toString() {
        return dest.toString() + " = " + "alloca " +
                ((Pointer)dest.type()).pointTo().toString() + ", align " +
                ((Pointer)dest.type()).pointTo().size() / 8;
    }
}
