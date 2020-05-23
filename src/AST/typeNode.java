package AST;

import Util.position;

public class typeNode extends ASTNode {

    private String baseTypeName;
    private int dim;

    public typeNode(String baseTypeName, int dim, position pos) {
        super(pos);
        this.baseTypeName = baseTypeName;
        this.dim = dim;
    }

    public String typeName() {
        return baseTypeName;
    }

    public int dim() {
        return dim;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
