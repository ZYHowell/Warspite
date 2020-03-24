package Optim;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.Inst;
import MIR.IRoperand.Register;
import MIR.Root;
import org.antlr.v4.runtime.misc.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/*
 * this is a naïve global CSE, based on the iDom of the block.
 * when there is an use(and not eliminated), visit all unvisited uses to check if there is a same use.
 * If there is a same and the iDom relation is correct, replace it.
 * This has to be done recursively, so maybe it is slow!!
 */

public class CSE extends Pass {

    private Root irRoot;
    private boolean change, fnChange, fnNewChange;
    private HashSet<IRBlock> visitedBlock = new HashSet<>();
    private HashSet<Inst> visitedInst = new HashSet<>();

    public CSE(Root irRoot) {
        this.irRoot = irRoot;
    }

    private void tryAllUseOf(Register reg) {
        HashMap<Inst, Register> removeInst = new HashMap<>();
        reg.uses().forEach(use -> {
            if (!visitedInst.contains(use)) {
                visitedInst.add(use);
                //make it visited to avoid being replaced

                IRBlock useBlock = use.block();
                reg.uses().forEach(inst -> {
                    if (!visitedInst.contains(inst) && //no need to judge this(only a small improvement)
                            inst.block().isDomed(useBlock) && inst.sameMeaning(use)) {
                        removeInst.put(inst, use.dest());
                        visitedInst.add(inst);
                    }
                });

            }
        });

        fnNewChange = fnNewChange || removeInst.isEmpty();

        for (Map.Entry<Inst, Register> entry : removeInst.entrySet()) {
            Inst inst = entry.getKey();
            Register value = entry.getValue();
            inst.dest().replaceAllUseWith(value);
            inst.removeSelf(true);
        }
    }

    private void visit(IRBlock block) {
        visitedBlock.add(block);

        block.phiInst().forEach((reg, phi) ->tryAllUseOf(phi.dest()));
        block.instructions().forEach(inst -> tryAllUseOf(inst.dest()));

        //Deep first in dom tree(seems reasonable, at least better than successor)
        block.domChildren().forEach(children -> {
            if (!visitedBlock.contains(children)) visit(children);
        });
    }

    private void runForFn(Function fn) {
        fnChange = false;
        do {
            fnNewChange = false;
            visitedBlock.clear();
            visitedInst.clear();
            visit(fn.entryBlock());
            fnChange = fnChange || fnNewChange;
        }
        while (fnNewChange);
        change = change || fnChange;
    }

    @Override
    public boolean run() {
        change = false;
        irRoot.functions().forEach((name, fn) -> runForFn(fn));
        return change;
    }
}
