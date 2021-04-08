package MIR;

import MIR.IRinst.Inst;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;

import java.util.HashSet;
import java.util.Map;

public class CorrectnessCheck {
    private HashSet<Inst> instructions = new HashSet<>();
    public boolean run(Root root) {
        instructions.clear();
        boolean pass = true;
        for (Map.Entry<String, Function> entry : root.functions().entrySet()) {
            String n = entry.getKey();
            Function f = entry.getValue();
            f.blocks.forEach(b -> {
                for (Inst i = b.headInst; i != null; i = i.next) instructions.add(i);
            });
            for (IRBlock b : f.blocks) {
                for (Inst i = b.headInst; i != null; i = i.next) {
                    for (Operand o : i.uses()) {
                        if (o instanceof Register) {
                            Register r = (Register) o;
                            for (Inst u : r.uses()) {
                                if (!instructions.contains(u)) {
                                    pass = false;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return pass;
    }
}
