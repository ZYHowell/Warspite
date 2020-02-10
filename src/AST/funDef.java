package AST;

import java.util.ArrayList;
import Util.position;
import Util.type.Type;

public class funDef extends ASTNode {
    private String name;
    private typeNode type;
    private blockNode body;
    private boolean isConstructor;
    private ArrayList<varDef> parameters;

    public funDef(String name, position pos, boolean isConstructor,
                  typeNode type, blockNode body, ArrayList<varDef> parameters) {
        super(pos);
        this.name = name;
        this.isConstructor = isConstructor;
        this.type = type;
        this.body = body;
        this.parameters = parameters;
    }

    public boolean isConstructor() {
        return isConstructor;
    }

    public typeNode retValueType() {
        return type;
    }

    public String Identifier() {
        return name;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
