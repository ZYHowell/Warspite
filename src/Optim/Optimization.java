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

    public void run() {
        boolean change;
        int round = 0;

        new FunctionInline(irRoot).run();
        do{
            change = new ADCE(irRoot).run();
            change = new SCCP(irRoot).run() || change;
            change = new CFGSimplification(irRoot, false).run() || change;
            change = new CSE(irRoot).run() || change;
            new instReplacement(irRoot).run();
            change = new StrengthReduction(irRoot).run() || change;

            AliasAnalysis alias = new AliasAnalysis(irRoot);
            alias.run();
            change = new MemCSE(irRoot, alias).run() || change;
            //if (round == 0)new IRPrinter(new PrintStream("debug.ll"), true).run(irRoot);
            change = new LICM(irRoot, alias).run() || change;

            ++round;
        }
        while (change);
        new CFGSimplification(irRoot, true).run();
    }
}
