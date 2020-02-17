package Util.symbol;

import AST.ASTNode;
import Util.error.*;
import Util.position;
import Util.scope.functionScope;

public class funcDecl extends BaseType {

    private String FuncName;
    private Type type;
    private ASTNode block;
    private functionScope localScope;
    private boolean hasSideEffect;

    public funcDecl(String name, ASTNode block) {
        super("funcDecl" + name);
        this.FuncName = name;
        this.block = block;
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

    @Override
    public int dim() {
        throw new internalError("call dim of funcType", block.pos());
    }

    @Override
    public TypeCategory typeCategory() {
        return TypeCategory.FUNC;
    }

    @Override
    public BaseType baseType() {
        throw new internalError("call baseType of funcType", block.pos());
    }

    @Override
    public boolean sameType(Type it) {
        throw new internalError("call sameType of funcType", block.pos());
    }
}
