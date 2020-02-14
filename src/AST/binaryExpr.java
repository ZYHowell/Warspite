package AST;

import Util.position;

public class binaryExpr extends exprNode{
    public enum opCategory {
        Star, Div, Mod, LeftShift, RightShift, And, Or, Caret, Minus, Plus,
        Less, Greater, LessEqual, GreaterEqual, AndAnd, OrOr,
        Equal, NotEqual, Assign
    }
    /*
     * *, /, %, <<, >>, &, |, ^, -, +,  <,  >, <=, >=, &&, ||, ==, !=, =
     * 0, 1, 2,  3,  4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18
     * 0~9: only int, return int
     * 9~13: int, String
     * 14~15: only int, return boolean
     * 16~18: all same type(include class and array)
     */
    private exprNode src1, src2;
    private opCategory opCode;

    public binaryExpr(exprNode src1, exprNode src2, opCategory opCode, position pos) {
        super(pos, false);
        //the isAssignable of a=b is set manually later.
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

    public opCategory opCode() {
        return opCode;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
