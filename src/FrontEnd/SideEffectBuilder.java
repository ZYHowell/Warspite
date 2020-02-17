package FrontEnd;

import AST.*;
import Util.error.internalError;
import Util.scope.globalScope;
import Util.symbol.classType;
import Util.symbol.funcDecl;
import Util.symbol.varEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SideEffectBuilder implements ASTVisitor{

    private globalScope gScope;
    private funcDecl currentFunction;
    private HashSet<funcDecl> hasEffect = new HashSet<>();
    private HashMap<funcDecl, ArrayList<funcDecl>> caller = new HashMap<>();

    public SideEffectBuilder(globalScope gScope) {
        this.gScope = gScope;
    }

    private void spread(funcDecl it) {
        if (it.hasSideEffect()) return;
        it.setSideEffect(true);
        ArrayList<funcDecl> callers = caller.get(it);
        callers.forEach(this::spread);
    }
    private void setHasEffect(funcDecl func) {
        hasEffect.add(func);
        func.setSideEffect(true);
    }


    @Override
    public void visit(rootNode it) {
        it.allDef().forEach(node -> node.accept(this));
        for (funcDecl func : hasEffect) spread(func);
    }


    @Override
    public void visit(classDef it) {
        it.methods().forEach(method -> method.accept(this));
        it.constructors().forEach(constructor -> constructor.accept(this));
    }

    @Override
    public void visit(funDef it) {
        currentFunction = gScope.getMethod(it.Identifier(), it.pos(), false);
        caller.put(currentFunction, new ArrayList<>());
        if (!it.isConstructor()) it.body().accept(this);
        else currentFunction.setSideEffect(true);   //roughly set all constructor to be true; can be hacked
        currentFunction = null;
    }

    @Override
    public void visit(varDef it) {
        if (currentFunction == null) return;
        if (it.init() != null) it.init().accept(this);
    }

    @Override public void visit(varDefList it) {}

    @Override
    public void visit(blockNode it) {
        it.getStmtList().forEach(stmt -> stmt.accept(this));
    }

    @Override
    public void visit(exprStmt it) {
        it.expr().accept(this);
    }
    @Override
    public void visit(ifStmt it) {
        it.condition().accept(this);
        it.trueStmt().accept(this);
        if (it.falseStmt() != null) it.falseStmt().accept(this);
    }
    @Override
    public void visit(forStmt it) {
        if (it.init() != null) it.init().accept(this);
        if (it.incr() != null) it.incr().accept(this);
        if (it.condition() != null) it.condition().accept(this);
        it.body().accept(this);
    }
    @Override
    public void visit(whileStmt it) {
        it.condition().accept(this);
        it.body().accept(this);
    }
    @Override
    public void visit(returnStmt it) {
        if (it.retValue() != null) {
            it.retValue().accept(this);
            if (it.retValue().isAssignable()) {
                if (it.retValue().entity().isOuter())
                    setHasEffect(currentFunction);
            }
            else if (it.retValue() instanceof thisExpr) {
                setHasEffect(currentFunction);
            }
        }
    }
    @Override public void visit(breakStmt it) {}
    @Override public void visit(continueStmt it) {}
    @Override public void visit(emptyStmt it) {}

    @Override
    public void visit(exprList it) {
        it.params().forEach(expr -> expr.accept(this));
    }
    @Override public void visit(typeNode it) {}

    @Override
    public void visit(arrayExpr it) {
        it.base().accept(this);
        it.width().accept(this);
    }
    @Override
    public void visit(binaryExpr it) {
        it.src1().accept(this);
        it.src2().accept(this);
    }
    @Override
    public void visit(assignExpr it) {
        if (it.src1().entity().isOuter()) setHasEffect(currentFunction);
    }
    @Override
    public void visit(prefixExpr it) {
        it.src().accept(this);
        if (it.opCode().ordinal() < 5 && it.opCode().ordinal() > 2)
            if (it.src().entity().isOuter()) setHasEffect(currentFunction);
    }
    @Override
    public void visit(suffixExpr it) {
        it.src().accept(this);
        if (it.src().entity().isOuter()) setHasEffect(currentFunction);
    }
    @Override
    public void visit(thisExpr it) {}
    @Override
    public void visit(funCallExpr it) {
        it.callee().accept(this);
        it.params().forEach(param -> param.accept(this));
        caller.get((funcDecl)it.callee().type()).add(currentFunction);
    }
    @Override
    public void visit(methodExpr it) {
        it.caller().accept(this);

    }
    @Override
    public void visit(memberExpr it) {
        it.caller().accept(this);
    }
    @Override
    public void visit(newExpr it) {
        it.exprs().forEach(expr -> expr.accept(this));
        if (it.type().isClass()) {
            funcDecl constructor = ((classType) it.type()).scope().constructor();
            if (constructor != null) {
                caller.get(constructor).add(currentFunction);
            }
        }
    }

    @Override public void visit(funcNode it) {}
    @Override public void visit(varNode it) {}
    @Override public void visit(intLiteral it) {}
    @Override public void visit(boolLiteral it) {}
    @Override public void visit(nullLiteral it) {}
    @Override public void visit(stringLiteral it) {}
}
