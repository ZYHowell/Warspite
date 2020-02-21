package MIR.IRinst;

import MIR.Function;
import MIR.IRoperand.Operand;

public class Call extends Inst{

    private Operand dest;
    private Function callee;

    public Call(Function callee, Operand dest) {
        super();
        this.callee = callee;
        this.dest = dest;
    }
}
