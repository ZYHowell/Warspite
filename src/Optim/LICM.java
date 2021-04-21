package Optim;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.Inst;
import MIR.IRinst.Load;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.Root;
import Util.MIRLoop;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;

//mention that it is hard to add/remove blocks in MIRLoop,
//so do this before/after MIRLoop is created/used up
public class LICM extends Pass{

    private Root irRoot;
    private boolean change;
    private AliasAnalysis alias;

    public LICM(Root irRoot, AliasAnalysis alias) {
        super();
        this.irRoot = irRoot;
        this.alias = alias;
    }

    private void tryHoist(Inst inst, HashSet<Register> defInLoop, Queue<Inst> canHoist) {
        if (inst instanceof Load) {
            HashSet<Operand> uses = inst.uses();
            uses.retainAll(defInLoop);
            if (uses.isEmpty() && !alias.storeInLoop((Load)inst)) {
                defInLoop.remove(inst.dest());
                canHoist.add(inst);
            }
        } else if (inst.noSideEffect()) {
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

        HashSet<Register> defInLoop = new LinkedHashSet<>();
        Queue<Inst> canHoist = new LinkedList<>();

        loop.blocks().forEach(block -> {
            block.PhiInst.forEach((reg, phi) -> defInLoop.add(reg));
            for(Inst inst = block.headInst; inst != null; inst = inst.next)
                if (inst.dest() != null) defInLoop.add(inst.dest());
        });
        alias.buildStoreInLoop(loop);
        //collect all register defined in the loop
        IRBlock pre = loop.preHead();
        loop.blocks().forEach(block -> {
            for(Inst inst = block.headInst; inst != null; inst = inst.next)
                tryHoist(inst, defInLoop, canHoist);
        });
        change = change || !canHoist.isEmpty();
        //hoist and get more. the queue protects the correctness of the order
        while (!canHoist.isEmpty()) {
            Inst inst = canHoist.poll();
            inst.removeInList();
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
        //merge unnecessary preHead, is safe here since the loops are used up
    }

    @Override
    public boolean run() {
        change = false;
        irRoot.functions().forEach((name, fn) -> runForFn(fn));
        return change;
    }
}
