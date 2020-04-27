package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import Util.MIRMirror;

import java.util.HashSet;

public class Binary extends Inst {
    public enum BinaryOpCat {
        mul, sdiv, srem, shl, ashr, and, or, xor, sub, add
        //*  /     %     <<   >>    &    |   ^    -    +
    }
    private BinaryOpCat opCode;
    private Operand src1, src2;
    private boolean canCommute;

    public Binary(Operand src1, Operand src2, Register dest, BinaryOpCat opCode, IRBlock block) {
        super(dest, block);
        this.src1 = src1;
        this.src2 = src2;
        this.opCode = opCode;
        canCommute = opCode == BinaryOpCat.add || opCode == BinaryOpCat.mul ||
                     opCode == BinaryOpCat.and || opCode == BinaryOpCat.or ||
                     opCode == BinaryOpCat.xor;
        src1.addUse(this);src2.addUse(this);
        dest.setDef(this);
    }
    //the type is always the same as its src
    public Operand src1() {
        return src1;
    }
    public Operand src2() {
        return src2;
    }
    public BinaryOpCat opCode() {
        return opCode;
    }
    public void strengthReduction(Operand src1, Operand src2, BinaryOpCat opCode) {
        this.src1.removeUse(this);
        this.src2.removeUse(this);
        src1.addUse(this);
        src2.addUse(this);
        this.src1 = src1;
        this.src2 = src2;
        this.opCode = opCode;
    }
    public boolean commutable() {
        return canCommute;
    }

    @Override
    public String toString() {
        return dest().toString() + " = " + opCode.toString() + " " +
                src1.type().toString() + " " + src1.toString() + ", " + src2.toString();
    }

    @Override
    public void addMirror(IRBlock destBlock, MIRMirror mirror) {
        destBlock.addInst(new Binary(mirror.opMir(src1), mirror.opMir(src2),
                (Register)mirror.opMir(dest()), opCode, destBlock));
    }

    @Override
    public HashSet<Operand> uses() {
        HashSet<Operand> ret = new HashSet<>();
        ret.add(src1);ret.add(src2);
        return ret;
    }

    @Override
    public boolean sameMeaning(Inst inst) {
        if (inst instanceof Binary) {
            Binary instr = (Binary)inst;
            if (opCode == instr.opCode()){
                if (canCommute) {
                return (instr.src1().equals(src1) && instr.src2().equals(src2)) ||
                       (instr.src1().equals(src2) && instr.src2().equals(src1));
                }
                else return instr.src1().equals(src1) && instr.src2().equals(src2);
            }
        }
        return false;
    }

    @Override
    public boolean canHoist() {
        return true;
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
