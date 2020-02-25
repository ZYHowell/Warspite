package MIR.IRinst;

import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.IRtype.IRBaseType;

public class GetElementPtr extends Inst{

    private Operand arrayOffset, elementOffset;
    private IRBaseType type;
    private Operand ptr;
    private Register dest;

    public GetElementPtr(IRBaseType type, Operand ptr, Operand arrayOffset,
                         Operand elementOffset, Register dest) {
        super();
        this.type = type;
        this.ptr = ptr;
        this.arrayOffset = arrayOffset;
        this.elementOffset = elementOffset; //this can be null
        this.dest = dest;
    }

    @Override
    public String toString() {
        String arrayOffToStr = arrayOffset.type().toString() + " " + arrayOffset.toString();
        String elementOffToStr = elementOffset == null ? "" :
                elementOffset.type().toString() + " " + elementOffset.toString();
        return dest.toString() + " = getelementptr inbounds " + type.name() + ", " +
                ptr.type().toString() + ", " + arrayOffToStr + elementOffToStr;
    }
}
