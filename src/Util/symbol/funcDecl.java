package Util.symbol;

import AST.funDef;
import MIR.Function;
import Util.error.internalError;
import Util.position;
import Util.scope.functionScope;

public class funcDecl extends BaseType {

    public String FuncName;
    private Type type;
    private funDef defNode;
    private functionScope localScope;
    private boolean isMethod;
    private Function function;

    public funcDecl(String name, funDef defNode) {
        super("funcDecl" + name);
        this.FuncName = name;
        this.defNode = defNode;
        this.isMethod = false;
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

    public void setFunction (Function func) {
        this.function = func;
    }
    public Function function() {
        return function;
    }

    public void setIsMethod() {
        isMethod = true;
    }
    public boolean isMethod() {
        return isMethod;
    }

    @Override
    public int size() {
        throw new internalError("call for size of function " + FuncName, new position(0, 0));
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
