package MIR;

import MIR.IRinst.*;
import MIR.IRoperand.Register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class IRBlock {

    public ArrayList<IRBlock> precursors = new ArrayList<>();
    public ArrayList<IRBlock> successors = new ArrayList<>();
    public Inst headInst = null, tailInst = null;
    public HashMap<Register, Phi>  PhiInst = new HashMap<>();
    public String name;
    public boolean terminated = false;
    public int loopDepth = 0;

    public IRBlock iDom = null;
    public HashSet<IRBlock> domFrontiers = new HashSet<>();
    public int domEntranceID = -1, domExitID = -1;

    public IRBlock(String name) {
        this.name = name;
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
        inst.setCurrentBlock(this);
    }
    public void addTerminator(Inst inst) {
        addInst(inst);
        terminated = true;
        IRBlock dest;
        if (inst instanceof Jump) {
            dest = ((Jump)inst).destBlock();
            successors.add(dest);
            dest.precursors.add(this);
        } else if (inst instanceof Branch) {
            dest = ((Branch)inst).trueDest();
            successors.add(dest);
            dest.precursors.add(this);
            dest = ((Branch)inst).falseDest();
            successors.add(dest);
            dest.precursors.add(this);
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
        PhiInst.forEach((reg, phi) -> phi.removeBlock(precursor));
    }

    public void splitTo(IRBlock later, Inst inst) {
        successors.forEach(suc -> {
            suc.phiModify(this, later);
            suc.precursors.remove(this);
            suc.precursors.add(later);
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
    public void clearDomInfo() {
        domFrontiers.clear();
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

    public void mergeBlock(IRBlock merged) {
        assert !terminated;
        assert merged.precursors.size() == 0; //so no phi
        successors.addAll(merged.successors);
        merged.successors.forEach(successor -> successor.replacePrecursor(merged, this));
        merged.PhiInst.forEach((reg, phi) -> phi.setCurrentBlock(this));
        for (Inst inst = merged.headInst; inst != null; inst = inst.next)
            inst.setCurrentBlock(this);
        if (tailInst != null) tailInst.next = merged.headInst;
        if (merged.headInst != null) merged.headInst.prior = tailInst;
        if (headInst == null) headInst = merged.headInst;
        tailInst = merged.tailInst;
        terminated = merged.terminated;
        merged.successors.forEach(suc -> {
            if (suc.precursors.contains(merged)) throw new RuntimeException();
        });
    }

    public boolean isDomed(IRBlock tryDom) {
        assert tryDom.domEntranceID != -1 && tryDom.domExitID != -1;
        assert domExitID != -1 && domEntranceID != -1;
        return tryDom.domEntranceID < domEntranceID && tryDom.domExitID > domExitID;
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
}
