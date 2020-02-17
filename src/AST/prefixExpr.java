package AST;

import Util.position;

public class prefixExpr extends exprNode{

    public enum prefixCode {
        Positive, Negative, Tilde, Increment, Decrement, Not
    }
    private exprNode src;
    private prefixCode opCode;
    /*
     * +, -, ~, ++, --, !
     * 0, 1, 2,  3,  4, 5
     * 0~4: int
     * 5: boolean
     */

    public prefixExpr(exprNode src, prefixCode opCode, position pos) {
        super(pos, false);
        this.src = src;
        this.opCode = opCode;
    }

    public prefixCode opCode() {
        return opCode;
    }

    public exprNode src() {
        return src;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
