package AST;

import Util.position;

public class arrayExpr extends exprNode{

    private exprNode base, width;

    public arrayExpr(exprNode base, exprNode width, position pos) {
        super(pos);
        this.base = base;
        this.width = width;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
