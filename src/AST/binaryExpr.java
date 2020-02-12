package AST;

import Util.position;

public class binaryExpr extends exprNode{

    private exprNode src1, src2;
    private int opCode;
    /*
     * *, /, %, <<, >>, &, |, ^, -, +,  <,  >, <=, >=, &&, ||, ==, !=, =
     * 0, 1, 2,  3,  4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18
     * 0~9: only int
     * 9~13: int, String
     * 14~15: boolean
     * 16~18: all same type(include class and array)
     */

    public binaryExpr(exprNode src1, exprNode src2, int opCode, position pos) {
        super(pos, false);
        this.src1 = src1;
        this.src2 = src2;
        this.opCode = opCode;
    }

    public exprNode src1() {
        return src1;
    }

    public exprNode src2() {
        return src2;
    }

    public int opCode() {
        return opCode;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
