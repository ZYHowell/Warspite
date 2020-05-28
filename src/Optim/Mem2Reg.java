package Optim;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.*;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.Root;
import Util.DomGen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Mem2Reg extends Pass{

    private Root irRoot;

    public Mem2Reg(Root irRoot) {
        super();
        this.irRoot = irRoot;
    }

    private Operand finalReplace(HashMap<Operand, Operand> replaceMap, Operand replaced) {
        Operand tmp = replaced;
        while (replaceMap.containsKey(tmp)) tmp = replaceMap.get(tmp);
        return tmp;
    }

    private void runForFn(Function fn) {
    //only a normal minimal SSA
        HashSet<Register> allocVars = fn.allocVars();

        HashSet<IRBlock> defBlocks = new HashSet<>();
        HashMap<IRBlock, HashSet<Load>> allocLoads = new HashMap<>();
        HashMap<IRBlock, HashMap<Register, Phi>> allocPhiMap = new HashMap<>();
        HashMap<IRBlock, HashMap<Register, Operand>> allocStores = new HashMap<>();
        HashMap<Operand, Operand> replaceMap = new HashMap<>();

        new DomGen(fn).runForFn();

        fn.blocks().forEach(block -> {
            allocLoads.put(block, new HashSet<>());
            allocStores.put(block, new HashMap<>());
            allocPhiMap.put(block, new HashMap<>());
        });

        //collect load/store info.
        for (IRBlock block : fn.blocks()) {
            for (Inst inst = block.headInst; inst != null;) {
                Inst tmp = inst.next;
                if (inst instanceof Load) {
                    Operand address = ((Load) inst).address();
                    if (address instanceof Register && (allocVars.contains(address))) {
                        HashMap<Register, Operand> blockLiveOut = allocStores.get(inst.block());
                        if (blockLiveOut.containsKey(address)) {
                            replaceMap.put(inst.dest(), blockLiveOut.get(address));
                            inst.removeSelf(true);
                        } //in the same block, no need to insert phi
                        else allocLoads.get(inst.block()).add((Load) inst);
                    }
                } else if (inst instanceof Store) {
                    Operand address = ((Store) inst).address();
                    if (address instanceof Register && (allocVars.contains(address))) {
                        //add live-out info
                        defBlocks.add(inst.block());
                        allocStores.get(inst.block()).put((Register) address, ((Store) inst).value());
                        inst.removeSelf(true);
                    }
                }
                inst = tmp;
            }
        }

        //phi inserting. not the quickest, but the faster one seems needing a "cache"(by SSA book)
        HashSet<IRBlock> runningSet;
        while(defBlocks.size() > 0){
            runningSet = defBlocks;
            defBlocks = new HashSet<>();
            for (IRBlock runner : runningSet) {
                HashMap<Register, Operand> runnerDefAlloc = allocStores.get(runner);
                if (runnerDefAlloc.size() != 0) {
                    for (IRBlock df : runner.domFrontiers()) {
                        for (Map.Entry<Register, Operand> entry : runnerDefAlloc.entrySet()) {
                            Register allocVar = entry.getKey();
                            Operand value = entry.getValue();
                    //for the domFrontier of runner, try to add phi
                    //in the runner, allocVar is defined(by store or phi), whose liveOut value is value
                    //MENTION: !!! fill the value of phi later rather than in creating
                            if (!allocPhiMap.get(df).containsKey(allocVar)) {
                                //the phi does not exist, needs to do more and use it in the next cycle
                                Register dest = new Register(value.type(), allocVar.name() + "_phi");
                                Phi phi = new Phi(dest, new ArrayList<>(), new ArrayList<>(), df);
                                df.addPhi(phi);
                                if (!allocStores.get(df).containsKey(allocVar)) {
                                    allocStores.get(df).put(allocVar, dest);
                                    defBlocks.add(df);
                                }
                                allocPhiMap.get(df).put(allocVar, phi);
                            }
                        }
                    }
                }
            }
        }

        //rename: remove all load about alloca reg(stores are already removed above)
        fn.blocks().forEach(block -> {
            if (!allocPhiMap.get(block).isEmpty()) {
                allocPhiMap.get(block).forEach((address, phi) -> block.precursors().forEach(pre -> {
                    IRBlock runner = pre;
                    while (!allocStores.get(runner).containsKey(address)) runner = runner.iDom();
                    phi.addOrigin(allocStores.get(runner).get(address), pre);
                }));
            }
            if (!allocLoads.get(block).isEmpty()) {
                allocLoads.get(block).forEach(load -> {
                    Register reg = load.dest();
                    assert load.address() instanceof Register;
                    Register replacedVar = (Register)load.address();
                    Operand replace;
                    if (allocPhiMap.get(block).containsKey(replacedVar))
                        replace = allocPhiMap.get(block).get(replacedVar).dest();
                    else {
                        IRBlock currentBlock = block.iDom();
                        while (true)
                            if (allocStores.get(currentBlock).containsKey(replacedVar)) {
                                replace = allocStores.get(currentBlock).get(replacedVar);
                                break;
                            } else currentBlock = currentBlock.iDom();
                    }
                    //the replaced one can only from an ancestor of the currentBlock or itself
                    replaceMap.put(reg, finalReplace(replaceMap, replace));
                    load.removeSelf(true);
                });
            }
        });
        replaceMap.forEach((reg, rep) -> ((Register)reg).replaceAllUseWith(finalReplace(replaceMap, rep)));

        fn.blocks().forEach(block -> {
            for (Inst inst = block.headInst; inst != null; inst = inst.next)
                if (inst instanceof Alloc) inst.removeInList();
        });
    }


    @Override
    public boolean run() {
        irRoot.functions().forEach((name, function) -> runForFn(function));
        return true;
    }
}
