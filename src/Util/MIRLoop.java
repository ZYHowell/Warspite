package Util;

import MIR.IRBlock;

import java.util.HashSet;

public class MIRLoop {

    private HashSet<IRBlock> blocks = new HashSet<>();
    private HashSet<IRBlock> tails = new HashSet<>();
    private HashSet<MIRLoop> children = new HashSet<>();
    private IRBlock preHead;

    public MIRLoop() {}

    public void setPreHead(IRBlock preHeader) {
        this.preHead = preHeader;
    }
    public IRBlock preHead() {
        return preHead;
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
    public void addTail(IRBlock tail) {
        tails.add(tail);
    }
    public HashSet<IRBlock> tails() {
        return tails;
    }
}
