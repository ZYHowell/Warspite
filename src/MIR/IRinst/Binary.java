package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import Util.MIRMirror;

import java.util.HashSet;

public class Binary extends Inst {
    public enum BinaryOpCategory {
        mul, sdiv, srem, shl, ashr, and, or, xor, sub, add
        //* / % << >> & | ^ - + =
    }
    private BinaryOpCategory opCode;
    private Operand src1, src2;

    public Binary(Operand src1, Operand src2, Register dest, BinaryOpCategory opCode, IRBlock block) {
        super(dest, block);
        this.src1 = src1;
        this.src2 = src2;
        this.opCode = opCode;
        src1.addUse(this);src2.addUse(this);
        dest.setDef(this);
    }
    //the type is always the same as its src

    @Override
    public String toString() {
        return dest().name() + " = " + opCode.toString() + " " +
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
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        if (src1 == replaced) src1 = replaceTo;
        if (src2 == replaced) src2 = replaceTo;
    }
    @Override
    public void removeSelf() {
        block().remove(this);
    }
}
