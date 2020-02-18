package AST;

import Util.error.internalError;
import Util.position;
import Util.symbol.Type;
import Util.symbol.varEntity;
import MIR.IRoperand.Operand;

abstract public class exprNode extends ASTNode {
    private Type type;
    private boolean isAssignable;
    private Operand operand;

    public exprNode(position pos, boolean isAssignable) {
        super(pos);
        this.isAssignable = isAssignable;
    }

    public void setType(Type type) {
        this.type = type;
    }
    public Type type() {
        return type;
    }
    public boolean isAssignable() {
        return isAssignable;
    }

    public void setOperand(Operand operand) {
        this.operand = operand;
    }
    public Operand operand() {
        return operand;
    }

    public varEntity entity() {
        throw new internalError("call an entity() not overridden", pos());
    }
}
