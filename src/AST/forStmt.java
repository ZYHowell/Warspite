package AST;

import Util.position;

public class forStmt extends stmtNode{
    private exprNode condition, incr, init;
    private stmtNode body;

    public forStmt(exprNode init, exprNode incr, exprNode condition, stmtNode body, position pos) {
        super(pos);
        this.init = init;
        this.incr = incr;
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
