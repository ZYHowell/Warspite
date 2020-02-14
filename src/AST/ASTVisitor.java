package AST;

/* anyone going to write a compiler like this is suggested to consider
   if rebuild an AST out ANTLR is essential. */
public interface ASTVisitor {
    void visit(rootNode it);

    void visit(classDef it);
    void visit(funDef it);
    void visit(varDef it);
    void visit(varDefList it);

    void visit(blockNode it);

    void visit(exprStmt it);
    void visit(ifStmt it);
    void visit(forStmt it);
    void visit(whileStmt it);
    void visit(returnStmt it);
    void visit(breakStmt it);
    void visit(continueStmt it);
    void visit(emptyStmt it);

    void visit(exprList it);

    void visit(typeNode it);

    void visit(arrayExpr it);
    void visit(binaryExpr it);
    void visit(prefixExpr it);
    void visit(suffixExpr it);

    void visit(thisExpr it);
    void visit(funCallExpr it);
    void visit(methodExpr it);
    void visit(memberExpr it);
    void visit(newExpr it);

    void visit(funcNode it);
    void visit(varNode it);
    void visit(intLiteral it);
    void visit(boolLiteral it);
    void visit(nullLiteral it);
    void visit(stringLiteral it);
}
