package MIR;

import MIR.IRoperand.Param;
import MIR.IRoperand.Register;
import MIR.IRtype.ClassType;
import MIR.IRtype.IRBaseType;
import MIR.IRtype.Pointer;

import java.util.HashMap;
import java.util.ArrayList;

public class Function {

    private String name;
    private Register classPtr;
    private IRBaseType retType;
    private HashMap<String, Param> parameters;
    private IRBlock entryBlock = new IRBlock("entry"),
                    exitBlock = new IRBlock("exit");

    public Function(String name) {
        this.name = name;
    }

    public String name() {
        return name;
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
    public void addParam(String name, Param parameter){
        parameters.put(name, parameter);
    }
    public Param param(String name) {
        return parameters.get(name);
    }
    public IRBlock entryBlock() {
        return entryBlock;
    }
    public IRBlock exitBlock() {
        return exitBlock;
    }

    public void addVar(String name, IRBaseType type) {
        //todo. mention that this is not the reference type, instead it is used for alloc
    }
}
