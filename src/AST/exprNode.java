package AST;

import Util.error.internalError;
import Util.position;
import Util.symbol.Type;
import Util.symbol.varEntity;

abstract public class exprNode extends ASTNode {
    private Type type;
    private boolean isAssignable;

    public exprNode(position pos, boolean isAssignable) {
        super(pos);
        this.isAssignable = isAssignable;
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

    public varEntity entity() {
        throw new internalError("call an entity() not overridden", pos());
    }
}
