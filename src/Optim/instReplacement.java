package Optim;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.Binary;
import MIR.IRinst.Cmp;
import MIR.IRinst.Inst;
import MIR.IRoperand.ConstInt;
import MIR.IRoperand.Operand;
import MIR.Root;

import java.util.Iterator;

import static MIR.IRinst.Binary.BinaryOpCat.*;
import static MIR.IRinst.Cmp.CmpOpCategory.sgt;
import static MIR.IRinst.Cmp.CmpOpCategory.slt;

public class instReplacement extends Pass {

    private Root irRoot;
    public instReplacement(Root irRoot) {
        super();
        this.irRoot = irRoot;
    }

    public int twoPow(int value) {
        if (value <= 0) return -1;
        int powNum = 0;
        while(value > 1) {
            if (value % 2 != 0) return -1;
            value = value / 2;
            ++powNum;
        }
        return powNum;
    }
    public void runForBlock(IRBlock block) {
        for (Inst inst = block.headInst; inst != null; inst = inst.next) {
            if (inst instanceof Binary) {
                Binary bi = (Binary) inst;
                Operand src = null;
                if (bi.opCode() == mul) {
                    int powNum = -1;
                    if (bi.src1() instanceof ConstInt) {
                        if (((ConstInt) bi.src1()).value() == 0) {
                            bi.dest().replaceAllUseWith(new ConstInt(0, 32));
                            bi.removeSelf(true);
                        }
                        powNum = twoPow(((ConstInt) bi.src1()).value());
                        src = bi.src2();
                    }
                    if (bi.src2() instanceof ConstInt) {
                        if (((ConstInt) bi.src2()).value() == 0) {
                            bi.dest().replaceAllUseWith(new ConstInt(0, 32));
                            bi.removeSelf(true);
                        }
                        powNum = twoPow(((ConstInt) bi.src2()).value());
                        src = bi.src1();
                    }
                    if (powNum > 0) {
                        bi.strengthReduction(src, new ConstInt(powNum, 32), shl);
                        continue;
                    }
                } else if (bi.opCode() == add) {
                    if (bi.src1() instanceof ConstInt && ((ConstInt)bi.src1()).value() == 0) src = bi.src2();
                    else if (bi.src2() instanceof ConstInt && ((ConstInt)bi.src2()).value() == 0) src = bi.src1();
                    else continue;
                } else if (bi.opCode() == sub) {
                    if (bi.src2() instanceof ConstInt && ((ConstInt)bi.src2()).value() == 0) src = bi.src1();
                } else if (bi.opCode() == sdiv) {
                    if (bi.src2() instanceof ConstInt && ((ConstInt)bi.src2()).value() == 1) src = bi.src1();
                } else if (bi.opCode() == shl) {
                    if (bi.src2() instanceof ConstInt && ((ConstInt)bi.src2()).value() == 0) src = bi.src1();
                } else continue;
                bi.dest().replaceAllUseWith(src);
                bi.removeSelf(true);
            } else if (inst instanceof Cmp) {
                Cmp cm = (Cmp) inst;
                Operand src1, src2;
                switch (cm.opCode()) {
                    case sgt:
                        cm.modify(cm.src2(), cm.src1(), slt);
                        break;
                    case sle: {
                        if (cm.src1() instanceof ConstInt){
                            src1 = new ConstInt(((ConstInt) cm.src1()).value() - 1, 32);
                            src2 = cm.src2();
                        } else if (cm.src2() instanceof ConstInt) {
                            src2 = new ConstInt(((ConstInt) cm.src2()).value() + 1, 32);
                            src1 = cm.src1();
                        } else break;
                        cm.modify(src1, src2, slt);
                        break;
                    }
                    case sge:{
                        if (cm.src1() instanceof ConstInt){
                            src2 = new ConstInt(((ConstInt) cm.src1()).value() + 1, 32);
                            src1 = cm.src2();
                        } else if (cm.src2() instanceof ConstInt) {
                            src1 = new ConstInt(((ConstInt) cm.src2()).value() - 1, 32);
                            src2 = cm.src1();
                        } else break;
                        cm.modify(src1, src2, slt);
                        break;
                    }
                }
            }
        }
    }
    public void runForFn(Function fn) {
        fn.blocks().forEach(this::runForBlock);
    }
    @Override
    public boolean run() {
        irRoot.functions().forEach((name, fn) -> runForFn(fn));
        return true;    //only need to do once
    }
}
