package Optim;

import BackEnd.IRPrinter;
import MIR.Root;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

public class Optimization {
    private Root irRoot;

    public Optimization(Root irRoot) {
        this.irRoot = irRoot;
    }

    public void run() throws FileNotFoundException {
        boolean change;

        new FunctionInline(irRoot).run();
        do{
            change = new ADCE(irRoot).run();
            change = new SCCP(irRoot).run() || change;
            change = new CFGSimplification(irRoot).run() || change;
            change = new CSE(irRoot).run() || change;
            new IRPrinter(new PrintStream("debug.ll"), true).run(irRoot);
//            change = new LICM(irRoot).run() || change;    this will be checked later after alias
            change = new StrengthReduction(irRoot).run() || change;
        }
        while (change);
    }
}
