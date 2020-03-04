package Optim;

import MIR.Function;
import MIR.IRinst.*;
import MIR.IRoperand.Operand;
import MIR.IRtype.Pointer;
import MIR.Root;

import java.util.HashSet;

/*
 * principle: I/O, "outer" store and side effect call are necessary
 * "outer" store: the address is "outer"
 * "outer" address: init by globalReg and parameters, propagated by bitCast, getElementPtr
 * notice that an address loaded from outer address is also outer
 * sideEffectCall: a function containing no IO & outer store & sideEffectCall is not side effective.
 * assume all functions are not side effective at the beginning.
 *
 * This is not the most precise one. an improvement is:
 *      record which parameter is affected, if the parameter is not a necessary one,
 *      the call is still not side effective. however, this is too hard to complete; consider:
 *          class a{ b t;} global; class b{int it};
 *          f(b entity) {global.t = entity;}
 *          int main(){
 *              b x;f(x);
 *              printInt(global.t.it);
 *          }
 *      although x SEEMS not useful so f(x) can be eliminated, the fact is not so.
 */

public class ADCE extends Pass {

    private Root irRoot;
    private boolean change, MoreLive;
    private HashSet<Inst> liveCode = new HashSet<>();
    private HashSet<Operand> outerOp = new HashSet<>();

    public ADCE(Root irRoot) {
        super();
        this.irRoot = irRoot;
    }

    private void initFnCollect(Function fn) {
        change = true;
        while(change) {
            change = false;
            fn.blocks().forEach(block -> {
                block.instructions().forEach(inst -> {
                    HashSet<Operand> uses = inst.uses();
                    uses.retainAll(outerOp);
                    if (!uses.isEmpty() &&
                        (inst instanceof BitCast || inst instanceof GetElementPtr ||
                            (inst instanceof Load && inst.dest().type() instanceof Pointer))) {
                        change = true;
                        outerOp.add(inst.dest());
                    } else assert inst instanceof Load || uses.isEmpty();
                      //i've never seen such judgement from any textbook, so check if my idea is correct...
                });
                block.phiInst().forEach((dest, inst) -> {
                    HashSet<Operand> uses = inst.uses();
                    uses.retainAll(outerOp);
                    if (!uses.isEmpty()) {
                        change = true;
                        outerOp.add(inst.dest());
                    }
                });
            });
        }
    }
    private void initCollect() {
        //collect "outer" address of each function
        outerOp.addAll(irRoot.globalVar());
        irRoot.functions().forEach((name, fn) -> {
            outerOp.addAll(fn.params());
            initFnCollect(fn);
            fn.setSideEffect(false);
        });
        //collect I/O and side effect functions and outer stores
        irRoot.functions().forEach((name, fn) -> fn.blocks().forEach(block ->
            block.instructions().forEach(inst -> {
                if (inst instanceof Call && ((Call)inst).callee().hasSideEffect()){
                    liveCode.add(inst);
                    fn.setSideEffect(true);
                    //also add others?
                }
                else if (inst instanceof Store && outerOp.contains(((Store)inst).address())){
                    liveCode.add(inst);
                    fn.setSideEffect(true);
                    //also add others?
                }
            })));
        //side effect propagation
    }

    private void collect() {
        while(MoreLive) {
            MoreLive = false;

        }
    }

    @Override
    public boolean run() {
        liveCode.clear();
        change = false;
        MoreLive = true;
        initCollect();
        collect();

        return false;
    }
}
