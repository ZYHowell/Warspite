package Util.scope;

import java.util.ArrayList;
import Util.position;
import Util.symbol.varEntity;

public class functionScope extends Scope {

    private ArrayList<varEntity> params;

    public functionScope(Scope parentScope) {
        super(parentScope);
        params = new ArrayList<>();
    }

    public void addParam(varEntity param, position pos) {
        params.add(param);
        defineMember(param.name(), param, pos);
    }

    public ArrayList<varEntity> params() {
        return params;
    }

    public int paramNum() {
        return params.size();
    }
}
