package AST;

import Util.position;

public class whileStmt extends stmtNode {
    private exprNode condition;
    private stmtNode body;

    public whileStmt(exprNode condition, stmtNode body, position pos) {
        super(pos);
        this.condition = condition;
        this.body = body;
    }

    public exprNode condition() {
        return condition;
    }
    public stmtNode body() {
        return body;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
