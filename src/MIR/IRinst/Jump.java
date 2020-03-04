package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import Util.MIRMirror;

import java.util.HashSet;

public class Jump extends Inst {

    private IRBlock jumpDest;

    public Jump(IRBlock jumpDest, IRBlock block) {
        super(null, block);
        this.jumpDest = jumpDest;
    }

    public IRBlock destBlock() {
        return jumpDest;
    }

    @Override
    public String toString() {
        return "br label %" + jumpDest.name();
    }

    @Override
    public void addMirror(IRBlock destBlock, MIRMirror mirror) {
        destBlock.addTerminator(new Jump(mirror.blockMir(jumpDest), destBlock));
    }

    @Override
    public HashSet<Operand> uses() {
        return new HashSet<>();
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {}
    @Override
    public void removeSelf() {
        block().remove(this);
    }
}
