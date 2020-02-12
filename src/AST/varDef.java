package AST;
import Util.position;

public class varDef extends stmtNode {
    private typeNode type;
    private String name;
    private exprNode init;

    public varDef(String name, exprNode expr, position pos) {
        super(pos);
        this.name = name;
        this.init = expr;
    }

    public String name() {
        return name;
    }

    public typeNode type() {
        return type;
    }

    public void setTypeNode(typeNode type) {
        this.type = type;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
