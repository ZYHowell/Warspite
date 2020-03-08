package Optim;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.*;
import MIR.IRoperand.*;
import MIR.Root;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConstFold extends Pass {

    private Root irRoot;
    private HashMap<Operand, Operand> constMap = new HashMap<>();

    public ConstFold(Root irRoot) {
        super();
        this.irRoot = irRoot;
    }

    private Operand constTrans(Operand src) {
        if (src instanceof ConstInt || src instanceof ConstString
                || src instanceof ConstBool)
            return src;
        if (src instanceof Null) return new ConstInt(0, 32);
        else return constMap.getOrDefault(src, null);
    }
    private boolean binaryCheck(Binary inst) {
        Operand op1, op2;
        op1 = constTrans(inst.src1());
        op2 = constTrans(inst.src2());
        if (op1 != null && op2 != null) {
            if (op1 instanceof ConstInt) {
                assert op2 instanceof ConstInt;
                int value1 = ((ConstInt)op1).value(), value2 = ((ConstInt)op2).value();
                int destValue = 0;
                switch(inst.opCode()) {
                    case mul: destValue = value1 * value2; break;
                    case sdiv: destValue = value1 / value2; break;
                    case srem: destValue = value1 % value2; break;
                    case shl: destValue = value1 << value2;break;
                    case ashr:destValue = value1 >> value2;break;
                    case sub:destValue = value1 - value2;break;
                    case add:destValue = value1 + value2;break;
                    default: assert false;
                }
                ConstInt replaceConst = new ConstInt(destValue, 32);
                inst.dest().replaceAllUseWith(replaceConst);
                constMap.put(inst.dest(), replaceConst);
            } else {
                assert op1 instanceof ConstBool;
                assert op2 instanceof ConstBool;
                boolean value1 = ((ConstBool) op1).value(), value2 = ((ConstBool) op2).value();
                boolean destValue = false;
                switch(inst.opCode()) {
                    case and: destValue = value1 && value2; break;
                    case or: destValue = value1 || value2;break;
                    case xor: destValue = value1 ^ value2;break;
                    default: assert false;
                }
                ConstBool replaceConst = new ConstBool(destValue);
                inst.dest().replaceAllUseWith(replaceConst);
                constMap.put(inst.dest(), replaceConst);
            }
            return true;
        } else return false;
    }
    private boolean cmpCheck(Cmp inst) {
        Operand op1, op2;
        op1 = constTrans(inst.src1());
        op2 = constTrans(inst.src2());
        if (op1 != null && op2 != null) {
            boolean destValue = false;
            if (op1 instanceof ConstInt) {
                assert op2 instanceof ConstInt;
                int value1 = ((ConstInt)op1).value(), value2 = ((ConstInt)op2).value();
                switch(inst.opCode()) {
                    case slt: destValue = value1 < value2;break;
                    case sgt: destValue = value1 > value2;break;
                    case sle: destValue = value1 <= value2;break;
                    case sge: destValue = value1 >= value2;break;
                    case eq:  destValue = value1 == value2;break;
                    case ne:  destValue = value1 != value2;break;
                    default:assert false;
                }
            } else {
                assert op1 instanceof ConstBool;
                assert op2 instanceof ConstBool;
                boolean value1 = ((ConstBool)op1).value(), value2 = ((ConstBool)op2).value();
                switch(inst.opCode()) {
                    case eq: destValue = value1 == value2;break;
                    case ne: destValue = value1 != value2;break;
                    default:assert false;
                }
            }
            ConstBool replaceConst = new ConstBool(destValue);
            inst.dest().replaceAllUseWith(replaceConst);
            constMap.put(inst.dest(), replaceConst);
            return true;
        } else return false;
    }
    private boolean phiCheck(Phi inst) {
        ArrayList<Operand> values = inst.values();
        Operand trans = constTrans(values.get(0));
        int size = values.size();
        assert size > 0;
        if (size == 1) {
            if (trans != null) inst.dest().replaceAllUseWith(trans);
            else inst.dest().replaceAllUseWith(values.get(0));
            return true;
        } else {
            if (trans != null) {
                boolean same = true;
                if (trans instanceof ConstBool) {
                    boolean value = ((ConstBool)trans).value();
                    for (int i = 1;i < size;++i) {
                        Operand currentTrans = constTrans(values.get(i));
                        if (!(currentTrans instanceof ConstBool
                                && ((ConstBool) currentTrans).value() == value)) {
                            same = false;
                            break;
                        }
                    }
                } else {
                    assert trans instanceof ConstInt;
                    int value = ((ConstInt)trans).value();
                    for (int i = 1;i < size;++i) {
                        Operand currentTrans = constTrans(values.get(i));
                        if (!(currentTrans instanceof ConstInt
                                && ((ConstInt) currentTrans).value() == value)) {
                            same = false;
                            break;
                        }
                    }
                }
                if (same) inst.dest().replaceAllUseWith(trans);
                return same;
            }
            else return false;
        }
    }

    @Override
    public boolean run() {
        boolean newChange = false;
        boolean change = true;
        while(change) {
            change = false;
            for (Map.Entry<String, Function> entry : irRoot.functions().entrySet()) {
                Function fn = entry.getValue();
                for (IRBlock block : fn.blocks()) {
                    for (Iterator<Inst> iter = block.instructions().iterator(); iter.hasNext(); ) {
                        Inst inst = iter.next();
                        boolean instChange = false;
                        if (inst instanceof Binary)
                            instChange = binaryCheck((Binary) inst);
                        else if (inst instanceof Cmp)
                            instChange = cmpCheck((Cmp) inst);
                        if (instChange) {
                            change = true;
                            iter.remove();
                            inst.removeSelf(false);
                        }
                    }
                    for (Iterator<Map.Entry<Register, Phi>> iter =
                         block.phiInst().entrySet().iterator(); iter.hasNext();) {
                        Phi inst = iter.next().getValue();
                        if (phiCheck(inst)) {
                            change = true;
                            iter.remove();
                            inst.removeSelf(false);
                        }
                    }
                }
            }
            if (change) newChange = true;
        }
        return newChange;
    }
}
