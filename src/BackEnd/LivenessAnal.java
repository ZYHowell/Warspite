package BackEnd;

import Assemb.DAG;
import Assemb.LFn;
import Assemb.LIRBlock;
import Assemb.LOperand.Reg;
import Assemb.LRoot;
import Assemb.RISCInst.Cal;
import Assemb.RISCInst.Mv;
import Assemb.RISCInst.RISCInst;

import java.util.HashMap;
import java.util.HashSet;

//this is done after phi resolution
public class LivenessAnal {
    private LFn fn;
    private LRoot irRoot;
    private HashMap<LIRBlock, HashSet<Reg>> blockUses = new HashMap<>(),
                                            blockDefs = new HashMap<>();
    private HashMap<LIRBlock, HashSet<Reg>> blockLiveIn = new HashMap<>(),
                                            blockLiveOut = new HashMap<>();
    private HashSet<LIRBlock> visited = new HashSet<>();
    private DAG dag;
    private HashSet<Mv> workInstMv;

    public LivenessAnal(LRoot irRoot, LFn fn) {
        this.irRoot = irRoot;
        this.fn = fn;
    }

    public void init(LIRBlock block) {
        block.instructions().forEach(inst -> {
            if (inst.dest() != null) {
                inst.dest().moveList.clear();
                --inst.dest().degree;
            }
        });
    }
    public void runForBlockA(LIRBlock block) {
        HashSet<Reg> uses = new HashSet<>();
        HashSet<Reg> defs = new HashSet<>();
        block.instructions().forEach(inst -> {
            inst.uses().forEach(use -> {
                if (!defs.contains(use)) uses.add(use);
            });
            if (inst.dest() != null) defs.add(inst.dest());
        });
        blockUses.put(block, uses);
        blockDefs.put(block, defs);
        blockLiveIn.put(block, new HashSet<>());
        blockLiveOut.put(block, new HashSet<>());
    }
    public void LiveIO(LIRBlock block) {
        visited.add(block);
        HashSet<Reg> liveOut = new HashSet<>();
        block.successors.forEach(suc -> liveOut.addAll(blockLiveIn.get(suc)));
        HashSet<Reg> liveIn = new HashSet<>(liveOut);
        liveIn.removeAll(blockDefs.get(block));
        liveIn.addAll(blockUses.get(block));
        blockLiveOut.get(block).addAll(liveOut);
        liveIn.removeAll(blockLiveIn.get(block));
        if (!liveIn.isEmpty()) {
            blockLiveIn.get(block).addAll(liveIn);
            visited.removeAll(block.precursors);
        }
        block.precursors.forEach(pre -> {
            if (!visited.contains(pre)) LiveIO(pre);
        });
    }
    public void runForBlockB(LIRBlock block) {
        HashSet<Reg> currentLive = new HashSet<>(blockLiveOut.get(block));
        int size = block.instructions().size();
        for (int i = size - 1;i >= 0;--i) {
            RISCInst inst = block.instructions().get(i);
            if (inst instanceof Mv) {
                currentLive.removeAll(inst.uses());
                HashSet<Reg> mvAbout = inst.uses();
                mvAbout.add(inst.dest());   //def of move is only the inst.dest()
                mvAbout.forEach(reg -> reg.moveList.add((Mv) inst));
                workInstMv.add((Mv) inst);
            }
            HashSet<Reg> defs = new HashSet<>();
            if (inst.dest() != null) defs.add(inst.dest());
            if (inst instanceof Cal) {
                defs.addAll(irRoot.callerSave());
                defs.add(irRoot.getPhyReg(6));
            }
            currentLive.addAll(defs);
            defs.forEach(def -> currentLive.forEach(reg -> dag.addEdge(reg, def)));
            
            currentLive.removeAll(defs);
            currentLive.addAll(inst.uses());
        }
        //no phi inst here
    }

    public void runForFn() {
        dag = fn.dag();
        dag.init();
        workInstMv = fn.workListMv();
        workInstMv.clear();
        fn.blocks().forEach(this::init);
        //run the first round in each block: collect def and use in each block
        fn.blocks().forEach(this::runForBlockA);
        //run to get the live-in and live-out of each block
        LiveIO(fn.exitBlock());
        //run the second round in each block
        fn.blocks().forEach(this::runForBlockB);
    }

}
