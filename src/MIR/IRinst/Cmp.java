package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Null;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.IRtype.IRBaseType;
import Util.MIRMirror;

import java.util.HashSet;

public class Cmp extends Inst{
    public enum CmpOpCategory {
        slt, sgt, sle, sge, eq, ne
    }
    private CmpOpCategory opCode, inverseOpCode;
    private Operand src1, src2;

    public Cmp(Operand src1, Operand src2, Register dest, CmpOpCategory opCode, IRBlock block) {
        super(dest, block);
        this.src1 = src1;
        this.src2 = src2;
        this.opCode = opCode;
        if (opCode == CmpOpCategory.slt) inverseOpCode = CmpOpCategory.sgt;
        else if (opCode == CmpOpCategory.sgt) inverseOpCode = CmpOpCategory.slt;
        else if (opCode == CmpOpCategory.sle) inverseOpCode = CmpOpCategory.sge;
        else if (opCode == CmpOpCategory.sge) inverseOpCode = CmpOpCategory.sle;
        else if (opCode == CmpOpCategory.eq) inverseOpCode = CmpOpCategory.eq;
        else if (opCode == CmpOpCategory.ne) inverseOpCode = CmpOpCategory.ne;
        src1.addUse(this);
        src2.addUse(this);
        dest.setDef(this);
    }

    public Operand src1() {
        return src1;
    }
    public Operand src2() {
        return src2;
    }
    public CmpOpCategory opCode() {
        return opCode;
    }
    @Override
    public String toString() {
        String typeString;
        if (src1 instanceof Null) {
            if (src2 instanceof Null) typeString = "int*";
            else typeString = src2.type().toString();
        } else typeString = src1.type().toString();
        return dest().toString() + " = " + "icmp " + opCode.toString() + " " + typeString + " " +
                src1.toString()  + ", " + src2.toString();
    }

    @Override
    public HashSet<Operand> uses() {
        HashSet<Operand> ret = new HashSet<>();
        ret.add(src1);ret.add(src2);
        return ret;
    }

    @Override
    public boolean sameMeaning(Inst inst) {
        if (inst instanceof Cmp) {
            Cmp instr = (Cmp) inst;
            if (instr.opCode() == opCode)
                if (instr.src1() == src1 && instr.src2() == src2) return true;
            if (instr.opCode() == inverseOpCode)
                return instr.src1().equals(src2) && instr.src2().equals(src1);
        }
        return false;
    }

    @Override
    public boolean canHoist() {
        return true;
    }

    @Override
    public void addMirror(IRBlock destBlock, MIRMirror mirror) {
        destBlock.addInst(new Cmp(mirror.opMir(src1), mirror.opMir(src2),
                (Register)mirror.opMir(dest()), opCode, destBlock));
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        if (src1 == replaced) src1 = replaceTo;
        if (src2 == replaced) src2 = replaceTo;
    }
    @Override
    public void removeSelf(boolean removeFromBlock) {
        if (removeFromBlock) block().remove(this);
        src1.removeUse(this);src2.removeUse(this);
    }
    @Override
    public boolean isTerminal() {
        return false;
    }
}
