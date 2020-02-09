package Util.type;

public class primitiveType extends BaseType {
    private TypeCategory typeCategory;

    public primitiveType(TypeCategory it) {
        this.typeCategory = it;
    }

    @Override
    public TypeCategory typeCategory(){
        return typeCategory;
    }
    @Override
    public boolean sameType(Type it) {
        return typeCategory == it.typeCategory() && it.dim() == 0;
    }
}
