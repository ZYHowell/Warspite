package AST;

import Util.position;
import java.util.ArrayList;

public class varDefList extends ASTNode {

    private ArrayList<varDef> varDefs;

    public varDefList(position pos) {
        super(pos);
    }

    public void addVarDef(varDef def) {
        varDefs.add(def);
    }

    public ArrayList<varDef> getList() {
        return varDefs;
    }
}
