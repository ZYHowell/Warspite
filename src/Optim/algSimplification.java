package Optim;

import MIR.IRinst.Binary;
import MIR.IRinst.Inst;
import MIR.IRoperand.ConstInt;
import MIR.IRoperand.Register;
import MIR.Root;

import static MIR.IRinst.Binary.BinaryOpCat.*;
public class algSimplification extends Pass {
    private class Pair {
        public Binary.BinaryOpCat op;
        public int value;
        public Pair(Binary.BinaryOpCat op, int value) {
            this.op = op;
            this.value = value;
        }
    }
    private Root root;
    private boolean change = false;
    public algSimplification(Root root) {
        this.root = root;
    }
    private int partialConst(Binary inst) {
        if (inst.src1() instanceof ConstInt
                && inst.src2() instanceof Register
                && inst.commutable())
            return 1;
        else if (inst.src2() instanceof ConstInt
                && inst.src1() instanceof Register)
            return 2;
        else return 0;
    }
    private Pair try_combination(
            int a, int b, Binary.BinaryOpCat op1, Binary.BinaryOpCat op2) {
        int value;
        if (op1 == add) {
            if (op2 == add) value = a + b;
            else if (op2 == sub) value = a - b;
            else return new Pair(null, 0);
            return new Pair(add, value);
        } else if (op1 == mul) {
            if (op2 == mul) value = a * b;
            else return new Pair(null, 0);
            // dangerous to transform x / b * a into x * (a/b) for int
            return new Pair(mul, value);
        } else if (op1 == sub) {
            if (op2 == sub) value = a + b;
            else if (op2 == add) value = a - b;
            else return new Pair(null, 0);
            return new Pair(sub, value);
        }
        return new Pair(null, 0);
    }
    public void binary_alg(Binary inst) {
        int src1;
        Register src2;
        switch(partialConst(inst)){
            case 1: {
                src1 = ((ConstInt) inst.src1()).value();
                src2 = (Register)inst.src2();
                break;
            }
            case 2: {
                src1 = ((ConstInt) inst.src2()).value();
                src2 = (Register)inst.src1();
                break;
            }
            default: return;
        }
        if (src2.uses().size() == 1 && src2.def() instanceof Binary){
            Binary b = (Binary) src2.def();
            int src_b1;
            switch (partialConst(b)){
                case 1: {
                    src_b1 = ((ConstInt) b.src1()).value();
                    src2 = (Register)b.src2();
                    break;
                }
                case 2: {
                    src_b1 = ((ConstInt) b.src2()).value();
                    src2 = (Register)b.src1();
                    break;
                }
                default: return;
            }
            Pair combination =
                    try_combination(src1, src_b1, inst.opCode(), b.opCode());
            if (combination.op == null) return;
            inst.strengthReduction(src2, new ConstInt(combination.value, 32), combination.op);
            b.removeSelf(true);
            change = true;
        }
    }
    @Override
    public boolean run() {
        change = false;
        root.functions().forEach((n, f) -> {
            f.blocks.forEach(b -> {
                for (Inst inst = b.headInst; inst != null; inst = inst.next) {
                    if (inst instanceof Binary) binary_alg((Binary) inst);
                }
            });
        });
        return change;
    }
}
