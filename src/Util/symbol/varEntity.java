package Util.symbol;


public class varEntity extends Entity {

    Type type;

    public varEntity(String name, Type type) {
        super(name);
        this.type = type;
    }

    public Type type() { return type; }
}
