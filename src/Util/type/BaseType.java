package Util.type;

abstract public class BaseType extends Type {
    @Override
    public BaseType baseType() {
        return this;
    };
    @Override
    public int dim() {
        return 0;
    };
}
