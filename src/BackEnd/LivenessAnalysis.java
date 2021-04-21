package BackEnd;

import Assemb.LFn;
import Assemb.LIRBlock;
import Assemb.LOperand.Reg;
import Assemb.RISCInst.RISCInst;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;

public class LivenessAnalysis {
    private LFn fn;
    private HashMap<LIRBlock, HashSet<Reg>> blockUses = new LinkedHashMap<>(),
                                            blockDefs = new LinkedHashMap<>();
    private HashSet<LIRBlock> visited = new LinkedHashSet<>();
    private Queue<LIRBlock> handleQueue = new LinkedList<>();

    public LivenessAnalysis(LFn fn) {
        this.fn = fn;
    }

    public void runForBlock(LIRBlock block) {
        HashSet<Reg> uses = new LinkedHashSet<>();
        HashSet<Reg> defs = new LinkedHashSet<>();
        for (RISCInst inst = block.head; inst != null; inst = inst.next) {
            HashSet<Reg> curUse = inst.uses();
            curUse.removeAll(defs);
            uses.addAll(curUse);
            defs.addAll(inst.defs());
        }
        blockUses.put(block, uses);
        blockDefs.put(block, defs);
        block.liveIn.clear();
        block.liveOut.clear();
    }
    public void LiveIO(LIRBlock block) {
        visited.add(block);
        HashSet<Reg> liveOut = new LinkedHashSet<>();
        block.successors.forEach(suc -> liveOut.addAll(suc.liveIn));
        HashSet<Reg> liveIn = new LinkedHashSet<>(liveOut);
        liveIn.removeAll(blockDefs.get(block));
        liveIn.addAll(blockUses.get(block));
        block.liveOut.addAll(liveOut);
        liveIn.removeAll(block.liveIn);
        if (!liveIn.isEmpty()) {
            block.liveIn.addAll(liveIn);
            visited.removeAll(block.precursors);
        }
        block.precursors.forEach(pre -> {
            if (!visited.contains(pre)) {
                handleQueue.offer(pre);
                visited.add(pre);
            }
        });
    }

    public void runForFn() {
        //run the first round in each block: collect def and use in each block
        fn.blocks().forEach(this::runForBlock);
        //run to get the live-in and live-out of each block
        handleQueue.offer(fn.exitBlock());
        visited.add(fn.exitBlock());
        while(!handleQueue.isEmpty()){
            LiveIO(handleQueue.poll());
        }
    }

}
