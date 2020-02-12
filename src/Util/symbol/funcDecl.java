package Util.symbol;

import java.util.ArrayList;
import AST.ASTNode;
import Util.error.*;
import Util.position;
import Util.scope.Scope;

//todo: add parameters
public class funcDecl extends BaseType {

    private String FuncName;
    private Type type;
    private ASTNode block;
    private ArrayList<Type> paramsType;
    private Scope localScope;

    public funcDecl(String name, ASTNode block) {
        super("funcDecl" + name);
        this.FuncName = name;
        this.block = block;
    }

    public void setRetType(Type t) {
        this.type = t;
    }

    public Type returnType() {
        return type;
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
