package Util;

import MIR.Function;
import MIR.IRinst.Call;
import MIR.Root;

import java.util.HashMap;
import java.util.HashSet;

public class MIRFnGraph {

    private Root irRoot;
    private HashMap<Function, HashSet<Function>> caller = new HashMap<>();
    private boolean callerCollect;

    public MIRFnGraph(Root irRoot, boolean callerCollect) {
        this.irRoot = irRoot;
        this.callerCollect = callerCollect;
    }

    public void build() {
        if (callerCollect)
            irRoot.functions().forEach((name, func) -> caller.put(func, new HashSet<>()));
        irRoot.functions().forEach((name, func) -> {
            func.callFunction().clear();
            func.blocks().forEach(block ->
                block.instructions().forEach(inst -> {
                    if (inst instanceof Call) {
                        if ( !irRoot.isBuiltIn(((Call) inst).callee().name()) )
                            func.addCalleeFunction(((Call)inst).callee());
                        if (callerCollect) caller.get(((Call)inst).callee()).add(func);
                    }
                })
            );
        });
    }

    public HashMap<Function, HashSet<Function>> caller() {
        return caller;
    }
    public HashSet<Function> callerOf(Function fn) {
        return caller.get(fn);
    }
}
