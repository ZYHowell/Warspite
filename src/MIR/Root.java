package MIR;

import MIR.IRoperand.GlobalReg;
import MIR.IRtype.*;
import Util.error.internalError;
import Util.position;

import java.util.ArrayList;
import java.util.HashMap;

public class Root {

    private Function printFunc = new Function("print");
    private Function printlnFunc = new Function("println");
    private Function printIntFunc = new Function("printInt");
    private Function printlnIntFunc = new Function("printlnInt");
    private Function getStringFunc = new Function("getString");
    private Function getIntFunc = new Function("getInt");
    private Function toStringFunc = new Function("toString");
    private Function sizeFunc = new Function("size");
    private HashMap<String, Function> builtinFunctions = new HashMap<>();
    private HashMap<String, Function> functions = new HashMap<>();
    private ArrayList<GlobalReg> globalVar = new ArrayList<>();
    private HashMap<String, ClassType> types = new HashMap<>();

    public Root() {
        builtinFunctions.put("print", printFunc);
        builtinFunctions.put("println", printlnFunc);
        builtinFunctions.put("printInt", printIntFunc);
        builtinFunctions.put("printlnInt", printlnIntFunc);
        builtinFunctions.put("getString", getStringFunc);
        builtinFunctions.put("getInt", getIntFunc);
        builtinFunctions.put("toString", toStringFunc);
        builtinFunctions.put("size", sizeFunc);
        //todo: set stringAdd and stringCmp(stringLess, stringGreater, stringLessEqual, stringGreaterEqual)
        //todo: init the types(maybe need to use the globalScope)
    }

    public ClassType getType(String name) {
        return types.get(name);
    }
    public Function getBuiltinFunction(String name) {
        return builtinFunctions.get(name);
    }
    public void addFunction(String name, Function func) {
        functions.put(name, func);
    }
    public Function getFunction(String name) {
        if (functions.containsKey(name))
            return functions.get(name);
        else return builtinFunctions.get(name);
    }
    public void addGlobalVar(GlobalReg var) {
        globalVar.add(var);
    }
}
