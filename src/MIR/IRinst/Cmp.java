package MIR.IRinst;

public class Cmp extends Inst{
    public enum CmpOpCategory {
        Less, Greater, LessEqual, GreaterEqual, AndAnd, OrOr,
        Equal, NotEqual
    }

    public Cmp() {
        super();
    }
}
