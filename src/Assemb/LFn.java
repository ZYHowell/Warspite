package Assemb;

import Assemb.LOperand.Reg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class LFn {

    private HashSet<LIRBlock> blocks = new LinkedHashSet<>();
    private ArrayList<Reg> params = new ArrayList<>();
    public LIRBlock entryBlock, exitBlock;
    public int paramOffset = 0;
    public int cnt;
    private String name;

    public LFn(String name, LIRBlock entryBlock, LIRBlock exitBlock) {
        this.entryBlock = entryBlock;
        this.exitBlock = exitBlock;
        this.name = name;
    }

    public String name() {
        return name;
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
