package AST;
import java.util.ArrayList;
import Util.position;

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
}
