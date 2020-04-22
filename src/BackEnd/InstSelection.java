package BackEnd;

import Assemb.*;
import Assemb.LOperand.*;
import Assemb.RISCInst.*;
import MIR.*;
import MIR.IRoperand.*;
import MIR.IRinst.*;
import MIR.IRtype.ClassType;
import MIR.IRtype.Pointer;
import Optim.LoopDetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static Assemb.RISCInst.RISCInst.*;
import static Assemb.RISCInst.RISCInst.CalCategory.*;
import static Assemb.RISCInst.RISCInst.EzCategory.*;

/*
 * for extra params(a8, a9, ...an) they are in stack:
 * 0 an, an-1, an-2...a8
 * ^
 * |
 * sp
 */
public class InstSelection {

    private Root irRoot;
    private LRoot lRoot = new LRoot();
    private HashMap<Function, LFn> fnMap = new HashMap<>();
    private HashMap<IRBlock, LIRBlock> blockMap = new HashMap<>();
    private HashMap<Operand, Reg> regMap = new HashMap<>();

    private LFn currentLFn;
    private LIRBlock currentBlock;

    public InstSelection(Root irRoot) {
        this.irRoot = irRoot;
    }

    private Reg RegM2L(Operand src) {
        LIRBlock block = currentBlock;
        if (src instanceof Register || src instanceof Param) {
            if (!regMap.containsKey(src)) regMap.put(src,
                    new VirtualReg(src.type().size() / 8));
            return regMap.get(src);
        } else if (src instanceof GlobalReg || src instanceof ConstString) {
            if (!regMap.containsKey(src)) {
                int size;
                if (src.type().isResolvable()) size = ((Pointer)src.type()).pointTo().size();
                else size = src.type().size();
                String name;
                if (src instanceof GlobalReg) name = ((GlobalReg)src).name();
                else name = "." + ((ConstString)src).name;
                GReg reg = new GReg(size / 8, name);
                regMap.put(src, reg);
                if (src instanceof ConstString) lRoot.addString(reg, ((ConstString)src).value());
                else lRoot.addGlobalReg(reg);
                return reg;
            } else return regMap.get(src);
        } else if (src instanceof ConstInt) {
            VirtualReg reg = new VirtualReg(4);
            block.addInst(new Li(new Imm(((ConstInt) src).value()), reg, block));
            return reg;
        }
        else if (src instanceof ConstBool) {
            VirtualReg reg = new VirtualReg(1);
            block.addInst(new Li(new Imm(((ConstBool) src).value() ? 1 : 0), reg, block));
            return reg;
        } else {
            VirtualReg reg = new VirtualReg(1);
            block.addInst(new Li(new Imm(0), reg, block));
            return reg;
        }
    }
    private boolean inBounds(int value) {
        return (value < (1 << 11)) && (value > (-1 * (1 << 11)));
    }
    private boolean isBranchReg(Register reg, IRBlock block) {
        HashSet<Inst> uses = reg.uses();
        if (uses.size() == 1) for (Inst use : uses) if (use == block.terminator()) return true;
        return false;
    }

    private void genBinaryLIR(Operand src1, Operand src2, Reg dest, Binary.BinaryOpCat op, boolean commutable) {
        LIRBlock block = currentBlock;
        CalCategory opCode = null;
        switch(op) {
            case mul: opCode = mul;break;
            case sdiv: opCode = div;break;
            case srem: opCode = rem;break;
            case shl: opCode = sll;break;
            case ashr: opCode = sra;break;
            case and: opCode = and;break;
            case or: opCode = or;break;
            case xor: opCode = xor;break;
            case sub: opCode = sub;break;
            case add: opCode = add;break;
        }
        if (src1 instanceof ConstInt && commutable &&
                inBounds(((ConstInt)src1).value())){
            block.addInst(new IType(RegM2L(src2), new Imm(((ConstInt)src1).value()),
                    opCode, dest, block));
            return;
        }
        else if (src2 instanceof ConstInt && inBounds(((ConstInt)src2).value())){
            block.addInst(new IType(RegM2L(src1), new Imm(((ConstInt)src2).value()),
                    opCode,  dest, block));
            return;
        }
        block.addInst(new RType(RegM2L(src1), RegM2L(src2), opCode, dest, block));
    }
    private void genSltLIR(Operand src1, Operand src2, Reg dest) {
        if (src2 instanceof ConstInt && inBounds(((ConstInt)src2).value())){
            currentBlock.addInst(new IType(RegM2L(src1), new Imm(((ConstInt)src2).value()),
                    slt, dest, currentBlock));
            return;
        }
        currentBlock.addInst(new RType(RegM2L(src1), RegM2L(src2), slt, dest, currentBlock));
    }
    private void genLIR(Inst inst) {
        LIRBlock block = currentBlock;
        if (inst instanceof Binary) {
            Binary bi = (Binary) inst;
            genBinaryLIR(bi.src1(), bi.src2(), RegM2L(inst.dest()), bi.opCode(), bi.commutable());
        }
        else if (inst instanceof BitCast) {
            if (regMap.containsKey(inst.dest()))
                block.addInst(new Mv(RegM2L(((BitCast) inst).origin()), regMap.get(inst.dest()), block));
            else regMap.put(inst.dest(), RegM2L(((BitCast) inst).origin()));
        }
        else if (inst instanceof Branch) {
            Branch br = (Branch) inst;
            if (br.condition() instanceof Register && isBranchReg((Register)br.condition(), inst.block())) {
                if (((Register) br.condition()).def() instanceof Cmp) {
                    Cmp cm = (Cmp) ((Register) br.condition()).def();
                    Cmp.CmpOpCategory opCode = cm.opCode();
                    switch (opCode){
                        case slt:
                            block.addInst(new Br(RegM2L(cm.src1()), RegM2L(cm.src2()), Br.BrCategory.lt,
                                    blockMap.get(br.trueDest()), block));
                            break;
                        case sgt:
                            block.addInst(new Br(RegM2L(cm.src2()), RegM2L(cm.src1()), Br.BrCategory.lt,
                                    blockMap.get(br.trueDest()), block));
                            break;
                        case sle:
                            block.addInst(new Br(RegM2L(cm.src2()), RegM2L(cm.src1()), Br.BrCategory.ge,
                                    blockMap.get(br.trueDest()), block));
                            break;
                        case sge:
                            block.addInst(new Br(RegM2L(cm.src1()), RegM2L(cm.src2()), Br.BrCategory.ge,
                                    blockMap.get(br.trueDest()), block));
                            break;
                        case eq:
                            block.addInst(new Br(RegM2L(cm.src1()), RegM2L(cm.src2()), Br.BrCategory.eq,
                                    blockMap.get(br.trueDest()), block));
                            break;
                        case ne:
                            block.addInst(new Br(RegM2L(cm.src1()), RegM2L(cm.src2()), Br.BrCategory.ne,
                                    blockMap.get(br.trueDest()), block));
                            break;
                    }
                    block.addInst(new Jp(blockMap.get(br.falseDest()), block));
                    return;
                }
            }
            block.addInst(new Bz(RegM2L(br.condition()), eq, blockMap.get(br.falseDest()), block));
            block.addInst(new Jp(blockMap.get(br.trueDest()), block));
        }
        else if (inst instanceof Call) {
            //move first eight params into a0-a7
            Call ca = (Call) inst;
            for (int i = 0;i < Integer.min(8, ca.params().size());++i)
                block.addInst(new Mv(RegM2L(ca.params().get(i)), lRoot.getPhyReg(i + 10), block));
            //store extra params
            int paramOff = 0;
            for (int i = 8;i < ca.params().size();++i) {
                Operand param = ca.params().get(i);
                block.addInst(new St(lRoot.getPhyReg(2), RegM2L(param), new Imm(paramOff),
                        param.type().size() / 8, block));
                paramOff += 4;
            }
            if (paramOff > currentLFn.paramOffset) currentLFn.paramOffset = paramOff;
            //add function call
            block.addInst(new Cal(fnMap.get(ca.callee()), block));
            if (inst.dest() != null)
                block.addInst(new Mv(lRoot.getPhyReg(10), RegM2L(inst.dest()), block));
        }
        else if (inst instanceof Cmp) {
            Cmp cm = (Cmp) inst;
            if (isBranchReg(cm.dest(), inst.block())) return;
            switch (cm.opCode()) {
                case slt: {
                    genSltLIR(cm.src1(), cm.src2(), RegM2L(cm.dest()));
                    break;
                }
                case sgt:
                    genSltLIR(cm.src2(), cm.src1(), RegM2L(cm.dest()));
                    break;
                case sle:
                    VirtualReg sleTmp = new VirtualReg(4);
                    genSltLIR(cm.src2(), cm.src1(), sleTmp);
                    block.addInst(new IType(sleTmp, new Imm(1), xor, RegM2L(cm.dest()), block));
                    break;
                case sge:
                    VirtualReg sgeTmp = new VirtualReg(4);
                    genSltLIR(cm.src1(), cm.src2(), sgeTmp);
                    block.addInst(new IType(sgeTmp, new Imm(1), xor, RegM2L(cm.dest()), block));
                    break;
                case eq:
                    VirtualReg eqTmp = new VirtualReg(4);
                    genBinaryLIR(cm.src1(), cm.src2(), eqTmp, Binary.BinaryOpCat.xor, true);
                    block.addInst(new Sz(eqTmp, eq, RegM2L(cm.dest()), block));
                    break;
                case ne:
                    VirtualReg neTmp = new VirtualReg(4);
                    genBinaryLIR(cm.src1(), cm.src2(), neTmp, Binary.BinaryOpCat.xor, true);
                    block.addInst(new Sz(neTmp, ne, RegM2L(cm.dest()), block));
                    break;
            }
        }
        else if (inst instanceof GetElementPtr) {
            GetElementPtr gep = (GetElementPtr) inst;
            //get the array offset(arrOff * typeSize)
            VirtualReg destMul = new VirtualReg(4);
            Reg destIdx;
            int typeSize = gep.type().size() / 8;
            if (gep.arrayOffset() instanceof ConstInt) {
                int arrIndex = ((ConstInt) gep.arrayOffset()).value();
                if (arrIndex != 0) {
                    destIdx = new VirtualReg(4);
                    genBinaryLIR(gep.ptr(), new ConstInt(arrIndex * typeSize, 32), destIdx,
                            Binary.BinaryOpCat.add, true);
                } else destIdx = RegM2L(gep.ptr());
            } else {
                genBinaryLIR(gep.arrayOffset(), new ConstInt(typeSize, 32), destMul,
                        Binary.BinaryOpCat.mul, true);
                destIdx = new VirtualReg(4);
                block.addInst(new RType(RegM2L(gep.ptr()), destMul, add, destIdx, block));
            }
            //get the element offset(eleOff)
            Reg destPtr;
            if (gep.elementOffset() == null) destPtr = destIdx;
            else {
                int value = gep.elementOffset().value();
                if (value == 0) destPtr = destIdx;
                else {
                    assert gep.type() instanceof ClassType;
                    value = ((ClassType) gep.type()).getEleOff(value) / 8;
                    destPtr = new VirtualReg(4);
                    if (inBounds(value)) block.addInst(new IType(destIdx, new Imm(value), add,
                            destPtr, block));
                    else block.addInst(new RType(destIdx, RegM2L(new ConstInt(value, 32)), add,
                            destPtr, block));
                }
            }
            if (regMap.containsKey(gep.dest())) {
                block.addInst(new Mv(destPtr, regMap.get(gep.dest()), block));
            } else regMap.put(gep.dest(), destPtr);
        }
        else if (inst instanceof Jump) {
            block.addInst(new Jp(blockMap.get(((Jump) inst).destBlock()), block));
        }
        else if (inst instanceof Load) {
            Operand address = ((Load) inst).address(), dest = inst.dest();
            block.addInst(new Ld(RegM2L(address), RegM2L(dest), new Imm(0),
                    dest.type().size() / 8, block));
        }
        else if (inst instanceof Malloc) {
            block.addInst(new Mv(RegM2L(((Malloc) inst).length()), lRoot.getPhyReg(10), block));
            block.addInst(new Cal(fnMap.get(irRoot.getBuiltinFunction("g_Malloc")), block));
            block.addInst(new Mv(lRoot.getPhyReg(10), RegM2L(inst.dest()), block));
        }
        else if (inst instanceof Move) {
            Move mv = (Move) inst;
            if (mv.origin() instanceof ConstInt)
                block.addInst(new Li(new Imm(((ConstInt) mv.origin()).value()), RegM2L(mv.dest()), block));
            else block.addInst(new Mv(RegM2L(mv.origin()), RegM2L(mv.dest()), block));
        }
        else if (inst instanceof Return) {
            Operand value = ((Return) inst).value();
            if (value != null) {
                block.addInst(new Mv(RegM2L(value), lRoot.getPhyReg(10), block));
            }
            // block.addInst(new Ret(block));
        }
        else if (inst instanceof Store) {
            Operand value = ((Store) inst).value(), address = ((Store) inst).address();
            block.addInst(new St(RegM2L(address), RegM2L(value), new Imm(0),
                    value.type().size(), block));
        }
        else if (inst instanceof Zext) {
            if (regMap.containsKey(inst.dest()))
                block.addInst(new Mv(RegM2L(((Zext)inst).origin()), regMap.get(inst.dest()), block));
            else regMap.put(inst.dest(), RegM2L(((Zext)inst).origin()));
        }
    }

    private void copyBlock(IRBlock origin, LIRBlock block) {
        currentBlock = block;
        origin.instructions().forEach(this::genLIR);
    }
    private void runForFn(Function fn) {
        LFn lFn = fnMap.get(fn);
        currentLFn = lFn;
        LIRBlock entryBlock = lFn.entryBlock(), exitBlock = lFn.exitBlock();
        ArrayList<VirtualReg> calleeSaveMap = new ArrayList<>();
        lRoot.calleeSave().forEach(reg -> {
            VirtualReg map = new VirtualReg(4);
            calleeSaveMap.add(map);
            entryBlock.addInst(new Mv(reg, map, entryBlock));
        });
        for (int i = 0;i < Integer.min(8, fn.params().size());++i) {
            entryBlock.addInst(new Mv(lRoot.getPhyReg(10 + i), lFn.params().get(i), entryBlock));
        }
        int paraOffset = 0;
        for (int i = 8; i < fn.params().size();++i) {
            entryBlock.addInst(new Ld(lRoot.getPhyReg(2), lFn.params().get(i),
                    new SLImm(paraOffset), fn.params().get(i).type().size() / 8, entryBlock));
            paraOffset += fn.params().get(i).type().size() / 8;
        }
        fn.blocks().forEach(block -> {
            LIRBlock lBlock = blockMap.get(block);
            copyBlock(block, lBlock);
            lFn.addBlock(lBlock);
        });
        for (int i = 0;i < lRoot.calleeSave().size();++i)
            exitBlock.addInst(new Mv(calleeSaveMap.get(i), lRoot.calleeSave().get(i), entryBlock));
        exitBlock.addInst(new Ret(exitBlock));
    }
    public LRoot run() {
        irRoot.builtinFunctions().forEach((name, fn) -> {
            LFn func = new LFn(name, null, null);
            lRoot.addBuiltinFunction(func);
            fnMap.put(fn, func);
        });
        irRoot.functions().forEach((name, fn) -> {
            new LoopDetector(fn, false).runForFn();
            fn.blocks().forEach(block -> {
                LIRBlock lBlock = new LIRBlock(block.loopDepth, "." + fn.name() + "_" + block.name());
                blockMap.put(block, lBlock);
            });
            //this is for reg alloc
            LFn lFn = new LFn(name, blockMap.get(fn.entryBlock()), blockMap.get(fn.exitBlock()));
            fnMap.put(fn, lFn);
            if (fn.getClassPtr() != null) lFn.addPara(RegM2L(fn.getClassPtr()));
            fn.params().forEach(para -> lFn.addPara(RegM2L(para)));
            lRoot.addFunction(lFn);
        });
        irRoot.functions().forEach((name, fn) -> runForFn(fn));
        return lRoot;
    }
}
