package Assemb;

import Assemb.RISCInst.RISCInst;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LIRBlock {

    private ArrayList<LIRBlock> precursors = new ArrayList<>();
    private ArrayList<LIRBlock> successors = new ArrayList<>();
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
    public ArrayList<LIRBlock> successors() {
        return successors;
    }
    public ArrayList<LIRBlock> precursors() {
        return precursors;
    }
    @Override
    public String toString() {
        return name;
    }
}
