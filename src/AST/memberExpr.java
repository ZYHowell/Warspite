package AST;

import Util.position;

public class memberExpr extends exprNode {

    private exprNode caller;
    private String member;
    public memberExpr(exprNode caller, String member, position pos) {
        super(pos);
        this.caller = caller;
        this.member = member;
    }
}
