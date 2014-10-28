package mmj.oth;

import java.util.*;

import mmj.lang.*;
import mmj.util.InternPool;

public abstract class Term {
    protected static InternPool<Term> pool = new InternPool<Term>();

    public ConstTerm asConst() {
        return (ConstTerm)this;
    }
    public AppTerm asApp() {
        return (AppTerm)this;
    }
    public AbsTerm asAbs() {
        return (AbsTerm)this;
    }
    public VarTerm asVar() {
        return (VarTerm)this;
    }

    public abstract Set<Var> freeVars();
    public abstract Type getType();
    public abstract Set<VarType> getSubTypeVars();
    public abstract Term substPlain(Map<Var, Var> subst);
    public abstract Term subst(List<List<List<Object>>> subst);
    public abstract boolean alphaEquiv(Term t, Map<Var, Var> subst);
    public abstract boolean strictEquals(Term t);
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Term
            && alphaEquiv((Term)obj, new HashMap<Var, Var>());
    }

    protected ParseNode termProof = null;
    protected ParseNode typeProof = null;

    public ParseNode getTermProof(final Interpreter i) {
        if (termProof == null) {
            generateTypeProof(i);
            try {
                termProof.toString();
            } catch (final Exception e) {
                hashCode();
            }
        }
        return termProof;
    }
    public ParseNode getTypeProof(final Interpreter i) {
        if (typeProof == null)
            generateTypeProof(i);
        return typeProof;
    }
    protected abstract void generateTypeProof(final Interpreter i);

    private static Term createFromParseNode(final Interpreter i,
        final ParseNode expr, final ParseNode type)
        throws UnsupportedOperationException
    {
        final String stmt = expr.stmt.getLabel();
        if (stmt.equals(OTConstants.HOL_CT_TERM)) {
            final ParseNode bool = i.c(OTConstants.HOL_BOOL_TYPE);
            return ContextTerm.get(createFromParseNode(i, expr.child[0], bool),
                createFromParseNode(i, expr.child[1], bool));
        }
        if (stmt.equals(OTConstants.HOL_ABS_TERM))
            return AbsTerm.get(
                Var.createFromVarHyp(i, (VarHyp)expr.child[1].stmt,
                    type == null ? null : type.child[0]),
                createFromParseNode(i, expr.child[2], type == null ? null
                    : type.child[1]));
        if (stmt.equals(OTConstants.HOL_OV_TERM))
            return createFromParseNode(i, i.c(OTConstants.HOL_APP_TERM,
                i.c(OTConstants.HOL_APP_TERM, expr.child[0], expr.child[1]),
                expr.child[2]), type);
        if (stmt.equals(OTConstants.HOL_APP_TERM)) {
            final Term t = createFromParseNode(i, expr.child[1], null);
            return AppTerm.get(
                createFromParseNode(i, expr.child[0], i
                    .c(OTConstants.HOL_FUN_TYPE, t.getType().getTypeProof(i),
                        type)), t);
        }
        if (stmt.equals(OTConstants.HOL_VAR_TERM))
            return VarTerm.get(Var.createFromVarHyp(i,
                (VarHyp)expr.child[1].stmt, expr.child[0]));
        if (expr.stmt.getFormula().getCnt() == 2) {
            final Cnst c = (Cnst)expr.stmt.getFormula().getSym()[1];
            return ConstTerm.get(Type.createFromParseNode(i, type == null ? i
                .getTypeAxiom(c).getExprParseTree().getRoot().child[0] : type),
                new Const(OTConstants.mapTypeVar(c.getId())));
        }
        throw new UnsupportedOperationException("Unknown term constructor");
    }
    public static ParseNode typeOf(final Interpreter i, final ParseNode expr,
        final ParseNode type) throws UnsupportedOperationException
    {
        final String stmt = expr.stmt.getLabel();
        if (stmt.equals(OTConstants.HOL_CT_TERM))
            return i.c(OTConstants.HOL_BOOL_TYPE);
        return createFromParseNode(i, expr, type).getTypeProof(i);
    }
}
