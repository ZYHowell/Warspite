package Util;

import MIR.Function;
import MIR.IRBlock;

import java.util.HashSet;

public class MIRReachable {

    public HashSet<IRBlock> reachable = new HashSet<>();

    private void dfs(IRBlock block) {
        block.successors.forEach(suc -> {
            if (!reachable.contains(suc)) {
                reachable.add(suc);
                dfs(suc);
            }
        });
    }

    public MIRReachable(Function fn) {
        reachable.add(fn.entryBlock);
        dfs(fn.entryBlock);
    }
}
