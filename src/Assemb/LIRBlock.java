package Assemb;

import Assemb.RISCInst.RISCInst;

import java.util.ArrayList;

public class LIRBlock {

    private ArrayList<LIRBlock> precursors = new ArrayList<>();
    private ArrayList<LIRBlock> successors = new ArrayList<>();
    private ArrayList<RISCInst> instructions = new ArrayList<>();

    public LIRBlock() {}

    public void addInst(RISCInst inst) {
        instructions.add(inst);
    }
    public ArrayList<RISCInst> instructions() {
        return instructions;
    }
    public ArrayList<LIRBlock> successors() {
        return successors;
    }
    public ArrayList<LIRBlock> precursors() {
        return precursors;
    }
}
