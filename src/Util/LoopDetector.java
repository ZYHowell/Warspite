package Util;

import MIR.Function;
import MIR.IRBlock;

import java.util.*;

public class LoopDetector {

    private Function fn;
    private HashMap<IRBlock, MIRLoop> loopMap = new HashMap<>();
    private HashSet<MIRLoop> rootLoops = new HashSet<>();
    private HashSet<IRBlock> visited = new HashSet<>();
    private Stack<MIRLoop> loopStack = new Stack<>();

    public LoopDetector(Function fn) {
        this.fn = fn;
    }

    private void getWholeLoop(IRBlock tail, IRBlock head){
        HashSet<IRBlock> inLoopBlocks = new HashSet<>();
        Queue<IRBlock> workList = new LinkedList<>();
        inLoopBlocks.add(head);
        inLoopBlocks.add(tail);
        workList.offer(tail);

        while(!workList.isEmpty()) {
            IRBlock workBlock = workList.poll();
            workBlock.precursors().forEach(pre -> {
                if (!inLoopBlocks.contains(pre)) {
                    workList.offer(pre);
                    inLoopBlocks.add(pre);
                }
            });
        }

        if (!loopMap.containsKey(head)) loopMap.put(head, new MIRLoop(head));
        loopMap.get(head).addBlocks(inLoopBlocks);
    }

    private void judgeOutOfLoop(IRBlock block) {
        if (!loopStack.isEmpty())
            while (!loopStack.peek().blocks().contains(block))
                loopStack.pop();
    }
    private void visit(IRBlock block) {
        visited.add(block);
        judgeOutOfLoop(block);
        if (loopMap.containsKey(block)) {
            MIRLoop loop = loopMap.get(block);
            if (loopStack.isEmpty()) rootLoops.add(loop);
            else loopStack.peek().addChild(loop);
            loopStack.push(loop);
        }
        block.successors().forEach(suc -> {
           if (!visited.contains(suc)) visit(suc);
        });
    }
    private void treeSpanning() {
        visit(fn.entryBlock());
    }

    public void runForFn() {
        //assume that the dominator relation is correct.
        fn.blocks().forEach(block -> block.successors().forEach(suc -> {
            if (block.isDomed(suc)) getWholeLoop(block, suc);
        }));
        treeSpanning();
    }
}
