package Util.scope;

//this is only defined to identify whether a varDef is directly in classScope(a member)
public class classScope extends Scope {

    public classScope(Scope parentScope) {
        super(parentScope);
    }
}
