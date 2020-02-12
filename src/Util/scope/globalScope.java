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

        typeMap.put("string", stringType);

        //insert print etc. into the map;

        //
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

}
