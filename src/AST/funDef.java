package AST;

import java.util.ArrayList;
import Util.position;
import Util.symbol.funcDecl;


public class funDef extends ASTNode {
    private String name;
    private typeNode type;
    private blockNode body;
    private boolean isConstructor, isMethod;
    private ArrayList<varDef> parameters;   //should have no expr node(not visited in semantic checker)
    private funcDecl decl;

    public funDef(String name, position pos, boolean isConstructor,
                  typeNode type, blockNode body, ArrayList<varDef> parameters) {
        super(pos);
        this.name = name;
        this.isConstructor = isConstructor;
        this.type = type;
        this.body = body;
        this.parameters = parameters;
        this.isMethod = false;
    }

    public void setIsMethod() {
        isMethod = true;
    }
    public boolean isMethod() {
        return isMethod;
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
    public blockNode body() {
        return body;
    }
    public ArrayList<varDef> parameters() {
        return parameters;
    }
    public void setDecl(funcDecl decl) {
        this.decl = decl;
    }
    public funcDecl decl(){
        return decl;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
