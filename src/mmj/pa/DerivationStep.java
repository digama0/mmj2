//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

// =================================================
// ===                   Class                   ===
// ===                                           ===
// ===      D e r i v a t i o n   S t e p        ===
// ===                                           ===
// =================================================

/*
 * DerivationStep.java  0.09 08/01/2008
 *
 * Version 0.04: 06/01/2007
 *     - Un-nested inner class
 *     - replace ProofworkStmt.status
 *     - Do not require use of "?" to invoke
 *       deriveStepHyps; the mere fact of a
 *       Ref entry and fewer Hyps entered than
 *       needed by the Ref is sufficient.
 *       Also, if a Ref is entered and the
 *       correct number of Hyps is entered,
 *       any "?" or "" Hyp entries are ignored
 *       (e.g. if "1,2,?" entered and 2 hyps are
 *       required, then "1,2" is used and the
 *       "?" is deleted by the program.)
 *
 * Version 0.05: 08/01/2007
 *     - Work Var Enhancement misc. changes.
 *
 * Version 0.06: 11/01/2007
 *     - Add loadDerivationStepHypLevels() to compute
 *       proofLevel number for hypotheses of DerivationStep.
 *     - Modify constructor used for proof export to accept
 *       a proofLevel parameter.
 *     - Add new loadLocalRefDerivationStep() routine.
 *     - Add ERRMSG_BAD_LOCAL_REF_1, "E-PA-0379"
 *     - Made a separate routine out of
 *       reloadLogHypKeysAndMaxDepth() to recompute when
 *       Hyps change.
 *
 * Version 0.07: 02/01/2008
 *     - Modify loadGeneratedFormulaIntoDerivStep() to
 *       include new boolean "stmtTextAlreadyLoaded"
 *       for use with the new ProofAsstAutoReformat RunParm
 *       option (handled up in ProofUnifier).
 *
 * Version 0.08: 03/01/2008
 *     - Remove Hints feature
 *     - add sortedHypArray and get/set/resetSortedHypArray(),
 *       and makes sure that resetSortedHypArray() is called
 *       to reset the array to null whenever/wherever
 *       formulas are updated (see computeLogHypsL1HiLoKey()
 *       and loadGeneratedFormulaIntoDerivStep() via
 *       ProofUnifier.) In some cases multiple sorts
 *       of the same hyps will be avoided -- it is hoped.
 *
 * Version 0.09: 08/01/2008
 *     - add getHyp() so that logical hypotheses of a step
 *       can be obtained.
 */

package mmj.pa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mmj.lang.Assrt;
import mmj.lang.DjVars;
import mmj.lang.Formula;
import mmj.lang.LangException;
import mmj.lang.ParseNode;
import mmj.lang.ParseTree;
import mmj.lang.Stmt;
import mmj.lang.VarHyp;
import mmj.lang.WorkVar;
import mmj.mmio.MMIOError;
import mmj.util.DelimitedTextParser;

/**
 * DerivationStep represents proof steps that derive a new formula.
 */
public class DerivationStep extends ProofStepStmt {

    private ProofStepStmt[] hyp;
    private ProofStepStmt[] sortedHypArray;

    private String[] hypStep;

    private String logHypsL1HiLoKey;
    private int logHypsMaxDepth;

    private String heldDjErrorMessage;

    // new fields for Proof Assistant "Derive" Feature:
    private boolean deriveStepFormula; // for Derive
    private boolean deriveStepHyps; // for Derive

    /** assrtSubst is created by ProofUnifier.java when step is unified (yay!) */
    private ParseNode[] assrtSubst;

    private ParseTree proofTree;

    private ProofStepStmt localRef;

    /** new fields for Proof Assistant "Derive" Feature */
    private boolean generatedByDeriveFeature; // for Derive

    /**
     * new fields in replacement of ProofWorkStmt.status these will only ever be
     * set to true on DerivationStep's.
     * <ul>
     * <li>set to true if "?" is entered in the Hyp field of a step and a Ref is
     * not entered, signifying that the step is not ready for unification
     * (because the user doesn't know the Hyps that correlate with the formula
     * and we cannot perform a search with this incomplete information.)
     * <li>NOTE: this is not set to true in the case where deriveStepHyps ==
     * true; "hypFldIncomplete" is a special situation in which unification
     * cannot be attempted; it is not an "error" by the user, just a delayed
     * entry...
     */
    private boolean hypFldIncomplete;

    // ---------------------------------------------------------------
    // This fields have only external usage
    // So they could be left as public
    // ---------------------------------------------------------------

    int unificationStatus;
    int djVarsErrorStatus;

    List<DjVars> softDjVarsErrorList;

    boolean verifyProofError;

    List<Assrt> alternateRefList;

    int nbrHypsGenerated;

    /**
     * Auto derivation step is a derivation step with some form of autocomplete.
     */
    private boolean autoStep = false;

    /**
     * Sorts DerivationStep hyp array in descending order of hypothesis step
     * formula length.
     * 
     * @param dArray array of ProofStepStmt.
     * @return dArray is return sorted in descending order of formula length
     *         (longest formulas first).
     */
    public static ProofStepStmt[] sortDerivStepHypArray(
        final ProofStepStmt[] dArray)
    {

        final ProofStepStmt[] outArray = new ProofStepStmt[dArray.length];

        ProofStepStmt holdStep1;

        int outEnd;
        int outIndex;
        int iFormulaLength;

        for (int i = 0; i < dArray.length; i++) {
            outEnd = i;
            holdStep1 = dArray[i];

            if (holdStep1 == null) {
                outArray[outEnd] = holdStep1;
                continue;
            }

            iFormulaLength = holdStep1.getFormula().getCnt();

            outIndex = 0;
            while (outIndex < outEnd) {
                if (outArray[outIndex] == null)
                    break;
                if (outArray[outIndex].getFormula().getCnt() - iFormulaLength >= 0)
                {
                    outIndex++;
                    continue;
                }
                break;
            }
            /* end of outLoop: insert here at outIndex, which means shifting
             * whatever is here downwards by one. */
            for (int k = outEnd; k > outIndex; k--)
                outArray[k] = outArray[k - 1];
            outArray[outIndex] = holdStep1;

        }

        return outArray;
    }

    /**
     * Default Constructor.
     * 
     * @param w the owner ProofWorksheet
     */
    public DerivationStep(final ProofWorksheet w) {
        super(w);
        hyp = new ProofStepStmt[0];
        hypStep = new String[0];
    }

    /**
     * Constructor for incomplete DerivationStep destined only for output to the
     * GUI.
     * <p>
     * Creates "incomplete" DerivationStep which is destined only for output to
     * the GUI, hence, the object references, etc. are not loaded. After display
     * to the GUI this worksheet disappears -- recreated via "load" each time
     * the user selects "StartUnification".
     * 
     * @param w the owner ProofWorksheet
     * @param step step number of the proof step
     * @param hypStep array of hyp step numbers for the proof step
     * @param refLabel Ref label of the proof step
     * @param formula the proof step formula
     * @param parseTree formula ParseTree (can be null)
     * @param setCaret true means position caret of TextArea to this statement.
     * @param proofLevel level of step in proof.
     */
    public DerivationStep(final ProofWorksheet w, final String step,
        final String[] hypStep, final String refLabel, final Formula formula,
        final ParseTree parseTree, final boolean setCaret, final int proofLevel)
    {

        super(w, step, refLabel, formula, setCaret);

        this.proofLevel = proofLevel;

        this.hypStep = hypStep;
        hyp = new ProofStepStmt[0];

        updateFormulaParseTree(parseTree);

        stmtText = buildStepHypRefSB();
        lineCnt = loadStmtText(stmtText, formula, parseTree);
    }

    /**
     * Constructor for generated DerivationSteps created during Unification.
     * 
     * @param w the owner ProofWorksheet
     * @param generatedHyp array of ProofStepStmt
     * @param generatedStep array of step number strings
     * @param generatedHypStep array of hyp step numbers for the proof step
     * @param generatedFormula Formula of new step.
     * @param generatedParseTree Formula's ParseTree
     * @param generatedFormulaFldIncomplete see
     *            {@link ProofStepStmt#formulaFldIncomplete}
     * @param generatedHypFldIncomplete see
     *            {@link DerivationStep#hypFldIncomplete}
     * @param generatedFlag true means "generated".
     * @param generatedWorkVarList List of Work Vars in formula
     */
    public DerivationStep(final ProofWorksheet w, final String generatedStep,
        final ProofStepStmt[] generatedHyp, final String[] generatedHypStep,
        final Formula generatedFormula, final ParseTree generatedParseTree,
        final boolean generatedFormulaFldIncomplete,
        final boolean generatedHypFldIncomplete, final boolean generatedFlag,
        final List<WorkVar> generatedWorkVarList)
    {

        super(w, generatedStep, "", generatedFormula, false); // don't set
                                                              // caret.

        hyp = generatedHyp;
        hypStep = generatedHypStep;
        updateFormulaParseTree(generatedParseTree);

        formulaFldIncomplete = generatedFormulaFldIncomplete;
        hypFldIncomplete = generatedHypFldIncomplete;
        generatedByDeriveFeature = generatedFlag;
        if (generatedWorkVarList.isEmpty())
            workVarList = null;
        else
            workVarList = generatedWorkVarList;

        stmtText = buildStepHypRefSB();
        lineCnt = loadStmtText(stmtText, generatedFormula, generatedParseTree);

    }

    /**
     * Set proofLevel numbers of hypotheses of DerivationStep. Sets proofLevel
     * numbers to 1 plus DerivationStep's proofLevel -- but only if the Hyp's
     * proofLevel is zero (because a step can be used as a Hyp in more than one
     * place in a proof!)
     */
    public void loadDerivationStepHypLevels() {
        for (final ProofStepStmt element : hyp)
            if (element != null)
                element.loadProofLevel(proofLevel + 1);
    }

    /**
     * Returns true if the DerivationStep or its hypotheses contain Work
     * Variables.
     * 
     * @return true if the DerivationStep or its hypotheses contain Work
     *         Variables.
     */
    public boolean hasWorkVarsInStepOrItsHyps() {
        if (workVarList != null)
            return true;
        for (final ProofStepStmt element : hyp)
            if (element != null && element.workVarList != null)
                return true;
        return false;
    }

    /**
     * Returns true if the DerivationStep is considered incomplete.
     * <p>
     * "Incomplete" here means that hypFldIncomplete, or formulaFldIncomplete is
     * true, or the formulaParseTree is null.
     * 
     * @return true if the DerivationStep is incomplete otherwise false.
     */
    @Override
    public boolean stmtIsIncomplete() {
        if (hypFldIncomplete || formulaFldIncomplete
            || formulaParseTree == null)
            return true;
        return false;
    }

    @Override
    public void renum(final Map<String, String> renumberMap) {

        String newNum = renumberMap.get(getStep());
        boolean changes = false;
        if (newNum != null) {
            setStep(newNum);
            changes = true;
        }

        for (int i = 0; i < hypStep.length; i++) {
            newNum = renumberMap.get(hypStep[i]);
            if (newNum != null) {
                hypStep[i] = newNum;
                changes = true;
            }
        }

        if (changes)
            reloadStepHypRefInStmtText();
    }

    /**
     * Reformats Derivation Step using TMFF. Note: This isn't guaranteed to work
     * unless the Proof Worksheet is free of structural errors. Also, a formula
     * which has not yet been parsed or which failed to parse successfully will
     * contain a null parse tree and be formatted using Format 0 -
     * "Unformatted".
     */
    @Override
    public void tmffReformat() {
        stmtText = buildStepHypRefSB();
        lineCnt = loadStmtText(stmtText, getFormula(), formulaParseTree);
    }

    /**
     * Loads DerivationStep with step, hyp and ref.
     * <p>
     * This method is passed the contents of the first token which has already
     * been parsed into step, hyp and ref fields and loads them into the
     * DerivationStep -- which already contains the step's formula! This
     * back-asswardness is a result of trying to maintain cursor/caret control
     * when the formula is validated. Messy...
     * 
     * @param origStepHypRefLength length of first token of statement
     * @param lineStartCharNbr character number in the input stream of the
     *            statement start
     * @param stepField step number field
     * @param hypField step hyps field
     * @param refField step ref field
     * @return first token of next statement.
     * @throws IOException if an error occurred
     * @throws MMIOError if an error occurred
     * @throws ProofAsstException if an error occurred
     */
    public String loadDerivationStep(final int origStepHypRefLength,
        final int lineStartCharNbr, final String stepField,
        final String hypField, final String refField) throws IOException,
        MMIOError, ProofAsstException
    {

        // update ProofStepStmt fields
        setStep(stepField);
        setRefLabel(refField);

        final boolean isQedStep = getStep().equals(PaConstants.QED_STEP_NBR);

        // !isQedStep means workVarsOk
        final String nextT = loadStmtTextWithFormula(!isQedStep);

        if (getFormula() == null)
            if (isQedStep)
                w.triggerLoadStructureException(
                    (int)w.proofTextTokenizer.getCurrentCharNbr() + 1
                        - lineStartCharNbr, PaConstants.ERRMSG_FORMULA_REQ,
                    w.getErrorLabelIfPossible(), getStep());
            else {
                deriveStepFormula = true;
                w.hasWorkVarsOrDerives = true;
            }

        if (w.isNewTheorem() || !isQedStep) {
            if (getFormula() != null)
                getNewFormulaStepParseTree();
        }
        else if (getFormula().equals(w.theorem.getFormula()))
            formulaParseTree = w.theorem.getExprParseTree();
        else
            w.triggerLoadStructureException(
                (int)w.proofTextTokenizer.getCurrentCharNbr() + 1
                    - formulaStartCharNbr, PaConstants.ERRMSG_FORMULA_NOMATCH,
                w.getErrorLabelIfPossible(), getStep(), w.theorem.getFormula());

        int nbrExpectedHyps = -1;
        if (getValidDerivationRefField(lineStartCharNbr) && getRef() != null)
            nbrExpectedHyps = ((Assrt)getRef()).getLogHypArrayLength();

        parseHypField(hypField);
        hyp = new ProofStepStmt[hypStep.length];

        final int nbrValidHyps = checkForValidHypEntries(lineStartCharNbr,
            nbrExpectedHyps);

        if (getRef() != null) {
            if (nbrValidHyps < nbrExpectedHyps) {
                deriveStepHyps = true;
                w.hasWorkVarsOrDerives = true;
                resizeHypField(nbrExpectedHyps);
            }
            else if (nbrValidHyps == nbrExpectedHyps)
                resizeHypField(nbrExpectedHyps);
            else
                validateNonDeriveFeatureHyp(lineStartCharNbr, nbrExpectedHyps,
                    nbrValidHyps);
        }
        else {
            if (workVarList != null)
                formulaFldIncomplete = true;
            validateNonDeriveFeatureHyp(lineStartCharNbr, nbrExpectedHyps,
                nbrValidHyps);
        }

        loadStepHypRefIntoStmtText(origStepHypRefLength, buildStepHypRefSB());

        if (!hypFldIncomplete && !formulaFldIncomplete)
            w.incNbrDerivStepsReadyForUnify();

        reloadLogHypKeysAndMaxDepth();

        return nextT;
    }
    /**
     * Loads DerivationStep with step, hyp and a localRef to a previous proof
     * step.
     * <p>
     * This routine exists solely to validate the localRef and the step number
     * so that subsequent step(s) that refer to it (as a Hyp) can be validated,
     * and then later be redirected to point to the step that the localRef is
     * pointing to. There are two possibilities: a structure error -- in which
     * case the ProofWorksheet is redisplayed as is -- or the step and localRef
     * are 100% valid, in which case this step will be deleted from the
     * ProofWorksheet during the "finale" of loadWorksheet() in
     * ProofWorksheet.java. So none of the other fields in the DerivationStep
     * are important.
     * 
     * @param origStepHypRefLength length of first token of statement
     * @param lineStartCharNbr character number in the input stream of the
     *            statement start
     * @param stepField step number field
     * @param hypField step hyps field
     * @param localRefField ProofStepStmt ref to previous step
     * @return first token of next statement.
     * @throws IOException if an error occurred
     * @throws MMIOError if an error occurred
     * @throws ProofAsstException if an error occurred
     */
    public String loadLocalRefDerivationStep(final int origStepHypRefLength,
        final int lineStartCharNbr, final String stepField,
        final String hypField, final String localRefField) throws IOException,
        MMIOError, ProofAsstException
    {

        // already validated
        setStep(stepField);

        // localRefField already has "#" stripped off and we know
        // that the remainder has length >= 0.
        if (localRefField.length() > 0)
            localRef = (ProofStepStmt)w
                .findFirstMatchingRefOrStep(localRefField);
        else
            localRef = null;

        if (localRef == null || localRef.getLocalRef() != null)
            w.triggerLoadStructureException(localRefField.length(),
                PaConstants.ERRMSG_BAD_LOCAL_REF, w.getErrorLabelIfPossible(),
                getStep());

        // note: load formula to get it out of the way
        // and we know that this is NOT a qed step,
        // so pass false in the next call
        // boolean isQedStep =
        // step.equals(PaConstants.QED_STEP_NBR);
        // !isQedStep means workVarsOk
        return loadStmtTextWithFormula(true);
    }

    /**
     * Loads generated Formula and its ParseTree into DerivationStep and
     * recomputes reference steps' L1HiLo key values.
     * <p>
     * This is a helper routine for ProofUnifier.java.
     * 
     * @param genFormula new generated Formula.
     * @param genFormulaParseTree ParseTree for Formula.
     * @param stmtTextAlreadyLoaded false if stmtText must be recomputed from
     *            scratch.
     */
    public void loadGeneratedFormulaIntoDerivStep(final Formula genFormula,
        final ParseTree genFormulaParseTree, final boolean stmtTextAlreadyLoaded)
    {

        setFormula(genFormula);

        updateFormulaParseTree(genFormulaParseTree);

        // this is in place for the ProofAsstAutoReformat RunParm
        // which in taken care of by ProofUnifier...
        if (stmtTextAlreadyLoaded) {}
        else {
            stmtText = buildStepHypRefSB();
            lineCnt = loadStmtText(stmtText, getFormula(), formulaParseTree);
        }

        // recompute referencing steps' L1HiLo key values;
        for (final ProofWorkStmt o : w.getProofWorkStmtList()) {
            if (o == this || !(o instanceof DerivationStep))
                continue;
            final DerivationStep d = (DerivationStep)o;
            if (d.hyp == null)
                continue;
            for (final ProofStepStmt element : d.hyp)
                if (element == this) {
                    d.logHypsL1HiLoKey = d.computeLogHypsL1HiLoKey();
                    d.logHypsMaxDepth = d.computeLogHypsMaxDepth();
                    d.resetSortedHypArray();
                }
        }
    }

    /**
     * Updates the Step/Hyp/Ref field in the statement text area of the proof
     * step.
     * <p>
     * This is needed because Unify can alter Hyp and Ref.
     */
    public void reloadStepHypRefInStmtText() {
        reviseStepHypRefInStmtText(buildStepHypRefSB());
    }

    /**
     * Updates these pesky things used in ProofUnifier for speedy scanning
     * during Unification Search.
     */
    public void reloadLogHypKeysAndMaxDepth() {
        if (!hypFldIncomplete && !deriveStepHyps) {
            logHypsL1HiLoKey = computeLogHypsL1HiLoKey();
            logHypsMaxDepth = computeLogHypsMaxDepth();
        }
        else {
            logHypsL1HiLoKey = "";
            logHypsMaxDepth = 0;
        }
    }

    /**
     * Creates the Step/Hyp/Ref field and loads it into a new StringBuilder.
     * 
     * @return StringBuilder containing new Step/Hyp/Ref.
     */
    public StringBuilder buildStepHypRefSB() {
        final StringBuilder sb = new StringBuilder();

        sb.append(getStep());
        if (autoStep)
            return sb;
        sb.append(PaConstants.FIELD_DELIMITER_COLON);

        if (hypStep.length > 0) {
            int i = 0;
            while (true) {
                if (hypStep[i] == null)
                    sb.append(PaConstants.DEFAULT_STMT_LABEL);
                else
                    sb.append(hypStep[i]);
                if (++i < hypStep.length) {
                    sb.append(PaConstants.FIELD_DELIMITER_COMMA);
                    continue;
                }
                break;
            }
        }
        sb.append(PaConstants.FIELD_DELIMITER_COLON);
        if (getRefLabel() != null)
            sb.append(getRefLabel());
        return sb;
    }

    /**
     * Set the assrtSubst array.
     * 
     * @param assrtSubst ParseNode array.
     */
    public void setAssrtSubstList(final ParseNode[] assrtSubst) {
        this.assrtSubst = assrtSubst;
    }

    public ParseNode[] getAssrtSubstList() {
        return assrtSubst;
    }

    public int getAssrtSubstNumber() {
        return assrtSubst.length;
    }

    public ParseNode getAssrtSubst(final int i) {
        return assrtSubst[i];
    }

    public void setAssrtSubst(final int i, final ParseNode node) {
        assrtSubst[i] = node;
    }

    /**
     * Get the proofTree. Could return null.
     * 
     * @return proofTree.
     */
    public ParseTree getProofTree() {
        return proofTree;
    }

    /**
     * Set the proofTree.
     * 
     * @param proofTree new proof tree.
     */
    public void setProofTree(final ParseTree proofTree) {
        this.proofTree = proofTree;
    }
    /**
     * Swap two hyp references in a DerivationStep. This method is provided as a
     * convenience for ProofUnifier.java.
     * 
     * @param i index of first hyp to swap
     * @param j index of second hyp to swap
     */
    public void swapHyps(final int i, final int j) {
        final ProofStepStmt holdHypStmt = hyp[i];
        final String holdHypStep = hypStep[i];
        hyp[i] = hyp[j];
        hypStep[i] = hypStep[j];
        hyp[j] = holdHypStmt;
        hypStep[j] = holdHypStep;
    }

    /**
     * Builds an error message detailing the "soft" DjVars errors and stores it
     * in the heldDjErrorMessage field.
     * 
     * @param softDjVarsErrorList List of DjVars reported to be missing from the
     *            Dj Vars of the theorem which are needed by the Derivation
     *            Step.
     */
    public void buildSoftDjErrorMessage(final List<DjVars> softDjVarsErrorList)
    {
        heldDjErrorMessage = softDjVarsErrorList == null
            || softDjVarsErrorList.isEmpty() ? null : LangException.format(
            PaConstants.ERRMSG_SUBST_TO_VARS_NOT_DJ, getStep(),
            softDjVarsErrorList);
    }

    /**
     * Gets the HypArray sorted using a lazy evaluation to perform the sort.
     * 
     * @return Sorted array of ProofStepStmt for the DerivationStep.
     */
    public ProofStepStmt[] getSortedHypArray() {
        if (sortedHypArray == null)
            setSortedHypArray(DerivationStep.sortDerivStepHypArray(hyp));
        return sortedHypArray;
    }

    /** Sets the sortedHypArray to null. */
    public void resetSortedHypArray() {
        setSortedHypArray(null);
    }

    /**
     * Sets the sortedHypArray.
     * 
     * @param sortedHypArray sorted array of ProofStepStmt.
     */
    public void setSortedHypArray(final ProofStepStmt[] sortedHypArray) {
        this.sortedHypArray = sortedHypArray;
    }

    // hyp access methods:

    public ProofStepStmt[] getHypList() {
        return hyp;
    }

    public void setHypList(final ProofStepStmt[] hypArray) {
        hyp = hypArray;
    }

    public ProofStepStmt getHyp(final int i) {
        return hyp[i];
    }

    public void setHyp(final int i, final ProofStepStmt stmt) {
        hyp[i] = stmt;
    }

    public int getHypNumber() {
        return hyp.length;
    }

    private boolean getValidDerivationRefField(final int lineStartCharNbr)
        throws ProofAsstException
    {

        // ref checking: if input,
        // is < maxSeq,
        // is existing Assrt and
        // not a syntax axiom and
        // nbr log hyps matches hyp field count

        if (getRefLabel() == null)
            return true; // ok, unify normally outputs.
        if (getRefLabel().equals(PaConstants.DEFAULT_STMT_LABEL)) {
            setRefLabel(null);
            return true;
        }

        setRef(w.logicalSystem.getStmtTbl().get(getRefLabel()));
        if (getRef() == null)
            w.triggerLoadStructureException(
                (int)w.proofTextTokenizer.getCurrentCharNbr() + 1
                    - formulaStartCharNbr, PaConstants.ERRMSG_REF_NOTFND,
                w.getErrorLabelIfPossible(), getStep(), getRefLabel());
        if (getRef().getSeq() >= w.getMaxSeq())
            w.triggerLoadStructureException(
                (int)w.proofTextTokenizer.getCurrentCharNbr() + 1
                    - formulaStartCharNbr, PaConstants.ERRMSG_REF_MAXSEQ,
                w.getErrorLabelIfPossible(), getStep(), getRefLabel());
        if (!(getRef() instanceof Assrt))
            w.triggerLoadStructureException(
                (int)w.proofTextTokenizer.getCurrentCharNbr() + 1
                    - formulaStartCharNbr, PaConstants.ERRMSG_REF_NOT_ASSRT,
                w.getErrorLabelIfPossible(), getStep(), getRefLabel());

        if (getRef().getFormula().getTyp() != w.getProvableLogicStmtTyp())
            w.triggerLoadStructureException(
                (int)w.proofTextTokenizer.getCurrentCharNbr() + 1
                    - formulaStartCharNbr, PaConstants.ERRMSG_REF_BAD_TYP, w
                    .getErrorLabelIfPossible(), getStep(), getRefLabel(),
                getRef().getFormula().getTyp());
        return true;
    }

    private int parseHypField(final String hypField) {

        int questionMarks = 0;

        final List<String> list = new ArrayList<String>();

        if (hypField != null && !hypField.equals("")) {

            final DelimitedTextParser hypParser = new DelimitedTextParser(
                hypField);
            hypParser.setParseDelimiter(PaConstants.FIELD_DELIMITER_COMMA);
            hypParser.setQuoterEnabled(false);

            String s;
            while ((s = hypParser.nextField()) != null)
                list.add(s.toLowerCase());
        }

        setHypStepList(list.toArray(new String[list.size()]));

        for (final String step : hypStep)
            if (step.compareTo(PaConstants.DEFAULT_STMT_LABEL) == 0)
                questionMarks++;

        return questionMarks;
    }

    private int checkForValidHypEntries(final int lineStartCharNbr,
        final int nbrExpectedHyps) throws ProofAsstException
    {

        int nbrValidHyps = 0;

        for (int i = 0; i < hypStep.length; i++) {
            if (hypStep[i].equals("")
                || hypStep[i].equals(PaConstants.DEFAULT_STMT_LABEL))
                continue;

            if (hypStep[i].equals(getStep()))
                w.triggerLoadStructureException(
                    (int)w.proofTextTokenizer.getCurrentCharNbr() + 1
                        - lineStartCharNbr, PaConstants.ERRMSG_BAD_HYP_STEP,
                    w.getErrorLabelIfPossible(), getStep(), hypStep[i]);

            final ProofWorkStmt x = w.findMatchingStepNbr(hypStep[i]);
            if (x == null)
                w.triggerLoadStructureException(
                    (int)w.proofTextTokenizer.getCurrentCharNbr() + 1
                        - lineStartCharNbr, PaConstants.ERRMSG_HYP_STEP_NOTFND,
                    w.getErrorLabelIfPossible(), getStep(), hypStep[i]);

            hyp[i] = (ProofStepStmt)x;

            nbrValidHyps++;
        }

        if (nbrExpectedHyps != -1 && nbrValidHyps > nbrExpectedHyps)
            w.triggerLoadStructureException(
                (int)w.proofTextTokenizer.getCurrentCharNbr() + 1
                    - formulaStartCharNbr,
                PaConstants.ERRMSG_REF_NBR_HYPS_LT_INPUT,
                w.getErrorLabelIfPossible(), getStep(), hypStep.length,
                nbrExpectedHyps, getRefLabel());

        return nbrValidHyps;
    }

    /**
     * OK, a Ref label was entered and the input nbrValidHyps <= nbrExpectedHyps
     * for the Ref. All that needs to be done is output hyp and hypStep arrays
     * that are of the correct length (nbrExpectedHyps), to expand/smoosh the
     * input to match, and to replace any remaining (post-smoosh) "" hyp entries
     * with "?".
     * 
     * @param nbrExpectedHyps the number of expected Hyps
     */
    private void resizeHypField(final int nbrExpectedHyps) {
        final String[] outHypStep = new String[nbrExpectedHyps];
        final ProofStepStmt[] outHyp = new ProofStepStmt[nbrExpectedHyps];

        for (int i = 0; i < outHypStep.length; i++) {
            if (i >= hypStep.length) {
                outHypStep[i] = PaConstants.DEFAULT_STMT_LABEL;
                continue;
            }
            if (hypStep[i].equals("")
                || hypStep[i].equals(PaConstants.DEFAULT_STMT_LABEL))
            {
                outHypStep[i] = PaConstants.DEFAULT_STMT_LABEL;
                continue;
            }
            outHypStep[i] = hypStep[i];
            outHyp[i] = hyp[i];
            continue;
        }

        if (hypStep.length > outHypStep.length) {
            final int lastOutputIndex = outHypStep.length - 1;
            for (int i = outHypStep.length; i < hypStep.length; i++) {
                if (hypStep[i].equals("")
                    || hypStep[i].equals(PaConstants.DEFAULT_STMT_LABEL))
                    continue;
                if (!outHypStep[lastOutputIndex]
                    .equals(PaConstants.DEFAULT_STMT_LABEL))
                    smooshLeft(lastOutputIndex, outHypStep, outHyp);
                // make the move...
                outHypStep[lastOutputIndex] = hypStep[i];
                outHyp[lastOutputIndex] = hyp[i];
            }
        }

        hyp = outHyp;
        setHypStepList(outHypStep);
    }

    private void smooshLeft(final int lastOutputIndex,
        final String[] outHypStep, final ProofStepStmt[] outHyp)
    {
        int i = 0;
        for (; i < lastOutputIndex; i++)
            if (outHypStep[i].equals(PaConstants.DEFAULT_STMT_LABEL)) {
                outHypStep[i] = outHypStep[i + 1];
                outHyp[i] = outHyp[i + 1];
                return;
            }
        throw new IllegalArgumentException(PaConstants.ERRMSG_SMOOSH_FAILED);
    }

    /* OK, we are *not* in deriveStepHyps mode, so either a Ref was not entered,
     * or, the input nbrValidHyps > nbrExpectedHyps. For the old,
     * non-deriveStepHyps processing, the number of input hyps must match the
     * Ref's number of logical hypotheses, unless "?" or "" was input for one of
     * them. And "" hyps are automatically converted to "?". If any of the input
     * hyps are "?" or "" then the step is given status "INCOMPLETE_HYPS". */
    private void validateNonDeriveFeatureHyp(final int lineStartCharNbr,
        final int nbrExpectedHyps, final int nbrValidHyps)
        throws ProofAsstException
    {

        hypFldIncomplete = false;

        for (int i = 0; i < hypStep.length; i++)
            if (hypStep[i].equals("")
                || hypStep[i].equals(PaConstants.DEFAULT_STMT_LABEL))
            {
                hypStep[i] = PaConstants.DEFAULT_STMT_LABEL;

                hypFldIncomplete = true;
            }

        if (nbrExpectedHyps != -1 // a valid Ref was entered
            && !hypFldIncomplete)
            w.triggerLoadStructureException(
                (int)w.proofTextTokenizer.getCurrentCharNbr() + 1
                    - formulaStartCharNbr, PaConstants.ERRMSG_REF_NBR_HYPS,
                w.getErrorLabelIfPossible(), getStep(), hypStep.length,
                nbrExpectedHyps, getRefLabel());
    }

    private String computeLogHypsL1HiLoKey() {
        String hiLoKey = "";

        if (hyp.length > 0) {
            Stmt hStmt;
            int n;
            String low = null;
            String high = null;
            int lowNbr = Integer.MAX_VALUE;
            int highNbr = Integer.MIN_VALUE;
            for (final ProofStepStmt element : hyp) {

                // hyp[i].formulaParseTree may be null
                // if formula null and new "Derive" feature in use!
                if (element == null || element.formulaParseTree == null)
                    return hiLoKey;

                hStmt = element.formulaParseTree.getRoot().getStmt();
                if (hStmt instanceof VarHyp)
                    return hiLoKey;
                n = hStmt.getSeq();
                if (n < lowNbr) {
                    lowNbr = n;
                    low = hStmt.getLabel();
                }
                if (n > highNbr) {
                    highNbr = n;
                    high = hStmt.getLabel();
                }
            }
            hiLoKey = high + " " + low;
        }
        return hiLoKey;
    }

    private int computeLogHypsMaxDepth() {

        int hypMaxDepth = 0;
        int hypDepth;
        for (final ProofStepStmt element : hyp) {
            if (element == null || element.formulaParseTree == null
                || element.workVarList != null)
                return 0;
            if ((hypDepth = element.formulaParseTree.getMaxDepth()) > hypMaxDepth)
                hypMaxDepth = hypDepth;
        }
        return hypMaxDepth;
    }

    @Override
    public String toString() {
        if (autoStep)
            return getStep() + " " + getFormula();

        String s = getStep() + ":";
        String delim = "";
        for (final String h : hypStep) {
            s += delim + h;
            delim = ",";
        }
        return s + ":" + getRefLabel() + " " + getFormula();
    }

    void setHypStep(final int i, final String s) {
        hypStep[i] = s;
    }

    void setHypStepList(final String[] hypStep) {
        this.hypStep = hypStep;
    }

    public String getLogHypsL1HiLoKey() {
        return logHypsL1HiLoKey;
    }

    public int getLogHypsMaxDepth() {
        return logHypsMaxDepth;
    }

    public String getHeldDjErrorMessage() {
        return heldDjErrorMessage;
    }

    public void setHeldDjErrorMessage(final String heldDjErrorMessage) {
        this.heldDjErrorMessage = heldDjErrorMessage;
    }

    public boolean hasDeriveStepFormula() {
        return deriveStepFormula;
    }

    public boolean hasDeriveStepHyps() {
        return deriveStepHyps;
    }

    @Override
    public ProofStepStmt getLocalRef() {
        return localRef;
    }

    public boolean isGeneratedByDeriveFeature() {
        return generatedByDeriveFeature;
    }

    public boolean isHypFldIncomplete() {
        return hypFldIncomplete;
    }

    public void setHypFldIncomplete(final boolean hypFldIncomplete) {
        this.hypFldIncomplete = hypFldIncomplete;
    }

    public boolean isAutoStep() {
        return autoStep;
    }

    public void setAutoStep(final boolean autoStep) {
        this.autoStep = autoStep;
    }
}
