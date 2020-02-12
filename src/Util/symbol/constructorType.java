package Util.symbol;

import Util.error.internalError;
import Util.position;
//this is for constructor
public class constructorType extends BaseType {

    public constructorType() {
        super("classConstruct");
    }

    //this name should never be used.
    @Override
    public String name(){
        throw new internalError("try to get name of the classDefType", new position(0,0));
    }
    @Override
    public TypeCategory typeCategory(){ return TypeCategory.CONSTRUCTOR; }
    @Override
    public boolean sameType(Type it) { return it.isConstructor(); }
}
