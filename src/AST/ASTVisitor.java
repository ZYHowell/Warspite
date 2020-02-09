package AST;

/* anyone going to write a compiler like this is suggested to consider
   if rebuild an AST out ANTLR is essential. */
public interface ASTVisitor {
    void visit(assignStmt it);
    void visit(blockNode it);
    void visit(breakStmt it);
    void visit(classDef it);
    void visit(continueStmt it);
    void visit(forStmt it);
    void visit(funDef it);
    void visit(ifStmt it);
    void visit(returnStmt it);
    void visit(typeNode it);
    void visit(varDef it);
    void visit(whileStmt it);
}
