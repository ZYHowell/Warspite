package Optim;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.*;
import MIR.IRoperand.*;
import MIR.IRtype.BoolType;
import MIR.IRtype.IntType;
import MIR.Root;
import Util.DomGen;

import java.util.*;

public class SCCP extends Pass {

    private Root irRoot;
    private HashMap<Operand, Operand> constMap = new HashMap<>();
    private HashSet<IRBlock> visited = new HashSet<>();
    private boolean change, changeFn;
    private Function currentFn;

    public SCCP(Root irRoot) {
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
                    case sdiv: {
                        if (value2 == 0) return false;
                        destValue = value1 / value2; break;
                    }
                    case srem: {
                        if (value2 == 0) return false;
                        destValue = value1 % value2; break;
                    }
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
            return true;
        } else return false;
    }
    private boolean zextCheck(Zext inst) {
        int value;
        if (inst.origin() instanceof ConstInt) value = ((ConstInt) inst.origin()).value();
        else if (inst.origin() instanceof ConstBool)
            value = ((ConstBool) inst.origin()).value() ? 1 : 0;
        else return false;
        Operand replace;
        if (inst.dest().type() instanceof IntType) {
            replace = new ConstInt(value, inst.dest().type().size());
        } else {
            assert inst.dest().type() instanceof BoolType;
            replace = new ConstBool(value != 0);
        }
        inst.dest().replaceAllUseWith(replace);
        return true;
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
    private boolean isConst(Operand src) {
        return src instanceof ConstInt || src instanceof ConstBool || src instanceof ConstString;
    }

    private void visit(IRBlock block) {
        visited.add(block);
        if (block.precursors().size() == 0 && block != currentFn.entryBlock()) {
            block.removeTerminator();   //unreachable block, wait to be removed
            block.addTerminator(new Jump(block, block));
            return;
        }
        for (Iterator<Map.Entry<Register, Phi>> iter = block.phiInst().entrySet().iterator(); iter.hasNext();) {
            Map.Entry<Register, Phi> entry = iter.next();
            Phi phi = entry.getValue();
            if (phiCheck(phi)) {
                iter.remove();
                phi.removeSelf(false);
                changeFn = true;
            }
        }
        for (Inst inst = block.headInst; inst != null; inst = inst.next) {
            if (inst == block.tailInst && inst instanceof Branch) {
                if (isConst(((Branch) inst).condition())) {
                    Operand src = ((Branch) inst).condition();
                    assert src instanceof ConstBool;
                    block.removeTerminator();
                    if (((ConstBool) src).value())
                        block.addTerminator(new Jump(((Branch) inst).trueDest(), block));
                    else
                        block.addTerminator(new Jump(((Branch) inst).falseDest(), block));
                    changeFn = true;
                    break;
                }
            }
            if ((inst instanceof Binary && binaryCheck((Binary) inst)) ||
                (inst instanceof Cmp && cmpCheck((Cmp) inst)) ||
                (inst instanceof Zext && zextCheck((Zext) inst))) {
                inst.removeSelf(true);
                changeFn = true;
            }
        }

        block.successors().forEach(suc -> {
            if (!visited.contains(suc)) visit(suc);
        });
    }
    private void runForFn(Function fn) {
        currentFn = fn;
        boolean everChanged = false;
        do {
            visited.clear();
            changeFn = false;
            visit(fn.entryBlock());
            fn.blocks().forEach(block -> {
                if (block.precursors().size() == 0 && block != currentFn.entryBlock()) {
                    block.removeTerminator();   //unreachable block, wait to be removed
                    block.addTerminator(new Jump(block, block));
                    changeFn = true;
                }
            });
            everChanged = everChanged || changeFn;
        } while(changeFn);
        if (everChanged) new DomGen(fn, true).runForFn();
        change = change || everChanged;
    }
    @Override
    public boolean run() {
        constMap = new HashMap<>();
        change = false;

        irRoot.functions().forEach((name, fn) -> runForFn(fn));

        return change;
        //running: start from the entry block, check each inst. it can be:
        // 1. some value used is uncertain: set the dest be uncertain,
        // and put all used into 'has uncertain' bucket
        // 2. all values used are certain: replace all use of the result.
        // 3. a branch inst, the result is uncertain/a jump inst: all dest blocks are alive.
        // 4. a branch inst, the result is certain: set the unreachable dest be unreachable by this one,
        // notice that, only if all precursors of a block are executed, can the block executed
        // so the block has 4 conditions: executed, halted, unreachable, executable
    }
}
