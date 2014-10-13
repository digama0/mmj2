package mmj.oth;

import java.util.*;

public class AbsTerm extends Term {
    Var x;
    Term b;

    public AbsTerm(final Var x, final Term b) {
        this.x = x;
        this.b = b;
    }

    @Override
    public String toString() {
        return "\\" + x + ". " + b;
    }

    @Override
    public boolean strictEquals(final Term obj) {
        return obj instanceof AbsTerm && x.equals(((AbsTerm)obj).x)
            && b.strictEquals(((AbsTerm)obj).b);
    }

    @Override
    public Set<Var> freeVars() {
        final HashSet<Var> s = new HashSet<Var>(b.freeVars());
        s.remove(x);
        return s;
    }

    @Override
    public Set<VarType> getSubTypeVars() {
        final HashSet<VarType> s = new HashSet<VarType>(b.getSubTypeVars());
        s.addAll(x.t.getTypeVars());
        return s;
    }

    @Override
    public Type getType() {
        return OpType.to(x.t, b.getType());
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Term subst(final List<List<List<Object>>> subst) {
        final Var x2 = new Var(x.t.subst(subst), x.n);
        for (final List<Object> pr : subst.get(1))
            if (pr.get(0).equals(x2)
                || ((Term)pr.get(1)).freeVars().contains(x2))
            {
                final Var dummy = new Var(x2.t, Name.dummy());
                final List subst2 = Arrays.asList(Collections.EMPTY_LIST,
                    Arrays.asList(Arrays.asList(x2, new VarTerm(dummy))));
                return new AbsTerm(dummy, b.subst(subst2).subst(subst));
            }
        return new AbsTerm(x2, b.subst(subst));
    }

    @Override
    Term substPlain(final Map<Var, Var> subst) {
        return new AbsTerm(x.subst(subst), b.substPlain(subst));
    }

    @Override
    public boolean alphaEquiv(final Term t, final Map<Var, Var> subst) {
        if (!(t instanceof AbsTerm))
            return false;
        final Var old = subst.get(x);
        subst.put(x, ((AbsTerm)t).x);
        final boolean ae = b.alphaEquiv(((AbsTerm)t).b, subst);
        if (old == null)
            subst.remove(x);
        else
            subst.put(x, old);
        return ae;
    }

}