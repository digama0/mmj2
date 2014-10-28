package mmj.oth;

import java.lang.ref.WeakReference;
import java.util.*;

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
    private final String prefix;
    private final Map<Cnst, Stmt> termMap;
    private final Map<Cnst, Stmt> typeMap;
    private final Map<VarHyp, LogHyp> activeTypeAx;
    private final Map<Cnst, Stmt> cnstMap;
    private final Map<Cnst, Stmt> defnMap;
    private final Map<ParseNode, WeakReference<ParseNode>> typeProofs;
    private final List<Assrt> thmList;

    final ParseNode[] unifyNodeStack = new ParseNode[PaConstants.UNIFIER_NODE_STACK_SIZE];

    final ParseNode[] compareNodeStack = new ParseNode[PaConstants.UNIFIER_NODE_STACK_SIZE];

    public Interpreter(final String fileName, final LogicalSystem l,
        final VerifyProofs v, final WorkVarManager w, final Messages m)
        throws VerifyException
    {
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
        typeProofs = new WeakHashMap<ParseNode, WeakReference<ParseNode>>();
        final List<Stmt> stmtList = new ArrayList<Stmt>(l.getStmtTbl().values());
        Collections.sort(stmtList, MObj.SEQ);
        for (final Stmt s : stmtList)
            if (s instanceof Assrt && s.getTyp().isProvableLogicStmtTyp()) {
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
        return createVarHyp("v" + var, OTConstants.HOL_VAR_CNST, var);
    }

    public Cnst getConstant(final String id) {
        final Cnst s = (Cnst)logicalSystem.getSymTbl().get(id);
        return s == null ? createConstant(id) : s;
    }

    public Cnst createConstant(final String id) {
        try {
            System.out.println("  $c " + id + " $.");
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
            System.out.println("  $v " + var + " $.");
            System.out
                .println("  " + label + " $f " + type + " " + var + " $.");
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
                System.out.println("  ${");
                int i = 1;
                for (final ParseNode n : hyps) {
                    final String labelS = label + "." + i++;
                    final Formula expr = verify.convertRPNToFormula(
                        n.convertToRPN(true), labelS);
                    expr.setTyp((Cnst)logicalSystem.getSymTbl().get(
                        OTConstants.HOL_PROOF_CNST));
                    System.out.println("    " + labelS + " $e "
                        + expr.toString() + " $.");
                    logicalSystem.addLogHyp(labelS, expr.getSym());
                }
                System.out.print("  ");
            }
            final Formula expr = verify.convertRPNToFormula(
                wff.convertToRPN(true), label);
            expr.setTyp((Cnst)logicalSystem.getSymTbl().get(
                OTConstants.HOL_PROOF_CNST));
            System.out.println("  " + label + " $a " + expr.toString() + " $.");
            final Axiom ax = logicalSystem.addAxiom(label, expr.getSym());
            ax.setExprParseTree(new ParseTree(wff));
            if (hyps.length > 0) {
                System.out.print("  $}");
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
            System.out.println(s + " $.");
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
            System.out.println("  ${");
            final RPNStep[] rpn = new ParseTree(proof).convertToRPN(false);
            // final RPNStep[] rpn = new
            // ParseTree(proof).convertToRPNExpanded();
            final Formula expr = verify.convertRPNToFormula(
                wff.convertToRPN(true), label);
            expr.setTyp((Cnst)logicalSystem.getSymTbl().get(
                OTConstants.HOL_PROOF_CNST));
            // generateDjVars(proof);
            System.out.println("    " + label + " $p " + expr + " $=");
            System.out.print("  "
                + new GeneratedProofStmt(null, rpn, 7, 78).getStmtText()
                    .substring(2));
            final Theorem th = logicalSystem.addTheorem(label, 4,
                expr.getSym(), rpn, messages);
            th.setExprParseTree(new ParseTree(wff));
            final List<DjVars> list = new ArrayList<DjVars>();
            String err = verify.verifyOneProof(th, list);
            if (err != null)
                hashCode();
            assert (err = verify.verifyOneProof(th, list)) == null : err;
            System.out.println("  $}\n");
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
            if (((WorkVarHyp)e1.stmt).paSubst == null) {
                ((WorkVarHyp)e1.stmt).paSubst = e2;
                return true;
            }
            return e2.isDeepDup(((WorkVarHyp)e1.stmt).paSubst);
        }
        if (e2.stmt instanceof WorkVarHyp) {
            if (((WorkVarHyp)e2.stmt).paSubst == null) {
                ((WorkVarHyp)e2.stmt).paSubst = e1;
                return true;
            }
            return e1.isDeepDup(((WorkVarHyp)e2.stmt).paSubst);
        }
        if (e1.stmt != e2.stmt)
            return false;
        for (int i = 0; i < e1.child.length; i++)
            if (!unify(e1.child[i], e2.child[i]))
                return false;
        return true;
    }

    private ParseNode typeOf(final ParseNode proof) {
        final ParseNode root = proof.stmt.getExprParseTree().getRoot();
        assert root.stmt.getLabel().equals(OTConstants.HOL_TYPE_TERM);
        if (proof.stmt instanceof LogHyp)
            return root.child[0];
        return root.child[0].deepCloneApplyingAssrtSubst(
            ((Assrt)proof.stmt).getMandFrame().hypArray, proof.child, null);
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
            final WorkVar v = workVarManager.alloc((Cnst)logicalSystem
                .getSymTbl().get(OTConstants.HOL_TYPE_CNST));
            final ParseNode ga = c(v.getActiveVarHyp());
            final ParseNode f = getTypeProof(expr.child[0]);
            final ParseNode a = getTypeProof(expr.child[1]);
            final ParseNode b = getTypeProof(expr.child[2]);
            final ParseNode al = typeOf(a);
            final ParseNode be = typeOf(b);
            final boolean u = unify(
                typeOf(f),
                c(OTConstants.HOL_FUN_TYPE, al,
                    c(OTConstants.HOL_FUN_TYPE, be, ga)));
            assert u;

            out = c(OTConstants.HOL_OV_TYPE, al, be, ga, expr.child[0],
                expr.child[1], expr.child[2], f, a, b)
                .deepCloneApplyingWorkVarUpdates();
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
                out = cleanupWorkVars(out)));
        return out;
    }

    private ParseNode cleanupWorkVars(final ParseNode expr) {
        final List<WorkVar> workVarList = new ArrayList<WorkVar>();
        expr.accumSetOfWorkVarsUsed(workVarList);
        if (!workVarList.isEmpty()) {
            final Iterator<WorkVar> i = workVarList.iterator();
            WorkVar w = i.next();
            VarHyp wh = w.getActiveVarHyp();
            for (final VarHyp v : logicalSystem.getScopeDefList().get(0).scopeVarHyp)
                if (v.getTyp() == wh.getTyp()) {
                    wh.paSubst = c(v);
                    workVarManager.dealloc(w);
                    if (!i.hasNext()) {
                        w = null;
                        break;
                    }
                    w = i.next();
                    wh = w.getActiveVarHyp();
                }
            assert w == null;
            return expr.deepCloneApplyingWorkVarUpdates();
        }
        return expr;
    }

    private ParseNode cleanTypeProof(final ParseNode expr) {
        try {
            final ParseNode out = cleanupWorkVars(getTypeProof(expr));
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
        if (a.stmt != b.stmt)
            return false;
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
        if (wff.child[0].stmt.getLabel().equals(OTConstants.HOL_TRUE_TERM)) {
            final ParseNode r = target.child[0], a = target.child[1];
            return c(OTConstants.HOL_THM_A1I, r, a, cleanTypeProof(r), thm);
        }
        throw new UnsupportedOperationException();
    }
    public ParseNode proveSimp(final ParseNode wff) {
        final ParseNode r = wff.child[0], e1 = wff.child[1];
        if (e1.stmt.getLabel().equals(OTConstants.HOL_CT_TERM)) {
            final ParseNode s = e1.child[0];
            final ParseNode t = e1.child[1];
            return c(OTConstants.HOL_THM_JCA, r, s, t,
                proveSimp(c(wff.stmt, r, s)), proveSimp(c(wff.stmt, r, t)));
        }
        final ParseNode alpha = findEquivalent(r, e1,
            new HashMap<DeepKey, VarHyp>());
        if (!alpha.isDeepDup(e1)) {
            final ParseNode eq = c(OTConstants.HOL_OV_TERM,
                c(OTConstants.HOL_EQ_TERM), alpha, e1);
            return c(OTConstants.HOL_THM_SYLIB, r, alpha, e1,
                proveSimp(c(wff.stmt, r, alpha)),
                proveEq(c(wff.stmt, c(OTConstants.HOL_TRUE_TERM), eq)));
        }
        final List<Integer> obligations = new ArrayList<Integer>();
        for (final String s : OTConstants.simpThms) {
            final ParseNode n = testTheorem(wff, (Assrt)logicalSystem
                .getStmtTbl().get(s), obligations);
            if (n != null) {
                for (final int i : obligations)
                    n.child[i] = proveSimp(n.child[i]);
                return n;
            }
        }
        if (r.stmt.getLabel().equals(OTConstants.HOL_CT_TERM)) {
            String s = null;
            if (findEquivalent(r.child[1], e1, null) != null)
                s = OTConstants.HOL_THM_ADANTL;
            if (findEquivalent(r.child[0], e1, null) != null)
                s = OTConstants.HOL_THM_ADANTR;
            if (s == null)
                throw new UnsupportedOperationException();
            final ParseNode n = testTheorem(wff, (Assrt)logicalSystem
                .getStmtTbl().get(s), obligations);
            if (n != null) {
                for (final int i : obligations)
                    n.child[i] = proveSimp(n.child[i]);
                return n;
            }
        }
        throw new UnsupportedOperationException();
    }

    public ParseNode proveEq(final ParseNode wff) {
        final List<Integer> obligations = new ArrayList<Integer>();
        for (final String s : OTConstants.equalityThms) {
            final ParseNode n = testTheorem(wff, (Assrt)logicalSystem
                .getStmtTbl().get(s), obligations);
            if (n != null) {
                for (final int i : obligations)
                    n.child[i] = proveEq(n.child[i]);
                return n;
            }
        }
        return proveSimp(wff);
    }

    private ParseNode genSubst(final ParseNode proof,
        final Map<DeepKey, LogHyp> assign)
    {
        return genSubst(proof, assign, new ArrayDeque<DeepKey>(),
            new HashMap<ParseNode, ParseNode>());
    }
    private ParseNode genSubst(final ParseNode proof,
        final Map<DeepKey, LogHyp> assign, final Deque<DeepKey> boundVars,
        final Map<ParseNode, ParseNode> cache)
    {
        final ParseNode val = cache.get(proof);
        if (val != null)
            return val;
        boolean changed = false;
        final ParseNode[] newChild = new ParseNode[proof.child.length];
        int i = 0;
        final String s = proof.stmt.getLabel();
        ParseNode bound = null;
        if (s.equals(OTConstants.HOL_ABS_TERM))
            bound = c(OTConstants.HOL_VAR_TERM, proof.child[0], proof.child[1]);
        else if (s.equals(OTConstants.HOL_ABS_TYPE)
            || s.equals(OTConstants.HOL_THM_LEQ))
            bound = c(OTConstants.HOL_VAR_TERM, proof.child[0], proof.child[2]);
        if (bound != null)
            boundVars.push(new DeepKey(bound));
        for (final ParseNode n : proof.child) {
            if (n.stmt.getTyp().getId().equals(OTConstants.HOL_VAR_CNST)) {
                if (s.equals(OTConstants.HOL_VAR_TERM)) {
                    final DeepKey k = new DeepKey(proof);
                    if (boundVars.contains(k))
                        return proof;
                    final LogHyp newN = assign.get(k);
                    return newN == null ? proof : newN.getExprParseTree()
                        .getRoot().child[1];
                }
                if (s.equals(OTConstants.HOL_VAR_TYPE)) {
                    final DeepKey k = new DeepKey(c(OTConstants.HOL_VAR_TERM,
                        proof.child));
                    if (boundVars.contains(k))
                        return proof;
                    final LogHyp newN = assign.get(k);
                    return newN == null ? proof : c(newN);
                }
                if (s.equals(OTConstants.HOL_THM_BETA)) {
                    final ParseNode al = proof.child[0];
                    final ParseNode x = proof.child[2];
                    final DeepKey k = new DeepKey(c(OTConstants.HOL_VAR_TERM,
                        al, x));
                    final LogHyp newN = assign.get(k);
                    if (newN != null) {
                        final ParseNode be = proof.child[1];
                        boundVars.push(k);
                        final ParseNode a = genSubst(proof.child[3], assign,
                            boundVars, cache);
                        final ParseNode at = genSubst(proof.child[4], assign,
                            boundVars, cache);
                        boundVars.pop();
                        final ParseNode b = genSubst(proof.child[3], assign,
                            boundVars, cache);
                        final ParseNode c = newN.getExprParseTree().getRoot().child[1];
                        final ParseNode ct = c(newN);
                        final ParseNode eq = c(
                            OTConstants.HOL_PROOF_TERM,
                            c(OTConstants.HOL_OV_TERM,
                                c(OTConstants.HOL_EQ_TERM), k.value, c),
                            c(OTConstants.HOL_OV_TERM,
                                c(OTConstants.HOL_EQ_TERM), a, b));
                        return c(OTConstants.HOL_THM_CL, al, be, x, a, b, c,
                            at, ct, proveEq(eq));
                    }
                }
            }
            final ParseNode newN = genSubst(n, assign, boundVars, cache);
            if (newN == null)
                return null;
            if (boundVars.isEmpty())
                cache.put(n, newN);
            if (newN != n)
                changed = true;
            newChild[i++] = newN;
        }
        if (bound != null)
            boundVars.pop();
        if (changed)
            return c(proof.stmt, newChild);
        return proof;
    }
    public Theorem createTheoremGen(final String label, ParseNode wff,
        ParseNode proof)
    {
        try {
            final Set<DeepKey> freeVars = getFreeVars(wff);
            if (freeVars.isEmpty())
                return createTheorem(label, wff, proof);
            final HashMap<DeepKey, LogHyp> assign = new HashMap<DeepKey, LogHyp>();

            logicalSystem.beginScope();
            System.out.println("  ${");
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
                    System.out.println("    " + labelS + " $e "
                        + expr.toString() + " $.");
                    final LogHyp hyp = logicalSystem.addLogHyp(labelS,
                        expr.getSym());
                    hyp.setExprParseTree(new ParseTree(n));
                    assign.put(w, hyp);
                    activeTypeAx.put(v, hyp);
                    if (!i.hasNext()) {
                        w = null;
                        break;
                    }
                    w = i.next();
                }
            assert w == null;
            wff = genSubst(wff, assign);
            final Formula expr = verify.convertRPNToFormula(
                wff.convertToRPN(true), label);
            expr.setTyp((Cnst)logicalSystem.getSymTbl().get(
                OTConstants.HOL_PROOF_CNST));
            proof = genSubst(proof, assign);
            // generateDjVars(proof);
            System.out.println("    " + label + " $p " + expr + " $=");
            final RPNStep[] rpn = new ParseTree(proof).convertToRPN(false);
            System.out.print("  "
                + new GeneratedProofStmt(null, rpn, 7, 78).getStmtText()
                    .substring(2));
            final Theorem th = logicalSystem.addTheorem(label, 4,
                expr.getSym(), rpn, messages);
            final List<DjVars> list = new ArrayList<DjVars>();
            String err = verify.verifyOneProof(th, list);
            if (err != null || !list.isEmpty())
                hashCode();
            assert (err = verify.verifyOneProof(th, list)) == null : err;
            System.out.println("  $}\n");
            logicalSystem.endScope();
            activeTypeAx.clear();
            th.setExprParseTree(new ParseTree(wff));
            thmList.add(th);
            return th;
        } catch (final LangException e) {
            throw new IllegalStateException(e);
        }
    }
    private void generateDjVars(final ParseNode proof) {
        final List<VarHyp> varHypList = new ArrayList<VarHyp>();
        proof.accumVarHypUsedListBySeq(varHypList);

        try {
            for (final VarHyp v : varHypList)
                if (v.getTyp().getId().equals(OTConstants.HOL_VAR_CNST))
                    for (final VarHyp w : varHypList)
                        if (v != w)
                            logicalSystem.addDjVars(v.getVar(), w.getVar());
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
            System.out.println("  " + s);
    }
    public LogicalSystem getLogicalSystem() {
        return logicalSystem;
    }

    public ParseNode c(final String label, final ParseNode... child) {
        return c(logicalSystem.getStmtTbl().get(label), child);
    }
    public ParseNode c(final Stmt label, final ParseNode... child) {
        assert label.getMandHypArrayLength() == child.length : label;
        for (final ParseNode n : child)
            assert n != null;
        final ParseNode node = new ParseNode(label);
        node.child = child;
        return node;
    }

    private Term stripUniversalQuantifiers(Term t) {
        while (t instanceof AppTerm && t.asApp().getF() instanceof ConstTerm
            && t.asApp().getF().asConst().getConst().n.fullName().equals("!")
            && t.asApp().getX() instanceof AbsTerm)
            t = t.asApp().getX().asAbs().getB();
        return t;
    }

    public ParseNode findMatchingTheorem(final ParseNode expr,
        final List<Integer> obligations)
    {
        big: for (final Stmt s : thmList) {
            for (final LogHyp h : ((Assrt)s).getLogHypArray())
                if (h.getExprParseTree().getRoot().stmt.getLabel().equals(
                    OTConstants.HOL_PROOF_TERM))
                    continue big;

            final ParseNode n = testTheorem(expr, (Assrt)s, obligations);
            if (n != null)
                return n;
        }
        return null;
    }

    public ParseNode testTheorem(final ParseNode expr, final Assrt s,
        final List<Integer> obligations)
    {
        final VarHyp[] origVarHyps = s.getMandVarHypArray();
        final ParseNode[] origRepl = s
            .getExprParseTree()
            .getRoot()
            .unifyWithSubtree(expr, origVarHyps, unifyNodeStack,
                compareNodeStack);
        if (origRepl != null) {
            final List<VarHyp> varHyps = new ArrayList<VarHyp>();
            final Map<VarHyp, ParseNode> repl = new HashMap<VarHyp, ParseNode>();
            for (int i = 0; i < origVarHyps.length; i++) {
                varHyps.add(origVarHyps[i]);
                repl.put(origVarHyps[i], origRepl[i]);
            }
            for (final Hyp h : s.getMandFrame().hypArray)
                if (h instanceof VarHyp && !repl.containsKey(h))
                    repl.put((VarHyp)h, c(workVarManager.allocWorkVarHyp(h.getTyp())));
                    final ParseNode out = c(s,);
            for (final LogHyp h : s.getLogHypArray()) {
                final ParseNode r = h.getExprParseTree().getRoot();
                if (r.stmt.getLabel().equals(OTConstants.HOL_TYPE_TERM)) {
                    final ParseNode node = repl.get(r.child[1].stmt);
                    if (node != null) {
                        int len = varHyps.size();
                        r.child[0].accumVarHypUsedListBySeq(varHyps);
                        if (len == (len = varHyps.size()))
                            continue;
                        final VarHyp[] varHypArr = varHyps
                            .toArray(new VarHyp[len]);
                        try {
                            final ParseNode[] out = r.child[0]
                                .unifyWithSubtree(typeOf(cleanTypeProof(node)),
                                    varHypArr, unifyNodeStack, compareNodeStack);
                            for (int i = 0; i < len; i++)
                                if (out[i] != null) {
                                    final ParseNode old = repl.put(
                                        varHypArr[i], out[i]);
                                    if (old != null && !out[i].equals(old))
                                        return null;
                                }
                        } catch (final UnsupportedOperationException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                }
            }
            final Hyp[] hyps = s.getMandFrame().hypArray;
            final ParseNode[] child = new ParseNode[hyps.length];
            final VarHyp[] varHypArr = varHyps.toArray(new VarHyp[varHyps
                .size()]);
            final ParseNode[] replArr = new ParseNode[varHypArr.length];
            for (int i = 0; i < varHypArr.length; i++)
                replArr[i] = repl.get(varHypArr[i]);
            for (int i = 0; i < hyps.length; i++) {
                child[i] = hyps[i].getExprParseTree().getRoot()
                    .deepCloneApplyingAssrtSubst(varHypArr, replArr);
                if (child[i].stmt.getLabel().equals(OTConstants.HOL_TYPE_TERM))
                    child[i] = cleanTypeProof(child[i].child[1]);
                else if (child[i].stmt.getLabel().equals(
                    OTConstants.HOL_PROOF_TERM))
                    obligations.add(i);
            }
            return c(s, child);
        }
        return null;
    }
    public Assrt addThm(final Thm thm, final boolean auto) throws LangException
    {
        if (thm == null)
            return null;
        if (auto && !thm.ref.equals(OTConstants.ART_THM)) {
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
            final Stmt df = createAxiom(
                OTConstants.HOL_DEF_PFX + id,
                c(OTConstants.HOL_PROOF_TERM,
                    tru,
                    c(OTConstants.HOL_OV_TERM, c(OTConstants.HOL_EQ_TERM), ctc,
                        term)));
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
        final String label = prefix + "_" + thm.num;
        final Map<String, Stmt> stmtTbl = logicalSystem.getStmtTbl();
        final Stmt orig = stmtTbl.get(label);
        if (orig != null)
            return (Assrt)orig;
        final ParseNode node = thm.getExprProof(this);
        final Formula expr = verify.convertRPNToFormula(
            node.convertToRPN(true), label);
        System.out.println(expr);
        return createTheoremGen(label, node, thm.getProof(this, node));
    }
}
