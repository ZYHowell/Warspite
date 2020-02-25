package MIR.IRinst;

import MIR.IRBlock;

public class Jump extends Inst {

    private IRBlock dest;

    public Jump(IRBlock dest) {
        super();
        this.dest = dest;
    }

    public IRBlock dest() {
        return dest;
    }

    @Override
    public String toString() {
        return "br label " + dest.name();
    }
}
