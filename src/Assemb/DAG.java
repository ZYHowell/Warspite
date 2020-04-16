package Assemb;

import Assemb.LOperand.Reg;

import java.util.HashMap;
import java.util.HashSet;

public class DAG {

    private HashMap<Reg, HashSet<Reg>> connected = new HashMap<>();
    public DAG() {}

    private void addNode(Reg a, Reg b) {
        if (!connected.containsKey(a))  connected.put(a, new HashSet<>());
        connected.get(a).add(b);
        ++a.degree;
    }
    public void addEdge(Reg a, Reg b) {
        if (a != b){
            addNode(a, b);
            addNode(b, a);
        }
    }
    public HashSet<Reg> adjacent(Reg a) {
        return new HashSet<>(connected.get(a));
    }
    public boolean connected(Reg a, Reg b) {
        return connected.get(a).contains(b);
    }
}
