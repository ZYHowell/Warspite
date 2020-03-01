package MIR;

import MIR.IRoperand.ConstString;
import MIR.IRoperand.GlobalReg;
import MIR.IRtype.*;
import Util.error.internalError;
import Util.position;
import Util.symbol.Type;
import Util.symbol.arrayType;
import Util.symbol.classType;

import java.util.ArrayList;
import java.util.HashMap;

public class Root {

    private Function printFunc = new Function("print"),
                     printlnFunc = new Function("println"),
                     printIntFunc = new Function("printInt"),
                     printlnIntFunc = new Function("printlnInt"),
                     getStringFunc = new Function("getString"),
                     getIntFunc = new Function("getInt"),
                     toStringFunc = new Function("toString"),
                     stringAdd = new Function("stringAdd"),
                     stringLT = new Function("stringLT"),
                     stringGT = new Function("stringGT"),
                     stringLE = new Function("stringLE"),
                     stringGE = new Function("stringGE"),
                     stringEQ = new Function("stringEQ"),
                     stringNE = new Function("stringNE");

    private HashMap<String, Function> builtinFunctions = new HashMap<>();
    private HashMap<String, Function> functions = new HashMap<>();
    private HashMap<String, String> ConstStrings = new HashMap<>();
    private ArrayList<GlobalReg> globalVar = new ArrayList<>();
    private HashMap<String, ClassType> types = new HashMap<>();

    public Root() {
        builtinFunctions.put("g_print", printFunc);
        builtinFunctions.put("g_println", printlnFunc);
        builtinFunctions.put("g_printInt", printIntFunc);
        builtinFunctions.put("g_printlnInt", printlnIntFunc);
        builtinFunctions.put("g_getString", getStringFunc);
        builtinFunctions.put("g_getInt", getIntFunc);
        builtinFunctions.put("g_toString", toStringFunc);
        builtinFunctions.put("g_stringAdd", stringAdd);
        builtinFunctions.put("g_stringLT", stringLT);
        builtinFunctions.put("g_stringGT", stringGT);
        builtinFunctions.put("g_stringLE", stringLE);
        builtinFunctions.put("g_stringGE", stringGE);
        builtinFunctions.put("g_stringEQ", stringEQ);
        builtinFunctions.put("g_stringNE", stringNE);
    }

    public void addType(String name, ClassType type) {
        types.put(name, type);
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
    public HashMap<String, Function> functions() {
        return functions;
    }
    public void addGlobalVar(GlobalReg var) {
        globalVar.add(var);
    }
    public void addConstString(String name, String value) {
        ConstStrings.put(name, value);
    }
    public String getConstString(String name) {
        return ConstStrings.get(name);
    }

    public IRBaseType getIRType(Type type, boolean isMemSet) {
        if (type instanceof arrayType) {
            IRBaseType tmp = getIRType(type.baseType(), isMemSet);
            for (int i = 0; i < type.dim();++i)
                tmp = new Pointer(tmp, false);
            //consider int[][] t; t(int***) is resolvable, but of course the value(int**) of t is not.
            //consider int[][] f(); it returns int**, so not resolvable.
        }
        else if (type.isInt()) return new IntType(32);
        else if (type.isBool()) {
            if (isMemSet) return new IntType(8);
            return new BoolType();
        }
        else if (type.isVoid()) return new VoidType();
        else if (type.isClass()) {
            String name = ((classType)type).name();
            if (name.equals("string")) return new Pointer(new IntType(8), false);
            else return new Pointer(getType(name), false);
        }
        else if (type.isNull()) return new VoidType();
        return new VoidType(); //really do so? or just throw error? type is function/constructor
    }
}
