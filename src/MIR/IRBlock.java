package MIR;

import MIR.IRinst.Inst;

import java.util.ArrayList;

public class IRBlock {

    private ArrayList<IRBlock> precursors = new ArrayList<>();
    private ArrayList<IRBlock> successors = new ArrayList<>();
    private ArrayList<Inst>    instructions = new ArrayList<>();

    public IRBlock() {
    }

    public void addPrecursor(IRBlock precursor) {
        precursors.add(precursor);
    }
    public void addSuccessor(IRBlock successor) {
        successors.add(successor);
    }
    public ArrayList<IRBlock> precursors() {
        return precursors;
    }
    public ArrayList<IRBlock> successors() {
        return successors;
    }

    public void addInst(Inst inst) {
        instructions.add(inst);
    }
    public ArrayList<Inst> instructions() {
        return instructions;
    }
}
