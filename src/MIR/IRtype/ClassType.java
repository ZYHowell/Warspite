package MIR.IRtype;

import java.util.ArrayList;

public class ClassType extends IRBaseType{
    private String name;
    private int size = 0;
    private ArrayList<IRBaseType> members = new ArrayList<>();

    public ClassType(String name) {
        super();
        this.name = name;
    }

    public void addMember(IRBaseType member) {
        members.add(member);
        size += member.size();
    }

    public int getEleOff(int idx) {
        int ret = 0;
        for (int i = 0;i < idx;++i) ret += members.get(i).size();
        return ret;
    }
    public ArrayList<IRBaseType> members() {
        return members;
    }
    public String name() {
        return name;
    }

    @Override
    public int size() {
        return size;
    }
    @Override
    public String toString() {
        return "%struct." + name;
    }

    @Override
    public boolean sameType(IRBaseType o) {
        return o instanceof ClassType && ((ClassType) o).name().equals(name);
    }
}
