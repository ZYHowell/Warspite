package Optim;

import MIR.Function;
import MIR.IRinst.Call;
import MIR.Root;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FunctionInline extends Pass{

    private boolean change = false;
    private Root irRoot;
    private HashMap<Function, HashSet<Function>> callInfo = new HashMap<>();
    private HashSet<Function> cannotInlineFun = new HashSet<>();

    public FunctionInline(Root irRoot) {
        super();
        this.irRoot = irRoot;
    }

    private ArrayList<Function> DFSStack = new ArrayList<>();
    private HashSet<Function> visited = new HashSet<>();
    private void DFS(Function it) {
        visited.add(it);
        DFSStack.add(it);
        boolean inRing = false;
        for (Function fn : DFSStack) {
            if (it.isCallee(fn)) inRing = true;
            if (inRing) cannotInlineFun.add(fn);
        }
        it.callFunction().forEach(callee -> {
            if (!visited.contains(callee)) DFS(callee);
        });
        DFSStack.remove(DFSStack.size() - 1);
    }
    private void inlineJudge() {
        irRoot.functions().forEach((name, func) -> {
            if (!visited.contains(func)) DFS(func);
        });
    }

    private void checkInline(Function fn) {
        fn.blocks().forEach(block -> block.instructions().forEach(inst -> {
            if (inst instanceof Call && !cannotInlineFun.contains(((Call)inst).callee())) {
                //todo: unfold this instruction
            }
        }));
    }
    private void inlining() {
        irRoot.functions().forEach((name, func) -> checkInline(func));
    }

    @Override
    public boolean run() {
        change = false;
        inlineJudge();
        inlining();
        return change;
    }
}
