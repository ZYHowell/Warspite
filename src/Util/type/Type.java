package Util.type;

abstract public class Type {
    public enum TypeCategory {
        NULL, BOOL, INT, STRING, ARRAY, CLASS, CLASSDef, VOID
    }           //the class is a defined class, and the classDef is the type of constructive function
    public abstract int dim();
    public abstract TypeCategory typeCategory();
    public abstract BaseType baseType();
    public abstract boolean sameType(Type it);
    public Type totType(){
        return this;
    }

    public boolean isClassDef() { return typeCategory() == TypeCategory.CLASSDef; }
    public boolean isVoid() { return typeCategory() == TypeCategory.VOID; }
    public boolean isNull(){
        return typeCategory() == TypeCategory.NULL;
    }
    public boolean isBool(){
        return typeCategory() == TypeCategory.BOOL;
    }
    public boolean isInt() { return typeCategory() == TypeCategory.INT; }
    public boolean isString() { return typeCategory() == TypeCategory.STRING; }
}
