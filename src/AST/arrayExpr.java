package AST;

import Util.position;
import Util.symbol.varEntity;

public class arrayExpr extends exprNode{

    private exprNode base, width;
    private varEntity var; //can only be funcDecl or varEntity

    public arrayExpr(exprNode base, exprNode width, position pos) {
        super(pos, base.isAssignable());
        this.base = base;
        this.width = width;
        this.var = base.entity();
    }

    public exprNode base() {
        return base;
    }

    public exprNode width() {
        return width;
    }

    @Override
    public varEntity entity() {
        return var;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
