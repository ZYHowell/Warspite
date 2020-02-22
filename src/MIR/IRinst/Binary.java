package MIR.IRinst;

import MIR.IRoperand.Operand;

public class Binary extends Inst {
    public enum BinaryOpCategory {
        mul, sdiv, srem, shl, ashr, and, or, xor, sub, add
        //* / % << >> & | ^ - + =
    }
    private BinaryOpCategory opCode;
    private Operand src1, src2, dest;

    public Binary(Operand src1, Operand src2, Operand dest, BinaryOpCategory opCode) {
        super();
        this.src1 = src1;
        this.src2 = src2;
        this.opCode = opCode;
        this.dest = dest;
    }
    //the type is always the same as its src
}
