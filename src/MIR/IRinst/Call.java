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
        StringBuilder ret = new StringBuilder(dest.toString());
        ret.append(" = ");
        ret.append(dest.type().toString());
        ret.append(" @");
        ret.append(callee.name());
        for (int i = 0;i < params.size();++i){
            ret.append((i == 0 ? "(" : ", "));
            ret.append(params.get(i).type().toString());
            ret.append(" ");
            ret.append(params.get(i).toString());
        }
        ret.append(")");
        return ret.toString();
    }
}
