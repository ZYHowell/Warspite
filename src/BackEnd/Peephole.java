package BackEnd;

import Assemb.LRoot;

public class Peephole {

    private LRoot root;

    public Peephole(LRoot root) {
        this.root = root;
    }

    public void run() {
        root.functions().forEach(fn -> fn.blocks().forEach(block -> {

        }));
    }
}
