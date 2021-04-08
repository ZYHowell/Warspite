package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.ConstInt;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.IRtype.IRBaseType;
import Util.MIRMirror;

import java.util.HashSet;

public class GetElementPtr extends Inst{

    private Operand arrayOffset;
    private ConstInt elementOffset;
    private IRBaseType type;
    private Operand ptr;

    public GetElementPtr(IRBaseType type, Operand ptr, Operand arrayOffset,
                         ConstInt elementOffset, Register dest, IRBlock block) {
        super(dest, block);
        this.type = type;
        this.ptr = ptr;
        this.arrayOffset = arrayOffset;
        this.elementOffset = elementOffset; //this can be null
        ptr.addUse(this);
        arrayOffset.addUse(this);
        dest.setDef(this);
    }

    public Operand ptr() {
        return ptr;
    }
    public Operand arrayOffset() {
        return arrayOffset;
    }
    public ConstInt elementOffset() {
        return elementOffset;
    }
    public IRBaseType type() {
        return type;
    }

    @Override
    public String toString() {
        String arrayOffToStr = arrayOffset.type().toString() + " " + arrayOffset.toString();
        String elementOffToStr = elementOffset == null ? "" :
                ", " + elementOffset.type().toString() + " " + elementOffset.toString();
        return dest().toString() + " = getelementptr inbounds " + type.toString() + ", " +
                ptr.type().toString() + " " + ptr.toString() + ", " + arrayOffToStr + elementOffToStr;
    }

    @Override
    public HashSet<Operand> uses() {
        HashSet<Operand> ret = new HashSet<>();
        ret.add(ptr);ret.add(arrayOffset);
        return ret;
    }

    @Override
    public void addMirror(IRBlock destBlock, MIRMirror mirror) {
        destBlock.addInst(new GetElementPtr(type, mirror.opMir(ptr), mirror.opMir(arrayOffset),
                elementOffset, (Register)mirror.opMir(dest()), destBlock));
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        if (ptr == replaced) ptr = replaceTo;
        if (arrayOffset == replaced) arrayOffset = replaceTo;
    }
    @Override
    public void removeSelf(boolean removeFromBlock) {
        if (removeFromBlock) block().remove(this);
        ptr.removeUse(this);
        arrayOffset.removeUse(this);
        if (elementOffset != null) elementOffset.removeUse(this);
    }
    @Override
    public boolean isTerminal() {
        return false;
    }
    @Override
    public boolean sameMeaning(Inst inst) {
        if (inst instanceof GetElementPtr) {
            GetElementPtr gep = (GetElementPtr) inst;
            return gep.ptr().equals(ptr) && gep.arrayOffset().equals(arrayOffset)
                    && ((elementOffset == null && gep.elementOffset() == null) ||
                        (elementOffset != null && elementOffset.equals(gep.elementOffset())));
        } else return false;
    }

    @Override
    public boolean noSideEffect() {
        return true;
    }
}
