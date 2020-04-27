package Util.symbol;

import Util.error.internalError;
import Util.position;

//int, bool, void, null
public class primitiveType extends BaseType {
    private TypeCategory typeCategory;

    public primitiveType(String name, TypeCategory it) {
        super(name);
        this.typeCategory = it;
    }

    @Override
    public int size() {
        if (isInt()) return 32;
        else if (isBool()) return 8;
        else throw new internalError("call for size of void/null", new position(0, 0));
    }
    @Override
    public TypeCategory typeCategory(){
        return typeCategory;
    }
    @Override
    public boolean sameType(Type it) {
        return (isNull() && (it.isArray() || it.isClass())) ||
                (typeCategory == it.typeCategory() && it.dim() == 0);
    }
}
