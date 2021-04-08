package Optim;

import MIR.Root;

public class Optimization {
    private Root irRoot;

    public Optimization(Root irRoot) {
        this.irRoot = irRoot;
    }

    private boolean LCSAI(){
        boolean change;
        boolean has_change = false;
        do{
            change = new ADCE(irRoot).run();
            change = new SCCP(irRoot).run() || change;
            change = new CFGSimplification(irRoot, false).run() || change;
            change = new CSE(irRoot).run() || change;
            new instReplacement(irRoot).run();
            new algSimplification(irRoot).run();
            change = new StrengthReduction(irRoot).run() || change;

            AliasAnalysis alias = new AliasAnalysis(irRoot);
            alias.run();
            change = new MemCSE(irRoot, alias).run() || change;
            change = new LICM(irRoot, alias).run() || change;
            has_change = has_change || change;
        }
        while (change);
        return has_change;
    }
    public void run() {
        boolean another_inline;
        do {
            FunctionInline inl = new FunctionInline(irRoot, false);
            another_inline = inl.run();
            another_inline = LCSAI() && another_inline && inl.recheck_inline_fails;
        } while(another_inline);
        new FunctionInline(irRoot, true).run();
        LCSAI();
        new CFGSimplification(irRoot, true).run();
    }
}
