package MIR.IRtype;

public class ClassType extends IRBaseType{

    private String name;

    public ClassType(String name) {
        super();
        this.name = name;
    }

    @Override
    public String toString() {
        return "%struct." + name;
    }
}
