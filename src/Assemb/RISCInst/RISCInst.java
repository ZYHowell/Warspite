package Assemb.RISCInst;

import Assemb.LIRBlock;
import Assemb.LOperand.Reg;

import java.util.HashSet;

public abstract class RISCInst {

    public enum CalCategory {
        add, sub, slt, xor, or, and , sll, srl, sra, mul, div, rem
    }
    public enum EzCategory {
        eq, ne, le, ge, lt, gt
    }
    private Reg dest;
    private LIRBlock block;
    public RISCInst next = null, previous = null;
    public RISCInst(Reg dest, LIRBlock block) {
        this.dest = dest;
        this.block = block;
    }

    public Reg dest() {
        return dest;
    }
    public void replaceDest(Reg origin, Reg replaced) {
        if (dest == origin) dest = replaced;
    }
    public LIRBlock block() {
        return block;
    }
    public void removeSelf() {
        if (next != null) next.previous = previous;
        else block.tail = previous;
        if (previous != null) previous.next = next;
        else block.head = next;
    }
    public void addPre(RISCInst added) {
        added.previous = previous;
        added.next = this;
        if (previous != null) previous.next = added;
        else block.head = added;
        previous = added;
    }
    public void addPost(RISCInst added) {
        added.next = next;
        added.previous = this;
        if (next != null) next.previous = added;
        else block.tail = added;
        next = added;
    }
    public void replaceBy(RISCInst replaced) {
        replaced.previous = previous;
        if (previous != null) previous.next = replaced;
        else block.head = replaced;
        replaced.next = next;
        if (next != null) next.previous = replaced;
        else block.tail = replaced;
    }

    public abstract HashSet<Reg> uses();
    public abstract void replaceUse(Reg origin, Reg replaced);
    public abstract void stackLengthAdd(int stackLength);
    public abstract HashSet<Reg> defs();
    @Override
    public abstract String toString();
}
