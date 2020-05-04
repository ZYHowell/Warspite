package Optim;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.Inst;
import MIR.IRinst.Phi;
import MIR.IRoperand.Register;
import MIR.Root;

import java.util.*;

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

    private void visit(IRBlock block) {
        boolean hasChange;
        do {
            hasChange = false;
            HashSet<Phi> retainedPhi = new HashSet<>();
            for (Iterator<Map.Entry<Register, Phi>> iter = block.phiInst().entrySet().iterator(); iter.hasNext();) {
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
            change = change || hasChange;
        } while(hasChange);
    }

    private void runForFn(Function fn) {
        fn.blocks().forEach(this::visit);
    }

    @Override
    public boolean run() {
        change = false;
        irRoot.functions().forEach((name, fn) -> runForFn(fn));
        return change;
    }
}
