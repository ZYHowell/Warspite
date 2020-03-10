package MIR.IRoperand;

import MIR.IRinst.Inst;
import MIR.IRtype.IRBaseType;

import java.util.HashSet;

public class GlobalReg extends Operand {

    private String name;
    private HashSet<Inst> uses = new HashSet<>();

    public GlobalReg(IRBaseType type, String name) {
        super(type);
        this.name = name;
    }


    @Override
    public void addUse(Inst inst) {
        uses.add(inst);
    }

    @Override
    public void removeUse(Inst inst) {
        uses.remove(inst);
    }
    @Override
    public HashSet<Inst> uses() {
        return uses;
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
