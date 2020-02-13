package Util.symbol;

import AST.classDef;
import Util.position;
import Util.error.*;
import Util.scope.*;
import java.util.HashMap;

//this is for a class
public class classType extends BaseType {

    private classDef define;

    private Scope localScope;

    public classType(String name, classDef define) {
        super(name);
        this.define = define;
    }

    public Scope scope() {
        return localScope;
    }

    public void addScope(Scope localScope) {
        this.localScope = localScope;
    }

    public void defineMethod(String name, funcDecl func, position pos) {
        localScope.defineMethod(name, func, pos);
    }

    public void defineMember(String name, varEntity var, position pos) {
        localScope.defineMember(name, var, pos);
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