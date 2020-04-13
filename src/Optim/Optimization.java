package Optim;

import MIR.Root;

import java.util.ArrayList;

public class Optimization {
    private Root irRoot;

    public Optimization(Root irRoot) {
        this.irRoot = irRoot;
    }

    public void run() {
        boolean change;

        new FunctionInline(irRoot).run();

        do{
            change = new ADCE(irRoot).run();
            change = new SCCP(irRoot).run() || change;
            change = new CFGSimplification(irRoot).run() || change;
            change = new CSE(irRoot).run() || change;
        }
        while (change);
    }
}
