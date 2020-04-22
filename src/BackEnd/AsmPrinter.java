package BackEnd;

import Assemb.LFn;
import Assemb.LRoot;

public class AsmPrinter {

    private LRoot root;
    public AsmPrinter(LRoot root) {
        this.root = root;
    }

    private void runForFn(LFn fn) {

    }

    public void run() {
        root.functions().forEach(this::runForFn);
    }
}
