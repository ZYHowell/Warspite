package MIR;

import MIR.IRinst.*;
import Util.error.internalError;

import java.util.ArrayList;

public class IRBlock {

    private ArrayList<IRBlock> precursors = new ArrayList<>();
    private ArrayList<IRBlock> successors = new ArrayList<>();
    private ArrayList<Inst>    instructions = new ArrayList<>();
    private String name;
    private boolean terminated = false;

    public IRBlock(String name) {
        this.name = name;
    }

    public String name() {
        return name;
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
    public void addTerminator(Inst inst) {
        instructions.add(inst);
        terminated = true;
        IRBlock dest;
        if (inst instanceof Return) {
        } else if (inst instanceof Jump) {
            dest = ((Jump)inst).dest();
            addSuccessor(dest);
            dest.addPrecursor(this);
        } else if (inst instanceof Branch) {
            dest = ((Branch)inst).trueDest();
            addSuccessor(dest);
            dest.addPrecursor(this);
            dest = ((Branch)inst).falseDest();
            addSuccessor(dest);
            dest.addPrecursor(this);
        }
    }
    public boolean terminated() {
        return terminated;
    }
    public void removeTerminal() {
        terminated = false;
        Inst currentTerm = instructions.get(instructions.size() - 1);
        if (currentTerm instanceof Return) {}
        else if (currentTerm instanceof Jump) {
            removeSuccessor(((Jump)currentTerm).dest());
        }
        else {
            removeSuccessor(((Branch)currentTerm).trueDest());
            removeSuccessor(((Branch)currentTerm).falseDest());
        }
    }

    private void removeSuccessor(IRBlock successor) {
        successor.precursors().remove(this);
        successors.remove(successor);
    }
}
