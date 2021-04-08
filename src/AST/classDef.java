package AST;

import Util.position;

import java.util.ArrayList;

public class classDef extends ASTNode {
    private String identifier;
    private ArrayList<varDef> variables;
    private ArrayList<funDef> methods;
    private ArrayList<funDef> constructors;
    private boolean hasConstructor;

    public classDef(String identifier, position pos,
                    ArrayList<varDef> variables, ArrayList<funDef> methods,
                    ArrayList<funDef> constructors, boolean hasConstructor
                    ) {
        super(pos);
        this.identifier = identifier;
        this.variables = variables;
        this.methods = methods;
        this.constructors = constructors;
        this.hasConstructor = hasConstructor;
    }

    public String Identifier() {
        return identifier;
    }

    public ArrayList<varDef> members(){ return variables; }

    public ArrayList<funDef> methods(){ return methods; }

    public ArrayList<funDef> constructors(){ return constructors; }

    public boolean hasConstructor() {
        return hasConstructor;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
