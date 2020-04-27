package Assemb;

import Assemb.LOperand.Reg;
import Assemb.LOperand.VirtualReg;

import java.util.HashMap;
import java.util.HashSet;

public class DAG {

    private HashMap<Reg, HashSet<Reg>> connected = new HashMap<>();
    private HashMap<Reg, HashSet<Reg>> adjacentMap = new HashMap<>();
    private HashSet<Reg> initial = new HashSet<>();
    public boolean initCollect = true;
    public DAG() {}

    public void init() {
        connected = new HashMap<>();
        adjacentMap = new HashMap<>();
    }
    private void addNode(Reg a, Reg b) {
        if (!connected.containsKey(a))  {
            connected.put(a, new HashSet<>());
            adjacentMap.put(a, new HashSet<>());
            if (initCollect && a instanceof VirtualReg) initial.add(a);
        }
        if (connected.get(a).contains(b)) return;
        if (a instanceof VirtualReg) {
            adjacentMap.get(a).add(b);
            ++a.degree;
        }
        connected.get(a).add(b);
    }
    public HashSet<Reg> initial() {
        return initial;
    }
    public void addEdge(Reg a, Reg b) {
        if (a != b){
            addNode(a, b);
            addNode(b, a);
        }
    }
    public HashSet<Reg> adjacent(Reg a) {
        return new HashSet<>(adjacentMap.get(a));
    }
    public boolean connected(Reg a, Reg b) {
        return connected.get(a).contains(b);
    }
}
