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
    private final Map<Cnst, Assrt> eqImplications = new HashMap<Cnst, Assrt>();

    /** The list of implication operators : A & A -> B => B. */
    private final Map<Stmt, Assrt> implOp = new HashMap<Stmt, Assrt>();

    /** The list of transitive rules: A -> B & B -> C => A -> C */
    private final Map<Stmt, Assrt> implTrans = new HashMap<Stmt, Assrt>();

    /** The list of rules to construct trivial implications : B => A -> B. */
    private final Map<Stmt, Assrt> addPrefixRules = new HashMap<Stmt, Assrt>();

    /**
     * The list of distributive rules for implication and equality:
     * <p>
     * th -> (ph <-> ps) => (th -> ph) <-> (th -> ps)
     * <p>
     * It is constructed as a map : implication operator -> assertion
     */
    private final Map<Stmt, Assrt> distrRules = new HashMap<Stmt, Assrt>();

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

        for (final Stmt imOp : implOp.keySet())
            if (!eqInfo.isEquivalence(imOp) && !distrRules.containsKey(imOp))
                output.errorMessage(TrConstants.ERRMSG_MISSING_IMPL_DISRT_RULE,
                    imOp, implOp.get(imOp));
    }

    private void findDistributiveRules(final Assrt assrt) {
        final ParseTree assrtTree = assrt.getExprParseTree();

        final LogHyp[] logHyps = assrt.getLogHypArray();
        if (logHyps.length != 1)
            return;

        final ParseNode root = assrtTree.getRoot();

        final Stmt eqStatement = root.getStmt();

        if (!eqInfo.isEquivalence(eqStatement))
            return;

        final Stmt implStmt = root.getChild()[0].getStmt();

        if (!implOp.containsKey(implStmt))
            return;

        if (root.getChild()[1].getStmt() != implStmt)
            return;

        if (!TrUtil.isVarNode(root.getChild()[0].getChild()[0]))
            return;
        if (!TrUtil.isVarNode(root.getChild()[0].getChild()[1]))
            return;
        if (!TrUtil.isVarNode(root.getChild()[1].getChild()[1]))
            return;

        if (root.getChild()[0].getChild()[0].getStmt() != root.getChild()[1]
            .getChild()[0].getStmt())
            return;
        if (root.getChild()[0].getChild()[1].getStmt() == root.getChild()[1]
            .getChild()[1].getStmt())
            return;

        final ParseNode log0Root = logHyps[0].getExprParseTree().getRoot();

        if (log0Root.getStmt() != implStmt)
            return;

        if (log0Root.getChild()[0].getStmt() != root.getChild()[0].getChild()[0]
            .getStmt())
            return;

        if (log0Root.getChild()[1].getStmt() != eqStatement)
            return;

        if (log0Root.getChild()[1].getChild()[0].getStmt() != root.getChild()[0]
            .getChild()[1].getStmt())
            return;

        if (log0Root.getChild()[1].getChild()[1].getStmt() != root.getChild()[1]
            .getChild()[1].getStmt())
            return;

        // We assume that there could be only one equivalence operator for every
        // type
        assert eqInfo.getEqStmt(root.getChild()[0].getStmt().getTyp()) == eqStatement;

        if (distrRules.containsKey(implStmt))
            return;

        output.dbgMessage(dbg,
            "I-TR-DBG distributive rule for implication: %s: %s", assrt,
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

        final Stmt implStmt = root.getStmt();

        if (!implOp.containsKey(implStmt))
            return;

        final ParseNode log0Root = logHyps[0].getExprParseTree().getRoot();
        final ParseNode log1Root = logHyps[1].getExprParseTree().getRoot();

        if (log0Root.getStmt() != implStmt)
            return;

        if (log1Root.getStmt() != implStmt)
            return;

        final Stmt stmtA = log0Root.getChild()[0].getStmt();
        final Stmt stmtB = log0Root.getChild()[1].getStmt();
        final Stmt stmtC = log1Root.getChild()[1].getStmt();

        if (!TrUtil.isVarStmt(stmtA))
            return;

        if (!TrUtil.isVarStmt(stmtB))
            return;

        if (!TrUtil.isVarStmt(stmtC))
            return;

        if (stmtA != root.getChild()[0].getStmt())
            return;

        if (stmtB != log1Root.getChild()[0].getStmt())
            return;

        if (stmtC != root.getChild()[1].getStmt())
            return;

        if (implTrans.containsKey(implStmt))
            return;

        output.dbgMessage(dbg, "I-TR-DBG implication transitive rule: %s: %s",
            assrt, assrt.getFormula());

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

        if (implHyp.getChild().length != 2)
            return;

        if (implHyp.getChild()[0].getStmt() != preHyp.getStmt())
            return;

        if (implHyp.getChild()[1].getStmt() != resNode.getStmt())
            return;

        final Stmt stmt = implHyp.getStmt();

        if (implOp.containsKey(stmt))
            return;

        if (preHyp != log0Root) {
            output.dbgMessage(dbg,
                "I-TR-DBG the current implementation doesn't support A->B & A"
                    + " hypotheses order, assert %s", assrt);
            return;
        }

        output.dbgMessage(dbg, "I-TR-DBG implication assrt: %s: %s", assrt,
            assrt.getFormula());
        implOp.put(stmt, assrt);

        if (!eqInfo.isEquivalence(stmt))
            return;

        final Cnst type = resNode.getStmt().getTyp();

        if (eqImplications.containsKey(type))
            return;

        output.dbgMessage(dbg, "I-TR-DBG implication equal assrt: %s: %s",
            type, assrt);

        eqImplications.put(type, assrt);
    }

    private void findAddPrefixRules(final Assrt assrt) {
        // Debug: a1i

        final ParseTree assrtTree = assrt.getExprParseTree();

        final LogHyp[] logHyps = assrt.getLogHypArray();
        if (logHyps.length != 1)
            return;

        final ParseNode root = assrtTree.getRoot();

        final Stmt implStmt = root.getStmt();

        if (!implOp.containsKey(implStmt))
            return;

        final ParseNode log0Root = logHyps[0].getExprParseTree().getRoot();
        if (!TrUtil.isVarNode(log0Root))
            return;

        assert root.getChild().length == 2;

        final ParseNode child0 = root.getChild()[0];
        final ParseNode child1 = root.getChild()[1];

        if (child1.getStmt() != log0Root.getStmt())
            return;

        if (!TrUtil.isVarNode(child0))
            return;

        if (addPrefixRules.containsKey(implStmt))
            return;

        output.dbgMessage(dbg, "I-TR-DBG implication trivial rule: %s: %s",
            assrt, assrt.getFormula());

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
        final ProofStepStmt min, final ParseNode implNode, final Assrt majAssrt)
    {
        // implication operator (in the example it is ->)
        final Stmt op = majAssrt.getExprParseTree().getRoot().getStmt();

        final Assrt assrt = implTrans.get(op);

        assert assrt != null;

        final ParseNode hypRoot = min.formulaParseTree.getRoot();

        assert hypRoot.getStmt() == op;
        assert hypRoot.getChild().length == 2;

        final ParseNode hypPrefix = hypRoot.getChild()[0];
        final ParseNode hypCore = hypRoot.getChild()[1];

        // Create node A -> B
        final ParseNode majNode = TrUtil
            .createBinaryNode(op, hypCore, implNode);

        // |- A -> B
        final ProofStepStmt maj = info.getOrCreateProofStepStmt(majNode,
            new ProofStepStmt[]{}, majAssrt);

        final ProofStepStmt[] hypDerivArray = new ProofStepStmt[]{min, maj};

        final ParseNode stepImplRes = maj.formulaParseTree.getRoot().getChild()[1];

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
        final ProofStepStmt min, final ParseNode implNode, final Assrt majAssrt)
    {
        // implication operator (in the example it is ->)
        final Stmt op = majAssrt.getExprParseTree().getRoot().getStmt();

        final Assrt assrt = implTrans.get(op);

        assert assrt != null;

        final ParseNode hypRoot = min.formulaParseTree.getRoot();

        assert hypRoot.getStmt() == op;
        assert hypRoot.getChild().length == 2;

        final ParseNode hypPrefix = hypRoot.getChild()[0];
        final ParseNode hypCore = hypRoot.getChild()[1];

        assert hypPrefix.isDeepDup(info.implPrefix);
        assert hypPrefix.isDeepDup(info.derivStep.formulaParseTree.getRoot()
            .getChild()[0]);

        // Create node A -> B
        final ParseNode majNode = TrUtil
            .createBinaryNode(op, hypCore, implNode);

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

        final ParseNode stepNode = maj.formulaParseTree.getRoot().getChild()[1];

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
        final ProofStepStmt min, final ParseNode implNode, final Assrt majAssrt)
    {
        final SubstParam subst = getImplicationSubst(info, min, implNode,
            majAssrt);

        final ProofStepStmt maj = subst.hypDerivArray[1];
        final ParseNode stepNode = maj.formulaParseTree.getRoot().getChild()[1];

        final ProofStepStmt stepTr = info.getOrCreateProofStepStmt(stepNode,
            subst.hypDerivArray, subst.assrt);
        return stepTr;
    }

    private SubstParam getImplicationSubst(final WorksheetInfo info,
        final ProofStepStmt min, final ParseNode implNode, final Assrt majAssrt)
    {
        // implication operator (in the example it is ->)
        final Stmt op = majAssrt.getExprParseTree().getRoot().getStmt();

        final Assrt assrt = getImplOp(op);

        final ParseNode hypNode = min.formulaParseTree.getRoot();

        // Create node A -> B
        final ParseNode majNode = TrUtil
            .createBinaryNode(op, hypNode, implNode);

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
        final ProofStepStmt min, final ParseNode implNode, final Assrt majAssrt)
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

            if (root.getStmt() == stmt) {
                assert root.getChild().length == 2;
                final ParseNode implPrefix = root.getChild()[0];
                final Stmt implStatement = stmt;
                final ParseNode core = root.getChild()[1];
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
        final Stmt implStmt = root.getStmt();
        assert isImplOperator(implStmt);
        final Stmt eqStmt = root.getChild()[1].getStmt();
        assert eqInfo.isEquivalence(eqStmt);

        final ParseNode precond = root.getChild()[0];
        final ParseNode first = root.getChild()[1].getChild()[0];
        final ParseNode second = root.getChild()[1].getChild()[1];

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
        final Set<Stmt> res = new HashSet<Stmt>();
        for (final Stmt op : implOp.keySet())
            if (!eqInfo.isEquivalence(op))
                res.add(op);
        return res;
    }

    private Assrt getDistributiveRule(final Stmt implOp) {
        return distrRules.get(implOp);
    }
}
