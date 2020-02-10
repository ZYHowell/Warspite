package AST;

import Util.position;

public class emptyStmt extends stmtNode{

    public emptyStmt(position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
