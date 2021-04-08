package BackEnd;

import Assemb.LFn;
import Assemb.LIRBlock;
import Assemb.LOperand.*;
import Assemb.LRoot;
import Assemb.RISCInst.*;
import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.*;
import MIR.IRoperand.*;
import MIR.IRtype.ClassType;
import MIR.IRtype.IRBaseType;
import MIR.IRtype.Pointer;
import MIR.Root;
import Optim.LoopDetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static Assemb.RISCInst.RISCInst.CalCategory;
import static Assemb.RISCInst.RISCInst.CalCategory.*;
import static Assemb.RISCInst.RISCInst.EzCategory.eq;
import static Assemb.RISCInst.RISCInst.EzCategory.ne;

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
    private int cnt;

    private LFn currentLFn;
    private LIRBlock currentBlock;
    private HashMap<Integer, Reg> liReg = new HashMap<>();

    public InstSelection(Root irRoot) {
        this.irRoot = irRoot;
    }
    private Reg RegM2L(Operand src) {   //transform the register from mir to lir
        LIRBlock block = currentBlock;
        if (src instanceof Register || src instanceof Param) {
            if (!regMap.containsKey(src)) regMap.put(src,
                    new VirtualReg(src.type().size() / 8, cnt++));
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
            int value = ((ConstInt) src).value();
            if (value == 0) return lRoot.getPhyReg(0);
            if (liReg.containsKey(value)) return liReg.get(value);

            VirtualReg reg = new VirtualReg(4, cnt++);
            block.addInst(new Li(new Imm(value), reg, block));
            liReg.put(value, reg);
            return reg;
        }
        else if (src instanceof ConstBool) {
            int value = ((ConstBool) src).value() ? 1 : 0;
            if (value == 0) return lRoot.getPhyReg(0);
            if (liReg.containsKey(value)) return liReg.get(value);

            VirtualReg reg = new VirtualReg(1, cnt++);
            block.addInst(new Li(new Imm(value), reg, block));
            liReg.put(value, reg);
            return reg;
        } else {
            return lRoot.getPhyReg(0);
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

    private boolean canBeImm(Operand src) {
        return src instanceof ConstInt && inBounds(((ConstInt) src).value());
    }
    private int reduction(int n) {
        int ret = 0;
        while(n > 1) {
            if (n % 2 == 0) {n /= 2;++ret;}
            else return -1;
        }
        return ret;
    }
    private void genBinaryLIR(Operand src1, Operand src2, Reg dest, Binary.BinaryOpCat op, boolean commutable) {
        LIRBlock block = currentBlock;
        CalCategory opCode = null;
        boolean noIType = false;
        switch(op) {
            case mul: opCode = mul;noIType = true;break;
            case sdiv: opCode = div;noIType = true;break;
            case srem: opCode = rem;noIType = true;break;
            case shl: opCode = sll;break;
            case ashr: opCode = sra;break;
            case and: opCode = and;break;
            case or: opCode = or;break;
            case xor: opCode = xor;break;
            case sub: opCode = sub;break;
            case add: opCode = add;break;
        }
        if (!noIType){
            if (commutable && canBeImm(src1)){
                block.addInst(new IType(RegM2L(src2), new Imm(((ConstInt)src1).value()),
                        opCode, dest, block));
                return;
            }
            else if (canBeImm(src2)){
                if (opCode != sub)
                block.addInst(new IType(RegM2L(src1), new Imm(((ConstInt)src2).value()),
                        opCode,  dest, block));
                else block.addInst(new IType(RegM2L(src1), new Imm(-1 * ((ConstInt)src2).value()),
                        add,  dest, block));
                return;
            }
        } else {
            if (opCode == mul) {
                if (canBeImm(src1)) {
                    int ret = reduction(((ConstInt) src1).value());
                    if (ret != -1) {
                        block.addInst(new IType(RegM2L(src2), new Imm(ret), sll, dest, block));
                        return;
                    }
                }
                else if (canBeImm(src2)) {
                    int ret = reduction(((ConstInt) src2).value());
                    if (ret != -1) {
                        block.addInst(new IType(RegM2L(src1), new Imm(ret), sll, dest, block));
                        return;
                    }
                }
            }
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
            Reg reg = RegM2L(((BitCast) inst).origin());
            if (reg instanceof GReg) block.addInst(new La((GReg) reg, RegM2L(inst.dest()), block));
            else block.addInst(new Mv(reg, RegM2L(inst.dest()), block));
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
            for (int i = 0;i < Integer.min(8, ca.params().size());++i){
                Reg reg = RegM2L(ca.params().get(i));
                if (reg instanceof GReg) block.addInst(new La((GReg) reg, lRoot.getPhyReg(i + 10), block));
                //actually no need since parameter cannot be global var
                else block.addInst(new Mv(reg, lRoot.getPhyReg(i + 10), block));
            }
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
            block.addInst(new Cal(lRoot, fnMap.get(ca.callee()), block));
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
                    VirtualReg sleTmp = new VirtualReg(4, cnt++);
                    genSltLIR(cm.src2(), cm.src1(), sleTmp);
                    block.addInst(new IType(sleTmp, new Imm(1), xor, RegM2L(cm.dest()), block));
                    break;
                case sge:
                    VirtualReg sgeTmp = new VirtualReg(4, cnt++);
                    genSltLIR(cm.src1(), cm.src2(), sgeTmp);
                    block.addInst(new IType(sgeTmp, new Imm(1), xor, RegM2L(cm.dest()), block));
                    break;
                case eq:
                    VirtualReg eqTmp = new VirtualReg(4, cnt++);
                    genBinaryLIR(cm.src1(), cm.src2(), eqTmp, Binary.BinaryOpCat.xor, true);
                    block.addInst(new Sz(eqTmp, eq, RegM2L(cm.dest()), block));
                    break;
                case ne:
                    VirtualReg neTmp = new VirtualReg(4, cnt++);
                    genBinaryLIR(cm.src1(), cm.src2(), neTmp, Binary.BinaryOpCat.xor, true);
                    block.addInst(new Sz(neTmp, ne, RegM2L(cm.dest()), block));
                    break;
            }
        }
        else if (inst instanceof GetElementPtr) {
            GetElementPtr gep = (GetElementPtr) inst;
            //get the array offset(arrOff * typeSize)
            VirtualReg destMul;
            Reg destIdx = new VirtualReg(4, cnt++);
            int typeSize = gep.type().size() / 8;
            if (gep.arrayOffset() instanceof ConstInt) {
                int arrIndex = ((ConstInt) gep.arrayOffset()).value();
                if (arrIndex != 0) {
                    genBinaryLIR(gep.ptr(), new ConstInt(arrIndex * typeSize, 32), destIdx,
                            Binary.BinaryOpCat.add, true);
                } else {
                    Reg origin = RegM2L(gep.ptr());
                    if (origin instanceof GReg) block.addInst(new La((GReg) origin, destIdx, block));
                    else block.addInst(new Mv(RegM2L(gep.ptr()), destIdx, block));
                }
            } else {
                destMul = new VirtualReg(4, cnt++);
                genBinaryLIR(gep.arrayOffset(), new ConstInt(typeSize, 32), destMul,
                        Binary.BinaryOpCat.mul, true);
                block.addInst(new RType(RegM2L(gep.ptr()), destMul, add, destIdx, block));
            }
            //get the element offset(eleOff)
            Reg destPtr;
            if (gep.elementOffset() == null) destPtr = destIdx;
            else {
                int value = gep.elementOffset().value();
                if (value == 0) destPtr = destIdx;
                else {
                    assert gep.ptr().type() instanceof Pointer;
                    IRBaseType type = ((Pointer)gep.ptr().type()).pointTo();
                    if (type instanceof ClassType)
                        value = ((ClassType) type).getEleOff(value) / 8;
                    else value = 0;
                    destPtr = new VirtualReg(4, cnt++);
                    if (inBounds(value)) block.addInst(new IType(destIdx, new Imm(value), add,
                            destPtr, block));
                    else block.addInst(new RType(destIdx, RegM2L(new ConstInt(value, 32)), add,
                            destPtr, block));
                }
            }

            if (regMap.containsKey(gep.dest()))
                block.addInst(new Mv(destPtr, regMap.get(gep.dest()), block));
            else regMap.put(gep.dest(), destPtr);   //this is good since destPtr must be a useless temp
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
            block.addInst(new Cal(lRoot, fnMap.get(irRoot.getBuiltinFunction("malloc")), block));
            block.addInst(new Mv(lRoot.getPhyReg(10), RegM2L(inst.dest()), block));
        }
        else if (inst instanceof Move) {
            Move mv = (Move) inst;
            if (mv.origin() instanceof ConstInt)
                block.addInst(new Li(new Imm(((ConstInt) mv.origin()).value()), RegM2L(mv.dest()), block));
            else if (mv.origin() instanceof GlobalReg || mv.origin() instanceof ConstString)
                block.addInst(new La((GReg)RegM2L(mv.origin()), RegM2L(mv.dest()), block));
            else if (mv.origin() instanceof ConstBool) {
                if (((ConstBool) mv.origin()).value()) block.addInst(new Li(new Imm(1), RegM2L(mv.dest()), block));
                else block.addInst(new Mv(lRoot.getPhyReg(0), RegM2L(mv.dest()), block));
            }
            else block.addInst(new Mv(RegM2L(mv.origin()), RegM2L(mv.dest()), block));
        }
        else if (inst instanceof Return) {
            Operand value = ((Return) inst).value();
            if (value != null) {
                Reg reg = RegM2L(value);
                if (reg instanceof GReg) block.addInst(new La((GReg)reg, lRoot.getPhyReg(10), block));
                else block.addInst(new Mv(reg, lRoot.getPhyReg(10), block));
            }
            // block.addInst(new Ret(block));
        }
        else if (inst instanceof Store) {
            Operand value = ((Store) inst).value(), address = ((Store) inst).address();
            Reg addressReg = RegM2L(address);
            if (addressReg instanceof GReg) {
                VirtualReg tmp = new VirtualReg(4, cnt++);
                Relocation rel = new Relocation((GReg) addressReg, true);
                Reg valueReg = RegM2L(value);
                block.addInst(new lui(rel, tmp, block));
                block.addInst(new St(tmp, valueReg, new Relocation((GReg) addressReg, false),
                        value.type().size() / 8, block));
            }
            else block.addInst(new St(RegM2L(address), RegM2L(value), new Imm(0),
                    value.type().size() / 8, block));
        }
        else if (inst instanceof Zext) {
            Reg reg = RegM2L(((Zext)inst).origin());
            if (reg instanceof GReg) block.addInst(new La((GReg) reg, RegM2L(inst.dest()), block));
            else block.addInst(new Mv(reg, RegM2L(inst.dest()), block));
        }
    }

    private void copyBlock(IRBlock origin, LIRBlock block) {
        currentBlock = block;
        for(Inst inst = origin.headInst; inst != null; inst = inst.next) genLIR(inst);
        origin.successors.forEach(suc -> {
            block.successors.add(blockMap.get(suc));
            blockMap.get(suc).precursors.add(block);
        });
    }
    private void runForFn(Function fn) {
        LFn lFn = fnMap.get(fn);
        currentLFn = lFn;
        cnt = 0;
        LIRBlock entryBlock = lFn.entryBlock(), exitBlock = lFn.exitBlock();
        ArrayList<VirtualReg> calleeSaveMap = new ArrayList<>();
        //set new sp
        SLImm stackL = new SLImm(0);
        stackL.reverse = true;
        entryBlock.addInst(new IType(lRoot.getPhyReg(2), stackL, add, lRoot.getPhyReg(2), entryBlock));
        //end set new sp, start store callee save
        lRoot.calleeSave().forEach(reg -> {
            VirtualReg map = new VirtualReg(4, cnt++);
            calleeSaveMap.add(map);
            entryBlock.addInst(new Mv(reg, map, entryBlock));
        });
        //end store new callee save, start store ra
        VirtualReg map = new VirtualReg(4, cnt++);
        entryBlock.addInst(new Mv(lRoot.getPhyReg(1), map, entryBlock));
        //end store ra, start move parameters to regs
        for (int i = 0;i < Integer.min(8, fn.params().size());++i) {
            entryBlock.addInst(new Mv(lRoot.getPhyReg(10 + i), lFn.params().get(i), entryBlock));
        }
        int paraOffset = 0;
        for (int i = 8; i < fn.params().size();++i) {
            entryBlock.addInst(new Ld(lRoot.getPhyReg(2), lFn.params().get(i),
                    new SLImm(paraOffset), fn.params().get(i).type().size() / 8, entryBlock));
            paraOffset += 4;
        }

        fn.blocks.forEach(block -> {
            LIRBlock lBlock = blockMap.get(block);
            liReg.clear();
            copyBlock(block, lBlock);
            lFn.addBlock(lBlock);
        });

        for (int i = 0;i < lRoot.calleeSave().size();++i)
            exitBlock.addInst(new Mv(calleeSaveMap.get(i), lRoot.calleeSave().get(i), exitBlock));
        exitBlock.addInst(new Mv(map, lRoot.getPhyReg(1), exitBlock));
        exitBlock.addInst(new IType(lRoot.getPhyReg(2), new SLImm(0), add,
                lRoot.getPhyReg(2), exitBlock));
        exitBlock.addInst(new Ret(lRoot, exitBlock));
        lFn.cnt = cnt;
    }
    public LRoot run() {
        irRoot.builtinFunctions().forEach((name, fn) -> {
            LFn func = new LFn(name, null, null);
            lRoot.addBuiltinFunction(func);
            fnMap.put(fn, func);
        });
        irRoot.functions().forEach((name, fn) -> {
            new LoopDetector(fn, false).runForFn();
            fn.blocks.forEach(block -> {
                LIRBlock lBlock = new LIRBlock(block.loopDepth, "." + fn.name + "_" + block.name);
                blockMap.put(block, lBlock);
            });
            //this is for reg alloc
            LFn lFn = new LFn(name, blockMap.get(fn.entryBlock), blockMap.get(fn.exitBlock));
            fnMap.put(fn, lFn);
            fn.params().forEach(para -> lFn.addPara(RegM2L(para)));
            lRoot.addFunction(lFn);
        });
        irRoot.functions().forEach((name, fn) -> runForFn(fn));
        return lRoot;
    }
}
