package MIR.IRtype;

public class ClassType extends IRBaseType{
    //each class type is treated as a pointer(an i32 address)
    private String name;
    private int size;

    public ClassType(String name, int size) {
        super();
        this.name = name;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }
    @Override
    public String toString() {
        return "%struct." + name;
    }
}
