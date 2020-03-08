package Optim;

import MIR.Root;

public class ConstFold extends Pass {

    private Root irRoot;

    public ConstFold(Root irRoot) {
        super();
        this.irRoot = irRoot;
    }

    @Override
    public boolean run() {
        return true;    //only need one cycle
    }
}
