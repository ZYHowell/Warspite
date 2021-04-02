package Util;

import MIR.Function;
import MIR.IRBlock;

import java.util.*;

public class DomGen {
    private Function fn;

    public DomGen(Function fn) {
        this.fn = fn;
    }

    ArrayList<ArrayList<IRBlock>> bucket = new ArrayList<>();
    private int tot = 0;
    private ArrayList<IRBlock> DFSIndex = new ArrayList<>();
    private HashMap<IRBlock, Integer> dfsOrder = new HashMap<>();
    private HashMap<IRBlock, IRBlock> sDom = new HashMap<>(),
            union = new HashMap<>(),
            minVer = new HashMap<>(),
            dfsFather = new HashMap<>();
    private HashMap<IRBlock, LinkedList<IRBlock>> domTree;

    private void DFS(IRBlock it) {
        if (dfsOrder.containsKey(it)) return;
        it.clearDomInfo();
        DFSIndex.add(it);
        sDom.put(it, it);
        dfsOrder.put(it, ++tot);
        union.put(it, it);
        minVer.put(it, it);
        it.successors.forEach(son -> {
            if (!dfsOrder.containsKey(son)){
                DFS(son);
                dfsFather.put(son, it);
            }
        });
    }

    private void DFSOrderGen(IRBlock entranceBlock) {
        tot = 0;
        DFSIndex.add(null); //1-base DFS order here, so...
        DFS(entranceBlock);
        dfsFather.put(entranceBlock, null);
    }

    private IRBlock eval(IRBlock it) {
        if (union.get(it) != union.get(union.get(it))) {
            if (dfsOrder.get(sDom.get(minVer.get(it))) >
                    dfsOrder.get(sDom.get(eval(union.get(it)))))
                minVer.put(it, eval(union.get(it)));
            union.put(it, union.get(union.get(it)));
        }
        return minVer.get(it);
    }

    public void runForFn() {
        IRBlock tmp, entranceBlock = fn.entryBlock;

        bucket = new ArrayList<>();
        DFSIndex = new ArrayList<>();
        tot = 0;

        DFSOrderGen(entranceBlock);

        for (int i = 0; i <= tot; ++i) bucket.add(new ArrayList<>());

        for (int i = tot; i > 1; --i) {
            tmp = DFSIndex.get(i);
            for (IRBlock pre : tmp.precursors) {
                IRBlock evalBlock = eval(pre);
                if (dfsOrder.get(sDom.get(tmp)) > dfsOrder.get(sDom.get(evalBlock)))
                    sDom.put(tmp, sDom.get(evalBlock));
            }
            bucket.get(dfsOrder.get(sDom.get(tmp))).add(tmp);
            IRBlock x = dfsFather.get(tmp);
            union.put(tmp, x);
            for (IRBlock buk : bucket.get(dfsOrder.get(x))) {
                IRBlock u = eval(buk);
                buk.iDom = dfsOrder.get(sDom.get(u)) < dfsOrder.get(x) ? u : x;
            }
            bucket.get(dfsOrder.get(x)).clear();
        }
        for (int i = 2; i <= tot; ++i) {
            tmp = DFSIndex.get(i);
            if (tmp.iDom != sDom.get(tmp))
                tmp.iDom = tmp.iDom.iDom;
        }

        int size = DFSIndex.size();
        for (int i = 1; i < size; ++i) {
            IRBlock block = DFSIndex.get(i);
            if (block.precursors.size() >= 2) {
                for (IRBlock runner : block.precursors) {
                    while (runner != block.iDom) {
                        runner.domFrontiers.add(block);
                        runner = runner.iDom;
                    }
                }
            }
        }

        domTree = new HashMap<>();
        fn.blocks.forEach(b -> domTree.put(b, new LinkedList<>()));
        fn.blocks.forEach(b -> {
            if (b.iDom != null) domTree.get(b.iDom).add(b);
            b.domEntranceID = b.domExitID = -1;
        });
        _id = 0;
        domTreeDFS(entranceBlock);
        _id = 0;domTree.clear();
    }
    private int _id = 0;
    private void domTreeDFS(IRBlock b){
        b.domEntranceID = _id++;
        domTree.get(b).forEach(this::domTreeDFS);
        b.domExitID = _id++;
    }
}