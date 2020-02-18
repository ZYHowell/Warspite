package FrontEnd;

import AST.*;
import Util.error.*;
import Util.scope.*;
import Util.symbol.*;
import Util.symbol.funcDecl;

//to support forwarding reference
//mention that only function and class support forwarding reference
//class(types), class methods, functions. (no global variables)
public class SymbolCollector implements ASTVisitor {

    globalScope gScope;
    Scope currentScope;

    public SymbolCollector(globalScope gScope) {
        this.gScope = gScope;
    }

    @Override
    public void visit(rootNode it) {
        if (!it.allDef().isEmpty()) {
            it.allDef().forEach(node -> node.accept(this));
        }
    }


    @Override
    public void visit(classDef it) {
        if (!(currentScope instanceof globalScope))
            throw new internalError("class not defined in global scope", it.pos());
        classType defClass = new classType(it.Identifier(), it);
        Scope localScope = new classScope(currentScope);
        currentScope = localScope;
        it.members().forEach(member -> member.accept(this));
        it.methods().forEach(method -> method.accept(this));
        it.constructors().forEach(constructor->constructor.accept(this));
        currentScope = currentScope.parentScope();
        defClass.addScope(localScope);
        gScope.defineClass(it.Identifier(), defClass, it.pos());
    }

    @Override
    public void visit(funDef it) {
        funcDecl func = new funcDecl(it.Identifier(), it);
        it.setDecl(func);
        if (it.isConstructor())
            currentScope.defineConstructor(func, it.pos());
        else currentScope.defineMethod(it.Identifier(), func, it.pos());
    }

    @Override
    public void visit(varDef it) {}

    @Override public void visit(varDefList it){}
    @Override public void visit(blockNode it){}
    @Override public void visit(exprStmt it){}
    @Override public void visit(ifStmt it){}
    @Override public void visit(forStmt it){}
    @Override public void visit(whileStmt it){}
    @Override public void visit(returnStmt it){}
    @Override public void visit(breakStmt it){}
    @Override public void visit(continueStmt it){}
    @Override public void visit(emptyStmt it){}
    @Override public void visit(exprList it){}
    @Override public void visit(typeNode it){}
    @Override public void visit(arrayExpr it){}
    @Override public void visit(assignExpr it){}
    @Override public void visit(binaryExpr it){}
    @Override public void visit(prefixExpr it){}
    @Override public void visit(suffixExpr it){}
    @Override public void visit(thisExpr it){}
    @Override public void visit(funCallExpr it){}
    @Override public void visit(methodExpr it){}
    @Override public void visit(memberExpr it){}
    @Override public void visit(newExpr it){}
    @Override public void visit(funcNode it){}
    @Override public void visit(varNode it){}
    @Override public void visit(intLiteral it){}
    @Override public void visit(boolLiteral it){}
    @Override public void visit(nullLiteral it){}
    @Override public void visit(stringLiteral it){}
}
