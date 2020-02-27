package Optim;

import MIR.Function;
import MIR.IRBlock;
import MIR.Root;

import java.util.ArrayList;

public class Mem2Reg extends Pass{

    private Root irRoot;

    public Mem2Reg(Root irRoot) {
        super();
        this.irRoot = irRoot;
    }


    ArrayList<ArrayList<IRBlock>> bucket;
    private int tot = 0;
    private ArrayList<IRBlock> DFSIndex = new ArrayList<>();
    private void DFS(IRBlock it)
    {
        if (it.DFSOrder() != 0) return;
        DFSIndex.add(it);
        it.setSDom(it);
        it.setDFSOrder(++tot);
        it.successors().forEach(son -> {
            DFS(son);
            son.setDFSFather(it);
        });
    }
    private void DFSOrderGen(IRBlock entranceBlock)
    {
        tot = 0;
        DFS(entranceBlock);
        entranceBlock.setDFSFather(null);
    }

    private IRBlock FindUnionRoot(IRBlock it)
    {
        if (it.unionRoot() == it) return it;
        IRBlock ret = FindUnionRoot(it.unionRoot());
        if (it.unionRoot().minVer().sDom().DFSOrder() < it.minVer().sDom().DFSOrder())
            it.setMinVer(it.unionRoot().minVer());
        it.setUnionRoot(ret);
        return ret;
    }
    private IRBlock eval(IRBlock it)
    {
        FindUnionRoot(it);
        return it.minVer();
    }

    private void iDomGen(IRBlock entranceBlock)
    {
        IRBlock tmp;

        DFSOrderGen(entranceBlock);

        bucket = new ArrayList<>();
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
        iDomGen(fn.entryBlock());
        //todo
    }

    @Override
    public boolean run() {
        irRoot.functions().forEach((name, function) -> runForFn(function));
        return true;
    }
}
