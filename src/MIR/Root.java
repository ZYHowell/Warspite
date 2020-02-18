package MIR;

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

    public Root() {
        builtinFunctions.put("global_print", printFunc);
        builtinFunctions.put("global_println", printlnFunc);
        builtinFunctions.put("global_printInt", printIntFunc);
        builtinFunctions.put("global_printlnInt", printlnIntFunc);
        builtinFunctions.put("global_getString", getStringFunc);
        builtinFunctions.put("global_getInt", getIntFunc);
        builtinFunctions.put("global_toString", toStringFunc);
        builtinFunctions.put("global_size", sizeFunc);
    }

    public void addFunction(String name, Function func) {
        functions.put(name, func);
    }
    public Function getFunction(String name) {
        if (functions.containsKey(name))
            return functions.get(name);
        else return builtinFunctions.get(name);
    }
}
