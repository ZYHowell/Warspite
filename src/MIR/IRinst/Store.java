package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import Util.MIRMirror;

import java.util.HashSet;

public class Store extends Inst{

    private Operand value;
    private Operand address;

    public Store(Operand address, Operand value, IRBlock block) {
        super(null, block);
        this.value = value;
        this.address = address;
        value.addUse(this);
        address.addUse(this);
        //notice that the address is a "def" in mem2reg if it is an allocated reg
    }

    public Operand address() {
        return address;
    }

    public Operand value() {
        return value;
    }

    @Override
    public String toString() {
        return "store " + value.type().toString() + " " + value.toString() + ", " +
                address.type().toString() + " " + address.toString() +
                ", align " + value.type().size() / 8;
    }

    @Override
    public void addMirror(IRBlock destBlock, MIRMirror mirror) {
        destBlock.addInst(new Store(mirror.opMir(address), mirror.opMir(value), destBlock));
    }

    @Override
    public HashSet<Operand> uses() {
        HashSet<Operand> ret = new HashSet<>();
        ret.add(value);ret.add(address);
        return ret;
    }

    @Override
    public boolean sameMeaning(Inst inst) {
        return false;
    }

    @Override
    public boolean canHoist() {
        return false;
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        if (value == replaced) value = replaceTo;
        if (address == replaced) address = replaceTo;
    }
    @Override
    public void removeSelf(boolean removeFromBlock) {
        if (removeFromBlock) block().remove(this);
        address.removeUse(this);
        value.removeUse(this);
    }
    @Override
    public boolean isTerminal() {
        return false;
    }
}
