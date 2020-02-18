package Util.symbol;

import AST.ASTNode;
import AST.funDef;
import MIR.Function;
import Util.error.*;
import Util.position;
import Util.scope.functionScope;
import MIR.Function;

public class funcDecl extends BaseType {

    private String FuncName;
    private Type type;
    private funDef defNode;
    private functionScope localScope;
    private boolean hasSideEffect;
    private Function function;

    public funcDecl(String name, funDef defNode) {
        super("funcDecl" + name);
        this.FuncName = name;
        this.defNode = defNode;
        this.hasSideEffect = false;
    }

    public void setScope(functionScope localScope) {
        this.localScope = localScope;
    }
    public functionScope scope() {
        return localScope;
    }

    public void addParam(varEntity param, position pos) {
        localScope.addParam(param, pos);
    }

    public void setRetType(Type t) {
        this.type = t;
    }
    public Type returnType() {
        return type;
    }

    public void setSideEffect(boolean hSE) {
        hasSideEffect = hSE;
    }
    public boolean hasSideEffect() {
        return hasSideEffect;
    }

    public void setFunction (Function func) {
        this.function = func;
    }
    public Function function() {
        return function;
    }

    @Override
    public int dim() {
        throw new internalError("call dim of funcType", defNode.pos());
    }

    @Override
    public TypeCategory typeCategory() {
        return TypeCategory.FUNC;
    }

    @Override
    public BaseType baseType() {
        throw new internalError("call baseType of funcType", defNode.pos());
    }

    @Override
    public boolean sameType(Type it) {
        throw new internalError("call sameType of funcType", defNode.pos());
    }
}
