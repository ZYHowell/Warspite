package AST;

import Util.position;

public class nullLiteral extends exprNode{

    public nullLiteral(position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
