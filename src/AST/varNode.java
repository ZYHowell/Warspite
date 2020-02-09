package AST;

import Util.position;

public class varNode extends exprNode {

    private String varName;
    private typeNode type;

    public varNode(String name, position pos) {
        super(pos);
        this.varName = name;
    }

    public void setType(typeNode type) {
        this.type = type;
    }
}
