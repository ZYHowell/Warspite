package AST;

import Util.position;

public class methodExpr extends exprNode{

    private exprNode caller;
    private String method;
    public methodExpr(exprNode caller, String method, position pos) {
        super(pos, false);
        this.caller = caller;
        this.method = method;
    }

    public exprNode caller() {
        return caller;
    }

    public String method() {
        return method;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
