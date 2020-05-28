package BackEnd;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.Call;
import MIR.IRinst.Inst;
import MIR.IRinst.Load;
import MIR.IRinst.Store;
import MIR.IRoperand.GlobalReg;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import MIR.IRtype.Pointer;
import MIR.Root;
import Optim.Pass;
import Util.MIRFnGraph;

import java.util.*;

public class globLoc extends Pass {

    private Root irRoot;

    public globLoc(Root irRoot) {
        this.irRoot = irRoot;
        callRelation = new MIRFnGraph(irRoot);
        callRelation.build();
    }

    private HashMap<Function, HashSet<GlobalReg>> uses = new HashMap<>(), defs = new HashMap<>();
    private HashMap<Function, HashSet<Function>> calls = new HashMap<>();

    private MIRFnGraph callRelation;

    private void collect() {
        Queue<Function> queue = new LinkedList<>();
        HashSet<Function> inQueue = new HashSet<>();
        irRoot.functions().forEach((name, fn) -> {
            HashSet<GlobalReg> allUse = new HashSet<>(), allDefs = new HashSet<>();
            HashSet<Function> allCall = new HashSet<>();
            fn.blocks().forEach(block -> {
                for (Inst inst = block.headInst; inst != null; inst = inst.next){
                    for (Operand use : inst.uses()) {
                        if (use instanceof GlobalReg) {
                            allUse.add((GlobalReg) use);
                            if (inst instanceof Store) allDefs.add((GlobalReg) use);
                        }
                    }
                    if (inst instanceof Call) {
                        Function callee = ((Call) inst).callee();
                        if (!irRoot.isBuiltIn(callee.name())) allCall.add(callee);
                    }
                }
            });
            uses.put(fn, allUse);
            defs.put(fn, allDefs);
            calls.put(fn, allCall);
            queue.offer(fn);
            inQueue.add(fn);
        });

        while (!queue.isEmpty()) {
            Function fn = queue.poll();
            inQueue.remove(fn);
            HashSet<GlobalReg> fnUses = uses.get(fn);
            callRelation.callerOf(fn).forEach(caller -> {
                if (!uses.get(caller).containsAll(fnUses)) {
                    uses.get(caller).addAll(fnUses);
                    if (!inQueue.contains(caller)) {
                        inQueue.add(caller);
                        queue.offer(caller);
                    }
                }
            });
        }
    }

    private ArrayList<Function> DFSStack = new ArrayList<>();
    private HashSet<Function> visited = new HashSet<>();
    private HashSet<Function> cannotInlineFun = new HashSet<>();
    private void DFS(Function it) {
        visited.add(it);
        DFSStack.add(it);
        boolean inRing = false;
        for (Function fn : DFSStack) {
            if (it.isCallee(fn)) inRing = true;
            if (inRing) cannotInlineFun.add(fn);
        }
        calls.get(it).forEach(callee -> {
            if (!visited.contains(callee)) DFS(callee);
            callRelation.callerOf(callee).add(it);
        });
        DFSStack.remove(DFSStack.size() - 1);
    }
    private void inlineJudge() {
        irRoot.functions().forEach((name, func) -> {
            if (!visited.contains(func)) DFS(func);
        });
    }

    private void runForFn(Function fn) {
        if (cannotInlineFun.contains(fn)) return;
        HashSet<GlobalReg> canLoc = new HashSet<>(uses.get(fn));
        calls.get(fn).forEach(callee -> canLoc.removeAll(uses.get(callee)));
        HashMap<Operand, Register> localMap = new HashMap<>();
        canLoc.forEach(glob -> {
            Register reg = new Register(glob.type(), "local_" + glob.name());
            localMap.put(glob, reg);
            fn.addVar(reg);
        });
        fn.blocks().forEach(block -> {
            for (Inst inst = block.headInst; inst != null; inst = inst.next) {
                HashSet<Operand> uses = inst.uses();
                uses.retainAll(canLoc);
                if (!uses.isEmpty()) {
                    if (inst instanceof Load) {
                        Load ld = (Load) inst;
                        if (uses.contains(ld.address())) ld.setAddress(localMap.get(ld.address()));
                    }
                    else if (inst instanceof Store) {
                        Store st = (Store) inst;
                        if (uses.contains(st.address())) st.setAddress(localMap.get(st.address()));
                    }
                    else throw new RuntimeException("inst not a load/store but uses global reg");
                }
            }
        });
        IRBlock entry = fn.entryBlock(), exit = fn.exitBlock();
        localMap.forEach((glob, loc) -> {
            Register tmpHd = new Register(((Pointer) glob.type()).pointTo(),
                            "tmp_load_" + ((GlobalReg)glob).name());
            entry.addHeadInst(new Store(loc, tmpHd, entry));
            entry.addHeadInst(new Load(tmpHd, glob, entry));
            if (defs.get(fn).contains(glob)){
                Register tmpTl = new Register(((Pointer) glob.type()).pointTo(),
                        "tmp_load_" + ((GlobalReg)glob).name());
                exit.addInstTerminated(new Load(tmpTl, loc, exit));
                exit.addInstTerminated(new Store(glob, tmpTl, exit));
            }
        });
    }

    @Override
    public boolean run() {
        collect();
        inlineJudge();
        irRoot.functions().forEach((name, fn)->runForFn(fn));
        return false;
    }
}
