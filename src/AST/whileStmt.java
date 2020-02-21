package AST;

import MIR.IRBlock;
import Util.position;

public class whileStmt extends stmtNode {
    private exprNode condition;
    private stmtNode body;
    private IRBlock condBlock, destBlock;

    public whileStmt(exprNode condition, stmtNode body, position pos) {
        super(pos);
        this.condition = condition;
        this.body = body;
    }

    public exprNode condition() {
        return condition;
    }
    public stmtNode body() {
        return body;
    }
    public IRBlock destBlock() {
        return destBlock;
    }
    public void setDestBlock(IRBlock destBlock) {
        this.destBlock = destBlock;
    }
    public IRBlock condBlock() {
        return condBlock;
    }
    public void setCondBlock(IRBlock destBlock) {
        this.condBlock = destBlock;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
