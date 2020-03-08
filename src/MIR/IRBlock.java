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


    private int dfsOrder = 0;
    private IRBlock DFSFather = null, sDom = null, unionRoot = this, minVer = this,
                    iDom = null;
    private HashSet<IRBlock> domFrontiers = new HashSet<>();


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
    public void removeTerminal() {
        terminated = false;
        Inst currentTerm = instructions.get(instructions.size() - 1);
        if (currentTerm instanceof Jump) {
            removeSuccessor(((Jump)currentTerm).destBlock());
        }
        else if (currentTerm instanceof Branch){
            removeSuccessor(((Branch)currentTerm).trueDest());
            removeSuccessor(((Branch)currentTerm).falseDest());
        }
    }
    public void addPhi(Phi inst) {
        PhiInst.put(inst.dest(), inst);
    }
    public void PhiInsertion(Register dest, Operand value, IRBlock origin) {
        if (PhiInst.containsKey(dest)) {
            Phi phi = PhiInst.get(dest);
            phi.addOrigin(value, origin);
        } else {
            ArrayList<IRBlock> blocks = new ArrayList<>();
            blocks.add(origin);
            ArrayList<Operand> values = new ArrayList<>();
            values.add(value);
            Phi phi = new Phi(dest, blocks, values, this);
            PhiInst.put(dest, phi);
        }
    }
    public HashMap<Register, Phi> phiInst() {
        return PhiInst;
    }

    private void removeSuccessor(IRBlock successor) {
        successor.removePrecursor(this);
        successors.remove(successor);
    }
    private void removePrecursor(IRBlock precursor) {
        precursors.remove(precursor);
        phiInst().forEach((reg, phi) -> phi.removeBlock(precursor));
    }

    public void setDFSOrder(int order) {
        dfsOrder = order;
    }
    public int DFSOrder() {
        return dfsOrder;
    }
    public void setDFSFather(IRBlock father) {
        DFSFather = father;
    }
    public IRBlock DFSFather() {
        return DFSFather;
    }
    public void setSDom(IRBlock sDom) {
        this.sDom = sDom;
    }
    public IRBlock sDom() {
        return sDom;
    }
    public void setIDom(IRBlock iDom) {
        this.iDom = iDom;
    }
    public IRBlock iDom() {
        return iDom;
    }
    public void setUnionRoot(IRBlock uRoot) {
        unionRoot = uRoot;
    }
    public IRBlock unionRoot() {
        return unionRoot;
    }
    public void setMinVer(IRBlock minVer) {
        this.minVer = minVer;
    }
    public IRBlock minVer() {
        return minVer;
    }
    //the part above is ugly. no need to keep these info after mem2reg.
    public void addDomFrontier(IRBlock domF) {
        domFrontiers.add(domF);
    }
    public HashSet<IRBlock> domFrontiers() {
        return domFrontiers;
    }

    public void remove(Inst inst) {
        if (inst instanceof Phi) PhiInst.remove(inst.dest());
        else if (inst instanceof Branch || inst instanceof Return || inst instanceof Jump)
            removeTerminal();
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

        merged.instructions().forEach(inst -> inst.setCurrentBlock(this));
        instructions.addAll(merged.instructions());
        terminated = merged.terminated();
    }
}
