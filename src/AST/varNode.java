package AST;

import Util.position;
import Util.symbol.varEntity;

public class varNode extends exprNode {

    private String varName;
    private varEntity entity;

    public varNode(String name, position pos) {
        super(pos, true);
        this.varName = name;
    }

    public String name() {
        return varName;
    }

    public void setVarEntity(varEntity entity) {
        this.entity = entity;
    }

    @Override
    public varEntity entity() {
        return entity;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
