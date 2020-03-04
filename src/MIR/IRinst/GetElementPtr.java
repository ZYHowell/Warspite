package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.IRtype.IRBaseType;
import Util.MIRMirror;

import java.util.HashSet;

public class GetElementPtr extends Inst{

    private Operand arrayOffset, elementOffset;
    private IRBaseType type;
    private Operand ptr;

    public GetElementPtr(IRBaseType type, Operand ptr, Operand arrayOffset,
                         Operand elementOffset, Register dest, IRBlock block) {
        super(dest, block);
        this.type = type;
        this.ptr = ptr;
        this.arrayOffset = arrayOffset;
        this.elementOffset = elementOffset; //this can be null
        ptr.addUse(this);
        arrayOffset.addUse(this);
        if (elementOffset != null) elementOffset.addUse(this);
        dest.setDef(this);
    }

    @Override
    public String toString() {
        String arrayOffToStr = arrayOffset.type().toString() + " " + arrayOffset.toString();
        String elementOffToStr = elementOffset == null ? "" :
                elementOffset.type().toString() + " " + elementOffset.toString();
        return dest().toString() + " = getelementptr inbounds " + type.toString() + ", " +
                ptr.type().toString() + ", " + arrayOffToStr + elementOffToStr;
    }

    @Override
    public HashSet<Operand> uses() {
        HashSet<Operand> ret = new HashSet<>();
        ret.add(ptr);ret.add(arrayOffset);
        if (elementOffset != null) ret.add(elementOffset);
        return ret;
    }

    @Override
    public void addMirror(IRBlock destBlock, MIRMirror mirror) {
        destBlock.addInst(new GetElementPtr(type, mirror.opMir(ptr), mirror.opMir(arrayOffset),
                mirror.opMir(elementOffset), (Register)mirror.opMir(dest()), destBlock));
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        if (ptr == replaced) ptr = replaceTo;
        if (arrayOffset == replaced) arrayOffset = replaceTo;
        if (elementOffset == replaced) elementOffset = replaceTo;
    }
    @Override
    public void removeSelf() {
        block().remove(this);
    }
}
