package Assemb.LOperand;

import Assemb.RISCInst.Mv;

import java.util.HashSet;

public class Reg extends LOperand {
    public int degree = 0;

    public HashSet<Mv> moveInst = new HashSet<>();
    public Reg() {
        super();
    }
}
