package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.LOperand;
import Assemb.LOperand.VirtualReg;

import java.util.HashSet;

public class Bz extends RISCInst {
    public enum BzCategory {
        eq, ne, le, ge, lt, gt
    }
    private LOperand judged;
    private LIRBlock jumpTo;
    private BzCategory opCode;
    public Bz(LOperand judged, BzCategory opCode, LIRBlock jumpTo, LIRBlock block) {
        super(null, block);
        this.judged = judged;
        this.jumpTo = jumpTo;
        this.opCode = opCode;
    }

    public LOperand judged() {
        return judged;
    }
    public LIRBlock jumpTo() {
        return jumpTo;
    }
    @Override
    public HashSet<LOperand> uses() {
        HashSet<LOperand> ret = new HashSet<>();
        if (judged instanceof VirtualReg) ret.add(judged);
        return ret;
    }
}
