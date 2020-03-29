package Util;

import MIR.IRBlock;

import java.util.HashSet;

public class MIRLoop {

    private HashSet<IRBlock> blocks = new HashSet<>();
    private IRBlock head;
    private HashSet<MIRLoop> children = new HashSet<>();

    public MIRLoop(IRBlock head) {
        this.head = head;
    }
    public void addBlock(IRBlock block) {
        blocks.add(block);
    }
    public void addBlocks(HashSet<IRBlock> newBlocks) {
        blocks.addAll(newBlocks);
    }
    public HashSet<IRBlock> blocks() {
        return blocks;
    }
    public void addChild(MIRLoop child) {
        children.add(child);
    }
    public HashSet<MIRLoop> children() {
        return children;
    }
}
