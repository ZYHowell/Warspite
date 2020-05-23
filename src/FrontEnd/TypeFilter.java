package FrontEnd;

import AST.*;
import Util.error.internalError;
import Util.error.semanticError;
import Util.scope.Scope;
import Util.scope.functionScope;
import Util.scope.globalScope;
import Util.symbol.classType;
import Util.symbol.constructorType;
import Util.symbol.funcDecl;
import Util.symbol.varEntity;

//mention that in this language, forwarding reference of class member is also not allowed!!!
public class TypeFilter implements ASTVisitor {

    globalScope gScope;
    Scope currentScope;


    public TypeFilter(globalScope gScope) {
        this.gScope = gScope;
    }

    @Override
    public void visit(rootNode it) {
        currentScope = gScope;
        if (!it.allDef().isEmpty()) {
            it.allDef().forEach(node -> {
                if (!(node instanceof varDef)) node.accept(this);
            });
        }
    }


    @Override
    public void visit(classDef it) {
        classType defClass = (classType)gScope.getType(it.Identifier(), it.pos());
        currentScope = defClass.scope();
        // it.members().forEach(member -> member.accept(this));                    //cannot be null
        it.methods().forEach(method -> method.accept(this));                //cannot be null
        it.constructors().forEach(constructor-> constructor.accept(this));  //cannot be null
        currentScope = currentScope.parentScope();
    }

    @Override
    public void visit(funDef it) {
        funcDecl func;
        if (it.isConstructor()) {
            func = it.decl();
            func.setRetType(new constructorType());
        } else {
            func = it.decl();
            func.setRetType(gScope.generateType(it.retValueType()));
        }
        currentScope = new functionScope(currentScope);
        it.parameters().forEach(param -> param.accept(this));   //cannot be null
        func.setScope((functionScope)currentScope);
        currentScope = currentScope.parentScope();
    }

    @Override public void visit(varDef it) {
        varEntity param = new varEntity(it.name(), gScope.generateType(it.type()), false);
        if (param.type().isVoid())
            throw new semanticError("type of a parameter is void", it.pos());
        if (currentScope instanceof functionScope){
            it.setEntity(param);
            ((functionScope)currentScope).addParam(param, it.pos());
        }
        else throw new internalError("type filter visit vardef not a param", it.pos());
    }

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
