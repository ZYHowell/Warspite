package Optim;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.*;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.Root;
import Util.DomGen;

import java.util.*;

//to consider: what about using DJ-graph and a better algorithm in SSA book to do this? maybe quicker

public class Mem2Reg extends Pass{

    private Root irRoot;

    public Mem2Reg(Root irRoot) {
        super();
        this.irRoot = irRoot;
    }

    private void runForFn(Function fn) {
    //only a normal minimal SSA
        HashSet<Register> allocVars = fn.allocVars();

        HashSet<IRBlock> defBlocks = new HashSet<>();
        HashMap<IRBlock, HashSet<Load>> allocLoads = new HashMap<>();
        HashMap<IRBlock, HashMap<Register, Register>> allocPhiMap = new HashMap<>();
        HashMap<IRBlock, HashMap<Register, Operand>> allocStores = new HashMap<>();

        new DomGen(fn, false).runForFn();

        fn.blocks().forEach(block -> {
            allocLoads.put(block, new HashSet<>());
            allocStores.put(block, new HashMap<>());
            allocPhiMap.put(block, new HashMap<>());
        });

        //collect load/store info.
        for (IRBlock block : fn.blocks()) {
            for (Iterator<Inst> iter = block.instructions().iterator(); iter.hasNext(); ) {
                Inst inst = iter.next();
                if (inst instanceof Load) {
                    Operand address = ((Load) inst).address();
                    if (address instanceof Register && (allocVars.contains(address))) {
                        /*
                         * add live-in info, which may be used to make an early liveness analysis
                         * which may release the pressure on later liveness analysis
                         * and form pruned SSA rather than minimal SSA/
                         */
                        HashMap<Register, Operand> blockLiveOut = allocStores.get(inst.block());
                        if (blockLiveOut.containsKey(address)) {
                            inst.dest().replaceAllUseWith(blockLiveOut.get(address));
                            iter.remove();
                            inst.removeSelf(false);
                        } //in the same block, no need to insert phi
                        else {
                            allocLoads.get(inst.block()).add((Load) inst);
                        }
                    }
                } else if (inst instanceof Store) {
                    Operand address = ((Store) inst).address();
                    if (address instanceof Register && (allocVars.contains(address))) {
                        //add live-out info
                        defBlocks.add(inst.block());
                        allocStores.get(inst.block()).put((Register) address, ((Store) inst).value());
                        iter.remove();
                        inst.removeSelf(false);
                    }
                }
            }
        }

        //phi inserting. not the quickest, but the faster one seems needing a "cache"(by SSA book. consider later)
        IRBlock runner;
        HashSet<IRBlock> runningSet;
        while(defBlocks.size() > 0){
            runningSet = defBlocks;
            defBlocks = new HashSet<>();
            for (IRBlock irBlock : runningSet) {
                runner = irBlock;
                HashMap<Register, Operand> runnerDefAlloc = allocStores.get(runner);
                if (runnerDefAlloc.size() != 0) {
                    for (IRBlock df : runner.domFrontiers()) {
                        for (Map.Entry<Register, Operand> entry : runnerDefAlloc.entrySet()) {
                            Register allocVar = entry.getKey();
                            Operand value = entry.getValue();
                    //for the domFrontier of runner, try to add phi
                    //in the runner, allocVar is defined(by store or phi), whose liveOut value is value
                            if (allocPhiMap.get(df).containsKey(allocVar)){
                                //the phi already exists, simply add one source
                                for (IRBlock pre : df.precursors()) if (pre.isDomed(runner))
                                    df.PhiInsertion(allocPhiMap.get(df).get(allocVar), value, pre);
                            }
                            else {
                                //the phi does not exist, needs to do more and use it in the next cycle
                                Register dest = new Register(value.type(), allocVar.name() + "_phi");
                                for (IRBlock pre : df.precursors()) if (pre.isDomed(runner))
                                    df.PhiInsertion(dest, value, pre);
                                if (!allocStores.get(df).containsKey(allocVar)) {
                                    allocStores.get(df).put(allocVar, dest);
                                    defBlocks.add(df);
                                }
                                allocPhiMap.get(df).put(allocVar, dest);
                            }
                        }
                    }
                }
            }
        }

        //rename: remove all load about alloca reg(stores is already removed above)
        fn.blocks().forEach(block -> {
            if (!allocLoads.get(block).isEmpty()) {
                allocLoads.get(block).forEach(load -> {
                    Register reg = load.dest();
                    assert load.address() instanceof Register;
                    Register replacedVar = (Register)load.address();
                    IRBlock currentBlock = block;
                    while(true)
                        if (allocStores.get(currentBlock).containsKey(replacedVar)) {
                            reg.replaceAllUseWith(allocStores.get(currentBlock).get(replacedVar));
                            break;
                        } else currentBlock = currentBlock.iDom();
                        //the replaced one can only from an ancestor of the currentBlock or itself
                    load.removeSelf(true);
                    //this one is safe since it is not in iterating all instructions in the block
                });
            }
        });

        fn.blocks().forEach(block -> block.instructions().removeIf(inst -> inst instanceof Alloc));
    }


    @Override
    public boolean run() {
        irRoot.functions().forEach((name, function) -> runForFn(function));
        return true;
    }
}
