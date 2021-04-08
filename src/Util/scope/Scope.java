package Util.scope;

import Util.error.semanticError;
import Util.position;
import Util.symbol.Type;
import Util.symbol.funcDecl;
import Util.symbol.varEntity;

import java.util.HashMap;

public class Scope {

    private HashMap<String, funcDecl> methods;
    private HashMap<String, varEntity> members;
    private funcDecl constructor;
    private Scope parentScope;


    public Scope(Scope parentScope) {
        methods = new HashMap<>();
        members = new HashMap<>();
        constructor = null;
        this.parentScope = parentScope;
    }

    public Scope parentScope() {
        return parentScope;
    }

    public void defineMember(String name, varEntity var, position pos) {
        if (members.containsKey(name))
            throw new semanticError("member redefine", pos);
        members.put(name, var);
    }
    public void defineMethod(String name, funcDecl func, position pos) {
        if (methods.containsKey(name))
            throw new semanticError("method redefine", pos);
        methods.put(name, func);
    }
    public void defineConstructor(funcDecl func, position pos) {
        if (constructor != null)
            throw new semanticError("constructor redefine", pos);
        constructor = func;
    }

    public boolean containsMember(String name, boolean lookUpon) {
        if (members.containsKey(name)) return true;
        else if (parentScope != null && lookUpon) return parentScope.containsMember(name, true);
        else return false;
    }
    public boolean containsMethod(String name, boolean lookUpon) {
        if (methods.containsKey(name)) return true;
        else if (parentScope != null && lookUpon) return parentScope.containsMethod(name, true);
        else return false;
    }

    public funcDecl constructor() {
        return constructor;
    }

    public Type getMemberType(String name, position pos, boolean lookUpon) {
        if (members.containsKey(name)) return members.get(name).type();
        else if (parentScope != null && lookUpon) return parentScope.getMemberType(name, pos, true);
        else throw new semanticError("undefined variable", pos);
    }
    public varEntity getMember(String name, position pos, boolean lookUpon) {
        if (members.containsKey(name)) return members.get(name);
        else if (parentScope != null && lookUpon) return parentScope.getMember(name, pos, true);
        else throw new semanticError("undefined variable", pos);
    }

    public funcDecl getMethod(String name, position pos, boolean lookUpon) {
        if (methods.containsKey(name)) return methods.get(name);
        else if (parentScope != null && lookUpon) return parentScope.getMethod(name, pos, true);
        else throw new semanticError("undefined method", pos);
    }
}
