package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.LOperand;
import Assemb.LOperand.Reg;
import Assemb.LOperand.VirtualReg;

import java.util.HashSet;

public class Bz extends RISCInst {
    private Reg judged;
    private LIRBlock jumpTo;
    private EzCategory opCode;
    public Bz(Reg judged, EzCategory opCode, LIRBlock jumpTo, LIRBlock block) {
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
    public HashSet<Reg> uses() {
        HashSet<Reg> ret = new HashSet<>();
        ret.add(judged);
        return ret;
    }
}
