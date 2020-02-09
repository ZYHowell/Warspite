package FrontEnd;

import java.util.ArrayList;

import Parser.MxBaseVisitor;
import Parser.MxParser;

import org.antlr.v4.runtime.ParserRuleContext;

import AST.*;
import Util.position;
import Util.error.*;
import org.antlr.v4.runtime.tree.TerminalNode;

public class ASTBuilder extends MxBaseVisitor<ASTNode> {

    @Override
    //should return classDef(class name is main)
    public ASTNode visitProgram(MxParser.ProgramContext ctx) {
        ArrayList<varDef> variables = new ArrayList<>();
        ArrayList<funDef> functions = new ArrayList<>();
        ArrayList<funDef> constructors = new ArrayList<>();
        if (!ctx.varDef().isEmpty()) {
            for (ParserRuleContext varDef : ctx.varDef()) {
                ASTNode tmp = visit(varDef);
                variables.addAll(((varDefList)tmp).getList());
            }
        }
        if (!ctx.funcDef().isEmpty()) {
            for (ParserRuleContext funcDef : ctx.funcDef()) {
                ASTNode tmp = visit(funcDef);
                functions.add(((funDef)tmp));
            }
        }
        return new classDef("main", new position(ctx),
                            variables, functions, constructors, false);
    }

    @Override
    //should return classDef
    public ASTNode visitClassDef(MxParser.ClassDefContext ctx) {
        ArrayList<varDef> variables = new ArrayList<>();
        ArrayList<funDef> functions = new ArrayList<>();
        ArrayList<funDef> constructors = new ArrayList<>();
        boolean hasConstructor = false;
        if (!ctx.varDef().isEmpty()) {
            for (ParserRuleContext varDef : ctx.varDef()) {
                ASTNode tmp = visit(varDef);
                variables.addAll(((varDefList)tmp).getList());
            }
        }
        if (!ctx.funcDef().isEmpty()) {
            for (ParserRuleContext funcDef : ctx.funcDef()) {
                ASTNode tmp = visit(funcDef);
                if (((funDef)tmp).isConstructor()) {
                    hasConstructor = true;
                    constructors.add(((funDef)tmp));
                }
                else functions.add(((funDef)tmp));
            }
        }
        return new classDef(ctx.Identifier().toString(), new position(ctx),
                            variables, functions, constructors, hasConstructor);
    }

    @Override
    //should return funDef
    public ASTNode visitFuncDef(MxParser.FuncDefContext ctx) {
        boolean isConstructor;
        typeNode type;
        blockNode body = (blockNode)visit(ctx.suite());
        ArrayList<varDef> parameters;
        if (ctx.type() != null) {
            isConstructor = true;
            type = (typeNode)visit(ctx.type());
        } else {
            isConstructor = false;
            type = null;
        }
        if (ctx.paramList() == null) parameters = null;
        else {
            parameters = ((varDefList)visit(ctx.paramList())).getList();
        }
        return new funDef(ctx.Identifier().toString(), new position(ctx), isConstructor,
                          type, body, parameters);
    }

    @Override
    //should return varDefList
    public ASTNode visitVarDef(MxParser.VarDefContext ctx) {    //should return varDefList
        varDefList defs = new varDefList(new position(ctx));
        typeNode type = (typeNode)visit(ctx.type());
        if (!ctx.singleVarDef().isEmpty()) {
            for (ParserRuleContext singleVarDef : ctx.singleVarDef()) {
                varDef tmp = (varDef)visit(singleVarDef);
                tmp.setType(type);
                defs.addVarDef(tmp);
            }
        }
        return defs;
    }

    @Override
    //should return varDef
    public ASTNode visitSingleVarDef(MxParser.SingleVarDefContext ctx) {    //should return varDef
        exprNode expr = ctx.expression() == null ? null : (exprNode)visit(ctx.expression());
        return new varDef(ctx.Identifier().toString(), expr, new position(ctx));
    }

    @Override
    //should return varDefList
    public ASTNode visitParamList(MxParser.ParamListContext ctx) {
        varDefList parameters = new varDefList(new position(ctx));
        for (ParserRuleContext parameter : ctx.param()) {
            varDef param = (varDef)visit(parameter);
            parameters.addVarDef(param);
        }
        return parameters;
    }

    @Override
    //should return varDef
    public ASTNode visitParam(MxParser.ParamContext ctx) {
        typeNode type = (typeNode)visit(ctx.type());
        varDef param = new varDef(ctx.Identifier().toString(), null, new position(ctx));
        param.setType(type);
        return param;
    }

    //not called
    @Override
    public ASTNode visitBasicType(MxParser.BasicTypeContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    //should return typeNode
    public ASTNode visitType(MxParser.TypeContext ctx) {
        if (ctx.Void() != null) return new typeNode("void", 0, new position(ctx));
        int dim;
        String typeName;
        if (ctx.LeftBracket() != null) dim = ctx.LeftBracket().size();
        else dim = 0;
        if (ctx.Identifier() != null) typeName = ctx.Identifier().toString();
        else typeName = ctx.basicType().toString();
        return new typeNode(typeName, dim, new position(ctx));
    }

    @Override
    //should return blockNode
    public ASTNode visitSuite(MxParser.SuiteContext ctx) {
        blockNode block = new blockNode(new position(ctx));
        if (!ctx.statement().isEmpty()) {
            for (ParserRuleContext statement : ctx.statement()) {
                stmtNode tmp = (stmtNode)visit(statement);
                block.addStmt(tmp);
            }
        }
        return block;
    }

    @Override
    //should return a stmtNode(blockNode)
    public ASTNode visitBlock(MxParser.BlockContext ctx) {
        return visit(ctx.suite());
    }

    @Override
    //should return a stmtNode(varDef)
    public ASTNode visitVardefStmt(MxParser.VardefStmtContext ctx) {
        return visit(ctx.varDef());
    }

    @Override
    //should return a stmtNode(ifStmt)
    public ASTNode visitIfStmt(MxParser.IfStmtContext ctx) {
        exprNode condition = (exprNode)visit(ctx.expression());
        stmtNode trueStmt  = (stmtNode)visit(ctx.trueStmt);
        stmtNode falseStmt = ctx.falseStmt == null ? null : (stmtNode)visit(ctx.falseStmt);
        return new ifStmt(condition, trueStmt, falseStmt, new position(ctx));
    }

    @Override
    //should return a stmtNode(forStmt)
    public ASTNode visitForStmt(MxParser.ForStmtContext ctx) {
        exprNode init = ctx.init == null ? null : (exprNode)visit(ctx.init);
        exprNode incr = ctx.incr == null ? null : (exprNode)visit(ctx.incr);
        exprNode cond = ctx.cond == null ? null : (exprNode)visit(ctx.cond);
        stmtNode body = (stmtNode)visit(ctx.statement());
        return new forStmt(init, incr, cond, body, new position(ctx));
    }

    @Override
    //should return a stmtNode(whileStmt)
    public ASTNode visitWhileStmt(MxParser.WhileStmtContext ctx) {
        exprNode expr = (exprNode)visit(ctx.expression());
        stmtNode block = (stmtNode)visit(ctx.statement());
        return new whileStmt(expr, block, new position(ctx));
    }

    @Override
    //should return a stmtNode(returnStmt)
    public ASTNode visitReturnStmt(MxParser.ReturnStmtContext ctx) {
        exprNode returnExpr = ctx.expression() == null ? null : (exprNode)visit(ctx.expression());
        return new returnStmt(returnExpr, new position(ctx));
    }

    @Override
    //should return a stmtNode(breakStmt)
    public ASTNode visitBreakStmt(MxParser.BreakStmtContext ctx) {
        return new breakStmt(new position(ctx));
    }

    @Override
    //should return a stmtNode(continueStmt)
    public ASTNode visitContinueStmt(MxParser.ContinueStmtContext ctx) {
        return new continueStmt(new position(ctx));
    }

    @Override
    //should return a stmtNode(exprStmt)
    public ASTNode visitPureExprStmt(MxParser.PureExprStmtContext ctx) {
        exprNode expr = (exprNode)visit(ctx.expression());
        return new exprStmt(expr, new position(ctx));
    }

    @Override
    //should return a stmtNode(emptyStmt)
    public ASTNode visitEmptyStmt(MxParser.EmptyStmtContext ctx) {
        return new emptyStmt(new position(ctx));
    }

    @Override
    //should return an exprList
    public ASTNode visitExpressionList(MxParser.ExpressionListContext ctx) {
        exprList exprLst = new exprList(new position(ctx));
        if (!ctx.expression().isEmpty()) {
            for (ParserRuleContext expression : ctx.expression()) {
                exprNode expr = (exprNode)visit(expression);
                exprLst.addParam(expr);
            }
        }
        return exprLst;
    }

    @Override
    //should return an exprNode(newExpr)
    public ASTNode visitNewExpr(MxParser.NewExprContext ctx) {
        return visit(ctx.creator());
    }

    @Override
    //should return an exprNode(prefixExpr)
    public ASTNode visitPrefixExpr(MxParser.PrefixExprContext ctx) {
        int opCode = -1;
        if (ctx.Plus() != null) opCode = 0;
        else if (ctx.Minus() != null) opCode = 1;
        else if (ctx.PlusPlus() != null) opCode = 2;
        else if (ctx.MinusMinus() != null) opCode = 3;
        else if (ctx.Tilde() != null) opCode = 4;
        else if (ctx.Not() != null) opCode = 5;
        return new prefixExpr((exprNode)visit(ctx.expression()), opCode, new position(ctx));
    }

    @Override
    //should return an exprNode(subscriptExpr)
    public ASTNode visitSubscript(MxParser.SubscriptContext ctx) {
        return new arrayExpr((exprNode)visit(ctx.expression(0)),
                             (exprNode)visit(ctx.expression(1)),
                             new position(ctx));
    }

    @Override
    //should return an exprNode
    public ASTNode visitMemberExpr(MxParser.MemberExprContext ctx) {
        return new memberExpr((exprNode)visit(ctx.expression()),
                              ctx.Identifier().toString(), new position(ctx));
    }

    @Override
    //should return an exprNode(funCallExpr)
    public ASTNode visitFuncCall(MxParser.FuncCallContext ctx) {
        return new funCallExpr((exprNode)visit(ctx.expression()),
                               ctx.expressionList() == null ? null : (exprList)visit(ctx.expressionList()),
                               new position(ctx));
    }

    @Override
    //should return an exprNode(suffixExpr)
    public ASTNode visitSuffixExpr(MxParser.SuffixExprContext ctx) {
        int opCode = -1;
        if (ctx.PlusPlus() != null) opCode = 0;
        else if (ctx.MinusMinus() != null) opCode = 1;
        return new suffixExpr((exprNode)visit(ctx.expression()), opCode, new position(ctx));
    }

    @Override
    //should return an exprNode
    public ASTNode visitAtomExpr(MxParser.AtomExprContext ctx) {
        return visit(ctx.primary());
    }

    @Override
    //should return an exprNode(binaryExpr)
    public ASTNode visitBinaryExpr(MxParser.BinaryExprContext ctx) {
        int opCode = -1;
        if      (ctx.Star() != null)         opCode = 0;
        else if (ctx.Div() != null)          opCode = 1;
        else if (ctx.Mod() != null)          opCode = 2;
        else if (ctx.LeftShift() != null)    opCode = 3;
        else if (ctx.RightShift() != null)   opCode = 4;
        else if (ctx.And() != null)          opCode = 5;
        else if (ctx.Or() != null)           opCode = 6;
        else if (ctx.Caret() != null)        opCode = 7;
        else if (ctx.Minus() != null)        opCode = 8;
        else if (ctx.Plus() != null)         opCode = 9;
        else if (ctx.Less() != null)         opCode = 10;
        else if (ctx.Greater() != null)      opCode = 11;
        else if (ctx.LessEqual() != null)    opCode = 12;
        else if (ctx.GreaterEqual() != null) opCode = 13;
        else if (ctx.AndAnd() != null)       opCode = 14;
        else if (ctx.OrOr() != null)         opCode = 15;
        else if (ctx.Equal() != null)        opCode = 16;
        else if (ctx.NotEqual() != null)     opCode = 17;
        else if (ctx.Assign() != null)       opCode = 18;
        return new binaryExpr((exprNode)visit(ctx.expression(0)),
                              (exprNode)visit(ctx.expression(1)),
                              opCode, new position(ctx));
    }

    @Override
    //should return an exprNode
    public ASTNode visitPrimary(MxParser.PrimaryContext ctx) {
        if (ctx.expression() != null) return visit(ctx.expression());
        else if (ctx.This() != null) return new thisExpr(new position(ctx));
        else if (ctx.Identifier() != null) return new varNode(ctx.Identifier().toString(), new position(ctx));
        else if (ctx.literal() != null) return visit(ctx.literal());
        else throw new syntaxError("not a real primary", new position(ctx));
    }

    @Override
    //should return an exprNode
    public ASTNode visitLiteral(MxParser.LiteralContext ctx) {
        if (ctx.DecimalInteger() != null)
            return new intLiteral(Integer.parseInt(ctx.DecimalInteger().toString()), new position(ctx));
        else if (ctx.StringLiteral() != null)
            return new stringLiteral(ctx.StringLiteral().toString(), new position(ctx));
        else if (ctx.True() != null)
            return new boolLiteral(true, new position(ctx));
        else if (ctx.False() != null)
            return new boolLiteral(false, new position(ctx));
        else if (ctx.Null() != null)
            return new nullLiteral(new position(ctx));
        else throw new syntaxError("illegally parsed", new position(ctx));
    }

    @Override
    //should return a newExpr
    public ASTNode visitCreator(MxParser.CreatorContext ctx) {
        String baseTypeName;
        ArrayList<exprNode> exprs = new ArrayList<>();
        position typePos;
        int dim = -1;
        if (ctx.basicType() != null) {
            typePos = new position(ctx.basicType());
            baseTypeName = ctx.basicType().toString();
        }
        else {
            typePos = new position(ctx.Identifier());
            baseTypeName = ctx.Identifier().toString();
        }
        if (ctx.expression() != null){
            for (ParserRuleContext expr : ctx.expression())
                exprs.add((exprNode)visit(expr));
        }
        if (ctx.LeftBracket() != null) {
            dim = ctx.LeftBracket().size();
        }
        else dim = 0;
        return new newExpr(new typeNode(baseTypeName, dim, typePos),
                           exprs, new position(ctx));
    }
}
