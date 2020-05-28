package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import Util.MIRMirror;

import java.util.HashSet;

public class Load extends Inst{

    private Operand address;

    public Load(Register dest, Operand address, IRBlock block) {
        super(dest, block);
        this.address = address;
        address.addUse(this);
        dest.setDef(this);
    }

    public Operand address() {
        return address;
    }
    public void setAddress(Operand address) {
        this.address.removeUse(this);
        this.address = address;
        address.addUse(this);
    }

    @Override
    public String toString() {
        return "%" + dest().name() + " = load " + dest().type().toString() + ", " +
                address.type().toString() + " " + address.toString() + ", align " +
                dest().type().size() / 8;
    }

    @Override
    public void addMirror(IRBlock destBlock, MIRMirror mirror) {
        destBlock.addInst(new Load((Register)mirror.opMir(dest()), mirror.opMir(address), destBlock));
    }

    @Override
    public HashSet<Operand> uses() {
        HashSet<Operand> ret = new HashSet<>();
        ret.add(address);
        return ret;
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        if (address == replaced) address = replaceTo;
    }
    @Override
    public void removeSelf(boolean removeFromBlock) {
        if (removeFromBlock) block().remove(this);
        address.removeUse(this);
    }
    @Override
    public boolean isTerminal() {
        return false;
    }
    @Override
    public boolean sameMeaning(Inst inst) {
        return false;
    }

    @Override
    public boolean noSideEffect() {
        return false;
    }
}
