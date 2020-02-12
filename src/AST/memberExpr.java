package AST;

import Util.position;

public class memberExpr extends exprNode {

    private exprNode caller;
    private String member;
    public memberExpr(exprNode caller, String member, position pos) {
        super(pos, true);
        this.caller = caller;
        this.member = member;
    }

    public exprNode caller() {
        return caller;
    }

    public String member() {
        return member;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
