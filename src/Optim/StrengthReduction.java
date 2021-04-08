package Optim;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.Binary;
import MIR.IRinst.Inst;
import MIR.IRinst.Phi;
import MIR.IRoperand.ConstInt;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.Root;
import Util.MIRInductVar;
import Util.MIRLoop;

import java.util.ArrayList;
import java.util.HashMap;

public class StrengthReduction extends Pass{

    private boolean change;
    private Root irRoot;

    public StrengthReduction(Root irRoot) {
        super();
        this.irRoot = irRoot;
    }

    private Register isInduct(Phi phi, IRBlock preHead) {
        int inner = -1;
        if (phi.blocks().size() > 2) return null;
        if (phi.blocks().get(0) == preHead) inner = 1;
        else if (phi.blocks().get(1) == preHead) inner = 0;
        //i = phi(i0, i1)
        if (inner == -1 || !((phi.values().get(inner) instanceof Register))) return null; //i1 is register
        Register reg = (Register)phi.values().get(inner);
        return reg.def() instanceof Binary ? reg : null; //def of i1 is binary, return i1
    }
    private int partlyConst(Binary inst) {
        if (inst.src1() instanceof ConstInt && inst.src2() instanceof Register) return 0;
        if (inst.src1() instanceof Register && inst.src2() instanceof ConstInt) return 1;
        return -1;
    }
    private void runForLoop(MIRLoop loop) {
        loop.children().forEach(this::runForLoop);
        HashMap<Register, MIRInductVar> indVar = new HashMap<>();
        IRBlock preHead = loop.preHead();
        IRBlock head = preHead.successors.get(0);
        head.PhiInst.forEach((reg, phi) -> {
            Register innerReg = isInduct(phi, preHead);
            if (innerReg != null){
                Binary innerDef = (Binary) innerReg.def();
                int constAt = partlyConst(innerDef);
                if (constAt > -1 && innerDef.uses().contains(phi.dest())) {
                    int as = -1;
                    if (innerDef.opCode() == Binary.BinaryOpCat.add) as = 1;
                    else if (innerDef.opCode() == Binary.BinaryOpCat.sub) as = 0;
                    if (as > -1){
                        MIRInductVar ind = new MIRInductVar(phi.dest(), innerDef.dest(), constAt);
                        indVar.put(phi.dest(), ind);
                        indVar.put(innerDef.dest(), ind);
                    }
                }
            }
        });

        boolean newChange;
        do{
            newChange = false;
            for (IRBlock block : loop.blocks()) {
                for (Inst inst = block.headInst; inst != null; inst = inst.next) {
                    if (inst instanceof Binary && ((Binary) inst).opCode() == Binary.BinaryOpCat.mul) {
                        Binary instr = (Binary) inst;
                        int constAt = partlyConst(instr);
                        if (constAt > -1) {
                            Operand op = constAt == 0 ? instr.src2() : instr.src1();
                            if (op instanceof Register && indVar.containsKey(op)) {
                                Register src = (Register) op;
                                newChange = true;
                                int mulValue = ((ConstInt)
                                        (constAt == 0 ? instr.src1() : instr.src2())).value();
                                MIRInductVar ind = indVar.get(src);
                                int addValue = ind.addValue() * mulValue;
                                Phi phiDef = (Phi)ind.phiReg().def();
                                Operand phiInit;
                                IRBlock innerTerminator;
                                if (phiDef.blocks().get(0) == preHead) {
                                    innerTerminator = phiDef.blocks().get(1);
                                    phiInit = phiDef.values().get(0);
                                }
                                else {
                                    innerTerminator = phiDef.blocks().get(0);
                                    phiInit = phiDef.values().get(1);
                                }
                                Register newPhiReg = new Register(instr.dest().type(), "strRedPhi");
                                Register newInitReg = new Register(newPhiReg.type(), "strRedInit");
                                if (src == ind.addReg())
                                    //init: if src is phi, the init is phi.outer * mulValue
                                    preHead.addInstTerminated(
                                        new Binary(phiInit, new ConstInt(mulValue, 32),
                                                newInitReg, Binary.BinaryOpCat.mul, preHead));
                                else {
                                    //else, the init is (phi.outer + ind.addValue()) * mulValue;
                                    assert src == ind.phiReg();
                                    Register tmpAdd = new Register(newInitReg.type(), "strRedInitAdd");
                                    if (ind.addValue() < 0)
                                        preHead.addInstTerminated(new Binary(phiInit, new ConstInt(-1 * ind.addValue(), 32),
                                                tmpAdd, Binary.BinaryOpCat.add, preHead));
                                    else preHead.addInstTerminated(new Binary(phiInit, new ConstInt(ind.addValue(), 32),
                                                tmpAdd, Binary.BinaryOpCat.sub, preHead));
                                    preHead.addInstTerminated(
                                        new Binary(tmpAdd, new ConstInt(mulValue, 32),
                                                newInitReg, Binary.BinaryOpCat.mul, preHead));
                                }
                                ArrayList<IRBlock> blocks = new ArrayList<>();
                                ArrayList<Operand> values = new ArrayList<>();
                                blocks.add(preHead);
                                values.add(newInitReg);

                                blocks.add(innerTerminator);
                                values.add(instr.dest());
                                head.addPhi(new Phi(newPhiReg, blocks, values, head));
                                Binary stepInst;
                                Register stepReg = new Register(instr.dest().type(), "");
                                if (addValue >= 0) stepInst = new Binary(newPhiReg, new ConstInt(addValue, 32), stepReg, Binary.BinaryOpCat.add, head);
                                else stepInst = new Binary(newPhiReg, new ConstInt(addValue * -1, 32), stepReg, Binary.BinaryOpCat.sub, head);
                                head.addInstTerminated(stepInst);
                                instr.dest().replaceAllUseWith(stepReg);
                                instr.removeSelf(true);

                                MIRInductVar newInd = new MIRInductVar(newPhiReg, instr.dest(), 1);
                                indVar.put(newPhiReg, newInd);
                                indVar.put(instr.dest(), newInd);
                            }
                        }
                    }
                }
            }
            change = change || newChange;
        }
        while(newChange);

    }

    private void runForFn(Function fn) {
        LoopDetector loops = new LoopDetector(fn, true);
        loops.runForFn();
        loops.rootLoops().forEach(this::runForLoop);
    }

    @Override
    public boolean run() {
        change = false;
        irRoot.functions().forEach((name, fn) -> runForFn(fn));
        return change;
    }
}
