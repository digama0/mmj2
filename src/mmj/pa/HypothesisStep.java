//********************************************************************/
//* Copyright (C) 2006, 2007, 2008                                   */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

// =================================================
// ===                   Class                   ===
// ===                                           ===
// ===      H y p o t h e s i s   S t e p        ===
// ===                                           ===
// =================================================

/*
 * HypothesisStep.java  0.07 08/01/2008
 *
 * Version 0.04: 06/01/2007
 *     - Un-nested inner class
 *
 * Version 0.05: 08/01/2007
 *     - Work Var Enhancement misc. changes.
 *
 * Nov-01-2007 Version 0.06
 * - Modify constructor used for proof export to accept
 *   a proofLevel parameter.
 *
 * Aug-01-2008 Version 0.07
 * - Remove stmtHasError() method.
 */

package mmj.pa;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import mmj.lang.*;
import mmj.mmio.MMIOException;
import mmj.mmio.Statementizer;

/**
 * HypothesisStep represents a Logical Hypothesis of the Theorem being proved.
 * <p>
 * On the GUI a HypothesisStep is shown with an 'h' prefix to step number.
 * <p>
 * A HypothesisStep cannot have Hyp's in its Step/Hyp/Ref field. Nor does it
 * have a proof, just a parse tree.
 */
public class HypothesisStep extends ProofStepStmt {

    /**
     * Default Constructor.
     *
     * @param w the owning ProofWorksheet
     */
    public HypothesisStep(final ProofWorksheet w) {
        super(w);
    }

    /**
     * Constructor for incomplete DerivationStep destined only for output to the
     * GUI.
     * <p>
     * Creates "incomplete" HypothesisStep which is destined only for output to
     * the GUI, hence, the object references, etc. are not loaded. After display
     * to the GUI this worksheet disappears -- recreated via "load" each time
     * the user selects "StartUnification".
     *
     * @param w the owning ProofWorksheet
     * @param step step number of the proof step
     * @param refLabel Ref label of the proof step
     * @param formula the proof step formula
     * @param parseTree formula ParseTree (can be null)
     * @param setCaret true means position caret of TextArea to this statement.
     * @param proofLevel level of step in proof.
     */
    public HypothesisStep(final ProofWorksheet w, final String step,
        final String refLabel, final Formula formula, final ParseTree parseTree,
        final boolean setCaret, final int proofLevel)
    {

        super(w, step, refLabel, formula, setCaret);

        this.proofLevel = proofLevel;

        updateFormulaParseTree(parseTree);

        stmtText = buildStepHypRefSB();
        lineCnt = loadStmtText(stmtText, formula, parseTree);
    }

    @Override
    public boolean stmtIsIncomplete() {
        return false;
    }

    /**
     * Compares input Ref label to this step.
     *
     * @param newRefLabel ref label to compare
     * @return true if equal, false if not equal.
     */
    @Override
    public boolean hasMatchingRefLabel(final String newRefLabel) {
        return getRefLabel().equals(newRefLabel);
    }

    @Override
    public void renum(final Map<String, String> renumberMap) {

        final String newNum = renumberMap.get(getStep());
        if (newNum != null) {
            setStep(newNum);
            reloadStepHypRefInStmtText();
        }
    }

    /**
     * Updates the Step/Hyp/Ref field in the statement text area of the proof
     * step.
     * <p>
     * This is needed because Unify can alter Hyp and Ref.
     */
    @Override
    public void reloadStepHypRefInStmtText() {
        reviseStepHypRefInStmtText(buildStepHypRefSB());
    }

    /**
     * Reformats Hypothesis Step using TMFF.
     * <p>
     * Note: This isn't guaranteed to work unless the Proof Worksheet is free of
     * structural errors. Also, a formula which has not yet been parsed or which
     * failed to parse successfully will contain a null parse tree and be
     * formatted using Format 0 - "Unformatted".
     */
    @Override
    public void tmffReformat() {
        stmtText = buildStepHypRefSB();
        lineCnt = loadStmtText(stmtText, getFormula(), formulaParseTree);
    }

    /**
     * Loads HypothesisStep with step and ref.
     * <p>
     * This method is passed the contents of the first token which has already
     * been parsed into step, and ref fields and loads them into the
     * HypothesisStep -- which already contains the step's formula! This
     * back-asswardness is a result of trying to maintain cursor/caret control
     * when the formula is validated. Messy...
     *
     * @param origStepHypRefLength length of first token of statement
     * @param lineStartCharNbr character number in the input stream of the
     *            statement start
     * @param stepField step number field
     * @param refField step ref field
     * @return first token of next statement.
     * @throws IOException if an error occurred
     * @throws MMIOException if an error occurred
     * @throws ProofAsstException if an error occurred
     */
    public String loadHypothesisStep(final int origStepHypRefLength,
        final int lineStartCharNbr, final String stepField,
        final String refField)
            throws IOException, MMIOException, ProofAsstException
    {

        // update ProofStepStmt fields
        setStep(stepField);
        setRefLabel(refField);

        final String nextT = loadStmtTextWithFormula(false); // false =
                                                             // !workVarsOk

        final Stmt hyp = getFormula() == null && refField != null
            ? w.logicalSystem.getStmtTbl().get(getRefLabel()) : null;
        if (hyp instanceof LogHyp) {
            setRef(hyp);
            setFormula(hyp.getFormula());
        }

        if (w.isNewTheorem()) {
            getValidNewTheoremLogHypRef(lineStartCharNbr);
            getNewFormulaStepParseTree();
        }
        else {
            getValidOldTheoremLogHypRef(lineStartCharNbr);
            formulaParseTree = getRef().getExprParseTree();
        }

        if (hyp instanceof LogHyp)
            tmffReformat();
        else
            loadStepHypRefIntoStmtText(origStepHypRefLength,
                buildStepHypRefSB());

        return nextT;
    }

    /**
     * <code>
     * If Ref field input
     *     see if it matches one of theorem's log hyps
     *     if no match, throw exception
     *     if match, compare log hyp formula to
     *       proof step formula,
     *     if formulas not equal, throw exception
     * else (Ref field not input)
     *     look up Ref label using formula
     *     if not found, throw exception
     * end-if
     * save Ref stmt
     * </code>
     *
     * @param lineStartCharNbr character number in the input stream of the
     *            statement start
     * @throws ProofAsstException if an error occurred
     */
    private void getValidOldTheoremLogHypRef(final int lineStartCharNbr)
        throws ProofAsstException
    {
        setRef(null);
        final LogHyp[] logHypArray = w.theorem.getLogHypArray();
        if (getRefLabel() == null) {
            if (getFormula() != null)
                for (final LogHyp element : logHypArray)
                    if (getFormula().equals(element.getFormula())) {
                        if (dupLogHypFormulas(getFormula()))
                            w.triggerLoadStructureException(
                                (int)w.proofTextTokenizer.getCurrentCharNbr()
                                    + 1 - lineStartCharNbr,
                                PaConstants.ERRMSG_DUP_LOG_HYPS,
                                w.getErrorLabelIfPossible(), getStep());
                        setRef(element);
                        setRefLabel(getRef().getLabel());
                    }
            if (getRef() == null)
                w.triggerLoadStructureException(
                    (int)w.proofTextTokenizer.getCurrentCharNbr() + 1
                        - lineStartCharNbr,
                    PaConstants.ERRMSG_HYP_FORMULA_ERR,
                    w.getErrorLabelIfPossible(), getStep());
        }
        else {
            // refLabel was input
            setRef(w.logicalSystem.getStmtTbl().get(getRefLabel()));
            if (getRef() == null)
                w.triggerLoadStructureException(
                    (int)w.proofTextTokenizer.getCurrentCharNbr() + 1
                        - lineStartCharNbr,
                    PaConstants.ERRMSG_REF_NOTFND2, w.getErrorLabelIfPossible(),
                    getStep(), getRefLabel());
            if (!(getRef() instanceof LogHyp))
                w.triggerLoadStructureException(
                    (int)w.proofTextTokenizer.getCurrentCharNbr() + 1
                        - lineStartCharNbr,
                    PaConstants.ERRMSG_REF_NOT_LOGHYP,
                    w.getErrorLabelIfPossible(), getStep(), getRefLabel());
            if (!Arrays.asList(logHypArray).contains(getRef()))
                w.triggerLoadStructureException(
                    (int)w.proofTextTokenizer.getCurrentCharNbr() + 1
                        - lineStartCharNbr,
                    PaConstants.ERRMSG_LOGHYP_MISMATCH,
                    w.getErrorLabelIfPossible(), getStep(), getRefLabel());
            if (!getRef().getFormula().equals(getFormula()))
                w.triggerLoadStructureException(
                    (int)w.proofTextTokenizer.getCurrentCharNbr() + 1
                        - lineStartCharNbr,
                    PaConstants.ERRMSG_HYP_FORMULA_ERR2,
                    w.getErrorLabelIfPossible(), getStep(), getRefLabel(),
                    getRef().getFormula());
        }
        checkDupHypRefLabel(lineStartCharNbr);
    }

    private boolean dupLogHypFormulas(final Formula f) {
        for (final ProofWorkStmt x : w.proofWorkStmtList)
            if (x instanceof HypothesisStep)
                if (((HypothesisStep)x).getFormula().equals(f))
                    return true;
        return false;
    }

    /**
     * <code>
     * If Ref field not input,
     *     generates ref field as theoremLabel
     *        + "." + sequence number of hyp
     * End-if
     *
     * See if it is a dup of another proof step ref
     * If dup, throw exception
     *
     * See if it already exists in database
     * If exists, throw exception.
     *
     * See if it is a valid label according to
     * the Metamath.pdf spec -- not on the prohibited
     * list and no offending characters...
     * </code>
     *
     * @param lineStartCharNbr character number in the input stream of the
     *            statement start
     * @throws ProofAsstException if an error occurred
     */
    private void getValidNewTheoremLogHypRef(final int lineStartCharNbr)
        throws ProofAsstException
    {
        setRef(null);
        if (getRefLabel() == null)
            setRefLabel(
                w.getTheoremLabel() + "." + Integer.toString(w.hypStepCnt + 1));

        checkDupHypRefLabel(lineStartCharNbr);

        if (!Statementizer.isValidLabel(getRefLabel()))
            w.triggerLoadStructureException(
                (int)w.proofTextTokenizer.getCurrentCharNbr() + 1
                    - lineStartCharNbr,
                PaConstants.ERRMSG_REF_PROHIB, w.getErrorLabelIfPossible(),
                getStep(), getRefLabel());
        if (w.logicalSystem.getSymTbl().containsKey(getRefLabel()))
            w.triggerLoadStructureException(
                (int)w.proofTextTokenizer.getCurrentCharNbr() + 1
                    - lineStartCharNbr,
                PaConstants.ERRMSG_STMT_LABEL_DUP_OF_SYM_ID2,
                w.getErrorLabelIfPossible(), getStep(), getRefLabel());

        setRef(w.logicalSystem.getStmtTbl().get(getRefLabel()));
        if (getRef() == null)
            return;

        if (!(getRef() instanceof LogHyp))
            w.triggerLoadStructureException(
                (int)w.proofTextTokenizer.getCurrentCharNbr() + 1
                    - lineStartCharNbr,
                PaConstants.ERRMSG_REF_NOT_LOGHYP, w.getErrorLabelIfPossible(),
                getStep(), getRefLabel());
        if (!getRef().getFormula().equals(getFormula()))
            w.triggerLoadStructureException(
                (int)w.proofTextTokenizer.getCurrentCharNbr() + 1
                    - lineStartCharNbr,
                PaConstants.ERRMSG_HYP_FORMULA_ERR2,
                w.getErrorLabelIfPossible(), getStep(), getRefLabel(),
                getRef().getFormula());
    }

    private void checkDupHypRefLabel(final int lineStartCharNbr)
        throws ProofAsstException
    {

        for (final ProofWorkStmt x : w.proofWorkStmtList)
            if (x.hasMatchingRefLabel(getRefLabel()))
                w.triggerLoadStructureException(
                    (int)w.proofTextTokenizer.getCurrentCharNbr() + 1
                        - lineStartCharNbr,
                    PaConstants.ERRMSG_DUP_HYP_REF, w.getErrorLabelIfPossible(),
                    getStep(), getRefLabel());
    }

    private StringBuilder buildStepHypRefSB() {

        final StringBuilder sb = new StringBuilder();

        sb.append(PaConstants.HYP_STEP_PREFIX);
        sb.append(getStep());
        sb.append(PaConstants.FIELD_DELIMITER_COLON);
        sb.append(PaConstants.FIELD_DELIMITER_COLON);
        if (getRefLabel() != null)
            sb.append(getRefLabel());
        return sb;
    }

    @Override
    public String toString() {
        return "h" + getStep() + "::" + getRefLabel() + " " + getFormula();
    }
}
