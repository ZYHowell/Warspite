package BackEnd;

import Assemb.LFn;
import Assemb.LIRBlock;
import Assemb.LOperand.PhyReg;
import Assemb.LOperand.Reg;
import Assemb.LRoot;
import Assemb.RISCInst.Ld;
import Assemb.RISCInst.Mv;
import Assemb.RISCInst.RISCInst;
import Assemb.RISCInst.St;

public class Peephole {

    private LRoot root;

    public Peephole(LRoot root) {
        this.root = root;
    }

    private boolean sameLd(Ld inst, PhyReg addr, int offset) {
        return inst.address == addr && inst.offset.value == offset;
    }
    public void run() {
        for (LFn fn : root.functions()) {
            for (LIRBlock block : fn.blocks()) {
                for (RISCInst inst = block.head; inst != null; inst = inst.next) {
                    if (inst instanceof Ld) {
                        PhyReg rd = inst.dest().color, addr = ((Ld) inst).address.color;
                        int offset = ((Ld) inst).offset.value;
                        for (RISCInst replace = inst.next; replace != null; replace = replace.next) {
                            if (replace instanceof Ld && sameLd((Ld) replace, addr, offset)) {
                                if (rd == replace.dest().color) replace.removeSelf();
                                else replace.replaceBy(new Mv(rd, replace.dest().color, block));
                            }
                            boolean interrupt = false;
                            for (Reg def : replace.defs()) {
                                if (def.color == rd || def.color == addr) {
                                    interrupt = true;
                                    break;
                                }
                            }
                            if (interrupt) break;
                        }
                    } else if (inst instanceof St) {
                        PhyReg value = ((St) inst).value.color, addr = ((St) inst).address.color;
                        int offset = ((St) inst).offset.value;
                        for (RISCInst replace = inst.next; replace != null; replace = replace.next) {
                            if (replace instanceof Ld && sameLd((Ld) replace, addr, offset)) {
                                if (value == replace.dest().color) replace.removeSelf();
                                else replace.replaceBy(new Mv(value, replace.dest().color, block));
                            }
                            boolean interrupt = false;
                            for (Reg def : replace.defs()) {
                                if (def.color == value || def.color == addr) {
                                    interrupt = true;
                                    break;
                                }
                            }
                            if (interrupt) break;
                        }
                    }
                }
            }
        }
    }
}
