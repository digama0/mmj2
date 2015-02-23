package mmj.oth;

import java.util.*;

import mmj.lang.*;

public class ConstTerm extends Term {
    private Const c;
    private Type t;

    private ConstTerm() {}

    public static ConstTerm get(final Type ty, final Const c) {
        final ConstTerm t = new ConstTerm();
        t.c = c;
        t.t = ty;
        return Term.pool.intern(t).asConst();
    }

    public Const getConst() {
        return c;
    }

    public static Term eq(final Type t) {
        return ConstTerm.get(OpType.to(t, OpType.to(t, ArtReader.BOOL)),
            ArtReader.EQUALS);
    }

    public static Term eq(final Term a, final Term b) {
        return AppTerm.get(AppTerm.get(eq(a.getType()), a), b);
    }

    @Override
    public String toString() {
        return c.toString();
    }

    @Override
    public boolean strictEquals(final Term obj) {
        return obj instanceof ConstTerm && c.equals(((ConstTerm)obj).c)
            && t.equals(((ConstTerm)obj).t);
    }

    @Override
    public Set<Var> freeVars() {
        return Collections.emptySet();
    }

    @Override
    public Set<Var> boundVars() {
        return Collections.emptySet();
    }

    @Override
    public Set<VarType> getSubTypeVars() {
        return t.getTypeVars();
    }

    @Override
    public Type getType() {
        return t;
    }

    @Override
    public Term subst(final List<List<List<Object>>> subst) {
        return ConstTerm.get(t.subst(subst), c);
    }

    @Override
    public boolean alphaEquiv(final Term t, final Map<Var, Var> subst) {
        return strictEquals(t);
    }

    @Override
    public Term substPlain(final Map<Var, Var> subst) {
        return this;
    }

    @Override
    public ParseNode getTermProof(final ProofContext p) {
        final Cnst cnst = p.i.getConstant(OTConstants.mapConstants(c.n));
        if (cnst != null)
            return p.c(p.i.getTermAxiom(cnst));
        else
            throw new UnsupportedOperationException();
    }

    @Override
    public ParseNode getTypeProof(final ProofContext p, final ParseNode wff) {
        final Cnst cnst = p.i.getConstant(OTConstants.mapConstants(c.n));
        if (cnst != null) {
            final Stmt ty = p.i.getTypeAxiom(cnst);
            return p.c(
                ty,
                ty.getExprParseTree()
                    .getRoot()
                    .unifyWithSubtree(wff, ty.getMandVarHypArray(),
                        p.i.unifyNodeStack, p.i.compareNodeStack));
        }
        else
            throw new UnsupportedOperationException();
    }
}
