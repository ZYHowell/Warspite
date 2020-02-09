package Util.entity;

public class classEntity extends entity {
    protected String name;
    public classEntity(String name) {
        this.name = name;
    }
    public boolean isSame(classEntity other) {
        return name == other.name;
    }
}
