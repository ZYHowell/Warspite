package Util.symbol;

import AST.classDef;
import Util.position;
import Util.error.*;
import Util.scope.*;
import java.util.HashMap;

//this is for a class
public class classType extends BaseType {

    private classDef define;
    private HashMap<String, varEntity> members;
    private HashMap<String, funcDecl> methods;

    private Scope localScope;

    public classType(String name, classDef define) {
        super(name);
        this.define = define;
    }

    public void defineVar(String name, varEntity var, position pos) {
        if (members.containsKey(name))
            throw new semanticError("member redefined", pos);
        members.put(name, var);
    }

    public varEntity var(String name, position pos) {
        if (!members.containsKey(name))
            throw new semanticError("undefined", pos);
        return members.get(name);
    }

    public Scope scope() {
        return localScope;
    }

    public void addScope(Scope localScope) {
        this.localScope = localScope;
    }

    @Override
    public TypeCategory typeCategory(){
        return TypeCategory.CLASS;
    }

    @Override
    public boolean sameType(Type it) {
        return it.dim() == 0 &&
               TypeCategory.CLASS == it.typeCategory() &&
               this.name().equals(((classType)it).name());
    }
}