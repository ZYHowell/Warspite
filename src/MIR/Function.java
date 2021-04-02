package MIR;

import MIR.IRoperand.Param;
import MIR.IRoperand.Register;
import MIR.IRtype.IRBaseType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class Function {

    public String name;
    public Param classPtr;
    public IRBaseType retType;
    public ArrayList<Param> parameters = new ArrayList<>();
    public IRBlock entryBlock = new IRBlock("entry"),
                    exitBlock;
    public HashSet<Function> callFunction = new HashSet<>();
    public HashSet<Register> allocaVar = new HashSet<>();
    public LinkedHashSet<IRBlock> blocks = new LinkedHashSet<>();
    public boolean hasSideEffect = true;


    public Function(String name) {
        this.name = name;
        blocks.add(entryBlock);
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
    public void setClassPtr(Param classPtr) {
        this.classPtr = classPtr;
        parameters.add(classPtr);
    }
    public void addParam(Param parameter){
        parameters.add(parameter);
    }
    public ArrayList<Param> params() {
        return parameters;
    }
    public void setExitBlock(IRBlock exitBlock) {
        this.exitBlock = exitBlock;
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
