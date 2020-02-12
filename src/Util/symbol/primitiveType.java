package Util.symbol;

//int, bool, void, null
public class primitiveType extends BaseType {
    private TypeCategory typeCategory;

    public primitiveType(String name, TypeCategory it) {
        super(name);
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
