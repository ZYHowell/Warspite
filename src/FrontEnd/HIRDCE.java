package FrontEnd;

import AST.*;
import Util.error.internalError;
import Util.scope.globalScope;
import Util.symbol.varEntity;
import Util.symbol.funcDecl;

import java.util.ArrayList;
import java.util.HashSet;


/*
 * this is a high level IR dead code eliminator, suggested by @merrymercy
 * a varEntity can be: 1.used(safe/use); 2.side-effectively used or defined(unsafe/dirty).

 * Side-effective: as a parameter, it is changed || global/member is changed; both are isOuter().
 * this DCE is rough, only used to decrease the size of SSA form CFG
 * 1. collect all print/println nodes.
 *    collect all information about dangerous funcCall(may have side effect)
 *      dangerous: 1) defines outer variable; 2)uses dangerous function/method;
 * 2. flow the calling graph to get the final dangerous funcCall list
 *
 * this is a really really rough one(even can be called a data-oriented one)
 */
public class HIRDCE implements ASTVisitor{

    private enum stage {
        init, check, clean
    }

    private globalScope gScope;
    stage currentStage = stage.init;
    private HashSet<varEntity> relevantVars = new HashSet<>();
    private varEntity formalRelevantEntity = new varEntity("", null, true);

    public HIRDCE(globalScope gScope) {
        this.gScope = gScope;
        relevantVars.add(formalRelevantEntity);
    }


    private boolean isInit() {return currentStage == stage.init;}
    private boolean isCheck() {return currentStage == stage.check;}
    private boolean isClean() {return currentStage == stage.clean;}

    private void derive(ASTNode parent, ASTNode child) {
        parent.dirty().addAll(child.dirty());
        parent.use().addAll(child.use());
    }

    private boolean irrelevant(ASTNode it) {
        HashSet<varEntity> tmp = new HashSet<>(it.dirty());
        tmp.retainAll(relevantVars);
        return tmp.isEmpty();
    }
    private void heritage(ASTNode parent, ASTNode child) {
        if (irrelevant(parent)) relevantVars.addAll(child.use());
    }
    private void heritage(ASTNode parent, ArrayList<? extends ASTNode> children) {
        if (irrelevant(parent)) {
            children.forEach(child -> {
                relevantVars.addAll(child.use());   //this is a really really rough one
            });
        }
    }
    private void eliminate(ArrayList<stmtNode> block) {
        HashSet<stmtNode> tmp = new HashSet<>();
        block.forEach(stmt -> {
            if (irrelevant(stmt)) tmp.add(stmt);
            stmt.accept(this);
        });
        block.removeAll(tmp);
    }


    @Override
    public void visit(rootNode it) {
        currentStage = stage.init;
        it.allDef().forEach(node -> node.accept(this));
        currentStage = stage.check;
        int backup;
        do{
            backup = relevantVars.size();
            it.allDef().forEach(node -> {
                if (node instanceof funDef) //should improve later
                    node.accept(this);
            });
        } while(backup != relevantVars.size());
        currentStage = stage.clean;
        it.allDef().forEach(node -> {
            if (node instanceof funDef) //should improve later
                node.accept(this);
        });
    }


    @Override
    public void visit(classDef it) {
        //should improve later
    }

    @Override
    public void visit(funDef it) {
        if (isInit()) {
            it.body().accept(this);
        }
        else if (isCheck()) {
            it.body().accept(this);
        }
        else {
            it.body().accept(this);
        }
    }

    @Override
    public void visit(varDef it) {
        if (isInit()) {
            if (it.init() != null) {
                it.init().accept(this);
                derive(it, it.init());
                it.dirty().add(it.entity());
            }
        }
        else if (isCheck()) {
            if (it.init() != null) {
                heritage(it, it.init());
                it.init().accept(this);
            }
        }
    }

    @Override
    public void visit(varDefList it) {
        throw new internalError("access an varDefList", it.pos());
    }

    @Override
    public void visit(blockNode it) {
        if (isInit()) {
            it.getStmtList().forEach(stmt -> {
                stmt.accept(this);
                derive(it, stmt);
            });
        }
        else if (isCheck()) {
            it.getStmtList().forEach(stmt -> stmt.accept(this));
        }
        else {
            eliminate(it.getStmtList());
        }
    }

    @Override
    public void visit(exprStmt it) {
        if (isInit()) {
            it.expr().accept(this);
            derive(it, it.expr());
        }
        else if (isCheck()) {
            heritage(it, it.expr());
        }
    }
    @Override
    public void visit(ifStmt it) {
        if (isInit()) {
            it.condition().accept(this);
            derive(it, it.condition());
            it.trueStmt().accept(this);
            derive(it, it.trueStmt());
            if (it.falseStmt() != null) {
                it.falseStmt().accept(this);
                derive(it, it.falseStmt());
            }
        }
        else if (isCheck()) {
            heritage(it, it.condition());
            it.condition().accept(this);
            heritage(it, it.trueStmt());
            it.trueStmt().accept(this);
            if (it.falseStmt() != null) {
                heritage(it, it.falseStmt());
                it.falseStmt().accept(this);
            }
        }
        else {
            it.trueStmt().accept(this);
            if (it.falseStmt() != null) it.falseStmt().accept(this);
        }
    }
    @Override
    public void visit(forStmt it) {
        if (isInit()) {
            if (it.init() != null) {
                it.init().accept(this);
                derive(it, it.init());
            }
            if (it.incr() != null) {
                it.incr().accept(this);
                derive(it, it.incr());
            }
            if (it.condition() != null) {
                it.condition().accept(this);
                derive(it, it.condition());
            }
            it.body().accept(this);
            derive(it, it.body());
        }
        else if (isCheck()) {
            if (it.init() != null) {
                heritage(it, it.init());
                it.init().accept(this);
            }
            if (it.incr() != null) {
                heritage(it, it.incr());
                it.incr().accept(this);
            }
            if (it.condition() != null) {
                heritage(it, it.condition());
                it.condition().accept(this);
            }
            heritage(it, it.body());
            it.body().accept(this);
        }
        else {
            it.body().accept(this);
        }
    }
    @Override
    public void visit(whileStmt it) {
        if (isInit()) {
            if (it.condition() != null) {
                it.condition().accept(this);
                derive(it, it.condition());
            }
            it.body().accept(this);
            derive(it, it.body());
        }
        else if (isCheck()) {
            if (it.condition() != null) {
                heritage(it, it.condition());
                it.condition().accept(this);
            }
            heritage(it, it.body());
            it.body().accept(this);
        }
        else {
            it.body().accept(this);
        }
    }
    @Override
    public void visit(returnStmt it) {
        if (isInit()) {
            if (it.retValue() != null) {
                it.retValue().accept(this);
                derive(it, it.retValue());
            }
            it.dirty().add(formalRelevantEntity);
        }
        else if (isCheck()) {
            if (it.retValue() != null) it.retValue().accept(this);
        }
    }
    @Override
    public void visit(breakStmt it) {
        if (isInit()) {
            it.dirty().add(formalRelevantEntity);
        }
    }
    @Override
    public void visit(continueStmt it) {
        if (isInit()) {
            it.dirty().add(formalRelevantEntity);
        }
    }
    @Override public void visit(emptyStmt it) {}
    @Override
    public void visit(exprList it) {
        if (isInit()) {
            it.params().forEach(expr -> {
                expr.accept(this);
                derive(it, expr);
            });
        }
        else if (isCheck()) {
            heritage(it, it.params());
            it.params().forEach(expr -> expr.accept(this));
        }
    }

    @Override public void visit(typeNode it) {}

    @Override
    public void visit(arrayExpr it) {
        if (isInit()) {
            it.base().accept(this);
            it.width().accept(this);
            derive(it, it.base());
            derive(it, it.width());
        }
    }
    @Override
    public void visit(binaryExpr it) {
        if (isInit()) {
            it.src1().accept(this);
            it.src2().accept(this);
            derive(it, it.src1());
            derive(it, it.src2());
        }
        else if (isCheck()) {
            heritage(it, it.src1());
            heritage(it, it.src2());
            it.src1().accept(this);
            it.src2().accept(this);
        }
    }
    @Override
    public void visit(assignExpr it) {
        if (isInit()) {
            it.src1().accept(this);
            it.src2().accept(this);
            derive(it, it.src1());
            derive(it, it.src2());
            it.src1().dirty().addAll(it.src1().use());
            it.dirty().add(it.src1().entity());
        }
        else if (isCheck()) {
            heritage(it, it.src1());
            heritage(it, it.src2());
            it.src1().accept(this);
            it.src2().accept(this);
        }
    }
    @Override
    public void visit(prefixExpr it) {
        if (isInit()) {
            it.src().accept(this);
            derive(it, it.src());
            if (it.opCode().ordinal() < 5 && it.opCode().ordinal() > 2)
                it.dirty().addAll(it.use());
        }
        else if (isCheck()) {
            heritage(it, it.src());
            it.src().accept(this);
        }
    }
    @Override
    public void visit(suffixExpr it) {
        if (isInit()) {
            it.src().accept(this);
            derive(it, it.src());
            it.dirty().addAll(it.use());
        }
        else if (isCheck()) {
            heritage(it, it.src());
            it.src().accept(this);
        }
    }
    @Override
    public void visit(thisExpr it) {}
    @Override
    public void visit(funCallExpr it) {
        if (isInit()) {
            it.callee().accept(this);
            it.params().forEach(param -> {
                param.accept(this);
                derive(it, param);
            });
            if (((funcDecl)it.callee().type()).hasSideEffect()) {
                relevantVars.addAll(it.use());
                it.dirty().add(formalRelevantEntity);
            }
        }
    }
    @Override
    public void visit(methodExpr it) {
        if (isInit()) {
            it.caller().accept(this);
            derive(it, it.caller());
        }
        else if (isCheck()) {
            it.caller().accept(this);
        }
    }
    @Override
    public void visit(memberExpr it) {
        if (isInit()) {
            it.caller().accept(this);
            derive(it, it.caller());
            it.use().add(it.entity());
        }
    }
    @Override
    public void visit(newExpr it) {
        if (isInit()) {
            it.exprs().forEach(expr -> {
                expr.accept(this);
                derive(it, expr);
            });
            if (it.type().isClass()) {
                relevantVars.addAll(it.use());
                it.dirty().add(formalRelevantEntity);
            }
        }
        else if (isCheck()) {
            heritage(it, it.exprs());
            it.exprs().forEach(expr -> expr.accept(this));
        }
    }
    @Override public void visit(funcNode it) {}
    @Override
    public void visit(varNode it) {
        if (isInit()) {
            it.use().add(it.entity());
        }
    }

    @Override public void visit(intLiteral it) {}
    @Override public void visit(boolLiteral it) {}
    @Override public void visit(nullLiteral it) {}
    @Override public void visit(stringLiteral it) {}
}
