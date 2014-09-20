// TODO: Name refactoring!
package mmj.transforms;

import java.util.*;
import java.util.Map.Entry;

import mmj.lang.*;
import mmj.pa.ProofStepStmt;
import mmj.transforms.WorksheetInfo.SubstParam;

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

    public ImplicationInfo(final EquivalenceInfo eqInfo,
        final List<Assrt> assrtList, final TrOutput output, final boolean dbg)
    {
        super(output, dbg);
        this.eqInfo = eqInfo;
        for (final Assrt assrt : assrtList)
            findImplicationRules(assrt);
        for (final Assrt assrt : assrtList)
            findAddFrefixRules(assrt);
        for (final Assrt assrt : assrtList)
            findTransitiveRules(assrt);

        for (final Stmt stmt : implOp.keySet())
            if (!eqInfo.isEquivalence(stmt))
                if (!addPrefixRules.containsKey(stmt))
                    output.errorMessage(
                        TrConstants.ERRMSG_MISSING_IMPL_TRIV_RULE, stmt,
                        implOp.get(stmt));
    }

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

    private void findAddFrefixRules(final Assrt assrt) {
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

    public ParseNode extractPrefixAndGetImplPart(final WorksheetInfo info) {
        final ParseNode root = info.derivStep.formulaParseTree.getRoot();
        for (final Entry<Stmt, Assrt> elem : implOp.entrySet()) {
            final Stmt stmt = elem.getKey();
            if (eqInfo.isEquivalence(stmt))
                continue; // it is not true implication construction.

            if (root.getStmt() == stmt) {
                assert root.getChild().length == 2;
                info.implPrefix = root.getChild()[0];
                info.implStatement = stmt;
                return root.getChild()[1];
            }
        }
        // The target derivation step is not an implication " ( ph -> ps )"
        return null;
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

}
