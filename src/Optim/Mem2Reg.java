package Optim;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.*;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.Root;

import java.util.*;

//to consider: what about using DJ-graph and a better algorithm in SSA book to do this? maybe quicker

public class Mem2Reg extends Pass{

    private Root irRoot;

    public Mem2Reg(Root irRoot) {
        super();
        this.irRoot = irRoot;
    }


    ArrayList<ArrayList<IRBlock>> bucket = new ArrayList<>();
    private int tot = 0;
    private ArrayList<IRBlock> DFSIndex = new ArrayList<>();
    private void DFS(IRBlock it) {
        if (it.DFSOrder() != 0) return;
        DFSIndex.add(it);
        it.setSDom(it);
        it.setDFSOrder(++tot);
        it.successors().forEach(son -> {
            DFS(son);
            son.setDFSFather(it);
        });
    }
    private void DFSOrderGen(IRBlock entranceBlock) {
        tot = 0;
        DFSIndex.add(null); //1-base DFS order here, so...
        DFS(entranceBlock);
        entranceBlock.setDFSFather(null);
    }

    private IRBlock FindUnionRoot(IRBlock it) {
        if (it.unionRoot() == it) return it;
        IRBlock ret = FindUnionRoot(it.unionRoot());
        if (it.unionRoot().minVer().sDom().DFSOrder() < it.minVer().sDom().DFSOrder())
            it.setMinVer(it.unionRoot().minVer());
        it.setUnionRoot(ret);
        return ret;
    }
    private IRBlock eval(IRBlock it) {
        FindUnionRoot(it);
        return it.minVer();
    }

    private void iDomGen(IRBlock entranceBlock) {
        IRBlock tmp;

        DFSOrderGen(entranceBlock);

        for (int i = 0;i <= tot;++i) bucket.add(new ArrayList<>());

        for (int i = tot;i > 1;--i) {
            tmp = DFSIndex.get(i);
            for (IRBlock pre : tmp.precursors()){
                IRBlock evalBlock = eval(pre);
                if (tmp.sDom().DFSOrder() > evalBlock.sDom().DFSOrder())
                    tmp.setSDom(evalBlock.sDom());
            }
            bucket.get(tmp.sDom().DFSOrder()).add(tmp);
            IRBlock tmpFather = tmp.DFSFather();
            tmp.setUnionRoot(tmpFather);
            for (IRBlock buk : bucket.get(tmpFather.DFSOrder())) {
                IRBlock u = eval(buk);
                buk.setIDom(u.sDom() == buk.sDom() ? tmpFather : u);
            }
            bucket.get(tmpFather.DFSOrder()).clear();
        }
        for (int i = 2;i <= tot;++i) {
            tmp = DFSIndex.get(i);
            if (tmp.iDom() != DFSIndex.get(tmp.sDom().DFSOrder()))
                tmp.setIDom(tmp.iDom().iDom());
        }
    }
    private void runForFn(Function fn) {
    //only a normal minimal SSA
        HashSet<Register> allocVars = fn.allocVars();

        HashSet<IRBlock> defBlocks = new HashSet<>();
        HashMap<IRBlock, HashSet<Load>> allocLoads = new HashMap<>();
        HashMap<IRBlock, HashMap<Register, Register>> allocPhiMap = new HashMap<>();
        HashMap<IRBlock, HashMap<Register, Operand>> allocStores = new HashMap<>();

        bucket = new ArrayList<>();
        DFSIndex = new ArrayList<>();
        tot = 0;

        iDomGen(fn.entryBlock());
        //in any order is ok, but since I have DFSIndex to collect all blocks...
        for (int i = 1; i <= tot;++i) {
            IRBlock block = DFSIndex.get(i);
            fn.addBlock(block);
            allocLoads.put(block, new HashSet<>());
            allocStores.put(block, new HashMap<>());
            allocPhiMap.put(block, new HashMap<>());
            if (block.precursors().size() >= 2) {
                for (IRBlock runner : block.precursors()) {
                    while (runner != block.iDom()) {
                        runner.addDomFrontier(block);
                        runner = runner.iDom();
                    }
                }
            }
        }

        //collect load/store info.
        for (int i = 1; i <= tot;++i) {
            IRBlock block = DFSIndex.get(i);
            for (Inst inst : block.instructions()) {
                if (inst instanceof Load) {
                    Operand address = ((Load) inst).address();
                    if (address instanceof Register && (allocVars.contains(address))) {
                        /*
                         * add live-in info, which may be used to make an early liveness analysis
                         * which may release the pressure on later liveness analysis
                         * and form pruned SSA rather than minimal SSA/
                         */
                        HashMap<Register, Operand> blockLiveOut = allocStores.get(inst.block());
                        if (blockLiveOut.containsKey(address)){
                            inst.dest().replaceAllUseWith(blockLiveOut.get(address));
                            inst.removeSelf();
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
                        inst.removeSelf();
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
                            if (allocPhiMap.get(df).containsKey(allocVar))
                                //the phi already exists, simply add one source
                                df.PhiInsertion(allocPhiMap.get(df).get(allocVar), value, runner);
                            else {
                                //the phi does not exist, needs to do more and use it in the next cycle
                                Register dest = new Register(value.type(), allocVar.name() + "_phi");
                                df.PhiInsertion(dest, value, runner);
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
        for (int i = 1; i <= tot;++i) {
            IRBlock block = DFSIndex.get(i);
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
                    load.removeSelf();
                });
            }
        }
    }


    @Override
    public boolean run() {
        irRoot.functions().forEach((name, function) -> runForFn(function));
        return true;
    }
}
