package Optim;

import MIR.Function;
import MIR.IRinst.*;
import MIR.IRoperand.Operand;
import MIR.IRtype.Pointer;
import MIR.Root;
import Util.MIRFnGraph;

import java.util.*;

/*
 * this is alias analysis. It seems that the structure is based on mem2mem, so a map from reg to mem
 * To consider: is this really such necessary?
 * ArrayType only used in load,
 * pointer type only used in bitCast, GEP, Call, Malloc, Cmp, Load, Store, Return
 */
public class AliasAnalysis {

    private static class Memory{
        public ArrayList<Memory> baseConstraints = new ArrayList<>(),       //a = &b recorded in a
                                 simpleConstraints = new ArrayList<>(),     //a = b  recorded in b, so edge b->a
                                 complexAConstraints = new ArrayList<>(),   //a = *b recorded in b
                                 complexBConstraints = new ArrayList<>();   //*a = b recorded in a
        public HashSet<Memory> pts = new HashSet<>();
        public String name;
        //the current memory is a and the one in constraints is b
        public Memory(String name) {
            this.name = name;
        }
    }
    private Root irRoot;
    private HashMap<Operand, Memory> pointerMap = new HashMap<>();
    private HashSet<Memory> memSet = new HashSet<>();
    private HashMap<Function, HashSet<Memory>> fnStores = new HashMap<>();

    public AliasAnalysis(Root irRoot) {
        super();
        this.irRoot = irRoot;
    }

    private void initMem() {            //
        irRoot.globalVar().forEach(reg -> {
            Memory pointerMem = new Memory(reg.name()), pointToMem = new Memory(reg.name() + "_pointTo");
            pointerMem.baseConstraints.add(pointToMem);
            pointerMap.put(reg, pointerMem);
            memSet.add(pointerMem);
            memSet.add(pointToMem);
        });
        irRoot.constStrings().forEach((name, value) -> {
            Memory pointerMem = new Memory(name), pointToMem = new Memory(name + "_pointTo");
            pointerMem.baseConstraints.add(pointToMem);
            pointerMap.put(value, pointerMem);
            memSet.add(pointerMem);
            memSet.add(pointToMem);
        });
        irRoot.functions().forEach((name, fn) ->{
            fn.blocks().forEach(block -> {
                for (Inst inst = block.headInst; inst != null; inst = inst.next)
                    if (inst.dest() != null && inst.dest().type() instanceof Pointer){
                        Memory tmp = new Memory(inst.dest().toString());
                        pointerMap.put(inst.dest(), tmp);
                        memSet.add(tmp);
                    }

                block.phiInst().forEach((reg, phi) -> {
                    if (reg.type() instanceof Pointer) {
                        Memory tmp = new Memory(reg.toString());
                        pointerMap.put(reg, tmp);
                        memSet.add(tmp);
                    }
                });
            });
            fn.params().forEach(param -> {
                if (param.type() instanceof Pointer) {
                    Memory tmp = new Memory(param.toString());
                    pointerMap.put(param, tmp);
                    memSet.add(tmp);
                }
            });
        });
    }
    private void collect(Function fn) { //collect constraints
        fn.blocks().forEach(block -> {
            block.phiInst().forEach((reg, phi) -> {
                if (reg.type() instanceof Pointer) {
                    Memory phiMem = pointerMap.get(reg);
                    phi.values().forEach(value -> pointerMap.get(value).simpleConstraints.add(phiMem));
                }
            });
            for (Inst inst = block.headInst; inst != null; inst = inst.next) {
                if (inst instanceof Load && inst.dest().type() instanceof Pointer) {
                    pointerMap.get(((Load) inst).address()).complexAConstraints.add(pointerMap.get(inst.dest()));
                }
                else if (inst instanceof BitCast) {
                    pointerMap.get(((BitCast) inst).origin()).simpleConstraints.add(pointerMap.get(inst.dest()));
                }
                else if (inst instanceof GetElementPtr) {
                    pointerMap.get(((GetElementPtr) inst).ptr()).simpleConstraints.add(pointerMap.get(inst.dest()));
                }
                else if (inst instanceof Call) {
                    Call ca = (Call) inst;
                    for (int i = 0;i < ca.params().size();++i) {
                        Operand op = ca.params().get(i);
                        if (op.type() instanceof Pointer)
                            pointerMap.get(op).simpleConstraints.add(
                                    pointerMap.get(ca.callee().params().get(i)));
                    }
                    if (ca.dest().type() instanceof Pointer) {
                        Operand RetReg = ((Return) ca.callee().exitBlock().terminator()).value();
                        pointerMap.get(RetReg).simpleConstraints.add(pointerMap.get(ca.dest()));
                    }
                }
                else if (inst instanceof Malloc) {
                    Memory allocMem = new Memory("alloc_" + inst.dest().toString());
                    pointerMap.get(inst.dest()).baseConstraints.add(allocMem);
                    memSet.add(allocMem);
                }
                else if (inst instanceof Store) {
                    Store st = (Store) inst;
                    if (st.value().type() instanceof Pointer)
                        pointerMap.get(st.address()).complexBConstraints.add(pointerMap.get(st.value()));
                }
            }
        });
    }
    private void workQueue() {
        Queue<Memory> W = new LinkedList<>();
        //handle all base constraints
        memSet.forEach(mem -> {
            if (!mem.baseConstraints.isEmpty())
                mem.pts.addAll(mem.baseConstraints);
        });
        //handle all simple constraints: spread the pts by edges(that is, simple constraints)
        HashSet<Memory> simpleSet = new HashSet<>();
        memSet.forEach(mem -> {if (!mem.simpleConstraints.isEmpty()) simpleSet.add(mem);});
        HashSet<Memory> visited = new HashSet<>();
            //a faster method is to handle all rings first, then the topological graph.
        while(!visited.containsAll(simpleSet)) {
            HashSet<Memory> todo = new HashSet<>(simpleSet);
            todo.removeAll(visited);
            todo.forEach(b -> {
                visited.add(b);
                b.simpleConstraints.forEach(a -> {
                    if (!a.pts.containsAll(b.pts)) {
                        visited.remove(a);
                        a.pts.addAll(b.pts);
                    }
                });
            });
        }
        memSet.forEach(mem -> {
            if (!mem.pts.isEmpty()) W.offer(mem);
        });
        while (!W.isEmpty()) {
            Memory v = W.poll();
            v.pts.forEach(a -> {
                //for each p that p = *v(constraint A)
                v.complexAConstraints.forEach(p -> {
                    if (!a.simpleConstraints.contains(p)){
                        a.simpleConstraints.add(p); //edge a->p
                        W.offer(a);
                    }
                });
                //for each q that *v = q(constraint B)
                v.complexBConstraints.forEach(q -> {
                    if (!q.simpleConstraints.contains(a)) {
                        q.simpleConstraints.add(a); //edge q->a
                        W.offer(q);
                    }
                });
            });
            //edge v->q
            v.simpleConstraints.forEach(q -> {
                if (!q.pts.containsAll(v.pts)) {
                    q.pts.addAll(v.pts);
                    W.offer(q);
                }
            });
        }
    }
    private void fnStoreCollect() {
        MIRFnGraph callGraph = new MIRFnGraph(irRoot);
        callGraph.build();
        irRoot.functions().forEach((name, fn) -> {
            HashSet<Memory> stores = new HashSet<>();
            fn.blocks().forEach(block -> {
                for (Inst inst = block.headInst; inst != null; inst = inst.next)
                    if (inst instanceof Store) stores.addAll(pointerMap.get(((Store) inst).address()).pts);
            });
            fnStores.put(fn, stores);
        });
        HashSet<Function> visited = new HashSet<>();
        HashSet<Function> allFn = new HashSet<>(irRoot.functions().values());
        while(!visited.containsAll(allFn)) {
            HashSet<Function> todo = new HashSet<>(allFn);
            todo.removeAll(visited);
            todo.forEach(fn -> {
                visited.add(fn);
                HashSet<Memory> stores = fnStores.get(fn);
                callGraph.callerOf(fn).forEach(caller -> {
                    if (!fnStores.get(caller).containsAll(stores)) {
                        visited.remove(caller);
                        fnStores.get(caller).addAll(stores);
                    }
                });
            });
        }
    }

    public void run() {
        initMem();
        irRoot.functions().forEach((name, fn) -> collect(fn));
        workQueue();
        fnStoreCollect();
    }

    public boolean mayAlias(Operand src1, Operand src2) {
        if (!((Pointer)src1.type()).pointTo().sameType(((Pointer)src2.type()).pointTo())) return false;
        HashSet<Memory> pts1 = new HashSet<>(pointerMap.get(src1).pts);
        pts1.retainAll(pointerMap.get(src2).pts);
        return pts1.isEmpty();
    }
    public boolean mayModify(Operand src, Function fn) {
        if (irRoot.isBuiltIn(fn.name())) return false;
        HashSet<Memory> pts = new HashSet<>(pointerMap.get(src).pts);
        pts.retainAll(fnStores.get(fn));
        return pts.isEmpty();
    }
}
