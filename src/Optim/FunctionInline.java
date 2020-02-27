package Optim;

public class FunctionInline extends Pass{

    boolean change = false;

    public FunctionInline() {
        super();
    }

    @Override
    public boolean run() {
        change = false;
        return change;
    }
}
