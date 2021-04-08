package Util.symbol;

abstract public class Entity extends Symbol{

    private String name;

    public Entity(String name) {
        this.name = name;
    }

    public String name() { return name; }

}
