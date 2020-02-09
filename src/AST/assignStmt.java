package AST;

import java.util.ArrayList;
import Util.position;

public class assignStmt extends stmtNode {
    private ArrayList<exprNode> lhs;
    private exprNode rhs;

    public assignStmt(position pos) {
        super(pos);
    }
}
