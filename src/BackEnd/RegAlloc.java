package BackEnd;

import Assemb.*;
import Assemb.LOperand.*;
import Assemb.RISCInst.*;

import java.util.*;

public class RegAlloc {
    private static class edge {
        Reg u, v;
        public edge(Reg u, Reg v){
            this.u = u;
            this.v = v;
        }
        @Override
        public boolean equals(Object o) {
            return (o instanceof edge && ((edge)o).u == u && ((edge)o).v == v);
        }
        @Override
        public int hashCode() {
            return u.toString().hashCode() ^ v.toString().hashCode();
        }
    }

    private HashSet<Reg> preColored;
    private LRoot root;
    private LFn currentFn;
    private HashSet<Mv> workListMoves = new LinkedHashSet<>(),
                        activeMoves = new HashSet<>(),
                        coalescedMoves = new HashSet<>(),
                        constrainedMoves = new HashSet<>(),
                        frozenMoves = new HashSet<>();
    private HashSet<Reg> spillWorkList = new LinkedHashSet<>(),
                         freezeWorkList = new HashSet<>(),
                         simplifyWorkList = new LinkedHashSet<>(),
                         spilledNodes = new LinkedHashSet<>(),
                         coloredNodes = new HashSet<>(),
                         coalescedNodes = new LinkedHashSet<>(),
                         spillIntroduce = new HashSet<>(),
                         initial = new LinkedHashSet<>();
    private HashSet<edge> adjSet = new HashSet<>();
    private Stack<Reg> selectStack = new Stack<>();
    private int stackLength = 0;
    private int K;

    private static int inf = 1147483640;

    public RegAlloc(LRoot root) {
        this.root = root;
        preColored = new HashSet<>(root.phyRegs());
        K = root.assignableRegs().size();
    }

    private void useDefCollect(LFn fn) {
        fn.blocks().forEach(block ->{
            double weight = Math.pow(10, block.loopDepth);
            for (RISCInst inst = block.head; inst != null; inst = inst.next) {
                inst.uses().forEach(reg -> reg.weight += weight);
                if (inst.dest() != null) inst.dest().weight += weight;
            }
        });
    }

    private void init() {
        initial.clear();
        simplifyWorkList.clear();
        freezeWorkList.clear();
        spillWorkList.clear();
        spilledNodes.clear();
        coalescedNodes.clear();
        coloredNodes.clear();
        selectStack.clear();

        coalescedMoves.clear();
        constrainedMoves.clear();
        frozenMoves.clear();
        activeMoves.clear();
        workListMoves.clear();

        adjSet.clear();
        currentFn.blocks().forEach(block -> {
            for (RISCInst inst = block.head; inst != null; inst = inst.next){
                initial.addAll(inst.defs());
                initial.addAll(inst.uses());
            }
        });
        initial.removeAll(preColored);
        initial.forEach(reg -> {
            reg.weight = 0;
            reg.alias = null;
            reg.color = null;
            reg.degree = 0;
            reg.adjList.clear();
            reg.moveList.clear();
        });

        preColored.forEach(reg -> {
            reg.degree = inf;
            reg.color = (PhyReg)reg;
            reg.alias = null;
            reg.adjList.clear();
            reg.moveList.clear();
        });
        useDefCollect(currentFn);
    }
    private void build() {
        currentFn.blocks().forEach(block -> {
            HashSet<Reg> currentLive = new HashSet<>(block.liveOut);
            for (RISCInst inst = block.tail; inst != null; inst = inst.previous) {
                if (inst instanceof Mv) {
                    currentLive.removeAll(inst.uses());
                    HashSet<Reg> mvAbout = inst.uses();
                    mvAbout.addAll(inst.defs());   //def of move is only the inst.dest()
                    for (Reg reg : mvAbout) reg.moveList.add((Mv) inst);
                    workListMoves.add((Mv) inst);
                }
                HashSet<Reg> defs = inst.defs();
                currentLive.add(root.getPhyReg(0));
                currentLive.addAll(defs);
                defs.forEach(def -> currentLive.forEach(reg -> addEdge(reg, def)));

                currentLive.removeAll(defs);
                currentLive.addAll(inst.uses());
            }
        });
    }

    private void addEdge(Reg u, Reg v) {
        if (u != v && !adjSet.contains(new edge(u, v))){
            adjSet.add(new edge(u, v));
            adjSet.add(new edge(v, u));
            if (!(preColored.contains(u))) {
                u.adjList.add(v);
                ++u.degree;
            }
            if (!(preColored.contains(v))) {
                v.adjList.add(u);
                ++v.degree;
            }
        }
    }
    private HashSet<Reg> adjacent(Reg n) {
        HashSet<Reg> ret = new HashSet<>(n.adjList);
        ret.removeAll(selectStack);
        ret.removeAll(coalescedNodes);
        return ret;
    }
    private HashSet<Reg> adjacent(Reg u, Reg v) {
        HashSet<Reg> ret = new HashSet<>(adjacent(u));
        ret.addAll(adjacent(v));
        return ret;
    }

    private HashSet<Mv> nodeMoves(Reg n) {
        HashSet<Mv> ret = new HashSet<>(workListMoves);
        ret.addAll(activeMoves);
        ret.retainAll(n.moveList);
        return ret;
    }
    private boolean moveRelated(Reg n) {
        return !nodeMoves(n).isEmpty();
    }
    private void simplify() {
        Reg node = simplifyWorkList.iterator().next();
        simplifyWorkList.remove(node);
        selectStack.push(node);
        adjacent(node).forEach(this::decrementDegree);
    }
    private void decrementDegree(Reg m) {
        int d = m.degree;
        m.degree--;
        if (d == K) {
            HashSet<Reg> nodes = new HashSet<>(adjacent(m));
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
                workListMoves.add(move);
            }
        }));
    }
    private void coalesce() {
        Mv move = workListMoves.iterator().next();
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
        workListMoves.remove(move);
        if (u == v) {
            coalescedMoves.add(move);
            addWorkList(u);
        } else if (preColored.contains(v) || adjSet.contains(new edge(u, v))) {
            constrainedMoves.add(move);
            addWorkList(u);
            addWorkList(v);
        } else {
            if ((preColored.contains(u) && checkOK(u, v)) ||
                (!preColored.contains(u) && conservative(adjacent(u, v)))) {
                coalescedMoves.add(move);
                combine(u, v);
                addWorkList(u);
            } else activeMoves.add(move);
        }
    }
    private void addWorkList(Reg node) {
        if (!preColored.contains(node) && !moveRelated(node) && node.degree < K) {
            freezeWorkList.remove(node);
            simplifyWorkList.add(node);
        }
    }
    private boolean ok(Reg t, Reg r) {
        return t.degree < K || preColored.contains(t) || adjSet.contains(new edge(t, r));
    }
    private boolean checkOK(Reg u, Reg v) {
        boolean ret = true;
        for (Reg t : adjacent(v)) ret &= ok(t, u);
        return ret;
    }
    private boolean conservative(HashSet<Reg> nodes) {
        int count = 0;
        for (Reg node : nodes) {
            if (node.degree >= K) ++count;
        }
        return count < K;
    }
    private Reg getAlias(Reg n) {
        if (coalescedNodes.contains(n)) {
            Reg alias = getAlias(n.alias);
            n.alias = alias;
            return alias;
        }
        else return n;
    }
    private void combine(Reg u, Reg v) {
        if (freezeWorkList.contains(v)) freezeWorkList.remove(v);
        else spillWorkList.remove(v);
        coalescedNodes.add(v);
        v.alias = u;
        u.moveList.addAll(v.moveList);
        HashSet<Reg> tmp = new HashSet<>();tmp.add(v);
        enableMoves(tmp);
        adjacent(v).forEach(t -> {
            addEdge(t, u);
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
            if (v.degree < K && nodeMoves(v).isEmpty()) {
                freezeWorkList.remove(v);
                simplifyWorkList.add(v);
            }
        });
    }
    private void freeze() {
        Reg u = freezeWorkList.iterator().next();
        freezeWorkList.remove(u);
        simplifyWorkList.add(u);
        freezeMoves(u);
    }
    private Reg getSpill() {
        Reg min = null;
        double minCost = 0;
        Iterator<Reg> iter = spillWorkList.iterator();
        while(iter.hasNext()) {
            min = iter.next();
            minCost = min.weight / min.degree;
            if (!spillIntroduce.contains(min)) break;
        }
        while(iter.hasNext()) {
            Reg reg = iter.next();
            if (!spillIntroduce.contains(reg) && (reg.weight / reg.degree < minCost)) {
                min = reg;
                minCost = reg.weight / reg.degree;
            }
        }
        return min;
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
            ArrayList<PhyReg> okColors = new ArrayList<>(root.assignableRegs());
            HashSet<Reg> colored = new HashSet<>(coloredNodes);
            colored.addAll(preColored);
            n.adjList.forEach(w -> {
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
        spilledNodes.forEach(v -> {
            v.stackOffset = new SLImm(-1 * stackLength - 4); // if stackOffset is 0, it is actually store 4(sp)
            stackLength += 4;
        });
        currentFn.blocks().forEach(block -> {
            for (RISCInst inst = block.head; inst != null; inst = inst.next)
                if (inst.dest() != null && inst.dest() instanceof VirtualReg)
                    getAlias(inst.dest());
        });
        currentFn.blocks().forEach(block -> {
            for (RISCInst inst = block.head; inst != null; inst = inst.next) {
                for (Reg reg : inst.uses()) {
                    if (reg.stackOffset != null) {
                        if (inst.defs().contains(reg)) {
                            VirtualReg tmp = new VirtualReg(((VirtualReg) reg).size(), ++currentFn.cnt);
                            spillIntroduce.add(tmp);
                            inst.replaceUse(reg, tmp);
                            inst.replaceDest(reg, tmp);
                            inst.addPre(new Ld(root.getPhyReg(2), tmp, reg.stackOffset, tmp.size(), block));
                            inst.addPost(new St(root.getPhyReg(2), tmp, reg.stackOffset, tmp.size(), block));
                        }
                        else {
                            if (inst instanceof Mv && ((Mv)inst).origin() == reg && inst.dest().stackOffset == null) {//safe for only one reg
                                RISCInst replace = new Ld(root.getPhyReg(2), inst.dest(), reg.stackOffset, ((VirtualReg)reg).size(), block);
                                inst.replaceBy(replace);
                                inst = replace;
                            } else {
                                VirtualReg tmp = new VirtualReg(((VirtualReg) reg).size(), ++currentFn.cnt);
                                spillIntroduce.add(tmp);
                                inst.addPre(new Ld(root.getPhyReg(2), tmp, reg.stackOffset, tmp.size(), block));
                                inst.replaceUse(reg, tmp);
                            }
                        }
                    }
                }
                for (Reg def : inst.defs()) {
                    if (def.stackOffset != null) {
                        if (!inst.uses().contains(def)) {
                            if (inst instanceof Mv && ((Mv) inst).origin().stackOffset == null){
                                RISCInst replace = new St(root.getPhyReg(2), ((Mv) inst).origin(), def.stackOffset, ((VirtualReg)def).size(), block);
                                inst.replaceBy(replace);
                                inst = replace;
                            }
                            else {
                                VirtualReg tmp = new VirtualReg(((VirtualReg)def).size(), ++currentFn.cnt);
                                spillIntroduce.add(tmp);
                                inst.replaceDest(def, tmp);
                                inst.addPost(new St(root.getPhyReg(2), tmp, def.stackOffset, ((VirtualReg)def).size(), block));
                            }
                        }
                    }
                }
            }
        });
    }
    private void runForFn(LFn fn){
        //makeWorkList
        boolean done;
        do{
            init();
            new LivenessAnalysis(fn).runForFn();
            build();
            //make workList
            initial.forEach(node -> {
                if (node.degree >= K) spillWorkList.add(node);
                else if (moveRelated(node)) freezeWorkList.add(node);
                else simplifyWorkList.add(node);
            });
            //do simplify&spill
            do{
                if (!simplifyWorkList.isEmpty()) simplify();
                else if (!workListMoves.isEmpty()) coalesce();
                else if (!freezeWorkList.isEmpty()) freeze();
                else if (!spillWorkList.isEmpty()) selectSpill();
            }
            while (!(freezeWorkList.isEmpty() && simplifyWorkList.isEmpty()
                    && spillWorkList.isEmpty() && workListMoves.isEmpty()));
            assignColors();
            if (!spilledNodes.isEmpty()) {
                rewrite();
                done = false;
            } else done = true;
        }
        while(!done);
    }

    private void subtleModify() {
        currentFn.blocks().forEach(block -> {
            for (RISCInst inst = block.head; inst != null; inst = inst.next)
                inst.stackLengthAdd(stackLength);
        });
        currentFn.blocks().forEach(block -> {
            for (RISCInst inst = block.head; inst != null; inst = inst.next){
                if (inst instanceof Mv && (((Mv) inst).origin().color == inst.dest().color))
                    inst.removeSelf();
            }
        }
        );
        HashSet<LIRBlock> canMix = new HashSet<>();
        currentFn.blocks().forEach(block -> {
            if (block.head instanceof Jp) canMix.add(block);
        });
        canMix.forEach(block -> {
            LIRBlock suc = block;
            do {
                suc = ((Jp)suc.head).destBlock();
            }
            while (canMix.contains(suc));
            for (LIRBlock pre : block.precursors) {
                for (RISCInst inst = pre.head; inst != null; inst = inst.next) {
                    if (inst instanceof Jp) ((Jp) inst).replaceDest(block, suc);
                    else if (inst instanceof Br) ((Br) inst).replaceDest(block, suc);
                    else if (inst instanceof Bz) ((Bz) inst).replaceDest(block, suc);
                }
                pre.successors.remove(block);
                pre.successors.add(suc);
            }
            suc.precursors.remove(block);
            suc.precursors.addAll(block.precursors);
            if (currentFn.entryBlock == block) currentFn.entryBlock = suc;
        });
        currentFn.blocks().removeAll(canMix);
    }
    private void reschedule() {
//        HashSet<LIRBlock> visited = new HashSet<>();
//        Queue<LIRBlock> queue = new LinkedList<>();
//        queue.add(currentFn.entryBlock);
//        do {
//            LIRBlock currentBlock = queue.poll();
//            visited.add(currentBlock);
//            assert currentBlock != null;
//            if (currentBlock.tail instanceof Jp) {
//                assert currentBlock.next == null;
//                Jp jp = (Jp) currentBlock.tail;
//                if (!jp.destBlock().hasPrior) {
//                    currentBlock.next = jp.destBlock();
//                    jp.destBlock().hasPrior = true;
////                    currentBlock.tail = jp.previous;
////                    //jp cannot be the only instruction by the one in subtle modify
////                    currentBlock.tail.next = null;
////                    jp.previous = null;
//                }
//            }
//            currentBlock.successors.forEach(suc -> {
//                if (!visited.contains(suc)) queue.offer(suc);
//            });
//        }
//        while(!queue.isEmpty());
    }
    public void run() {
        root.functions().forEach(fn -> {
            stackLength = 0;
            currentFn = fn;
            runForFn(fn);
            stackLength += fn.paramOffset;
            if (stackLength % 16 != 0) stackLength = (stackLength / 16 + 1) * 16;
            subtleModify();
            reschedule();
        });
    }
}
