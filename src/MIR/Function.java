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
    private ArrayList<Param> parameters;
    private IRBlock entryBlock = new IRBlock("entry"),
                    exitBlock;
    private HashSet<Function> callFunction = new HashSet<>();
    private HashSet<Register> allocaVar = new HashSet<>();

    public Function(String name) {
        this.name = name;
    }


    public String name() {
        return name;
    }
    public void addCalleeFunction(Function callee) {
        callFunction.add(callee);
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
}
