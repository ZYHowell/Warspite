package Util.type;

public class classDefType extends BaseType {
    @Override
    public TypeCategory typeCategory(){ return TypeCategory.CLASSDef; }
    @Override
    public boolean sameType(Type it) { return it.isClassDef(); }
}
