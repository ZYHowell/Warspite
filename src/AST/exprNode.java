package AST;

import Util.position;
import Util.type.*;

abstract public class exprNode extends ASTNode {
    private Type type;

    public exprNode(position pos) {
        super(pos);
    }

    public Type type() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
