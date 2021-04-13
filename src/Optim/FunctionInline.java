package Optim;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.*;
import MIR.IRoperand.Operand;
import MIR.Root;
import Util.DomGen;
import Util.MIRMirror;

import java.util.*;

public class FunctionInline extends Pass{

    private boolean newRound = false, change = false;
    private Root irRoot;
    private HashSet<Function> cannotInlineFun = new HashSet<>();
    private HashMap<Call, Function> canUnFold = new HashMap<>();
    private boolean forceInline;

    public FunctionInline(Root irRoot, boolean forceInline) {
        super();
        this.irRoot = irRoot;
        this.forceInline = forceInline;
    }

    private ArrayList<Function> DFSStack = new ArrayList<>();
    private HashSet<Function> visited = new HashSet<>();
    private HashMap<Function, HashSet<Function>> caller = new HashMap<>();
    private void init() {
        irRoot.functions().forEach((name, fn) -> caller.put(fn, new HashSet<>()));
    }
    private void DFS(Function it) {
        visited.add(it);
        DFSStack.add(it);
        boolean inRing = false;
        for (Function fn : DFSStack) {
            if (it.isCallee(fn)) inRing = true;
            if (inRing) cannotInlineFun.add(fn);
        }
        it.callFunction.forEach(callee -> {
            if (!visited.contains(callee)) DFS(callee);
            caller.get(callee).add(it);
        });
        DFSStack.remove(DFSStack.size() - 1);
    }
    private void inlineJudge() {
        irRoot.functions().forEach((name, func) -> {
            if (!visited.contains(func)) DFS(func);
        });
        visited.clear();
    }

    private void unfold(Call inst, Function fn) {
        MIRMirror mirror = new MIRMirror();
        HashMap<Operand, Operand> mirrorOpr = mirror.opMirror();
        HashMap<IRBlock, IRBlock> mirrorBlocks = new HashMap<>();
        Function callee = inst.callee();
        IRBlock currentBlock = inst.block();

        int paramSize = inst.params().size();
        for (int i = 0;i < paramSize;++i) {
            Operand callParam = inst.params().get(i),
                    virtualParam = callee.params().get(i);
            mirrorOpr.put(virtualParam, callParam);
        }
        //copy all blocks of callee
        HashSet<IRBlock> copied = new HashSet<>(callee.blocks);
        for (IRBlock block : copied) {
            IRBlock mirrorBlock =  new IRBlock(block.name + "_inline");
            mirrorBlocks.put(block, mirrorBlock);
        }
        fn.blocks.addAll(mirrorBlocks.values());
        mirror.setBlockMirror(mirrorBlocks);
        //copy all instructions
        copied.forEach(block -> {
            IRBlock mirB = mirror.blockMir(block);
            for(Inst instr = block.headInst; instr != null; instr = instr.next)
                instr.addMirror(mirB, mirror);
            block.PhiInst.forEach((reg, instr) -> instr.addMirror(mirB, mirror));
        });

        //split the current block into two parts(before the call and after it)
        IRBlock laterBlock = new IRBlock(currentBlock.name + "_split");
        currentBlock.splitTo(laterBlock, inst);

        //merge the exit block with the laterBlock one of current block,
        //no need to remove laterBlock since it is never added
        IRBlock exitBlock = mirrorBlocks.get(callee.exitBlock);
        assert exitBlock.terminator() instanceof Return;
        Return retInst = (Return) exitBlock.terminator();
        if (retInst.value() != null) inst.dest().replaceAllUseWith(retInst.value());
        exitBlock.removeTerminator();

        exitBlock.mergeBlock(laterBlock);
        //merge the entry block with the former one of current block
        IRBlock mirEntry = mirrorBlocks.get(callee.entryBlock);
        fn.removeBlock(mirEntry);  //no need to remove later block: not added into fn
        currentBlock.mergeBlock(mirEntry);
        if (fn.exitBlock == currentBlock && mirEntry != exitBlock) fn.setExitBlock(exitBlock);
        inst.removeSelf(false);
    }
    private void checkInline(Function fn) {
        fn.blocks.forEach(block -> {
            for (Inst inst = block.headInst; inst != null; inst = inst.next)
            if (inst instanceof Call && !cannotInlineFun.contains(((Call)inst).callee())) {
                canUnFold.put((Call) inst, fn);
                newRound = true;
            }
        });
    }
    int round = 0;
    private static int memBound = 10;
    public boolean recheck_inline_fails;
    private void recheck_inline() {
        irRoot.functions().forEach((name, fn) -> {
            if (!cannotInlineFun.contains(fn)) {
                int memCnt = 0;
                for (IRBlock block : fn.blocks) {
                    for (Inst inst = block.headInst; inst != null; inst = inst.next) {
                        if (inst instanceof Load || inst instanceof Store) {
                            memCnt++;
                            if (memCnt > memBound) {
                                cannotInlineFun.add(fn);
                                recheck_inline_fails = true;
                                break;
                            }
                        }
                    }
                }
            }
        });
    }
    private void reorder_canUnFold() {
        // TODO
    }

    private void inlining() {
        int inline_size_factor = 1000;
        int inline_round_bound = 20;
        do {
            round++;
            newRound = false;
            canUnFold.clear();
            irRoot.functions().forEach((name, func) -> checkInline(func));
            reorder_canUnFold();
            if (canUnFold.size() > irRoot.functions().size() * inline_size_factor
                || round > inline_round_bound) {
                recheck_inline_fails = true;
                break;
            } else {
                canUnFold.forEach((c, f) -> {
                    Function callee = c.callee();
                    if (lineNumber.get(callee) < bound) {
                        lineNumber.put(f, lineNumber.get(f) + lineNumber.get(callee));
                        unfold(c, f);
                    } else {
                        recheck_inline_fails = true;
                    }
                });
            }
            recheck_inline();
            change = change || newRound;
        } while (newRound);
        if (!recheck_inline_fails)
            for (Iterator<Map.Entry<String, Function>> iter = irRoot.functions().entrySet().iterator(); iter.hasNext();) {
                Map.Entry<String, Function> entry = iter.next();
                Function fn = entry.getValue();
                if (!cannotInlineFun.contains(fn)) iter.remove();
                else if (caller.get(fn).size() == 1 && caller.get(fn).contains(fn)) iter.remove();
            }   //remove inlined function
    }

    private static int bound = 150;
    private HashMap<Function, Integer> lineNumber = new HashMap<>();
    @Override
    public boolean run() {
        irRoot.functions().forEach((name, fn) -> {
            int cnt = 0;
            for (IRBlock block : fn.blocks) {
                for (Inst inst = block.headInst; inst != null; inst = inst.next) cnt++;
            }
            lineNumber.put(fn, cnt);
        });
        if (!forceInline){
            newRound = change = false;
            recheck_inline_fails = false;
            visited.addAll(irRoot.builtinFunctions().values());
            cannotInlineFun.addAll(visited);
            cannotInlineFun.add(irRoot.getFunction("main"));
            init();
            inlineJudge();
            inlining();
            irRoot.functions().forEach((name, fn) -> new DomGen(fn).runForFn());
            return change;
        } else {
            cannotInlineFun.addAll(irRoot.builtinFunctions().values());
            cannotInlineFun.add(irRoot.getFunction("main"));
            do{
                canUnFold.clear();
                irRoot.functions().forEach((name, fn) -> fn.blocks.forEach(block -> {
                    for (Inst inst = block.headInst; inst != null; inst = inst.next)
                        if (inst instanceof Call) {
                            Call ca = (Call) inst;
                            Function callee = ca.callee();
                            if (!cannotInlineFun.contains(callee) && lineNumber.get(fn) < bound){
                                canUnFold.put(ca, fn);
                                lineNumber.put(fn, lineNumber.get(fn) + lineNumber.get(callee));
                            }
                        }
                }));
                canUnFold.forEach(this::unfold);
            } while(!canUnFold.isEmpty());
            irRoot.functions().forEach((name, fn) -> new DomGen(fn).runForFn());
            return true;
        }
    }
}
