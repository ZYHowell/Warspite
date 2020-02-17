package FrontEnd;

import java.util.ArrayList;
import AST.*;
import Util.error.semanticError;
import Util.scope.Scope;
import Util.scope.globalScope;
import Util.symbol.*;

import java.io.FileNotFoundException;
import java.io.PrintStream;

//I'm afraid that I might be wrong, so I make such ASTPrinter to keep track of my AST.
//is for DEBUG USE
public class ASTPrinter implements ASTVisitor{
    private globalScope gScope;
    private PrintStream output;
    private int indentNum;

    private void outputIndent() {
        output.print("   ");
    }
    private void outputLine(String msg) {
        for (int i = 0;i < indentNum;++i) outputIndent();
        output.println(msg);
    }

    public ASTPrinter(globalScope gScope, String outFile) throws FileNotFoundException {
        this.gScope = gScope;
        this.output = new PrintStream(outFile);
        this.indentNum = 0;
    }

    @Override
    public void visit(rootNode it) {
        if (!it.allDef().isEmpty()) {
            outputLine("rootNode");
            ++indentNum;
            it.allDef().forEach(node -> node.accept(this));
            --indentNum;
        }
    }

    @Override
    public void visit(classDef it) {
        outputLine("classDef, name is: " + it.Identifier());
        ++indentNum;
        it.members().forEach(member -> member.accept(this));
        it.methods().forEach(method -> method.accept(this));
        it.constructors().forEach(constructor -> constructor.accept(this));
        --indentNum;
    }

    @Override
    public void visit(funDef it) {
        outputLine("funDef, name is: " + it.Identifier());
        ++indentNum;
        it.body().accept(this);
        --indentNum;
    }

    @Override public void visit(varDef it) {
        outputLine("varDef, type is: " + it.type().toString() + ", name is: " + it.name());
    }

    @Override public void visit(varDefList it) {
        outputLine("unbelievable! there is a varDefList");
    }
    @Override
    public void visit(blockNode it) {
        outputLine("blockNode");
        it.getStmtList().forEach(stmt -> {
            if (stmt instanceof blockNode)
                ++indentNum;
            stmt.accept(this);
            if (stmt instanceof blockNode)
                --indentNum;
        }); //cannot be null
    }


    @Override
    public void visit(exprStmt it) {
        outputLine("exprStmt");
        ++indentNum;
        it.expr().accept(this);
        --indentNum;
    }

    @Override
    public void visit(ifStmt it) {
        outputLine("ifStmt");
        outputLine("  condition: ");
        ++indentNum;
        it.condition().accept(this);
        --indentNum;
        outputLine("  trueStmt: ");
        ++indentNum;
        it.trueStmt().accept(this);
        --indentNum;
        if (it.falseStmt() != null) {
            outputLine("  falseStmt: ");
            ++indentNum;
            it.falseStmt().accept(this);
            --indentNum;
        }
    }

    @Override
    public void visit(forStmt it) {
        outputLine("forStmt");
        if (it.init() != null) {
            outputLine("  init is:");
            ++indentNum;
            it.init().accept(this);
            --indentNum;
        }
        if (it.incr() != null) {
            outputLine("  incr is:");
            ++indentNum;
            it.incr().accept(this);
            --indentNum;
        }
        if (it.condition() != null) {
            outputLine("  condition is:");
            ++indentNum;
            it.condition().accept(this);
            --indentNum;
        }
        outputLine("  body is: ");
        ++indentNum;
        it.body().accept(this);
        --indentNum;
    }

    @Override
    public void visit(whileStmt it) {
        outputLine("whileStmt");
        if (it.condition() != null) {
            outputLine("  condition is:");
            ++indentNum;
            it.condition().accept(this);
            --indentNum;
        }
        outputLine(" body is: ");
        ++indentNum;
        it.body().accept(this);
        --indentNum;
    }

    @Override
    public void visit(returnStmt it) {
        String msg = "returnStmt";
        if (it.retValue() != null) {
            outputLine(msg);
            ++indentNum;
            it.retValue().accept(this);
            --indentNum;
            return;
        } else msg = msg + " no return value";
        outputLine(msg);
    }

    @Override
    public void visit(breakStmt it) {
        outputLine("breakStmt");
    }

    @Override
    public void visit(continueStmt it) {
        outputLine("continueStmt");
    }

    @Override
    public void visit(emptyStmt it) {}


    @Override   //more check in funcCall
    public void visit(exprList it) {
        outputLine("exprList(params)");
        ++indentNum;
        it.params().forEach(param -> param.accept(this));   //cannot be null
        --indentNum;
    }

    //no need to visit it, only use it to form type;
    @Override
    public void visit(typeNode it) {
        outputLine("unbelievable! reach a typeNode at " + it.pos().toString());
    }


    @Override
    public void visit(arrayExpr it) {
        outputLine("arrayExpr");
        outputLine("  base is: ");
        ++indentNum;
        it.base().accept(this);
        --indentNum;
        outputLine("width is: ");
        ++indentNum;
        it.width().accept(this);
        --indentNum;
    }

    @Override
    public void visit(binaryExpr it){
        outputLine("binaryExpr, opCode is: " + it.opCode().toString());
        outputLine("  src1 is: ");
        ++indentNum;
        it.src1().accept(this);
        --indentNum;
        outputLine("  src2 is: ");
        ++indentNum;
        it.src2().accept(this);
        --indentNum;
    }

    @Override
    public void visit(assignExpr it){
        outputLine("assignExpr");
        outputLine("  src1 is: ");
        ++indentNum;
        it.src1().accept(this);
        --indentNum;
        outputLine("  src2 is: ");
        ++indentNum;
        it.src2().accept(this);
        --indentNum;
    }

    @Override
    public void visit(prefixExpr it) {
        outputLine("prefixExpr, opCode is: " + it.opCode().toString());
        it.src().accept(this);
    }

    @Override
    public void visit(suffixExpr it) {
        outputLine("prefixExpr, opCode is: " + (it.opCode() == 0 ? "++" : "--"));
        ++indentNum;
        it.src().accept(this);
        --indentNum;
    }


    @Override
    public void visit(thisExpr it) {
        outputLine("thisExpr");
    }

    //the type of the constructor function funcCall should be the type of the class(help newExpr get type)
    @Override
    public void visit(funCallExpr it) {
        outputLine("funCallExpr, name is: " + ((funcDecl)it.callee().type()).name());
        outputLine("  callee is: ");
        ++indentNum;
        it.callee().accept(this);
        --indentNum;
        outputLine("params are: ");
        ArrayList<exprNode> params = it.params();
        params.forEach(param -> param.accept(this));   //cannot be null
        outputLine("params end");
    }

    @Override
    public void visit(methodExpr it) {
        String msg = "methodExpr";
        if (it.caller().type().isArray()) {
            msg = msg + "  is size method";
        } else {
            msg = msg + "  method name is: " + it.method();
        }
        outputLine(msg);
        ++indentNum;
        it.caller().accept(this);
        --indentNum;
    }
    @Override
    public void visit(memberExpr it) {
        String msg = "memberExpr";
        if (it.caller().type().isArray()) {
            msg = msg + " is size method";
        } else {
            msg = msg + " is member, name is: " + it.member();
        }
        outputLine(msg);
        ++indentNum;
        it.caller().accept(this);
        --indentNum;
    }

    @Override
    public void visit(newExpr it) {
        outputLine("newExpr, type is: " + it.typeN().typeName() + ", dim is: " + it.typeN().dim());
        outputLine("parameters: ");
        ++indentNum;
        it.exprs().forEach(expr -> expr.accept(this));
        --indentNum;
    }

    @Override
    public void visit(funcNode it){
        outputLine("funcNode" + it.name());
    }


    @Override
    public void visit(varNode it){
        outputLine("varNode" + it.name());
    }

    @Override
    public void visit(intLiteral it) {
        outputLine("intLiteral" + it.value());
    }

    @Override
    public void visit(boolLiteral it) {
        outputLine("boolLiteral" + it.value());
    }

    @Override
    public void visit(nullLiteral it){
        outputLine("null");
    }

    @Override
    public void visit(stringLiteral it){
        outputLine("stringLiteral, value is: " + it.value());
    }
}
