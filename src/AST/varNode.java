package AST;

import Util.position;

public class varNode extends exprNode {

    private String varName;

    public varNode(String name, position pos) {
        super(pos, true);
        this.varName = name;
    }

    public String name() {
        return varName;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
