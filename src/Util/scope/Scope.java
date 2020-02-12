package Util.scope;

import java.util.HashMap;

import Util.symbol.funcDecl;
import Util.symbol.varEntity;
import Util.position;
import Util.error.semanticError;
import Util.symbol.Type;

public class Scope {

    /*
     * todo: add support of cross-scope reference!!!!!!!!!!
     */
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

    /*
     * todo: rewrite the containsMember and getMemberType in order to realize cross-scope reference
     */
    public boolean containsMember(String name) {
        return members.containsKey(name);
    }
    public boolean containsMethod(String name) { return methods.containsKey(name); }
    public funcDecl constructor() {
        return constructor;
    }


    public Type getMemberType(String name, position pos) {
        if (members.containsKey(name))
            return members.get(name).type();
        throw new semanticError("undefined variable", pos);
    }

    public funcDecl getMethod(String name, position pos) {
        if (methods.containsKey(name))
            return methods.get(name);
        throw new semanticError("undefined method", pos);
    }
}
