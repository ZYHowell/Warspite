package FrontEnd;

import AST.*;
import Util.error.internalError;
import Util.position;
import Util.scope.globalScope;
import Util.symbol.Type;
import Util.symbol.funcDecl;

import java.util.ArrayList;
import java.util.HashSet;

public class printOptimizer implements ASTVisitor{

    private funcDecl printFunction, printlnFunction, printIntFn, printlnIntFn, toStringFn;
    private HashSet<stmtNode> canSplit = new HashSet<>();
    private Type voidInstance;

    private stringLiteral toLiteral(String it, position pos) {
        return new stringLiteral("\"" + it + "\"", pos);
    }
    private String stringResolve(stringLiteral it) {
        String ret = it.value();
        return ret.substring(1, ret.length() - 1);
    }
    private exprStmt generatePrint(exprNode param, boolean newLine) {
        exprList tmp = new exprList(param.pos());
        tmp.addParam(param);
        funCallExpr ret;
        if (!newLine) {
            funcNode func = new funcNode("print", param.pos());
            ret = new funCallExpr(func, tmp, param.pos());
            func.setType(printFunction);
            ret.setType(voidInstance);
        } else {
            funcNode func = new funcNode("println", param.pos());
            ret = new funCallExpr(func, tmp, param.pos());
            func.setType(printlnFunction);
            ret.setType(voidInstance);
        }
        return new exprStmt(ret, param.pos());
    }

    private ArrayList<exprNode> unfold(binaryExpr expr) {
        ArrayList<exprNode> ret = new ArrayList<>();
        exprNode lhs = expr.src1(), rhs = expr.src2();
        if (lhs instanceof binaryExpr) {
            ret = unfold((binaryExpr)lhs);
        }
        else ret.add(lhs);
        if (rhs instanceof binaryExpr) ret.addAll(unfold((binaryExpr) rhs));
        else ret.add(rhs);
        for (int i = 0; i < ret.size();++i) {
            exprNode arg = ret.get(i);
            if (arg instanceof funCallExpr &&
                ((funCallExpr) arg).callee().type().equals(toStringFn)) {
                exprNode param =  ((funCallExpr) arg).params().get(0);
                if (param instanceof intLiteral)
                    ret.set(i, toLiteral("" + ((intLiteral) param).value(), param.pos()));
            }
        }
        return ret;
    }

    private blockNode splitPrint(stmtNode it) {
        if (!(it instanceof exprStmt)) throw new internalError("split stmt not expr", it.pos());
        exprNode ex = ((exprStmt) it).expr();
        if (!(ex instanceof funCallExpr)) throw new internalError("split expr not print", it.pos());
        funCallExpr fn = (funCallExpr) ex;
        blockNode printBlock = new blockNode(it.pos());
        if (fn.callee().type().equals(printFunction)) {
            if (!(fn.params().get(0) instanceof binaryExpr))
                throw new internalError("split not print str1 + str2", it.pos());
            binaryExpr param = (binaryExpr)fn.params().get(0);
            ArrayList<exprNode> args = unfold(param);
            StringBuilder currentString = null;
            for (exprNode arg : args) {
                if (arg instanceof stringLiteral) {
                    if (currentString == null)
                        currentString = new StringBuilder(stringResolve((stringLiteral) arg));
                    else currentString.append(stringResolve((stringLiteral) arg));
                } else {
                    if (currentString != null) {
                        stringLiteral tmp = toLiteral(currentString.toString(), arg.pos());
                        printBlock.addStmt(generatePrint(tmp, false));
                        currentString = null;
                    }
                    printBlock.addStmt(generatePrint(arg, false));
                }
            }
            if (currentString != null) {
                stringLiteral tmp = toLiteral(currentString.toString(), it.pos());
                printBlock.addStmt(generatePrint(tmp, false));
            }
            return printBlock;
        }
        else if (fn.callee().type().equals(printlnFunction)) {
            if (!(fn.params().get(0) instanceof binaryExpr))
                throw new internalError("split not print str1 + str2", it.pos());
            binaryExpr param = (binaryExpr)fn.params().get(0);
            ArrayList<exprNode> args = unfold(param);
            StringBuilder currentString = null;
            for (int i = 0;i < args.size();++i) {
                exprNode arg = args.get(i);
                if (arg instanceof stringLiteral) {
                    if (currentString == null)
                        currentString = new StringBuilder(stringResolve((stringLiteral) arg));
                    else currentString.append(stringResolve((stringLiteral) arg));
                } else {
                    if (currentString != null) {
                        stringLiteral tmp = toLiteral(currentString.toString(), arg.pos());
                        printBlock.addStmt(generatePrint(tmp, false));
                        currentString = null;
                    }
                    printBlock.addStmt(generatePrint(arg, i == args.size() - 1));
                }
            }
            if (currentString != null) {
                stringLiteral tmp = toLiteral(currentString.toString(), it.pos());
                printBlock.addStmt(generatePrint(tmp, true));
            }
            return printBlock;
        }
        else throw new internalError("split function not print", it.pos());
    }

    public printOptimizer(globalScope gScope) {
        position tmp = new position(0, 0);
        printFunction = gScope.getMethod("print",tmp, false);
        printlnFunction = gScope.getMethod("println", tmp, false);
        printIntFn = gScope.getMethod("printInt", tmp, false);
        printlnIntFn = gScope.getMethod("printlnInt", tmp, false);
        toStringFn = gScope.getMethod("toString", tmp, false);
        voidInstance = printFunction.returnType();
    }

    @Override
    public void visit(rootNode it) {
        if (!it.allDef().isEmpty())
            it.allDef().forEach(node -> node.accept(this));
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
        ArrayList<stmtNode> stmtList = new ArrayList<>(it.getStmtList());
        for (int i = 0; i < stmtList.size(); i++) {
            stmtNode stmt = stmtList.get(i);
            stmt.accept(this);
            if (canSplit.contains(stmt)) {
                it.getStmtList().set(i, splitPrint(stmt));
            }
        }
    }


    @Override
    public void visit(exprStmt it) {
        it.expr().accept(this);
        if (it.expr() instanceof funCallExpr) {
            funCallExpr ca = (funCallExpr) it.expr();
            funcDecl func = (funcDecl) ca.callee().type();
            if (func.equals(printFunction)) {
                if (ca.params().get(0) instanceof binaryExpr) canSplit.add(it);
            } else if (func.equals(printlnFunction)) {
                if (ca.params().get(0) instanceof binaryExpr) canSplit.add(it);
            }
        }
    }

    @Override
    public void visit(ifStmt it) {
        it.trueStmt().accept(this);
        if (canSplit.contains(it.trueStmt())) {
            it.trueStmt = splitPrint(it.trueStmt());
        }

        if (it.falseStmt() != null) {
            it.falseStmt().accept(this);
            if (canSplit.contains(it.falseStmt())) {
                it.falseStmt = splitPrint(it.falseStmt());
            }
        }
    }

    @Override
    public void visit(forStmt it) {
        it.body().accept(this);
        if (canSplit.contains(it.body())) {
            it.body = splitPrint(it.body());
        }
    }

    @Override
    public void visit(whileStmt it) {
        it.body().accept(this);
        if (canSplit.contains(it.body())) {
            it.body = splitPrint(it.body());
        }
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
            if (expr instanceof funCallExpr &&
                    ((funCallExpr) expr).callee().type().equals(toStringFn)) {
                funcNode callee = new funcNode("printInt", it.pos());
                callee.setType(printIntFn);
                it.resetCallee(callee);

                it.params().clear();
                it.params().add(((funCallExpr) expr).params().get(0));
            }
        } else if (func.equals(printlnFunction)) {
            exprNode expr = it.params().get(0);
            if (expr instanceof funCallExpr &&
                    ((funCallExpr) expr).callee().type().equals(toStringFn)) {
                funcNode callee = new funcNode("printlnInt", it.pos());
                callee.setType(printlnIntFn);
                it.resetCallee(callee);

                it.params().clear();
                it.params().add(((funCallExpr) expr).params().get(0));
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
