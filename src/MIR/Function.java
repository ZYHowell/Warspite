package MIR;

import MIR.IRoperand.Param;
import MIR.IRoperand.Register;
import MIR.IRtype.IRBaseType;

import java.util.ArrayList;
import java.util.HashSet;

public class Function {

    private String name;
    private Param classPtr;  //this is very ugly! classPtr is better to be param[0]
    private IRBaseType retType;
    private ArrayList<Param> parameters = new ArrayList<>();
    public IRBlock entryBlock = new IRBlock("entry"),
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
    public void setClassPtr(Param classPtr) {
        this.classPtr = classPtr;
        parameters.add(classPtr);
    }
    public Param getClassPtr() {
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
