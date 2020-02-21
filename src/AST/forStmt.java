package AST;

import MIR.IRBlock;
import Util.position;

public class forStmt extends stmtNode{
    private exprNode condition, incr, init;
    private stmtNode body;
    private IRBlock condBlock, destBlock;

    public forStmt(exprNode init, exprNode incr, exprNode condition, stmtNode body, position pos) {
        super(pos);
        this.init = init;
        this.incr = incr;
        this.condition = condition;
        this.body = body;
    }

    public exprNode condition() {
        return condition;
    }
    public exprNode incr() {
        return incr;
    }
    public exprNode init() {
        return init;
    }
    public stmtNode body() {
        return body;
    }
    public IRBlock condBlock() {
        return condBlock;
    }
    public void setCondBlock(IRBlock condBlock) {
        this.condBlock = condBlock;
    }
    public IRBlock destBlock() {
        return destBlock;
    }
    public void setDestBlock(IRBlock destBlock) {
        this.destBlock = destBlock;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
