package AST;

import java.util.ArrayList;
import Util.position;
import Util.symbol.varEntity;

public class newExpr extends exprNode {

    private typeNode type;
    private ArrayList<exprNode> exprs;

    public newExpr(typeNode type, ArrayList<exprNode> exprs, position pos) {
        super(pos, true);
        this.type = type;
        this.exprs = exprs;
    }

    public typeNode typeN() {
        return type;
    }

    public ArrayList<exprNode> exprs() {
        return exprs;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
