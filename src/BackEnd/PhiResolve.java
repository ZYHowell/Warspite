package BackEnd;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.Jump;
import MIR.IRinst.Move;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.Root;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class PhiResolve {

    private static class blockPair{
        public IRBlock pre, suc;
        public blockPair(IRBlock pre, IRBlock suc) {
            this.pre = pre;
            this.suc = suc;
        }
    }

    private static class paraCopy {
        public ArrayList<Move> copies = new ArrayList<>();
        public HashMap<Operand, Integer> useMap = new HashMap<>();

        public paraCopy() {}

        public void addMove(Move move) {
            copies.add(move);
            Operand origin = move.origin();
            if (origin instanceof Register)
                if (useMap.containsKey(origin)) useMap.put(origin, useMap.get(origin) + 1);
                else useMap.put(origin, 1);
        }
    }

    private Root irRoot;

    public PhiResolve(Root irRoot) {
        this.irRoot = irRoot;
    }

    private void runForBlock(IRBlock block, paraCopy para) {
        boolean newRound;
        do {
            boolean hasMore = false;
            for (Iterator<Move> iter = para.copies.iterator(); iter.hasNext(); ) {
                Move inst = iter.next();
                if (!para.useMap.containsKey(inst.dest())) {
                    iter.remove();
                    if (inst.origin() instanceof Register) {
                        int num = para.useMap.get(inst.origin()) - 1;
                        if (num > 0) para.useMap.put(inst.origin(), num);
                        else para.useMap.remove(inst.origin());
                    }

                    block.addInstTerminated(new Move(inst.origin(), inst.dest(), block, true));
                    hasMore = true;
                }
            }
            int size = para.copies.size();
            if (!hasMore) { //all in rings
                for (int i = 0; i < size; ++i) {
                    Move inst = para.copies.get(i);
                    if (inst.origin() != inst.dest()) {
                        Register mirror = new Register(inst.origin().type(), "mirror_" + inst.origin());
                        block.addInstTerminated(new Move(inst.origin(),
                                mirror, block, true));
                        //replace all origin in remained copies by mirror then
                        para.useMap.remove(inst.origin());
                        para.copies.forEach(copy -> copy.ReplaceUseWith((Register) inst.origin(), mirror));
                        break;
                    }
                }
            }
            newRound = false;
            for (int i = 0;i < size;++i) {
                Move inst = para.copies.get(i);
                if (inst.origin() != inst.dest()) {
                    newRound = true;
                    break;
                }
            }
        } while(newRound);
    }
    private void runForFn(Function fn) {
        //split critical edge
        HashSet<blockPair> critical = new HashSet<>();
        fn.blocks().forEach(block -> {
            if (block.successors().size() > 1) {
                block.successors().forEach(suc -> {
                    if (suc.precursors().size() > 1) critical.add(new blockPair(block, suc));
                });
            }
        });
        critical.forEach(pair -> {
            IRBlock pre = pair.pre, suc = pair.suc;
            IRBlock mid = new IRBlock("mid");
            fn.addBlock(mid);

            mid.addTerminator(new Jump(suc, mid));
            //change the phi info in suc
            suc.phiInst().forEach((reg, phi) -> {
                int size = phi.blocks().size();
                for (int i = 0;i < size;++i)
                    if (phi.blocks().get(i) == pre) phi.blocks().set(i, mid);
            });
            //replace successor in the last place to avoid remove the info in phi
            pre.replaceSuccessor(suc, mid);
        });
        //get parallel copy
        HashMap<IRBlock, paraCopy> copyMap = new HashMap<>();
        fn.blocks().forEach(block -> copyMap.put(block, new paraCopy()));
        fn.blocks().forEach(block -> block.phiInst().forEach((reg, phi) -> {
            int size = phi.blocks().size();
            for (int i = 0;i < size;++i) {
                IRBlock pre = phi.blocks().get(i);
                Operand value = phi.values().get(i);
                copyMap.get(pre).addMove(new Move(value, reg, pre, false));
            }
        }));
        copyMap.forEach(this::runForBlock);
    }

    public void run() {
        irRoot.functions().forEach((name, fn) -> runForFn(fn));
    }
}
