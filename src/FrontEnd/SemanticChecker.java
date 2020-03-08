package FrontEnd;

import java.util.ArrayList;
import java.util.Stack;
import AST.*;
import MIR.IRtype.ClassType;
import MIR.IRtype.IRBaseType;
import MIR.IRtype.Pointer;
import MIR.Root;
import Util.scope.*;
import Util.symbol.*;
import Util.error.*;
import Util.position;

//principle: set new scope before visit its body;
//(so check condition: {...;{};...})
//principle: always visit a node before getting its type;
//check type and variable principle
public class SemanticChecker implements ASTVisitor {

    private semanticError binaryCalError(Type typeO, Type typeT,
                                         AST.binaryExpr.opCategory opCode, position pos) {
        return new semanticError("Incorrect calculation, type Error, type is: '" +
                                 typeO.toString() + "' and '" + typeT.toString() +
                                 "', opCode is " + opCode.toString(), pos);
    }
    private boolean isOuter() {
        return (currentScope == gScope) || (currentScope instanceof classScope);
    }

    private globalScope gScope;
    private Scope currentScope;
    private classType currentClass = null;
    private Type currentRetType = null;
    private funDef currentFunction;
    private Stack<ASTNode> loopStack = new Stack<>();
    private boolean haveReturn = false;
    private boolean classMemberCollect = false;
    private Root irRoot;
    private ClassType currentIRClass = null;

    public SemanticChecker(globalScope gScope, Root irRoot) {
        this.gScope = gScope;
        this.irRoot = irRoot;
    }

    @Override
    public void visit(rootNode it) {
        currentScope = gScope;
        if (!it.allDef().isEmpty()) {
            classMemberCollect = true;
            it.allDef().forEach(node -> {
                if (node instanceof classDef) node.accept(this);
            });
            classMemberCollect = false;
            it.allDef().forEach(node -> node.accept(this));
        }
        if (!gScope.containsMethod("main", true))
            throw new semanticError("no main", it.pos());
    }


    @Override
    public void visit(classDef it) {
        classType defClass = (classType)gScope.getType(it.Identifier(), it.pos());
        currentScope = defClass.scope();
        currentClass = defClass;
        currentIRClass = irRoot.getType(it.Identifier());
        if (classMemberCollect) it.members().forEach(member -> member.accept(this));
        currentIRClass = null;
        if (!classMemberCollect) {
            it.methods().forEach(method -> method.accept(this));
            it.constructors().forEach(constructor->constructor.accept(this));
        }
        currentClass = null;
        currentScope = currentScope.parentScope();
    }

    @Override
    public void visit(funDef it) {
        if(it.isConstructor()) {
            currentRetType = gScope.getVoidType();
            if (!it.Identifier().equals(currentClass.name()))
                throw new semanticError("mismatch constructor", it.pos());
        } else currentRetType = it.decl().returnType();
        currentFunction = it;
        haveReturn = false;
        //parameters are already in the scope(in TypeFilter)
        currentScope = it.decl().scope();
        it.body().accept(this);
        currentScope = currentScope.parentScope();
        if (it.Identifier().equals("main")) {
            haveReturn = true;
            if (!currentRetType.isInt())
                throw new semanticError("return of main is not int", it.pos());
            if (it.parameters().size() > 0)
                throw new semanticError("main has args", it.pos());
        }
        if (!haveReturn && !currentRetType.isVoid()) throw new semanticError("no return", it.pos());
        currentFunction = null;
        currentRetType = null;
    }

    @Override
    public void visit(varDef it) {
        varEntity theVar = new varEntity(it.name(), gScope.generateType(it.type()), isOuter(),
                currentScope == gScope);
        it.setEntity(theVar);
        if (theVar.type().isVoid()) throw new semanticError("type of the variable is void", it.pos());

        if (currentScope instanceof classScope) {
            theVar.setIsMember();
            theVar.setElementIndex(currentClass.setElement(theVar.type()));
        }   //for IR use
        if (currentIRClass != null) {
            IRBaseType type = irRoot.getIRType(it.entity().type(), true);
            if (type instanceof ClassType) type = new Pointer(type, false);
            currentIRClass.addMember(type);
        }

        if (it.init() != null) {
            it.init().accept(this);
            if (!it.init().type().sameType(theVar.type()))
                throw new semanticError("not same type. should be: " +
                                        theVar.type().toString() + ", actually is: " +
                                        it.init().type().toString(), it.pos());
        }
        currentScope.defineMember(it.name(), theVar, it.pos());
    }

    @Override
    public void visit(varDefList it) {
        it.getList().forEach(varDef -> varDef.accept(this));
    }


    @Override
    public void visit(blockNode it) {
        it.getStmtList().forEach(stmt -> {
            if (stmt instanceof blockNode) {
                currentScope = new Scope(currentScope);
                stmt.setScope(currentScope);
                stmt.accept(this);
                currentScope = currentScope.parentScope();
            } else stmt.accept(this);
        }); //cannot be null
    }


    @Override
    public void visit(exprStmt it) {
        it.expr().accept(this);
    }

    @Override
    public void visit(ifStmt it) {
        it.condition().accept(this);
        if (!it.condition().type().isBool())
            throw new semanticError("not a bool", it.condition().pos());

        currentScope = new Scope(currentScope);
        it.trueStmt().setScope(currentScope);
        it.trueStmt().accept(this);
        currentScope = currentScope.parentScope();

        if (it.falseStmt() != null) {
            currentScope = new Scope(currentScope);
            it.falseStmt().setScope(currentScope);
            it.falseStmt().accept(this);
            currentScope = currentScope.parentScope();
        }
    }

    @Override
    public void visit(forStmt it) {
        if (it.init() != null)
            it.init().accept(this);

        if (it.incr() != null)
            it.incr().accept(this);

        if (it.condition() != null) {
            it.condition().accept(this);
            if (!it.condition().type().isBool())
                throw new semanticError("not a bool", it.condition().pos());
        }

        currentScope = new Scope(currentScope);
        loopStack.push(it);
        it.body().setScope(currentScope);
        it.body().accept(this);
        loopStack.pop();
        currentScope = currentScope.parentScope();
    }

    @Override
    public void visit(whileStmt it) {
        if (it.condition() != null)
            it.condition().accept(this);

        if (!it.condition().type().isBool())
            throw new semanticError("not a bool", it.condition().pos());

        currentScope = new Scope(currentScope);
        loopStack.push(it);
        it.body().setScope(currentScope);
        it.body().accept(this);
        loopStack.pop();
        currentScope = currentScope.parentScope();
    }

    @Override
    public void visit(returnStmt it) {
        haveReturn = true;
        if (it.retValue() != null) {
            it.retValue().accept(this);
            if (!currentRetType.sameType(it.retValue().type()))
                throw new semanticError("not the correct return type: is " +
                                        it.retValue().type().toString() + ", should be: " +
                                        currentRetType.toString(), it.pos());
        } else if (!currentRetType.isVoid()) {
            throw new semanticError("not the correct return type: is Void, should be: " +
                                    currentRetType.toString(), it.pos());
        }
        it.setDest(currentFunction);
    }

    @Override
    public void visit(breakStmt it) {
        if (loopStack.isEmpty())
            throw new semanticError("break not in loop", it.pos());
        it.setDest(loopStack.peek());
    }

    @Override
    public void visit(continueStmt it) {
        if (loopStack.isEmpty())
            throw new semanticError("continue not in loop", it.pos());
        it.setDest(loopStack.peek());
    }

    @Override public void visit(emptyStmt it) {}

    //never visited
    @Override public void visit(exprList it) {}
    @Override public void visit(typeNode it) {}


    @Override
    public void visit(arrayExpr it) {
        it.base().accept(this);
        it.width().accept(this);
        if (!it.width().type().isInt())
            throw new semanticError("array index not int", it.width().pos());
        if (it.base().type().dim() > 1)
            it.setType(new arrayType(it.base().type()));
        else if (it.base().type().dim() < 1)
            throw new semanticError("not actually an array", it.base().pos());
        else it.setType(it.base().type().baseType());
    }

    @Override
    public void visit(binaryExpr it){
        it.src1().accept(this);
        it.src2().accept(this);
        Type typeO, typeT;
        typeO = it.src1().type();
        typeT = it.src2().type();
        AST.binaryExpr.opCategory opCode = it.opCode();
        if (opCode.ordinal() < 9) {
            if (!(typeO.isInt() && typeT.isInt()))
                throw binaryCalError(typeO, typeT, opCode, it.pos());
            it.setType(gScope.getIntType());
        } else if (opCode.ordinal() < 14) {
            if (!( (typeO.isInt() || typeO.sameType(gScope.getStringType()))
                    && (typeO.sameType(typeT)) ))
                throw binaryCalError(typeO, typeT, opCode, it.pos());
            if (opCode.ordinal() == 9) it.setType(typeO);
            else it.setType(gScope.getBoolType());
        } else if (opCode.ordinal() < 16) {
            if (!(typeO.isBool() && typeT.isBool()))
                throw binaryCalError(typeO, typeT, opCode, it.pos());
            it.setType(gScope.getBoolType());
        } else if (opCode.ordinal() < 18) {
            if (!typeO.sameType(typeT))
                throw binaryCalError(typeO, typeT, opCode, it.pos());
            it.setType(gScope.getBoolType());
        }
    }

    @Override
    public void visit(assignExpr it) {
        it.src1().accept(this);
        it.src2().accept(this);
        Type typeO, typeT;
        typeO = it.src1().type();
        typeT = it.src2().type();
        if (!typeO.sameType(typeT))
            throw new semanticError("cannot assign different type: '" + typeO.toString() +
                                    "' with '" + typeT.toString() + "'", it.pos());
        it.setType(typeO);
        if (!it.src1().isAssignable())
            throw new semanticError("not a left value", it.src1().pos());
    }

    @Override
    public void visit(prefixExpr it) {
        it.src().accept(this);
        prefixExpr.prefixCode opCode = it.opCode();
        if (opCode.ordinal() < 5){
            if (!it.src().type().isInt())
                throw new semanticError("operator not match. Type: " +
                                        it.src().type().toString(), it.pos());
            if (opCode.ordinal() > 2)   //++ or --
                if (!it.src().isAssignable())
                    throw new semanticError("not a left value. ", it.src().pos());
            it.setType(gScope.getIntType());
        } else {
            it.setType(gScope.getBoolType());
            if (!it.src().type().isBool())
                throw new semanticError("operator not match. Type: " +
                                    it.src().type().toString(), it.pos());
        }
    }

    @Override
    public void visit(suffixExpr it) {
        it.src().accept(this);
        if (!it.src().type().isInt())
            throw new semanticError("operator not match. Type: " +
                    it.src().type().toString(), it.pos());
        if (!it.src().isAssignable())
            throw new semanticError("not a left value. ", it.pos());
        it.setType(gScope.getIntType());
    }


    @Override
    public void visit(thisExpr it) {
        if (currentClass == null)
            throw new semanticError("this expr not in a class", it.pos());
        it.setPointClass(currentClass);
        it.setType(currentClass);
    }

    @Override
    public void visit(funCallExpr it) {
        it.callee().accept(this);
        if (it.callee().type() instanceof funcDecl) {
            funcDecl func = (funcDecl)it.callee().type();
            ArrayList<varEntity> args = func.scope().params();
            ArrayList<exprNode> params = it.params();
            params.forEach(param -> param.accept(this));   //cannot be null
            if (params.size() != args.size())
                throw new semanticError("", it.pos());
            for (int i = 0; i < args.size(); i++) {
                if (!(args.get(i).type().sameType(params.get(i).type())) )
                    throw new semanticError("parameter type not match. is: '" +
                                            params.get(i).type().toString() + "', should be :'" +
                                            args.get(i).type().toString() + "'", params.get(i).pos());
            }
            it.setType(func.returnType());
        } else throw new semanticError("function not defined(as a function)", it.callee().pos());
    }

    @Override
    public void visit(methodExpr it) {
        it.caller().accept(this);
        if (it.caller().type().isArray()) {
            if (it.method().equals("size")) {
                it.setType(gScope.getMethod("size", it.pos(), false));
                return;
            } else throw new semanticError("array with a method not size, instead it is: " +
                    it.method(), it.pos());
        }
        if (!it.caller().type().isClass())
            throw new semanticError("not a class, instead it is: " +
                    it.caller().type().toString(), it.caller().pos());
        classType callerClass = (classType)it.caller().type();
        if (callerClass.scope().containsMethod(it.method(), false)){
            it.setType(callerClass.scope().getMethod(it.method(), it.pos(), false));
        } else throw new semanticError("no such symbol in class'"
                + callerClass.name() + "'", it.pos());
    }
    @Override
    public void visit(memberExpr it) {
        it.caller().accept(this);
        if (!it.caller().type().isClass())
            throw new semanticError("not a class, instead it is: " +
                                    it.caller().type().toString(), it.caller().pos());
        classType callerClass = (classType)it.caller().type();
        if (callerClass.scope().containsMember(it.member(), false)) {
            it.setType(callerClass.scope().getMemberType(it.member(), it.pos(), false));
            it.setVarEntity(callerClass.scope().getMember(it.member(), it.pos(), false));
        } else throw new semanticError("no such symbol in class'"
                                        + callerClass.name() + "'", it.pos());
    }

    @Override
    public void visit(newExpr it) {
        it.exprs().forEach(expr -> {
            expr.accept(this);
            if (!expr.type().isInt())
                throw new semanticError("not a int", expr.pos());
        }); //cannot be null
        it.setType(gScope.generateType(it.typeN()));
    }

    @Override
    public void visit(funcNode it){
        it.setType(currentScope.getMethod(it.name(), it.pos(), true));
    }
    @Override
    public void visit(varNode it){
        it.setType(currentScope.getMemberType(it.name(), it.pos(), true));
        it.setVarEntity(currentScope.getMember(it.name(), it.pos(), true));
    }

    @Override
    public void visit(intLiteral it) {
        it.setType(gScope.getIntType());
    }

    @Override
    public void visit(boolLiteral it) {
        it.setType(gScope.getBoolType());
    }

    @Override
    public void visit(nullLiteral it){
        it.setType(gScope.getNullType());
    }

    @Override
    public void visit(stringLiteral it){
        it.setType(gScope.getType("string", it.pos()));
    }
}
