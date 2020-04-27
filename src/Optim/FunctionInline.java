package Optim;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.*;
import MIR.IRoperand.Operand;
import MIR.Root;
import Util.MIRMirror;

import java.util.*;

public class FunctionInline extends Pass{

    private boolean change = false;
    private Root irRoot;
    private HashSet<Function> cannotInlineFun = new HashSet<>();

    public FunctionInline(Root irRoot) {
        super();
        this.irRoot = irRoot;
    }

    private ArrayList<Function> DFSStack = new ArrayList<>();
    private HashSet<Function> visited = new HashSet<>();
    private HashMap<Function, HashSet<Function>> caller = new HashMap<>();
    private void DFS(Function it) {
        visited.add(it);
        DFSStack.add(it);
        boolean inRing = false;
        for (Function fn : DFSStack) {
            if (it.isCallee(fn)) inRing = true;
            if (inRing) cannotInlineFun.add(fn);
        }
        it.callFunction().forEach(callee -> {
            if (!visited.contains(callee)) DFS(callee);
            caller.get(callee).add(it);
        });
        DFSStack.remove(DFSStack.size() - 1);
    }
    private void inlineJudge() {
        irRoot.functions().forEach((name, func) -> {
            if (!visited.contains(func)) DFS(func);
        });
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
        for (IRBlock block : callee.blocks()) {
            IRBlock mirrorBlock =  new IRBlock(block.name() + "_inline");
            mirrorBlocks.put(block, mirrorBlock);
            fn.addBlock(mirrorBlock);
        }
        mirror.setBlockMirror(mirrorBlocks);
        //copy all instructions
        callee.blocks().forEach(block -> {
            block.instructions().forEach(instr -> instr.addMirror(block, mirror));
            block.phiInst().forEach((reg, instr) -> instr.addMirror(block, mirror));
        });
        //split the current block into two parts(before the call and after it)
        IRBlock laterBlock = new IRBlock(currentBlock.name() + "_split");
        boolean collect = false;
        for (Iterator<Inst> iter = currentBlock.instructions().iterator(); iter.hasNext();) {
            Inst instr = iter.next();
            if (collect) {
                instr.setCurrentBlock(laterBlock);
                if (instr instanceof Branch || instr instanceof Return || instr instanceof Jump){
                    laterBlock.addTerminator(instr);
                    currentBlock.removeTerminator();
                    break;
                }
                else {
                    laterBlock.addInst(instr);
                    iter.remove();
                }
            }
            else if (instr == inst) {
                collect = true;
                iter.remove();
            }
        }
        //merge the entry block with the former one of current block
        fn.removeBlock(mirrorBlocks.get(callee.entryBlock()));
        currentBlock.mergeBlock(mirrorBlocks.get(callee.entryBlock()));
        //merge the exit block with the laterBlock one of current block,
        //no need to remove laterBlock since it is never added
        IRBlock exitBlock = mirrorBlocks.get(callee.exitBlock());
        assert exitBlock.terminator() instanceof Return;
        Return retInst = (Return) exitBlock.terminator();
        if (retInst.value() != null) inst.dest().replaceAllUseWith(retInst.value());
        exitBlock.removeTerminator();

        exitBlock.mergeBlock(laterBlock);
    }
    private void checkInline(Function fn) {
        fn.blocks().forEach(block -> block.instructions().forEach(inst -> {
            if (inst instanceof Call && !cannotInlineFun.contains(((Call)inst).callee())) {
                unfold((Call)inst, fn);
                change = true;
            }
        }));
    }
    private void inlining() {
        irRoot.functions().forEach((name, func) -> checkInline(func));
        for (Iterator<Map.Entry<String, Function>> iter = irRoot.functions().entrySet().iterator(); iter.hasNext();) {
            Map.Entry<String, Function> entry = iter.next();
            Function fn = entry.getValue();
            if (!cannotInlineFun.contains(fn)) iter.remove();
            else if (caller.get(fn).size() == 1 && caller.get(fn).contains(fn)) iter.remove();
            //very rare: its only caller is itself. This is used to speed up anal instead of running
        }   //remove inlined function
    }

    @Override
    public boolean run() {
        change = false;
        /* cannotInlineFun().clear();
         * visited.clear();
         * new MIRFuncCallCollect(irRoot).collect();
         */
        visited.addAll(irRoot.builtinFunctions().values());
        cannotInlineFun.addAll(visited);
        cannotInlineFun.add(irRoot.getFunction("g_main"));
        inlineJudge();
        inlining();
        return change;
    }
}
