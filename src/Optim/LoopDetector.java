package Optim;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.Jump;
import MIR.IRinst.Phi;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import Util.MIRLoop;

import java.util.*;

//mention that it is hard to add/remove blocks in MIRLoop,
//so do this before/after MIRLoop is created/used up
public class LoopDetector {

    private Function fn;
    private boolean addPreHeader;
    private HashMap<IRBlock, MIRLoop> loopMap = new HashMap<>();
    private HashSet<MIRLoop> rootLoops = new HashSet<>();
    private HashSet<IRBlock> visited = new HashSet<>();
    private Stack<MIRLoop> loopStack = new Stack<>();

    public LoopDetector(Function fn, boolean addPreHeader) {
        this.fn = fn;
        this.addPreHeader = addPreHeader;
    }

    private void collectLoop(IRBlock tail, IRBlock head) {
        if (!loopMap.containsKey(head)) {
            MIRLoop loop = new MIRLoop();
            loopMap.put(head, loop);
        }
        loopMap.get(head).addTail(tail);
    }
    private void addPreHeader(IRBlock head, MIRLoop loop) {
        ArrayList<IRBlock> precursors = new ArrayList<>(head.precursors);
        precursors.removeAll(loop.tails());
        if (precursors.size() == 1) loop.setPreHead(precursors.get(0));
        else {
            IRBlock preHead = new IRBlock("preHead of " + head.name);
            fn.blocks.add(preHead);
            for (Iterator<Map.Entry<Register, Phi>> iter = head.PhiInst.entrySet().iterator(); iter.hasNext();) {
                Map.Entry<Register, Phi> entry = iter.next();
                Phi phi = entry.getValue(), mirrorPhi = null;
                boolean canRemove = true;

                ArrayList<Operand> values = phi.values();
                ArrayList<IRBlock> blocks = phi.blocks();
                for (int i = 0;i < values.size();++i) {
                    //if in a phi there is someone from tails(always so?)
                    //split it into two parts: a mirror(which will be in preHead) and its origin one
                    if (!loop.tails().contains(blocks.get(i))) {
                        if (mirrorPhi == null) {
                            mirrorPhi = new Phi(new Register(phi.dest().type(),
                                                    "preHead phi of " + phi.dest().name()),
                                                new ArrayList<>(), new ArrayList<>(), preHead);
                            preHead.addPhi(mirrorPhi);
                        }
                        mirrorPhi.addOrigin(values.get(i), blocks.get(i));
                        blocks.remove(i);
                        values.remove(i);
                        --i;
                    } else canRemove = false;
                }

                if (canRemove) {
                    iter.remove();
                    assert mirrorPhi != null;
                    phi.dest().replaceAllUseWith(mirrorPhi.dest());
                } else if (mirrorPhi != null) phi.addOrigin(mirrorPhi.dest(), preHead);
            }

            precursors.forEach(precursor -> precursor.replaceSuccessor(head, preHead));
            preHead.addTerminator(new Jump(head, preHead));
            loop.setPreHead(preHead);
        }
    }

    private void getWholeLoop(IRBlock tail, IRBlock head){
        HashSet<IRBlock> inLoopBlocks = new HashSet<>();
        Queue<IRBlock> workList = new LinkedList<>();
        inLoopBlocks.add(head);
        inLoopBlocks.add(tail);
        workList.offer(tail);

        while(!workList.isEmpty()) {
            IRBlock workBlock = workList.poll();
            workBlock.precursors.forEach(pre -> {
                if (!inLoopBlocks.contains(pre)) {
                    workList.offer(pre);
                    inLoopBlocks.add(pre);
                }
            });
        }

        loopMap.get(head).addBlocks(inLoopBlocks);
    }

    private void judgeOutOfLoop(IRBlock block) {
        if (!loopStack.isEmpty())
            while (!loopStack.isEmpty() && !loopStack.peek().blocks().contains(block)) loopStack.pop();
    }
    private void visit(IRBlock block) {
        visited.add(block);
        judgeOutOfLoop(block);
        if (loopMap.containsKey(block)) {
            MIRLoop loop = loopMap.get(block);
            if (loopStack.isEmpty()) rootLoops.add(loop);
            else loopStack.peek().addChild(loop);
            loopStack.push(loop);
        }
        block.loopDepth = loopStack.size();
        block.successors.forEach(suc -> {
           if (!visited.contains(suc)) visit(suc);
        });
    }
    private void treeSpanning() {
        visit(fn.entryBlock);
    }

    public void runForFn() {
        //assume that the dominator relation is correct.
        fn.blocks.forEach(block -> {
            for (IRBlock suc : block.successors)
                if (block.isDomed(suc)) {
                    collectLoop(block, suc);
                    break;
                }
        });
        if (addPreHeader) loopMap.forEach(this::addPreHeader);
        loopMap.forEach((head, loop) -> loop.tails().forEach(tail -> getWholeLoop(tail, head)));
        treeSpanning();
    }

    public HashSet<MIRLoop> rootLoops() {
        return rootLoops;
    }
}
