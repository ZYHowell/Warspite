package AST;

import Util.position;

public class stringLiteral extends exprNode {

    private String value;

    public stringLiteral(String value, position pos) {
        super(pos, false);
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
