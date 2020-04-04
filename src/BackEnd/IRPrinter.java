package BackEnd;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRoperand.Param;
import MIR.Root;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class IRPrinter {

    private int symbolCnt, blockCnt;
    private boolean bNC;
    private ArrayList<IRBlock> visitList = new ArrayList<>();

    public IRPrinter(boolean bNC) {
        this.bNC = bNC;
    }

    private void printBlock(IRBlock block) {
        System.out.println("%" + block.name() + ":");
        block.phiInst().forEach((reg, phi) -> {
            reg.setName("" + symbolCnt++);
            System.out.println("\t" + phi.toString());
        });
        block.instructions().forEach(inst -> {
            if (inst.dest() != null) inst.dest().setName("" + symbolCnt++);
            System.out.println("\t" + inst.toString());
        });
    }

    private void collectWithRename(Function fn) {
        Queue<IRBlock> blocks = new LinkedList<>();
        blocks.add(fn.entryBlock());
        visitList.add(fn.entryBlock());
        while (!blocks.isEmpty()) {
            IRBlock check = blocks.poll();
            check.setName("b." + blockCnt++);
            check.successors().forEach(block -> {
                if (block != null && !visitList.contains(block)) {
                    blocks.add(block);
                    visitList.add(block);
                    if (bNC) fn.blocks().add(block);
                }
            });
        }
    }
    public void printFn(String name, Function fn) {
        symbolCnt = blockCnt = 0;
        System.out.print("define " + fn.retType().toString() + " @" + fn.name() + "(");
        int size = fn.params().size();
        for (int i = 0;i < size;++i) {
            Param param = fn.params().get(i);
            param.setName("" + symbolCnt++);
            System.out.print(param.type().toString() + " " + param.toString() +
                    (i == size - 1 ? ")\n" : ", "));
        }
        if (size == 0) System.out.println(")");
        visitList.clear();

        collectWithRename(fn);

        visitList.forEach(this::printBlock);
    }

    public void run(Root irRoot) {
        irRoot.types().forEach((name, type) -> {
            System.out.print("%struct." + name + " = " + "type {");
            int size = type.members().size();
            for (int i = 0; i < size;++i) {
                System.out.print(type.members().get(i).toString() + (i == size - 1 ? "}\n" : ", "));
            }
        });
        irRoot.globalVar().forEach(gVar ->
            System.out.println("@" + gVar.name() + " = global " + gVar.type().toString() +
                    "zeroinitializer, align " + gVar.type().size() / 8)
        );
        irRoot.constStrings().forEach((name, constString) -> System.out.println(
                "@" + name + " = private unnamed_addr constant "
                + "[" + (constString.value().length() + 1) + " * i8] c" + "\"" + constString.value() + "\\00\", align 1"));
        irRoot.functions().forEach(this::printFn);
    }
}
