package MIR.IRinst;

import MIR.IRoperand.Register;
import MIR.IRtype.ClassType;
import MIR.IRtype.IRBaseType;
import MIR.IRtype.Pointer;

public class Alloc extends Inst {

    private Register dest;

    public Alloc() {
        super();
    }

    @Override
    public String toString() {
        int allocSize;
        String typeName;
        if (dest.type() instanceof Pointer){

            typeName = ((Pointer)dest.type()).pointTo().toString();
        }
        else {
            assert dest.type() instanceof ClassType;
            typeName = dest.type().name();
        }
        return dest.toString() + " = " + "alloca " +
                typeName + ", align " +
                ((Pointer)dest.type()).pointTo().size() / 8;
    }
}
