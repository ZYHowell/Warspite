package Assemb;

import java.util.HashSet;

public class LFn {

    private HashSet<LIRBlock> blocks = new HashSet<>();
    private LIRBlock entryBlock, exitBlock;

    public LFn(LIRBlock entryBlock, LIRBlock exitBlock) {
        this.entryBlock = entryBlock;
        this.exitBlock = exitBlock;
    }

    public LIRBlock entryBlock() {
        return entryBlock;
    }
    public LIRBlock exitBlock() {
        return exitBlock;
    }
    public void addBlock(LIRBlock block) {
        blocks.add(block);
    }
    public HashSet<LIRBlock> blocks() {
        return blocks;
    }
}
