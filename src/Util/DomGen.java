package Util;

import MIR.Function;
import MIR.IRBlock;

import java.util.ArrayList;
import java.util.HashMap;

public class DomGen {
    private Function fn;
    private boolean blockCollected;
    public DomGen(Function fn, boolean blockCollected) {
        this.fn = fn;
        this.blockCollected = blockCollected;
    }

    ArrayList<ArrayList<IRBlock>> bucket = new ArrayList<>();
    private int tot = 0;
    private ArrayList<IRBlock> DFSIndex = new ArrayList<>();
    private HashMap<IRBlock, Integer> dfsOrder = new HashMap<>();
    private HashMap<IRBlock, IRBlock> sDom = new HashMap<>(),
                                      union = new HashMap<>(),
                                      minVer = new HashMap<>(),
                                      dfsFather = new HashMap<>();
    private void DFS(IRBlock it) {
        if (dfsOrder.containsKey(it)) return;
        it.clearDomInfo();
        DFSIndex.add(it);
        sDom.put(it, it);
        dfsOrder.put(it, ++tot);
        union.put(it, it);
        minVer.put(it, it);
        it.successors().forEach(son -> {
            DFS(son);
            dfsFather.put(son, it);
        });
    }
    private void DFSOrderGen(IRBlock entranceBlock) {
        tot = 0;
        DFSIndex.add(null); //1-base DFS order here, so...
        DFS(entranceBlock);
        dfsFather.put(entranceBlock, null);
    }

    private IRBlock FindUnionRoot(IRBlock it) {
        if (union.get(it) == it) return it;
        IRBlock ret = FindUnionRoot(union.get(it));
        if (dfsOrder.get(sDom.get(minVer.get(union.get(it)))) <
                dfsOrder.get(sDom.get(minVer.get(it))))
            minVer.put(it, minVer.get(union.get(it)));
        union.put(it, ret);
        return ret;
    }
    private IRBlock eval(IRBlock it) {
        FindUnionRoot(it);
        return minVer.get(it);
    }

    public void runForFn() {
        IRBlock tmp, entranceBlock = fn.entryBlock();

        bucket = new ArrayList<>();
        DFSIndex = new ArrayList<>();
        tot = 0;

        DFSOrderGen(entranceBlock);

        for (int i = 0;i <= tot;++i) bucket.add(new ArrayList<>());

        for (int i = tot;i > 1;--i) {
            tmp = DFSIndex.get(i);
            for (IRBlock pre : tmp.precursors()){
                IRBlock evalBlock = eval(pre);
                if (dfsOrder.get(sDom.get(tmp)) > dfsOrder.get(sDom.get(evalBlock)))
                    sDom.put(tmp, sDom.get(evalBlock));
            }
            bucket.get(dfsOrder.get(sDom.get(tmp))).add(tmp);
            IRBlock tmpFather = dfsFather.get(tmp);
            union.put(tmp, tmpFather);
            for (IRBlock buk : bucket.get(dfsOrder.get(tmpFather))) {
                IRBlock u = eval(buk);
                buk.setIDom(sDom.get(u) == sDom.get(buk) ? tmpFather : u);
            }
            bucket.get(dfsOrder.get(tmpFather)).clear();
        }
        for (int i = 2;i <= tot;++i) {
            tmp = DFSIndex.get(i);
            if (tmp.iDom() != DFSIndex.get(dfsOrder.get(sDom.get(tmp))))
                tmp.setIDom(tmp.iDom().iDom());
        }

        if (!blockCollected) {
            fn.blocks().addAll(DFSIndex);
        }

        //in any order is ok, but since I have DFSIndex to collect all blocks...
        for (IRBlock block : DFSIndex) {
            if (block.precursors().size() >= 2) {
                for (IRBlock runner : block.precursors()) {
                    while (runner != block.iDom()) {
                        runner.addDomFrontier(block);
                        runner = runner.iDom();
                    }
                }
            }
        }
    }
}
