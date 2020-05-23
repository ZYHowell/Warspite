package AST;

import Util.position;

import java.util.ArrayList;

public class varDefList extends stmtNode {

    private ArrayList<varDef> varDefs = new ArrayList<>();

    public varDefList(position pos) {
        super(pos);
    }

    public void addVarDef(varDef def) {
        varDefs.add(def);
    }

    public ArrayList<varDef> getList() {
        return varDefs;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
