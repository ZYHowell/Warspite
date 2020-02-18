package BackEnd;

import AST.*;
import MIR.*;
import MIR.IRoperand.*;
import MIR.IRinst.*;
import Util.position;
import Util.scope.globalScope;
import Util.symbol.classType;
import Util.symbol.funcDecl;
import Util.symbol.varEntity;

import static MIR.IRinst.Binary.BinaryOpCategory.*;
import static MIR.IRinst.Cmp.CmpOpCategory.*;

public class IRBuilder implements ASTVisitor {

    private globalScope gScope;
    private Root irRoot = new Root();
    private position beginning = new position(0, 0);
    private classType currentClass;
    private Function currentFunction;

    private void setBuiltinMethod(String name) {
        gScope.getMethod(name, beginning, false)
                .setFunction(irRoot.getFunction("global_" + name));
    }
    public IRBuilder(globalScope gScope) {
        this.gScope = gScope;
        setBuiltinMethod("print");
        setBuiltinMethod("println");
        setBuiltinMethod("printInt");
        setBuiltinMethod("printlnInt");
        setBuiltinMethod("getString");
        setBuiltinMethod("getInt");
        setBuiltinMethod("toString");
        setBuiltinMethod("size");
        //todo: set builtin of string
    }

    @Override
    public void visit(rootNode it) {
        it.allDef().forEach(node -> {
            if (node instanceof funDef) {
                Function fun = new Function("global_" + ((funDef)node).Identifier());
                ((funDef)node).decl().setFunction(fun);
                irRoot.addFunction(fun.name(), fun);
            } else if (node instanceof classDef) {
                ((classDef)node).methods().forEach(method -> {
                    Function fun = new Function("local_" + ((classDef) node).Identifier() +
                                                "_" + method.Identifier());
                    method.decl().setFunction(fun);
                    irRoot.addFunction(fun.name(), fun);
                });
            }
        });
        it.allDef().forEach(node -> node.accept(this));
        //todo: move the info from sideEffectBuilder about recursive function(todo) to IR
    }

    @Override
    public void visit(classDef it) {
        it.members().forEach(member -> member.accept(this));
        it.methods().forEach(method -> method.accept(this));
    }

    @Override
    public void visit(funDef it) {
        funcDecl func = it.decl();
        currentFunction = func.function();
        it.body().accept(this);
    }

    @Override
    public void visit(varDef it) {
        if (it.init() != null) {
            it.init().accept(this);
        }
    }

    @Override public void visit(varDefList it) {}

    @Override
    public void visit(blockNode it) {
        it.getStmtList().forEach(stmt -> stmt.accept(this));
    }

    @Override
    public void visit(exprStmt it) {

    }

    @Override
    public void visit(ifStmt it) {

    }

    @Override
    public void visit(forStmt it) {

    }

    @Override
    public void visit(whileStmt it) {

    }

    @Override
    public void visit(returnStmt it) {

    }

    @Override
    public void visit(breakStmt it) {

    }

    @Override
    public void visit(continueStmt it) {

    }

    @Override
    public void visit(emptyStmt it) {

    }

    @Override
    public void visit(exprList it) {

    }

    @Override
    public void visit(typeNode it) {

    }

    @Override
    public void visit(arrayExpr it) {

    }

    @Override
    public void visit(binaryExpr it) {
        it.src1().accept(this);
        it.src2().accept(this);
        Binary.BinaryOpCategory biOp = null;
        Cmp.CmpOpCategory cmpOp = null;
        switch (it.opCode()) {
            case Star: biOp = Star;break;
            case Div : biOp = Div;break;
            case Mod : biOp = Mod;break;
            case LeftShift : biOp = LeftShift;break;
            case RightShift: biOp = RightShift;break;
            case And : biOp = And;break;
            case Or : biOp = Or;break;
            case Caret : biOp = Caret;break;
            case Minus : biOp = Minus;break;
            case Plus : biOp = Plus;break;
            case Less : cmpOp = Less;break;
            case Greater: cmpOp = Greater;break;
            case LessEqual: cmpOp = LessEqual;break;
            case GreaterEqual: cmpOp = GreaterEqual;break;
            case AndAnd : cmpOp = AndAnd;break;
            case OrOr: cmpOp = OrOr;break;
            case Equal: cmpOp = Equal;break;
            case NotEqual: cmpOp = NotEqual;break;
        }
    }

    @Override
    public void visit(assignExpr it) {

    }

    @Override
    public void visit(prefixExpr it) {

    }

    @Override
    public void visit(suffixExpr it) {

    }

    @Override
    public void visit(thisExpr it) {

    }

    @Override
    public void visit(funCallExpr it) {

    }

    @Override
    public void visit(methodExpr it) {

    }

    @Override
    public void visit(memberExpr it) {

    }

    @Override
    public void visit(newExpr it) {

    }

    @Override
    public void visit(funcNode it) {

    }

    @Override
    public void visit(varNode it) {
        varEntity entity = it.entity();
        it.setOperand(entity.asOperand());
    }

    @Override
    public void visit(intLiteral it) {
        it.setOperand(new ConstInt(it.value()));
    }

    @Override
    public void visit(boolLiteral it) {
        it.setOperand(new ConstBool(it.value()));
    }

    @Override
    public void visit(nullLiteral it) {
        it.setOperand(new Null());
    }

    @Override
    public void visit(stringLiteral it) {

    }
}
