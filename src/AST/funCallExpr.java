package AST;

import java.util.ArrayList;
import Util.position;

public class funCallExpr extends exprNode {

    private ArrayList<exprNode> params;
    private exprNode callee;

    public funCallExpr(exprNode callee, exprList params, position pos) {
        super(pos);
        this.callee = callee;
        this.params = params == null ? new ArrayList<>() : params.params();
    }
}
