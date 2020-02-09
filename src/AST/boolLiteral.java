package AST;

import Util.position;
public class boolLiteral extends exprNode{

    private boolean value;

    public boolLiteral(boolean value, position pos) {
        super(pos);
        this.value = value;
    }
}
