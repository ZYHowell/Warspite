package Util.symbol;

import AST.classDef;
import Util.position;
import Util.scope.*;

import java.util.ArrayList;

public class classType extends BaseType {

    private int allocSize = 0;
    private Scope localScope;
    private varEntity formalEntity; //used for this expr(provide it a entity)
    private ArrayList<Type> elementTypeList = new ArrayList<>();

    public classType(String name, classDef define) {
        super(name);
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
    public int setElement(Type type) {
        int ret = elementTypeList.size();
        elementTypeList.add(type);
        allocSize += type.size();
        return ret;
    }
    public ArrayList<Type> elementTypeList() {
        return elementTypeList;
    }

    @Override
    public int allocSize() {
        return allocSize;
    }
    @Override
    public int size() {
        return 32;  //a pointer.
    }
    @Override
    public TypeCategory typeCategory(){
        return TypeCategory.CLASS;
    }
    @Override
    public boolean sameType(Type it) {
        return it.isNull() || (it.dim() == 0 && it.isClass() &&
                    this.name().equals(((classType)it).name()));
    }
}