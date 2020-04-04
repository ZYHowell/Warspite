package Util;

import MIR.IRinst.Binary;
import MIR.IRoperand.ConstInt;
import MIR.IRoperand.Register;

public class MIRInductVar {

    private Register phiReg, addReg;
    private int constAt;

    public MIRInductVar(Register phiReg, Register addReg, int constAt) {
        this.phiReg = phiReg;
        this.addReg = addReg;
        this.constAt = constAt; //const is at 0 or 1
    }

    public Register phiReg() {
        return phiReg;
    }
    public Register addReg() {
        return addReg;
    }

    public int addValue() {
        Binary inst = (Binary)addReg.def();
        int sign = inst.opCode() == Binary.BinaryOpCat.add ? 1 : -1;
        if (constAt == 1)
            return ((ConstInt)inst.src2()).value() * sign;
        else return ((ConstInt)inst.src1()).value() * sign;
    }
}
