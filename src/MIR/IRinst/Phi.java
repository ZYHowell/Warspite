package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;

import java.util.ArrayList;

public class Phi extends Inst {

    private Register dest;
    private ArrayList<IRBlock> blocks = new ArrayList<>();
    private ArrayList<Operand> values = new ArrayList<>();

    public Phi(Register dest, ArrayList<IRBlock> blocks, ArrayList<Operand> values) {
        super();
        this.dest = dest;
        this.blocks = blocks;
        this.values = values;
    }

    @Override
    public String toString() {
        String ret = dest.toString() + " = phi " + dest.type().toString();
        for (int i = 0;i < values.size();i++) {
            if (i > 0) ret = ret + ", ";
            ret = ret + "[ " + values.get(i).toString() + ", " + blocks.get(i).name() + " ]";
        }
        return ret;
    }
}
