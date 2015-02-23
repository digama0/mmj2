package mmj.oth;

import java.io.*;
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
    final WorkVarManager workVarManager;
    final Messages messages;
    public final String prefix;
    private final Map<Cnst, Stmt> termMap;
    private final Map<Cnst, Stmt> typeMap;
    final Map<VarHyp, LogHyp> activeTypeAx;
    private final Map<Cnst, Stmt> cnstMap;
    private final Map<Cnst, Stmt> defnMap;
    private final Map<Assrt, Map<VarHyp, Set<DeepKey>>> boundVarAnalysis;
    final List<Assrt> thmList;

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
            log("  $[ set-hol.mm $]\n");
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
        boundVarAnalysis = new HashMap<Assrt, Map<VarHyp, Set<DeepKey>>>();
        final List<Stmt> stmtList = new ArrayList<Stmt>(l.getStmtTbl().values());
        Collections.sort(stmtList, MObj.SEQ);
        for (final Stmt s : stmtList)
            if (s instanceof Assrt && s.getTyp().isProvableLogicStmtTyp()
                && !OTConstants.avoidThms.contains(s.getLabel()))
            {
                // thmList.add((Assrt)s);
                final ParseNode r = s.getExprParseTree().getRoot();
                if (r.stmt.getLabel().equals(OTConstants.SET_IM_WFF)
                    && r.child[1].stmt.getLabel()
                        .equals(OTConstants.SET_EL_WFF))
                {
                    final Stmt s2 = r.child[1].child[0].stmt;
                    if (s2.getTyp().getId().equals(OTConstants.SET_CLS_CNST)
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
        for (final Entry<String, String> e : OTConstants.termOverrides
            .entrySet())
        {
            final Stmt s2 = logicalSystem.getStmtTbl().get(e.getKey());
            final Sym sym = s2.getFormula().getSym()[1];
            typeMap
                .put((Cnst)sym, logicalSystem.getStmtTbl().get(e.getValue()));
            termMap.put((Cnst)sym, s2);
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

    Set<DeepKey> getFreeVars(final ParseNode r) {
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

    boolean unify(final ParseNode e1, final ParseNode e2) {
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

    ParseNode cleanupWorkVars(final ParseNode expr, final String typ) {
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

    private final boolean debugging = false;

    void generateDjVars(final ParseNode wff, final ParseNode proof) {
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

    public ParseNode makeDed(final ParseNode a) {
        return c(OTConstants.SET_IM_WFF, c(OTConstants.SET_PH_VAR), a);
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
            final ProofContext pc = new ProofContext(this,
                OTConstants.HOL_TYPE_PFX + id);
            final ParseNode ctc = c(tc), tru = c(OTConstants.HOL_TRUE_TERM), term = t
                .getTermProof(pc), typ = t.getType().getTypeProof(pc);
            termMap.put(cnst, tc);
            final Stmt df = createAxiom(OTConstants.HOL_DEF_PFX + id,
                c(OTConstants.HOL_PROOF_TERM, tru, makeEq(ctc, term)));
            defnMap.put(cnst, df);
            final VarHyp[] hyps = df.getMandVarHypArray();
            final ParseNode[] vars = new ParseNode[hyps.length];
            for (int i = 0; i < hyps.length; i++)
                vars[i] = c(hyps[i]);
            final Stmt wff = pc.createTheorem(
                c(OTConstants.HOL_TYPE_TERM, typ, c(tc)),
                c(OTConstants.HOL_THM_EQTYPRI, typ, tru, term, ctc,
                    t.getTypeProof(pc, null), c(df, vars)));
            typeMap.put(cnst, wff);
            throw new UnsupportedOperationException();
        }
        if (thm.num == 9633)
            hashCode();
        final String label = prefix + "_" + thm.num;
        final Map<String, Stmt> stmtTbl = logicalSystem.getStmtTbl();
        final Stmt orig = stmtTbl.get(label);
        if (orig != null)
            return (Assrt)orig;
        final ProofContext pc = new ProofContext(this, label);
        final ParseNode node = pc.loadThm(thm);
        verify.convertRPNToFormula(node.convertToRPN(true), label);
        thms++;
        return pc.createTheorem(node, thm.getProof(pc, node));
    }

    int thms = 0;
}
