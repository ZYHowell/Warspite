package AST;

import Util.error.internalError;
import Util.position;
import Util.symbol.varEntity;

public class arrayExpr extends exprNode{

    private exprNode base, width;

    public arrayExpr(exprNode base, exprNode width, position pos) {
        super(pos, base.isAssignable());
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
    public varEntity entity() {
        throw new internalError("entity of arrayExpr??", pos());
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
