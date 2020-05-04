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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import static MIR.IRinst.Binary.BinaryOpCat.*;
import static MIR.IRinst.Cmp.CmpOpCategory.*;

/*
 * this is an MIR Builder(LLVM IR generator) and also:
    * generate def-use chain
    * not collect blocks in function(is done in mem2reg since less condition needs to be judged)
 */
public class IRBuilder implements ASTVisitor {

    private static class logicalPhi{
        ArrayList<Operand> values = new ArrayList<>();
        ArrayList<IRBlock> blocks = new ArrayList<>();
    }

    private boolean isParam;
    private globalScope gScope;
    private Root irRoot;
    private position beginning = new position(0, 0);
    private classType currentClass = null;
    private Function currentFunction = null;
    private IRBlock currentBlock = null;
    private int symbolCtr = 0;
    private ArrayList<Return> returnList = new ArrayList<>();
    private HashMap<IRBlock, logicalPhi> logicalPhiMap = new HashMap<>();

    private void setBuiltinMethod(String name) {
        gScope.getMethod(name, beginning, false)
                .setFunction(irRoot.getBuiltinFunction("g_" + name));
    }
    private String getName(Operand src) {
        if (src instanceof Param) return ((Param) src).name();
        else if (src instanceof GlobalReg) return ((GlobalReg) src).name();
        else if (src instanceof Register) return ((Register) src).name();
        else return "const";
    }
    private Operand resolvePointer(IRBlock currentBlock, Operand it) {
        if (it.type().isResolvable()) {
            String name = getName(it);
            Register dest = new Register(((Pointer)it.type()).pointTo(),
                    "resolved_" + name);
            currentBlock.addInst(new Load(dest, it, currentBlock));
            if (dest.type() instanceof IntType && dest.type().size() == 8) {
                Register zextDest = new Register(new BoolType(), "zext_" + dest.name());
                currentBlock.addInst(new Zext(dest, zextDest, currentBlock));
                return zextDest;
            } else return dest;
        } else return it;

    }
    private Operand resolveStringPointer(IRBlock currentBlock, Operand it) {    //to consider: this is ugly
        if (it.type() instanceof Pointer) {
            if (((Pointer)it.type()).pointTo() instanceof Pointer) {
                Register dest = new Register(((Pointer)it.type()).pointTo(),
                        "resolved_" + getName(it));
                currentBlock.addInst(new Load(dest, it, currentBlock));
                return dest;
            }
            else return it;
        }
        else throw new internalError("not actually resolve a string", new position(0, 0));
    }
    private Operand intTrans(Operand it) {
        if (it.type() instanceof BoolType) {
            Register ext = new Register(Root.i32T, "ext_" + it.toString());
            currentBlock.addInst(new Zext(it, ext, currentBlock));
            return ext;
        } else {
            assert (it.type() instanceof IntType) && (it.type().size() == 32);
            return it;
        }
    }

    private void assign(Operand reg, exprNode expr) {
        expr.accept(this);
        Operand tmp;
        Operand value = resolvePointer(currentBlock, expr.operand());
        assert reg.type() instanceof Pointer;
        if (value.type() instanceof BoolType) {
            if (value instanceof ConstBool) {
                tmp = new ConstInt(((ConstBool)value).value() ? 1 : 0, 8);
            } else {
                tmp = new Register(Root.charT, "extTo");
                currentBlock.addInst(new Zext(value, (Register)tmp, currentBlock));
            }
        } else tmp = value;
        currentBlock.addInst(new Store(reg, tmp, currentBlock));
    }
    private void branchAdd(exprNode it) {
        if (it.thenBlock() != null){
            Operand tmp = resolvePointer(currentBlock, it.operand());
            assert tmp.type() instanceof BoolType;
            currentBlock.addTerminator(new Branch(tmp, it.thenBlock(), it.elseBlock(), currentBlock));
            if (logicalPhiMap.containsKey(it.thenBlock())) {
                logicalPhi phiInfo = logicalPhiMap.get(it.thenBlock());
                phiInfo.values.add(new ConstBool(true));
                phiInfo.blocks.add(currentBlock);
            }
            if (logicalPhiMap.containsKey(it.elseBlock())) {
                logicalPhi phiInfo = logicalPhiMap.get(it.elseBlock());
                phiInfo.values.add(new ConstBool(false));
                phiInfo.blocks.add(currentBlock);
            }
        }
    }

    public IRBuilder(globalScope gScope, Root irRoot) {
        isParam = false;
        this.gScope = gScope;
        this.irRoot = irRoot;
        setBuiltinMethod("print");
        setBuiltinMethod("println");
        setBuiltinMethod("printInt");
        setBuiltinMethod("printlnInt");
        setBuiltinMethod("getString");
        setBuiltinMethod("getInt");
        setBuiltinMethod("toString");
        //the IRTypes are already generated in frontEnd/SymbolCollector & frontEnd/SemanticChecker
    }

    @Override
    public void visit(rootNode it) {
        classType stringType = (classType)gScope.getStringType();
        Function func = new Function("l_string_length");
        func.setRetType(Root.i32T);
        func.addParam(new Param(Root.stringT, "s"));
        irRoot.builtinFunctions().put("l_string_length", func);
        stringType.scope().getMethod("length", null, false).setFunction(func);
        func = new Function("l_string_substring");
        func.setRetType(Root.stringT);
        func.addParam(new Param(Root.stringT, "s"));
        func.setRetType(Root.i32T);
        func.addParam(new Param(Root.i32T, "left"));
        func.addParam(new Param(Root.i32T, "right"));
        irRoot.builtinFunctions().put("l_string_substring", func);
        stringType.scope().getMethod("substring", null, false).setFunction(func);
        func = new Function("l_string_parseInt");
        func.setRetType(Root.i32T);
        func.addParam(new Param(Root.stringT, "s"));
        irRoot.builtinFunctions().put("l_string_parseInt", func);
        stringType.scope().getMethod("parseInt", null, false).setFunction(func);
        func = new Function("l_string_ord");
        func.setRetType(Root.charT);
        func.addParam(new Param(Root.stringT, "s"));
        func.addParam(new Param(Root.i32T, "ord"));
        irRoot.builtinFunctions().put("l_string_ord", func);
        stringType.scope().getMethod("ord", null, false).setFunction(func);
        it.allDef().forEach(node -> {
            if (node instanceof funDef) {
                String name = ((funDef) node).Identifier();
                if (!name.equals("main")) name = "c_" + name;
                Function fun = new Function(name);
                ((funDef)node).decl().setFunction(fun);
                irRoot.addFunction(fun.name(), fun);
            } else if (node instanceof classDef) {
                String className = ((classDef)node).Identifier();
                ((classDef)node).methods().forEach(method -> {
                    Function fun = new Function("l_" + className +
                                                "_" + method.Identifier());
                    method.decl().setFunction(fun);
                    irRoot.addFunction(fun.name(), fun);
                });
                int constructorCnt = 0;
                for (funDef construct : ((classDef) node).constructors()) {
                    Function fun = new Function("l_" + className +
                            "_" + constructorCnt++);
                    construct.decl().setFunction(fun);
                    irRoot.addFunction(fun.name(), fun);
                }
            }
        });
        it.allDef().forEach(node -> node.accept(this));
        irRoot.getInit().exitBlock().addTerminator(new Return(irRoot.getInit().exitBlock(), null));
    }

    @Override
    public void visit(classDef it) {
        currentClass = (classType)gScope.getType(it.Identifier(), it.pos());
        symbolCtr = 0;
        it.members().forEach(member -> member.accept(this));
        it.methods().forEach(method -> method.accept(this));
        if (it.hasConstructor())
            it.constructors().forEach(constructor-> constructor.accept(this));
        //to consider: it is better to add a default constructor, though not strictly required
    }

    private HashSet<IRBlock> entryReachable = new HashSet<>(), returnVisited = new HashSet<>();
    private void entryDFS(IRBlock it) {
        entryReachable.add(it);
        it.successors().forEach(suc -> { if (!entryReachable.contains(suc)) entryDFS(suc); });
    }
    private boolean returnDFS(IRBlock it) {
        returnVisited.add(it);
        ArrayList<IRBlock> precursors = new ArrayList<>(it.precursors());
        for (IRBlock pre : precursors) {
            if (!returnVisited.contains(pre)) {
                if (returnDFS(pre)) pre.removeTerminator();
            }
        }
        if (entryReachable.contains(it)) return false;
        return it.precursors().isEmpty();
    }

    @Override
    public void visit(funDef it) {
        symbolCtr = 0;
        funcDecl func = it.decl();
        returnList.clear();
        currentFunction = func.function();
        currentBlock = currentFunction.entryBlock();
        if (it.isMethod()) currentFunction.setClassPtr(
                new Param(irRoot.getIRType(currentClass, false), "this"));

        currentFunction.setRetType(irRoot.getIRType(func.returnType(), false));

        isParam = true;
        it.parameters().forEach(param -> param.accept(this));
        isParam = false;

        if (currentFunction.name().equals("main"))
            currentBlock.addInst(new Call(irRoot.getInit(), new ArrayList<>(), null, currentBlock));
        it.body().accept(this);

        if (!currentBlock.terminated()) {
            Return retInst;
            if (currentFunction.name().equals("main"))
                retInst = new Return(currentBlock, new ConstInt(0, 32));
            else if (currentFunction.retType() instanceof VoidType)
                retInst = new Return(currentBlock, null);
            else if (currentFunction.retType() instanceof IntType)
                retInst = new Return(currentBlock, new ConstInt(0, currentFunction.retType().size()));
            else if (currentFunction.retType() instanceof BoolType)
                retInst = new Return(currentBlock, new ConstBool(false));
            else if (currentFunction.retType() instanceof Pointer)
                retInst = new Return(currentBlock, new Null());
            else throw new internalError("return a class itself", it.pos());
            currentBlock.addTerminator(retInst);
            returnList.add(retInst);
        }

        entryReachable = new HashSet<>(); returnVisited = new HashSet<>();
        entryDFS(currentFunction.entryBlock());
        for (Iterator<Return> iter = returnList.iterator();iter.hasNext();) {
            Return ret = iter.next();
            if (returnDFS(ret.block())) {
                iter.remove();
                ret.block().removeTerminator();
            }
        }

        entryReachable = null;returnVisited = null;

        if (returnList.size() > 1) {
            IRBlock rootReturn = new IRBlock("rootReturn");
            Register returnValue = returnList.get(0).value() == null ? null :
                    new Register(returnList.get(0).value().type(), "rootRet");
            if (returnValue != null){
                ArrayList<Operand> values = new ArrayList<>();
                ArrayList<IRBlock> blocks = new ArrayList<>();
                returnList.forEach(ret -> {
                    ret.currentBlock().removeTerminator();
                    values.add(ret.value());
                    blocks.add(ret.currentBlock());
                    ret.currentBlock().addTerminator(new Jump(rootReturn, ret.currentBlock()));
                });
                rootReturn.addPhi(new Phi(returnValue, blocks, values, rootReturn));
            } else {
                returnList.forEach(ret -> {
                    ret.currentBlock().removeTerminator();
                    ret.currentBlock().addTerminator(new Jump(rootReturn, ret.currentBlock()));
                });
            }
            rootReturn.addTerminator(new Return(rootReturn, returnValue));
            currentFunction.setExitBlock(rootReturn);
        } else {
            currentFunction.setExitBlock(returnList.get(0).currentBlock());
        }
        IRBlock entryBlock = currentFunction.entryBlock();
        currentFunction.allocVars().forEach(var ->{
            if (((Pointer)var.type()).pointTo() instanceof Pointer)
                entryBlock.addHeadInst(new Store(var, new Null(), entryBlock));
            else entryBlock.addHeadInst(new Store(var, new ConstInt(65536, ((Pointer)var.type()).pointTo().size()), entryBlock));
        });

        returnList.clear();
        currentFunction = null;
        currentBlock = null;
    }

    @Override
    public void visit(varDef it) {
        varEntity entity = it.entity();
        Operand reg;
        IRBaseType type = irRoot.getIRType(entity.type(), true);
        if (entity.isGlobal()) {
            reg = new GlobalReg(new Pointer(type, true), it.name());
            it.entity().setOperand(reg);
            irRoot.addGlobalVar((GlobalReg)reg);
            if (it.init() != null) {
                currentBlock = irRoot.getInit().exitBlock();
                currentFunction = irRoot.getInit();
                assign(it.entity().asOperand(), it.init());
                currentFunction.setExitBlock(currentBlock);
                currentFunction = null;
                currentBlock = null;
            }
        }
        else {
            if (isParam) {
                Param par = new Param(type, it.name() + "_param");
                currentFunction.addParam(par);
                it.entity().setOperand(new Register(new Pointer(type, true), it.name() + "_addr"));
                currentFunction.addVar((Register)it.entity().asOperand());
                currentBlock.addInst(new Store(it.entity().asOperand(), par, currentBlock));
            } else {
                if (currentFunction != null) {
                    reg = new Register(new Pointer(type, true), it.name() + "_addr");
                    if (it.init() != null) assign(reg, it.init());
                    currentFunction.addVar((Register)reg);
                }
                else {
                    //is a member, so no init and no function
                    if (type instanceof ClassType)
                        type = new Pointer(type, false);
                    reg = new Register(new Pointer(type, true), it.name() + "_addr");
                }
                it.entity().setOperand(reg);
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

        if (it.falseStmt() == null) elseBlock = destBlock;
        it.condition().setThenBlock(thenBlock);
        it.condition().setElseBlock(elseBlock);
        it.condition().accept(this);
        currentBlock = thenBlock;
        it.trueStmt().accept(this);
        if (!currentBlock.terminated()) currentBlock.addTerminator(new Jump(destBlock, currentBlock));
        if (it.falseStmt() != null) {
            currentBlock = elseBlock;
            it.falseStmt().accept(this);
            if (!currentBlock.terminated()) currentBlock.addTerminator(new Jump(destBlock, currentBlock));
        }
        if (thenBlock.returnTerminated() && elseBlock.returnTerminated()) return;
        currentBlock = destBlock;
    }

    @Override
    public void visit(forStmt it) {
        IRBlock bodyBlock = new IRBlock("for_body"),
                destBlock = new IRBlock("for_dest"),
                condBlock = new IRBlock("for_cond"),
                incrBlock = new IRBlock("for_incr");

        it.setDestBlock(destBlock);
        if (it.init() != null) it.init().accept(this);

        it.setIncrBlock(incrBlock);
        if (it.condition() != null){
            currentBlock.addTerminator(new Jump(condBlock, currentBlock));
            currentBlock = condBlock;
            it.condition().setThenBlock(bodyBlock);
            it.condition().setElseBlock(destBlock);
            it.condition().accept(this);
        }
        else {
            currentBlock.addTerminator(new Jump(bodyBlock, currentBlock));
            condBlock = bodyBlock;
        }

        currentBlock = bodyBlock;
        it.body().accept(this);
        if (!currentBlock.terminated()) currentBlock.addTerminator(new Jump(incrBlock, currentBlock));
        currentBlock = incrBlock;
        if (it.incr() != null) it.incr().accept(this);
        if (!currentBlock.terminated()) currentBlock.addTerminator(new Jump(condBlock, currentBlock));
        currentBlock = destBlock;
    }

    @Override
    public void visit(whileStmt it) {
        IRBlock bodyBlock = new IRBlock("while_body"),
                destBlock = new IRBlock("while_dest"),
                condBlock = new IRBlock("while_cond");

        it.setDestBlock(destBlock);

        if (it.condition() != null){
            currentBlock.addTerminator(new Jump(condBlock, currentBlock));
            currentBlock = condBlock;
            it.condition().setThenBlock(bodyBlock);
            it.condition().setElseBlock(destBlock);
            it.condition().accept(this);
            it.setCondBlock(condBlock);
        }
        else {
            currentBlock.addTerminator(new Jump(bodyBlock, currentBlock));
            condBlock = bodyBlock;
            it.setCondBlock(bodyBlock);
        }

        currentBlock = bodyBlock;
        it.body().accept(this);
        if (!currentBlock.terminated()) currentBlock.addTerminator(new Jump(condBlock, currentBlock));
        currentBlock = destBlock;
    }

    @Override
    public void visit(returnStmt it) {
        Return retInst;
        if (it.retValue() == null) {
            retInst = new Return(currentBlock, null);
        } else {
            it.retValue().accept(this);
            Operand ret;
            if (it.retValue().operand().type().dim() > currentFunction.retType().dim()){
                assert it.retValue().operand().type().dim() == currentFunction.retType().dim() + 1;
                ret = resolvePointer(currentBlock, it.retValue().operand());
            } else ret = it.retValue().operand();
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
        currentBlock.addTerminator(new Jump(dest, currentBlock));
    }

    @Override
    public void visit(continueStmt it) {
        IRBlock dest;
        if (it.dest() instanceof whileStmt) dest = ((whileStmt)it.dest()).condBlock();
        else dest = ((forStmt)it.dest()).incrBlock();
        currentBlock.addTerminator(new Jump(dest, currentBlock));
    }

    @Override public void visit(emptyStmt it) {}
    @Override public void visit(exprList it) {}
    @Override public void visit(typeNode it) {}

    @Override
    public void visit(arrayExpr it) {
        it.base().accept(this);
        it.width().accept(this);
        Operand pointer = resolvePointer(currentBlock, it.base().operand()),
                width = resolvePointer(currentBlock, it.width().operand());
        it.setOperand(new Register(
                new Pointer(irRoot.getIRType(it.type(), true), true),
                "arrayElePointer"));

        currentBlock.addInst(new GetElementPtr(((Pointer)pointer.type()).pointTo(),
                            pointer, width, null, (Register)it.operand(), currentBlock));
        branchAdd(it);
    }

    @Override
    public void visit(binaryExpr it) {
        Operand src1, src2;
        Inst inst;
        Binary.BinaryOpCat binaryOp = null;
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
                if (it.src1().type().isInt()) binaryOp = add;
                else stringCall = irRoot.getBuiltinFunction("g_stringAdd");
                break;
            }
            case Less : {
                if (it.src1().type().isInt()) cmpOp = slt;
                else stringCall = irRoot.getBuiltinFunction("g_stringLT");
                break;
            }
            case Greater: {
                if (it.src1().type().isInt()) cmpOp = sgt;
                else stringCall = irRoot.getBuiltinFunction("g_stringGT");
                break;
            }
            case LessEqual: {
                if (it.src1().type().isInt()) cmpOp = sle;
                else stringCall = irRoot.getBuiltinFunction("g_stringLE");
                break;
            }
            case GreaterEqual: {
                if (it.src1().type().isInt()) cmpOp = sge;
                else stringCall = irRoot.getBuiltinFunction("g_stringGE");
                break;
            }
            case AndAnd :
            case OrOr: break;
            case Equal: {
                if (it.src1().type().sameType(gScope.getStringType()))
                    stringCall = irRoot.getBuiltinFunction("g_stringEQ");
                else cmpOp = eq;
                break;
            }
            case NotEqual: {
                if (it.src1().type().sameType(gScope.getStringType()))
                    stringCall = irRoot.getBuiltinFunction("g_stringNE");
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
                    it.setOperand(new Register(Root.i32T, "binary_" + binaryOp.toString()));
                    inst = new Binary(src1, src2, (Register)it.operand(), binaryOp, currentBlock);
                } else {
                    it.setOperand(new Register(Root.stringT, "binary_string_plus"));
                    ArrayList<Operand> params = new ArrayList<>();

                    src1 = resolveStringPointer(currentBlock, it.src1().operand());
                    src2 = resolveStringPointer(currentBlock, it.src2().operand());

                    params.add(src1);
                    params.add(src2);
                    inst = new Call(stringCall, params, (Register)it.operand(), currentBlock);
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
                    inst = new Cmp(src1, src2, (Register)it.operand(), cmpOp, currentBlock);
                } else {
                    it.setOperand(new Register(new BoolType(), "cmp_string_" + it.opCode().toString()));

                    ArrayList<Operand> params = new ArrayList<>();

                    src1 = resolveStringPointer(currentBlock, it.src1().operand());
                    src2 = resolveStringPointer(currentBlock, it.src2().operand());

                    params.add(src1);
                    params.add(src2);

                    inst = new Call(stringCall, params, (Register)it.operand(), currentBlock);
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
                    logicalPhi phiInfo = new logicalPhi();
                    logicalPhiMap.put(destBlock, phiInfo);

                    it.setOperand(new Register(new BoolType(), "logicalAnd"));
                    //to consider: add a pattern named "jump anyway block" for src2?
                    it.src1().setThenBlock(condBlock);
                    it.src1().setElseBlock(destBlock);
                    it.src1().accept(this);

                    currentBlock = condBlock;
                    it.src2().accept(this);
                    opr = resolvePointer(currentBlock, it.src2().operand());
                    currentBlock.addTerminator(new Jump(destBlock, currentBlock));
                    phiInfo.values.add(opr);
                    phiInfo.blocks.add(currentBlock);

                    currentBlock = destBlock;
                    destBlock.addPhi(new Phi((Register)it.operand(), new ArrayList<>(phiInfo.blocks),
                            new ArrayList<>(phiInfo.values), destBlock));
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

                    logicalPhi phiInfo = new logicalPhi();
                    logicalPhiMap.put(destBlock, phiInfo);

                    it.setOperand(new Register(new BoolType(), "logicalOr"));

                    it.src1().setThenBlock(destBlock);
                    it.src1().setElseBlock(condBlock);
                    it.src1().accept(this);
                    phiInfo.values.add(new ConstBool(true));
                    phiInfo.blocks.add(currentBlock);

                    currentBlock = condBlock;
                    it.src2().accept(this);
                    opr = resolvePointer(currentBlock, it.src2().operand());
                    currentBlock.addTerminator(new Jump(destBlock, currentBlock));
                    phiInfo.values.add(opr);
                    phiInfo.blocks.add(currentBlock);

                    currentBlock = destBlock;
                    destBlock.addPhi(new Phi((Register)it.operand(), new ArrayList<>(phiInfo.blocks),
                            new ArrayList<>(phiInfo.values), destBlock));
                }
                break;
            }
            case Equal:
            case NotEqual: {
                it.src1().accept(this);
                it.src2().accept(this);
                it.setOperand(new Register(new BoolType(),  it.opCode().toString()));
                if (cmpOp != null) {
                    src1 = intTrans(resolvePointer(currentBlock, it.src1().operand()));
                    src2 = intTrans(resolvePointer(currentBlock, it.src2().operand()));
                    currentBlock.addInst(new Cmp(src1, src2, (Register)it.operand(), cmpOp, currentBlock));
                }
                else {
                    ArrayList<Operand> params = new ArrayList<>();

                    src1 = resolveStringPointer(currentBlock, it.src1().operand());
                    src2 = resolveStringPointer(currentBlock, it.src2().operand());

                    params.add(src1);
                    params.add(src2);
                    currentBlock.addInst(new Call(stringCall, params, (Register)it.operand(), currentBlock));
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
        IRBaseType type;
        if (it.opCode().ordinal() < 5) type = Root.i32T;
        else type = Root.boolT;
        it.setOperand(new Register(type, "prefix_" + it.opCode().toString()));

        if (type == Root.boolT) {
            if (it.thenBlock() != null) {
                it.src().setThenBlock(it.elseBlock());
                it.src().setElseBlock(it.thenBlock());
                it.src().accept(this);
                return;
            }
        }
        it.src().accept(this);
        Operand src = resolvePointer(currentBlock, it.src().operand());
        switch (it.opCode()) {
            case Positive: {
                it.setOperand(it.src().operand());
                break;
            }
            case Negative: {
                currentBlock.addInst(new Binary(new ConstInt(0, 32), src,
                                                (Register)it.operand(), sub, currentBlock));
                break;
            }
            case Tilde: {
                currentBlock.addInst(new Binary(src, new ConstInt(-1, 32),
                                                (Register)it.operand(), xor, currentBlock));
                break;
            }
            case Increment: {
                currentBlock.addInst(new Binary(src, new ConstInt(1, 32),
                                                (Register)it.operand(), add, currentBlock));
                if (it.src().isAssignable())
                    currentBlock.addInst(new Store(it.src().operand(), it.operand(), currentBlock));
                break;
            }
            case Decrement: {
                currentBlock.addInst(new Binary(src, new ConstInt(1, 32),
                                                (Register)it.operand(), sub, currentBlock));
                if (it.src().isAssignable())
                    currentBlock.addInst(new Store(it.src().operand(), it.operand(), currentBlock));
                break;
            }
            case Not: {
                currentBlock.addInst(new Binary(src, new ConstBool(true),
                                                (Register)it.operand(), xor, currentBlock));
                break;
            }
        }
        branchAdd(it);
    }

    @Override
    public void visit(suffixExpr it) {
        it.src().accept(this);
        ConstInt one = new ConstInt(1, 32);
        Binary inst;
        Register tmp = new Register(Root.i32T, "suffix_tmp");
        it.setOperand(resolvePointer(currentBlock, it.src().operand()));

        if (it.opCode() == 0) inst = new Binary(it.operand(), one, tmp, add, currentBlock);
        else inst = new Binary(it.operand(), one, tmp, sub, currentBlock);
        currentBlock.addInst(inst);
        currentBlock.addInst(new Store(it.src().operand(), tmp, currentBlock));

    }

    @Override
    public void visit(thisExpr it) {
        it.setOperand(currentFunction.getClassPtr());
    }

    @Override
    public void visit(funCallExpr it) {
        it.callee().accept(this);
        funcDecl calleeFunc = (funcDecl)it.callee().type();
        if (calleeFunc.FuncName.equals("size") && !calleeFunc.isMethod()) {
            it.setOperand(new Register(Root.i32T, "array_size"));
            Operand opr = resolvePointer(currentBlock, it.callee().operand());
            assert opr.type() instanceof Pointer;
            Register metaPtr = new Register(new Pointer(Root.i32T, false), "metadataPtr");
            Operand BCPtr = new Register(new Pointer(Root.i32T, false), "bitCastPtr");
            if (((Pointer)opr.type()).pointTo() == Root.i32T && ((Pointer)opr.type()).pointTo().size() == 32)
                BCPtr = opr;
            else currentBlock.addInst(new BitCast(opr, (Register)BCPtr, currentBlock));
            currentBlock.addInst(new GetElementPtr(Root.i32T, BCPtr, new ConstInt(-1, 32),
                    null, metaPtr, currentBlock));
            currentBlock.addInst(new Load((Register)it.operand(), metaPtr, currentBlock));
        } else {
            if (!it.type().isVoid())
                it.setOperand(new Register(irRoot.getIRType(calleeFunc.returnType(), false),
                        "funCallRet"));
            else it.setOperand(null);
            ArrayList<Operand> params = new ArrayList<>();
            if (calleeFunc.isMethod())
                params.add(resolvePointer(currentBlock, it.callee().operand())); //let "this" be the first
            it.params().forEach(param -> {
                param.accept(this);
                params.add(resolvePointer(currentBlock, param.operand()));
            });
            currentBlock.addInst(new Call(calleeFunc.function(), params, (Register) it.operand(), currentBlock));
            if (!(irRoot.isBuiltIn(calleeFunc.function().name())))
                currentFunction.addCalleeFunction(calleeFunc.function());
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
        Operand classPtr = resolvePointer(currentBlock, it.caller().operand());
        it.setOperand(new Register(it.entity().asOperand().type(), "this." + it.member()));
        //the entity.operand() is always a resolvable pointer, pointing to this+offset
        //entity.reg is an abstract one, so only use its type to create a new reg
        currentBlock.addInst(new GetElementPtr(((Pointer)classPtr.type()).pointTo(), classPtr,
                            new ConstInt(0, 32), it.entity().elementIndex(),
                            (Register)it.operand(), currentBlock));
        branchAdd(it);
    }

    @Override
    public void visit(newExpr it) {
        if (it.type() instanceof arrayType) {
            it.setOperand(new Register(irRoot.getIRType(it.type(), true), "new_result"));
            arrayMalloc(0, it, (Register)it.operand());
        } else {    //the result is a pointer to a class
            Register mallocTmp = new Register(Root.stringT, "mallocTmp");
            it.setOperand(new Register(irRoot.getIRType(it.type(), false),
                                        "new_class_ptr"));
            currentBlock.addInst(new Malloc(
                    new ConstInt(((classType)it.type()).allocSize() / 8, 32),
                    mallocTmp, currentBlock));
            currentBlock.addInst(new BitCast(mallocTmp, (Register)it.operand(), currentBlock));
            if (((classType)it.type()).scope().constructor() != null) {
                ArrayList<Operand> params = new ArrayList<>();
                params.add(it.operand());
                currentBlock.addInst(new Call(
                        ((classType)it.type()).scope().constructor().function(),
                        params, null, currentBlock));
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
            Param classPtr = currentFunction.getClassPtr();  //this one does not need to load
            it.setOperand(new Register(it.entity().asOperand().type(),
                        "this." + it.name() + "_addr"));
            currentBlock.addInst(new GetElementPtr(((Pointer)classPtr.type()).pointTo(), classPtr,
                                    new ConstInt(0, 32), it.entity().elementIndex(),
                                    (Register)it.operand(), currentBlock));
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

    private String getSString(String it) {
        String ret = it.replace("\\n", "\n");
        ret = ret.replace("\\t", "\t");
        ret = ret.replace("\\\"", "\"");
        ret = ret.replace("\\\\", "\\");
        ret += "\0";
        return ret;
    }
    @Override
    public void visit(stringLiteral it) {
        String realValue = getSString(it.value().substring(1, it.value().length() - 1));
        ConstString str = irRoot.getStringByValue(realValue);
        String name;
        if (str == null) {
            name = currentFunction.name() +"." + symbolCtr++;
            irRoot.addConstString(name, realValue);
            str = irRoot.getConstString(name);
        } else name = str.name;
        it.setOperand(new Register(Root.stringT, "resolved_" + name));
        currentBlock.addInst(new GetElementPtr(new ArrayType(realValue.length(), Root.charT),
                str, new ConstInt(0, 32), new ConstInt(0, 32),
                (Register)it.operand(), currentBlock));
    }

    private void arrayMalloc(int nowDim, newExpr it, Register result) {
        Register arrayPtr;
        if (nowDim == 0) arrayPtr = result;
        else arrayPtr = new Register(((Pointer) result.type()).pointTo(), "mallocArrayPtr");
        if (nowDim == it.exprs().size()) return;
        it.exprs().get(nowDim).accept(this);
        IRBaseType pointTo = ((Pointer)arrayPtr.type()).pointTo();

        Operand currentNum = resolvePointer(currentBlock, it.exprs().get(nowDim).operand());
        ConstInt typeWidth = new ConstInt(((Pointer)arrayPtr.type()).pointTo().size() / 8, 32);
        Register PureWidth = new Register(Root.i32T, "pureWidth"),
                 metaWidth = new Register(Root.i32T, "metaWidth");
        Register allocPtr = new Register(Root.stringT, "allocPtr"),
                 allocBitCast = new Register(new Pointer(Root.i32T, false), "allocBitCast"),
                 allocOffset = new Register(new Pointer(Root.i32T, false), "offsetHead");

        currentBlock.addInst(new Binary(currentNum, typeWidth, PureWidth, mul, currentBlock));
        currentBlock.addInst(new Binary(PureWidth, new ConstInt(4, 32), metaWidth, add, currentBlock));
        currentBlock.addInst(new Malloc(metaWidth, allocPtr, currentBlock));
        currentBlock.addInst(new BitCast(allocPtr, allocBitCast, currentBlock));
        currentBlock.addInst(new Store(allocBitCast, currentNum, currentBlock));
        if (pointTo instanceof IntType && pointTo.size() == 32) {
            currentBlock.addInst(new GetElementPtr(Root.i32T, allocBitCast, new ConstInt(1, 32),
                    null, arrayPtr, currentBlock));
        } else {
            currentBlock.addInst(new GetElementPtr(Root.i32T, allocBitCast, new ConstInt(1, 32),
                    null, allocOffset, currentBlock));
            currentBlock.addInst(new BitCast(allocOffset, arrayPtr, currentBlock));
        }
        if (nowDim != 0) currentBlock.addInst(new Store(result, arrayPtr, currentBlock));
        if (nowDim < it.exprs().size() - 1){
            IRBlock incrBlock = new IRBlock("ArrayIncrBlock"),
                    bodyBlock = new IRBlock("ArrayBodyBlock"),
                    destBlock = new IRBlock("ArrayDestBlock");
            assert pointTo instanceof Pointer;
            Register ptr = new Register(new Pointer(Root.i32T, false), "pointer"),
                     castedPtr = new Register(arrayPtr.type(), "castedPointer"),
                     counter = new Register(Root.i32T, "counter"),
                     counterTmp = new Register(Root.i32T, "counterTmp"),
                     branchJudge = new Register(new BoolType(), "branchJudge");
            ArrayList<Operand> values = new ArrayList<>();
            ArrayList<IRBlock> blocks = new ArrayList<>();
            //here use the ptr as i32* is correct, so use allocBitCast to get the ptr is ok
            //this is especially efficient since I can use 1-base counter to get 0-base ptr
            values.add(new ConstInt(0, 32));
            blocks.add(currentBlock);
            currentBlock.addTerminator(new Jump(incrBlock, currentBlock));

            currentBlock = incrBlock;
            currentBlock.addInst(new Binary(counter, new ConstInt(1, 32),
                    counterTmp, add, currentBlock));//counter++
            currentBlock.addInst(new Cmp(counter, currentNum, branchJudge, sle, currentBlock));
            currentBlock.addTerminator(new Branch(branchJudge, bodyBlock, destBlock, currentBlock));

            currentBlock = bodyBlock;
            currentBlock.addInst(new GetElementPtr(Root.i32T, allocBitCast,
                    counter, null, ptr, currentBlock));
            currentBlock.addInst(new BitCast(ptr, castedPtr, currentBlock));

            arrayMalloc(nowDim + 1, it, castedPtr);

            currentBlock.addTerminator(new Jump(incrBlock, currentBlock));
            values.add(counterTmp);
            blocks.add(currentBlock);

            currentBlock = incrBlock;
            currentBlock.addPhi(new Phi(counter, blocks, values, currentBlock));

            currentBlock = destBlock;
        }
    }
}
