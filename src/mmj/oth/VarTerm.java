package mmj.oth;

import java.util.*;

import mmj.lang.ParseNode;

public class VarTerm extends Term {
    private Var v;

    private VarTerm() {}
    public static VarTerm get(final Var v) {
        final VarTerm t = new VarTerm();
        t.v = v;
        return Term.pool.intern(t).asVar();
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
    public Set<Var> boundVars() {
        return Collections.emptySet();
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
        final Var v2 = Var.get(v.t.subst(subst), v.n);
        for (final List<Object> pr : subst.get(1))
            if (v2.equals(pr.get(0)))
                return (Term)pr.get(1);
        return VarTerm.get(v2);
    }

    @Override
    public boolean alphaEquiv(final Term t, final Map<Var, Var> subst) {
        final Var v2 = subst.get(v);
        return t instanceof VarTerm
            && (v2 == null ? v : v2).equals(((VarTerm)t).v);
    }

    @Override
    public Term substPlain(final Map<Var, Var> subst) {
        return VarTerm.get(v.subst(subst));
    }
    @Override
    protected void generateTypeProof(final Interpreter i) {
        final ParseNode tp = v.t.getTypeProof(i);
        final ParseNode vp = i.c(v.toVarHyp(i));
        termProof = i.c(OTConstants.HOL_VAR_TERM, tp, vp);
        typeProof = i.c(OTConstants.HOL_VAR_TYPE, tp, vp);
    }
}
