package AST;
import Util.position;
import Util.symbol.varEntity;

public class varDef extends stmtNode {
    private typeNode type;
    private String name;
    private exprNode init;
    private varEntity entity;

    public varDef(String name, exprNode expr, position pos) {
        super(pos);
        this.name = name;
        this.init = expr;
    }

    public void setTypeNode(typeNode type) {
        this.type = type;
    }
    public void setEntity(varEntity entity) {
        this.entity = entity;
    }
    public varEntity entity() {
        return entity;
    }
    public String name() {
        return name;
    }
    public typeNode type() {
        return type;
    }
    public exprNode init() {
        return init;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
