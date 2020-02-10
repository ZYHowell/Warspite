package AST;

import Util.position;

public class suffixExpr extends exprNode {

    private exprNode src;
    private int opCode;//++: 0, --: 1;

    public suffixExpr(exprNode src, int opCode, position pos) {
        super(pos);
        this.src = src;
        this.opCode = opCode;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
