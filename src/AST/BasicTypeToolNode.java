package AST;

import Util.error.internalError;
import Util.position;

public class BasicTypeToolNode extends ASTNode {

    String value;

    public BasicTypeToolNode(String value, position pos) {
        super(pos);
        this.value = value;
    }

    public String value() {
        return value;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        throw new internalError("visit basic type tool node", pos());
    }
}
