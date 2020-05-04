//DROPPED: can make the spill costly
/*
 * this is a naïve global CSE, based on the iDom of the block.
 * when there is an use(and not eliminated), visit all unvisited uses to check if there is a same use.
 * If there is a same and the iDom relation is correct, replace it.
 * This has to be done recursively, so maybe it is slow!!
 */

public class CSE extends Pass {

    private Root irRoot;
    private boolean change, fnNewChange;
    private HashSet<IRBlock> visitedBlock = new HashSet<>();
    private HashSet<Inst> visitedInst = new HashSet<>();

    public CSE(Root irRoot) {
        this.irRoot = irRoot;
    }

    private boolean canReplace(Inst a, Inst b) {
        //can replace a by b
        if (a.block() == b.block()) {
            for (Inst inst = a.block().headInst; inst != null; inst = inst.next) {
                if (inst == a) return false;
                else if (inst == b) return true;
            }
        }
        return false;
    }
    private void tryAllUseOf(Register reg) {
        ArrayList<Map.Entry<Inst, Register>> removeInst = new ArrayList<>();
        //should be arrayList!!
        reg.uses().forEach(use -> {
            if (!visitedInst.contains(use)) {
                IRBlock useBlock = use.block();
                reg.uses().forEach(inst -> {
                    if (!visitedInst.contains(inst) && inst != use &&
                            //no need to judge such inst
                            (inst.block().isDomed(useBlock) || canReplace(inst, use)) &&
                            inst.sameMeaning(use)) {
                        removeInst.add(new AbstractMap.SimpleEntry<>(inst, use.dest()));
                        visitedInst.add(inst);
                    }
                });

            }
        });
        visitedInst.addAll(reg.uses());

        fnNewChange = fnNewChange || removeInst.isEmpty();

        for (Map.Entry<Inst, Register> entry : removeInst) {
            Inst inst = entry.getKey();
            Register value = entry.getValue();
            inst.dest().replaceAllUseWith(value);
            inst.removeSelf(true);
        }
        //notice that, replace a by b, then b by c is correct;
        //arrayList to make it kept in order
        //and replace b by c, then a by b is impossible to happen; it will be replace a by c rather than b;
    }

    private void visit(IRBlock block) {
        assert !visitedBlock.contains(block);
        visitedBlock.add(block);

        //to consider: fix "modify in visit"
        block.phiInst().forEach((reg, phi) ->tryAllUseOf(phi.dest()));
        for (Inst inst = block.headInst; inst != null; inst = inst.next) {
            if (inst.dest() != null) tryAllUseOf(inst.dest());
        }

        //Deep first in dom tree(seems reasonable, at least better than successor)
        block.domChildren().forEach(this::visit);
    }

    private void runForFn(Function fn) {
        boolean fnChange = false;
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
