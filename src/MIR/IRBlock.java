package MIR;

import MIR.IRinst.*;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class IRBlock {

    private ArrayList<IRBlock> precursors = new ArrayList<>();
    private ArrayList<IRBlock> successors = new ArrayList<>();
    private ArrayList<Inst>    instructions = new ArrayList<>();
    private HashMap<Register, Phi>  PhiInst = new HashMap<>();
    private String name;
    private boolean terminated = false;
    public int loopDepth = 0;

    private IRBlock iDom = null;
    private HashSet<IRBlock> domFrontiers = new HashSet<>();
    private HashSet<IRBlock> domChildren = new HashSet<>();


    public IRBlock(String name) {
        this.name = name;
    }

    public void setName(String name) {
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

    public void addInst(Inst inst) {    //add inst in unterminated block
        instructions.add(inst);
    }
    public void addInstTerminated(Inst inst) {  //add inst in terminated blocks
        instructions.add(instructions.size() - 1, inst);
    }
    public ArrayList<Inst> instructions() {
        return instructions;
    }
    public void addTerminator(Inst inst) {
        instructions.add(inst);
        terminated = true;
        IRBlock dest;
        if (inst instanceof Jump) {
            dest = ((Jump)inst).destBlock();
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
    public boolean returnTerminated() {
        return terminated && terminator() instanceof Return;
    }
    public void removeTerminator() {
        if (!terminated) return;
        terminated = false;
        Inst currentTerm = instructions.get(instructions.size() - 1);
        if (currentTerm instanceof Jump) {
            removeSuccessor(((Jump)currentTerm).destBlock());
        }
        else if (currentTerm instanceof Branch){
            removeSuccessor(((Branch)currentTerm).trueDest());
            removeSuccessor(((Branch)currentTerm).falseDest());
        }
        instructions.remove(instructions.size() - 1);
    }
    public void addPhi(Phi inst) {
        PhiInst.put(inst.dest(), inst);
    }
    public HashMap<Register, Phi> phiInst() {
        return PhiInst;
    }
    public Inst terminator() {
        assert terminated;
        return instructions.get(instructions.size() - 1);
    }

    public void removeSuccessor(IRBlock successor) {
        //this one also removes precursor, so public
        successor.removePrecursor(this);
        successors.remove(successor);
    }
    private void removePrecursor(IRBlock precursor) {
        //this one does not remove successor, so private
        precursors.remove(precursor);
        phiInst().forEach((reg, phi) -> phi.removeBlock(precursor));
    }

    public void setIDom(IRBlock iDom) {
        this.iDom = iDom;
        iDom.domChildren().add(this);
    }
    public IRBlock iDom() {
        return iDom;
    }
    public void addDomFrontier(IRBlock domF) {
        domFrontiers.add(domF);
    }
    public HashSet<IRBlock> domFrontiers() {
        return domFrontiers;
    }
    public void clearDomInfo() {
        domFrontiers.clear();
        domChildren.clear();
        iDom = null;
    }

    public void remove(Inst inst) {
        if (inst instanceof Phi) PhiInst.remove(inst.dest());
        else if (inst instanceof Branch || inst instanceof Return || inst instanceof Jump)
            removeTerminator();
        else instructions.remove(inst);
    }

    public void mergeBlock(IRBlock merged) {
        assert !terminated;
        assert merged.precursors().size() == 0; //so no phi
        successors.addAll(merged.successors());
        merged.successors().forEach(successor -> {
            successor.precursors().remove(merged);
            successor.addPrecursor(this);
        });
        merged.phiInst().forEach((reg, phi) -> phi.setCurrentBlock(this));
        merged.instructions().forEach(inst -> inst.setCurrentBlock(this));
        instructions.addAll(merged.instructions());
        terminated = merged.terminated();
    }

    public boolean isDomed(IRBlock tryDom) {
        IRBlock dom = iDom;

        while(dom != null) {
            if (dom == tryDom) return true;
            dom = dom.iDom();
        }

        return false;
    }
    public HashSet<IRBlock> domChildren() {
        return domChildren;
    }

    public void replaceSuccessor(IRBlock origin, IRBlock dest) {
        if (terminator() instanceof Jump) {
            removeTerminator();
            addTerminator(new Jump(dest, this));
        } else {
            assert terminator() instanceof Branch;
            Branch terminator = (Branch)terminator(), newTerm;
            if (terminator.trueDest() == origin)
                newTerm = new Branch(terminator.condition(), dest, terminator.falseDest(), this);
            else newTerm = new Branch(terminator.condition(), terminator.trueDest(), dest, this);
            removeTerminator();
            addTerminator(newTerm);
        }
    }

    private void phiMerge(IRBlock origin) {
        assert origin.successors().size() == 1 && origin.successors().contains(this);
        HashMap<Register, Phi> merged = origin.phiInst();
        HashSet<Register> notCopy = new HashSet<>();
        PhiInst.forEach((reg, phi) -> {
            //if the phiInst uses a phi in merged blocks, merge the two, no need to replaceAllUse
            ArrayList<Operand> values = phi.values();
            for (Operand value : values) {
                if (value instanceof Register && merged.containsKey(value)) {
                    Phi mergeInst = merged.get(value);
                    ArrayList<Operand> mergeValues = mergeInst.values();
                    ArrayList<IRBlock> mergeBlocks = mergeInst.blocks();
                    for (int j = 0; j < mergeValues.size(); ++j)
                        phi.addOrigin(mergeValues.get(j), mergeBlocks.get(j));
                    notCopy.add(mergeInst.dest());
                }
            }
        });

        merged.forEach((reg, phi) -> {
            //otherwise, copy it currently
            if (notCopy.contains(reg)) {
                //to consider: remove this check in the final version
                phi.dest().uses().forEach(use -> {assert use.block() == this;});
            }
            else phi.moveTo(this);
        });
    }
    public void mergeEmptyBlock(IRBlock merged) {
        phiMerge(merged);
        merged.precursors().forEach(pre -> pre.replaceSuccessor(merged, this));
    }
}
