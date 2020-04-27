package Assemb;

import Assemb.RISCInst.RISCInst;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LIRBlock {

    public ArrayList<LIRBlock> precursors = new ArrayList<>();
    public ArrayList<LIRBlock> successors = new ArrayList<>();
    private List<RISCInst> instructions = new LinkedList<>();
    public int loopDepth;
    public String name;

    public LIRBlock(int loopDepth, String name) {
        this.loopDepth = loopDepth;
        this.name = name;
    }

    public void addInst(RISCInst inst) {
        instructions.add(inst);
    }
    public List<RISCInst> instructions() {
        return instructions;
    }

    @Override
    public String toString() {
        return name;
    }
}
