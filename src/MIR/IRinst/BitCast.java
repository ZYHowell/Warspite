package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.IRtype.IRBaseType;
import Util.MIRMirror;

import java.util.HashSet;

public class BitCast extends Inst {

    private Operand it;

    public BitCast(Operand it, Register dest, IRBlock block) {
        super(dest, block);
        this.it = it;
        it.addUse(this);
        dest.setDef(this);
    }

    public IRBaseType originType() {
        return it.type();
    }
    public IRBaseType terminalType() {
        return dest().type();
    }

    @Override
    public String toString() {
        return dest().toString() + " = " + it.type().toString() + " " + it.toString() +
                " to " + dest().type().toString();
    }

    @Override
    public void addMirror(IRBlock destBlock, MIRMirror mirror) {
        destBlock.addInst(new BitCast(mirror.opMir(it),
                (Register)mirror.opMir(dest()), destBlock));
    }

    @Override
    public HashSet<Operand> uses() {
        HashSet<Operand> ret = new HashSet<>();
        ret.add(it);
        return ret;
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        if (it == replaced) it = replaceTo;
    }
    @Override
    public void removeSelf() {
        block().remove(this);
    }
}
