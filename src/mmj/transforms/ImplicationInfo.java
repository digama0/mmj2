//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import java.util.*;
import java.util.Map.Entry;

import mmj.lang.*;
import mmj.pa.ProofStepStmt;
import mmj.transforms.WorksheetInfo.SubstParam;

/**
 * This class is used for implication transformations.
 * <p>
 * Note: in theory we support several implication operators for one type. But it
 * wasn't tested.
 */
public class ImplicationInfo extends DBInfo {
    /** The information about equivalence rules */
    private final EquivalenceInfo eqInfo;

    /**
     * The map from type to equivalence implication rule: A & A <-> B => B.
     * set.mm has only one element: (wff, <->)
     */
    private final Map<Cnst, Assrt> eqImplications = new HashMap<>();

    /** The list of implication operators : A & A -> B => B. */
    private final Map<Stmt, Assrt> implOp = new HashMap<>();

    /** The list of transitive rules: A -> B & B -> C => A -> C */
    private final Map<Stmt, Assrt> implTrans = new HashMap<>();

    /** The list of rules to construct trivial implications : B => A -> B. */
    private final Map<Stmt, Assrt> addPrefixRules = new HashMap<>();

    /**
     * The list of distributive rules for implication and equality:
     * <p>
     * th -> (ph <-> ps) => (th -> ph) <-> (th -> ps)
     * <p>
     * It is constructed as a map : implication operator -> assertion
     */
    private final Map<Stmt, Assrt> distrRules = new HashMap<>();

    public ImplicationInfo(final EquivalenceInfo eqInfo,
        final List<Assrt> assrtList, final TrOutput output, final boolean dbg)
    {
        super(output, dbg);
        this.eqInfo = eqInfo;
        for (final Assrt assrt : assrtList)
            findImplicationRules(assrt);
        for (final Assrt assrt : assrtList)
            findAddPrefixRules(assrt);
        for (final Assrt assrt : assrtList)
            findTransitiveRules(assrt);

        for (final Stmt stmt : implOp.keySet())
            if (!eqInfo.isEquivalence(stmt))
                if (!addPrefixRules.containsKey(stmt))
                    output.errorMessage(
                        TrConstants.ERRMSG_MISSING_IMPL_TRIV_RULE, stmt,
                        implOp.get(stmt));

        for (final Assrt assrt : assrtList)
            findDistributiveRules(assrt);
    }

    private void findDistributiveRules(final Assrt assrt) {
        final ParseTree assrtTree = assrt.getExprParseTree();

        final LogHyp[] logHyps = assrt.getLogHypArray();
        if (logHyps.length != 1)
            return;

        final ParseNode root = assrtTree.getRoot();

        final Stmt eqStatement = root.stmt;

        if (!eqInfo.isEquivalence(eqStatement))
            return;

        final Stmt implStmt = root.child[0].stmt;

        if (!implOp.containsKey(implStmt))
            return;

        if (root.child[1].stmt != implStmt)
            return;

        if (!TrUtil.isVarNode(root.child[0].child[0]))
            return;
        if (!TrUtil.isVarNode(root.child[0].child[1]))
            return;
        if (!TrUtil.isVarNode(root.child[1].child[1]))
            return;

        if (root.child[0].child[0].stmt != root.child[1].child[0].stmt)
            return;
        if (root.child[0].child[1].stmt == root.child[1].child[1].stmt)
            return;

        final ParseNode log0Root = logHyps[0].getExprParseTree().getRoot();

        if (log0Root.stmt != implStmt)
            return;

        if (log0Root.child[0].stmt != root.child[0].child[0].stmt)
            return;

        if (log0Root.child[1].stmt != eqStatement)
            return;

        if (log0Root.child[1].child[0].stmt != root.child[0].child[1].stmt)
            return;

        if (log0Root.child[1].child[1].stmt != root.child[1].child[1].stmt)
            return;

        // We assume that there could be only one equivalence operator for every
        // type
        assert eqInfo.getEqStmt(root.child[0].stmt.getTyp()) == eqStatement;

        if (distrRules.containsKey(implStmt))
            return;

        output.dbgMessage(dbg, TrConstants.ERRMSG_IMPL_DISTR_ASSRTS, assrt,
            assrt.getFormula());

        distrRules.put(implStmt, assrt);
    }

    /**
     * Checks the assert for the form: A -> B & B -> C => A -> C
     *
     * @param assrt checked assert
     */
    private void findTransitiveRules(final Assrt assrt) {
        // Debug:

        final ParseTree assrtTree = assrt.getExprParseTree();

        final LogHyp[] logHyps = assrt.getLogHypArray();
        if (logHyps.length != 2)
            return;

        final ParseNode root = assrtTree.getRoot();

        final Stmt implStmt = root.stmt;

        if (!implOp.containsKey(implStmt))
            return;

        final ParseNode log0Root = logHyps[0].getExprParseTree().getRoot();
        final ParseNode log1Root = logHyps[1].getExprParseTree().getRoot();

        if (log0Root.stmt != implStmt)
            return;

        if (log1Root.stmt != implStmt)
            return;

        final Stmt stmtA = log0Root.child[0].stmt;
        final Stmt stmtB = log0Root.child[1].stmt;
        final Stmt stmtC = log1Root.child[1].stmt;

        if (!TrUtil.isVarStmt(stmtA))
            return;

        if (!TrUtil.isVarStmt(stmtB))
            return;

        if (!TrUtil.isVarStmt(stmtC))
            return;

        if (stmtA != root.child[0].stmt)
            return;

        if (stmtB != log1Root.child[0].stmt)
            return;

        if (stmtC != root.child[1].stmt)
            return;

        if (implTrans.containsKey(implStmt))
            return;

        output.dbgMessage(dbg, TrConstants.ERRMSG_IMPL_TRANS_ASSRTS, assrt,
            assrt.getFormula());

        implTrans.put(implStmt, assrt);
    }
    /**
     * Filters implication rules, like A & A -> B => B. Some of implications
     * could be equivalence operators (<-> in set.mm). Note: now we consider
     * only left to right direction. The rules in the form A & B <- A => B are
     * not supported! Also we don't support the reverse hypotheses order, for
     * example A -> B & A => B
     *
     * @param assrt the candidate
     */
    private void findImplicationRules(final Assrt assrt) {
        final ParseTree assrtTree = assrt.getExprParseTree();

        final LogHyp[] logHyps = assrt.getLogHypArray();
        if (logHyps.length != 2)
            return;

        if (assrtTree.getMaxDepth() != 1)
            return;

        final ParseNode resNode = assrtTree.getRoot();

        if (!TrUtil.isVarNode(resNode))
            return;

        final ParseNode log0Root = logHyps[0].getExprParseTree().getRoot();
        final ParseNode log1Root = logHyps[1].getExprParseTree().getRoot();

        final ParseNode preHyp;
        final ParseNode implHyp;
        if (TrUtil.isVarNode(log0Root)) {
            preHyp = log0Root;
            implHyp = log1Root;
        }
        else if (TrUtil.isVarNode(log1Root)) {
            preHyp = log1Root;
            implHyp = log0Root;
        }
        else
            return;

        if (implHyp.child.length != 2)
            return;

        if (implHyp.child[0].stmt != preHyp.stmt)
            return;

        if (implHyp.child[1].stmt != resNode.stmt)
            return;

        final Stmt stmt = implHyp.stmt;

        if (implOp.containsKey(stmt))
            return;

        if (preHyp != log0Root) {
            output.dbgMessage(dbg, TrConstants.ERRMSG_MP_BACKWARDS, assrt);
            return;
        }

        output.dbgMessage(dbg, TrConstants.ERRMSG_IMPL_ASSRTS, assrt,
            assrt.getFormula());
        implOp.put(stmt, assrt);

        if (!eqInfo.isEquivalence(stmt))
            return;

        final Cnst type = resNode.stmt.getTyp();

        if (eqImplications.containsKey(type))
            return;

        output.dbgMessage(dbg, TrConstants.ERRMSG_IMPL_EQ_ASSRTS, type, assrt);

        eqImplications.put(type, assrt);
    }

    private void findAddPrefixRules(final Assrt assrt) {
        // Debug: a1i

        final ParseTree assrtTree = assrt.getExprParseTree();

        final LogHyp[] logHyps = assrt.getLogHypArray();
        if (logHyps.length != 1)
            return;

        final ParseNode root = assrtTree.getRoot();

        final Stmt implStmt = root.stmt;

        if (!implOp.containsKey(implStmt))
            return;

        final ParseNode log0Root = logHyps[0].getExprParseTree().getRoot();
        if (!TrUtil.isVarNode(log0Root))
            return;

        assert root.child.length == 2;

        final ParseNode child0 = root.child[0];
        final ParseNode child1 = root.child[1];

        if (child1.stmt != log0Root.stmt)
            return;

        if (!TrUtil.isVarNode(child0))
            return;

        if (addPrefixRules.containsKey(implStmt))
            return;

        output.dbgMessage(dbg, TrConstants.ERRMSG_IMPL_TRIV_ASSRTS, assrt,
            assrt.getFormula());

        addPrefixRules.put(implStmt, assrt);
    }
    // ------------------------------------------------------------------------
    // ----------------------------Transformations-----------------------------
    // ------------------------------------------------------------------------

    /**
     * Apply implication rule: like A -> B & B -> C => A -> C.
     *
     * @param info the work sheet info
     * @param min the precondition (in the example it is statement A -> B)
     * @param implNode the implication part (in the example it is node C)
     * @param majAssrt the assert to construct B -> C step
     * @return the result of implication (in the example it is statement A -> C)
     */
    public ProofStepStmt applyTransitiveRule(final WorksheetInfo info,
        final ProofStepStmt min, final ParseNode implNode,
        final Assrt majAssrt)
    {
        // implication operator (in the example it is ->)
        final Stmt op = majAssrt.getExprParseTree().getRoot().stmt;

        final Assrt assrt = implTrans.get(op);

        assert assrt != null;

        final ParseNode hypRoot = min.formulaParseTree.getRoot();

        assert hypRoot.stmt == op;
        assert hypRoot.child.length == 2;

        final ParseNode hypPrefix = hypRoot.child[0];
        final ParseNode hypCore = hypRoot.child[1];

        // Create node A -> B
        final ParseNode majNode = TrUtil.createBinaryNode(op, hypCore,
            implNode);

        // |- A -> B
        final ProofStepStmt maj = info.getOrCreateProofStepStmt(majNode,
            new ProofStepStmt[]{}, majAssrt);

        final ProofStepStmt[] hypDerivArray = new ProofStepStmt[]{min, maj};
        final ParseNode r = maj.formulaParseTree.getRoot();

        final ParseNode stepImplRes = r.child[1];

        final ParseNode stepNode = TrUtil.createBinaryNode(op, hypPrefix,
            stepImplRes);

        final ProofStepStmt stepTr = info.getOrCreateProofStepStmt(stepNode,
            hypDerivArray, assrt);
        return stepTr;
    }

    /**
     * Finish implication rule: like A -> B & B -> C => A -> C.
     *
     * @param info the work sheet info
     * @param min the precondition (in the example it is statement A -> B)
     * @param implNode the implication part (in the example it is node C)
     * @param majAssrt the assert to construct B -> C step
     */
    public void finishTransitiveRule(final WorksheetInfo info,
        final ProofStepStmt min, final ParseNode implNode,
        final Assrt majAssrt)
    {
        // implication operator (in the example it is ->)
        final Stmt op = majAssrt.getExprParseTree().getRoot().stmt;

        final Assrt assrt = implTrans.get(op);

        assert assrt != null;

        final ParseNode hypRoot = min.formulaParseTree.getRoot();

        assert hypRoot.stmt == op;
        assert hypRoot.child.length == 2;

        final ParseNode hypPrefix = hypRoot.child[0];
        final ParseNode hypCore = hypRoot.child[1];

        assert hypPrefix.isDeepDup(info.implPrefix);
        final ParseNode r = info.derivStep.formulaParseTree.getRoot();
        assert hypPrefix.isDeepDup(r.child[0]);

        // Create node A -> B
        final ParseNode majNode = TrUtil.createBinaryNode(op, hypCore,
            implNode);

        // |- A -> B
        final ProofStepStmt maj = info.getOrCreateProofStepStmt(majNode,
            new ProofStepStmt[]{}, majAssrt);

        final ProofStepStmt[] hypDerivArray = new ProofStepStmt[]{min, maj};

        info.finishDerivationStep(hypDerivArray, assrt);
    }

    /**
     * Apply stub implication rule: like B => A -> B.
     *
     * @param info the work sheet info
     * @param core the hypothesis (B in the example)
     * @return the implication (A->B in the example)
     */
    public ProofStepStmt applyStubRule(final WorksheetInfo info,
        final ProofStepStmt core)
    {
        final Assrt assrt = addPrefixRules.get(info.implStatement);

        final ParseNode coreNode = core.formulaParseTree.getRoot();

        final ParseNode resRoot = TrUtil.createBinaryNode(info.implStatement,
            info.implPrefix, coreNode);

        final ProofStepStmt stepTr = info.getOrCreateProofStepStmt(resRoot,
            new ProofStepStmt[]{core}, assrt);
        return stepTr;
    }

    /**
     * Finish stub implication rule: like B => A -> B.
     *
     * @param info the work sheet info
     * @param core the hypothesis (B in the example)
     */
    public void finishStubRule(final WorksheetInfo info,
        final ProofStepStmt core)
    {
        final Assrt assrt = addPrefixRules.get(info.implStatement);

        info.finishDerivationStep(new ProofStepStmt[]{core}, assrt);
    }

    /**
     * Apply implication rule: like A & A -> B => B.
     *
     * @param info the work sheet info
     * @param min the precondition (in the example it is statement A)
     * @param maj the implication (in the example it is statement A -> B)
     * @param op implication operator (in the example it is ->)
     * @return the result of implication (in the example it is statement B)
     */
    public ProofStepStmt applyImplicationRule(final WorksheetInfo info,
        final ProofStepStmt min, final ProofStepStmt maj, final Stmt op)
    {
        final Assrt assrt = getImplOp(op);
        final ParseNode r = maj.formulaParseTree.getRoot();

        final ParseNode stepNode = r.child[1];

        final ProofStepStmt stepTr = info.getOrCreateProofStepStmt(stepNode,
            new ProofStepStmt[]{min, maj}, assrt);
        return stepTr;
    }

    /**
     * Apply implication rule: like A & A -> B => B.
     *
     * @param info the work sheet info
     * @param min the precondition (in the example it is statement A)
     * @param implNode the implication part (in the example it is node B)
     * @param majAssrt the assert to construct A -> B step
     * @return the result of implication (in the example it is statement B)
     */
    public ProofStepStmt applyImplicationRule(final WorksheetInfo info,
        final ProofStepStmt min, final ParseNode implNode,
        final Assrt majAssrt)
    {
        final SubstParam subst = getImplicationSubst(info, min, implNode,
            majAssrt);

        final ProofStepStmt maj = subst.hypDerivArray[1];
        final ParseNode r = maj.formulaParseTree.getRoot();
        final ParseNode stepNode = r.child[1];

        final ProofStepStmt stepTr = info.getOrCreateProofStepStmt(stepNode,
            subst.hypDerivArray, subst.assrt);
        return stepTr;
    }

    private SubstParam getImplicationSubst(final WorksheetInfo info,
        final ProofStepStmt min, final ParseNode implNode,
        final Assrt majAssrt)
    {
        // implication operator (in the example it is ->)
        final Stmt op = majAssrt.getExprParseTree().getRoot().stmt;

        final Assrt assrt = getImplOp(op);

        final ParseNode hypNode = min.formulaParseTree.getRoot();

        // Create node A -> B
        final ParseNode majNode = TrUtil.createBinaryNode(op, hypNode,
            implNode);

        // |- A -> B
        final ProofStepStmt maj = info.getOrCreateProofStepStmt(majNode,
            new ProofStepStmt[]{}, majAssrt);

        return new SubstParam(new ProofStepStmt[]{min, maj}, assrt);
    }

    /**
     * Completes implication derivation step with input hypotheses. For example
     * for "A & A -> B => B" it will complete target derivation step B with
     * hypothesis A and A -> B.
     *
     * @param info the work sheet info
     * @param min the precondition (in the example it is statement A)
     * @param implNode the implication part (in the example it is node B)
     * @param majAssrt the assert to construct A -> B step
     */
    public void finishWithImplication(final WorksheetInfo info,
        final ProofStepStmt min, final ParseNode implNode,
        final Assrt majAssrt)
    {
        final SubstParam subst = getImplicationSubst(info, min, implNode,
            majAssrt);

        info.finishDerivationStep(subst.hypDerivArray, subst.assrt);
    }

    public static class ExtractImplResult {
        public final ParseNode implPrefix;
        public final Stmt implStatement;
        public final ParseNode core;

        public ExtractImplResult(final ParseNode implPrefix,
            final Stmt implStatement, final ParseNode core)
        {
            this.implPrefix = implPrefix;
            this.implStatement = implStatement;
            this.core = core;
        }
    }

    /**
     * @param info the work sheet info
     * @return returns right part of implication (after ->)
     */
    public ExtractImplResult extractPrefixAndGetImplPart(
        final WorksheetInfo info)
    {
        final ParseNode root = info.derivStep.formulaParseTree.getRoot();
        for (final Entry<Stmt, Assrt> elem : implOp.entrySet()) {
            final Stmt stmt = elem.getKey();
            if (!isImplForPrefixOperator(stmt))
                continue; // it is not true implication construction.

            if (root.stmt == stmt) {
                assert root.child.length == 2;
                final ParseNode implPrefix = root.child[0];
                final Stmt implStatement = stmt;
                final ParseNode core = root.child[1];
                return new ExtractImplResult(implPrefix, implStatement, core);
            }
        }
        // The target derivation step is not an implication " ( ph -> ps )"
        return null;
    }

    /**
     * Apply implication rule (one or another pattern depend on hypothesis
     * form):
     * <p>
     * A -> B & B -> C => A -> C.
     * <p>
     * B & B -> C => C.
     *
     * @param info the work sheet info
     * @param hypGenStep the hypothesis (in the example it is B or "A -> B")
     * @param stepNode the result node (in the example it is C)
     * @param assrt the assertion to construct B -> C step
     * @return the result of implication (in the example it is statement C or
     *         "A -> C")
     */
    public GenProofStepStmt applyHyp(final WorksheetInfo info,
        final GenProofStepStmt hypGenStep, final ParseNode stepNode,
        final Assrt assrt)
    {
        if (!hypGenStep.hasPrefix()) {
            final ProofStepStmt r = applyImplicationRule(info,
                hypGenStep.getSimpleStep(), stepNode, assrt);
            return new GenProofStepStmt(r, null);
        }
        else {
            final ProofStepStmt r = applyTransitiveRule(info,
                hypGenStep.getImplicationStep(), stepNode, assrt);
            return new GenProofStepStmt(r, hypGenStep.getPrefix());
        }
    }

    public ProofStepStmt applyDisrtibutiveRule(final WorksheetInfo info,
        final ProofStepStmt hyp)
    {
        final ParseNode root = hyp.formulaParseTree.getRoot();
        final Stmt implStmt = root.stmt;
        assert isImplOperator(implStmt);
        final Stmt eqStmt = root.child[1].stmt;
        assert eqInfo.isEquivalence(eqStmt);

        final ParseNode precond = root.child[0];
        final ParseNode r = root.child[1];
        final ParseNode first = r.child[0];
        final ParseNode r1 = root.child[1];
        final ParseNode second = r1.child[1];

        final ParseNode stepNode = TrUtil.createBinaryNode(eqStmt,
            TrUtil.createBinaryNode(implStmt, precond, first),
            TrUtil.createBinaryNode(implStmt, precond, second));

        final Assrt distr = getDistributiveRule(implStmt);
        assert distr != null;

        final ProofStepStmt res = info.getOrCreateProofStepStmt(stepNode,
            new ProofStepStmt[]{hyp}, distr);

        return res;
    }
    // ------------------------------------------------------------------------
    // ------------------------------Getters-----------------------------------
    // ------------------------------------------------------------------------

    public Assrt getEqImplication(final Cnst type) {
        return eqImplications.get(type);
    }

    private Assrt getImplOp(final Stmt op) {
        return implOp.get(op);
    }

    public boolean isImplOperator(final Stmt op) {
        return implOp.containsKey(op);
    }

    public Assrt getStubImplication(final Stmt implOp) {
        return addPrefixRules.get(implOp);
    }

    public boolean isImplForPrefixOperator(final Stmt op) {
        return implOp.containsKey(op) && !eqInfo.isEquivalence(op);
    }

    public Collection<Stmt> getImplForPrefixOperators() {
        final Set<Stmt> res = new HashSet<>();
        for (final Stmt op : implOp.keySet())
            if (!eqInfo.isEquivalence(op))
                res.add(op);
        return res;
    }

    private Assrt getDistributiveRule(final Stmt implOp) {
        return distrRules.get(implOp);
    }
}
