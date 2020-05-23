package Optim;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRinst.Call;
import MIR.IRinst.Inst;
import MIR.IRinst.Load;
import MIR.IRinst.Store;
import MIR.IRoperand.Operand;
import MIR.Root;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

//this one includes two parts: LICM about load and redundant elimination
public class MemCSE extends Pass {

    private Root irRoot;
    private AliasAnalysis alias;
    private boolean change;
    private HashSet<IRBlock> visited = new HashSet<>();

    public MemCSE(Root irRoot, AliasAnalysis alias) {
        this.irRoot = irRoot;
        this.alias = alias;
    }

    private boolean _LoadCSE(Load inst, Inst beginning) {
        boolean interrupted = false;
        Operand src = inst.address();
        for (Inst following = beginning; following != null; following = following.next) {
            if (following instanceof Load && ((Load) following).address() == src) {
                following.dest().replaceAllUseWith((inst.dest()));
                following.removeSelf(true);
                change = true;
            }
            else if (following instanceof Store) {
                if (alias.mayAlias(src, ((Store) following).address())) {
                    interrupted = true;
                    break;
                }
            }
            else if (following instanceof Call) {
                if (alias.mayModify(src, ((Call) following).callee())) {
                    interrupted = true;
                    break;
                }
            }
        }
        return !interrupted;
    }

    private boolean _StoreCSE(Store inst, Inst beginning) {
        boolean interrupted = false;
        Operand src = inst.address(), value = inst.value();
        for (Inst following = beginning; following != null; following = following.next) {
            if (following instanceof Load && ((Load) following).address() == src) {
                following.dest().replaceAllUseWith(value);
                following.removeSelf(true);
                change = true;
            }
            else if (following instanceof Store) {
                if (alias.mayAlias(src, ((Store) following).address())) {
                    interrupted = true;
                    break;
                }
            }
            else if (following instanceof Call) {
                if (alias.mayModify(src, ((Call) following).callee())) {
                    interrupted = true;
                    break;
                }
            }
        }
        return !interrupted;
    }
    private void tryStoreCSE(IRBlock currentBlock, Store inst, HashSet<IRBlock> domChildren) {
        if (!domChildren.contains(currentBlock) || visited.contains(currentBlock)) return;
        visited.add(currentBlock);
        if (_StoreCSE(inst, currentBlock.headInst))
            currentBlock.successors().forEach(suc -> tryStoreCSE(suc, inst, domChildren));
        //maybe I'll debug this later
    }

    private void runForFn(Function fn) {
        fn.blocks().forEach(block -> {
            HashSet<IRBlock> domChildren = new HashSet<>();
            Queue<IRBlock> runners = new LinkedList<>(block.successors());
            HashSet<IRBlock> visited = new HashSet<>();
            while(!runners.isEmpty()) {
                IRBlock runner = runners.poll();
                visited.add(runner);
                if (runner.isDomed(block)) {
                    domChildren.add(runner);
                    runner.successors().forEach(suc -> {
                        if (!visited.contains(suc)) runners.offer(suc);
                    });
                }
            }
            //do the collection of store memories in.
            alias.buildStoreInBlock(domChildren);
            for (Inst inst = block.headInst; inst != null; inst = inst.next) {
                if (inst instanceof Load) {
                    if (_LoadCSE((Load) inst, inst.next)) {
                        for (IRBlock dom : domChildren) {
                            if (!alias.storeInBlock(dom, ((Load)inst).address()))
                                _LoadCSE((Load)inst, dom.headInst);
                        }
                    }
                }
                else if (inst instanceof Store) {
                    //is remove redundant store really that necessary? emm... just do for fun
                    boolean loadUse = false;
                    Operand src = ((Store) inst).address();
                    for (Inst following = inst.next; following != null; following = following.next) {
                        if (following instanceof Store && alias.mayAlias(src, ((Store) following).address())) {
                            if (!loadUse && ((Store) following).address() == src) {
                                inst.removeSelf(true);
                                change = true;
                            }
                            break;
                        } else if (following instanceof Call) {
                            if (alias.mayModify(src, ((Call) following).callee())) {
                                break;
                            }
                        } else if (following instanceof Load &&
                                alias.mayAlias(src, ((Load) following).address())) {
                            loadUse = true;
                            if (((Load) following).address() == src) {
                                following.dest().replaceAllUseWith(((Store) inst).value());
                                following.removeSelf(true);
                                change = true;
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean run() {
        change = false;
        irRoot.functions().forEach((name, fn) -> runForFn(fn));
        return change;
    }
}
