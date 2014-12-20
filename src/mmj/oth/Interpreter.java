package mmj.oth;

import java.io.*;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.Map.Entry;

import mmj.lang.*;
import mmj.lang.ParseNode.DeepKey;
import mmj.lang.ParseTree.RPNStep;
import mmj.pa.GeneratedProofStmt;
import mmj.pa.PaConstants;
import mmj.verify.VerifyProofs;

public class Interpreter {
    private final LogicalSystem logicalSystem;
    public final VerifyProofs verify;
    private final WorkVarManager workVarManager;
    private final Messages messages;
    public final String prefix;
    private final Map<Cnst, Stmt> termMap;
    private final Map<Cnst, Stmt> typeMap;
    private final Map<VarHyp, LogHyp> activeTypeAx;
    private final Map<Cnst, Stmt> cnstMap;
    private final Map<Cnst, Stmt> defnMap;
    private final Map<String, WorkVarHyp> tempVars;
    private final Map<ParseNode, WeakReference<ParseNode>> typeProofs;
    private final Map<Assrt, Map<VarHyp, Set<DeepKey>>> boundVarAnalysis;
    private final List<Assrt> thmList;

    final ParseNode[] unifyNodeStack = new ParseNode[PaConstants.UNIFIER_NODE_STACK_SIZE];

    final ParseNode[] compareNodeStack = new ParseNode[PaConstants.UNIFIER_NODE_STACK_SIZE];
    BufferedWriter writer;

    public Interpreter(final String fileName, final LogicalSystem l,
        final VerifyProofs v, final WorkVarManager w, final Messages m)
        throws VerifyException
    {
        try {
            writer = new BufferedWriter(new FileWriter(
                "C:\\Users\\Mario\\Documents\\metamath\\track\\log.mm"));
            log("  $[ hol.mm $]\n");
        } catch (final IOException e) {
            e.printStackTrace();
        }
        logicalSystem = l;
        verify = v;
        messages = m;
        workVarManager = w;
        prefix = fileName.substring(0, fileName.lastIndexOf('.'));
        termMap = new HashMap<Cnst, Stmt>();
        typeMap = new HashMap<Cnst, Stmt>();
        cnstMap = new HashMap<Cnst, Stmt>();
        defnMap = new HashMap<Cnst, Stmt>();
        thmList = new ArrayList<Assrt>();
        activeTypeAx = new HashMap<VarHyp, LogHyp>();
        tempVars = new HashMap<String, WorkVarHyp>();
        typeProofs = new WeakHashMap<ParseNode, WeakReference<ParseNode>>();
        boundVarAnalysis = new HashMap<Assrt, Map<VarHyp, Set<DeepKey>>>();
        final List<Stmt> stmtList = new ArrayList<Stmt>(l.getStmtTbl().values());
        Collections.sort(stmtList, MObj.SEQ);
        for (final Stmt s : stmtList)
            if (s instanceof Assrt && s.getTyp().isProvableLogicStmtTyp()
                && !OTConstants.avoidThms.contains(s.getLabel()))
            {
                thmList.add((Assrt)s);
                final ParseNode r = s.getExprParseTree().getRoot();
                if (r.stmt.getLabel().equals(OTConstants.HOL_TYPE_TERM)) {
                    final Stmt s2 = r.child[1].stmt;
                    if (s2.getTyp().getId().equals(OTConstants.HOL_TERM_CNST)
                        && s2.getFormula().getSym().length == 2)
                    {
                        final Sym sym = s2.getFormula().getSym()[1];
                        if (sym instanceof Cnst) {
                            typeMap.put((Cnst)sym, s);
                            termMap.put((Cnst)sym, s2);
                        }
                    }
                }
                else if (((Assrt)s).getLogHypArrayLength() == 0
                    && r.stmt.getLabel().equals(OTConstants.HOL_PROOF_TERM)
                    && r.child[1].stmt.getLabel().equals(
                        OTConstants.HOL_OV_TERM))
                {
                    final Stmt s2 = r.child[1].child[1].stmt;
                    if (s2.getTyp().getId().equals(OTConstants.HOL_TERM_CNST)
                        && s2.getFormula().getSym().length == 2)
                    {
                        final Sym sym = s2.getFormula().getSym()[1];
                        if (sym instanceof Cnst && !defnMap.containsKey(sym))
                            defnMap.put((Cnst)sym, s);
                    }
                }
            }
            else if (s.getLabel().equals(OTConstants.HOL_FUN_TYPE))
                cnstMap.put((Cnst)s.getFormula().getSym()[3], s);
            else if (s instanceof VarHyp)
                ((VarHyp)s).getVar().setActiveVarHyp((VarHyp)s);
            else if (s instanceof Assrt) {
                final Sym c = s.getFormula().getSym()[1];
                if (c instanceof Cnst)
                    cnstMap.put((Cnst)c, s);
            }
    }

    public void log(final String s) {
        System.out.println(s);
        try {
            writer.append(s + "\n");
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public Formula eval(final ParseNode n) {
        final ScopeFrame mandFrame = new ScopeFrame(logicalSystem
            .getScopeDefList().get(0), Integer.MAX_VALUE);
        try {
            return verify.evaluateProof(
                new ParseTree(n.cloneResolvingUpdatedWorkVars()),
                mandFrame,
                Theorem.buildOptFrame(mandFrame,
                    logicalSystem.getScopeDefList()));
        } catch (final VerifyException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Stmt getTypeAxiom(final Cnst c) {
        return typeMap.get(c);
    }

    public Stmt getTermAxiom(final Cnst c) {
        return termMap.get(c);
    }

    public Stmt getTypeTerm(final Cnst c) {
        return cnstMap.get(c);
    }

    public Stmt getDefinition(final Cnst c) {
        return defnMap.get(c);
    }

    public VarHyp getTermVar(final String var) {
        final mmj.lang.Var v = (mmj.lang.Var)logicalSystem.getSymTbl().get(var);
        if (v != null)
            return v.getActiveVarHyp();
        /* WorkVarHyp w = tempVars.get(var);
        if (w == null)
            try {
                tempVars.put(
                    var,
                    w = workVarManager.allocWorkVarHyp((Cnst)logicalSystem
                        .getSymTbl().get(OTConstants.HOL_VAR_CNST)));
            } catch (final VerifyException e) {
                throw new IllegalStateException(e);
            }
        return w; */
        return createVarHyp("v" + var, OTConstants.HOL_VAR_CNST, var);
    }

    public Cnst getConstant(final String id) {
        final Cnst s = (Cnst)logicalSystem.getSymTbl().get(id);
        return s == null ? createConstant(id) : s;
    }

    public VarHyp getDummy(final List<VarHyp> list) {
        for (final VarHyp v : logicalSystem.getScopeDefList().get(0).scopeVarHyp)
            if (v.getTyp().getId().equals(OTConstants.HOL_VAR_CNST)
                && !v.containedInVarHypListBySeq(list))
                return v;
        assert false : "no dummies left";
        return null;
    }

    public Cnst createConstant(final String id) {
        try {
            log("  $c " + id + " $.");
            return logicalSystem.addCnst(id);
        } catch (final LangException e) {
            throw new IllegalStateException(e);
        }
    }

    public VarHyp createVarHyp(final String label, final String type,
        final String var)
    {
        try {
            final VarHyp h;
            log("  $v " + var + " $.");
            log("  " + label + " $f " + type + " " + var + " $.");
            final mmj.lang.Var v = logicalSystem.addVar(var);
            v.setActiveVarHyp(h = logicalSystem.addVarHyp(label, type, var));
            h.setExprParseTree(new ParseTree(new ParseNode(h)));
            return h;
        } catch (final LangException e) {
            throw new IllegalStateException(e);
        }
    }
    public Stmt createAxiom(final String label, final ParseNode wff,
        final ParseNode... hyps)
    {
        try {
            if (hyps.length > 0) {
                logicalSystem.beginScope();
                log("  ${");
                int i = 1;
                for (final ParseNode n : hyps) {
                    final String labelS = label + "." + i++;
                    final Formula expr = verify.convertRPNToFormula(
                        n.convertToRPN(true), labelS);
                    expr.setTyp((Cnst)logicalSystem.getSymTbl().get(
                        OTConstants.HOL_PROOF_CNST));
                    log("    " + labelS + " $e " + expr.toString() + " $.");
                    logicalSystem.addLogHyp(labelS, expr.getSym());
                }
            }
            final Formula expr = verify.convertRPNToFormula(
                wff.convertToRPN(true), label);
            expr.setTyp((Cnst)logicalSystem.getSymTbl().get(
                OTConstants.HOL_PROOF_CNST));
            log((hyps.length > 0 ? "    " : "  ") + label + " $a " + expr
                + " $.");
            final Axiom ax = logicalSystem.addAxiom(label, expr.getSym());
            ax.setExprParseTree(new ParseTree(wff));
            if (hyps.length > 0) {
                log("  $}");
                logicalSystem.endScope();
            }
            thmList.add(ax);
            // GrammarRule.add(ax, grammar);
            return ax;
        } catch (final LangException e) {
            throw new IllegalStateException(e);
        }
    }

    public Stmt createAxiom(final String label, final Sym... symList) {
        try {
            String s = "  " + label + " $a";
            for (final Sym sym : symList)
                s += " " + sym.toString();
            log(s + " $.");
            final Axiom ax = logicalSystem.addAxiom(label, symList);
            if (ax.getTyp().isProvableLogicStmtTyp())
                thmList.add(ax);
            // ax.setExprParseTree(new ParseTree(new ParseNode(ax)));
            // GrammarRule.add(ax, grammar);
            return ax;
        } catch (final LangException e) {
            throw new IllegalStateException(e);
        }
    }

    public Theorem createTheorem(final String label, final ParseNode wff,
        final ParseNode proof)
    {
        try {
            logicalSystem.beginScope();
            log("  ${");
            final RPNStep[] rpn = new ParseTree(proof).convertToRPN(false);
            // final RPNStep[] rpn = new
            // ParseTree(proof).convertToRPNExpanded();
            final Formula expr = verify.convertRPNToFormula(
                wff.convertToRPN(true), label);
            expr.setTyp((Cnst)logicalSystem.getSymTbl().get(
                OTConstants.HOL_PROOF_CNST));
            generateDjVars(wff, proof);
            log("    " + label + " $p " + expr + " $=");
            log("  "
                + new GeneratedProofStmt(null, rpn, 7, 78).getStmtText()
                    .substring(2) + "  $}\n");
            final Theorem th = logicalSystem.addTheorem(label, 4,
                expr.getSym(), rpn, messages);
            th.setExprParseTree(new ParseTree(wff));
            final List<DjVars> list = new ArrayList<DjVars>();
            String err = verify.verifyOneProof(th, list);
            if (err != null)
                hashCode();
            assert (err = verify.verifyOneProof(th, list)) == null : err;
            logicalSystem.endScope();
            thmList.add(th);
            return th;
        } catch (final LangException e) {
            throw new IllegalStateException(e);
        }
    }

    private Set<DeepKey> getFreeVars(final ParseNode r) {
        final Set<DeepKey> vars = new HashSet<DeepKey>();
        getFreeVars(r, vars, new ArrayDeque<DeepKey>());
        return vars;
    }

    private void getFreeVars(final ParseNode r, final Set<DeepKey> vars,
        final Deque<DeepKey> stack)
    {
        final DeepKey k = new DeepKey(r);
        if (r.stmt.getLabel().equals(OTConstants.HOL_VAR_TERM)
            && !stack.contains(k))
            vars.add(k);
        else if (r.stmt.getLabel().equals(OTConstants.HOL_ABS_TERM)) {
            stack.push(new DeepKey(c(OTConstants.HOL_VAR_TERM, r.child[0],
                r.child[1])));
            getFreeVars(r.child[2], vars, stack);
            stack.pop();
        }
        else
            for (final ParseNode n : r.child)
                getFreeVars(n, vars, stack);
    }

    private boolean unify(final ParseNode e1, final ParseNode e2) {
        if (e1.stmt instanceof WorkVarHyp) {
            if (e1.stmt.getTyp().equals(OTConstants.HOL_VAR_CNST))
                hashCode();
            if (e1.stmt == e2.stmt)
                return true;
            final WorkVarHyp s = (WorkVarHyp)e1.stmt;
            if (s.getSubst() == null || s.getSubst() == e2) {
                s.setSubst(e2);
                return true;
            }
            return unify(s.getSubst(), e2);
        }
        if (e2.stmt instanceof WorkVarHyp) {
            if (e1.stmt.getTyp().equals(OTConstants.HOL_VAR_CNST))
                hashCode();
            final WorkVarHyp s = (WorkVarHyp)e2.stmt;
            if (s.getSubst() == null || s.getSubst() == e1) {
                s.setSubst(e1);
                return true;
            }
            return unify(e1, s.getSubst());
        }
        if (e1.stmt != e2.stmt) {
            assert false : "unify failed";
            return false;
        }
        for (int i = 0; i < e1.child.length; i++)
            if (!unify(e1.child[i], e2.child[i]))
                return false;
        return true;
    }

    public ParseNode proofToExpr(final ParseNode proof) {
        if (!proof.stmt.getTyp().getId().equals(OTConstants.HOL_PROOF_CNST))
            return proof;
        final ParseNode root = proof.stmt.getExprParseTree().getRoot();
        if (proof.stmt instanceof LogHyp)
            return root;
        return root.deepCloneApplyingAssrtSubst(
            ((Assrt)proof.stmt).getMandFrame().hypArray, proof.child, null);
    }

    private ParseNode typeOf(final ParseNode proof) {
        final ParseNode root = proofToExpr(proof);
        assert root.stmt.getLabel().equals(OTConstants.HOL_TYPE_TERM);
        return root.child[0];
    }

    private ParseNode getTypeProof(final ParseNode expr) throws VerifyException
    {
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
        return out;
    }

    private ParseNode cleanupWorkVars(final ParseNode expr, final String typ) {
        final List<WorkVar> workVarList = new ArrayList<WorkVar>();
        expr.accumSetOfWorkVarsUsed(workVarList);
        final Iterator<WorkVar> i = workVarList.iterator();
        WorkVar w = null;
        VarHyp wh = null;
        while (i.hasNext()
            && !(wh = (w = i.next()).getActiveVarHyp()).getTyp().getId()
                .equals(typ));
        if (w == null || !wh.getTyp().getId().equals(typ))
            return expr;
        for (final VarHyp v : logicalSystem.getScopeDefList().get(0).scopeVarHyp)
            if (v.getTyp() == wh.getTyp()) {
                wh.setSubst(c(v));
                workVarManager.dealloc(w);
                while (i.hasNext()
                    && !(wh = (w = i.next()).getActiveVarHyp()).getTyp()
                        .getId().equals(typ));
                if (!i.hasNext()) {
                    w = null;
                    break;
                }
            }
        assert w == null;
        return expr.cloneResolvingUpdatedWorkVars();
    }
    public ParseNode cleanTypeProof(final ParseNode expr, final ParseNode type)
    {
        try {

            ParseNode out = getTypeProof(expr);
            if (type != null)
                unify(typeOf(out), type);
            out = cleanupWorkVars(out.cloneResolvingUpdatedWorkVars(),
                OTConstants.HOL_TYPE_CNST);
            workVarManager.deallocAll();
            return out;
        } catch (final VerifyException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean alphaEquiv(final ParseNode a, final ParseNode b,
        final Map<DeepKey, VarHyp> subst)
    {
        if (subst == null)
            return a.isDeepDup(b);
        if (a.stmt != b.stmt) {
            if (a.stmt.getLabel().equals(OTConstants.HOL_OV_TERM))
                return alphaEquiv(
                    c(OTConstants.HOL_APP_TERM,
                        c(OTConstants.HOL_APP_TERM, a.child[0], a.child[1]),
                        a.child[2]), b, subst);
            if (b.stmt.getLabel().equals(OTConstants.HOL_OV_TERM))
                return alphaEquiv(b, a, subst);
            return false;
        }
        if (a.stmt.getLabel().equals(OTConstants.HOL_VAR_TERM))
            return a.isDeepDup(b)
                || b.child[1].stmt == subst.get(new DeepKey(a));
        if (a.stmt.getLabel().equals(OTConstants.HOL_ABS_TERM)) {
            final DeepKey k = new DeepKey(c(OTConstants.HOL_VAR_TERM,
                a.child[0], a.child[1]));
            final VarHyp old = subst.get(k);
            subst.put(k, (VarHyp)b.child[1].stmt);
            final boolean ae = alphaEquiv(a.child[2], b.child[2], subst);
            if (old == null)
                subst.remove(k);
            else
                subst.put(k, old);
            return ae;
        }
        for (int i = 0; i < a.child.length; i++)
            if (!alphaEquiv(a.child[i], b.child[i], subst))
                return false;
        return true;
    }
    public ParseNode findEquivalent(final ParseNode conjunction,
        final ParseNode model, final Map<DeepKey, VarHyp> subst)
    {
        if (model.stmt.getLabel().equals(OTConstants.HOL_TRUE_TERM))
            return model;
        if (alphaEquiv(conjunction, model, subst))
            return conjunction;
        if (conjunction.stmt.getLabel().equals(OTConstants.HOL_CT_TERM)) {
            final ParseNode n = findEquivalent(conjunction.child[0], model,
                subst);
            return n == null ? findEquivalent(conjunction.child[1], model,
                subst) : n;
        }
        return null;
    }

    public ParseNode reduceAssumptions(final ParseNode target,
        final ParseNode wff, final ParseNode thm)
    {
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
            simpProver.prove(c(OTConstants.HOL_PROOF_TERM, r, s)), thm);
    }

    public final Prover simpProver = new Prover() {
        public ParseNode prove(final ParseNode wff) {
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
            return null;
        }
    };

    public final Prover eqProver = new Prover() {
        public ParseNode prove(final ParseNode wff) {
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
            return null;
        }
    };

    public ParseNode makeEq(final ParseNode a, final ParseNode b) {
        return c(OTConstants.HOL_OV_TERM, c(OTConstants.HOL_EQ_TERM), a, b);
    }

    public ParseNode makeHB(final ParseNode a, final ParseNode al,
        final ParseNode x, final ParseNode y)
    {
        return c(
            OTConstants.HOL_PROOF_TERM,
            c(OTConstants.HOL_TRUE_TERM),
            makeEq(
                c(OTConstants.HOL_APP_TERM,
                    c(OTConstants.HOL_ABS_TERM, al, x, a),
                    c(OTConstants.HOL_VAR_TERM, al, y)), a));
    }

    public final Prover hbProver = new Prover() {
        public ParseNode prove(final ParseNode wff) {
            return testTheorems(wff, OTConstants.hbThms, this);
        }
    };

    private void doBoundVarAnalysis(final ParseNode expr,
        final Deque<DeepKey> boundVars, final Map<VarHyp, Set<DeepKey>> out)
    {
        if (expr.stmt.getLabel().equals(OTConstants.HOL_ABS_TERM)) {
            boundVars.push(new DeepKey(c(OTConstants.HOL_VAR_TERM,
                expr.child[0], expr.child[1])));
            doBoundVarAnalysis(expr.child[2], boundVars, out);
            boundVars.pop();
        }
        else if (expr.stmt instanceof VarHyp
            && expr.stmt.getTyp().getId().equals(OTConstants.HOL_TERM_CNST))
        {
            final Set<DeepKey> set = out.get(expr.stmt);
            if (set == null)
                out.put((VarHyp)expr.stmt, new HashSet<DeepKey>(boundVars));
            else
                for (final Iterator<DeepKey> i = set.iterator(); i.hasNext();)
                    if (!boundVars.contains(i.next()))
                        i.remove();
        }
        else
            for (final ParseNode n : expr.child)
                doBoundVarAnalysis(n, boundVars, out);
    }

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
        return proof;
    }

    private boolean debugging = false;

    public Theorem createTheoremGen(final String label, ParseNode wff,
        ParseNode proof)
    {
        try {
            final Set<DeepKey> freeVars = getFreeVars(wff);
            if (freeVars.isEmpty())
                return createTheorem(label, wff, proof);
            final HashMap<DeepKey, ParseNode> assign = new HashMap<DeepKey, ParseNode>();

            logicalSystem.beginScope();
            log("  ${");
            int j = 1;

            final Iterator<DeepKey> i = freeVars.iterator();
            DeepKey w = i.next();
            for (final VarHyp v : logicalSystem.getScopeDefList().get(0).scopeVarHyp)
                if (v.getTyp().getId().equals(OTConstants.HOL_TERM_CNST)) {
                    final String labelS = label + "." + j++;
                    final ParseNode n = c(OTConstants.HOL_TYPE_TERM,
                        w.value.child[0], c(v));
                    final Formula expr = verify.convertRPNToFormula(
                        n.convertToRPN(true), labelS);
                    expr.setTyp((Cnst)logicalSystem.getSymTbl().get(
                        OTConstants.HOL_PROOF_CNST));
                    log("    " + labelS + " $e " + expr.toString() + " $.");
                    final LogHyp hyp = logicalSystem.addLogHyp(labelS,
                        expr.getSym());
                    hyp.setExprParseTree(new ParseTree(n));
                    assign.put(w, c(hyp));
                    activeTypeAx.put(v, hyp);
                    if (!i.hasNext()) {
                        w = null;
                        break;
                    }
                    w = i.next();
                }
            assert w == null;
            if (label.equals("bool-int_266"))
                hashCode();
            wff = genSubst(wff, assign);
            final Formula expr = verify.convertRPNToFormula(
                wff.convertToRPN(true), label);
            expr.setTyp((Cnst)logicalSystem.getSymTbl().get(
                OTConstants.HOL_PROOF_CNST));
            proof = genSubst(proof, assign);
            generateDjVars(wff, proof);
            log("    " + label + " $p " + expr + " $=");
            final RPNStep[] rpn = new ParseTree(proof).convertToRPN(false);
            log("  "
                + new GeneratedProofStmt(null, rpn, 7, 78).getStmtText()
                    .substring(2) + "  $}\n");
            final Theorem th = logicalSystem.addTheorem(label, 4,
                expr.getSym(), rpn, messages);
            final List<DjVars> list = new ArrayList<DjVars>();
            String err = verify.verifyOneProof(th, list);
            if (err != null || !list.isEmpty())
                hashCode();
            assert (err = verify.verifyOneProof(th, list)) == null : err;
            logicalSystem.endScope();
            activeTypeAx.clear();
            th.setExprParseTree(new ParseTree(wff));
            thmList.add(th);
            return th;
        } catch (final LangException e) {
            throw new IllegalStateException(e);
        }
    }
    private void generateDjVars(final ParseNode wff, final ParseNode proof) {
        final List<VarHyp> varHypList = new ArrayList<VarHyp>();
        proof.accumVarHypUsedListBySeq(varHypList);

        try {
            for (final VarHyp v : varHypList)
                if (v.getTyp().getId().equals(OTConstants.HOL_VAR_CNST))
                    for (final VarHyp w : varHypList)
                        if (v != w
                            && !w.getTyp().getId()
                                .equals(OTConstants.HOL_TERM_CNST))
                            logicalSystem.addDjVars(v.getVar(), w.getVar());
            findBoundAndFreeVars(wff, new ArrayDeque<VarHyp>(),
                new HashMap<VarHyp, Set<VarHyp>>());
        } catch (final LangException e) {
            e.printStackTrace();
        }

        String s = "";
        for (final List<mmj.lang.Var> l : ScopeFrame.consolidateDvGroups(DjVars
            .sortAndCombineDvArrays(logicalSystem.getCurrScopeDef().scopeDjVars
                .toArray(new DjVars[0]), null)))
        {
            s += "  $d";
            for (final mmj.lang.Var v : l)
                s += " " + v.getId();
            s += " $.";
        }
        if (!s.isEmpty())
            log("  " + s);
    }
    private void findBoundAndFreeVars(final ParseNode wff,
        final Deque<VarHyp> boundVars, final Map<VarHyp, Set<VarHyp>> freeIn)
        throws LangException
    {
        if (wff.stmt.getLabel().equals(OTConstants.HOL_ABS_TERM)) {
            boundVars.push((VarHyp)wff.child[1].stmt);
            findBoundAndFreeVars(wff.child[2], boundVars, freeIn);
            boundVars.pop();
        }
        else if (wff.stmt instanceof VarHyp
            && wff.stmt.getTyp().getId().equals(OTConstants.HOL_TERM_CNST))
        {
            final Set<VarHyp> set = freeIn.get(wff.stmt);
            if (set == null)
                freeIn.put((VarHyp)wff.stmt, new HashSet<VarHyp>(boundVars));
            else {
                for (final VarHyp v : boundVars)
                    if (!set.contains(v))
                        logicalSystem.addDjVars(((VarHyp)wff.stmt).getVar(),
                            v.getVar());
                for (final VarHyp v : set)
                    if (!boundVars.contains(v))
                        logicalSystem.addDjVars(((VarHyp)wff.stmt).getVar(),
                            v.getVar());
            }
        }
        else
            for (final ParseNode n : wff.child)
                findBoundAndFreeVars(n, boundVars, freeIn);
    }

    public LogicalSystem getLogicalSystem() {
        return logicalSystem;
    }

    public ParseNode c(final String label, final ParseNode... child) {
        return c(logicalSystem.getStmtTbl().get(label), child);
    }
    public ParseNode c(final Stmt label, final ParseNode... child) {
        assert (label instanceof Assrt ? label.getMandHypArrayLength() : 0) == child.length : label;
        for (final ParseNode n : child)
            assert n != null;
        final ParseNode node = new ParseNode(label);
        node.child = child;
        assert eval(node) != null;
        return node;
    }

    private Term stripUniversalQuantifiers(Term t) {
        while (t instanceof AppTerm && t.asApp().getF() instanceof ConstTerm
            && t.asApp().getF().asConst().getConst().n.fullName().equals("!")
            && t.asApp().getX() instanceof AbsTerm)
            t = t.asApp().getX().asAbs().getB();
        return t;
    }

    public ParseNode findMatchingTheorem(final ParseNode expr) {
        big: for (final Stmt s : thmList) {
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

    public ParseNode testTheorem(final ParseNode expr, final Assrt s,
        final Prover prover)
    {
        final VarHyp[] origVarHyps = s.getMandVarHypArray();
        final ParseNode[] origRepl = s
            .getExprParseTree()
            .getRoot()
            .unifyWithSubtree(expr, origVarHyps, unifyNodeStack,
                compareNodeStack);
        if (origRepl != null) {
            final Map<VarHyp, ParseNode> repl = new HashMap<VarHyp, ParseNode>();
            for (int i = 0; i < origVarHyps.length; i++)
                repl.put(origVarHyps[i], origRepl[i]);
            final Hyp[] hyps = s.getMandFrame().hypArray;
            try {
                for (final Hyp h : hyps)
                    if (h instanceof VarHyp && !repl.containsKey(h))
                        repl.put((VarHyp)h,
                            c(workVarManager.allocWorkVarHyp(h.getTyp())));
                final VarHyp[] varHypArr = new VarHyp[repl.size()];
                final ParseNode[] replArr = new ParseNode[repl.size()];
                int i = 0;
                for (final Entry<VarHyp, ParseNode> e : repl.entrySet()) {
                    varHypArr[i] = e.getKey();
                    replArr[i++] = e.getValue();
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
                for (int j = 0; j < hyps.length; j++) {
                    out.child[j] = hyps[j].getExprParseTree().getRoot()
                        .deepCloneApplyingAssrtSubst(varHypArr, replArr);
                    if (out.child[j].stmt.getLabel().equals(
                        OTConstants.HOL_TYPE_TERM))
                        unify(
                            out.child[j].child[0],
                            typeOf(out.child[j] = getTypeProof(out.child[j].child[1])));
                    else if (out.child[j].stmt.getLabel().equals(
                        OTConstants.HOL_PROOF_TERM))
                        obligations.add(j);
                }
                out = out.cloneResolvingUpdatedWorkVars();
                final List<WorkVar> workVarList = new ArrayList<WorkVar>();
                out.accumSetOfWorkVarsUsed(workVarList);
                if (!workVarList.isEmpty())
                    out = cleanupWorkVars(out, OTConstants.HOL_TYPE_CNST);
                workVarManager.deallocAll();

                if (expr.toString().hashCode() == 1530591329)
                    hashCode();
                // Don't do sub-proofs until all WorkVars are cleared. This
                // assumes that logical steps don't contribute to type
                // inference, which should always be true.
                for (final int j : obligations)
                    out.child[j] = prover.prove(out.child[j]);
                return c(out.stmt, out.child); // does nothing except asserts
            } catch (final VerifyException e) {
                return null;
            }
        }
        return null;
    }

    public ParseNode testTheoremGen(final ParseNode expr, final Assrt s,
        final Prover prover)
    {
        final Map<VarHyp, ParseNode> repl = new HashMap<VarHyp, ParseNode>();
        if (!findSubst(expr, s.getExprParseTree().getRoot(),
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

    public boolean findSubst(final ParseNode target, final ParseNode wff,
        final Map<VarHyp, VarHyp> boundSubst,
        final Map<VarHyp, ParseNode> freeSubst)
    {
        if (wff.stmt instanceof VarHyp) {
            final ParseNode old = freeSubst.get(wff.stmt);
            if (old != null)
                return old.isDeepDup(target);
            freeSubst.put((VarHyp)wff.stmt, target);
            return true;
        }
        if (wff.stmt != target.stmt)
            return false;
        if (wff.stmt.getLabel().equals(OTConstants.HOL_VAR_TERM))
            return findSubst(target.child[0], wff.child[0], boundSubst,
                freeSubst)
                && target.child[1].stmt == boundSubst.get(wff.child[1].stmt);
        if (wff.stmt.getLabel().equals(OTConstants.HOL_ABS_TERM)) {
            final VarHyp v = (VarHyp)wff.child[1].stmt;
            final VarHyp old = boundSubst.get(v);
            boundSubst.put(v, (VarHyp)target.child[1].stmt);
            freeSubst.put(v, target.child[1]);
            final boolean sub = findSubst(target.child[0], wff.child[0],
                boundSubst, freeSubst)
                && findSubst(target.child[2], wff.child[2], boundSubst,
                    freeSubst);
            if (old == null)
                boundSubst.remove(v);
            else
                boundSubst.put(v, old);
            return sub;
        }
        for (int i = 0; i < wff.child.length; i++)
            if (!findSubst(target.child[i], wff.child[i], boundSubst, freeSubst))
                return false;
        return true;
    }

    private ParseNode testTheorems(final ParseNode expr,
        final List<String> thms, final Prover prover)
    {
        for (final String s : thms) {
            final ParseNode n = testTheorem(expr, (Assrt)logicalSystem
                .getStmtTbl().get(s), prover);
            if (n != null)
                return n;
        }
        return null;
    }

    public Assrt addThm(final Thm thm, final boolean auto) throws LangException
    {
        if (thm == null)
            return null;
        // if (thm.ref.equals(OTConstants.ART_DEFINE_CONST)) {
        if (auto && !thm.exportable) {
            if (!thm.ref.equals(OTConstants.ART_DEFINE_CONST))
                return null;
            final ConstTerm c = thm.t.asApp().getF().asApp().getX().asConst();
            final String id = OTConstants.mapConstants(c.getConst().n);
            if (logicalSystem.getSymTbl().containsKey(id))
                return null;
            final Term t = thm.t.asApp().getX();
            final Cnst cnst = createConstant(id);
            final Stmt tc = createAxiom(OTConstants.HOL_TERM_PFX + id,
                logicalSystem.getSymTbl().get(OTConstants.HOL_TERM_CNST), cnst);

            final ParseNode ctc = c(tc), tru = c(OTConstants.HOL_TRUE_TERM), term = t
                .getTermProof(this), typ = t.getType().getTypeProof(this);
            termMap.put(cnst, tc);
            final Stmt df = createAxiom(OTConstants.HOL_DEF_PFX + id,
                c(OTConstants.HOL_PROOF_TERM, tru, makeEq(ctc, term)));
            defnMap.put(cnst, df);
            final VarHyp[] hyps = df.getMandVarHypArray();
            final ParseNode[] vars = new ParseNode[hyps.length];
            for (int i = 0; i < hyps.length; i++)
                vars[i] = c(hyps[i]);
            final Stmt wff = createTheorem(
                OTConstants.HOL_TYPE_PFX + id,
                c(OTConstants.HOL_TYPE_TERM, typ, c(tc)),
                c(OTConstants.HOL_THM_EQTYPRI, typ, tru, term, ctc,
                    t.getTypeProof(this), c(df, vars)));
            typeMap.put(cnst, wff);
        }
        if (thm.num == 9633)
            hashCode();
        final String label = prefix + "_" + thm.num;
        final Map<String, Stmt> stmtTbl = logicalSystem.getStmtTbl();
        final Stmt orig = stmtTbl.get(label);
        if (orig != null)
            return (Assrt)orig;
        final ParseNode node = thm.getExprProof(this);
        verify.convertRPNToFormula(node.convertToRPN(true), label);
        thms++;
        return createTheoremGen(label, node, thm.getProof(this, node));
    }

    int thms = 0;
}
