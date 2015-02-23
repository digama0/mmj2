package mmj.oth;

import java.util.*;

import mmj.lang.ParseNode;
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
    public abstract Set<Var> boundVars();
    public boolean allVarsContains(final Name n) {
        return Var.getFromName(freeVars(), n) != null
            || Var.getFromName(boundVars(), n) != null;
    }

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

    public abstract ParseNode getTermProof(final ProofContext p);

    public ParseNode getWffTermProof(final ProofContext p) {
        final ParseNode n = getTermProof(p);
        if (n.stmt.getLabel().equals(OTConstants.SET_WT_CLS))
            return n.child[0];
        return p.c(OTConstants.SET_TW_WFF, n);
    }
    public abstract ParseNode getTypeProof(final ProofContext p,
        final ParseNode left);

    private static Term createFromParseNode(final ProofContext p,
        final ParseNode expr, final ParseNode type)
        throws UnsupportedOperationException
    {
        /*
        final String stmt = expr.stmt.getLabel();
        if (stmt.equals(OTConstants.HOL_CT_TERM)) {
            final ParseNode bool = p.c(OTConstants.HOL_BOOL_TYPE);
            return ContextTerm.get(createFromParseNode(p, expr.child[0], bool),
                createFromParseNode(p, expr.child[1], bool));
        }
        if (stmt.equals(OTConstants.HOL_ABS_TERM))
            return AbsTerm.get(
                Var.createFromVarHyp(p, (VarHyp)expr.child[1].stmt,
                    type == null ? null : type.child[0]),
                createFromParseNode(p, expr.child[2], type == null ? null
                    : type.child[1]));
        if (stmt.equals(OTConstants.HOL_OV_TERM))
            return createFromParseNode(p, p.c(OTConstants.HOL_APP_TERM,
                p.c(OTConstants.HOL_APP_TERM, expr.child[0], expr.child[1]),
                expr.child[2]), type);
        if (stmt.equals(OTConstants.HOL_APP_TERM)) {
            final Term t = createFromParseNode(p, expr.child[1], null);
            return AppTerm.get(
                createFromParseNode(p, expr.child[0], p
                    .c(OTConstants.HOL_FUN_TYPE, t.getType().getTypeProof(p),
                        type)), t);
        }
        if (stmt.equals(OTConstants.HOL_VAR_TERM))
            return VarTerm.get(Var.createFromVarHyp(p,
                (VarHyp)expr.child[1].stmt, expr.child[0]));
        if (expr.stmt.getFormula().getCnt() == 2) {
            final Cnst c = (Cnst)expr.stmt.getFormula().getSym()[1];
            return ConstTerm.get(Type.createFromParseNode(p, type == null ? p
                .getTypeAxiom(c).getExprParseTree().getRoot().child[0] : type),
                new Const(OTConstants.mapTypeVar(c.getId())));
        }*/
        throw new UnsupportedOperationException("Unknown term constructor");
    }
}
