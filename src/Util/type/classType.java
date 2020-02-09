package Util.type;
import Util.entity.classEntity;

public class classType extends BaseType {
    private static TypeCategory classT = TypeCategory.CLASS;
    private static String className;
    protected classEntity entity;
    protected String name;
    @Override
    public TypeCategory typeCategory(){
        return TypeCategory.CLASS;
    }
    @Override
    public boolean sameType(Type it) {
        return it.dim() == 0 && TypeCategory.CLASS == it.typeCategory() && entity.isSame(((classType)it).entity);
    }
}