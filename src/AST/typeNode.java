package AST;

import Util.position;
import java.util.ArrayList;

public class typeNode extends ASTNode {

    private String baseTypeName;
    private int dim;

    public typeNode(String baseTypeName, int dim, position pos) {
        super(pos);
        this.baseTypeName = baseTypeName;
        this.dim = dim;
    }

}
