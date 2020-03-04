package Optim;

import MIR.IRinst.Inst;
import MIR.Root;

import java.util.HashSet;

public class ADCE extends Pass {

    private Root irRoot;
    private boolean change, MoreLive;
    private HashSet<Inst> liveCode = new HashSet<>();

    public ADCE(Root irRoot) {
        super();
        this.irRoot = irRoot;
    }

    public void collect() {
        while(MoreLive) {
            MoreLive = false;

        }
    }

    @Override
    public boolean run() {
        liveCode.clear();
        change = false;
        MoreLive = true;

        return false;
    }
}
