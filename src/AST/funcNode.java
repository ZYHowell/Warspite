package AST;

import Util.position;

public class funcNode extends exprNode {

    private String funcName;

    public funcNode(String name, position pos) {
        super(pos, false);
        this.funcName = name;
    }

    public String name() {
        return funcName;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
