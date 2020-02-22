package BackEnd;

import AST.*;
import MIR.*;
import MIR.IRoperand.*;
import MIR.IRinst.*;
import MIR.IRtype.*;
import Util.position;
import Util.scope.globalScope;
import Util.symbol.*;
import Util.symbol.funcDecl;
import Util.symbol.varEntity;

import java.util.ArrayList;

import static MIR.IRinst.Binary.BinaryOpCategory.*;
import static MIR.IRinst.Cmp.CmpOpCategory.*;

public class IRBuilder implements ASTVisitor {

    private boolean isParam;
    private globalScope gScope;
    private Root irRoot = new Root();
    private position beginning = new position(0, 0);
    private classType currentClass = null;
    private Function currentFunction = null;
    private IRBlock currentBlock = null;
    ArrayList<Return> returnList = new ArrayList<>();

    private IRBaseType getIRType(Type type) {
        if (type instanceof arrayType) {
            IRBaseType tmp = getIRType(type.baseType());
            for (int i = 0; i < type.dim();++i)
                tmp = new Pointer(tmp);
        }
        else if (type.isInt()) return new IntType(32);
        else if (type.isBool()) return new BoolType();
        else if (type.isVoid()) return new VoidType();
        else if (type.isClass()) {
            String name = ((classType)type).name();
            if (name.equals("string")) return new Pointer(new IntType(8));
            else return new Pointer(irRoot.getType(name));
        }
        else if (type.isNull()) return new VoidType();
        return new VoidType(); //really do so? or just throw error? type is function/constructor
    }
    private IRBaseType getIRReferenceType(Type type) {
        IRBaseType tmp = getIRType(type);
        if (tmp instanceof Pointer) return tmp;
        else return new Pointer(tmp);
    }

    private void setBuiltinMethod(String name) {
        gScope.getMethod(name, beginning, false)
                .setFunction(irRoot.getFunction("global_" + name));
    }
    private Operand resolvePointer(IRBlock currentBlock, Operand it) {
        if (it.type() instanceof Pointer) {
            Register dest = new Register(((Pointer)it.type()).pointTo(),
                    "resolved_" + ((Register)it).name());
            currentBlock.addInst(new Load(dest, it));
            return dest;
        } else return it;
    }
    private void assign(Operand reg, exprNode expr) {
        if (expr.type().isBool()) {
            IRBlock thenBlock = new IRBlock("boolAssignThen"),
                    elseBlock = new IRBlock("boolAssignElse"),
                    destBlock = new IRBlock("boolAssignDest");
            expr.setThenBlock(thenBlock);
            expr.setElseBlock(elseBlock);
            expr.accept(this);
            if (reg.type() instanceof Pointer){
                thenBlock.addInst(new Store(reg, new ConstBool(true)));
                elseBlock.addInst(new Store(reg, new ConstBool(false)));
            } else {
                thenBlock.addInst(new Move(new ConstBool(true), reg));
                elseBlock.addInst(new Move(new ConstBool(false), reg));
            }
            thenBlock.addTerminator(new Jump(destBlock));
            elseBlock.addTerminator(new Jump(destBlock));
            currentBlock = destBlock;
        }
        else {
            expr.accept(this);
            if (reg.type() instanceof Pointer)
                //todo: not all instanceof pointer means to store: some is pointer of a class
                currentBlock.addInst(new Store(reg, expr.operand()));
            else currentBlock.addInst(new Move(expr.operand(), reg));
        }
    }
    private void branchAdd(exprNode it) {
        if (it.thenBlock() != null){
            Operand tmp = resolvePointer(currentBlock, it.operand());
            currentBlock.addTerminator(new Branch(tmp, it.thenBlock(), it.elseBlock()));
        }
    }

    public IRBuilder(globalScope gScope) {
        isParam = false;
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
        //todo: add more: stringPlus, stringCmp
        //todo: set all types into the
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
        currentClass = (classType)gScope.getType(it.Identifier(), it.pos());
        it.members().forEach(member -> member.accept(this));
        it.methods().forEach(method -> method.accept(this));
        if (it.hasConstructor())
            it.constructors().forEach(constructor-> constructor.accept(this));
        //notice: it is better to add a default constructor, though not strictly required
    }

    @Override
    public void visit(funDef it) {
        funcDecl func = it.decl();
        returnList.clear();
        currentFunction = func.function();
        currentBlock = currentFunction.entryBlock();
        if (it.isMethod())
            currentFunction.setClassPtr(new Register(
                    new Pointer(getIRReferenceType(currentClass)), "this"));

        isParam = true;
        it.parameters().forEach(param -> param.accept(this));
        isParam = false;

        it.body().accept(this);

        if (returnList.size() == 0) {
            if (currentFunction.name().equals("main"))
                currentBlock.addTerminator(new Return(currentBlock, new ConstInt(0)));
            else currentBlock.addTerminator(new Return(currentBlock, null));
        } else if (returnList.size() > 1) {
            IRBlock rootReturn = new IRBlock("rootReturn");
            Operand returnValue = new Register(returnList.get(0).value().type(), "rootRet");
            returnList.forEach(ret -> {
                ret.currentBlock().removeTerminal();
                ret.currentBlock().addInst(new Move(ret.value(), returnValue));
                ret.currentBlock().addTerminator(new Jump(rootReturn));
            });
            rootReturn.addTerminator(new Return(rootReturn, returnValue));
        }

        returnList.clear();
        currentFunction = null;
        currentBlock = null;
    }

    @Override
    public void visit(varDef it) {
        varEntity entity = it.entity();
        Operand reg;
        IRBaseType type = getIRReferenceType(entity.type());

        if (entity.isGlobal()) {
            reg = new GlobalReg(type, it.name());
            it.entity().setOperand(reg);
            irRoot.addGlobalVar((GlobalReg)reg);
        }
        else {
            if (isParam) { //is parameter
                reg = new Param(type, it.name());
                currentFunction.addParam(it.name() + "_addr", (Param)reg);
            } else {
                reg = new Register(new Pointer(type), it.name() + "_addr");
                if (it.init() != null) assign(reg, it.init());
                it.entity().setOperand(reg);
                currentFunction.addVar(it.name(), getIRType(entity.type()));
            }
        }
    }

    @Override public void visit(varDefList it) {}

    @Override
    public void visit(blockNode it) {
        for (stmtNode stmt : it.getStmtList()) {
            stmt.accept(this);
            if (currentBlock.terminated()) break;
        }
    }

    @Override
    public void visit(exprStmt it) {
        it.expr().accept(this);
    }

    @Override
    public void visit(ifStmt it) {
        IRBlock thenBlock = new IRBlock("if_then"),
                elseBlock = new IRBlock("if_else"),
                destBlock = new IRBlock("if_terminal");

        it.condition().accept(this);
        currentBlock = thenBlock;
        it.trueStmt().accept(this);
        if (it.falseStmt() != null) {
            currentBlock = elseBlock;
            it.falseStmt().accept(this);
        }
        if (!thenBlock.terminated()) thenBlock.addTerminator(new Jump(destBlock));
        if (!elseBlock.terminated()) elseBlock.addTerminator(new Jump(destBlock));
        currentBlock = destBlock;
    }

    @Override
    public void visit(forStmt it) {
        IRBlock bodyBlock = new IRBlock("for_body"),
                destBlock = new IRBlock("for_dest"),
                condBlock = new IRBlock("for_cond");

        it.setDestBlock(destBlock);
        if (it.init() != null) it.init().accept(this);

        currentBlock = condBlock;
        if (it.incr() != null) it.incr().accept(this);
        if (it.condition() != null){
            it.condition().setThenBlock(bodyBlock);
            it.condition().setElseBlock(destBlock);
            it.condition().accept(this);
        }
        else condBlock.addTerminator(new Jump(bodyBlock));
        it.setCondBlock(condBlock);
        currentBlock = bodyBlock;
        it.body().accept(this);
        if (!currentBlock.terminated()) {
            currentBlock.addTerminator(new Jump(condBlock));
        }
        currentBlock = destBlock;
    }

    @Override
    public void visit(whileStmt it) {
        IRBlock bodyBlock = new IRBlock("while_body"),
                destBlock = new IRBlock("while_dest"),
                condBlock = new IRBlock("while_cond");

        it.setDestBlock(destBlock);
        currentBlock.addTerminator(new Jump(condBlock));

        currentBlock = condBlock;
        if (it.condition() != null){
            it.condition().setThenBlock(bodyBlock);
            it.condition().setElseBlock(destBlock);
            it.condition().accept(this);
        }
        else condBlock.addTerminator(new Jump(bodyBlock));
        it.setCondBlock(condBlock);
        currentBlock = bodyBlock;
        it.body().accept(this);
        if (!currentBlock.terminated()) currentBlock.addTerminator(new Jump(condBlock));

        currentBlock = destBlock;
    }

    @Override
    public void visit(returnStmt it) {
        Return retInst;
        if (it.retValue() != null) {
            retInst = new Return(currentBlock, null);
        } else {
            Operand ret = new Register(, "returnValue");
            assign(ret, it.retValue());
            retInst = new Return(currentBlock, ret);
        }
        returnList.add(retInst);
    }

    @Override
    public void visit(breakStmt it) {
        IRBlock dest;
        if (it.dest() instanceof whileStmt) dest = ((whileStmt)it.dest()).destBlock();
        else dest = ((forStmt)it.dest()).destBlock();
        Jump ret = new Jump(dest);
        currentBlock.addTerminator(ret);
    }

    @Override
    public void visit(continueStmt it) {
        IRBlock dest;
        if (it.dest() instanceof whileStmt) dest = ((whileStmt)it.dest()).condBlock();
        else dest = ((forStmt)it.dest()).condBlock();
        Jump ret = new Jump(dest);
        currentBlock.addTerminator(ret);
    }

    @Override public void visit(emptyStmt it) {}

    @Override
    public void visit(exprList it) {
        //todo
    }

    @Override public void visit(typeNode it) {}

    @Override
    public void visit(arrayExpr it) {
        it.base().accept(this);
        it.width().accept(this);
        Operand pointer = it.base().operand(),
                width = resolvePointer(currentBlock, it.width().operand()),
                offset = new Register(new IntType(32), "offset");
        Operand ElementSize = new ConstInt(
                it.type().dim() > 0 ? 32 : it.type().baseType().size()
        );
        it.setOperand(new Register(new IntType(32), "arrayElePointer"));

        currentBlock.addInst(new Binary(ElementSize, width, offset, mul));
        currentBlock.addInst(new Binary(pointer, offset, it.operand(), add));
    }

    @Override
    public void visit(binaryExpr it) {
        Operand src1, src2;
        Inst inst;
        Binary.BinaryOpCategory binaryOp = null;
        Cmp.CmpOpCategory cmpOp = null;
        Function stringCall = null;

        switch (it.opCode()) {
            case Star: binaryOp = mul;break;
            case Div : binaryOp = sdiv;break;
            case Mod : binaryOp = srem;break;
            case LeftShift : binaryOp = shl;break;
            case RightShift: binaryOp = ashr;break;
            case And : binaryOp = and;break;
            case Or : binaryOp = or;break;
            case Caret : binaryOp = xor;break;
            case Minus : binaryOp = sub;break;
            case Plus : {
                if (it.type().isInt()) binaryOp = add;
                else stringCall = irRoot.getBuiltinFunction("stringAdd");
                break;
            }
            case Less : {
                if (it.type().isInt()) cmpOp = slt;
                else stringCall = irRoot.getBuiltinFunction("stringLess");
                break;
            }
            case Greater: {
                if (it.type().isInt()) cmpOp = sgt;
                else stringCall = irRoot.getBuiltinFunction("stringGreater");
                break;
            }
            case LessEqual: {
                if (it.type().isInt()) cmpOp = sle;
                else stringCall = irRoot.getBuiltinFunction("stringLessEqual");
                break;
            }
            case GreaterEqual: {
                if (it.type().isInt()) cmpOp = sge;
                else stringCall = irRoot.getBuiltinFunction("stringGreaterEqual");
                break;
            }
            case AndAnd : cmpOp = logicalAnd;break;
            case OrOr: cmpOp = logicalOr;break;
            case Equal: {
                if (it.src1().type().sameType(gScope.getStringType()))
                    stringCall = irRoot.getBuiltinFunction("stringEqual");
                else cmpOp = eq;
                break;
            }
            case NotEqual: {
                if (it.src1().type().sameType(gScope.getStringType()))
                    stringCall = irRoot.getBuiltinFunction("stringNotEqual");
                else cmpOp = ne;
                break;
            }
        }

        switch (it.opCode()) {
            case Star:
            case Div :
            case Mod :
            case LeftShift :
            case RightShift:
            case And :
            case Or :
            case Caret :
            case Minus :
            case Plus : {
                it.src1().accept(this);
                it.src2().accept(this);
                if (binaryOp != null) {
                    src1 = resolvePointer(currentBlock, it.src1().operand());
                    src2 = resolvePointer(currentBlock, it.src2().operand());
                    it.setOperand(new Register(new IntType(32), "binary_" + binaryOp.toString()));
                    inst = new Binary(src1, src2, it.operand(), binaryOp);
                } else {
                    it.setOperand(new Register(new Pointer(new IntType(8)), "binary_string_plus"));
                    inst = new Call(stringCall, it.operand());
                }
                currentBlock.addInst(inst);
                break;
            }
            case Less :
            case Greater:
            case LessEqual:
            case GreaterEqual: {
                it.src1().accept(this);
                it.src2().accept(this);
                if (cmpOp != null) {
                    src1 = resolvePointer(currentBlock, it.src1().operand());
                    src2 = resolvePointer(currentBlock, it.src2().operand());

                    it.setOperand(new Register(new BoolType(), "cmp_" + cmpOp.toString()));
                    inst = new Cmp(src1, src2, it.operand(), cmpOp);
                } else {
                    it.setOperand(new Register(new BoolType(), "cmp_string_" + it.opCode().toString()));
                    inst = new Call(stringCall, it.operand());
                }
                currentBlock.addInst(inst);
                branchAdd(it);
                break;
            }
            case AndAnd : {
                if (it.thenBlock() != null) {
                    IRBlock condBlock = new IRBlock("logicalAnd_tmp");
                    it.src1().setThenBlock(condBlock);
                    it.src1().setElseBlock(it.elseBlock());
                    it.src1().accept(this);

                    currentBlock = condBlock;
                    it.src2().setThenBlock(it.thenBlock());
                    it.src2().setElseBlock(it.elseBlock());
                    it.src2().accept(this);
                } else {
                    it.src1().accept(this);
                    it.src2().accept(this);
                    src1 = resolvePointer(currentBlock, it.src1().operand());
                    src2 = resolvePointer(currentBlock, it.src2().operand());
                    it.setOperand(new Register(new BoolType(), "logicalAnd"));
                    currentBlock.addInst(new Cmp(src1, src2, it.operand(), cmpOp));
                }
            }
            case OrOr: {
                if (it.thenBlock() != null) {
                    IRBlock condBlock = new IRBlock("logicalOr_tmp");
                    it.src1().setThenBlock(it.thenBlock());
                    it.src1().setElseBlock(condBlock);
                    it.src1().accept(this);

                    currentBlock = condBlock;
                    it.src2().setThenBlock(it.thenBlock());
                    it.src2().setElseBlock(it.elseBlock());
                    it.src2().accept(this);
                } else {
                    it.src1().accept(this);
                    it.src2().accept(this);
                    src1 = resolvePointer(currentBlock, it.src1().operand());
                    src2 = resolvePointer(currentBlock, it.src2().operand());
                    it.setOperand(new Register(new BoolType(), "logicalOr"));
                    currentBlock.addInst(new Cmp(src1, src2, it.operand(), cmpOp));
                }
            }
            case Equal:
            case NotEqual: {
                //todo
                if (cmpOp != null) {}
                else {}
            }
        }
    }

    @Override
    public void visit(assignExpr it) {
        it.src1().accept(this);
        assign(it.src1().operand(), it.src2());
        it.setOperand(it.src2().operand());
    }

    @Override
    public void visit(prefixExpr it) {
        it.src().accept(this);
        IRBaseType type;
        if (it.opCode().ordinal() < 5) type = new IntType(32);
        else type = new BoolType();
        it.setOperand(new Register(type, "prefix_" + it.opCode().toString()));

        Operand src = resolvePointer(currentBlock, it.src().operand());
        switch (it.opCode()) {
            case Positive: {
                currentBlock.addInst(new Binary(src, new ConstInt(0), it.operand(), add));
                break;
            }
            case Negative: {
                currentBlock.addInst(new Binary( new ConstInt(0), src, it.operand(), sub));
                break;
            }
            case Tilde: {
                currentBlock.addInst(new Binary(src, new ConstInt(-1), it.operand(), xor));
                break;
            }
            case Increment: {
                currentBlock.addInst(new Binary(src, new ConstInt(1), it.operand(), add));
                if (it.src().isAssignable())
                    currentBlock.addInst(new Store(it.src().operand(), it.operand()));
                break;
            }
            case Decrement: {
                currentBlock.addInst(new Binary(src, new ConstInt(1), it.operand(), sub));
                if (it.src().isAssignable())
                    currentBlock.addInst(new Store(it.src().operand(), it.operand()));
                break;
            }
            case Not: {
                currentBlock.addInst(new Binary(src, new ConstBool(true), it.operand(), xor));
                break;
            }
        }
    }

    @Override
    public void visit(suffixExpr it) {
        it.src().accept(this);
        ConstInt one = new ConstInt(1);
        Binary inst;
        Operand src;
        Register tmp = new Register(new IntType(32), "suffix_tmp");
        it.setOperand(new Register(new IntType(32), "suffix"));

        currentBlock.addInst(new Load((Register)it.operand(), it.src().operand()));

        if (it.opCode() == 0) inst = new Binary(it.operand(), one, tmp, add);
        else inst = new Binary(it.operand(), one, tmp, sub);
        currentBlock.addInst(inst);
        currentBlock.addInst(new Store(it.src().operand(), tmp));

    }

    @Override
    public void visit(thisExpr it) {
        it.setOperand(currentFunction.getClassPtr());
    }

    @Override
    public void visit(funCallExpr it) {
        if (!it.type().isVoid())
            it.setOperand(new Register(getIRType(it.type()), "funCallRet"));
        if (((funcDecl)it.type()).name().equals("size")) {
            it.setOperand(new Register(new IntType(32), "array_size"));
            //todo: call size of an array
        }
        it.params().forEach(param -> param.accept(this));
        //todo
    }

    @Override
    public void visit(methodExpr it) {
        it.caller().accept(this);
        if (it.caller().type().isArray()) {
            return;
        } else {
            it.setOperand(it.caller().operand());
        }
    }

    @Override
    public void visit(memberExpr it) {
        it.caller().accept(this);
        Operand classPtr = it.caller().operand();
        Operand dest = new Register(getIRReferenceType(it.type()), "this." + it.member());
        currentBlock.addInst(new Binary(classPtr, it.entity().offset(), dest, add));
        it.setOperand(dest);
    }

    @Override
    public void visit(newExpr it) {
        //todo
        Operand pointer = new Register(getIRReferenceType(it.type()), "new_pointer");
        if (it.type() instanceof arrayType) {

        } else {
        }
    }

    @Override
    public void visit(funcNode it) {
        funcDecl func = (funcDecl)it.type();
        if (func.isMethod()) {
            it.setOperand(currentFunction.getClassPtr());   //record "this"
        }
    }

    @Override
    public void visit(varNode it) {
        varEntity entity = it.entity();
        if (it.entity().isMember()) {
            Operand classPtr = currentFunction.getClassPtr();
            Operand dest = new Register(getIRReferenceType(it.type()), "this." + it.name());
            currentBlock.addInst(new Binary(classPtr, it.entity().offset(), dest, add));
            it.setOperand(dest);
        } else {
            it.setOperand(entity.asOperand());
        }
        branchAdd(it);
    }

    @Override
    public void visit(intLiteral it) {
        it.setOperand(new ConstInt(it.value()));
    }

    @Override
    public void visit(boolLiteral it) {
        it.setOperand(new ConstBool(it.value()));
        branchAdd(it);
    }

    @Override
    public void visit(nullLiteral it) {
        it.setOperand(new Null());
    }

    @Override
    public void visit(stringLiteral it) {
        it.setOperand(new ConstString(it.value()));
    }
}
