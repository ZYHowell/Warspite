package Util;

import MIR.Function;
import MIR.IRinst.Call;
import MIR.IRinst.Inst;
import MIR.Root;

import java.util.HashMap;
import java.util.HashSet;

public class MIRFnGraph {

    private Root irRoot;
    private HashMap<Function, HashSet<Function>> caller = new HashMap<>();
    private boolean callerCollect = true;

    public MIRFnGraph(Root irRoot) {
        this.irRoot = irRoot;
    }

    public void build() {
        if (callerCollect)
            irRoot.functions().forEach((name, func) -> caller.put(func, new HashSet<>()));
        irRoot.functions().forEach((name, func) -> {
            func.callFunction.clear();
            func.blocks.forEach(block ->{
                for (Inst inst = block.headInst; inst != null;inst = inst.next){
                    if (inst instanceof Call && !irRoot.isBuiltIn(((Call) inst).callee().name)) {
                        func.addCalleeFunction(((Call)inst).callee());
                        if (callerCollect) caller.get(((Call)inst).callee()).add(func);
                    }
                }
            });
        });
    }

    public HashSet<Function> callerOf(Function fn) {
        return caller.get(fn);
    }
}
