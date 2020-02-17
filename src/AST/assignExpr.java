package AST;

import Util.position;

public class assignExpr extends exprNode {

    private exprNode src1, src2;

    public assignExpr(exprNode src1, exprNode src2, boolean assignable, position pos) {
        super(pos, assignable);
        //the isAssignable of a=b is set manually later.
        this.src1 = src1;
        this.src2 = src2;
    }

    public exprNode src1() {
        return src1;
    }

    public exprNode src2() {
        return src2;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
