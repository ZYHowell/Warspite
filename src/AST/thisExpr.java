package AST;

import Util.position;
import Util.symbol.classType;
import Util.symbol.varEntity;

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
    public varEntity entity() {
        return pointClass.formalEntity();
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
