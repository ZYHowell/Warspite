package Util.symbol;

import Util.symbol.Type;

abstract public class Entity {

    private String name;

    public Entity(String name) {
        this.name = name;
    }

    public String name() { return name; }

}
