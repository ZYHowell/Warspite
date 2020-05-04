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
    public Inst headInst = null, tailInst = null;
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
        if (headInst == null) headInst = tailInst = inst;
        else {
            tailInst.next = inst;
            inst.prior = tailInst;
            tailInst = inst;
        }
    }
    public void addHeadInst(Inst inst) {
        if (headInst == null) headInst = tailInst = inst;
        else {
            inst.next = headInst;
            headInst.prior = inst;
            headInst = inst;
        }
    }
    public void addInstTerminated(Inst inst) {  //add inst in terminated blocks
        Inst priorTail = tailInst.prior;
        if (priorTail == null) {
            headInst = inst;
            inst.prior = null;
            inst.next = tailInst;
            tailInst.prior = inst;
        }
        else {
            priorTail.next = inst;
            inst.next = tailInst;
            tailInst.prior = inst;
            inst.prior = priorTail;
        }
    }
    public void addTerminator(Inst inst) {
        addInst(inst);
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
        Inst currentTerm = tailInst;
        if (currentTerm instanceof Jump) {
            removeSuccessor(((Jump)currentTerm).destBlock());
        }
        else if (currentTerm instanceof Branch){
            removeSuccessor(((Branch)currentTerm).trueDest());
            removeSuccessor(((Branch)currentTerm).falseDest());
        }
        currentTerm.removeInList();
        currentTerm.removeSelf(true);
    }
    public void addPhi(Phi inst) {
        PhiInst.put(inst.dest(), inst);
    }
    public HashMap<Register, Phi> phiInst() {
        return PhiInst;
    }
    public Inst terminator() {
        assert terminated;
        return tailInst;
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

    public void splitTo(IRBlock later, Inst inst) {
        successors.forEach(suc -> {
            suc.phiModify(this, later);
            suc.precursors().remove(this);
            suc.precursors().add(later);
        });
        later.successors.addAll(successors);
        successors.clear();

        later.headInst = inst.next;
        later.tailInst = tailInst;
        inst.next.prior = null;
        for(Inst instr = inst.next;instr != null; instr = instr.next)
            instr.setCurrentBlock(later);
        terminated = false;
        later.terminated = true;
        tailInst = inst.prior;
        if (tailInst != null) tailInst.next = null;
        if (headInst == inst) headInst = null;
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
        else inst.removeInList();
    }

    private void replacePrecursor(IRBlock replaced, IRBlock newPre) {
        if (precursors.contains(replaced)) {
            precursors.remove(replaced);
            precursors.add(newPre);
            phiModify(replaced, newPre);
        }
    }
    public void phiModify(IRBlock replaced, IRBlock newPre) {
        PhiInst.forEach((reg, phi) -> {
            int size = phi.blocks().size();
            for (int i = 0;i < size;++i)
                if (phi.blocks().get(i) == replaced) phi.blocks().set(i, newPre);
        });
    }
    public ArrayList<Inst> instructions() { //debug use;
        ArrayList<Inst> ret = new ArrayList<>();
        for (Inst inst = headInst; inst != null; inst = inst.next)
            ret.add(inst);
        return ret;
    }

    public void mergeBlock(IRBlock merged) {
        assert !terminated;
        assert merged.precursors().size() == 0; //so no phi
        successors.addAll(merged.successors());
        merged.successors().forEach(successor -> successor.replacePrecursor(merged, this));
        merged.phiInst().forEach((reg, phi) -> phi.setCurrentBlock(this));
        for (Inst inst = merged.headInst; inst != null; inst = inst.next)
            inst.setCurrentBlock(this);
        if (tailInst != null) tailInst.next = merged.headInst;
        if (merged.headInst != null) merged.headInst.prior = tailInst;
        if (headInst == null) headInst = merged.headInst;
        tailInst = merged.tailInst;
        terminated = merged.terminated;
        merged.successors().forEach(suc -> {
            if (suc.precursors().contains(merged)) throw new RuntimeException();
        });
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
            ArrayList<Operand> values = new ArrayList<>(phi.values());
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
            int size = phi.blocks().size();
            for (int i = 0;i < size;++i) {
                if (phi.blocks().get(i) == origin) {
                    Operand value = phi.values().get(i);
                    if (value instanceof Register && notCopy.contains(value)) {
                        phi.blocks().remove(i);
                        phi.values().remove(i);
                    } else phi.blocks().set(i, origin.precursors.get(0));
                    break;
                }
            }
        });

        merged.forEach((reg, phi) -> {
            //otherwise, copy it currently
            if (!notCopy.contains(reg)) {
                if (precursors().size() > 1) throw new RuntimeException();
                phi.moveTo(this);
            }
        });
    }
    public void mergeEmptyBlock(IRBlock merged) {
        phiMerge(merged);
        this.precursors().remove(merged);
        ArrayList<IRBlock> precursors = new ArrayList<>(merged.precursors());
        precursors.forEach(pre -> pre.replaceSuccessor(merged, this));
    }
}
