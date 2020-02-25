package MIR.IRinst;

import MIR.Function;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;

import java.util.ArrayList;

public class Call extends Inst{

    private Register dest;
    private Function callee;
    private ArrayList<Operand> params;

    public Call(Function callee, ArrayList<Operand> params, Register dest) {
        super();
        this.callee = callee;
        this.dest = dest;
        this.params = params;
    }

    @Override
    public String toString() {

    }
}
