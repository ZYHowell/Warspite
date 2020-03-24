package Optim;

import MIR.Root;

public class LICM extends Pass{

    private Root irRoot;

    public LICM(Root irRoot) {
        super();
        this.irRoot = irRoot;
    }

    @Override
    public boolean run() {
        return false;
    }
}
