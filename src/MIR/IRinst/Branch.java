package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import Util.MIRMirror;

import java.util.HashSet;

public class Branch extends Inst {

    private Operand condition;
    private IRBlock trueDest, falseDest;

    public Branch(Operand condition, IRBlock trueDest, IRBlock falseDest, IRBlock block) {
        super(null, block);
        this.condition = condition;
        this.trueDest = trueDest;
        this.falseDest = falseDest;
        condition.addUse(this);
    }

    public Operand condition() {
        return condition;
    }
    public IRBlock trueDest() {
        return trueDest;
    }
    public IRBlock falseDest() {
        return falseDest;
    }

    @Override
    public String toString() {
        return "br " + condition.type().toString() + " " + condition.toString() + ", %" +
                trueDest.name() + ", %" + falseDest.name();
    }

    @Override
    public void addMirror(IRBlock destBlock, MIRMirror mirror) {
        destBlock.addTerminator(new Branch(mirror.opMir(condition),
                mirror.blockMir(trueDest), mirror.blockMir(falseDest), destBlock));
    }

    @Override
    public HashSet<Operand> uses() {
        HashSet<Operand> ret = new HashSet<>();
        ret.add(condition);
        return ret;
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        if (condition == replaced) condition = replaceTo;
    }
    @Override
    public void removeSelf() {
        block().remove(this);
    }
}
