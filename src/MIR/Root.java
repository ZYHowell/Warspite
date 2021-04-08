package MIR;

import MIR.IRoperand.ConstString;
import MIR.IRoperand.GlobalReg;
import MIR.IRoperand.Param;
import MIR.IRtype.*;
import Util.symbol.Type;
import Util.symbol.arrayType;
import Util.symbol.classType;

import java.util.ArrayList;
import java.util.HashMap;

public class Root {

    private HashMap<String, Function> builtinFunctions = new HashMap<>();
    private HashMap<String, Function> functions = new HashMap<>();
    private HashMap<String, ConstString> ConstStrings = new HashMap<>(), stringValueMap = new HashMap<>();
    private ArrayList<GlobalReg> globalVar = new ArrayList<>();
    private HashMap<String, ClassType> types = new HashMap<>();
    public static IRBaseType stringT = new Pointer(new IntType(8), false),
                             voidT = new VoidType(), i32T = new IntType(32),
                             charT = new IntType(8), boolT = new BoolType();

    public Root() {
        Function printFunc = new Function("g_print");
        printFunc.retType = voidT;
        printFunc.addParam(new Param(stringT, "s"));
        builtinFunctions.put("g_print", printFunc);
        Function printlnFunc = new Function("g_println");
        printlnFunc.retType = voidT;
        printlnFunc.addParam(new Param(stringT, "s"));
        builtinFunctions.put("g_println", printlnFunc);
        Function printIntFunc = new Function("g_printInt");
        printIntFunc.retType = voidT;
        printIntFunc.addParam(new Param(i32T, "v"));
        builtinFunctions.put("g_printInt", printIntFunc);
        Function printlnIntFunc = new Function("g_printlnInt");
        printlnIntFunc.retType = voidT;
        printlnIntFunc.addParam(new Param(i32T, "v"));
        builtinFunctions.put("g_printlnInt", printlnIntFunc);
        Function getStringFunc = new Function("g_getString");
        getStringFunc.retType = stringT;
        builtinFunctions.put("g_getString", getStringFunc);
        Function getIntFunc = new Function("g_getInt");
        getIntFunc.retType = i32T;
        builtinFunctions.put("g_getInt", getIntFunc);
        //above: has side effect(I/O)
        Function toStringFunc = new Function("g_toString");
        toStringFunc.retType = stringT;
        toStringFunc.addParam(new Param(i32T, "i"));
        toStringFunc.setSideEffect(false);
        builtinFunctions.put("g_toString", toStringFunc);
        Function stringAdd = new Function("g_stringAdd");
        stringAdd.retType = stringT;
        stringAdd.addParam(new Param(stringT, "a"));
        stringAdd.addParam(new Param(stringT, "b"));
        stringAdd.setSideEffect(false);
        builtinFunctions.put("g_stringAdd", stringAdd);
        Function stringLT = new Function("g_stringLT");
        stringLT.retType = boolT;
        stringLT.addParam(new Param(stringT, "a"));
        stringLT.addParam(new Param(stringT, "b"));
        stringLT.setSideEffect(false);
        builtinFunctions.put("g_stringLT", stringLT);
        Function stringGT = new Function("g_stringGT");
        stringGT.retType = boolT;
        stringGT.addParam(new Param(stringT, "a"));
        stringGT.addParam(new Param(stringT, "b"));
        stringGT.setSideEffect(false);
        builtinFunctions.put("g_stringGT", stringGT);
        Function stringLE = new Function("g_stringLE");
        stringLE.retType = boolT;
        stringLE.addParam(new Param(stringT, "a"));
        stringLE.addParam(new Param(stringT, "b"));
        stringLE.setSideEffect(false);
        builtinFunctions.put("g_stringLE", stringLE);
        Function stringGE = new Function("g_stringGE");
        stringGE.retType = boolT;
        stringGE.addParam(new Param(stringT, "a"));
        stringGE.addParam(new Param(stringT, "b"));
        stringGE.setSideEffect(false);
        builtinFunctions.put("g_stringGE", stringGE);
        Function stringEQ = new Function("g_stringEQ");
        stringEQ.retType = boolT;
        stringEQ.addParam(new Param(stringT, "a"));
        stringEQ.addParam(new Param(stringT, "b"));
        stringEQ.setSideEffect(false);
        builtinFunctions.put("g_stringEQ", stringEQ);
        Function stringNE = new Function("g_stringNE");
        stringNE.retType = boolT;
        stringNE.addParam(new Param(stringT, "a"));
        stringNE.addParam(new Param(stringT, "b"));
        stringNE.setSideEffect(false);
        builtinFunctions.put("g_stringNE", stringNE);
        Function malloc = new Function("malloc");
        malloc.retType = stringT;
        malloc.addParam(new Param(i32T, "a"));
        malloc.setSideEffect(false);
        builtinFunctions.put("malloc", malloc);
        Function init = new Function("__init");
        init.setSideEffect(true);
        init.setExitBlock(init.entryBlock);
        init.retType = new VoidType();
        functions.put("__init", init);
    }

    public HashMap<String, ClassType> types() {
        return types;
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
    public HashMap<String, Function> builtinFunctions() {
        return builtinFunctions;
    }
    public void addFunction(String name, Function func) {
        functions.put(name, func);
    }
    public Function getFunction(String name) {
        if (functions.containsKey(name))
            return functions.get(name);
        else return builtinFunctions.get(name);
    }
    public Function getInit() {
        return functions.get("__init");
    }
    public HashMap<String, Function> functions() {
        return functions;
    }
    public void addGlobalVar(GlobalReg var) {
        globalVar.add(var);
    }
    public ArrayList<GlobalReg> globalVar() {
        return globalVar;
    }
    public void addConstString(String name, String value) {
        if (!ConstStrings.containsKey(name)){
            ConstString str = new ConstString(name, value);
            ConstStrings.put(name, str);
            stringValueMap.put(value, str);
        }
    }
    public ConstString getStringByValue(String value) {
        return stringValueMap.getOrDefault(value, null);
    }
    public ConstString getConstString(String name) {
        return ConstStrings.get(name);
    }
    public HashMap<String, ConstString> constStrings() {
        return ConstStrings;
    }
    public boolean isBuiltIn(String name) {
        return builtinFunctions.containsKey(name);
    }

    public IRBaseType getIRType(Type type, boolean isMemSet) {
        if (type instanceof arrayType) {
            IRBaseType tmp = getIRType(type.baseType(), isMemSet);
            for (int i = 0; i < type.dim();++i)
                tmp = new Pointer(tmp, false);
            //consider int[][] t; t(int***) is resolvable, but of course the value(int**) of t is not.
            //consider int[][] f(); it returns int**, so not resolvable.
            return tmp;
        }
        else if (type.isInt()) return Root.i32T;
        else if (type.isBool()) {
            if (isMemSet) return Root.charT;
            return new BoolType();
        }
        else if (type.isVoid()) return new VoidType();
        else if (type.isClass()) {
            String name = ((classType)type).name();
            if (name.equals("string")) return Root.stringT;
            else return new Pointer(getType(name), false);
        }
        else if (type.isNull()) return new VoidType();
        return new VoidType();
    }
}
