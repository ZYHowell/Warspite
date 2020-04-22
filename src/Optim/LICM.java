package Optim;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.*;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.Root;
import Util.MIRLoop;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

//mention that it is hard to add/remove blocks in MIRLoop,
//so do this before/after MIRLoop is created/used up
public class LICM extends Pass{

    private Root irRoot;
    private boolean change;

    public LICM(Root irRoot) {
        super();
        this.irRoot = irRoot;
    }

    private void tryHoist(Inst inst, HashSet<Register> defInLoop, Queue<Inst> canHoist) {
        if (inst.canHoist()) {
            HashSet<Operand> uses = inst.uses();
            uses.retainAll(defInLoop);
            if (uses.isEmpty()) {
                defInLoop.remove(inst.dest());
                canHoist.add(inst);
            }
        }
    }

    private void runForLoop(MIRLoop loop) {
        if (!loop.children().isEmpty())
            loop.children().forEach(this::runForLoop);

        HashSet<Register> defInLoop = new HashSet<>();
        Queue<Inst> canHoist = new LinkedList<>();

        loop.blocks().forEach(block -> {
            block.phiInst().forEach((reg, phi) -> defInLoop.add(phi.dest()));
            block.instructions().forEach(inst -> {
                if (inst.dest() != null) defInLoop.add(inst.dest());
            });
        });
        //collect all register defined in the loop
        IRBlock pre = loop.preHead();
        loop.blocks().forEach(block -> block.instructions().forEach(inst ->
                tryHoist(inst, defInLoop, canHoist)));
        change = change || !canHoist.isEmpty();
        //hoist and get more. the queue protects the correctness of the order
        while (!canHoist.isEmpty()) {
            Inst inst = canHoist.poll();
            pre.addInstTerminated(inst);
            inst.dest().uses().forEach(useInst -> {
                if (defInLoop.contains(useInst.dest())) tryHoist(useInst, defInLoop, canHoist);
            });
        }
    }
    private void runForFn(Function fn) {
        LoopDetector loops = new LoopDetector(fn, true);
        loops.runForFn();
        loops.rootLoops().forEach(this::runForLoop);
        loops.mergePreHeads();
        //merge unnecessary preHead, is safe here since the loops are used up
    }

    @Override
    public boolean run() {
        change = false;
        irRoot.functions().forEach((name, fn) -> runForFn(fn));
        return change;
    }
}
