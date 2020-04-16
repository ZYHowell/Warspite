package BackEnd;

import Assemb.*;
import Assemb.LOperand.Reg;
import Assemb.RISCInst.Mv;

import java.util.HashSet;
import java.util.Stack;

public class RegAlloc {

    private LRoot root;
    private DAG currentDAG;
    private HashSet<Mv> workListMv, activeMoves = new HashSet<>();
    private HashSet<Reg> spillWorkList = new HashSet<>(),
                         freezeWorkList = new HashSet<>(),
                         simplifyWorkList = new HashSet<>();
    private Stack<Reg> selectStack = new Stack<>();
    private HashSet<Reg> coalesced = new HashSet<>();

    public RegAlloc(LRoot root) {
        this.root = root;
    }

    private HashSet<Reg> adjacent(Reg n) {
        HashSet<Reg> ret = currentDAG.adjacent(n);
        ret.removeAll(selectStack);
        ret.removeAll(coalesced);
        return ret;
    }
    private HashSet<Mv> nodeMoves(Reg n) {
        HashSet<Mv> ret = new HashSet<>(workListMv);
        ret.addAll(activeMoves);
        ret.retainAll(n.moveInst);
        return ret;
    }
    private boolean MoveRelated(Reg n) {
        return !nodeMoves(n).isEmpty();
    }

    private void runForFn(LFn fn) {
        //initWorkList
        workListMv = fn.workListMv();
        currentDAG = fn.dag();
    }
    public void run() {
        root.functions().forEach(this::runForFn);
    }
}
