package Util;

import MIR.IRBlock;
import MIR.IRoperand.*;

import java.util.HashMap;

public class MIRMirror {

    private HashMap<Operand, Operand> opMirror = new HashMap<>();
    private HashMap<IRBlock, IRBlock> blockMirror;

    public MIRMirror() {}

    public Operand opMir(Operand origin) {
        if (origin instanceof GlobalReg) return origin;
        if (!opMirror.containsKey(origin)) opMirror.put(origin, origin.copy());
        return opMirror.get(origin);
    }
    public IRBlock blockMir(IRBlock origin) {
        assert blockMirror.containsKey(origin);
        return blockMirror.get(origin);
    }

    public HashMap<Operand, Operand> opMirror() {
        return opMirror;
    }

    public void setBlockMirror(HashMap<IRBlock, IRBlock> blockMirror) {
        this.blockMirror = blockMirror;
    }
}
