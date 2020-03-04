package MIR.IRoperand;

import MIR.IRtype.IRBaseType;
import MIR.IRinst.Inst;

import java.util.HashSet;

//all virtual register
public class Register extends Operand {

    private String name;
    private HashSet<Inst> uses = new HashSet<>();
    private Inst def;

    public Register(IRBaseType type, String name) {
        super(type);
        this.name = name;
    }

    @Override
    public void addUse(Inst inst) {
        uses.add(inst);
    }
    public void setDef(Inst inst) {
        def = inst;
    }
    @Override
    public Inst defInst() {
        return def;
    }

    @Override
    public Operand copy() {
        return new Register(type(), name);
    }

    public void replaceAllUseWith(Operand opr) {
        uses.forEach(inst -> {
            inst.ReplaceUseWith(this, opr);
            opr.addUse(inst);
        });
    }

    public String name() {
        return name;
    }
    public String toString() {
        return "%" + name;
    }
}
