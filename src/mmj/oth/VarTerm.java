package mmj.oth;

import java.util.*;

public class VarTerm extends Term {
    Var v;

    public VarTerm(final Var v) {
        this.v = v;
    }

    @Override
    public String toString() {
        return v.toString();
    }

    @Override
    public boolean strictEquals(final Term obj) {
        return obj instanceof VarTerm && v.equals(((VarTerm)obj).v);
    }

    @Override
    public Set<Var> freeVars() {
        final Set<Var> s = new HashSet<Var>();
        s.add(v);
        return s;
    }

    @Override
    public Type getType() {
        return v.t;
    }

    @Override
    public Set<VarType> getSubTypeVars() {
        return v.t.getTypeVars();
    }

    @Override
    public Term subst(final List<List<List<Object>>> subst) {
        final Var v2 = new Var(v.t.subst(subst), v.n);
        for (final List<Object> pr : subst.get(1))
            if (v2.equals(pr.get(0)))
                return (Term)pr.get(1);
        return new VarTerm(v2);
    }

    @Override
    public boolean alphaEquiv(final Term t, final Map<Var, Var> subst) {
        final Var v2 = subst.get(v);
        return t instanceof VarTerm
            && (v2 == null ? v : v2).equals(((VarTerm)t).v);
    }

    @Override
    Term substPlain(final Map<Var, Var> subst) {
        return new VarTerm(v.subst(subst));
    }
}