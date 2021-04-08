package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.Reg;
import Assemb.LRoot;

import java.util.Collections;
import java.util.HashSet;

public class Ret extends RISCInst{
    private LRoot root;
    public Ret(LRoot root, LIRBlock block) {
        super(null, block);
        this.root = root;
    }

    @Override
    public HashSet<Reg> uses() {
        return new HashSet<>(Collections.singleton(root.getPhyReg(1)));
    }
    @Override
    public HashSet<Reg> defs() {
        return new HashSet<>();
    }

    @Override
    public void replaceUse(Reg origin, Reg replaced) {}

    @Override
    public void stackLengthAdd(int stackLength) {}

    @Override
    public String toString() {
        return "ret";
    }
}
