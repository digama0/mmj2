package mmj.oth;

import java.util.*;

import mmj.lang.ParseNode;

public class AppTerm extends Term {
    private Term f;
    private Term x;

    private AppTerm() {}
    public static AppTerm get(final Term f, final Term x) {
        final AppTerm t = new AppTerm();
        t.f = f;
        t.x = x;
        return Term.pool.intern(t).asApp();
    }
    public Term getF() {
        return f;
    }
    public Term getX() {
        return x;
    }

    @Override
    public String toString() {
        if (f instanceof AppTerm)
            return "(" + f.asApp().x + " " + f.asApp().f + " " + x + ")";
        return "(" + f + " " + x + ")";
    }

    @Override
    public boolean strictEquals(final Term obj) {
        return obj instanceof AppTerm && f.strictEquals(obj.asApp().f)
            && x.strictEquals(obj.asApp().x);
    }

    @Override
    public Set<Var> freeVars() {
        final HashSet<Var> s = new HashSet<Var>(f.freeVars());
        s.addAll(x.freeVars());
        return s;
    }

    @Override
    public Set<Var> boundVars() {
        final HashSet<Var> s = new HashSet<Var>(f.boundVars());
        s.addAll(x.boundVars());
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
        return f.getType().asOp().getArgs().get(1);
    }

    @Override
    public Term subst(final List<List<List<Object>>> subst) {
        return get(f.subst(subst), x.subst(subst));
    }

    @Override
    public Term substPlain(final Map<Var, Var> subst) {
        return get(f.substPlain(subst), x.substPlain(subst));
    }

    @Override
    public boolean alphaEquiv(final Term t, final Map<Var, Var> subst) {
        return t instanceof AppTerm && f.alphaEquiv(t.asApp().f, subst)
            && x.alphaEquiv(t.asApp().x, subst);
    }

    @Override
    protected ParseNode getTermProof(final ProofContext p) {
        final Term c = f.asApp().f;
        if (c instanceof ConstTerm
            && c.asConst().getConst().n.fullName().equals(OTConstants.EQUALS))
        {
            final Term x1 = f.asApp().x;
            final ParseNode eq = p.c(OTConstants.SET_EQ_WFF,
                x1.getTermProof(p), x.getTermProof(p));
            return p.c(OTConstants.SET_WT_CLS, eq);
        }
        else {
            final ParseNode ft = f.getTermProof(p);
            final ParseNode xt = x.getTermProof(p);
            return p.c(OTConstants.SET_FV_CLS, ft, xt);
        }
    }

    @Override
    protected ParseNode getTypeProof(final ProofContext p, final ParseNode wff)
    {
        final Term c = f.asApp().f;
        if (c instanceof ConstTerm
            && c.asConst().getConst().n.fullName().equals(OTConstants.EQUALS))
            return p.c(OTConstants.SET_BOOL_TYPE, wff, getTermProof(p));
        else {
            final ParseNode ft = f.getTermProof(p);
            final ParseNode xt = x.getTermProof(p);
            termProof = p.c(OTConstants.HOL_APP_TERM, ft, xt);
            final List<Type> l = f.getType().asOp().getArgs();
            typeProof = p.c(OTConstants.SET_FV_TYPE, wff, l.get(0)
                .getTypeProof(p), l.get(1).getTypeProof(p), ft, xt, f
                .getTypeProof(p), x.getTypeProof(p));
        }
    }
}
