package mmj.oth;

import java.util.*;

import mmj.lang.ParseNode;

public class ContextTerm extends Term {
    private Term l;
    private Term r;

    private ContextTerm() {}
    public static ContextTerm get(final Term l, final Term r) {
        final ContextTerm t = new ContextTerm();
        t.l = l;
        t.r = r;
        return (ContextTerm)Term.pool.intern(t);
    }
    public Term getL() {
        return l;
    }
    public Term getR() {
        return r;
    }

    @Override
    public String toString() {
        return "(" + l + ", " + r + ")";
    }

    @Override
    public boolean strictEquals(final Term obj) {
        return obj instanceof ContextTerm
            && l.strictEquals(((ContextTerm)obj).l)
            && r.strictEquals(((ContextTerm)obj).r);
    }

    @Override
    public Set<Var> freeVars() {
        final HashSet<Var> s = new HashSet<Var>(l.freeVars());
        s.addAll(r.freeVars());
        return s;
    }

    @Override
    public Set<Var> boundVars() {
        final HashSet<Var> s = new HashSet<Var>(l.boundVars());
        s.addAll(r.boundVars());
        return s;
    }

    @Override
    public Set<VarType> getSubTypeVars() {
        final HashSet<VarType> s = new HashSet<VarType>(l.getSubTypeVars());
        s.addAll(r.getSubTypeVars());
        return s;
    }

    @Override
    public Type getType() {
        return ArtReader.BOOL;
    }

    @Override
    public Term subst(final List<List<List<Object>>> subst) {
        return get(l.subst(subst), r.subst(subst));
    }

    @Override
    public Term substPlain(final Map<Var, Var> subst) {
        return get(l.substPlain(subst), r.substPlain(subst));
    }

    @Override
    public boolean alphaEquiv(final Term t, final Map<Var, Var> subst) {
        return t instanceof ContextTerm
            && l.alphaEquiv(((ContextTerm)t).l, subst)
            && r.alphaEquiv(((ContextTerm)t).r, subst);
    }

    @Override
    protected void generateTypeProof(final ProofContext p) {
        final ParseNode lt = l.getTermProof(p);
        final ParseNode rt = r.getTermProof(p);
        termProof = p.c(OTConstants.HOL_CT_TERM, lt, rt);
        typeProof = p.c(OTConstants.HOL_CT_TYPE, lt, rt, l.getTypeProof(p),
            r.getTypeProof(p));
    }
}
