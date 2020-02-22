package MIR.IRinst;

import MIR.IRoperand.Operand;

public class Move extends Inst {

    private Operand src, dest;

    public Move(Operand src, Operand dest) {
        super();
        this.src = src;
        this.dest = dest;
    }
}
