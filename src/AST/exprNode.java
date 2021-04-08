package AST;

import MIR.IRBlock;
import MIR.IRoperand.Operand;
import Util.error.internalError;
import Util.position;
import Util.symbol.Type;
import Util.symbol.varEntity;

abstract public class exprNode extends ASTNode {
    private Type type;
    private boolean isAssignable;
    private Operand operand;
    private Operand address;
    private IRBlock thenBlock = null, elseBlock = null;

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
    public void setAddress(Operand address) {
        if (!isAssignable()) throw new internalError("set address of a not assignable expr", pos());
        this.address = address;
    }
    public Operand address() {
        if (!isAssignable()) throw new internalError("get address of a not assignable expr", pos());
        return address;
    }
    public void setThenBlock(IRBlock thenBlock) {
        this.thenBlock = thenBlock;
    }
    public IRBlock thenBlock() {
        return thenBlock;
    }
    public void setElseBlock(IRBlock elseBlock) {
        this.elseBlock = elseBlock;
    }
    public IRBlock elseBlock() {
        return elseBlock;
    }

    public varEntity entity() {
        throw new internalError("call an entity() not overridden", pos());
    }
}
