package Optim;

import MIR.Root;

public class StrengthReduction extends Pass{

    private Root irRoot;

    public StrengthReduction(Root irRoot) {
        super();
        this.irRoot = irRoot;
    }

    @Override
    public boolean run() {
        return false;
    }
}
