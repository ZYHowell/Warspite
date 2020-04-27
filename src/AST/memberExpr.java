package AST;

import Util.position;
import Util.symbol.varEntity;

public class memberExpr extends exprNode {

    private exprNode caller;
    private String member;
    private varEntity entity;

    public memberExpr(exprNode caller, String member, position pos) {
        super(pos, true);
        this.caller = caller;
        this.member = member;
    }

    public exprNode caller() {
        return caller;
    }

    public void setVarEntity(varEntity entity) {
        this.entity = entity;
    }

    @Override
    public varEntity entity() {
        return entity;
    }

    public String member() {
        return member;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
