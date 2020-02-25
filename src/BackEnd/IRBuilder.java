package BackEnd;

import AST.*;
import MIR.*;
import MIR.IRoperand.*;
import MIR.IRinst.*;
import MIR.IRtype.*;
import Util.error.internalError;
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

    private IRBaseType getIRType(Type type, boolean isMemSet) {
        if (type instanceof arrayType) {
            IRBaseType tmp = getIRType(type.baseType(), isMemSet);
            for (int i = 0; i < type.dim();++i)
                tmp = new Pointer(tmp, false);
        }
        else if (type.isInt()) return new IntType(32);
        else if (type.isBool()) {
            if (isMemSet) return new IntType(8);
            return new BoolType();
        }
        else if (type.isVoid()) return new VoidType();
        else if (type.isClass()) {
            String name = ((classType)type).name();
            if (name.equals("string")) return new Pointer(new IntType(8), false);
            else return irRoot.getType(name);
        }
        else if (type.isNull()) return new VoidType();
        return new VoidType(); //really do so? or just throw error? type is function/constructor
    }
    private IRBaseType getIRRetType(Type type) {
        IRBaseType retType = getIRType(type, false);
        if (retType instanceof ClassType)
            retType = new Pointer(retType, false);
        return retType;
    }

    private void setBuiltinMethod(String name) {
        gScope.getMethod(name, beginning, false)
                .setFunction(irRoot.getFunction("global_" + name));
    }
    private Operand resolvePointer(IRBlock currentBlock, Operand it) {
        //notice that this can only be used for primitive type;
        if (it.type() instanceof Pointer && ((Pointer)it.type()).isReference()) {
            Register dest = new Register(((Pointer)it.type()).pointTo(),
                    "resolved_" + ((Register)it).name());
            currentBlock.addInst(new Load(dest, it));
            if (dest.type() instanceof IntType && ((IntType)dest.type()).size() == 8) {
                Register zextDest = new Register(new BoolType(), "zext_" + dest.name());
                currentBlock.addInst(new Zext(dest, zextDest));
                return zextDest;
            } else return dest;
        } else return it;

    }
    private Operand resolveStringPointer(IRBlock currentBlock, Operand it) {
        if (it.type() instanceof Pointer) {
            if (((Pointer)it.type()).pointTo() instanceof Pointer) {
                Register dest = new Register(((Pointer)it.type()).pointTo(),
                        "resolved_" + ((Register)it).name());
                currentBlock.addInst(new Load(dest, it));
                return dest;
            }
            else return it;
        }
        else throw new internalError("not actually resolve a string", new position(0, 0));
    }

    private void assign(Operand reg, exprNode expr) {
        expr.accept(this);
        Operand tmp;
        assert reg.type() instanceof Pointer;
        if (expr.operand().type() instanceof BoolType) {
            if (expr.operand() instanceof ConstBool) {
                tmp = new ConstInt(((ConstBool)expr.operand()).value() ? 1 : 0, 8);
            } else {
                tmp = new Register(new IntType(8), "extTo");
                currentBlock.addInst(new Zext(expr.operand(), tmp));
            }
        } else tmp = expr.operand();
        currentBlock.addInst(new Store(reg, tmp));
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
                    new Pointer(getIRType(currentClass, false), false),
                    "this"));

        currentFunction.setRetType(getIRRetType(func.returnType()));

        isParam = true;
        it.parameters().forEach(param -> param.accept(this));
        isParam = false;

        it.body().accept(this);

        if (returnList.size() == 0) {
            if (currentFunction.name().equals("main"))
                currentBlock.addTerminator(new Return(currentBlock, new ConstInt(0, 32)));
            else currentBlock.addTerminator(new Return(currentBlock, null));
        } else if (returnList.size() > 1) {
            IRBlock rootReturn = new IRBlock("rootReturn");
            Register returnValue = new Register(returnList.get(0).value().type(), "rootRet");
            ArrayList<Operand> values = new ArrayList<>();
            ArrayList<IRBlock> blocks = new ArrayList<>();
            returnList.forEach(ret -> {
                ret.currentBlock().removeTerminal();
                values.add(ret.value());
                blocks.add(ret.currentBlock());
                ret.currentBlock().addTerminator(new Jump(rootReturn));
            });
            rootReturn.addInst(new Phi(returnValue, blocks, values));
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
        IRBaseType type = getIRType(entity.type(), true);
        boolean isResolvable = !(type instanceof ClassType) ||
                                entity.type().sameType(gScope.getStringType());
        //except for string, all classes are not resolvable(only a pointer to the head)
        if (entity.isGlobal()) {
            reg = new GlobalReg(new Pointer(type, isResolvable), it.name());
            it.entity().setOperand(reg);
            irRoot.addGlobalVar((GlobalReg)reg);
        }
        else {
            if (isParam) {
                reg = new Param(type, it.name());
                currentFunction.addParam(it.name() + "_param", (Param)reg);
            } else {
                reg = new Register(new Pointer(type, true), it.name() + "_addr");
                if (it.init() != null) assign(reg, it.init());
                it.entity().setOperand(reg);
                currentFunction.addVar(it.name(), type);
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
        if (it.condition() != null){
            it.condition().setThenBlock(bodyBlock);
            it.condition().setElseBlock(destBlock);
            it.condition().accept(this);
        }
        else condBlock.addTerminator(new Jump(bodyBlock));
        it.setCondBlock(condBlock);
        currentBlock = bodyBlock;
        it.body().accept(this);
        if (it.incr() != null) it.incr().accept(this);
        if (!currentBlock.terminated())
            currentBlock.addTerminator(new Jump(condBlock));
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
            it.retValue().accept(this);
            Operand ret;
            if (it.retValue().operand().type().dim() > currentFunction.retType().dim())
                ret = resolvePointer(currentBlock, it.retValue().operand());
            else ret = it.retValue().operand();
            retInst = new Return(currentBlock, ret);
        }
        currentBlock.addTerminator(retInst);
        returnList.add(retInst);
    }

    @Override
    public void visit(breakStmt it) {
        IRBlock dest;
        if (it.dest() instanceof whileStmt) dest = ((whileStmt)it.dest()).destBlock();
        else dest = ((forStmt)it.dest()).destBlock();
        currentBlock.addTerminator(new Jump(dest));
    }

    @Override
    public void visit(continueStmt it) {
        IRBlock dest;
        if (it.dest() instanceof whileStmt) dest = ((whileStmt)it.dest()).condBlock();
        else dest = ((forStmt)it.dest()).condBlock();
        currentBlock.addTerminator(new Jump(dest));
    }

    @Override public void visit(emptyStmt it) {}
    @Override public void visit(exprList it) {}
    @Override public void visit(typeNode it) {}

    @Override
    public void visit(arrayExpr it) {
        it.base().accept(this);
        it.width().accept(this);
        Operand pointer = it.base().operand(),
                width = resolvePointer(currentBlock, it.width().operand());
        it.setOperand(new Register(
                new Pointer(getIRType(it.type(), true), true),
                "arrayElePointer"));

        currentBlock.addInst(new GetElementPtr(((Pointer)pointer.type()).pointTo(),
                            pointer, width, null, (Register)it.operand()));
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
                    inst = new Binary(src1, src2, (Register)it.operand(), binaryOp);
                } else {
                    it.setOperand(new Register(new Pointer(new IntType(8), false), "binary_string_plus"));
                    ArrayList<Operand> params = new ArrayList<>();

                    src1 = resolveStringPointer(currentBlock, it.src1().operand());
                    src2 = resolveStringPointer(currentBlock, it.src2().operand());

                    params.add(src1);
                    params.add(src2);
                    inst = new Call(stringCall, params, (Register)it.operand());
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
                    inst = new Cmp(src1, src2, (Register)it.operand(), cmpOp);
                } else {
                    it.setOperand(new Register(new BoolType(), "cmp_string_" + it.opCode().toString()));

                    ArrayList<Operand> params = new ArrayList<>();

                    src1 = resolveStringPointer(currentBlock, it.src1().operand());
                    src2 = resolveStringPointer(currentBlock, it.src2().operand());

                    params.add(src1);
                    params.add(src2);

                    inst = new Call(stringCall, params, (Register)it.operand());
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
                    IRBlock condBlock = new IRBlock("AndCondBlock"),
                            destBlock = new IRBlock("AndDestBlock");
                    Operand opr;

                    ArrayList<Operand> values = new ArrayList<>();
                    ArrayList<IRBlock> blocks = new ArrayList<>();

                    it.setOperand(new Register(new BoolType(), "logicalAnd"));

                    it.src1().setThenBlock(condBlock);
                    it.src1().setElseBlock(destBlock);
                    it.src1().accept(this);
                    values.add(new ConstBool(false));
                    blocks.add(currentBlock);

                    currentBlock = condBlock;
                    it.src2().accept(this);
                    opr = resolvePointer(currentBlock, it.src2().operand());
                    currentBlock.addTerminator(new Jump(destBlock));
                    values.add(opr);
                    blocks.add(currentBlock);

                    currentBlock = destBlock;
                    destBlock.addInst(new Phi((Register)it.operand(), blocks, values));
                }
                break;
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
                    IRBlock condBlock = new IRBlock("OrCondBlock"),
                            destBlock = new IRBlock("OrDestBlock");
                    Operand opr;

                    ArrayList<Operand> values = new ArrayList<>();
                    ArrayList<IRBlock> blocks = new ArrayList<>();

                    it.setOperand(new Register(new BoolType(), "logicalOr"));

                    it.src1().setThenBlock(destBlock);
                    it.src1().setElseBlock(condBlock);
                    it.src1().accept(this);
                    values.add(new ConstBool(true));
                    blocks.add(currentBlock);

                    currentBlock = condBlock;
                    it.src2().accept(this);
                    opr = resolvePointer(currentBlock, it.src2().operand());
                    currentBlock.addTerminator(new Jump(destBlock));
                    values.add(opr);
                    blocks.add(currentBlock);

                    currentBlock = destBlock;
                    destBlock.addInst(new Phi((Register)it.operand(), blocks, values));
                }
                break;
            }
            case Equal:
            case NotEqual: {
                it.src1().accept(this);
                it.src2().accept(this);
                it.setOperand(new Register(new BoolType(),  it.opCode().toString()));
                if (cmpOp != null) {
                    src1 = resolvePointer(currentBlock, it.src1().operand());
                    src2 = resolvePointer(currentBlock, it.src2().operand());
                    currentBlock.addInst(new Cmp(src1, src2, (Register)it.operand(), cmpOp));
                }
                else {
                    ArrayList<Operand> params = new ArrayList<>();

                    src1 = resolveStringPointer(currentBlock, it.src1().operand());
                    src2 = resolveStringPointer(currentBlock, it.src2().operand());

                    params.add(src1);
                    params.add(src2);
                    currentBlock.addInst(new Call(stringCall, params, (Register)it.operand()));
                }
                branchAdd(it);
                break;
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
                currentBlock.addInst(new Binary(src, new ConstInt(0, 32), (Register)it.operand(), add));
                break;
            }
            case Negative: {
                currentBlock.addInst(new Binary( new ConstInt(0, 32), src, (Register)it.operand(), sub));
                break;
            }
            case Tilde: {
                currentBlock.addInst(new Binary(src, new ConstInt(-1, 32), (Register)it.operand(), xor));
                break;
            }
            case Increment: {
                currentBlock.addInst(new Binary(src, new ConstInt(1, 32), (Register)it.operand(), add));
                if (it.src().isAssignable())
                    currentBlock.addInst(new Store(it.src().operand(), it.operand()));
                break;
            }
            case Decrement: {
                currentBlock.addInst(new Binary(src, new ConstInt(1, 32), (Register)it.operand(), sub));
                if (it.src().isAssignable())
                    currentBlock.addInst(new Store(it.src().operand(), it.operand()));
                break;
            }
            case Not: {
                currentBlock.addInst(new Binary(src, new ConstBool(true), (Register)it.operand(), xor));
                break;
            }
        }
    }

    @Override
    public void visit(suffixExpr it) {
        it.src().accept(this);
        ConstInt one = new ConstInt(1, 32);
        Binary inst;
        Register tmp = new Register(new IntType(32), "suffix_tmp");
        it.setOperand(resolvePointer(currentBlock, it.src().operand()));

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
        it.callee().accept(this);
        if (((funcDecl)it.type()).name().equals("size")) {
            it.setOperand(new Register(new IntType(32), "array_size"));
            assert it.callee().operand().type() instanceof Pointer;
            Register metaPtr = new Register(new IntType(32), "metadataPtr"),
                     BCPtr = new Register(new IntType(32), "bitCastPtr");
            currentBlock.addInst(new BitCast(it.callee().operand(), BCPtr));
            currentBlock.addInst(new Binary(BCPtr, new ConstInt(4, 32), metaPtr, sub));
            currentBlock.addInst(new Load((Register)it.operand(), metaPtr));
        } else {
            if (!it.type().isVoid())
                it.setOperand(new Register(getIRRetType(((funcDecl)it.callee().type()).returnType()),
                        "funCallRet"));
            else it.setOperand(null);
            ArrayList<Operand> params = new ArrayList<>();
            it.params().forEach(param -> {
                param.accept(this);
                params.add(param.operand());
            });
            currentBlock.addInst(new Call(((funcDecl) it.callee().type()).function(), params, (Register) it.operand()));
        }
        if (it.operand() != null)
            branchAdd(it);
    }

    @Override
    public void visit(methodExpr it) {
        it.caller().accept(this);
        it.setOperand(it.caller().operand());   //record the array pointer or the caller class pointer(this)
    }

    @Override
    public void visit(memberExpr it) {
        it.caller().accept(this);
        Register classPtr = (Register)it.caller().operand();
        it.setOperand(new Register(it.entity().asOperand().type(), "this." + it.member()));
        currentBlock.addInst(new GetElementPtr(((Pointer)classPtr.type()).pointTo(), classPtr,
                            new ConstInt(0, 32), it.entity().elementIndex(),
                            (Register)it.operand()));
        branchAdd(it);
    }

    @Override
    public void visit(newExpr it) {
        if (it.type() instanceof arrayType) {
            it.setOperand(new Register(getIRType(it.type(), true), "new_result"));
            arrayMalloc(0, it, (Register)it.operand());
        } else {
            Register mallocTmp = new Register(new Pointer(new IntType(8), false), "mallocTmp");
            it.setOperand(new Register(new Pointer(getIRType(it.type(), true), false),
                                        "new_class_ptr"));
            currentBlock.addInst(new Malloc(
                    new ConstInt(((classType)it.type()).allocSize() / 8, 32), mallocTmp
            ));
            currentBlock.addInst(new BitCast(mallocTmp, (Register)it.operand()));
            if (((classType)it.type()).scope().constructor() != null) {
                ArrayList<Operand> params = new ArrayList<>();
                params.add(it.operand());
                currentBlock.addInst(new Call(
                        ((classType)it.type()).scope().constructor().function(), params, null
                ));
            }
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
            Register classPtr = currentFunction.getClassPtr();
            it.setOperand(new Register(it.entity().asOperand().type(), "this." + it.name()));
            currentBlock.addInst(new GetElementPtr(classPtr.type(), classPtr, new ConstInt(0, 32),
                                 it.entity().elementIndex(), (Register)it.operand()));
        } else {
            it.setOperand(entity.asOperand());
        }
        branchAdd(it);
    }

    @Override
    public void visit(intLiteral it) {
        it.setOperand(new ConstInt(it.value(), 32));
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

    private void arrayMalloc(int nowDim, newExpr it,
                               Register result) {
        if (nowDim == it.exprs().size()) return;
        it.exprs().get(nowDim).accept(this);
        IRBaseType pointTo = ((Pointer)result.type()).pointTo();

        Operand currentNum = resolvePointer(currentBlock, it.exprs().get(nowDim).operand());
        ConstInt typeWidth = new ConstInt(((Pointer)result.type()).pointTo().size(), 32);
        Register PureWidth = new Register(new IntType(32), "pureWidth"),
                 metaWidth = new Register(new IntType(32), "metaWidth");
        Register allocPtr = new Register(new Pointer(new IntType(8), false), "allocPtr"),
                 allocBitCast = new Register(new Pointer(new IntType(32), false), "allocBitCast"),
                 allocOffset = new Register(new Pointer(new IntType(32), false), "offsetHead");

        currentBlock.addInst(new Binary(currentNum, typeWidth, PureWidth, mul));
        currentBlock.addInst(new Binary(PureWidth, new ConstInt(32, 32), metaWidth, add));
        currentBlock.addInst(new Malloc(metaWidth, allocPtr));
        currentBlock.addInst(new BitCast(allocPtr, allocBitCast));
        currentBlock.addInst(new Store(allocBitCast, currentNum));
        if (pointTo instanceof IntType && pointTo.size() == 32) {
            currentBlock.addInst(new Binary(allocBitCast, new ConstInt(4, 32), result, add));
        } else {
            currentBlock.addInst(new Binary(allocBitCast, new ConstInt(4, 32), allocOffset, add));
            currentBlock.addInst(new BitCast(allocOffset, result));
        }
        if (nowDim < it.exprs().size() - 1){
            IRBlock incrBlock = new IRBlock("ArrayIncrBlock"),
                    bodyBlock = new IRBlock("ArrayBodyBlock"),
                    destBlock = new IRBlock("ArrayDestBlock");
            assert pointTo instanceof Pointer;
            Register ptr = new Register(pointTo, "pointer"),
                     counter = new Register(new IntType(32), "counter"),
                     counterTmp = new Register(new IntType(32), "counterTmp"),
                     branchJudge = new Register(new BoolType(), "branchJudge");
            ArrayList<Operand> values = new ArrayList<>();
            ArrayList<IRBlock> blocks = new ArrayList<>();
            //here use the ptr as i32* is correct, so use allocBitCast to get the ptr is ok
            //this is especially efficient since I can use 1-base counter to get 0-base ptr
            values.add(new ConstInt(0, 32));
            blocks.add(currentBlock);
            currentBlock.addTerminator(new Jump(incrBlock));

            currentBlock = bodyBlock;
            currentBlock.addInst(new GetElementPtr(new IntType(32), allocBitCast, counter, null, ptr));
            arrayMalloc(nowDim + 1, it, ptr);
            currentBlock.addInst(new Binary(counter, new ConstInt(1, 32), counterTmp, add));//counter++
            currentBlock.addTerminator(new Jump(incrBlock));
            values.add(counterTmp);
            blocks.add(currentBlock);

            currentBlock = incrBlock;
            currentBlock.addInst(new Phi(counter, blocks, values));
            currentBlock.addInst(new Cmp(counter, currentNum, branchJudge, sle));
            currentBlock.addTerminator(new Branch(branchJudge, bodyBlock, destBlock));

            currentBlock = destBlock;
        }
    }
}
