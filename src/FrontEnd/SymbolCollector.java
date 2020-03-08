package FrontEnd;

import AST.*;
import MIR.IRtype.ClassType;
import MIR.Root;
import Util.error.*;
import Util.scope.*;
import Util.symbol.*;
import Util.symbol.funcDecl;

//to support forwarding reference
//mention that only function and class support forwarding reference
//class(types), class methods, functions. (no global variables)
public class SymbolCollector implements ASTVisitor {

    globalScope gScope;
    Scope currentScope = null;
    Root irRoot;

    public SymbolCollector(globalScope gScope, Root irRoot) {
        this.gScope = gScope;
        this.irRoot = irRoot;
    }

    @Override
    public void visit(rootNode it) {
        currentScope = gScope;
        it.allDef().forEach(node -> node.accept(this));
    }


    @Override
    public void visit(classDef it) {
        if (!(currentScope instanceof globalScope))
            throw new internalError("class not defined in global scope", it.pos());
        classType defClass = new classType(it.Identifier(), it);
        Scope localScope = new classScope(currentScope);
        currentScope = localScope;
        irRoot.addType(it.Identifier(), new ClassType(it.Identifier()));
        it.members().forEach(member -> member.accept(this));
        it.methods().forEach(method -> method.accept(this));
        it.constructors().forEach(constructor->constructor.accept(this));
        currentScope = currentScope.parentScope();
        defClass.addScope(localScope);
        gScope.defineClass(it.Identifier(), defClass, it.pos());
        if (gScope.containsMethod(it.Identifier(), false))
            throw new semanticError("same name with a function", it.pos());
    }

    @Override
    public void visit(funDef it) {
        funcDecl func = new funcDecl(it.Identifier(), it);
        if (currentScope != gScope) func.setIsMethod();
        else if (gScope.hasType(it.Identifier()))
            throw new semanticError("same name with a class", it.pos());
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
