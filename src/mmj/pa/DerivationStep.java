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

/**
 *  DerivationStep.java  0.09 08/01/2008
 *  <code>
 *  Version 0.04: 06/01/2007
 *      - Un-nested inner class
 *      - replace ProofworkStmt.status
 *      - Do not require use of "?" to invoke
 *        deriveStepHyps; the mere fact of a
 *        Ref entry and fewer Hyps entered than
 *        needed by the Ref is sufficient.
 *        Also, if a Ref is entered and the
 *        correct number of Hyps is entered,
 *        any "?" or "" Hyp entries are ignored
 *        (e.g. if "1,2,?" entered and 2 hyps are
 *        required, then "1,2" is used and the
 *        "?" is deleted by the program.)
 *
 *  Version 0.05: 08/01/2007
 *      - Work Var Enhancement misc. changes.
 *
 *  Version 0.06: 11/01/2007
 *      - Add loadDerivationStepHypLevels() to compute
 *        proofLevel number for hypotheses of DerivationStep.
 *      - Modify constructor used for proof export to accept
 *        a proofLevel parameter.
 *      - Add new loadLocalRefDerivationStep() routine.
 *      - Add ERRMSG_BAD_LOCAL_REF_1, "E-PA-0379"
 *      - Made a separate routine out of
 *        reloadLogHypKeysAndMaxDepth() to recompute when
 *        Hyps change.
 *
 *  Version 0.07: 02/01/2008
 *      - Modify loadGeneratedFormulaIntoDerivStep() to
 *        include new boolean "stmtTextAlreadyLoaded"
 *        for use with the new ProofAsstAutoReformat RunParm
 *        option (handled up in ProofUnifier).
 *
 *
 *  Version 0.08: 03/01/2008
 *      - Remove Hints feature
 *      - add sortedHypArray and get/set/resetSortedHypArray(),
 *        and makes sure that resetSortedHypArray() is called
 *        to reset the array to null whenever/wherever
 *        formulas are updated (see computeLogHypsL1HiLoKey()
 *        and loadGeneratedFormulaIntoDerivStep() via
 *        ProofUnifier.) In some cases multiple sorts
 *        of the same hyps will be avoided -- it is hoped.
 *
 *  Version 0.09: 08/01/2008
 *      - add getHyp() so that logical hypotheses of a step
 *        can be obtained.
 *
 *  </code>
 *  DerivationStep represents proof steps that derive
 *  a new formula.
 */

package mmj.pa;

import  java.io.IOException;
import  java.util.ArrayList;
import  java.util.Iterator;
import  java.util.HashMap;
import  mmj.util.DelimitedTextParser;
import  mmj.lang.*;
import  mmj.mmio.*;
import  mmj.tmff.*;
import  mmj.verify.*;

public class DerivationStep extends ProofStepStmt {

    ProofStepStmt[]   hyp;
    private ProofStepStmt[] sortedHypArray;

    String[]          hypStep;

    String            logHypsL1HiLoKey;
    int               logHypsMaxDepth;

    int               unificationStatus;
    int               djVarsErrorStatus;

    ArrayList         softDjVarsErrorList;

    boolean           verifyProofError;


    String            heldDjErrorMessage;

    ArrayList         alternateRefList;

    //new fields for Proof Assistant "Derive" Feature:
    boolean           deriveStepFormula;        // for Derive
    boolean           deriveStepHyps;           // for Derive
    int               nbrMissingHyps;           // for Derive
    int               nbrHypsGenerated;

    /**
     *  assrtSubst is created by ProofUnifier.java when
     *  step is unified (yay!)
     */
    ParseNode[]       assrtSubst;

    ParseTree         proofTree;

    /**
     *  Sorts DerivationStep hyp array in descending order of
     *  hypothesis step formula length.
     *  <p>
     *  @param dArray array of ProofStepStmt.
     *  @return dArray is return sorted in descending order of
     *          formula length (longest formulas first).
     */
    public static ProofStepStmt[] sortDerivStepHypArray(
                                        ProofStepStmt[] dArray) {

        ProofStepStmt[] outArray
                                   =
            new ProofStepStmt[dArray.length];

        ProofStepStmt   holdStep1;

        int outEnd;
        int outIndex;
        int iFormulaLength;

        iLoop: for (int i = 0; i < dArray.length; i++) {
            outEnd                = i;
            holdStep1             = dArray[i];

            if (holdStep1 == null) {
                outArray[outEnd]  = holdStep1;
                continue iLoop;
            }

            iFormulaLength        = holdStep1.formula.getCnt();

            outIndex              = 0;
            while (outIndex < outEnd) {
                if (outArray[outIndex] == null) {
                    break;
                }
                if (outArray[outIndex].formula.getCnt()
                        - iFormulaLength
                    >= 0) {
                    ++outIndex;
                    continue;
                }
                break;
            }
            /*
             * end of outLoop: insert here at outIndex, which means
             * shifting whatever is here downwards by one.
             */
            for (int k = outEnd; k > outIndex; k--) {
                outArray[k]   = outArray[k - 1];
            }
            outArray[outIndex]
                              = holdStep1;

        }

        return outArray;
    }

    /**
     *  Default Constructor.
     */
    public DerivationStep(ProofWorksheet w) {
        super(w);
        hyp                   = new ProofStepStmt[0];
        hypStep               = new String[0];
    }

    /**
     *  Constructor for incomplete DerivationStep destined
     *  only for output to the GUI.
     *  <p>
     *  Creates "incomplete" DerivationStep which is
     *  destined only for output to the GUI, hence,
     *  the object references, etc. are not loaded.
     *  After display to the GUI this worksheet
     *  disappears -- recreated via "load" each time
     *  the user selects "StartUnification".
     *
     *  @param step step number of the proof step
     *  @param hypStep array of hyp step numbers for the
     *                 proof step
     *  @param refLabel Ref label of the proof step
     *  @param formula the proof step formula
     *  @param parseTree formula ParseTree (can be null)
     *  @param setCaret true means position caret of
     *                  TextArea to this statement.
     *  @param proofLevel level of step in proof.
     */
    public DerivationStep(ProofWorksheet w,
                          String         step,
                          String[]       hypStep,
                          String         refLabel,
                          Formula        formula,
                          ParseTree      parseTree,
                          boolean        setCaret,
                          int            proofLevel) {

        super(w,
              step,
              refLabel,
              setCaret);

        this.proofLevel           = proofLevel;

        this.hypStep          = hypStep;
        hyp                   = new ProofStepStmt[0];

        this.formula          = formula;
        updateFormulaParseTree(parseTree);

        stmtText              = buildStepHypRefSB();
        lineCnt               = loadStmtText(stmtText,
                                             formula,
                                             parseTree);
    }

    /**
     *  Constructor for generated DerivationSteps created
     *  during Unification.
     *  <p>
     *  @param generatedHyp array of ProofStepStmt
     *  @param generatedStep array of step number strings
     *  @param generatedFormula Formula of new step.
     *  @param generatedParseTree Formula's ParseTree
     *  @param generatedFormulaFldIncomplete
     *  @param generatedHypFldIncomplete
     *  @param generatedFlag true means "generated".
     *  @param generatedWorkVarList ArrayList of Work Vars in formula
     */
    public DerivationStep(
                  ProofWorksheet  w,
                  String          generatedStep,
                  ProofStepStmt[] generatedHyp,
                  String[]        generatedHypStep,
                  Formula         generatedFormula,
                  ParseTree       generatedParseTree,
                  boolean         generatedFormulaFldIncomplete,
                  boolean         generatedHypFldIncomplete,
                  boolean         generatedFlag,
                  ArrayList       generatedWorkVarList) {

        super(w,
              generatedStep,
              "",
              false); //don't set caret.

        this.hyp              = generatedHyp;
        this.hypStep          = generatedHypStep;
        this.formula          = generatedFormula;
        updateFormulaParseTree(generatedParseTree);

        this.formulaFldIncomplete
                              = generatedFormulaFldIncomplete;
        this.hypFldIncomplete = generatedHypFldIncomplete;
        this.generatedByDeriveFeature
                              = generatedFlag;
        if (generatedWorkVarList.size() == 0) {
            this.workVarList  = null;
        }
        else {
            this.workVarList  = generatedWorkVarList;
        }

        stmtText              = buildStepHypRefSB();
        lineCnt               = loadStmtText(stmtText,
                                             generatedFormula,
                                             generatedParseTree);

    }

    /**
     *  Returns false, a DerivationStep is never a HypothesisStep.
     *  @return false, a DerivationStep is never a HypothesisStep.
     */
    public boolean isHypothesisStep() {
        return false;
    }

    /**
     *  Returns true, a DerivationStep is a DerivationStep.
     *  @return true, a DerivationStep is a DerivationStep.
     */
    public boolean isDerivationStep() {
        return true;
    }


    /**
     *  Set proofLevel numbers of hypotheses of DerivationStep.
     *
     *  Sets proofLevel numbers to 1 plus DerivationStep's
     *  proofLevel -- but only if the Hyp's proofLevel is zero
     *  (because a step can be used as a Hyp in more than one
     *  place in a proof!)
     */
    public void loadDerivationStepHypLevels() {
        for (int i = 0; i < hyp.length; i++) {
            if (hyp[i] != null) {
                hyp[i].loadProofLevel(proofLevel + 1);
            }
        }
    }

    /**
     *  Returns true if the DerivationStep or its hypotheses
     *  contain Work Variables.
     *  <p>
     *  @return true if the DerivationStep or its hypotheses
     *  contain Work Variables.
     */
    public boolean hasWorkVarsInStepOrItsHyps() {
        if (workVarList != null) {
            return true;
        }
        for (int i = 0; i < hyp.length; i++) {
            if (hyp[i] != null
                &&
                hyp[i].workVarList != null) {
                return true;
            }
        }
        return false;
    }

    /**
     *  Returns true if the DerivationStep is considered
     *  incomplete.
     *  <p>
     *  "Incomplete" here means that hypFldIncomplete, or
     *  formulaFldIncomplete is true, or the formulaParseTree
     *  is null.
     *  <p>
     *  @return true if the DerivationStep is incomplete
     *               otherwise false.
     */
    public boolean stmtIsIncomplete() {
        if (hypFldIncomplete
            || formulaFldIncomplete
            || formulaParseTree == null
           ) {
            return true;
        }
        return false;
    }


    /**
     *  Renumbers step numbers using a HashMap containing
     *  old and new step number pairs.
     *
     *  @param renumberHashMap contains key/value pairs defining
     *                         newly assigned step numbers.
     */
    public void renum(HashMap renumberHashMap) {

        String  newNum        = (String)renumberHashMap.get(step);
        boolean changes       = false;
        if (newNum != null) {
            step              = newNum;
            changes           = true;
        }

        for (int i = 0; i < hypStep.length; i++) {
            newNum            =
                (String)renumberHashMap.get(hypStep[i]);
            if (newNum != null) {
                hypStep[i]    = newNum;
                changes       = true;
            }
        }

        if (changes) {
            reloadStepHypRefInStmtText();
        }
    }

    /**
     *  Reformats Derivation Step using TMFF.
     *
     *  Note: This isn't guaranteed to work unless the
     *        Proof Worksheet is free of structural errors.
     *        Also, a formula which has not yet been parsed
     *        or which failed to parse successfully will
     *        contain a null parse tree and be formatted
     *        using Format 0 - "Unformatted".
     */
    public void tmffReformat() {
        stmtText              = buildStepHypRefSB();
        lineCnt               = loadStmtText(stmtText,
                                             formula,
                                             formulaParseTree);
    }

    /**
     *  Loads DerivationStep with step, hyp and ref.
     *  <p>
     *  This method is passed the contents of the first
     *  token which has already been parsed into step,
     *  hyp and ref fields and loads them into the
     *  DerivationStep -- which already contains the
     *  step's formula! This back-asswardness is a
     *  result of trying to maintain cursor/caret
     *  control when the formula is validated. Messy...
     *
     *  @param origStepHypRefLength length of first token of
     *                              statement
     *  @param lineStartCharNbr character number in the input
     *                          stream of the statement start
     *  @param stepField step number field
     *  @param hypField step hyps field
     *  @param refField step ref field
     *
     *  @return      first token of next statement.
     */
    public String loadDerivationStep(int    origStepHypRefLength,
                                     int    lineStartCharNbr,
                                     String stepField,
                                     String hypField,
                                     String refField)
                        throws IOException,
                               MMIOError,
                               ProofAsstException {

        //update ProofStepStmt fields
        step                  = stepField;
        refLabel              = refField;

        boolean isQedStep     =
            step.equals(PaConstants.QED_STEP_NBR);

        // !isQedStep means workVarsOk
        String nextT          = loadStmtTextWithFormula(!isQedStep);

        if (formula == null) {
            if (isQedStep) {
                w.triggerLoadStructureException(
                    PaConstants.ERRMSG_FORMULA_REQ_1
                    + w.getErrorLabelIfPossible()
                    + PaConstants.ERRMSG_FORMULA_REQ_2
                    + step
                    + PaConstants.ERRMSG_FORMULA_REQ_3,
                    (int)w.proofTextTokenizer.getCurrentCharNbr()
                        + 1
                        - lineStartCharNbr);
            }
            else {
                deriveStepFormula = true;
                w.hasWorkVarsOrDerives
                                  = true;
            }
        }

        if (w.isNewTheorem() ||
            !isQedStep) {
            if (formula != null) {
                getNewFormulaStepParseTree();
            }
        }
        else {
            if (formula.equals(w.theorem.getFormula())) {
                formulaParseTree  = w.theorem.getExprParseTree();
            }
            else {
                w.triggerLoadStructureException(
                    PaConstants.ERRMSG_FORMULA_NOMATCH_1
                    + w.getErrorLabelIfPossible()
                    + PaConstants.ERRMSG_FORMULA_NOMATCH_2
                    + step
                    + PaConstants.ERRMSG_FORMULA_NOMATCH_3,
                    (int)w.proofTextTokenizer.getCurrentCharNbr()
                        + 1
                        - formulaStartCharNbr);
            }
        }

        int nbrExpectedHyps   = -1;
        if (getValidDerivationRefField(lineStartCharNbr)
            &&
            ref != null) {  //ref is optional
            nbrExpectedHyps   =
                ((Assrt)ref).getLogHypArrayLength();
        }


        if (formula == null &&
            ref     == null) {
            w.triggerLoadStructureException(
                PaConstants.ERRMSG_FORMULA_OR_REF_REQ_1
                + w.getErrorLabelIfPossible()
                + PaConstants.ERRMSG_FORMULA_OR_REF_REQ_2
                + step
                + PaConstants.ERRMSG_FORMULA_OR_REF_REQ_3,
                (int)w.proofTextTokenizer.getCurrentCharNbr()
                    + 1
                    - lineStartCharNbr);
        }

        int nbrQuestionMarkHypsInput
                              = parseHypField(hypField);
        hyp                   =
            new ProofStepStmt[hypStep.length];

        int nbrValidHyps      =
            checkForValidHypEntries(lineStartCharNbr,
                                    nbrExpectedHyps);

        if (ref != null) {
            if (nbrValidHyps < nbrExpectedHyps) {
                deriveStepHyps    = true;
                w.hasWorkVarsOrDerives
                                  = true;
                nbrMissingHyps    = nbrExpectedHyps -
                                    nbrValidHyps;
                resizeHypField(nbrExpectedHyps);
            }
            else {
                if (nbrValidHyps == nbrExpectedHyps) {
                    resizeHypField(nbrExpectedHyps);
                }
                else {
                    validateNonDeriveFeatureHyp(
                                      lineStartCharNbr,
                                      nbrExpectedHyps,
                                      nbrValidHyps);
                }
            }
        }
        else {
            if (workVarList != null) {
                formulaFldIncomplete
                                  = true;
            }
            validateNonDeriveFeatureHyp(
                                  lineStartCharNbr,
                                  nbrExpectedHyps,
                                  nbrValidHyps);
        }

        loadStepHypRefIntoStmtText(
            origStepHypRefLength,
            buildStepHypRefSB());

        if (!hypFldIncomplete &&
            !formulaFldIncomplete) {    //new work var enhancment!
            w.incNbrDerivStepsReadyForUnify();
        }

        reloadLogHypKeysAndMaxDepth();

        return nextT;
    }

    /**
     *  Loads DerivationStep with step, hyp and a localRef
     *  to a previous proof step.
     *  <p>
     *  This routine exists solely to validate the localRef
     *  and the step number so that subsequent step(s) that
     *  refer to it (as a Hyp) can be validated, and then
     *  later be redirected to point to the step that
     *  the localRef is pointing to. There are two possibilities:
     *  a structure error -- in which case the ProofWorksheet
     *  is redisplayed as is -- or the step and localRef
     *  are 100% valid, in which case this step will be
     *  deleted from the ProofWorksheet during the "finale"
     *  of loadWorksheet() in ProofWorksheet.java. So
     *  none of the other fields in the DerivationStep
     *  are important.
     *  <p>
     *  @param origStepHypRefLength length of first token of
     *                              statement
     *  @param lineStartCharNbr character number in the input
     *                          stream of the statement start
     *  @param stepField step number field
     *  @param hypField step hyps field
     *  @param localRefField ProofStepStmt ref to previous step
     *
     *  @return first token of next statement.
     */
    public String loadLocalRefDerivationStep(
                                     int       origStepHypRefLength,
                                     int       lineStartCharNbr,
                                     String    stepField,
                                     String    hypField,
                                     String    localRefField)
                        throws IOException,
                               MMIOError,
                               ProofAsstException {

        //already validated
        step                      = stepField;

        // localRefField already has "#" stripped off and we know
        // that the remainder has length >= 0.
        if (localRefField.length() > 0) {
            localRef              =
                (ProofStepStmt)
                    w.findFirstMatchingRefOrStep(
                        localRefField);
        }
        else {
            localRef              = null;
        }

        if (localRef == null ||
            localRef.localRef != null) {
            w.triggerLoadStructureException(
                PaConstants.ERRMSG_BAD_LOCAL_REF_1
                + w.getErrorLabelIfPossible()
                + PaConstants.ERRMSG_BAD_LOCAL_REF_2
                + step
                + PaConstants.ERRMSG_BAD_LOCAL_REF_3,
                localRefField.length());
        }

        //note: load formula to get it out of the way
        //      and we know that this is NOT a qed step,
        //      so pass false in the next call
        //boolean isQedStep     =
        //    step.equals(PaConstants.QED_STEP_NBR);
        // !isQedStep means workVarsOk
        return loadStmtTextWithFormula(true);
    }

    /**
     *  Loads generated Formula and its ParseTree into
     *  DerivationStep and recomputes reference steps'
     *  L1HiLo key values.
     *  <p>
     *  This is a helper routine for ProofUnifier.java.
     *  <p>
     *  @param genFormula new generated Formula.
     *  @param genFormulaParseTree ParseTree for Formula.
     *  @param stmtTextAlreadyLoaded false if stmtText
     *                   must be recomputed from scratch.
     */
    public void loadGeneratedFormulaIntoDerivStep(
                                Formula   genFormula,
                                ParseTree genFormulaParseTree,
                                boolean   stmtTextAlreadyLoaded) {

        formula               = genFormula;

        updateFormulaParseTree(genFormulaParseTree);

        // this is in place for the ProofAsstAutoReformat RunParm
        // which in taken care of by ProofUnifier...
        if (stmtTextAlreadyLoaded) {
        }
        else {
            stmtText          = buildStepHypRefSB();
            lineCnt           = loadStmtText(stmtText,
                                             formula,
                                             formulaParseTree);
        }

        //recompute referencing steps' L1HiLo key values;
        Iterator iterator     =
            w.getProofWorkStmtListIterator();
        ProofWorkStmt  o;
        DerivationStep d;
        while (iterator.hasNext()) {
            o                 = (ProofWorkStmt)iterator.next();
            if (o == this ||
                !(o.isDerivationStep())) {
                continue;
            }
            d                 = (DerivationStep)o;
            if (d.hyp == null) {
                continue;
            }
            for (int i = 0; i < d.hyp.length; i++) {
                if (d.hyp[i] == this) {
                    d.logHypsL1HiLoKey
                              = d.computeLogHypsL1HiLoKey();
                    d.logHypsMaxDepth
                              = d.computeLogHypsMaxDepth();
                    d.resetSortedHypArray();
                }
            }
        }
    }

    /**
     *  Updates the Step/Hyp/Ref field in the statement
     *  text area of the proof step.
     *  <p>
     *  This is needed because Unify can alter Hyp and Ref.
     */
    public void reloadStepHypRefInStmtText() {
        reviseStepHypRefInStmtText(buildStepHypRefSB());
    }

    /**
     *  Updates these pesky things used in ProofUnifier
     *  for speedy scanning during Unification Search.
     */
    public void reloadLogHypKeysAndMaxDepth() {
        if (!hypFldIncomplete &&
            !deriveStepHyps) {
            logHypsL1HiLoKey      = computeLogHypsL1HiLoKey();
            logHypsMaxDepth       = computeLogHypsMaxDepth();
        }
        else {
            logHypsL1HiLoKey      = "";
            logHypsMaxDepth       = 0;
        }
    }

    /**
     *  Creates the Step/Hyp/Ref field and loads it into
     *  a new StringBuffer.
     *  <p>
     *  @return StringBuffer containing new Step/Hyp/Ref.
     */
    public StringBuffer buildStepHypRefSB() {
        StringBuffer sb       = new StringBuffer();

        sb.append(step);
        sb.append(PaConstants.FIELD_DELIMITER_COLON);

        if (hypStep.length > 0) {
            int i             = 0;
            hLoop: while (true) {
                if (hypStep[i] == null) {
                    sb.append(
                        PaConstants.DEFAULT_STMT_LABEL);
                }
                else {
                    sb.append(hypStep[i]);
                }
                if (++i < hypStep.length) {
                    sb.append(
                        PaConstants.FIELD_DELIMITER_COMMA);
                    continue hLoop;
                }
                break;
            }
        }
        sb.append(PaConstants.FIELD_DELIMITER_COLON);
        if (refLabel != null) {
            sb.append(refLabel);
        }
        return sb;
    }

    /**
     *  Set the assrtSubst array.
     *  <p>
     *  @param assrtSubst ParseNode array.
     */
    public void setAssrtSubst(ParseNode[] assrtSubst) {
        this.assrtSubst       = assrtSubst;
    }

    /**
     *  Get the proofTree.
     *  <p>
     *  @return proofTree.
     */
    public ParseTree getProofTree() {
        return proofTree;
    }

    /**
     *  Swap two hyp references in a DerivationStep.
     *
     *  This method is provided as a convenience for
     *  ProofUnifier.java.
     *
     *  @param i1 index of first hyp to swap
     *  @param i2 index of second hyp to swap
     */
    public void swapHyps(int i1,
                         int i2) {
        ProofStepStmt holdHypStmt
                              = hyp[i1];
        String        holdHypStep
                              = hypStep[i1];
        hyp[i1]               = hyp[i2];
        hypStep[i1]           = hypStep[i2];
        hyp[i2]               = holdHypStmt;
        hypStep[i2]           = holdHypStep;
    }


    /**
     *  Builds an error message detailing the "soft" DjVars errors
     *  and stores it in the heldDjErrorMessage field.
     *  <p>
     *  @param softDjVarsErrorList ArrayList of DjVars reported
     *             to be missing from the Dj Vars of the theorem
     *             which are needed by the Derivation Step.
     */
    public void buildSoftDjErrorMessage(
                            ArrayList softDjVarsErrorList) {

        heldDjErrorMessage        = null;
        if (softDjVarsErrorList == null
            ||
            softDjVarsErrorList.isEmpty()) {
            return;
        }

        StringBuffer s            = new StringBuffer();
        s.append(PaConstants.ERRMSG_SUBST_TO_VARS_NOT_DJ_1);
        s.append(step);
        s.append(PaConstants.ERRMSG_SUBST_TO_VARS_NOT_DJ_2);

        Iterator i                = softDjVarsErrorList.iterator();
        DjVars   offenders        = (DjVars)i.next();
        while (true) {
            s.append(offenders.toString());
            if (i.hasNext()) {
                s.append(
                    PaConstants.ERRMSG_SUBST_TO_VARS_NOT_DJ_3);
                offenders         = (DjVars)i.next();
                continue;
            }
            break;
        }

        heldDjErrorMessage        = s.toString();

    }

    /**
     *  Gets the HypArray sorted using a lazy evaluation to
     *  perform the sort.
     *  <p>
     *  @return Sorted array of ProofStepStmt for the DerivationStep.
     */
    public ProofStepStmt[] getSortedHypArray() {
        if (sortedHypArray        == null) {
            setSortedHypArray(
                DerivationStep.
                    sortDerivStepHypArray(
                        hyp));
        }
        return sortedHypArray;
    }

    /**
     *  Sets the sortedHypArray to null.
     */
    public void resetSortedHypArray() {
        setSortedHypArray(null);
    }

    /**
     *  Sets the sortedHypArray.
     *  <p>
     *  @param sortedHypArray sorted array of ProofStepStmt.
     */
    public void setSortedHypArray(ProofStepStmt[] sortedHypArray) {
        this.sortedHypArray       = sortedHypArray;
    }

    /**
     *  Gets the DerivationStep hyp array.
     *  <p>
     *  @return array of ProofStepStmt representing the
     *          DerivationStep's hypotheses.
     */
    public ProofStepStmt[] getHyp() {
        return hyp;
    }

    private boolean getValidDerivationRefField(
                            int lineStartCharNbr)
                                throws ProofAsstException {

        //ref checking: if input,
        //                  is < maxSeq,
        //                  is existing Assrt and
        //                      not a syntax axiom and
        //                  nbr log hyps matches hyp field count

        if (refLabel == null) {
            return true; //ok, unify normally outputs.
        }
        if (refLabel.equals(PaConstants.DEFAULT_STMT_LABEL)) {
            refLabel          = null;
            return true;
        }

        ref                   =
                (Stmt)w.logicalSystem.getStmtTbl().get(refLabel);
        if (ref == null) {
            w.triggerLoadStructureException(
                PaConstants.ERRMSG_REF_NOTFND_1
                + w.getErrorLabelIfPossible()
                + PaConstants.ERRMSG_REF_NOTFND_2
                + step
                + PaConstants.ERRMSG_REF_NOTFND_3
                + refLabel
                + PaConstants.ERRMSG_REF_NOTFND_4,
                (int)w.proofTextTokenizer.getCurrentCharNbr()
                    + 1
                    - formulaStartCharNbr);
        }
        if (ref.getSeq() >= w.getMaxSeq()) {
            w.triggerLoadStructureException(
                PaConstants.ERRMSG_REF_MAXSEQ_1
                + w.getErrorLabelIfPossible()
                + PaConstants.ERRMSG_REF_MAXSEQ_2
                + step
                + PaConstants.ERRMSG_REF_MAXSEQ_3
                + refLabel
                + PaConstants.ERRMSG_REF_MAXSEQ_4,
                (int)w.proofTextTokenizer.getCurrentCharNbr()
                    + 1
                    - formulaStartCharNbr);
        }
        if (!ref.isAssrt()) {
            w.triggerLoadStructureException(
                PaConstants.ERRMSG_REF_NOT_ASSRT_1
                + w.getErrorLabelIfPossible()
                + PaConstants.ERRMSG_REF_NOT_ASSRT_2
                + step
                + PaConstants.ERRMSG_REF_NOT_ASSRT_3
                + refLabel
                + PaConstants.ERRMSG_REF_NOT_ASSRT_4,
                (int)w.proofTextTokenizer.getCurrentCharNbr()
                    + 1
                    - formulaStartCharNbr);
        }

        if (ref.getFormula().getTyp() !=
            w.getProvableLogicStmtTyp()) {
            w.triggerLoadStructureException(
                PaConstants.ERRMSG_REF_BAD_TYP_1
                + w.getErrorLabelIfPossible()
                + PaConstants.ERRMSG_REF_BAD_TYP_2
                + step
                + PaConstants.ERRMSG_REF_BAD_TYP_3
                + refLabel
                + PaConstants.ERRMSG_REF_BAD_TYP_4
                + ref.getFormula().getTyp()
                + PaConstants.ERRMSG_REF_BAD_TYP_5,
                (int)w.proofTextTokenizer.getCurrentCharNbr()
                    + 1
                    - formulaStartCharNbr);
        }
        return true;
    }

    private int parseHypField(String hypField) {

        int questionMarks  = 0;

        ArrayList list     = new ArrayList();

        if (hypField != null     &&
            !hypField.equals("")) {

            DelimitedTextParser hypParser =
                new DelimitedTextParser(hypField);
            hypParser.setParseDelimiter(
                PaConstants.FIELD_DELIMITER_COMMA);
            hypParser.setQuoterEnabled(false);

            String s;
            while ((s             =
                    hypParser.nextField())
                   != null) {
                list.add(s);
            }
        }

        hypStep               = new String[list.size()];
        Iterator iterator     = list.iterator();
        int      i            = 0;
        while (iterator.hasNext()) {
            hypStep[i]      = (String)iterator.next();
            if (hypStep[i].compareTo(
                    PaConstants.DEFAULT_STMT_LABEL) == 0) {
                ++questionMarks;
            }
            ++i;
        }

        return questionMarks;
    }

    private int checkForValidHypEntries(
                            int lineStartCharNbr,
                            int nbrExpectedHyps)
                                throws ProofAsstException {

        int     nbrValidHyps     = 0;

        for (int i = 0; i < hypStep.length; i++) {
            if (hypStep[i].equals("")
                ||
                hypStep[i].equals(
                    PaConstants.DEFAULT_STMT_LABEL)) {
                continue;
            }

            if (hypStep[i].equals(step)) {
                w.triggerLoadStructureException(
                PaConstants.ERRMSG_BAD_HYP_STEP_1
                + w.getErrorLabelIfPossible()
                + PaConstants.ERRMSG_BAD_HYP_STEP_2
                + step
                + PaConstants.ERRMSG_BAD_HYP_STEP_3
                + hypStep[i]
                + PaConstants.ERRMSG_BAD_HYP_STEP_4,
                (int)w.proofTextTokenizer.getCurrentCharNbr()
                    + 1
                    - lineStartCharNbr);
            }

            ProofWorkStmt x   =
                w.findMatchingStepNbr(hypStep[i]);
            if (x == null) {
                w.triggerLoadStructureException(
                    PaConstants.ERRMSG_HYP_STEP_NOTFND_1
                    + w.getErrorLabelIfPossible()
                    + PaConstants.ERRMSG_HYP_STEP_NOTFND_2
                    + step
                    + PaConstants.ERRMSG_HYP_STEP_NOTFND_3
                    + hypStep[i]
                    + PaConstants.ERRMSG_HYP_STEP_NOTFND_4,
                    (int)w.proofTextTokenizer.getCurrentCharNbr()
                        + 1
                        - lineStartCharNbr);
            }

            hyp[i]            = (ProofStepStmt)x;

            ++nbrValidHyps;
        }

        if (nbrExpectedHyps != -1
                &&
            nbrValidHyps > nbrExpectedHyps) {
            w.triggerLoadStructureException(
                PaConstants.ERRMSG_REF_NBR_HYPS_LT_INPUT_1
                + w.getErrorLabelIfPossible()
                + PaConstants.ERRMSG_REF_NBR_HYPS_LT_INPUT_2
                + step
                + PaConstants.ERRMSG_REF_NBR_HYPS_LT_INPUT_3
                + hypStep.length
                + PaConstants.ERRMSG_REF_NBR_HYPS_LT_INPUT_4
                + nbrExpectedHyps
                + PaConstants.ERRMSG_REF_NBR_HYPS_LT_INPUT_5
                + refLabel
                + PaConstants.ERRMSG_REF_NBR_HYPS_LT_INPUT_6,
                (int)w.proofTextTokenizer.getCurrentCharNbr()
                    + 1
                    - lineStartCharNbr);
        }

        return nbrValidHyps;
    }

    /*
     *  OK, a Ref label was entered and
     *  the input nbrValidHyps <= nbrExpectedHyps
     *  for the Ref.
     *
     *  All that needs to be done is output hyp
     *  and hypStep arrays that are of the correct
     *  length (nbrExpectedHyps), to expand/smoosh
     *  the input to match, and to replace any
     *  remaining (post-smoosh) "" hyp entries with "?".
     */
    private void resizeHypField(int nbrExpectedHyps) {
        String[]        outHypStep
                              = new String[nbrExpectedHyps];
        ProofStepStmt[] outHyp
                              =
            new ProofStepStmt[nbrExpectedHyps];

        for (int i = 0; i < outHypStep.length; i++) {
            if (i >= hypStep.length) {
                outHypStep[i] = PaConstants.DEFAULT_STMT_LABEL;
                continue;
            }
            if (hypStep[i].equals("") ||
                hypStep[i].equals(
                    PaConstants.DEFAULT_STMT_LABEL)) {
                outHypStep[i] = PaConstants.DEFAULT_STMT_LABEL;
                continue;
            }
            outHypStep[i]     = hypStep[i];
            outHyp[i]         = hyp[i];
            continue;
        }

        if (hypStep.length > outHypStep.length) {
            int lastOutputIndex
                              = outHypStep.length - 1;
            for (int i = outHypStep.length;
                 i < hypStep.length;
                 i++) {
                if (hypStep[i].equals("") ||
                    hypStep[i].equals(
                        PaConstants.DEFAULT_STMT_LABEL)) {
                    continue;
                }
                if (!outHypStep[lastOutputIndex].equals(
                    PaConstants.DEFAULT_STMT_LABEL)) {
                    smooshLeft(lastOutputIndex,
                               outHypStep,
                               outHyp);
                }
                //make the move...
                outHypStep[lastOutputIndex]
                                  = hypStep[i];
                outHyp[lastOutputIndex]
                                  = hyp[i];
            }
        }

        hyp               = outHyp;
        hypStep           = outHypStep;
    }

    private void smooshLeft(int             lastOutputIndex,
                            String[]        outHypStep,
                            ProofStepStmt[] outHyp) {
        int i = 0;
        for (;
             i < lastOutputIndex;
             i++) {
            if (outHypStep[i].equals(
                PaConstants.DEFAULT_STMT_LABEL)) {
                outHypStep[i]         = outHypStep[i + 1];
                outHyp[i]             = outHyp[i + 1];
                return;
            }
        }
        throw new IllegalArgumentException(
            PaConstants.ERRMSG_SMOOSH_FAILED);
    }

    /*
     *  OK, we are *not* in deriveStepHyps mode, so either
     *  a Ref was not entered,
     *  or,
     *  the input nbrValidHyps > nbrExpectedHyps.
     *
     *  For the old, non-deriveStepHyps processing, the number of
     *  input hyps must match the Ref's number of logical
     *  hypotheses, unless "?" or "" was input for one
     *  of them. And "" hyps are automatically converted to
     *  "?". If any of the input hyps are "?" or "" then the
     *  step is given status "INCOMPLETE_HYPS".
     */
    private void validateNonDeriveFeatureHyp(
                              int lineStartCharNbr,
                              int nbrExpectedHyps,
                              int nbrValidHyps)
                                  throws ProofAsstException {

        hypFldIncomplete      = false;

        for (int i = 0; i < hypStep.length; i++) {
            if (hypStep[i].equals("")
                ||
                hypStep[i].equals(
                    PaConstants.DEFAULT_STMT_LABEL)) {
                hypStep[i]    = PaConstants.DEFAULT_STMT_LABEL;

                hypFldIncomplete  = true;
            }
        }

        if (nbrExpectedHyps != -1  // a valid Ref was entered
                &&
            !hypFldIncomplete) {
            w.triggerLoadStructureException(
                PaConstants.ERRMSG_REF_NBR_HYPS_1
                + w.getErrorLabelIfPossible()
                + PaConstants.ERRMSG_REF_NBR_HYPS_2
                + step
                + PaConstants.ERRMSG_REF_NBR_HYPS_3
                + hypStep.length
                + PaConstants.ERRMSG_REF_NBR_HYPS_4
                + nbrExpectedHyps
                + PaConstants.ERRMSG_REF_NBR_HYPS_5
                + refLabel
                + PaConstants.ERRMSG_REF_NBR_HYPS_6,
                (int)w.proofTextTokenizer.getCurrentCharNbr()
                    + 1
                    - lineStartCharNbr);
        }
    }

    private String computeLogHypsL1HiLoKey() {
        String hiLoKey            = "";

        if (hyp.length > 0) {
            Stmt      hStmt;
            int       n;
            String    low         = null;
            String    high        = null;
            int       lowNbr      = Integer.MAX_VALUE;
            int       highNbr     = Integer.MIN_VALUE;
            for (int i = 0; i < hyp.length; i++) {

                // hyp[i].formulaParseTree may be null
                // if formula null and new "Derive" feature in use!
                if (hyp[i] == null ||
                    hyp[i].formulaParseTree == null) {
                    return hiLoKey;
                }

                hStmt             =
                    hyp[i].formulaParseTree.getRoot().getStmt();
                if (hStmt.isVarHyp()) {
                    return hiLoKey;
                }
                n                 = hStmt.getSeq();
                if (n < lowNbr) {
                    lowNbr        = n;
                    low           = hStmt.getLabel();
                }
                if (n > highNbr) {
                    highNbr       = n;
                    high          = hStmt.getLabel();
                }
            }
            hiLoKey               = new String(high + " " + low);
        }
        return hiLoKey;
    }

    private int computeLogHypsMaxDepth() {

        int hypMaxDepth = 0;
        int hypDepth;
        for (int i = 0; i < hyp.length; i++) {
            if (hyp[i]                  == null  ||
                hyp[i].formulaParseTree == null  ||
                hyp[i].workVarList      != null) {
                return 0;
            }
            if ((hypDepth =
                    hyp[i].formulaParseTree.getMaxDepth())
                > hypMaxDepth) {
                hypMaxDepth = hypDepth;
            }
        }
        return hypMaxDepth;
    }
}
