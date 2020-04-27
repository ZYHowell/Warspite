package BackEnd;

import Assemb.*;
import Assemb.LOperand.PhyReg;
import Assemb.LOperand.Reg;
import Assemb.LOperand.SLImm;
import Assemb.LOperand.VirtualReg;
import Assemb.RISCInst.Ld;
import Assemb.RISCInst.Mv;
import Assemb.RISCInst.RISCInst;
import Assemb.RISCInst.St;

import java.util.*;

public class RegAlloc {

    private static int K = 28;
    private HashSet<Reg> preColored;
    private LRoot root;
    private DAG currentDAG;
    private LFn currentFn;
    private HashSet<Mv> workListMv,
                        activeMoves = new HashSet<>(),
                        coalescedMoves = new HashSet<>(),
                        constrainedMoves = new HashSet<>(),
                        frozenMoves = new HashSet<>();
    private HashSet<Reg> spillWorkList = new HashSet<>(),
                         freezeWorkList = new HashSet<>(),
                         simplifyWorkList = new HashSet<>(),
                         spilledNodes = new HashSet<>(),
                         coloredNodes = new HashSet<>(),
                         coalescedNodes = new HashSet<>(),
                         spillIntroduce = new HashSet<>();
    private Stack<Reg> selectStack = new Stack<>();
    private int stackLength = 0;

    public RegAlloc(LRoot root) {
        this.root = root;
        preColored = new HashSet<>(root.phyRegs());
    }

    private HashSet<Reg> adjacent(Reg n) {
        HashSet<Reg> ret = currentDAG.adjacent(n);
        ret.removeAll(selectStack);
        ret.removeAll(coalescedNodes);
        return ret;
    }
    private HashSet<Reg> adjacent(Reg a, Reg b) {
        HashSet<Reg> ret = currentDAG.adjacent(a);
        ret.addAll(currentDAG.adjacent(b));
        ret.removeAll(selectStack);
        ret.removeAll(coalescedNodes);
        return ret;
    }
    private HashSet<Mv> nodeMoves(Reg n) {
        HashSet<Mv> ret = new HashSet<>(workListMv);
        ret.addAll(activeMoves);
        ret.retainAll(n.moveList);
        return ret;
    }
    private boolean moveRelated(Reg n) {
        return !nodeMoves(n).isEmpty();
    }
    private void decrementDegree(Reg m) {
        int d = m.degree;
        --m.degree;
        if (d == K) {
            HashSet<Reg> nodes = adjacent(m);
            nodes.add(m);
            enableMoves(nodes);
            spillWorkList.remove(m);
            if (moveRelated(m)) freezeWorkList.add(m);
            else simplifyWorkList.add(m);
        }
    }
    private void enableMoves(HashSet<Reg> nodes) {
        //if nothing in active moves, it is not needed
        nodes.forEach(node -> nodeMoves(node).forEach(move -> {
            if (activeMoves.contains(move)) {
                activeMoves.remove(move);
                workListMv.add(move);
            }
        }));
    }
    private void addWorkList(Reg node) {
        if (!preColored.contains(node) && !moveRelated(node) && node.degree < K) {
            freezeWorkList.remove(node);
            simplifyWorkList.add(node);
        }
    }
    private boolean ok(Reg a, Reg b) {
        return a.degree < K || preColored.contains(a) || currentDAG.connected(a, b);
    }
    private boolean checkOK(Reg u, Reg v) {
        for (Reg t : adjacent(v)) if (!ok(t, u)) return false;
        return true;
    }
    private boolean conservative(HashSet<Reg> nodes) {
        return (int) nodes.stream().filter(n -> n.degree >= K).count() < K;
    }
    private Reg getAlias(Reg n) {
        if (coalescedNodes.contains(n)) return getAlias(n.alias);
        else return n;
    }
    private void combine(Reg u, Reg v) {
        if (freezeWorkList.contains(v)) freezeWorkList.remove(v);
        else spillWorkList.remove(v);
        coalescedNodes.add(v);
        v.alias = u;
        u.moveList.addAll(v.moveList);
        enableMoves(new HashSet<>(Collections.singletonList(v)));
        adjacent(v).forEach(t -> {
            currentDAG.addEdge(t, u);
            decrementDegree(t);
        });
        if (u.degree >= K && freezeWorkList.contains(u)) {
            freezeWorkList.remove(u);
            spillWorkList.add(u);
        }
    }
    private void freezeMoves(Reg u) {
        nodeMoves(u).forEach(mv -> {
            Reg x = mv.dest(), y = mv.origin(), v;
            if (getAlias(u) == getAlias(y)) v = getAlias(x);
            else v = getAlias(y);
            activeMoves.remove(mv);
            frozenMoves.add(mv);
            if (freezeWorkList.contains(v) && nodeMoves(v).isEmpty()) {
                freezeWorkList.remove(v);
                simplifyWorkList.add(v);
            }
        });
    }

    private void simplify() {
        Reg node = simplifyWorkList.iterator().next();
        simplifyWorkList.remove(node);
        selectStack.push(node);
        adjacent(node).forEach(this::decrementDegree);
    }
    private void coalesce() {
        Mv move = workListMv.iterator().next();
        Reg x, y, u, v;
        x = getAlias(move.dest());
        y = getAlias(move.origin());
        if (preColored.contains(y)) {
            u = y;
            v = x;
        }else {
            u = x;
            v = y;
        }
        workListMv.remove(move);
        if (u == v) {
            coalescedMoves.add(move);
            addWorkList(u);
        } else if (preColored.contains(v) || currentDAG.connected(u, v)) {
            constrainedMoves.add(move);
            addWorkList(u);
            addWorkList(v);
        } else if ((preColored.contains(u) && checkOK(u, v)) ||
                   (!preColored.contains(u) && conservative(adjacent(u,v)))) {
            coalescedMoves.add(move);
            combine(u, v);
            addWorkList(u);
        } else activeMoves.add(move);
    }
    private void freeze() {
        Reg u = freezeWorkList.iterator().next();
        freezeWorkList.remove(u);
        simplifyWorkList.add(u);
        freezeMoves(u);
    }
    private Reg getSpill() {
        int min = -1;
        Reg ret = null;
        for (Reg reg : spillWorkList) {
            if ((min == -1 || reg.weight / reg.degree < min) && !spillIntroduce.contains(reg)) {
                ret = reg;
                min = reg.weight / reg.degree;
            }
        }
        return ret;
    }
    private void selectSpill() {
        Reg m = getSpill();
        spillWorkList.remove(m);
        simplifyWorkList.add(m);
        freezeMoves(m);
    }
    private void assignColors() {
        while (!selectStack.isEmpty()) {
            Reg n = selectStack.pop();
            ArrayList<PhyReg> okColors = root.assignableRegs();
            HashSet<Reg> colored = new HashSet<>(coloredNodes);
            colored.addAll(preColored);
            currentDAG.adjacent(n).forEach(w -> {
                if (colored.contains(getAlias(w))) okColors.remove(getAlias(w).color);
            });
            if (okColors.isEmpty()) spilledNodes.add(n);
            else {
                coloredNodes.add(n);
                n.color = okColors.get(0);
            }
        }
        coalescedNodes.forEach(n -> n.color = getAlias(n).color);
    }
    private void rewrite() {
        HashSet<Reg> newTemps = new HashSet<>();
        spilledNodes.forEach(v -> {
            v.stackOffset = new SLImm(-1 * stackLength - 4); // if stackOffset is 0, it is actually store 4(sp)
            stackLength += 4;
        });
        currentFn.blocks().forEach(block -> block.instructions().forEach(inst -> {
            if (inst.dest() != null && inst.dest() instanceof VirtualReg) getAlias(inst.dest());
        }));
        currentFn.blocks().forEach(block -> {
            for (ListIterator<RISCInst> iter = block.instructions().listIterator(); iter.hasNext();) {
                RISCInst inst = iter.next();
                boolean addAfter = false;
                for (Reg reg : inst.uses()) {
                    if (reg.stackOffset != null) {
                        if (inst.dest() == reg) {
                            VirtualReg tmp = new VirtualReg(((VirtualReg) reg).size(), -1);
                            spillIntroduce.add(tmp);
                            inst.replaceUse(reg, tmp);
                            inst.replaceDest(reg, tmp);
                            iter.previous();
                            iter.add(new Ld(root.getPhyReg(2), tmp, reg.stackOffset, tmp.size(), block));
                            iter.next();
                            iter.add(new St(root.getPhyReg(2), tmp, reg.stackOffset, tmp.size(), block));
                            iter.previous();    //go back to the inst, now ld->inst -(iter)-> st
                            addAfter = true;
                            newTemps.add(tmp);
                        }
                        else {
                            if (inst instanceof Mv && ((Mv)inst).origin() == reg && inst.dest().stackOffset == null) {
                                iter.remove();  //safe for only one reg
                                iter.add(new Ld(root.getPhyReg(2), inst.dest(), reg.stackOffset, ((VirtualReg)reg).size(), block));
                            } else {
                                VirtualReg tmp = new VirtualReg(((VirtualReg) reg).size(), -1);
                                spillIntroduce.add(tmp);
                                iter.previous();
                                iter.add(new Ld(root.getPhyReg(2), tmp, reg.stackOffset, tmp.size(), block));
                                inst.replaceUse(reg, tmp);
                                iter.next();
                                newTemps.add(tmp);
                            }
                        }
                    }
                }
                if (inst.dest() != null && inst.dest().stackOffset != null && !inst.uses().contains(inst.dest())) {
                    VirtualReg dest = (VirtualReg)inst.dest();
                    if (inst instanceof Mv && ((Mv) inst).origin() instanceof VirtualReg
                            && ((Mv) inst).origin().stackOffset == null) {
                        iter.remove();
                        //safe since doing only once, and notice two iterator remove in different condition(origin.sOff==null)
                        iter.add(new St(root.getPhyReg(2), ((Mv) inst).origin(), dest.stackOffset, dest.size(), block));
                    } else {
                        VirtualReg tmp = new VirtualReg(dest.size(), -1);
                        spillIntroduce.add(tmp);
                        inst.replaceDest(dest, tmp);
                        iter.add(new St(root.getPhyReg(2), tmp, dest.stackOffset, dest.size(), block));
                        newTemps.add(tmp);
                    }
                }
                if (addAfter) iter.next();  //jump through the store
            }
        });
        spillIntroduce.addAll(newTemps);
        spilledNodes.clear();
        currentDAG.initial().clear();
        currentDAG.initial().addAll(coloredNodes);
        currentDAG.initial().addAll(coalescedNodes);
        currentDAG.initial().addAll(newTemps);
        coloredNodes.clear();
        coalescedNodes.clear();
    }
    private void runForFn(LFn fn){
        //makeWorkList
        spillWorkList.clear();
        freezeWorkList.clear();
        simplifyWorkList.clear();
        new LivenessAnal(root, fn).runForFn();
        workListMv = fn.workListMv();
        currentDAG = currentFn.dag();
        currentDAG.initCollect = false;
        currentDAG.initial().forEach(node -> {
            if (node.degree >= K) spillWorkList.add(node);
            else if (moveRelated(node)) freezeWorkList.add(node);
            else simplifyWorkList.add(node);
        });
        //do simplify&spill
        do{
            if (!simplifyWorkList.isEmpty()) simplify();
            else if (!workListMv.isEmpty()) coalesce();
            else if (!freezeWorkList.isEmpty()) freeze();
            else if (!spillWorkList.isEmpty()) selectSpill();
        }
        while (!(freezeWorkList.isEmpty() && simplifyWorkList.isEmpty()
                && spillWorkList.isEmpty() && workListMv.isEmpty()));
        assignColors();
        if (!spilledNodes.isEmpty()) {
            rewrite();
            runForFn(fn);
        } else {
            coloredNodes.clear();
            coalescedNodes.clear();
        }
    }
    private void useDefCollect(LFn fn) {
        fn.blocks().forEach(block ->{
            int weight = block.loopDepth == 0 ? 1 : 10 * block.loopDepth;
            block.instructions().forEach(inst -> {
                inst.uses().forEach(reg -> reg.weight += weight);
                if (inst.dest() != null) inst.dest().weight += weight;
            });
        });
    }

    private void subtleModify() {
        currentFn.blocks().forEach(block -> block.instructions().forEach(inst -> inst.stackLengthAdd(stackLength)));
        currentFn.blocks().forEach(block -> block.instructions().removeIf(inst ->
                inst instanceof Mv && (((Mv) inst).origin().color == inst.dest().color))
        );
    }

    public void run() {
        root.functions().forEach(this::useDefCollect);
        root.functions().forEach(fn -> {
            stackLength = 0;
            currentFn = fn;
            runForFn(fn);
            stackLength += fn.paramOffset;
            if (stackLength % 16 != 0) stackLength = (stackLength / 16 + 1) * 16;
            subtleModify();
        });
    }
}
