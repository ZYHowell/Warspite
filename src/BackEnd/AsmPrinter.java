package BackEnd;

import Assemb.LFn;
import Assemb.LIRBlock;
import Assemb.LOperand.GReg;
import Assemb.LRoot;
import MIR.Function;
import MIR.IRBlock;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class AsmPrinter {

    private LRoot root;
    int funcCnt = 0;
    public AsmPrinter(LRoot root) {
        this.root = root;
    }

    private ArrayList<LIRBlock> visitList = new ArrayList<>();
    private void collectWithRename(LFn fn) {
        int blockCnt = 0;
        Queue<LIRBlock> blocks = new LinkedList<>();
        blocks.add(fn.entryBlock());
        visitList.add(fn.entryBlock());
        while (!blocks.isEmpty()) {
            LIRBlock check = blocks.poll();
            check.name ="." + fn.name() + "_b." + blockCnt++;
            check.successors().forEach(block -> {
                if (block != null && !visitList.contains(block)) {
                    blocks.add(block);
                    visitList.add(block);
                }
            });
        }
    }

    private void runForBlock(LIRBlock block) {
        System.out.println(block.name + ": ");
        block.instructions().forEach(inst -> System.out.println("\t" + inst.toString()));
    }

    private void runForFn(LFn fn) {
        collectWithRename(fn);
        System.out.println("\t.globl\t" + fn.name());
        System.out.println("\t.p2align\t1");
        System.out.println("\t.type\t" + fn.name() +",@function");
        System.out.println(fn.name() + ":");
        fn.blocks().forEach(this::runForBlock);
        System.out.println("\t.size\t" + fn.name() + ", " + ".-" + fn.name() + "\n");
    }

    private void runForGlb(GReg reg) {
        System.out.println("\t.type\t" + reg.name + ",@object");
        System.out.println("\t.section\t.bss");
        System.out.println("\t.globl\t" + reg.name);
        System.out.println("\t.p2align\t2");
        System.out.println(reg.name + ":");
        System.out.println(".L" + reg.name + "$local:");
        System.out.println("\t.word\t0");
        System.out.println("\t.size\t" + reg.name + ", 4\n");
    }

    private void runForString(GReg reg, String value) {
        System.out.println("\t.type\t" + reg.name + ",@object");
        System.out.println("\t.section\t.rodata");
        System.out.println(reg.name + ":");
        System.out.println("\t.asciz\t\"" + value + "\"");
        System.out.println("\t.size\t" + reg.name + ", " + (value.length() + 1) + "\n");
    }

    public void run() {
        System.out.println("\t.text");
        root.functions().forEach(this::runForFn);
        root.globalRegs.forEach(this::runForGlb);
        root.strings.forEach(this::runForString);
    }
}
