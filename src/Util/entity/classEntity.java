package Util.entity;

public class classEntity extends entity {

    private String name;

    public classEntity(String name) {
        this.name = name;
    }
    public boolean isSame(classEntity other) {
        return name.equals(other.name);
    }
}
