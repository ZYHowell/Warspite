package AST;

import java.util.ArrayList;
import Util.position;

//the exprList appears only in funcCalls
public class exprList extends ASTNode {
    private ArrayList<exprNode> params = new ArrayList<>();

    public exprList(position pos) {
        super(pos);
    }

    public void addParam(exprNode param) {
        params.add(param);
    }

    public ArrayList<exprNode> params() {
        return params;
    }
}
