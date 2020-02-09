package AST;

import Util.position;

public class ifStmt extends stmtNode {
    private exprNode condition;
    private stmtNode trueStmt, falseStmt;

    public ifStmt(exprNode condition, stmtNode trueStmt, stmtNode falseStmt, position pos) {
        super(pos);
        this.condition = condition;
        this.trueStmt  = trueStmt;
        this.falseStmt = falseStmt;
    }
}
