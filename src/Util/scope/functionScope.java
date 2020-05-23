package Util.scope;

import Util.position;
import Util.symbol.varEntity;

import java.util.ArrayList;

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

}
