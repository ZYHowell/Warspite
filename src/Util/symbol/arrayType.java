package Util.symbol;

public class arrayType extends Type {
    private BaseType baseType;
    private int dim;

    //constructors
    public arrayType(BaseType basicType, int dim) {
        this.baseType = basicType;
        this.dim = dim;
    }
    public arrayType(Type lowerType) {
        this.baseType = lowerType.baseType();
        this.dim = lowerType.dim() + 1;
    }

    //methods
    @Override
    public int dim(){
        return dim;
    }
    @Override
    public TypeCategory typeCategory(){
        return TypeCategory.ARRAY;
    }
    @Override
    public BaseType baseType(){
        return baseType;
    }
    @Override
    public boolean sameType(Type it) {
        return it.isNull() || (dim == it.dim() && baseType.sameType(it.baseType()));
    }
    @Override
    public String toString() {
        return "dim: " + dim + ", base type: " + baseType.toString();
    }
}
