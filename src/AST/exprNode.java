package AST;

import Util.position;
import Util.symbol.Type;

abstract public class exprNode extends ASTNode {
    private Type type;
    private boolean isAssignable;

    public exprNode(position pos, boolean isAssignable) {
        super(pos);
        this.isAssignable = isAssignable;
    }

    public void setAssignable(boolean dest) {
        isAssignable = dest;
    }

    public Type type() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isAssignable() {
        return isAssignable;
    }
}
