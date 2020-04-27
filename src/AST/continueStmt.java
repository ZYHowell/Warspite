package AST;

import Util.position;

public class continueStmt extends stmtNode{

    private ASTNode dest;

    public continueStmt(position pos) {
        super(pos);
    }

    public void setDest(ASTNode dest) {
        this.dest = dest;
    }
    public ASTNode dest() {
        return dest;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
