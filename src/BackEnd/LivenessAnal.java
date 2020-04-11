package BackEnd;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.Inst;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.Root;

import java.util.HashMap;
import java.util.HashSet;

//this is done after phi resolution
public class LivenessAnal {
    private Root irRoot;
    private HashMap<IRBlock, HashSet<Operand>> blockUses = new HashMap<>();
    private HashMap<IRBlock, HashSet<Register>> blockDefs = new HashMap<>();
    private HashMap<IRBlock, HashSet<Operand>> blockLiveIn = new HashMap<>(),
                                               blockLiveOut = new HashMap<>();
    private HashSet<IRBlock> visited = new HashSet<>();

    public LivenessAnal(Root irRoot) {
        this.irRoot = irRoot;
    }

    public void runForBlockA(IRBlock block) {
        HashSet<Operand> uses = new HashSet<>();
        HashSet<Register> defs = new HashSet<>();
        block.phiInst().forEach((reg, phi) -> {
            uses.addAll(phi.uses());
            defs.add(reg);
        });
        block.instructions().forEach(inst -> {
            if (inst.dest() != null) defs.add(inst.dest());
            uses.addAll(inst.uses());
        });
        blockUses.put(block, uses);
        blockDefs.put(block, defs);
        blockLiveIn.put(block, new HashSet<>());
        blockLiveOut.put(block, new HashSet<>());
    }
    public void LiveIO(IRBlock block) {
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
    public void runForBlockB(IRBlock block) {
        HashSet<Operand> currentLive = new HashSet<>(blockLiveOut.get(block));
        int size = block.instructions().size();
        for (int i = size - 1;i >= 0;--i) {
            Inst inst = block.instructions().get(i);
            inst.liveOut.addAll(currentLive);
            currentLive.addAll(inst.uses());
            if (inst.dest() != null) currentLive.remove(inst.dest());
        }
        //no phi inst here
    }

    public void runForFn(Function fn) {
        //run the first round in each block: collect def and use in each block
        fn.blocks().forEach(this::runForBlockA);
        //run to get the live-in and live-out of each block
        LiveIO(fn.exitBlock());
        //run the second round in each block
        fn.blocks().forEach(this::runForBlockB);
    }

    public void run() {
        irRoot.functions().forEach((name, fn) -> runForFn(fn));
    }
}
