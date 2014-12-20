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

    public ParseNode getExprProof(final Interpreter i) {
        ParseNode node = null;
        for (final Term a : assum)
            node = node == null ? a.getTermProof(i) : i.c(
                OTConstants.HOL_CT_TERM, node, a.getTermProof(i));
        return i.c(OTConstants.HOL_PROOF_TERM,
            node == null ? i.c(OTConstants.HOL_TRUE_TERM) : node,
            t.getTermProof(i));
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
     * @param i The parent {@link Interpreter} object
     * @param expr The goal expression; should be non-null with root stmt
     *            {@link OTConstants#HOL_PROOF_TERM}
     * @return a proof of the expression, or a blank proof {@code "?"} if the
     *         proof could not be translated successfully. (Blanks may appear in
     *         a sub-term of the expression if the failure occurred further down
     *         the proof.)
     * @throws LangException for some reason
     */
    public ParseNode getProof(final Interpreter i, final ParseNode expr)
        throws LangException
    {
        final HashMap<DeepKey, VarHyp> subst = new HashMap<DeepKey, VarHyp>();
        assert i.alphaEquiv(t.getTermProof(i), expr.child[1], subst);
        for (final Term a : assum)
            assert i.findEquivalent(expr.child[0], a.getTermProof(i), subst) != null;
        final ParseNode proof = getProof2(i, expr);
        assert proof != null;
        assert i.proofToExpr(proof).isDeepDup(expr);
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
     * @param i The parent {@link Interpreter} object
     * @param expr The goal expression; should be non-null with root stmt
     *            {@link OTConstants#HOL_PROOF_TERM}
     * @return a proof of the expression, or a blank proof {@code "?"} if the
     *         proof could not be translated successfully. (Blanks may appear in
     *         a sub-term of the expression if the failure occurred further down
     *         the proof.)
     * @throws LangException for some reason
     */
    public ParseNode getProof2(final Interpreter i, final ParseNode expr)
        throws LangException
    {
        if (num == 9805)
            hashCode();
        final Assrt old = (Assrt)i.getLogicalSystem().getStmtTbl()
            .get(i.prefix + "_" + num);
        if (old != null) {
            ParseNode thm = i.testTheorem(expr, old, null);
            if (thm != null)
                return thm;
            final ParseNode goal = getExprProof(i);
            if ((thm = i.reduceAssumptions(expr, goal,
                i.testTheorem(goal, old, null))) != null)
                return thm;
        }
        if (ref.equals(OTConstants.ART_THM))
            return hyp.get(0).getProof(i, expr);
        ParseNode thm = i.findMatchingTheorem(expr);
        if (thm != null)
            return thm;
        final ParseNode trim = trim(i, expr);
        thm = i.findMatchingTheorem(trim);
        if (thm != null)
            return i.reduceAssumptions(expr, trim, thm);
        if (ref.equals(OTConstants.ART_DEDUCT)) {
            final ParseNode r = expr.child[0];
            final Thm th1 = hyp.get(0), th2 = hyp.get(1);
            final ParseNode s = expr.child[1].child[1];
            final ParseNode t = expr.child[1].child[2];
            final boolean tru = r.stmt.getLabel().equals(
                OTConstants.HOL_TRUE_TERM);

            final ParseNode p1 = th2.getProof(
                i,
                i.c(OTConstants.HOL_PROOF_TERM,
                    tru ? s : i.c(OTConstants.HOL_CT_TERM, r, s), t));
            final ParseNode p2 = th1.getProof(
                i,
                i.c(OTConstants.HOL_PROOF_TERM,
                    tru ? t : i.c(OTConstants.HOL_CT_TERM, r, t), s));
            return tru ? i.c(OTConstants.HOL_THM_DEDI, s, t, p1, p2) : i.c(
                OTConstants.HOL_THM_DED, r, s, t, p1, p2);
        }
        if (ref.equals(OTConstants.ART_DEFINE_CONST)) {
            final ConstTerm c = t.asApp().getF().asApp().getX().asConst();
            final String id = OTConstants.mapConstants(c.getConst().n);
            final Stmt df = i.getDefinition(i.getConstant(id));
            final ParseNode[] repl = df
                .getExprParseTree()
                .getRoot()
                .unifyWithSubtree(getExprProof(i), df.getMandVarHypArray(),
                    i.unifyNodeStack, i.compareNodeStack);
            assert repl != null;
            return i.c(df, repl);
        }
        if (ref.equals(OTConstants.ART_EQ_MP)) {
            final ParseNode r = expr.child[0];
            final Thm th1 = hyp.get(0), th2 = hyp.get(1);
            final ParseNode a = th1.t.getTermProof(i);
            final ParseNode b = expr.child[1];
            final ParseNode p1 = th1.getProof(i,
                i.c(OTConstants.HOL_PROOF_TERM, r, a));
            final ParseNode p2 = th2.getProof(i,
                i.c(OTConstants.HOL_PROOF_TERM, r, i.makeEq(a, b)));
            return i.c(OTConstants.HOL_THM_MPBI, r, a, b, p1, p2);
        }
        if (ref.equals(OTConstants.ART_SUBST)) {
            final Thm th = hyp.get(0);
            final Assrt s = i.addThm(th, false);
            thm = i.testTheorem(trim, s, null);
            if (thm == null) {
                final ParseNode goal = getExprProof(i);
                if ((thm = i.reduceAssumptions(expr, goal,
                    i.testTheorem(goal, s, null))) != null)
                    return thm;
                return i.reduceAssumptions(expr, goal,
                    i.testTheoremGen(goal, s, null));
            }
            return i.reduceAssumptions(expr, trim, thm);
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
                final ParseNode al = left.getX().getType().getTypeProof(i);
                final ParseNode be = left.getF().asApp().getX().getType()
                    .getTypeProof(i);
                final ParseNode ga = left.getType().getTypeProof(i);
                final ParseNode ft = i.cleanTypeProof(
                    f,
                    i.c(OTConstants.HOL_FUN_TYPE, al,
                        i.c(OTConstants.HOL_FUN_TYPE, be, ga)));
                final ParseNode at = i.cleanTypeProof(a, al);
                final ParseNode bt = i.cleanTypeProof(b, be);
                final ParseNode w = i.c(
                    OTConstants.HOL_PROOF_TERM,
                    r,
                    i.makeEq(
                        i.c(OTConstants.HOL_APP_TERM,
                            i.c(OTConstants.HOL_APP_TERM, f, a), b), c));
                return i.c(OTConstants.HOL_THM_COVTRI, al, be, ga, f, r, a, b,
                    c, ft, at, bt, getProof(i, w));
            }
            else if (rightLabel.equals(OTConstants.HOL_OV_TERM)) {
                final ParseNode f = e1.child[2].child[0];
                final ParseNode a = e1.child[2].child[1];
                final ParseNode b = e1.child[2].child[2];
                final ParseNode c = e1.child[1];
                final AppTerm right = t.asApp().getX().asApp();
                final ParseNode al = right.getX().getType().getTypeProof(i);
                final ParseNode be = right.getF().asApp().getX().getType()
                    .getTypeProof(i);
                final ParseNode ga = right.getType().getTypeProof(i);
                final ParseNode ft = i.cleanTypeProof(
                    f,
                    i.c(OTConstants.HOL_FUN_TYPE, al,
                        i.c(OTConstants.HOL_FUN_TYPE, be, ga)));
                final ParseNode at = i.cleanTypeProof(a, al);
                final ParseNode bt = i.cleanTypeProof(b, be);
                final ParseNode w = i.c(
                    OTConstants.HOL_PROOF_TERM,
                    r,
                    i.makeEq(
                        c,
                        i.c(OTConstants.HOL_APP_TERM,
                            i.c(OTConstants.HOL_APP_TERM, f, a), b)));
                return i.c(OTConstants.HOL_THM_COVTRRI, al, be, ga, f, r, a, b,
                    c, ft, at, bt, getProof(i, w));
            }
            else {
                final ParseNode f = e1.child[1].child[0];
                final ParseNode a = e1.child[1].child[1];
                final ParseNode t = e1.child[2].child[0];
                final ParseNode b = e1.child[2].child[1];
                final AppTerm left = this.t.asApp().getF().asApp().getX()
                    .asApp();
                final ParseNode al = left.getX().getType().getTypeProof(i);
                final ParseNode be = left.getType().getTypeProof(i);
                final ParseNode ft = i.cleanTypeProof(f,
                    i.c(OTConstants.HOL_FUN_TYPE, al, be));
                final ParseNode at = i.cleanTypeProof(a, al);
                final ParseNode wf = i.c(OTConstants.HOL_PROOF_TERM, r,
                    i.makeEq(f, t));
                final ParseNode wa = i.c(OTConstants.HOL_PROOF_TERM, r,
                    i.makeEq(a, b));
                final ParseNode pf = hyp.get(0).getProof(i, wf);
                final ParseNode pa = hyp.get(1).getProof(i, wa);
                return i.c(OTConstants.HOL_THM_CEQ12, al, be, f, r, t, a, b,
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
            final ParseNode v = i.c(((Var)aux).toVarHyp(i));
            if (x.stmt != v.stmt || y.stmt != v.stmt) {
                final ParseNode target = i.c(OTConstants.HOL_VAR_TYPE, al, v);
                final ParseNode a2 = i.genSubst(a, Collections.singletonMap(
                    new DeepKey(i.c(OTConstants.HOL_VAR_TERM, al, x)), target));
                final ParseNode b2 = i.genSubst(b, Collections.singletonMap(
                    new DeepKey(i.c(OTConstants.HOL_VAR_TERM, al, y)), target));
                final ParseNode wff = i.c(
                    OTConstants.HOL_PROOF_TERM,
                    r,
                    i.makeEq(i.c(OTConstants.HOL_ABS_TERM, al, v, a2),
                        i.c(OTConstants.HOL_ABS_TERM, al, v, b2)));
                return i.reduceAssumptions(expr, wff, getProof(i, wff));
            }
            final AbsTerm left = t.asApp().getF().asApp().getX().asAbs();
            final ParseNode be = left.getB().getType().getTypeProof(i);
            final ParseNode at = i.cleanTypeProof(a, be);
            final ParseNode p = th.getProof(i,
                i.c(OTConstants.HOL_PROOF_TERM, r, i.makeEq(a, b)));
            final List<VarHyp> rVars = new ArrayList<VarHyp>();
            r.accumVarHypUsedListBySeq(rVars);
            if (!((VarHyp)x.stmt).containedInVarHypListBySeq(rVars))
                return i.c(OTConstants.HOL_THM_LEQ, al, be, x, r, a, b, at, p);

            a.accumVarHypUsedListBySeq(rVars);
            b.accumVarHypUsedListBySeq(rVars);
            ((VarHyp)x.stmt).accumVarHypListBySeq(rVars);
            y = i.c(i.getDummy(rVars));
            return i.c(OTConstants.HOL_THM_LEQF, al, be, x, y, r, a, b, at, p,
                i.hbProver.prove(i.makeHB(r, al, x, y)));
        }
        if (ref.equals(OTConstants.ART_ASSUME))
            return i.simpProver.prove(expr);
        if (ref.equals(OTConstants.ART_REFL))
            return i.reduceAssumptions(expr, trim, i.eqProver.prove(trim));
        return new ParseNode();
    }
    private ParseNode trim(final Interpreter i, final ParseNode expr) {
        ParseNode out = null;
        final ParseNode e0 = expr.child[0];
        final Map<DeepKey, VarHyp> subst = new HashMap<DeepKey, VarHyp>();
        for (final Term term : assum) {
            final ParseNode n = i.findEquivalent(e0, term.getTermProof(i),
                subst);
            out = out == null ? n : i.c(OTConstants.HOL_CT_TERM, out, n);
        }
        return i.c(expr.stmt, out == null ? i.c(OTConstants.HOL_TRUE_TERM)
            : out, expr.child[1]);
    }
}
