package Assemb;

import Assemb.LOperand.Reg;
import Assemb.RISCInst.RISCInst;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class LIRBlock {

    public ArrayList<LIRBlock> precursors = new ArrayList<>();
    public ArrayList<LIRBlock> successors = new ArrayList<>();
    public HashSet<Reg> liveIn = new HashSet<>(), liveOut = new HashSet<>();
    public RISCInst head = null, tail = null;
    public int loopDepth;
    public String name;
    public LIRBlock next = null;
    public boolean hasPrior = false;

    public LIRBlock(int loopDepth, String name) {
        this.loopDepth = loopDepth;
        this.name = name;
    }

    public void addInst(RISCInst inst) {
        if (head == null) head = inst;
         else {
            tail.next = inst;
            inst.previous = tail;
        }
        tail = inst;
    }

    @Override
    public String toString() {
        return name;
    }
}
