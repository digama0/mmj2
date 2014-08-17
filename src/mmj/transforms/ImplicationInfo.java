package mmj.transforms;

import java.util.*;

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

    public ImplicationInfo(final EquivalenceInfo eqInfo,
        final List<Assrt> assrtList, final TrOutput output, final boolean dbg)
    {
        super(output, dbg);
        this.eqInfo = eqInfo;
        for (final Assrt assrt : assrtList)
            findImplicationRules(assrt);
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

    // ------------------------------------------------------------------------
    // ----------------------------Transformations-----------------------------
    // ------------------------------------------------------------------------

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

    public void finishWithImplication(final WorksheetInfo info,
        final ProofStepStmt min, final ParseNode implNode, final Assrt majAssrt)
    {
        final SubstParam subst = getImplicationSubst(info, min, implNode,
            majAssrt);

        info.finishDerivationStep(subst.hypDerivArray, subst.assrt);
    }
    // ------------------------------------------------------------------------
    // ------------------------------Getters-----------------------------------
    // ------------------------------------------------------------------------

    public Assrt getEqImplication(final Cnst type) {
        return eqImplications.get(type);
    }

    public Assrt getImplOp(final Stmt op) {
        return implOp.get(op);
    }

    public boolean isImplOperator(final Stmt op) {
        return implOp.containsKey(op);
    }
}
