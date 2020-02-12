package Util.symbol;

abstract public class BaseType extends Type {

    private String name;

    public BaseType(String name) {
        super();
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public BaseType baseType() {
        return this;
    }
    @Override
    public int dim() {
        return 0;
    }
    @Override
    public String toString() {
        return name;
    }
}
