package Util.symbol;

abstract public class Type extends Symbol {
    public enum TypeCategory {
        NULL, BOOL, INT, ARRAY, CLASS, CONSTRUCTOR, VOID, FUNC
    }
    //the class is a defined class, and the classDef is the type of constructive function
    //no string because string is a CLASS
    //FUNC is for expression part M.a in expression M.a(x, y, z);

    public abstract int dim();
    public abstract TypeCategory typeCategory();
    public abstract BaseType baseType();
    public abstract boolean sameType(Type it);
    public abstract String toString();
    public abstract int size();

    public boolean isConstructor() { return typeCategory() == TypeCategory.CONSTRUCTOR; }
    public boolean isVoid() { return typeCategory() == TypeCategory.VOID; }
    public boolean isNull(){
        return typeCategory() == TypeCategory.NULL;
    }
    public boolean isBool(){
        return typeCategory() == TypeCategory.BOOL;
    }
    public boolean isInt() { return typeCategory() == TypeCategory.INT; }
    public boolean isClass() {return typeCategory() == TypeCategory.CLASS;}
    public boolean isFunc() { return typeCategory() == TypeCategory.FUNC;}
    public boolean isArray() {return dim() != 0;}
}
