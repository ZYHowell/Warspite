package AST;

import Util.position;

public class suffixExpr extends exprNode {

    private exprNode src;
    private int opCode;//++: 0, --: 1;

    public suffixExpr(exprNode src, int opCode, position pos) {
        super(pos, false);
        this.src = src;
        this.opCode = opCode;
    }

    public int opCode() {return opCode;}

    public exprNode src() {
        return src;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
