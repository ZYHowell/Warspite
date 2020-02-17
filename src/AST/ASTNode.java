package AST;

import java.util.HashSet;
import Util.position;
import Util.scope.Scope;
import Util.symbol.varEntity;

abstract public class ASTNode {
    private position pos;
    private Scope scope;
    private HashSet<varEntity> dirty, use;

    public ASTNode(position pos) {
        this.pos = pos;
        this.dirty = new HashSet<>();
        this.use = new HashSet<>();
    }

    public position pos() {
        return pos;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Scope scope() {
        return scope;
    }

    public HashSet<varEntity> dirty() {
        return dirty;
    }
    public HashSet<varEntity> use() {
        return use;
    }

    abstract public void accept(ASTVisitor visitor);
}
