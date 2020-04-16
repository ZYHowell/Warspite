package Assemb;

import Assemb.LOperand.Reg;
import Assemb.RISCInst.Mv;

import java.util.ArrayList;
import java.util.HashSet;

public class LFn {

    private HashSet<LIRBlock> blocks = new HashSet<>();
    private ArrayList<Reg> params = new ArrayList<>();
    private LIRBlock entryBlock, exitBlock;
    public int paramOffset = 0;
    private String name;
    private DAG dag = new DAG();
    private HashSet<Mv> workListMv = new HashSet<>();

    public LFn(String name, LIRBlock entryBlock, LIRBlock exitBlock) {
        this.entryBlock = entryBlock;
        this.exitBlock = exitBlock;
        this.name = name;
    }

    public String name() {
        return name;
    }
    public DAG dag() {
        return dag;
    }
    public HashSet<Mv> workListMv() {
        return workListMv;
    }
    public void addPara(Reg para){
        params.add(para);
    }
    public ArrayList<Reg> params() {
        return params;
    }
    public LIRBlock entryBlock() {
        return entryBlock;
    }
    public LIRBlock exitBlock() {
        return exitBlock;
    }
    public void addBlock(LIRBlock block) {
        blocks.add(block);
    }
    public HashSet<LIRBlock> blocks() {
        return blocks;
    }
}
