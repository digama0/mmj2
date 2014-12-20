package mmj.oth;

import java.util.*;

import mmj.lang.Cnst;
import mmj.lang.Stmt;

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
    public void generateTypeProof(final Interpreter i) {
        final Cnst cnst = i.getConstant(OTConstants.mapConstants(c.n));
        if (cnst != null) {
            termProof = i.c(i.getTermAxiom(cnst));
            final Stmt ty = i.getTypeAxiom(cnst);
            typeProof = ty.getMandHypArrayLength() == 0 ? i.c(ty) : i.c(ty, ty
                .getExprParseTree().getRoot().child[0].unifyWithSubtree(
                t.getTypeProof(i), ty.getMandVarHypArray(), i.unifyNodeStack,
                i.compareNodeStack));
        }
        else
            throw new UnsupportedOperationException();
    }
}
