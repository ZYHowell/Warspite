package Assemb;

import java.util.HashSet;

public class LRoot {
    HashSet<LFn> functions = new HashSet<>();

    public LRoot() {}

    public HashSet<LFn> functions() {
        return functions;
    }
    public void addFunction(LFn fn) {
        functions.add(fn);
    }
}
