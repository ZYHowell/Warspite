package AST;

import java.util.ArrayList;
import Util.position;
import Util.type.*;

public class newExpr extends exprNode {

    private typeNode type;
    private ArrayList<exprNode> exprs;

    public newExpr(typeNode type, ArrayList<exprNode> exprs, position pos) {
        super(pos);
        this.type = type;
        this.exprs = exprs;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
