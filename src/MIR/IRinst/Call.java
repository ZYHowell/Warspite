package MIR.IRinst;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;

import java.util.ArrayList;

public class Call extends Inst{

    private Function callee;
    private ArrayList<Operand> params;

    public Call(Function callee, ArrayList<Operand> params, Register dest, IRBlock block) {
        super(dest, block);
        this.callee = callee;
        this.params = params;
        params.forEach(param -> param.addUse(this));
        if (dest != null) dest.setDef(this);
    }

    public Function callee() {
        return callee;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder(dest().toString());
        ret.append(" = ");
        ret.append(dest().type().toString());
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

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        for (int i = 0;i < params.size();++i)
            if (params.get(i) == replaced)
                params.set(i, replaceTo);
    }
    @Override
    public void removeSelf() {
        block().remove(this);
    }
}
