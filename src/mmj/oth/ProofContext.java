package mmj.oth;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.Map.Entry;

import mmj.lang.*;
import mmj.lang.ParseNode.DeepKey;
import mmj.lang.ParseTree.RPNStep;
import mmj.pa.GeneratedProofStmt;

public class ProofContext {
    private final Map<VarType, Stmt> typeVars;
    private final Map<Var, Stmt> termVars;
    private final Map<Term, Stmt> assums;
    private final String label;
    private int index = 1;
    private final Map<ParseNode, WeakReference<ParseNode>> typeProofs;
    public final Interpreter i;

    public ProofContext(final Interpreter i, final String label) {
        this.label = label;
        this.i = i;
        typeVars = new HashMap<VarType, Stmt>();
        termVars = new HashMap<Var, Stmt>();
        assums = new HashMap<Term, Stmt>();
        typeProofs = new WeakHashMap<ParseNode, WeakReference<ParseNode>>();
    }

    public ParseNode loadThm(final Thm thm) {
        for (final Term t : thm.assum)
            assums.put(t, makeTempHyp(i.makeDed(t.getWffTermProof(this))));
        return i.makeDed(thm.t.getWffTermProof(this));
    }

    private LogHyp makeTempHyp(final ParseNode wff) {

        final String labelS = label + "." + index++;
        final Formula expr = i.verify.convertRPNToFormula(
            wff.convertToRPN(true), labelS);
        expr.setTyp((Cnst)i.getLogicalSystem().getSymTbl()
            .get(OTConstants.SET_PROOF_CNST));
        i.log("    " + labelS + " $e " + expr.toString() + " $.");
        try {
            final LogicalSystem ls = i.getLogicalSystem();
            final LogHyp hyp = new LogHyp(ls.getSeqAssigner().nextSeq(),
                ls.getSymTbl(), ls.getStmtTbl(), expr.getSym(), null);
            hyp.setExprParseTree(new ParseTree(wff));
            return hyp;
        } catch (final LangException e) {
            throw new IllegalStateException(e);
        }
    }

    private LogHyp addHyp(final ParseNode wff) {
        final String labelS = label + "." + index++;
        final Formula expr = i.verify.convertRPNToFormula(
            wff.convertToRPN(true), labelS);
        expr.setTyp((Cnst)i.getLogicalSystem().getSymTbl()
            .get(OTConstants.SET_PROOF_CNST));
        i.log("    " + labelS + " $e " + expr.toString() + " $.");
        try {
            final LogHyp hyp = i.getLogicalSystem().addLogHyp(labelS,
                expr.getSym());
            hyp.setExprParseTree(new ParseTree(wff));
            return hyp;
        } catch (final LangException e) {
            throw new IllegalStateException(e);
        }
    }

    public ParseNode c(final String label, final ParseNode... child) {
        return i.c(label, child);
    }
    public ParseNode c(final Stmt label, final ParseNode... child) {
        return i.c(label, child);
    }

    private ParseNode typeOf(final ParseNode proof) {
        final ParseNode root = i.proofToExpr(proof);
        assert root.stmt.getLabel().equals(OTConstants.HOL_TYPE_TERM);
        return root.child[0];
    }

    private ParseNode getTypeProof(final ParseNode expr) throws VerifyException
    {
        /*
        final WeakReference<ParseNode> ref = typeProofs.get(expr);
        if (ref != null) {
            final ParseNode proof = ref.get();
            if (proof != null)
                return proof;
        }
        final String stmt = expr.stmt.getLabel();
        ParseNode out;
        if (stmt.equals(OTConstants.HOL_CT_TERM)) {
            c(OTConstants.HOL_BOOL_TYPE);
            out = c(OTConstants.HOL_CT_TYPE, expr.child[0], expr.child[1],
                getTypeProof(expr.child[0]), getTypeProof(expr.child[1]));
        }
        else if (stmt.equals(OTConstants.HOL_ABS_TERM)) {
            final ParseNode n = getTypeProof(expr.child[2]);
            out = c(OTConstants.HOL_ABS_TYPE, expr.child[0], typeOf(n),
                expr.child[1], expr.child[2], n);
        }
        else if (stmt.equals(OTConstants.HOL_OV_TERM)) {
            final ParseNode a = getTypeProof(expr.child[1]);
            final ParseNode b = getTypeProof(expr.child[2]);
            final ParseNode al = typeOf(a);
            final ParseNode be = typeOf(b);
            if (expr.child[0].stmt.getLabel().equals(OTConstants.HOL_EQ_TERM)) {
                unify(al, be);
                out = c(OTConstants.HOL_THM_WEQI, al, expr.child[1],
                    expr.child[2], a, b).deepCloneApplyingWorkVarUpdates();
            }
            else {
                final WorkVar v = workVarManager.alloc((Cnst)logicalSystem
                    .getSymTbl().get(OTConstants.HOL_TYPE_CNST));
                final ParseNode ga = c(v.getActiveVarHyp());
                final ParseNode f = getTypeProof(expr.child[0]);
                final boolean u = unify(
                    typeOf(f),
                    c(OTConstants.HOL_FUN_TYPE, al,
                        c(OTConstants.HOL_FUN_TYPE, be, ga)));
                assert u;

                out = c(OTConstants.HOL_OV_TYPE, al, be, ga, expr.child[0],
                    expr.child[1], expr.child[2], f, a, b)
                    .deepCloneApplyingWorkVarUpdates();
            }
        }
        else if (stmt.equals(OTConstants.HOL_APP_TERM)) {
            final WorkVar v = workVarManager.alloc((Cnst)logicalSystem
                .getSymTbl().get(OTConstants.HOL_TYPE_CNST));
            final ParseNode be = c(v.getActiveVarHyp());
            final ParseNode f = getTypeProof(expr.child[0]);
            final ParseNode t = getTypeProof(expr.child[1]);
            final ParseNode al = typeOf(t);
            final boolean u = unify(typeOf(f),
                c(OTConstants.HOL_FUN_TYPE, al, be));
            assert u;

            out = c(OTConstants.HOL_APP_TYPE, al, be, expr.child[0],
                expr.child[1], f, t).deepCloneApplyingWorkVarUpdates();
        }
        else if (stmt.equals(OTConstants.HOL_VAR_TERM))
            out = c(OTConstants.HOL_VAR_TYPE, expr.child);
        else if (expr.stmt instanceof VarHyp)
            out = c(activeTypeAx.get(expr.stmt));
        else if (expr.stmt.getFormula().getCnt() == 2) {
            final Cnst c = (Cnst)expr.stmt.getFormula().getSym()[1];
            final Assrt s = (Assrt)getTypeAxiom(c);
            final Hyp[] hypArray = s.getMandFrame().hypArray;
            final ParseNode[] child = new ParseNode[hypArray.length];
            for (int i = 0; i < hypArray.length; i++)
                child[i] = c(workVarManager
                    .allocWorkVarHyp(((VarHyp)hypArray[i]).getTyp()));
            out = c(s, child);
        }
        else
            throw new IllegalArgumentException(expr.toString());
        final List<WorkVar> workVarList = new ArrayList<WorkVar>();
        typeOf(out).accumSetOfWorkVarsUsed(workVarList);
        if (workVarList.isEmpty())
            typeProofs.put(expr, new WeakReference<ParseNode>(
                out = cleanupWorkVars(out, OTConstants.HOL_TYPE_CNST)));
        return out;*/
        throw new UnsupportedOperationException();
    }

    public ParseNode cleanTypeProof(final ParseNode expr, final ParseNode type)
    {
        /*try {

            ParseNode out = getTypeProof(expr);
            if (type != null)
                i.unify(typeOf(out), type);
            out = i.cleanupWorkVars(out.cloneResolvingUpdatedWorkVars(),
                OTConstants.HOL_TYPE_CNST);
            workVarManager.deallocAll();
            return out;
        } catch (final VerifyException e) {
            throw new IllegalStateException(e);
        }*/
        throw new UnsupportedOperationException();
    }

    public ParseNode reduceAssumptions(final ParseNode target,
        final ParseNode wff, final ParseNode thm)
    {
        /*
        if (target.isDeepDup(wff))
            return thm;
        if (thm == null)
            return null;
        if (!target.child[1].isDeepDup(wff.child[1])) {
            final ParseNode r = target.child[0], a = wff.child[1], b = target.child[1];
            final ParseNode p1 = reduceAssumptions(
                c(OTConstants.HOL_PROOF_TERM, r, a), wff, thm);
            final ParseNode p2 = eqProver.prove(c(OTConstants.HOL_PROOF_TERM,
                c(OTConstants.HOL_TRUE_TERM), makeEq(a, b)));
            return c(OTConstants.HOL_THM_SYLIB, r, a, b, p1, p2);
        }
        if (wff.child[0].stmt.getLabel().equals(OTConstants.HOL_TRUE_TERM)) {
            final ParseNode r = target.child[0], a = target.child[1];
            return c(OTConstants.HOL_THM_A1I, r, a,
                cleanTypeProof(r, c(OTConstants.HOL_BOOL_TYPE)), thm);
        }
        final ParseNode r = target.child[0], s = wff.child[0], t = target.child[1];
        return c(OTConstants.HOL_THM_SYL, r, s, t,
            simpProver.prove(c(OTConstants.HOL_PROOF_TERM, r, s)), thm);*/
        throw new UnsupportedOperationException();
    }

    public final Prover simpProver = new Prover() {
        public ParseNode prove(final ParseNode wff) {
            /*
            if (wff.toString().equals("[[tim, kt, [hb, vt]; kv]; kov, kt]; fp"))
                hashCode();
            final ParseNode r = wff.child[0], e1 = wff.child[1];
            if (e1.stmt.getLabel().equals(OTConstants.HOL_CT_TERM)) {
                final ParseNode s = e1.child[0];
                final ParseNode t = e1.child[1];
                return c(OTConstants.HOL_THM_JCA, r, s, t,
                    prove(c(wff.stmt, r, s)), prove(c(wff.stmt, r, t)));
            }
            final ParseNode alpha = findEquivalent(r, e1,
                new HashMap<DeepKey, VarHyp>());
            if (alpha != null && !alpha.isDeepDup(e1))
                return c(OTConstants.HOL_THM_SYLIB, r, alpha, e1,
                    prove(c(wff.stmt, r, alpha)), eqProver.prove(c(wff.stmt,
                        c(OTConstants.HOL_TRUE_TERM), makeEq(alpha, e1))));
            ParseNode n = testTheorems(wff, OTConstants.simpThms, this);
            if (n != null)
                return n;
            if (r.stmt.getLabel().equals(OTConstants.HOL_CT_TERM)) {
                String s = null;
                if (findEquivalent(r.child[1], e1, null) != null)
                    s = OTConstants.HOL_THM_ADANTL;
                if (findEquivalent(r.child[0], e1, null) != null)
                    s = OTConstants.HOL_THM_ADANTR;
                if (s == null)
                    throw new UnsupportedOperationException();
                n = testTheorem(wff, (Assrt)logicalSystem.getStmtTbl().get(s),
                    this);
                if (n != null)
                    return n;
            }
            return null;*/
            throw new UnsupportedOperationException();
        }
    };

    public final Prover eqProver = new Prover() {
        public ParseNode prove(final ParseNode wff) {
            /*
            assert wff.stmt.getLabel().equals(OTConstants.HOL_PROOF_TERM);
            final ParseNode r = wff.child[0], e1 = wff.child[1];
            assert e1.stmt.getLabel().equals(OTConstants.HOL_OV_TERM);
            assert e1.child[0].stmt.getLabel().equals(OTConstants.HOL_EQ_TERM);
            ParseNode n = testTheorems(wff, OTConstants.equalityThms, this);
            if (n != null)
                return n;
            n = simpProver.prove(wff);
            if (n != null)
                return n;
            final ParseNode left = e1.child[1], right = e1.child[2];
            if (left.stmt.getLabel().equals(OTConstants.HOL_ABS_TERM)
                && right.stmt.getLabel().equals(OTConstants.HOL_ABS_TERM))
            {
                final ParseNode al = left.child[0];
                final ParseNode x = left.child[1];
                final ParseNode a = left.child[2];
                final ParseNode y = right.child[1];
                final ParseNode c = right.child[2];
                final ParseNode vx = c(OTConstants.HOL_VAR_TERM, al, x);
                final ParseNode vy = c(OTConstants.HOL_VAR_TERM, al, y);
                final ParseNode b = genSubst(
                    c,
                    Collections.singletonMap(new DeepKey(vy),
                        c(OTConstants.HOL_VAR_TYPE, al, x)));

                final ParseNode at = cleanTypeProof(a, null);
                final ParseNode be = typeOf(at);
                final ParseNode p1 = c(OTConstants.HOL_PROOF_TERM, r,
                    makeEq(a, b));
                final List<VarHyp> rcVars = new ArrayList<VarHyp>();
                c.accumVarHypUsedListBySeq(rcVars);
                r.accumVarHypUsedListBySeq(rcVars);
                final List<VarHyp> bVars = new ArrayList<VarHyp>();
                b.accumVarHypUsedListBySeq(bVars);
                final ParseNode p2 = c(OTConstants.HOL_PROOF_TERM,
                    makeEq(vx, vy), makeEq(b, c));
                if (!((VarHyp)x.stmt).containedInVarHypListBySeq(rcVars)
                    && !((VarHyp)y.stmt).containedInVarHypListBySeq(bVars))
                    return c(OTConstants.HOL_THM_CBVD, al, be, x, y, r, a, b,
                        c, at, prove(p1), prove(p2));

                b.accumVarHypUsedListBySeq(rcVars);
                ((VarHyp)x.stmt).accumVarHypListBySeq(rcVars);
                ((VarHyp)y.stmt).accumVarHypListBySeq(rcVars);
                final ParseNode z = c(getDummy(rcVars));

                final ParseNode hbr = hbProver.prove(makeHB(r, al, x, z));
                final ParseNode hbb = hbProver.prove(makeHB(b, al, y, z));
                final ParseNode hbc = hbProver.prove(makeHB(c, al, x, z));
                return c(OTConstants.HOL_THM_CBVDF, al, be, x, y, z, r, a, b,
                    c, at, prove(p1), prove(p2), hbr, hbb, hbc);
            }
            return null;*/
            throw new UnsupportedOperationException();
        }
    };

    public final Prover hbProver = new Prover() {
        public ParseNode prove(final ParseNode wff) {
            return testTheorems(wff, OTConstants.hbThms, this);
        }
    };

    public ParseNode genSubst(final ParseNode proof,
        final Map<DeepKey, ParseNode> assign)
    {
        return genSubst(proof, assign, new ArrayDeque<DeepKey>(),
            new HashMap<DeepKey, ParseNode>());
    }
    private ParseNode genSubst(final ParseNode proof,
        final Map<DeepKey, ParseNode> assign, final Deque<DeepKey> boundVars,
        final Map<DeepKey, ParseNode> cache)
    {
        /*
        final ParseNode val;
        if (boundVars.isEmpty() && (val = cache.get(proof)) != null)
            return val;
        if (proof.stmt instanceof Hyp)
            return proof;
        boolean changed = false;
        final String s = proof.stmt.getLabel();
        if (s.equals(OTConstants.HOL_VAR_TERM)) {
            final DeepKey k = new DeepKey(proof);
            if (boundVars.contains(k))
                return proof;
            final ParseNode newN = assign.get(k);
            return newN == null ? proof : proofToExpr(newN).child[1];
        }
        if (s.equals(OTConstants.HOL_VAR_TYPE)) {
            final DeepKey k = new DeepKey(c(OTConstants.HOL_VAR_TERM,
                proof.child));
            if (boundVars.contains(k))
                return proof;
            final ParseNode newN = assign.get(k);
            return newN == null ? proof : newN;
        }
        if (s.equals(OTConstants.HOL_THM_BETA)) {
            final ParseNode al = proof.child[0];
            final ParseNode x = proof.child[2];
            final DeepKey k = new DeepKey(c(OTConstants.HOL_VAR_TERM, al, x));
            final ParseNode ct = assign.get(k);
            if (ct != null) {
                final ParseNode be = proof.child[1];
                boundVars.push(k);
                final ParseNode a = genSubst(proof.child[3], assign, boundVars,
                    cache);
                final ParseNode at = genSubst(proof.child[4], assign,
                    boundVars, cache);
                boundVars.pop();
                final ParseNode b = genSubst(proof.child[3], assign, boundVars,
                    cache);
                final ParseNode c = proofToExpr(ct).child[1];
                final ParseNode eq = c(OTConstants.HOL_PROOF_TERM,
                    makeEq(k.value, c), makeEq(a, b));
                return c(OTConstants.HOL_THM_CL, al, be, x, a, b, c, at, ct,
                    eqProver.prove(eq));
            }
        }
        Map<VarHyp, Set<DeepKey>> bound = boundVarAnalysis.get(proof.stmt);
        if (bound == null) {
            final Deque<DeepKey> bv = new ArrayDeque<DeepKey>();
            bound = new HashMap<VarHyp, Set<DeepKey>>();
            for (final LogHyp h : ((Assrt)proof.stmt).getLogHypArray()) {
                final ParseNode p = h.getExprParseTree().getRoot();
                if (p.stmt.getLabel().equals(OTConstants.HOL_PROOF_TERM))
                    doBoundVarAnalysis(p, bv, bound);
                bound.clear();
            }
            doBoundVarAnalysis(proof.stmt.getExprParseTree().getRoot(), bv,
                bound);
            boundVarAnalysis.put((Assrt)proof.stmt, bound);
        }
        final ParseNode[] newChild = new ParseNode[proof.child.length];
        final Hyp[] hyp = ((Assrt)proof.stmt).getMandFrame().hypArray;
        for (int i = 0; i < proof.child.length; i++) {
            Set<DeepKey> newVars = null;
            if (hyp[i] instanceof VarHyp)
                newVars = bound.get(hyp[i]);
            else {
                final ParseNode r = hyp[i].getExprParseTree().getRoot();
                final int[][] override = OTConstants.boundVarOverrides
                    .get(hyp[i].getLabel());
                if (override != null) {
                    newVars = new HashSet<DeepKey>();
                    for (final int[] pair : override)
                        newVars.add(new DeepKey(c(OTConstants.HOL_VAR_TERM,
                            c(hyp[pair[0]]), c(hyp[pair[1]]))));
                }
                if (r.stmt.getLabel().equals(OTConstants.HOL_TYPE_TERM)
                    && r.child[1].stmt instanceof VarHyp)
                    newVars = bound.get(r.child[1].stmt);
            }
            final ParseNode n = proof.child[i];
            if (newVars == null)
                newChild[i] = genSubst(n, assign, boundVars, cache);
            else {
                for (final DeepKey k : newVars)
                    boundVars.push(new DeepKey(k.value
                        .deepCloneApplyingAssrtSubst(hyp, proof.child)));
                newChild[i] = genSubst(n, assign, boundVars, cache);
                for (int j = newVars.size(); j > 0; j--)
                    boundVars.pop();
            }
            if (newChild[i] == null)
                return null;
            if (boundVars.isEmpty())
                cache.put(new DeepKey(n), newChild[i]);
            if (newChild[i] != n)
                changed = true;
        }
        if (!debugging && boundVars.isEmpty()) {
            debugging = true;
            assert genSubst(proofToExpr(proof), assign).isDeepDup(
                proofToExpr(genSubst(proof, assign)));
            debugging = false;
        }
        if (changed)
            return c(proof.stmt, newChild);
        return proof;*/
        throw new UnsupportedOperationException();
    }

    public ParseNode findMatchingTheorem(final ParseNode expr) {
        big: for (final Stmt s : i.thmList) {
            for (final LogHyp h : ((Assrt)s).getLogHypArray())
                if (h.getExprParseTree().getRoot().stmt.getLabel().equals(
                    OTConstants.HOL_PROOF_TERM))
                    continue big;

            final ParseNode n = testTheorem(expr, (Assrt)s, null);
            if (n != null)
                return n;
        }
        return null;
    }

    public ParseNode testTheoremGen(final ParseNode expr, final Assrt s,
        final Prover prover)
    {
        final Map<VarHyp, ParseNode> repl = new HashMap<VarHyp, ParseNode>();
        if (!i.findSubst(expr, s.getExprParseTree().getRoot(),
            new HashMap<VarHyp, VarHyp>(), repl))
            return null;
        final VarHyp[] varHypArr = new VarHyp[repl.size()];
        final ParseNode[] replArr = new ParseNode[repl.size()];
        int i = 0;
        for (final Entry<VarHyp, ParseNode> e : repl.entrySet()) {
            varHypArr[i] = e.getKey();
            replArr[i++] = e.getValue();
        }
        final ParseNode wff = s.getExprParseTree().getRoot()
            .deepCloneApplyingAssrtSubst(varHypArr, replArr);
        return reduceAssumptions(expr, wff, testTheorem(wff, s, prover));

    }

    public ParseNode testTheorem(final ParseNode expr, final Assrt s,
        final Prover prover)
    {
        final VarHyp[] origVarHyps = s.getMandVarHypArray();
        final ParseNode[] origRepl = s
            .getExprParseTree()
            .getRoot()
            .unifyWithSubtree(expr, origVarHyps, i.unifyNodeStack,
                i.compareNodeStack);
        if (origRepl != null) {
            final Map<VarHyp, ParseNode> repl = new HashMap<VarHyp, ParseNode>();
            for (int j = 0; j < origVarHyps.length; j++)
                repl.put(origVarHyps[j], origRepl[j]);
            final Hyp[] hyps = s.getMandFrame().hypArray;
            try {
                for (final Hyp h : hyps)
                    if (h instanceof VarHyp && !repl.containsKey(h))
                        repl.put((VarHyp)h,
                            c(i.workVarManager.allocWorkVarHyp(h.getTyp())));
                final VarHyp[] varHypArr = new VarHyp[repl.size()];
                final ParseNode[] replArr = new ParseNode[repl.size()];
                int j = 0;
                for (final Entry<VarHyp, ParseNode> e : repl.entrySet()) {
                    varHypArr[j] = e.getKey();
                    replArr[j++] = e.getValue();
                    for (final Entry<VarHyp, ParseNode> e2 : repl.entrySet()) {
                        final VarHyp v1 = e.getKey(), v2 = e2.getKey();
                        if (v1.getTyp().getId()
                            .equals(OTConstants.HOL_VAR_CNST)
                            && ScopeFrame.isVarPairInDjArray(s.getMandFrame(),
                                v1.getVar(), v2.getVar()))
                        {
                            final List<VarHyp> varHypList = new ArrayList<VarHyp>();
                            e2.getValue().accumVarHypUsedListBySeq(varHypList);
                            if (varHypList.contains(e.getValue().stmt))
                                return null;
                        }
                    }
                }
                ParseNode out = new ParseNode(s); // We don't use c() here
                // because it asserts that the child is a correct substitution,
                // and we are using wff proofs in place of the corresponding
                // theorem proof temporarily

                out.child = new ParseNode[hyps.length];
                final List<Integer> obligations = new ArrayList<Integer>();
                for (int k = 0; k < hyps.length; k++) {
                    out.child[k] = hyps[k].getExprParseTree().getRoot()
                        .deepCloneApplyingAssrtSubst(varHypArr, replArr);
                    if (out.child[k].stmt.getLabel().equals(
                        OTConstants.HOL_TYPE_TERM))
                        i.unify(
                            out.child[k].child[0],
                            typeOf(out.child[k] = getTypeProof(out.child[k].child[1])));
                    else if (out.child[k].stmt.getLabel().equals(
                        OTConstants.HOL_PROOF_TERM))
                        obligations.add(k);
                }
                out = out.cloneResolvingUpdatedWorkVars();
                final List<WorkVar> workVarList = new ArrayList<WorkVar>();
                out.accumSetOfWorkVarsUsed(workVarList);
                if (!workVarList.isEmpty())
                    out = i.cleanupWorkVars(out, OTConstants.HOL_TYPE_CNST);
                i.workVarManager.deallocAll();

                if (expr.toString().hashCode() == 1530591329)
                    hashCode();
                // Don't do sub-proofs until all WorkVars are cleared. This
                // assumes that logical steps don't contribute to type
                // inference, which should always be true.
                for (final int k : obligations)
                    out.child[k] = prover.prove(out.child[k]);
                return c(out.stmt, out.child); // does nothing except asserts
            } catch (final VerifyException e) {
                return null;
            }
        }
        return null;
    }

    private ParseNode testTheorems(final ParseNode expr,
        final List<String> thms, final Prover prover)
    {
        for (final String s : thms) {
            final ParseNode n = testTheorem(expr, (Assrt)i.getLogicalSystem()
                .getStmtTbl().get(s), prover);
            if (n != null)
                return n;
        }
        return null;
    }

    public Theorem createTheorem(final ParseNode wff, final ParseNode proof) {
        i.getLogicalSystem().beginScope();
        i.log("  ${");
        try {
            final Formula expr = i.verify.convertRPNToFormula(
                wff.convertToRPN(true), label);
            expr.setTyp((Cnst)i.getLogicalSystem().getSymTbl()
                .get(OTConstants.HOL_PROOF_CNST));
            i.generateDjVars(wff, proof);
            i.log("    " + label + " $p " + expr + " $=");
            final RPNStep[] rpn = new ParseTree(proof).convertToRPN(false);
            i.log("  "
                + new GeneratedProofStmt(null, rpn, 7, 78).getStmtText()
                    .substring(2) + "  $}\n");
            final Theorem th = i.getLogicalSystem().addTheorem(label, 4,
                expr.getSym(), rpn, i.messages);
            final List<DjVars> list = new ArrayList<DjVars>();
            String err = i.verify.verifyOneProof(th, list);
            if (err != null || !list.isEmpty())
                hashCode();
            assert (err = i.verify.verifyOneProof(th, list)) == null : err;
            i.getLogicalSystem().endScope();
            i.activeTypeAx.clear();
            th.setExprParseTree(new ParseTree(wff));
            i.thmList.add(th);
            return th;
        } catch (final LangException e) {
            throw new IllegalStateException(e);
        }
    }

}
