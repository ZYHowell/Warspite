package AST;

import Util.position;

public class prefixExpr extends exprNode{

    private exprNode src;
    private int opCode;
    /*
     * +, -, ++, --, ~, !
     * 0, 1,  2,  3, 4, 5
     * 0~4: int
     * 5: boolean
     */

    public prefixExpr(exprNode src, int opCode, position pos) {
        super(pos);
        this.src = src;
        this.opCode = opCode;
    }
}
