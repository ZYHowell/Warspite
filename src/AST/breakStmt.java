package AST;

import Util.position;

public class breakStmt extends stmtNode {

    private ASTNode dest;

    public breakStmt(position pos) {
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
