package BackEnd;

import Assemb.LFn;
import Assemb.LIRBlock;
import Assemb.LRoot;
import Assemb.RISCInst.RISCInst;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;

import java.util.HashMap;
import java.util.HashSet;

//this is done after phi resolution
public class LivenessAnal {
    private LRoot irRoot;
    private HashMap<LIRBlock, HashSet<Operand>> blockUses = new HashMap<>();
    private HashMap<LIRBlock, HashSet<Register>> blockDefs = new HashMap<>();
    private HashMap<LIRBlock, HashSet<Operand>> blockLiveIn = new HashMap<>(),
                                               blockLiveOut = new HashMap<>();
    private HashSet<LIRBlock> visited = new HashSet<>();

    public LivenessAnal(LRoot irRoot) {
        this.irRoot = irRoot;
    }

    public void runForBlockA(LIRBlock block) {
        HashSet<Operand> uses = new HashSet<>();
        HashSet<Register> defs = new HashSet<>();
        block.instructions().forEach(inst -> {
            if (inst.dest() != null) defs.add(inst.dest());
            uses.addAll(inst.uses());
        });
        blockUses.put(block, uses);
        blockDefs.put(block, defs);
        blockLiveIn.put(block, new HashSet<>());
        blockLiveOut.put(block, new HashSet<>());
    }
    public void LiveIO(LIRBlock block) {
        visited.add(block);
        HashSet<Operand> liveOut = new HashSet<>();
        block.successors().forEach(suc -> liveOut.addAll(blockLiveOut.get(suc)));
        HashSet<Operand> liveIn = new HashSet<>(liveOut);
        liveIn.addAll(blockUses.get(block));
        liveIn.removeAll(blockDefs.get(block));
        blockLiveOut.get(block).addAll(liveOut);
        liveIn.removeAll(blockLiveIn.get(block));
        if (!liveIn.isEmpty()) {
            blockLiveIn.get(block).addAll(liveIn);
            visited.removeAll(block.precursors());
        }
        block.precursors().forEach(pre -> {
            if (!visited.contains(pre)) LiveIO(pre);
        });
    }
    public void runForBlockB(LIRBlock block) {
        HashSet<Operand> currentLive = new HashSet<>(blockLiveOut.get(block));
        int size = block.instructions().size();
        for (int i = size - 1;i >= 0;--i) {
            RISCInst inst = block.instructions().get(i);
            inst.liveOut.addAll(currentLive);
            currentLive.addAll(inst.uses());
            if (inst.dest() != null) currentLive.remove(inst.dest());
        }
        //no phi inst here
    }

    public void runForFn(LFn fn) {
        //run the first round in each block: collect def and use in each block
        fn.blocks().forEach(this::runForBlockA);
        //run to get the live-in and live-out of each block
        LiveIO(fn.exitBlock());
        //run the second round in each block
        fn.blocks().forEach(this::runForBlockB);
    }

    public void run() {
        irRoot.functions().forEach(this::runForFn);
    }
}
