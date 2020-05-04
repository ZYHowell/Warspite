package MIR.IRinst;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import MIR.IRoperand.Register;
import Util.MIRMirror;

import java.util.ArrayList;
import java.util.HashSet;

public class Phi extends Inst {

    private ArrayList<IRBlock> blocks;
    private ArrayList<Operand> values;

    public Phi(Register dest, ArrayList<IRBlock> blocks, ArrayList<Operand> values, IRBlock block) {
        super(dest, block);
        this.blocks = blocks;
        this.values = values;
        values.forEach(value -> value.addUse(this));
        dest.setDef(this);
    }

    public void addOrigin(Operand value, IRBlock origin) {
        blocks.add(origin);
        values.add(value);
        value.addUse(this);
    }

    public ArrayList<Operand> values() {
        return values;
    }
    public ArrayList<IRBlock> blocks() {
        return blocks;
    }

    public void removeBlock(IRBlock block) {
        int size = blocks.size();
        for (int i = 0;i < size;++i) {
            if (blocks.get(i) == block) {
                blocks.remove(i);
                values.remove(i);
                break;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder(dest().toString() + " = phi " + dest().type().toString() + " ");
        for (int i = 0;i < values.size();i++) {
            if (i > 0) ret.append(", ");
            ret.append("[ ").append(values.get(i).toString());
            ret.append(", ").append("%").append(blocks.get(i).name()).append(" ]");
        }
        return ret.toString();
    }

    @Override
    public void addMirror(IRBlock destBlock, MIRMirror mirror) {
        ArrayList<IRBlock> mirrorBlocks = new ArrayList<>();
        ArrayList<Operand> mirrorValues = new ArrayList<>();
        blocks.forEach(block -> mirrorBlocks.add(mirror.blockMir(block)));
        values.forEach(value -> mirrorValues.add(mirror.opMir(value)));
        destBlock.addPhi(new Phi((Register)mirror.opMir(dest()), mirrorBlocks, mirrorValues, destBlock));
    }
    @Override
    public HashSet<Operand> uses() {
        return new HashSet<>(values);
    }

    @Override
    public boolean sameMeaning(Inst inst) {
        if (inst instanceof Phi) {
            ArrayList<Operand> iValues = ((Phi) inst).values();
            HashSet<Operand> iValueSet = new HashSet<>(iValues);
            ArrayList<IRBlock> iBlocks = ((Phi) inst).blocks();
            int size = values.size();
            if (iValues.size() == size) {
                for (int i = 0; i < size; i++) {
                    Operand src = values.get(i);
                    if (!iValueSet.contains(src) || iBlocks.get(iValues.indexOf(src)) != blocks.get(i))
                        return false;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean noSideEffect() {
        return false;
    }

    @Override
    public void ReplaceUseWith(Register replaced, Operand replaceTo) {
        for (int i = 0;i < values.size();++i)
            if (values.get(i) == replaced)
                values.set(i, replaceTo);
    }
    @Override
    public void removeSelf(boolean removeFromBlock) {
        if (removeFromBlock) block().remove(this);
        values.forEach(value -> value.removeUse(this));
    }
    @Override
    public boolean isTerminal() {
        return false;
    }
}
