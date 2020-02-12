package AST;

import Util.position;

public class arrayExpr extends exprNode{

    private exprNode base, width;

    public arrayExpr(exprNode base, exprNode width, position pos) {
        super(pos, true);
        this.base = base;
        this.width = width;
    }

    public exprNode base() {
        return base;
    }

    public exprNode width() {
        return width;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
