package mmj.oth;

import java.util.*;

import mmj.lang.ParseNode;

public class AbsTerm extends Term {
    private Var x;
    private Term b;

    private AbsTerm() {}
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static AbsTerm get(final Var x, final Term b) {
        final AbsTerm t = new AbsTerm();
        if (Var.getFromName(b.boundVars(), x.n) != null) {
            t.x = Var.get(x.t, Name.dummy());
            t.b = b.subst((List)Arrays.asList(Collections.EMPTY_LIST,
                Arrays.asList(Arrays.asList(x, VarTerm.get(t.x)))));
        }
        else {
            t.x = x;
            t.b = b;
        }
        return Term.pool.intern(t).asAbs();
    }

    @Override
    public String toString() {
        return "\\" + x + ". " + b;
    }

    public Var getVar() {
        return x;
    }
    public Term getB() {
        return b;
    }

    @Override
    public boolean strictEquals(final Term obj) {
        return obj instanceof AbsTerm && x.equals(obj.asAbs().x)
            && b.strictEquals(obj.asAbs().b);
    }

    @Override
    public Set<Var> freeVars() {
        final HashSet<Var> s = new HashSet<Var>(b.freeVars());
        s.remove(x);
        return s;
    }

    @Override
    public Set<Var> boundVars() {
        final HashSet<Var> s = new HashSet<Var>(b.boundVars());
        s.add(x);
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
        final Var x2 = Var.get(x.t.subst(subst), x.n);
        for (final List<Object> pr : subst.get(1))
            if (((Var)pr.get(0)).n.equals(x2.n)
                || ((Term)pr.get(1)).allVarsContains(x2.n))
            {
                final Var dummy = Var.get(x2.t, Name.dummy());
                final List subst2 = Arrays.asList(Collections.EMPTY_LIST,
                    Arrays.asList(Arrays.asList(x2, VarTerm.get(dummy))));
                return AbsTerm.get(dummy, b.subst(subst2).subst(subst));
            }
        return AbsTerm.get(x2, b.subst(subst));
    }

    @Override
    public Term substPlain(final Map<Var, Var> subst) {
        return get(x.subst(subst), b.substPlain(subst));
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

    @Override
    protected void generateTypeProof(final Interpreter i) {
        final ParseNode xp = x.t.getTypeProof(i);
        final ParseNode v = i.c(x.toVarHyp(i));
        final ParseNode bp = b.getTermProof(i);
        termProof = i.c(OTConstants.HOL_ABS_TERM, xp, v, bp);
        typeProof = i.c(OTConstants.HOL_ABS_TYPE, xp,
            b.getType().getTypeProof(i), v, bp, b.getTypeProof(i));
    }

}
