package Optim;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.*;
import MIR.IRoperand.Register;
import MIR.Root;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/*
 * this is a naïve global CSE, based on the iDom of the block.
 * when there is an use(and not eliminated), visit all unvisited uses to check if there is a same use.
 * If there is a same and the iDom relation is correct, replace it.
 * This has to be done recursively, so maybe it is slow!!
 */

public class CSE extends Pass {

    private Root irRoot;
    private boolean change;

    public CSE(Root irRoot) {
        this.irRoot = irRoot;
    }

    static int bound = 20;

    private void tryReplace(IRBlock block, int cnt, ArrayList<Inst> instructions, IRBlock origin){
        for (Inst inst = block.headInst; inst != null; inst = inst.next) {
            ++cnt;
            if (cnt > bound) break;
            if (inst instanceof GetElementPtr || inst instanceof BitCast || inst instanceof Binary) {
                for (Inst instr : instructions) {
                    if (instr.sameMeaning(inst)) {
                        inst.dest().replaceAllUseWith(instr.dest());
                        inst.removeSelf(true);
                        break;
                    }
                }
            }
        }
        if (cnt < bound) {
            for (IRBlock suc : block.successors) {
                if (suc.isDomed(origin)) tryReplace(suc, cnt, instructions, block);
            }
        }
    }

    private void visit(IRBlock block) {
        boolean hasChange;
        do {
            hasChange = false;
            HashSet<Phi> retainedPhi = new HashSet<>();
            for (Iterator<Map.Entry<Register, Phi>> iter = block.PhiInst.entrySet().iterator(); iter.hasNext();) {
                Map.Entry<Register, Phi> entry = iter.next();
                Phi phi = entry.getValue();
                boolean replaced = false;
                for (Phi retPhi : retainedPhi) {
                    if (retPhi.sameMeaning(phi)) {
                        phi.dest().replaceAllUseWith(retPhi.dest());
                        replaced = true;
                        break;
                    }
                }
                if (!replaced) retainedPhi.add(phi);
                else {
                    iter.remove();
                    phi.removeSelf(false);
                    hasChange = true;
                }
            }
            ArrayList<Inst> instructions = new ArrayList<>();
            for (Inst inst = block.headInst; inst != null; inst = inst.next) {
                if (inst.noSideEffect()) {
                    boolean replaced = false;
                    for (Inst instr : instructions) {
                        if (instr.sameMeaning(inst)) {
                            replaced = true;
                            inst.dest().replaceAllUseWith(instr.dest());
                            inst.removeSelf(true);
                        }
                    }
                    if (!replaced) instructions.add(inst);
                    else hasChange = true;
                }
            }

            block.successors.forEach(suc -> {
                if (suc.isDomed(block)) tryReplace(suc, instructions.size(), instructions, block);
            });
            change = change || hasChange;
        } while(hasChange);
    }

    private void runForFn(Function fn) {
        fn.blocks.forEach(this::visit);
    }

    @Override
    public boolean run() {
        change = false;
        irRoot.functions().forEach((name, fn) -> runForFn(fn));
        return change;
    }
}
