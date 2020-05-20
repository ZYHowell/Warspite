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

    private void LCSAI() {
        boolean change;
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
            change = new LICM(irRoot, alias).run() || change;
        }
        while (change);
    }
    public void run() {
        new FunctionInline(irRoot, false).run();
        LCSAI();
        new FunctionInline(irRoot, true).run();
        //new IRPrinter(new PrintStream("debug.ll"), true).run(irRoot);
        LCSAI();
        new CFGSimplification(irRoot, true).run();
    }
}
