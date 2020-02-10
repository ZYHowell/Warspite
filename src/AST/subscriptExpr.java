package AST;

import Util.position;

public class subscriptExpr extends exprNode {

    public subscriptExpr(position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
