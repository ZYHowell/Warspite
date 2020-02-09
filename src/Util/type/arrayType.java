package Util.type;

public class arrayType extends Type {
    private BaseType basicType;
    private int dim = 0;
    public arrayType(Type it) {
        this.basicType = it.baseType();
        this.dim = it.dim() + 1;
    }
    public int dim(){ return dim;}
    public TypeCategory typeCategory(){ return TypeCategory.ARRAY; }
    public BaseType baseType(){ return basicType;}
    public boolean sameType(Type it) {
        return it.isNull() || (dim == it.dim() && basicType.sameType(it.baseType()));
    }
}
