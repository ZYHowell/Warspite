package AST;

import Util.position;
import Util.symbol.classType;

public class thisExpr extends exprNode {

    private classType pointClass;

    public thisExpr(position pos) {
        super(pos, false);
    }

    public void setPointClass(classType cT) {
        pointClass = cT;
    }

    public classType pointClass() {
        return pointClass;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
