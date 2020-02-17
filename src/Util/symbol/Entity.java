package Util.symbol;

import Util.symbol.Type;

abstract public class Entity extends Symbol{

    private String name;

    public Entity(String name) {
        this.name = name;
    }

    public String name() { return name; }

}
