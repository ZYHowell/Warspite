package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;

import java.util.ArrayList;

public class Phi extends Inst {

    private ArrayList<IRBlock> blocks;
    private ArrayList<Operand> values;

    public Phi(Register dest, ArrayList<IRBlock> blocks, ArrayList<Operand> values, IRBlock block) {
        super(dest, block);
        this.blocks = blocks;
        this.values = values;
        values.forEach(value -> value.addUse(this));
        dest.setDef(this);
    }

    public void addOrigin(Operand value, IRBlock origin) {
        blocks.add(origin);
        values.add(value);
        value.addUse(this);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder(dest().toString() + " = phi " + dest().type().toString());
        for (int i = 0;i < values.size();i++) {
            if (i > 0) ret.append(", ");
            ret.append("[ ");
            ret.append(values.get(i).toString());
            ret.append(", ");
            ret.append(blocks.get(i).name());
            ret.append(" ]");
        }
        return ret.toString();
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        for (int i = 0;i < values.size();++i)
            if (values.get(i) == replaced)
                values.set(i, replaceTo);
    }
    @Override
    public void removeSelf() {
        block().remove(this);
    }
}
