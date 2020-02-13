package AST;

import Util.position;

public class continueStmt extends stmtNode{

    public continueStmt(position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}