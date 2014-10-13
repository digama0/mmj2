package mmj.oth;

import java.util.*;

public class AppTerm extends Term {
    Term f;
    Term x;

    public AppTerm(final Term f, final Term x) {
        this.f = f;
        this.x = x;
    }

    @Override
    public String toString() {
        if (f instanceof AppTerm)
            return "(" + ((AppTerm)f).x + " " + ((AppTerm)f).f + " " + x
                + ")";
        return "(" + f + " " + x + ")";
    }

    @Override
    public boolean strictEquals(final Term obj) {
        return obj instanceof AppTerm && f.strictEquals(((AppTerm)obj).f)
            && x.strictEquals(((AppTerm)obj).x);
    }

    @Override
    public Set<Var> freeVars() {
        final HashSet<Var> s = new HashSet<Var>(f.freeVars());
        s.addAll(x.freeVars());
        return s;
    }

    @Override
    public Set<VarType> getSubTypeVars() {
        final HashSet<VarType> s = new HashSet<VarType>(f.getSubTypeVars());
        s.addAll(x.getSubTypeVars());
        return s;
    }

    @Override
    public Type getType() {
        return ((OpType)f.getType()).args.get(1);
    }

    @Override
    public Term subst(final List<List<List<Object>>> subst) {
        return new AppTerm(f.subst(subst), x.subst(subst));
    }

    @Override
    Term substPlain(final Map<Var, Var> subst) {
        return new AppTerm(f.substPlain(subst), x.substPlain(subst));
    }

    @Override
    public boolean alphaEquiv(final Term t, final Map<Var, Var> subst) {
        return t instanceof AppTerm && f.alphaEquiv(((AppTerm)t).f, subst)
            && x.alphaEquiv(((AppTerm)t).x, subst);
    }
}