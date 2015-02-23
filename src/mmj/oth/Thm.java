package mmj.oth;

import java.util.*;

import mmj.lang.*;
import mmj.lang.ParseNode.DeepKey;

public class Thm {
    public final String ref;
    public final List<Thm> hyp;
    public List<Term> assum;
    public Object aux;
    public Term t;
    public int num;
    public boolean exportable;

    public Thm(final int num, final String ref, final List<Thm> hyp,
        final List<Term> assum, final Term t)
    {
        this.ref = ref;
        this.hyp = hyp;
        this.assum = assum;
        this.t = t;
        this.num = num;
        aux = null;
        exportable = false;
    }

    @Override
    public boolean equals(final Object obj) {
        final Thm orig = (Thm)obj;
        final Map<Var, Var> subst = new HashMap<Var, Var>();
        if (assum.size() != orig.assum.size())
            return false;
        final int i = 0;
        for (final Term t2 : assum)
            if (!t2.alphaEquiv(orig.assum.get(i), subst))
                return false;
        return t.alphaEquiv(orig.t, subst);
    }

    public Thm subst(final int num, final List<List<List<Object>>> subst) {
        final List<Term> assum2 = new ArrayList<Term>();
        for (final Term t2 : assum)
            assum2.add(t2.subst(subst));
        final Thm th = new Thm(num, "subst", Arrays.asList(this), assum2,
            t.subst(subst));
        return th.equals(this) ? this : th;
    }

    @Override
    public String toString() {
        String s = "" + num + ":", delim = "";
        for (final Thm h : hyp) {
            s += delim + h.num;
            delim = ",";
        }
        s += ":" + ref;
        while (s.length() <= 30)
            s += " ";
        return s + " " + assum + " |- " + t;
    }

    /**
     * Convert a HOL proof to the Metamath style. The input is the actual form
     * of the output theorem; the invariant is that the consequent represented
     * by this {@link Thm} object is alpha-equivalent to the consequent of the
     * expression, and every hypothesis is alpha-equivalent to some term in the
     * context list of the hypothesis to the expression (i.e. the hypothesis
     * list of this object is alpha-equivalent to a subset of the given
     * hypothesis list).
     *
     * @param p The parent {@link ProofContext} object
     * @param expr The goal expression; should be non-null with root stmt
     *            {@link OTConstants#HOL_PROOF_TERM}
     * @return a proof of the expression, or a blank proof {@code "?"} if the
     *         proof could not be translated successfully. (Blanks may appear in
     *         a sub-term of the expression if the failure occurred further down
     *         the proof.)
     * @throws LangException for some reason
     */
    public ParseNode getProof(final ProofContext p, final ParseNode expr)
        throws LangException
    {
        final HashMap<DeepKey, VarHyp> subst = new HashMap<DeepKey, VarHyp>();
        assert p.i.alphaEquiv(t.getTermProof(p), expr.child[1].child[0], subst);
        for (final Term a : assum)
            assert p.i.findEquivalent(expr.child[0], a.getTermProof(p), subst) != null;
        final ParseNode proof = getProof2(p, expr);
        assert proof != null;
        assert p.i.proofToExpr(proof).isDeepDup(expr);
        return proof;
    }

    /**
     * Convert a HOL proof to the Metamath style. The input is the actual form
     * of the output theorem; the invariant is that the consequent represented
     * by this {@link Thm} object is alpha-equivalent to the consequent of the
     * expression, and every hypothesis is alpha-equivalent to some term in the
     * context list of the hypothesis to the expression (i.e. the hypothesis
     * list of this object is alpha-equivalent to a subset of the given
     * hypothesis list).
     *
     * @param p The parent {@link ProofContext} object
     * @param expr The goal expression; should be non-null with root stmt
     *            {@link OTConstants#HOL_PROOF_TERM}
     * @return a proof of the expression, or a blank proof {@code "?"} if the
     *         proof could not be translated successfully. (Blanks may appear in
     *         a sub-term of the expression if the failure occurred further down
     *         the proof.)
     * @throws LangException for some reason
     */
    public ParseNode getProof2(final ProofContext p, final ParseNode expr)
        throws LangException
    {
        if (num == 9805)
            hashCode();
        final Assrt old = (Assrt)p.i.getLogicalSystem().getStmtTbl()
            .get(p.i.prefix + "_" + num);
        if (old != null) {
            final ParseNode thm = p.testTheorem(expr, old, null);
            if (thm != null)
                return thm;
        }
        if (ref.equals(OTConstants.ART_THM))
            return hyp.get(0).getProof(p, expr);
        ParseNode thm = p.findMatchingTheorem(expr);
        if (thm != null)
            return thm;
        if (ref.equals(OTConstants.ART_DEDUCT)) {
            final ParseNode r = expr.child[0];
            final Thm th1 = hyp.get(0), th2 = hyp.get(1);
            final ParseNode s = expr.child[1].child[1];
            final ParseNode t = expr.child[1].child[2];
            final boolean tru = r.stmt.getLabel().equals(
                OTConstants.HOL_TRUE_TERM);

            final ParseNode p1 = th2.getProof(
                p,
                p.c(OTConstants.HOL_PROOF_TERM,
                    tru ? s : p.c(OTConstants.HOL_CT_TERM, r, s), t));
            final ParseNode p2 = th1.getProof(
                p,
                p.c(OTConstants.HOL_PROOF_TERM,
                    tru ? t : p.c(OTConstants.HOL_CT_TERM, r, t), s));
            return tru ? p.c(OTConstants.HOL_THM_DEDI, s, t, p1, p2) : p.c(
                OTConstants.HOL_THM_DED, r, s, t, p1, p2);
        }
        if (ref.equals(OTConstants.ART_DEFINE_CONST))
            /*
            final ConstTerm c = t.asApp().getF().asApp().getX().asConst();
            final String id = OTConstants.mapConstants(c.getConst().n);
            final Stmt df = p.i.getDefinition(p.i.getConstant(id));
            final ParseNode[] repl = df
                .getExprParseTree()
                .getRoot()
                .unifyWithSubtree(getExprProof(p), df.getMandVarHypArray(),
                    p.i.unifyNodeStack, p.i.compareNodeStack);
            assert repl != null;
            return p.c(df, repl);*/
            throw new UnsupportedOperationException();
        if (ref.equals(OTConstants.ART_EQ_MP)) {
            final ParseNode r = expr.child[0];
            final Thm th1 = hyp.get(0), th2 = hyp.get(1);
            final ParseNode a = th1.t.getTermProof(p);
            final ParseNode b = expr.child[1];
            final ParseNode p1 = th1.getProof(p,
                p.c(OTConstants.HOL_PROOF_TERM, r, a));
            final ParseNode p2 = th2.getProof(p,
                p.c(OTConstants.HOL_PROOF_TERM, r, p.i.makeEq(a, b)));
            return p.c(OTConstants.HOL_THM_MPBI, r, a, b, p1, p2);
        }
        if (ref.equals(OTConstants.ART_SUBST)) {
            final Thm th = hyp.get(0);
            final Assrt s = p.i.addThm(th, false);
            thm = p.testTheorem(thm, s, null);
            if (thm == null) {
                /*
                final ParseNode goal = getExprProof(p);
                if ((thm = p.reduceAssumptions(expr, goal,
                    p.testTheorem(goal, s, null))) != null)
                    return thm;
                return p.reduceAssumptions(expr, goal,
                    p.testTheoremGen(goal, s, null));
                    */
            }
            return thm;
        }
        if (ref.equals(OTConstants.ART_APP_THM)) {
            final ParseNode r = expr.child[0];
            final ParseNode e1 = expr.child[1];
            final String leftLabel = e1.child[1].stmt.getLabel();
            final String rightLabel = e1.child[2].stmt.getLabel();
            if (leftLabel.equals(OTConstants.HOL_OV_TERM)) {
                final ParseNode f = e1.child[1].child[0];
                final ParseNode a = e1.child[1].child[1];
                final ParseNode b = e1.child[1].child[2];
                final ParseNode c = e1.child[2];
                final AppTerm left = t.asApp().getF().asApp().getX().asApp();
                final ParseNode al = left.getX().getType().getTypeProof(p);
                final ParseNode be = left.getF().asApp().getX().getType()
                    .getTypeProof(p);
                final ParseNode ga = left.getType().getTypeProof(p);
                final ParseNode ft = p.cleanTypeProof(
                    f,
                    p.c(OTConstants.HOL_FUN_TYPE, al,
                        p.c(OTConstants.HOL_FUN_TYPE, be, ga)));
                final ParseNode at = p.cleanTypeProof(a, al);
                final ParseNode bt = p.cleanTypeProof(b, be);
                final ParseNode w = p.c(
                    OTConstants.HOL_PROOF_TERM,
                    r,
                    p.i.makeEq(
                        p.c(OTConstants.HOL_APP_TERM,
                            p.c(OTConstants.HOL_APP_TERM, f, a), b), c));
                return p.c(OTConstants.HOL_THM_COVTRI, al, be, ga, f, r, a, b,
                    c, ft, at, bt, getProof(p, w));
            }
            else if (rightLabel.equals(OTConstants.HOL_OV_TERM)) {
                final ParseNode f = e1.child[2].child[0];
                final ParseNode a = e1.child[2].child[1];
                final ParseNode b = e1.child[2].child[2];
                final ParseNode c = e1.child[1];
                final AppTerm right = t.asApp().getX().asApp();
                final ParseNode al = right.getX().getType().getTypeProof(p);
                final ParseNode be = right.getF().asApp().getX().getType()
                    .getTypeProof(p);
                final ParseNode ga = right.getType().getTypeProof(p);
                final ParseNode ft = p.cleanTypeProof(
                    f,
                    p.c(OTConstants.HOL_FUN_TYPE, al,
                        p.c(OTConstants.HOL_FUN_TYPE, be, ga)));
                final ParseNode at = p.cleanTypeProof(a, al);
                final ParseNode bt = p.cleanTypeProof(b, be);
                final ParseNode w = p.c(
                    OTConstants.HOL_PROOF_TERM,
                    r,
                    p.i.makeEq(
                        c,
                        p.c(OTConstants.HOL_APP_TERM,
                            p.c(OTConstants.HOL_APP_TERM, f, a), b)));
                return p.c(OTConstants.HOL_THM_COVTRRI, al, be, ga, f, r, a, b,
                    c, ft, at, bt, getProof(p, w));
            }
            else {
                final ParseNode f = e1.child[1].child[0];
                final ParseNode a = e1.child[1].child[1];
                final ParseNode t = e1.child[2].child[0];
                final ParseNode b = e1.child[2].child[1];
                final AppTerm left = this.t.asApp().getF().asApp().getX()
                    .asApp();
                final ParseNode al = left.getX().getType().getTypeProof(p);
                final ParseNode be = left.getType().getTypeProof(p);
                final ParseNode ft = p.cleanTypeProof(f,
                    p.c(OTConstants.HOL_FUN_TYPE, al, be));
                final ParseNode at = p.cleanTypeProof(a, al);
                final ParseNode wf = p.c(OTConstants.HOL_PROOF_TERM, r,
                    p.i.makeEq(f, t));
                final ParseNode wa = p.c(OTConstants.HOL_PROOF_TERM, r,
                    p.i.makeEq(a, b));
                final ParseNode pf = hyp.get(0).getProof(p, wf);
                final ParseNode pa = hyp.get(1).getProof(p, wa);
                return p.c(OTConstants.HOL_THM_CEQ12, al, be, f, r, t, a, b,
                    ft, at, pf, pa);
            }
        }
        if (ref.equals(OTConstants.ART_ABS_THM)) {
            final ArrayList<VarHyp> varHypList = new ArrayList<VarHyp>();
            expr.child[0].accumVarHypUsedListBySeq(varHypList);
            if (varHypList.contains(expr.child[1].child[1].child[1]))
                throw new UnsupportedOperationException();

            final ParseNode r = expr.child[0], e1 = expr.child[1];
            final Thm th = hyp.get(0);
            final ParseNode al = e1.child[1].child[0];
            final ParseNode x = e1.child[1].child[1];
            final ParseNode a = e1.child[1].child[2];
            ParseNode y = e1.child[2].child[1];
            final ParseNode b = e1.child[2].child[2];
            final ParseNode v = p.c(((Var)aux).toVarHyp(p));
            if (x.stmt != v.stmt || y.stmt != v.stmt) {
                final ParseNode target = p.c(OTConstants.HOL_VAR_TYPE, al, v);
                final ParseNode a2 = p.genSubst(a, Collections.singletonMap(
                    new DeepKey(p.c(OTConstants.HOL_VAR_TERM, al, x)), target));
                final ParseNode b2 = p.genSubst(b, Collections.singletonMap(
                    new DeepKey(p.c(OTConstants.HOL_VAR_TERM, al, y)), target));
                final ParseNode wff = p.c(
                    OTConstants.HOL_PROOF_TERM,
                    r,
                    p.i.makeEq(p.c(OTConstants.HOL_ABS_TERM, al, v, a2),
                        p.c(OTConstants.HOL_ABS_TERM, al, v, b2)));
                return p.reduceAssumptions(expr, wff, getProof(p, wff));
            }
            final AbsTerm left = t.asApp().getF().asApp().getX().asAbs();
            final ParseNode be = left.getB().getType().getTypeProof(p);
            final ParseNode at = p.cleanTypeProof(a, be);
            final ParseNode ap = th.getProof(p,
                p.c(OTConstants.HOL_PROOF_TERM, r, p.i.makeEq(a, b)));
            final List<VarHyp> rVars = new ArrayList<VarHyp>();
            r.accumVarHypUsedListBySeq(rVars);
            if (!((VarHyp)x.stmt).containedInVarHypListBySeq(rVars))
                return p.c(OTConstants.HOL_THM_LEQ, al, be, x, r, a, b, at, ap);

            a.accumVarHypUsedListBySeq(rVars);
            b.accumVarHypUsedListBySeq(rVars);
            ((VarHyp)x.stmt).accumVarHypListBySeq(rVars);
            y = p.c(p.i.getDummy(rVars));
            return p.c(OTConstants.HOL_THM_LEQF, al, be, x, y, r, a, b, at, ap,
                p.hbProver.prove(p.i.makeHB(r, al, x, y)));
        }
        if (ref.equals(OTConstants.ART_ASSUME))
            return p.simpProver.prove(expr);
        if (ref.equals(OTConstants.ART_REFL))
            return p.reduceAssumptions(expr, thm, p.eqProver.prove(thm));
        return new ParseNode();
    }
    private ParseNode trim(final ProofContext p, final ParseNode expr) {
        ParseNode out = null;
        final ParseNode e0 = expr.child[0];
        final Map<DeepKey, VarHyp> subst = new HashMap<DeepKey, VarHyp>();
        for (final Term term : assum) {
            final ParseNode n = p.i.findEquivalent(e0, term.getTermProof(p),
                subst);
            out = out == null ? n : p.c(OTConstants.HOL_CT_TERM, out, n);
        }
        return p.c(expr.stmt, out == null ? p.c(OTConstants.HOL_TRUE_TERM)
            : out, expr.child[1]);
    }
}
