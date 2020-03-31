package Optim;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.Binary;
import MIR.IRinst.Inst;
import MIR.IRoperand.ConstInt;
import MIR.IRoperand.Register;
import MIR.Root;
import Util.MIRLoop;

import java.util.HashSet;

public class StrengthReduction extends Pass{

    private boolean change;
    private Root irRoot;

    public StrengthReduction(Root irRoot) {
        super();
        this.irRoot = irRoot;
    }

    private void runForLoop(MIRLoop loop) {
        HashSet<Register> indVar = new HashSet<>();
        IRBlock head = loop.preHead().successors().get(0);
        head.phiInst().forEach((reg, phi) -> {
            if (phi.blocks().size() == 2) {
                int inner = -1;
                if (phi.blocks().get(0) == loop.preHead()) inner = 1;
                else if (phi.blocks().get(1) == loop.preHead()) inner = 0;

                if (inner > -1 && phi.values().get(inner) instanceof Register) {
                    Inst innerDef = ((Register) phi.values().get(inner)).def();
                    if (innerDef instanceof Binary) {
                        int as = -1;
                        if (((Binary) innerDef).opCode() == Binary.BinaryOpCategory.add) as = 1;
                        else if (((Binary) innerDef).opCode() == Binary.BinaryOpCategory.sub) as = 0;
                        if (as > -1){
                            if ((((Binary) innerDef).src1() == phi.dest() &&
                                    ((Binary) innerDef).src2() instanceof ConstInt)) {
                                indVar.add(phi.dest());
                            }
                            else if (((Binary) innerDef).src2() == phi.dest() &&
                                        ((Binary) innerDef).src2() instanceof ConstInt) {
                                indVar.add(phi.dest());
                            }
                        }
                    }
                }
            }
        });

        loop.blocks().forEach(block -> block.instructions().forEach(inst -> {
            //strength reduction
        }));
    }

    private void runForFn(Function fn) {
        LoopDetector loops = new LoopDetector(fn);
        loops.runForFn();
        loops.rootLoops().forEach(this::runForLoop);
    }

    @Override
    public boolean run() {
        change = true;
        irRoot.functions().forEach((name, fn) -> runForFn(fn));
        return change;
    }
}
