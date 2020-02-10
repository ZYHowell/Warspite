package FrontEnd;

import AST.*;
import Util.position;
import Util.error.*;

public class SemanticChecker implements ASTVisitor {

    public SemanticChecker() {}
    @Override
    public void visit(rootNode it) {
        if (!it.allDef().isEmpty()) {
            it.allDef().forEach(node -> node.accept(this));
        }
    }


    @Override
    public void visit(classDef it) {}

    @Override
    public void visit(funDef it){}

    @Override
    public void visit(varDef it){}

    @Override
    public void visit(varDefList it){}


    @Override
    public void visit(blockNode it){}


    @Override
    public void visit(assignStmt it){}

    @Override
    public void visit(exprStmt it){}

    @Override
    public void visit(ifStmt it){}

    @Override
    public void visit(forStmt it){}

    @Override
    public void visit(whileStmt it){}

    @Override
    public void visit(returnStmt it){}

    @Override
    public void visit(breakStmt it){}

    @Override
    public void visit(continueStmt it){}

    @Override
    public void visit(emptyStmt it){}


    @Override
    public void visit(exprList it){}


    @Override
    public void visit(typeNode it){}


    @Override
    public void visit(arrayExpr it){}

    @Override
    public void visit(binaryExpr it){}

    @Override
    public void visit(prefixExpr it){}

    @Override
    public void visit(subscriptExpr it){}

    @Override
    public void visit(suffixExpr it){}


    @Override
    public void visit(thisExpr it){}

    @Override
    public void visit(funCallExpr it){}

    @Override
    public void visit(memberExpr it){}

    @Override
    public void visit(newExpr it){}


    @Override
    public void visit(varNode it){}

    @Override
    public void visit(intLiteral it){}

    @Override
    public void visit(boolLiteral it){}

    @Override
    public void visit(nullLiteral it){}

    @Override
    public void visit(stringLiteral it){}
}
