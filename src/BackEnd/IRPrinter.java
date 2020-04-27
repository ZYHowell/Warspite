package BackEnd;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRoperand.Param;
import MIR.IRoperand.Register;
import MIR.IRtype.Pointer;
import MIR.Root;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class IRPrinter {

    private int symbolCnt, blockCnt;
    private boolean bNC;
    private ArrayList<IRBlock> visitList = new ArrayList<>();
    private PrintStream out;

    public IRPrinter(boolean bNC, PrintStream out) {
        this.bNC = bNC;
        this.out = out;
    }

    private void renameBlock(IRBlock block) {
        block.phiInst().forEach((reg, phi) -> reg.setName("" + symbolCnt++));
        block.instructions().forEach(inst -> {
            if (inst.dest() != null) inst.dest().setName("" + symbolCnt++);
        });
    }
    private void printBlock(IRBlock block) {
        out.println(block.name() + ":");
        block.phiInst().forEach((reg, phi) -> out.println("\t" + phi.toString()));
        block.instructions().forEach(inst -> out.println("\t" + inst.toString()));
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
    private String fnHead(Function fn, boolean isBuiltIn) {
        StringBuilder ret = new StringBuilder(isBuiltIn ? "declare " : "define ");
        ret.append(fn.retType()).append(" @").append(fn.name()).append("(");
        int size = fn.params().size();
        for (int i = 0;i < size;++i) {
            Param param = fn.params().get(i);
            param.setName("" + symbolCnt++);
            ret.append(param.type().toString()).append(" ")
                    .append(param.toString()).append(i == size - 1 ? ")" : ", ");
        }
        if (size == 0) ret.append(")");
        if (!isBuiltIn) ret.append("{");
        return ret.toString();
    }
    private void printFn(String name, Function fn) {
        symbolCnt = blockCnt = 0;
        out.println(fnHead(fn, false));
        visitList.clear();

        collectWithRename(fn);

        visitList.forEach(this::renameBlock);
        visitList.forEach(this::printBlock);
        out.println("}");
    }

    public void run(Root irRoot) {
        irRoot.builtinFunctions().forEach((name, fn) -> {
            symbolCnt = 0;
            out.println(fnHead(fn, true));
        });
        irRoot.types().forEach((name, type) -> {
            out.print("%struct." + name + " = " + "type {");
            int size = type.members().size();
            for (int i = 0; i < size;++i) {
                out.print(type.members().get(i).toString() + (i == size - 1 ? "}\n" : ", "));
            }
        });
        irRoot.globalVar().forEach(gVar ->
            out.println("@" + gVar.name() + " = global " + ((Pointer)gVar.type()).pointTo().toString() +
                    " zeroinitializer, align " + gVar.type().size() / 8)
        );
        irRoot.constStrings().forEach((name, constString) -> out.println(
                "@" + name + " = private unnamed_addr constant "
                + "[" + constString.value().length() + " x i8] c" + "\"" + constString.irValue() + "\", align 1"));
        irRoot.functions().forEach(this::printFn);
    }
}
