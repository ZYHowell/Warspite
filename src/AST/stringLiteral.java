package AST;

import Util.position;
public class stringLiteral extends exprNode {

    private String value;

    public stringLiteral(String value, position pos) {
        super(pos);
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
