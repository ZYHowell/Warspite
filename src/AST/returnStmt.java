package AST;

import Util.position;

public class returnStmt extends stmtNode {

    private exprNode returnValue;   //mention that this one can be null

    public returnStmt(exprNode returnValue, position pos) {
        super(pos);
        this.returnValue = returnValue;
    }
}
