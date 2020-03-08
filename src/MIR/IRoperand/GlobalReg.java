package MIR.IRoperand;

import MIR.IRinst.Inst;
import MIR.IRtype.IRBaseType;

public class GlobalReg extends Operand {

    private String name;

    public GlobalReg(IRBaseType type, String name) {
        super(type);
        this.name = name;
    }


    @Override
    public void addUse(Inst inst) {
        //to consider: should we add use of a global reg?
    }

    @Override
    public void removeUse(Inst inst) {
        //to consider just like the one above
    }

    @Override
    public Operand copy() {
        assert false;
        return null;
    }

    @Override
    public String toString() {
        return "%" + name;
    }
}
