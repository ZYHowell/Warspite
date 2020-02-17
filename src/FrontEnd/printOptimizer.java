package FrontEnd;

import java.util.ArrayList;
import AST.*;
import Util.position;
import Util.scope.globalScope;
import Util.symbol.funcDecl;
import Util.error.internalError;

//this is to split print(stringA + stringB) into print(stringA);print(stringB);
//the idea comes from whoever senior student's report, special thanks to him/her.
//todo
public class printOptimizer implements ASTVisitor{

    private globalScope gScope;
    private funcDecl printFunction, printlnFunction;

    private funCallExpr generatePrint(exprNode param) {
        exprList tmp = new exprList(param.pos());
        tmp.addParam(param);
        funCallExpr ret = new funCallExpr(new funcNode("print", param.pos()),
                                          tmp, param.pos());
        ret.setType(printFunction);
        return ret;
    }
    private ArrayList<exprNode> split(binaryExpr parameters, boolean newline) {
        ArrayList<exprNode> result = new ArrayList<>();
        if (parameters.src1() instanceof binaryExpr)
            result.addAll(split((binaryExpr)parameters.src1(), false));
        //warning: maybe no need to consider this
        else if (parameters.src1() instanceof funcNode || parameters.src1() instanceof stringLiteral)
            result.add(generatePrint(parameters.src1()));
        else throw new internalError("cannot split it", parameters.pos());

        if (parameters.src2() instanceof binaryExpr)
            result.addAll(split((binaryExpr)parameters.src2(), false));
        else if (parameters.src2() instanceof varNode || parameters.src2() instanceof stringLiteral)
            result.add(generatePrint(parameters.src2()));
        else throw new internalError("cannot split it", parameters.pos());

        if (newline)
            result.add(generatePrint(new stringLiteral("\n", parameters.pos())));
        return result;
    }

    public printOptimizer(globalScope gScope) {
        this.gScope = gScope;
        printFunction = gScope.getMethod("print", new position(0,0), false);
        printlnFunction = gScope.getMethod("println", new position(0,0), false);
    }

    @Override
    public void visit(rootNode it) {
        if (!it.allDef().isEmpty()) {
            it.allDef().forEach(node -> node.accept(this));
        }
    }

    @Override
    public void visit(classDef it) {
        it.members().forEach(member -> member.accept(this));
        it.methods().forEach(method -> method.accept(this));
    }

    @Override
    public void visit(funDef it) {
        it.body().accept(this);
    }

    @Override public void visit(varDef it) {}
    @Override public void visit(varDefList it) {}


    @Override
    public void visit(blockNode it) {
        it.getStmtList().forEach(stmt -> stmt.accept(this)); //cannot be null
    }


    @Override
    public void visit(exprStmt it) {
        it.expr().accept(this);
    }

    @Override
    public void visit(ifStmt it) {
        it.trueStmt().accept(this);

        if (it.falseStmt() != null) {
            it.falseStmt().accept(this);
        }
    }

    @Override
    public void visit(forStmt it) {
        it.body().accept(this);
    }

    @Override
    public void visit(whileStmt it) {
        it.body().accept(this);
    }

    @Override public void visit(returnStmt it) {}
    @Override public void visit(breakStmt it) {}
    @Override public void visit(continueStmt it) {}
    @Override public void visit(emptyStmt it) {}
    @Override public void visit(exprList it) {}
    @Override public void visit(typeNode it) {}
    @Override public void visit(arrayExpr it) {}
    @Override public void visit(assignExpr it) {}
    @Override public void visit(binaryExpr it) {}
    @Override public void visit(prefixExpr it) {}
    @Override public void visit(suffixExpr it) {}
    @Override public void visit(thisExpr it) {}

    @Override
    public void visit(funCallExpr it) {
        funcDecl func = (funcDecl)it.callee().type();
        if (func.equals(printFunction)) {
            exprNode expr = it.params().get(0);
            if (expr instanceof binaryExpr) {
                //todo: of course more
                split((binaryExpr)expr, false);
            }
        } else if (func.equals(printlnFunction)) {
            exprNode expr = it.params().get(0);
            if (expr instanceof binaryExpr) {
                //todo: of course more
                split((binaryExpr)expr, true);
            }
        }
    }

    @Override public void visit(methodExpr it) {}
    @Override public void visit(memberExpr it) {}
    @Override public void visit(newExpr it) {}
    @Override public void visit(funcNode it) {}
    @Override public void visit(varNode it) {}
    @Override public void visit(intLiteral it) {}
    @Override public void visit(boolLiteral it) {}
    @Override public void visit(nullLiteral it) {}
    @Override public void visit(stringLiteral it) {}
}
