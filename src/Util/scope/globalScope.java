package Util.scope;

import java.util.HashMap;

import AST.typeNode;
import Util.symbol.*;
import Util.error.semanticError;
import Util.position;

public class globalScope extends Scope {

    private HashMap<String, Type> typeMap;

    private primitiveType intInstance = new primitiveType("int", Type.TypeCategory.INT);
    private primitiveType boolInstance = new primitiveType("bool", Type.TypeCategory.BOOL);
    private primitiveType voidInstance = new primitiveType("void", Type.TypeCategory.VOID);
    private primitiveType nullInstance = new primitiveType("null", Type.TypeCategory.NULL);

    public globalScope() {
        super(null);
        typeMap = new HashMap<>();
        //now insert void, bool, int, string into the map;
        typeMap.put("int", intInstance);
        typeMap.put("bool", boolInstance);
        typeMap.put("void", voidInstance);
        typeMap.put("null", nullInstance);
        //insert string into the map;
        classType stringType = new classType("string", null);
        position pos = new position(0,0);
        funcDecl tmpFunc;

        tmpFunc = new funcDecl("length", null);
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.setRetType(intInstance);
        stringType.defineMethod("length", tmpFunc, pos);

        tmpFunc = new funcDecl("substring", null);
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.addParam(new varEntity("left", intInstance), pos);
        tmpFunc.addParam(new varEntity("right", intInstance), pos);
        tmpFunc.setRetType(stringType);
        stringType.defineMethod("substring", tmpFunc, pos);

        tmpFunc = new funcDecl("parseInt", null);
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.setRetType(intInstance);
        stringType.defineMethod("parseInt", tmpFunc, pos);

        tmpFunc = new funcDecl("ord", null);
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.addParam(new varEntity("pos", intInstance), pos);
        tmpFunc.setRetType(intInstance);
        stringType.defineMethod("ord", tmpFunc, pos);
        typeMap.put("string", stringType);
        //insert print etc. into the map;
        tmpFunc = new funcDecl("print", null);
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.addParam(new varEntity("str", stringType), pos);
        tmpFunc.setRetType(voidInstance);
        defineMethod("print", tmpFunc, pos);

        tmpFunc = new funcDecl("println", null);
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.addParam(new varEntity("str", stringType), pos);
        tmpFunc.setRetType(voidInstance);
        defineMethod("println", tmpFunc, pos);

        tmpFunc = new funcDecl("printInt", null);
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.addParam(new varEntity("n", intInstance), pos);
        tmpFunc.setRetType(voidInstance);
        defineMethod("printInt", tmpFunc, pos);

        tmpFunc = new funcDecl("printlnInt", null);
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.addParam(new varEntity("n", intInstance), pos);
        tmpFunc.setRetType(voidInstance);
        defineMethod("printlnInt", tmpFunc, pos);

        tmpFunc = new funcDecl("getString", null);
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.setRetType(stringType);
        defineMethod("getString", tmpFunc, pos);

        tmpFunc = new funcDecl("getInt", null);
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.setRetType(intInstance);
        defineMethod("getInt", tmpFunc, pos);

        tmpFunc = new funcDecl("toString", null);
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.addParam(new varEntity("n", intInstance), pos);
        tmpFunc.setRetType(stringType);
        defineMethod("toString", tmpFunc, pos);

        tmpFunc = new funcDecl("size", null);
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.setRetType(intInstance);
        defineMethod("size", tmpFunc, pos);
    }

    public void defineClass(String className, Type classType, position pos) {
        if (typeMap.containsKey(className))
            throw new semanticError("class redefine", pos);
        if (containsMember(className))
            throw new semanticError("class name same to a var", pos);
        typeMap.put(className, classType);
    }

    public Type getType(String typeName, position pos) {
        if (typeMap.containsKey(typeName))
            return typeMap.get(typeName);
        throw new semanticError("undefined type", pos);
    }

    public Type generateType(typeNode it) {
        if (it.dim() != 0)
            return new arrayType((BaseType)(getType(it.typeName(), it.pos())),
                    it.dim());
        return getType(it.typeName(), it.pos());
    }

    public Type getIntType() {
        return intInstance;
    }
    public Type getBoolType() {
        return boolInstance;
    }
    public Type getVoidType() {
        return voidInstance;
    }
    public Type getNullType() {
        return nullInstance;
    }
    public Type getStringType() {
        return typeMap.get("string");
    }

}
