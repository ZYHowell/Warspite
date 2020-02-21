package MIR;

import MIR.IRoperand.Operand;
import MIR.IRoperand.Param;
import MIR.IRtype.IRBaseType;

import java.util.HashMap;
import java.util.ArrayList;

public class Function {

    private String name;
    private Operand classPtr;
    private HashMap<String, Param> parameters;
    private IRBlock entryBlock = new IRBlock("entry"),
                    exitBlock = new IRBlock("exit");

    public Function(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public void setClassPtr(Operand classPtr) {
        this.classPtr = classPtr;
    }
    public Operand getClassPtr() {
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
