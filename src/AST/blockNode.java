package AST;

import java.util.ArrayList;
import Util.position;

public class blockNode extends stmtNode{
    private ArrayList<stmtNode> stmtList = new ArrayList<>();

    public blockNode(position pos) {
        super(pos);
    }

    public ArrayList<stmtNode> getStmtList() {return stmtList;}

    public void addStmt(stmtNode stmt) {
        stmtList.add(stmt);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
