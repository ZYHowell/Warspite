package Util.symbol;


public class varEntity extends Entity {

    Type type;
    boolean isOuter;    //indicate if the variable is {member of a class, global variable}

    public varEntity(String name, Type type, boolean isOuter) {
        super(name);
        this.type = type;
        this.isOuter = isOuter;
    }

    public Type type() { return type; }

    public boolean isOuter() {
        return isOuter;
    }
}
