package Optim;

import MIR.Root;

public class ADCE extends Pass {

    Root irRoot;

    public ADCE(Root irRoot) {
        super();
        this.irRoot = irRoot;
    }

    @Override
    public boolean run() {
        return false;
    }
}
