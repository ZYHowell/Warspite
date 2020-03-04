package Optim;

/* this is an MIR side effect analyser pass
 * I intend to use coloring registers to make it precise:
 * when storing to a param or global register, it has side effect.
 * and the address generated from a param will also be alarming in store analysing.
 */

import MIR.Function;
import MIR.Root;
import Util.MIRFnGraph;

public class MIRSEAnalysis extends Pass{

    private Root irRoot;
    private MIRFnGraph callGraph;

    public MIRSEAnalysis(Root irRoot) {
        super();
        this.irRoot = irRoot;
        callGraph = new MIRFnGraph(irRoot, true);
    }

    private void anal(Function fn) {

    }

    @Override
    public boolean run() {
        irRoot.functions().forEach((name, fn) -> fn.setSideEffect(false));
        irRoot.functions().forEach((name, fn) -> anal(fn));
        return false;
    }
}
