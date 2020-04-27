package AST;

import Util.position;

public class nullLiteral extends exprNode{

    public nullLiteral(position pos) {
        super(pos, false);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
