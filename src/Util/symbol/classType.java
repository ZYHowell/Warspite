package Util.symbol;

import AST.classDef;
import Util.position;
import Util.scope.*;

//this is for a class
//a classType can also be an entity(just like what varEntity can be)
public class classType extends BaseType {

    private classDef define;

    private Scope localScope;
    private varEntity formalEntity; //this is used for this expr(provide it a entity)

    public classType(String name, classDef define) {
        super(name);
        this.define = define;
        formalEntity = new varEntity("formal entity", this, true, false);
    }

    public varEntity formalEntity() {
        return formalEntity;
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