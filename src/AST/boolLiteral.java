package AST;

import Util.position;
public class boolLiteral extends exprNode{

    private boolean value;

    public boolLiteral(boolean value, position pos) {
        super(pos, false);
        this.value = value;
    }

    public boolean value() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
