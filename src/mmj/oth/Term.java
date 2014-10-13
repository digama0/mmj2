package mmj.oth;

import java.util.*;

public abstract class Term {
    abstract Set<Var> freeVars();
    abstract Type getType();
    abstract Set<VarType> getSubTypeVars();
    abstract Term substPlain(Map<Var, Var> subst);
    abstract Term subst(List<List<List<Object>>> subst);
    abstract boolean alphaEquiv(Term t, Map<Var, Var> subst);
    abstract boolean strictEquals(Term t);
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Term
            && alphaEquiv((Term)obj, new HashMap<Var, Var>());
    }
}