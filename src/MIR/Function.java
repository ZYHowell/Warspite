package MIR;

import MIR.IRoperand.Param;
import MIR.IRoperand.Register;
import MIR.IRtype.ClassType;
import MIR.IRtype.IRBaseType;
import MIR.IRtype.Pointer;

import java.util.ArrayList;
import java.util.HashSet;

public class Function {

    private String name;
    private Register classPtr;
    private IRBaseType retType;
    private ArrayList<Param> parameters = new ArrayList<>();
    private IRBlock entryBlock = new IRBlock("entry"),
                    exitBlock;
    private HashSet<Function> callFunction = new HashSet<>();
    private HashSet<Register> allocaVar = new HashSet<>();
    private HashSet<IRBlock> blocks = new HashSet<>();
    private boolean hasSideEffect = true;


    public Function(String name) {
        this.name = name;
        blocks.add(entryBlock);
    }

    public String name() {
        return name;
    }

    public void addCalleeFunction(Function callee) {
        callFunction.add(callee);
    }
    public boolean isCallee(Function fn) {
        return callFunction.contains(fn);
    }
    public void addBlock(IRBlock block) {
        blocks.add(block);
    }
    public void removeBlock(IRBlock block) {
        blocks.remove(block);
    }
    public HashSet<IRBlock> blocks() {
        return blocks;
    }
    public HashSet<Function> callFunction() {
        return callFunction;
    }
    public void setRetType(IRBaseType retType) {
        this.retType = retType;
    }
    public IRBaseType retType() {
        return retType;
    }
    public void setClassPtr(Register classPtr) {
        this.classPtr = classPtr;
    }
    public Register getClassPtr() {
        return classPtr;
    }
    public void addParam(Param parameter){
        parameters.add(parameter);
    }
    public ArrayList<Param> params() {
        return parameters;
    }
    public IRBlock entryBlock() {
        return entryBlock;
    }
    public void setExitBlock(IRBlock exitBlock) {
        this.exitBlock = exitBlock;
    }
    public IRBlock exitBlock() {
        return exitBlock;
    }

    public void addVar(Register var) {
        allocaVar.add(var);
    }
    public HashSet<Register> allocVars() {
        return allocaVar;
    }

    public void setSideEffect(boolean has) {
        hasSideEffect = has;
    }
    public boolean hasSideEffect() {
        return hasSideEffect;
    }
}
