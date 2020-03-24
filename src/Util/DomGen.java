package Util;

import MIR.Function;
import MIR.IRBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
    private void DFS(IRBlock it) {
        if (it.DFSOrder() != 0) return;
        it.domChildren().clear();
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

        if (!blockCollected) {
            fn.blocks().addAll(DFSIndex);
        }

        //in any order is ok, but since I have DFSIndex to collect all blocks...
        for (IRBlock block : DFSIndex) {
            fn.addBlock(block);
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
