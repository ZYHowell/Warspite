package MIR.IRinst;

public class Binary extends Inst {
    public enum BinaryOpCategory {
        Star, Div, Mod, LeftShift, RightShift, And, Or, Caret, Minus, Plus
    }

    public Binary() {
        super();
    }
}
