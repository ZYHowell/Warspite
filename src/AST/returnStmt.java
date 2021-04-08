package AST;

import Util.position;

public class returnStmt extends stmtNode {

    private exprNode returnValue;
    private funDef dest;

    public returnStmt(exprNode returnValue, position pos) {
        super(pos);
        this.returnValue = returnValue;
    }

    public void setDest(funDef dest) {
        this.dest = dest;
    }
    public funDef dest() {
        return dest;
    }
    public exprNode retValue() {
        return returnValue;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
