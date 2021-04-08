package Util.symbol;

import Util.error.internalError;
import Util.position;
public class constructorType extends BaseType {

    public constructorType() {
        super("classConstruct");
    }

    @Override
    public int size() {
        throw new internalError("call the size of a constructor return value",
                new position(0, 0));
    }
    @Override
    public String name(){
        throw new internalError("try to get name of the classDefType", new position(0,0));
    }
    @Override
    public TypeCategory typeCategory(){ return TypeCategory.CONSTRUCTOR; }
    @Override
    public boolean sameType(Type it) { return it.isConstructor(); }
}
