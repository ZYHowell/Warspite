package BackEnd;

import Assemb.LFn;
import Assemb.LIRBlock;
import Assemb.LRoot;
import Assemb.RISCInst.*;
import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.*;
import MIR.IRoperand.*;
import MIR.Root;

import java.util.HashMap;

import static Assemb.RISCInst.RISCInst.*;
import static Assemb.RISCInst.RISCInst.CalCategory.*;
import static Assemb.RISCInst.RISCInst.CalCategory.div;
import static Assemb.RISCInst.RISCInst.CalCategory.mul;

public class InstSelection {

    private Root irRoot;
    private LRoot lRoot = new LRoot();
    private HashMap<IRBlock, LIRBlock> blockMap = new HashMap<>();

    public InstSelection(Root irRoot) {
        this.irRoot = irRoot;
    }

    private void genLIR(Inst inst, LIRBlock block) {
        if (inst instanceof Binary) {
            Binary bi = (Binary) inst;
            CalCategory opCode = null;
            switch(bi.opCode()) {
                case mul: opCode = mul;break;
                case sdiv: opCode = div;break;
                case srem: opCode = rem;break;
                case shl: opCode = sll;break;
                case ashr: opCode = sra;break;
                case and: opCode = and;break;
                case or: opCode = or;break;
                case xor: opCode = xor;break;
                case sub: opCode = sub;break;
                case add: opCode = add;break;
            }
            if (bi.src1() instanceof ConstInt)
                block.addInst(new IType(bi.src2(), (ConstInt)bi.src1(), opCode, bi.dest(), block));
            else if (bi.src2() instanceof ConstInt)
                block.addInst(new IType(bi.src1(), (ConstInt)bi.src2(), opCode,  bi.dest(), block));
            else block.addInst(new RType(bi.src1(), bi.src2(), opCode, bi.dest(), block));
        }
        else if (inst instanceof Branch) {}
        else if (inst instanceof Call) {}
        else if (inst instanceof Cmp) {
            Cmp cm = (Cmp) inst;
            CalCategory opCode = null;
            switch (cm.opCode()) {
                case slt: break;
                case sgt: break;
                case sle: break;
                case sge: break;
                case eq: break;
                case ne: break;
            }
        }
        else if (inst instanceof GetElementPtr) {}
        else if (inst instanceof Jump) {}
        else if (inst instanceof Load) {}
        else if (inst instanceof Malloc) {}
        else if (inst instanceof Move) {}
        else if (inst instanceof Return) {}
        else if (inst instanceof Store) {}
    }

    private void copyBlock(IRBlock origin, LIRBlock block) {
        origin.instructions().forEach(inst -> genLIR(inst, block));
    }
    private void runForFn(Function fn) {
        fn.blocks().forEach(block -> {
            LIRBlock lBlock = new LIRBlock();
            blockMap.put(block, lBlock);
        });
        LFn lFn = new LFn(blockMap.get(fn.entryBlock()), blockMap.get(fn.exitBlock()));
        fn.blocks().forEach(block -> {
            LIRBlock lBlock = blockMap.get(block);
            copyBlock(block, lBlock);
            lFn.addBlock(lBlock);
        });
        lRoot.addFunction(lFn);
    }
    public LRoot run() {
        irRoot.functions().forEach((name, fn) -> runForFn(fn));
        return lRoot;
    }
}
