package BackEnd;

import Assemb.LFn;
import Assemb.LIRBlock;
import Assemb.LOperand.GReg;
import Assemb.LRoot;

public class AsmPrinter {

    private LRoot root;
    public AsmPrinter(LRoot root) {
        this.root = root;
    }

    private void runForBlock(LIRBlock block) {
        System.out.println(block.name + ": ");
        block.instructions().forEach(inst -> System.out.println("\t" + inst.toString()));
    }

    private void runForFn(LFn fn) {
        System.out.println("\t.globl\t" + fn.name());
        System.out.println("\t.p2align\t1");
        System.out.println("\t.type\t" + fn.name() +",@function");
        System.out.println(fn.name() + ":");
        fn.blocks().forEach(this::runForBlock);
        String endSection = "." + fn.name() + "_end";
        System.out.println(endSection + ":");
        System.out.println(".size\t" + fn.name() + ", " + endSection + "-" + fn.name());
    }

    private void runForGlb(GReg reg) {
        System.out.println("\t.type\t" + reg.name + ",@object");
        System.out.println("\t.section\t.bss");
        System.out.println("\t.globl\t" + reg.name);
        System.out.println("\t.p2align\t2");
        System.out.println(reg.name + ":");
        System.out.println(".L" + reg.name + "$local:");
        System.out.println("\t.word\t0");
        System.out.println("\t.size\t" + reg.name + ", 4");
    }

    private void runForString(GReg reg, String value) {
        System.out.println("\t.type\t" + reg.name + ",@object");
        System.out.println("\t.section\t.rodata");
        System.out.println(reg.name + ":");
        System.out.println("\t.asciz\t" + value);
        System.out.println("\t.size\t" + reg.name + ", " + value.length() + 1);
    }

    public void run() {
        root.functions().forEach(this::runForFn);
        root.globalRegs.forEach(this::runForGlb);
        root.strings.forEach(this::runForString);
    }
}
