package Optim;

import MIR.Function;
import MIR.Root;

public class CFGSimplification extends Pass {

    private Root irRoot;
    private boolean change;

    public CFGSimplification(Root irRoot) {
        super();
    }

    private void removeBB(Function fn) {

    }

    private void simplify(Function fn) {
        removeBB(fn);
    }

    @Override
    public boolean run() {
        change = false;
        irRoot.functions().forEach((name, fn) -> simplify(fn));
        return change;
    }
}
