package MIR.IRoperand;


import MIR.IRinst.Inst;
import MIR.IRtype.IRBaseType;

import java.util.HashSet;

public class Param extends Operand {

    private String name;
    private HashSet<Inst> uses = new HashSet<>();

    public Param(IRBaseType type, String name) {
        super(type);
        this.name = name;
    }

    @Override
    public void addUse(Inst inst) {
        uses.add(inst);
    }

    @Override
    public Operand copy() {
        return new Param(type(), name);
    }

    @Override
    public String toString() {
        return "%" + name;
    }
}
