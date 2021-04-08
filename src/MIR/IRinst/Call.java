package MIR.IRinst;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.IRtype.BoolType;
import Util.MIRMirror;

import java.util.ArrayList;
import java.util.HashSet;

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
    public ArrayList<Operand> params() {
        return params;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        if (dest() != null) {
            ret.append(dest().toString());
            ret.append(" = ");
            ret.append("call ");
            ret.append(dest().type().toString());
            ret.append(" ");
        } else ret.append("call void ");
        ret.append("@");
        ret.append(callee.name);
        if (params.size() == 0) ret.append("(");
        for (int i = 0;i < params.size();++i){
            ret.append((i == 0 ? "(" : ", "));
            String paramT = params.get(i).type().toString();
            if (params.get(i).type() instanceof BoolType) paramT = "i8";
            ret.append(paramT);
            ret.append(" ");
            ret.append(params.get(i).toString());
        }
        ret.append(")");
        return ret.toString();
    }

    @Override
    public HashSet<Operand> uses() {
        return new HashSet<>(params);
    }

    @Override
    public boolean sameMeaning(Inst inst) {
        return false;
    }

    @Override
    public boolean noSideEffect() {
        return false;
    }

    @Override
    public void addMirror(IRBlock destBlock, MIRMirror mirror) {
        ArrayList<Operand> mirrorParams = new ArrayList<>();
        params.forEach(param -> mirrorParams.add(mirror.opMir(param)));
        destBlock.addInst(new Call(callee, mirrorParams, dest() == null ? null : (Register)mirror.opMir(dest()), destBlock));
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        for (int i = 0;i < params.size();++i)
            if (params.get(i) == replaced)
                params.set(i, replaceTo);
    }
    @Override
    public void removeSelf(boolean removeFromBlock) {
        if (removeFromBlock) block().remove(this);
        params.forEach(param -> param.removeUse(this));
    }
    @Override
    public boolean isTerminal() {
        return false;
    }
}
