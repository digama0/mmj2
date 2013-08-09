//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * ProofUnifier.java  0.09 08/01/2008
 * Version 0.02:
 *     - extensive changes to incorporate new Proof Assistant
 *       "Derive" Feature.
 * 09-Sep-2006 - Version 0.03 - TMFF Enhancement
 *     - added get routine for Unify Search List (to be used
 *       in creation of Proof Asst GUI Get Forward/Backward Proof.)
 * 30-Oct-2006 - Patch for bug.
 *
 * 01-Jun-2007 - Version 0.04
 *     - generate $d's for "soft" DJ Vars errors
 *     - replace ProofWorkStmt.status
 *
 * Aug-01-2007 - Version 0.05
 *     - Work Var Enhancement, misc. changes.
 *
 * Nov-01-2007 Version 0.06
 *     - modify "markStepUnified() function:
 *       eliminate cursor positioning call for E-PA-0403,
 *       (example: E-PA-0403 Theorem syllogism Step qed: Proof
 *       incomplete for derivation proof step. The step was
 *       successfully unified with Ref ax-mp, but one (or more)
 *       of the step's hypotheses has an incomplete Hyp value (='?')."
 *       Also change from error message to *info* message.
 *     - Modified generateDerivStepHyps() to load the
 *       proofLevel number in the generated hypotheses.
 *
 * Feb-01-2008 Version 0.07
 *     - Modified for new RunParms:
 *           "ProofAsstIncompleteStepCursor"
 *           "ProofAsstAutoReformat"
 *     - Patch bug in hypVarsInCommonWithAssrt() wherein
 *       assertion with no variables resulted in
 *       ArrayIndexOutOfBoundsException. Doh!
 *
 * Mar-01-2008 Version 0.08
 *     - Misc. tidy up.
 *     - Make sortDerivStepHypArray() "public static" for
 *       reuse in StepSelectorSearch.java and move to
 *       DerivationStep.
 *     - Moved sortAssrtLogHypArray() to Assrt, along with
 *             hypVarsInCommonWithAssrt(). Sorting will be
 *       done at file load time.
 *     - Removed hints feature
 *     - Removed "alternates" feature for Refs input
 *       which fail to unify (keep alternates for Dj
 *       errors).
 *
 * Aug-01-2008 Version 0.09
 *     - modified unifySearchList allocation to use freespace
 *       parameter, and added update of it for TheoremLoader
 *       updates (saving total rebuilds for each TheoremLoader
 *       update.)
 */

package mmj.pa;

import java.util.*;

import mmj.lang.*;
import mmj.verify.Grammar;
import mmj.verify.VerifyProofs;

/**
 * ProofUnifier is a separate class simply to break out the unification code
 * from everything else. Logically it could be part of ProofAsst.
 * <p>
 * The job here is to unify the steps in a ProofWorksheet and build the proof
 * trees for the derivation steps in the proof. The proof tree of the 'qed' step
 * equals the proof of the Theorem, fyi.
 * <p>
 * There are five main things happening here (whew!):
 * <ol>
 * <li>Figure out the correct sequence of the LogHyp's on each derivation step
 * (if the input sequence turns out to be wrong.)
 * <li>Figure out the Ref label, if not input, that justifies each derivation
 * step;
 * <li>Derive the step's formula and/or logical hypotheses when the Ref label is
 * input and formula is missing or 0-n of the hyps are missing -- or both. This
 * is the (new) Proof Assistant "Derive" Feature. An in-depth description of its
 * required behaviors in ProofUnifier.java is provided below, but basically,
 * once the required fields are successfully derived, the derivation step is to
 * be handled normally -- and if the input Ref does not unify with its
 * associated input hyp(s), then the search for alternates is performed (the
 * "Derive" feature is to be tightly and seamlessly integrated into the normal
 * unification process!)
 * <li>When Ref label is input on a derivation step, use it instead of searching
 * for it, but if the Ref label has a unification error or a Distinct Variables
 * Restriction (DjVars) error, perform the search to generate a message
 * informing the user of alternate Refs. In the case of a DjVars error, if
 * another Ref is found that unifies perfectly without a DjVars error, take it
 * (this might be slightly odd and unusual if another Ref unifies perfectly with
 * "Derive" generated Hyps!)
 * <li>Once a derivation step has been "unified", its proof tree must be built
 * and checked for Distinct Variables Restriction errors. Since a step's proof
 * tree requires that its logical hypotheses have proof trees, a derivation step
 * is not errored if one of its hypotheses is not yet proved (for example, has
 * "?" in its Hyp). This allows for proving backwards, from conclusion to
 * premisses.
 * </ol>
 * <p>
 * The algorithm to do these things is not godlike :) It searches for the first
 * Assrt that successfully unifies with each proof step -- if the Ref is not
 * input. But there may be multiple assertions that unify with a step, and it is
 * possible that unification as a whole fails because an earlier step's Ref
 * label is wrong. The "unification" process means finding a consistent set of
 * variable substitutions. To unify an entire proof the requirement is that the
 * substitutions be consistent across the entire proof. However, the algorithm
 * does work for set.mm and ql.mm without any false positives. (As a fallback,
 * the user can input Ref to avoid the problem of multiple possible
 * unifications.)
 * <p>
 * Also, a RunParm is provided to specify double-checking each unification using
 * the VerifyProofs.java object -- i.e. the Metamath
 * "Proof Verification Engine". The default option is NO, but should probably be
 * YES in practice given that response time is not a problem (yet?) This will
 * catch false unifications early.
 * <p>
 * It should also be noted that ProofUnifier does check Distinct Variable ($d)
 * restrictions as it works on each proof step. This feature is an enhancement
 * to the Metamath Proof Assistant, which does not check $d's. By checking $d's
 * here we also avoid false unification positives and improve the results of the
 * algorithm.
 * <p>
 */
public class ProofUnifier {

    private final ProofAsstPreferences proofAsstPreferences;
    private final LogicalSystem logicalSystem;
    private final Grammar grammar;
    private final VerifyProofs verifyProofs;
    private StepSelectorSearch stepSelectorSearch;

    private Messages messages;
    private ProofWorksheet proofWorksheet;

    private Cnst provableLogicStmtTyp;

    // only one lookup table for now - sorted by MObj.seq
    private List<Assrt> unifySearchList = null;

    public List<Assrt> getUnifySearchListByMObjSeq() {
        return unifySearchList;
    }

    private boolean tablesInitialized = false;

    public boolean getTablesInitialized() {
        return tablesInitialized;
    }

    // softDjVarsErrorList contains $d statements generated
    // in VerifyProofs for a single step --
    private List<DjVars> holdSoftDjVarsErrorList;

    private final ParseNode[] unifyNodeStack = new ParseNode[PaConstants.UNIFIER_NODE_STACK_SIZE];

    private final ParseNode[] compareNodeStack = new ParseNode[PaConstants.UNIFIER_NODE_STACK_SIZE];

    /*
     * Global "work" areas for processing a single
     * theorem:
     */

    private Assrt assrt;
    private int assrtNbrLogHyps;
    private ParseTree assrtParseTree;

    // these are global, deep in the unification process
    private VarHyp[] assrtVarHypArray;
    private ParseNode[] assrtFormulaSubst;
    private Hyp[] assrtHypArray;
    private ParseNode[] assrtSubst;
    private LogHyp[] assrtLogHypArray;

    private DerivationStep derivStep;

    private int dsa1Count;
    private DerivationStep[] dsa1;

    private ProofStepStmt[] derivStepHypArray;
    private ParseNode[][][] substAnswer;
    private final ParseNode[] substAnswerImpossible = new ParseNode[0];

    private final int[] levelCleanupCnt = new int[PaConstants.UNIFIER_MAX_LOG_HYPS];

    private final int[][] levelCleanup = new int[PaConstants.UNIFIER_MAX_LOG_HYPS][];

    private boolean[] assrtHypUsed;
    private int[] derivAssrtXRef;
    private int[] impossibleCnt;

    /* ***********************************************************
     * ***********************************************************
     * ****************                         ******************
     * ALL NEW CODE FOR unifyStepWithWorkVars() FOLLOWS!!!!!!!!!!!
     * ****************                         ******************
     * ***********************************************************
     * ***********************************************************/
    private final StepUnifier stepUnifier;

    /**
     * Standard constructor for set up.
     * 
     * @param proofAsstPreferences variable settings
     * @param logicalSystem the loaded Metamath data
     * @param grammar the mmj.verify.Grammar object
     * @param verifyProofs the mmj.verify.VerifyProofs object
     */
    public ProofUnifier(final ProofAsstPreferences proofAsstPreferences,
        final LogicalSystem logicalSystem, final Grammar grammar,
        final VerifyProofs verifyProofs)
    {
        this.proofAsstPreferences = proofAsstPreferences;
        this.logicalSystem = logicalSystem;
        this.grammar = grammar;
        this.verifyProofs = verifyProofs;

        stepUnifier = proofAsstPreferences.getStepUnifierInstance();

    }

    /**
     * Initialize lookup tables to be used across multiple executions of the
     * GUI.
     * <p>
     * This is the place to create optimizations of search tables, etc. for the
     * Unification process.
     * <p>
     * <ol>
     * <li>The Grammar's Provable Logic Statement Type Code is fetched and
     * cached</li>
     * <li>LogicalSystem.stmtTbl is sorted into ascending database sequence
     * (MObj.seq) in an ArrayList (only Theorems and Axioms with the Provable
     * Logic Statment Type (i.e. "|-" are included.)</li>
     * </ol>
     * 
     * @param messages the mmj.lang.Messages object used to store error and
     *            informational messages.
     * @return boolean true if tables initialized successfully.
     */
    public boolean initializeLookupTables(final Messages messages) {

        provableLogicStmtTyp = getProvableLogicStmtTyp();

        final List<Assrt> unifySearchListUnsorted = new ArrayList<Assrt>(
            logicalSystem.getStmtTbl().size());

        for (final Stmt stmt : logicalSystem.getStmtTbl().values())
            if (stmt.isAssrt()
                && stmt.getFormula().getTyp() == provableLogicStmtTyp
                && !proofAsstPreferences.checkUnifySearchExclude((Assrt)stmt))
                unifySearchListUnsorted.add((Assrt)stmt);

        final int listSize = unifySearchListUnsorted.size()
            * (100 + proofAsstPreferences.getAssrtListFreespace()) / 100;
        unifySearchList = new ArrayList<Assrt>(listSize);

        unifySearchList.addAll(unifySearchListUnsorted);

        Collections.sort(unifySearchList, MObj.SEQ);

        final Assrt[] excl = proofAsstPreferences.getUnifySearchExclude();
        if (excl.length > 0) {
            final StringBuilder sb = new StringBuilder();
            sb.append(PaConstants.ERRMSG_UNIFY_SEARCH_EXCLUDE_1);
            sb.append(excl[0].getLabel());
            for (int i = 1; i < excl.length; i++) {
                sb.append(PaConstants.ERRMSG_UNIFY_SEARCH_EXCLUDE_2);
                sb.append(excl[i].getLabel());
            }
            messages.accumInfoMessage(sb.toString());
        }

        stepSelectorSearch = new StepSelectorSearch(proofAsstPreferences,
            verifyProofs, provableLogicStmtTyp, unifySearchList);

        tablesInitialized = true;

        return tablesInitialized;
    }

    public List<Assrt> getSortedAssrtSearchList() {
        return stepSelectorSearch.getSortedAssrtSearchList();
    }

    /**
     * Merges a list of added Assrt objects sorted by MObj seq into the
     * unifySearchList and passes the list on to the StepSelectorSearch for its
     * updates.
     * 
     * @param listOfAssrtAddsSortedBySeq List of Assrt sorted by MObj.seq
     *            representing new assertions which were added to the
     *            LogicalSystem.
     */
    public void mergeListOfAssrtAddsSortedBySeq(
        final List<Theorem> listOfAssrtAddsSortedBySeq)
    {

        // new MergeSortedArrayLists(
        // unifySearchList, listOfAssrtAddsSortedBySeq, MObj.SEQ, true); //
        // abortIfDupsFound

        stepSelectorSearch
            .mergeListOfAssrtAddsSortedBySeq(listOfAssrtAddsSortedBySeq);
    }

    /**
     * Unifies the proof steps in a Proof Worksheet.
     * <p>
     * This is called by ProofAsst.java.
     * <p>
     * The "parallelStepUnificationMethod()" is used for unification. This means
     * that one pass is made through LogicalSystem.stmtTbl and for each Stmt, an
     * attempt is made to unify each un-unified proof step with that Stmt.
     * Speed-wise, this works fine now. In theory, if set.mm had 1 million
     * Theorems things would be uglier and it might be better to use Stmt lookup
     * tables to unify each proof step, one by one. But today the extra coding
     * effort to build those tables might not even produce an improvement in
     * performance. The longest Theorem proof unification is around 500,000,000
     * nanoseconds -- or 1/2 second -- which is acceptable for the
     * ProofAsstGUI's response time. (The average unification time is much less,
     * like 1/10 second.)
     * 
     * @param proofWorksheet proof in progress
     * @param messages the mmj.lang.Messages object used to store error and
     *            informational messages.
     * @throws VerifyException if an error occurs
     */
    public void unifyAllProofDerivationSteps(
        final ProofWorksheet proofWorksheet, final Messages messages)
        throws VerifyException
    {

        if (!getTablesInitialized())
            throw new IllegalArgumentException(
                PaConstants.ERRMSG_UNIFY_TABLES_NOT_INIT_1);

        this.proofWorksheet = proofWorksheet;

        holdSoftDjVarsErrorList = new ArrayList<DjVars>();

        this.messages = messages;

        if (proofWorksheet.stepRequest != null
            && (proofWorksheet.stepRequest.request == PaConstants.STEP_REQUEST_GENERAL_SEARCH || proofWorksheet.stepRequest.request == PaConstants.STEP_REQUEST_SEARCH_OPTIONS
                && proofWorksheet.stepRequest.param1 == null))
            return;

        // ...also loads dsa1 array for input to
        // parallelStepUnificationMethod
        unifyStepsHavingRefLabels();

        if (proofWorksheet.stepRequest != null
            && (proofWorksheet.stepRequest.request == PaConstants.STEP_REQUEST_SELECTOR_SEARCH
                || proofWorksheet.stepRequest.request == PaConstants.STEP_REQUEST_STEP_SEARCH || proofWorksheet.stepRequest.request == PaConstants.STEP_REQUEST_SEARCH_OPTIONS))
            return; // our work here is complete :-)

        if (dsa1Count > 0)
            // still some left to unify!
            parallelStepUnificationMethod();

        final DerivationStep d = proofWorksheet.getQedStep();

        if (d.unificationStatus == PaConstants.UNIFICATION_STATUS_UNIFIED
            || d.unificationStatus == PaConstants.UNIFICATION_STATUS_UNIFIED_W_WORK_VARS)
            // do fixup before convertWorkVarsToDummyVars()
            chainWorkVarAndIncompleteHypStatuses();

        if (d.unificationStatus == PaConstants.UNIFICATION_STATUS_UNIFIED_W_WORK_VARS)
            try {
                convertWorkVarsToDummyVars(d);
            } catch (final VerifyException e) {
                messages.accumErrorMessage(e.getMessage());
                proofWorksheet.getProofCursor().setCursorAtProofWorkStmt(d,
                    PaConstants.FIELD_ID_REF);
            }

        final boolean hasHardDjVarsErrors = buildProofsAndErrorUnUnifiedSteps();

        if (d.proofTree != null && !hasHardDjVarsErrors) {
            // Well, Allrighty then!
        }
        else
            reportAlternateUnifications();
    }
    /*
     * a final fixup to ensure that we do not attempt
     * work var -> dummy var conversion unless everything
     * is kosher (and no incomplete unifications among the hyps).
     */
    private void chainWorkVarAndIncompleteHypStatuses() {

        for (final ProofWorkStmt proofWorkStmtObject : proofWorksheet
            .getProofWorkStmtList())
        {

            if (proofWorkStmtObject.isDerivationStep())
                derivStep = (DerivationStep)proofWorkStmtObject;
            else
                continue;

            if (derivStep.unificationStatus != PaConstants.UNIFICATION_STATUS_UNIFIED
                && derivStep.unificationStatus != PaConstants.UNIFICATION_STATUS_UNIFIED_W_WORK_VARS)
                continue;

            for (int i = 0; i < derivStep.hyp.length; i++) {

                if (!derivStep.hyp[i].isDerivationStep())
                    continue;
                final DerivationStep dH = (DerivationStep)derivStep.hyp[i];

                if (derivStep.unificationStatus == PaConstants.UNIFICATION_STATUS_UNIFIED)
                    if (dH.workVarList != null
                        || dH.unificationStatus == PaConstants.UNIFICATION_STATUS_UNIFIED_W_WORK_VARS)
                        derivStep.unificationStatus = PaConstants.UNIFICATION_STATUS_UNIFIED_W_WORK_VARS;

                if (dH.unificationStatus == PaConstants.UNIFICATION_STATUS_UNIFIED_W_INCOMPLETE_HYPS)
                    derivStep.unificationStatus = PaConstants.UNIFICATION_STATUS_UNIFIED_W_INCOMPLETE_HYPS;

                if (dH.hypFldIncomplete
                    || dH.unificationStatus == PaConstants.UNIFICATION_STATUS_UNIFIED_W_INCOMPLETE_HYPS
                    || dH.unificationStatus != PaConstants.UNIFICATION_STATUS_UNIFIED
                    && dH.unificationStatus != PaConstants.UNIFICATION_STATUS_UNIFIED_W_WORK_VARS)
                {

                    derivStep.unificationStatus = PaConstants.UNIFICATION_STATUS_UNIFIED_W_INCOMPLETE_HYPS;

                    break;
                }
            }
        }
    }
    /**
     * Makes a pass through the ProofWorksheet looking for derivation proof
     * steps containing a Ref.
     * <p>
     * When Ref is input by the user the Unification process does not need to
     * search the database looking for a justificatory assertion (Ref) -- unless
     * the Ref is invalid or has Distinct Variable Restriction errors, in which
     * case the search is performed so that a helpful message can be produced or
     * an assertion without a DV error can be found.
     * <p>
     * An array, "dsa1", is output, which contains the derivation steps that
     * need to be processed in the parallelStepUnificationMethod routine.
     * <p>
     * In addition, the ProofWorksheet's ArrayList of statement may be updated
     * with new derivation steps created by the (new) Proof Assistant "Derive"
     * feature.
     * <p>
     * Note: the input ProofWorksheet has been fully validated prior to its
     * visit here.
     * 
     * @throws VerifyException if an error occurs
     */
    private void unifyStepsHavingRefLabels() throws VerifyException {

        // 1) Make pass for steps with Ref labels involving work vars
        if (proofWorksheet.hasWorkVarsOrDerives
            || proofWorksheet.stepRequest != null
            && (proofWorksheet.stepRequest.request == PaConstants.STEP_REQUEST_SELECTOR_SEARCH
                || proofWorksheet.stepRequest.request == PaConstants.STEP_REQUEST_STEP_SEARCH || proofWorksheet.stepRequest.request == PaConstants.STEP_REQUEST_SEARCH_OPTIONS))
            unifyStepsInvolvingWorkVars();

        if (proofWorksheet.stepRequest != null
            && (proofWorksheet.stepRequest.request == PaConstants.STEP_REQUEST_SELECTOR_SEARCH
                || proofWorksheet.stepRequest.request == PaConstants.STEP_REQUEST_STEP_SEARCH || proofWorksheet.stepRequest.request == PaConstants.STEP_REQUEST_SEARCH_OPTIONS))
            return; // our work here is complete :-)

        // 2) Make pass for steps with Ref labels that don't
        // involve work vars or derives.
        dsa1Count = 0;
        dsa1 = new DerivationStep[proofWorksheet.getProofWorkStmtListCnt()];

        stepLoop: for (final ProofWorkStmt proofWorkStmtObject : proofWorksheet
            .getProofWorkStmtList())
        {
            if (!proofWorkStmtObject.isDerivationStep())
                continue stepLoop;

            derivStep = (DerivationStep)proofWorkStmtObject;

            if (derivStep.hypFldIncomplete)
                continue stepLoop;

            if (derivStep.ref == null && derivStep.hasWorkVarsInStepOrItsHyps())
                continue stepLoop;

            if (derivStep.unificationStatus != PaConstants.UNIFICATION_STATUS_NOT_UNIFIED)
                // already unified/failed in
                // unifyStepsInvolvingWorkVars()
                continue stepLoop;

            // this is b.s. but in theory a hyp step could have
            // failed in deriveFormula...
            for (int i = 0; i < derivStep.hyp.length; i++)
                if (derivStep.hyp[i] != null
                    && derivStep.hyp[i].formulaParseTree == null)
                {

                    // this will
                    // just be bypassed and considered incomplete
                    // based on the idea that the failed earlier
                    // step will generate a message
                    derivStep.unificationStatus = PaConstants.UNIFICATION_STATUS_ATTEMPT_CANCELLED;
                    continue stepLoop;
                }

            // build array of steps for parallel unification loop
            if (derivStep.ref == null) {
                dsa1[dsa1Count++] = derivStep;
                continue stepLoop;
            }

            assrt = (Assrt)derivStep.ref;
            assrtNbrLogHyps = assrt.getLogHypArrayLength();

            if (derivStep.hyp.length != assrtNbrLogHyps)
                throw new IllegalArgumentException(
                    PaConstants.ERRMSG_STEP_REF_HYP_NBR_ERR_1
                        + getErrorLabelIfPossible(proofWorksheet)
                        + PaConstants.ERRMSG_STEP_REF_HYP_NBR_ERR_2
                        + derivStep.step
                        + PaConstants.ERRMSG_STEP_REF_HYP_NBR_ERR_3);

            if (!unifyStepWithoutWorkVars()) {

                markRefUnificationFailure(assrt);
                continue stepLoop;
            }

            if (derivStep.djVarsErrorStatus != PaConstants.DJ_VARS_ERROR_STATUS_NO_ERRORS)
                // unified but build alternates list!!!
                dsa1[dsa1Count++] = derivStep;
            continue stepLoop;
        }

    }

    private void parallelStepUnificationMethod() throws VerifyException {

        final int maxSeq = proofWorksheet.getMaxSeq();
        int nbrCompleted = 0;

        assrtLoop: for (final Assrt a : unifySearchList) {
            if (a.getSeq() >= maxSeq)
                // halt the scan -- the list is sorted!!!
                break assrtLoop;

            assrt = a;
            assrtNbrLogHyps = assrt.getLogHypArrayLength();

            int i = -1;
            final int max = dsa1Count; // fool optimizer
            loopDSA1: while (true) {
                if (++i >= max)
                    break loopDSA1;
                derivStep = dsa1[i];

                if (derivStep.hyp.length == assrtNbrLogHyps)
                    if (unifyStepWithoutWorkVars())
                        if (derivStep.djVarsErrorStatus == PaConstants.DJ_VARS_ERROR_STATUS_NO_ERRORS)
                        {
                            // stick fork in it, this one is done!
                            dsa1[i] = null;
                            ++nbrCompleted;
                        }
                continue loopDSA1;
            }

            // this could be optimized a little :-)
            if (nbrCompleted > 0) {
                loopJ: for (int j = 0; j < dsa1Count - 1; j++)
                    if (dsa1[j] == null) {
                        int m = j;
                        int n = j;
                        loopN: while (true) {
                            if (++n >= dsa1Count)
                                break loopJ;
                            if (dsa1[n] == null)
                                continue loopN;
                            break loopN;
                        }
                        while (n < dsa1Count)
                            dsa1[m++] = dsa1[n++];
                    }
                dsa1Count -= nbrCompleted;
                nbrCompleted = 0;
            }

            if (dsa1Count > 0)
                continue assrtLoop;
            break assrtLoop;
        }
    }

    private boolean buildProofsAndErrorUnUnifiedSteps() {
        // ok, finish: error un-unified steps and
        // build proofs for the unified steps!

        boolean hardDjVarsErrorsFound = false;

        for (final ProofWorkStmt proofWorkStmtObject : proofWorksheet
            .getProofWorkStmtList())
        {

            if (proofWorkStmtObject.isDerivationStep())
                derivStep = (DerivationStep)proofWorkStmtObject;
            else
                continue;

            if (derivStep.djVarsErrorStatus == PaConstants.DJ_VARS_ERROR_STATUS_HARD_ERRORS)
                hardDjVarsErrorsFound = true;

            if (derivStep.unificationStatus == PaConstants.UNIFICATION_STATUS_NOT_UNIFIED)
            {
                if (derivStep.hypFldIncomplete) {}
                else
                    markUnificationFailure();
            }
            else if (derivStep.unificationStatus == PaConstants.UNIFICATION_STATUS_UNIFIED
                || derivStep.unificationStatus == PaConstants.UNIFICATION_STATUS_UNIFIED_W_WORK_VARS)
                attemptProofOfDerivStep();

            if (derivStep.djVarsErrorStatus == PaConstants.DJ_VARS_ERROR_STATUS_NO_ERRORS)
                continue;

            // accum ArrayLists of DjVars into ProofWorksheet
            // ArrayList of Arraylists of DjVars...
            if (derivStep.djVarsErrorStatus == PaConstants.DJ_VARS_ERROR_STATUS_SOFT_ERRORS
                && derivStep.softDjVarsErrorList != null
                && !derivStep.softDjVarsErrorList.isEmpty())
            {
                // that means we are generating $d's!
                if (proofWorksheet.proofSoftDjVarsErrorList == null)
                    proofWorksheet.proofSoftDjVarsErrorList = new ArrayList<List<DjVars>>();
                proofWorksheet.proofSoftDjVarsErrorList
                    .add(derivStep.softDjVarsErrorList);
            }

            if (derivStep.djVarsErrorStatus == PaConstants.DJ_VARS_ERROR_STATUS_HARD_ERRORS
                || proofAsstPreferences.getDjVarsSoftErrorsReport()
                && derivStep.djVarsErrorStatus == PaConstants.DJ_VARS_ERROR_STATUS_SOFT_ERRORS)
            {

                if (derivStep.alternateRefList != null)
                    messages.accumErrorMessage(derivStep.heldDjErrorMessage
                        + buildAlternateRefMessage(derivStep));
                else
                    messages.accumErrorMessage(derivStep.heldDjErrorMessage);
                proofWorksheet.getProofCursor().setCursorAtProofWorkStmt(
                    derivStep, PaConstants.FIELD_ID_REF);
            }
        }
        return hardDjVarsErrorsFound;
    }

    private void reportAlternateUnifications() {

        for (final ProofWorkStmt proofWorkStmtObject : proofWorksheet
            .getProofWorkStmtList())
            if (proofWorkStmtObject.isDerivationStep()) {

                derivStep = (DerivationStep)proofWorkStmtObject;

                if (derivStep.djVarsErrorStatus == // already reported
                PaConstants.DJ_VARS_ERROR_STATUS_NO_ERRORS
                    && derivStep.alternateRefList != null
                    && !derivStep.alternateRefList.isEmpty())
                {
                    messages
                        .accumErrorMessage(buildAlternateRefMessage(derivStep));
                    proofWorksheet.getProofCursor().setCursorAtProofWorkStmt(
                        derivStep, PaConstants.FIELD_ID_REF);
                }

            }
    }

    private String buildAlternateRefMessage(final DerivationStep derivStep) {
        if (derivStep.alternateRefList.isEmpty())
            return null;

        return "\n" + PaConstants.ERRMSG_ALT_UNIFY_REFS_1
            + getErrorLabelIfPossible(proofWorksheet)
            + PaConstants.ERRMSG_ALT_UNIFY_REFS_2 + derivStep.step
            + PaConstants.ERRMSG_ALT_UNIFY_REFS_3 + derivStep.alternateRefList;
    }

    /**
     * At this point 90% of the work has already been accomplished in building
     * the proof. We have available for the derivStep:
     * <ul>
     * <li>Stmt ref --> Assrt which justifies the step
     * <li>ParseNode[] assrtSubst which contains the variable substitutions for
     * the mandatory variables of the Ref assertion. Note that assrtSubst is a
     * parallel array to Assrt.mandFrame.hypArray (which we obtain via
     * assrt.getMandHypArrayLength()), so ... we only need to fill in the empty
     * nodes with the proof trees from the derivStep.hyp statements and then
     * create a root node using ref! Piece of cake!
     * <p>
     * The major chunk of work is hard though: we must confirm that the Distinct
     * Variable restrictions for the theorem are sufficient to satisfy the
     * assertion referenced in the proof step.
     * <p>
     * Also, for new theorems (not already present in the Metamath file(s) that
     * was loaded), the theorem's logical hypotheses (step nbr prefixed with
     * 'h') must have actual mmj.lang.LogHyp objects created. These are used not
     * just for VerifyProofs, but for generating the final RPN proof from the
     * proof tree.
     */
    private void attemptProofOfDerivStep() {

        ProofStepStmt holdHypProofStepStmt;
        ParseNode holdHypNode;
        int substIndex = -1;
        for (int i = 0; i < derivStep.hyp.length; i++) {

            holdHypProofStepStmt = derivStep.hyp[i];

            // Must be a HypothesisStep (otherwise status
            // would have been changed)
            if (holdHypProofStepStmt.isHypothesisStep()) {

                if (proofWorksheet.isNewTheorem()
                    && holdHypProofStepStmt.ref == null)
                    buildNewTheoremLogHyp(i,
                        (HypothesisStep)holdHypProofStepStmt);
                holdHypNode = new ParseNode(holdHypProofStepStmt.ref);
                holdHypNode.setChild(new ParseNode[0]);
            }
            else {
                final DerivationStep d = (DerivationStep)holdHypProofStepStmt;
                final ParseTree hypProofTree = d.proofTree;
                if (hypProofTree != null) {
                    holdHypNode = hypProofTree.getRoot();
                    if (d.unificationStatus == PaConstants.UNIFICATION_STATUS_UNIFIED_W_WORK_VARS)
                        derivStep.unificationStatus = PaConstants.UNIFICATION_STATUS_UNIFIED_W_WORK_VARS;
                }
                else {
                    // No need for message as the prev step has one.
                    derivStep.unificationStatus = PaConstants.UNIFICATION_STATUS_UNIFIED_W_INCOMPLETE_HYPS;
                    return;
                }
            }

            while (++substIndex < derivStep.assrtSubst.length)
                if (derivStep.assrtSubst[substIndex] == null) {
                    derivStep.assrtSubst[substIndex] = holdHypNode;
                    break;
                }

            // If no empty entry in array for hyp's proof tree root
            // then we are FUBAR
            if (substIndex >= derivStep.assrtSubst.length)
                throw new IllegalArgumentException(
                    PaConstants.ERRMSG_ASSRT_SUBST_SLOT_1
                        + getErrorLabelIfPossible(proofWorksheet)
                        + PaConstants.ERRMSG_ASSRT_SUBST_SLOT_2
                        + derivStep.step
                        + PaConstants.ERRMSG_ASSRT_SUBST_SLOT_3 + i);
        }

        final ParseNode proofRoot = new ParseNode(derivStep.ref);

        // Note that the proof tree root has a "child" array
        // that is merely a parallel array of the ref's
        // hypArray (holding VarHyp and LogHyp entires)!
        proofRoot.setChild(derivStep.assrtSubst);

        derivStep.proofTree = new ParseTree(proofRoot);

        if (proofAsstPreferences.getRecheckProofAsstUsingProofVerifier())
            if (!checkDerivStepProofUsingVerify()) {
                messages
                    .accumErrorMessage(PaConstants.ERRMSG_VERIFY_RECHECK_ERR_1
                        + getErrorLabelIfPossible(proofWorksheet)
                        + PaConstants.ERRMSG_VERIFY_RECHECK_ERR_2
                        + derivStep.step
                        + PaConstants.ERRMSG_VERIFY_RECHECK_ERR_3);

                derivStep.verifyProofError = true;
                proofWorksheet.getProofCursor().setCursorAtProofWorkStmt(
                    derivStep, PaConstants.FIELD_ID_REF);
                return;
            }
    }

    /*
     * Build a temporary LogHyp so that the proof tree
     * conforms to the norm -- and when it is time to
     * do other things like verify proof or generate the
     * RPN, we can use existing code!
     */
    private void buildNewTheoremLogHyp(final int hypIndexNbr,
        final HypothesisStep hypothesisStep)
    {

        hypothesisStep.ref = LogHyp.BuildTempLogHypObject(
            proofWorksheet.getMaxSeq() + hypIndexNbr + 1,
            hypothesisStep.refLabel, hypothesisStep.formula,
            proofWorksheet.getComboFrame().hypArray,
            hypothesisStep.formulaParseTree);
    }

    private boolean checkDerivStepProofUsingVerify() {
        final String errmsg = verifyProofs.verifyDerivStepProof(
            proofWorksheet.getTheoremLabel() + PaConstants.DOT_STEP_CAPTION
                + derivStep.step, derivStep.formula, derivStep.proofTree,
            proofWorksheet.getComboFrame());

        if (errmsg != null) {

            messages.accumErrorMessage(errmsg);

            proofWorksheet.getProofCursor().setCursorAtProofWorkStmt(derivStep,
                PaConstants.FIELD_ID_REF);
            return false;
        }

        return true;
    }

    /**
     * Attempt to unify -- provide consistent set of variable substitutions -- a
     * derivation proof step with an assertion.
     * <p>
     * Note that the assertion here is merely a "candidate" for unification,
     * which if successfully unified with the step will become the Ref field on
     * the proof step line (and, of course, will determine the proof tree for
     * this step.)
     * 
     * @return true if unification was successful
     * @throws VerifyException if an error occurs
     */
    private boolean unifyStepWithoutWorkVars() throws VerifyException {

        /*
         * FIRST we traverse a lengthy set of heuristics
         * to reject unsuitable candidate assertions.
         * These were invented based on set.mm and are
         * sequenced according to their likeliness to quickly
         * reject the greatest number of candidates.
         *
         * The raison d'etre of the whole list of heuristics
         * is to avoid certain worst case scenarios
         * involving LogHyp order. Since ProofAssistant
         * does not require input of derivation step hyp
         * numbers in the order they are in in the Assertions,
         * a combinatorial explosion -- and lengthy searches --
         * are theoretically possible. So...we use heuristics
         * to rapidly discern the shape, size and smell of
         * suitable candidates for unification, and then
         * during unification we will (perhaps) look at the
         * maxDepth of individual LogHyp's, among other things.
         *
         * These condition checking statements look
         * expensive, but Assrt and ParseTree cache their
         * answers so that repetitive computations can
         * be avoided (and they do not do the computations
         * unless asked, thus avoiding the size and speed
         * penalty for non-Proof Assistant users).
         */

        if (!checkAssrtLevelMatch())
            return false;

        if (!checkHypLevelMatch())
            return false;

        if (!derivStep.deriveStepFormula)
            if (derivStep.formulaParseTree.getMaxDepth() > 0
                && assrtParseTree.getMaxDepth() > derivStep.formulaParseTree
                    .getMaxDepth())
                return false;

        if (!derivStep.deriveStepHyps && derivStep.logHypsMaxDepth > 0)
            if (assrt.getLogHypsMaxDepth() > derivStep.logHypsMaxDepth)
                return false;

        /*
         * SECOND, we attempt to unify the assertion's
         * formula with the proof step's formula.
         */
        assrtVarHypArray = assrt.getMandVarHypArray();

        // Creates ParseNode[] of assrtVarHypArray.length size.
        // It is a parallel array, parallel to assrtVarHypArray.
        // Would be more convenient to have it parallel
        // assrtHypArray (see initLoadAssrtSubst), but I guess
        // we're trying to do this as efficiently as possible
        // because this code is executed a bazillion times
        // for each step/assrt combo and we want to fail fast.
        if (derivStep.deriveStepFormula)
            assrtFormulaSubst = new ParseNode[assrtVarHypArray.length];
        else {
            assrtFormulaSubst = assrtParseTree.getRoot().unifyWithSubtree(
                derivStep.formulaParseTree.getRoot(), assrtVarHypArray,
                unifyNodeStack, compareNodeStack);
            if (assrtFormulaSubst == null)
                return false;
        }

        assrtHypArray = assrt.getMandFrame().hypArray;
        assrtSubst = initLoadAssrtSubst();

        if (derivStep.hyp.length == 0) {
            markStepUnified(false, // usedUnifyWithWorkVars,
                false, // no "swap",
                null); // no rearrange
            return true; // Special Case 0
        }

        /*
         * THREE, we attempt to unify the assertion's
         * LogHyp(s) in the order they were given,
         * based on the hope/assumption that everything
         * is hunky-dory. Note that using the given order
         * is particularly important on a re-unification
         * because the program outputs both Ref and
         * the correctly sequenced Hyp's, so we should
         * not have to go through this misery a second
         * time!
         */
        assrtLogHypArray = assrt.getLogHypArray();
        derivStepHypArray = derivStep.hyp;

        /*
         * A temporary-use array to hold results
         */
        final ParseNode[][] assrtLogHypSubstArray = new ParseNode[assrtLogHypArray.length][];

        int iH = 0;
        while (true) {
            if (unifyAndMergeSubst(assrtLogHypSubstArray, iH, // assrtLogHypIndex
                iH))
                if (++iH >= assrtLogHypArray.length) {
                    markStepUnified(false, // usedUnifyWithWorkVars,
                        false, // no "swap",
                        null); // no rearrange
                    return true;
                }
                else
                    continue;
            if (assrtLogHypArray.length == 1)
                return false; // Special Case 1
            break;
        }

        /*
         * FOUR, since 95% of theorems have 0, 1 or 2
         * hypotheses and the problem of combinatorial
         * explosion is potentially costly, we code a
         * special case for 2 LogHyp's -- just switch
         * the user's input order and try again.
         */
        if (assrtLogHypArray.length == 2) { // Special Case 2
            if (iH > 0)
                assrtSubst = initLoadAssrtSubst();
            if (unifyAndMergeSubst(assrtLogHypSubstArray, 0, // assrtLogHypIndex
                1))
                if (unifyAndMergeSubst(assrtLogHypSubstArray, 1, // assrtLogHypIndex
                    0))
                { // stepLogHypIndex
                    markStepUnified(false, // usedUnifyWithWorkVars,
                        true, // yes "swap",
                        null); // no rearrange
                    return true;
                }
            return false;
        }

        /*
         * FIVE (whew, ugly!)
         *
         * We know the candidate assertion has 3 or more
         * LogHyps and that the user's Hyp sequence was
         * unsuccessful, at least partially. We could
         * just start trying new sequences, but the
         * problem of combinatorial explosion of the
         * number of variations to test looms large. In
         * the worst case scenario, the number of variations
         * of hypothesis sequences is n! -- n factorial
         * where n is the number of logical hypotheses. Wow.
         *
         * And believe it, the problem is potentially deadly.
         * Metamath's set.mm has one theorem with 19 logical
         * hypotheses and many with 10 or more. Even 5 logical
         * hypotheses could result in 5 * 4 * 3 * 2 = 120
         * variations of hypothesis sequences to test; and
         * remember, the assertion is merely a "candidate";
         * we might go through this horror and come out with
         * nothing to show for it!
         *
         * Solving the problem means ameliorating the worst
         * cases while doing the necessary work and balancing
         * the complexity/cost of the algorithm against the
         * time penalties of the worst cases. In other words,
         * there are several (at least) different valid
         * approaches, and it may be that "one size does not
         * fit all"; the algorithm may not be optimal for
         * all datasets.
         *
         * Empirical evidence suggests that unifying the
         * logical hypotheses according to formula length
         * (a good proxy for number of variables and
         * parse tree depth), improves our chances of
         * reducing n!. But, logical hypotheses with variables
         * not present in the assertion's formula are
         * problematic: the unification process
         * allows incorrect variable substitutions to go
         * uncorrected until deeper into the process when
         * an inconsistency is detected. So, in the case
         * where two formulas have the same length, if
         * the sort element (log hyp) has any variables
         * in common with the assertion's formula, we
         * insert it ahead of it's matched sort element.
         * (This fix applies only to sorting of the assertion's
         * logical hypotheses. There is no quick/easy comparable
         * fix for the proof step's logical hypotheses -- that
         * I see now.)
         */
        // don't bother caching the sorted assrtLogHypArray because
        // that would be useful only if we expected multiple steps
        // to satisfy unification to this point, and that is unlikely.
        assrtLogHypArray = assrt.getSortedLogHypArray();
        derivStepHypArray = derivStep.getSortedHypArray();

        /*
         * SIX
         *
         * Build the "substAnswer" array containing the
         * results of parseNode.unifyWithSubtree()
         * stored at these coordinates
         *
         *     row   = derivStepHypArray[i]  (1st dimension)
         *     col   = assrtLogHypArray[j]   (2nd dimension)
         *     unify = unifyWithSubtree in k (3rd dimension)
         *
         * We use the following conventions:
         *
         *     substAnswer[i][j] == null means that
         *         unifyWithSubtree() has not been attempted
         *         for the ith derivStepHypArray element
         *         with the jth assrtLogHypArray element.
         *
         *     substAnswer[i][j] == substAnswerImpossible
         *         (a ParseNode[] array with length = 0) means
         *         that unifyWithSubtree() was attempted and
         *         failure was reported -- no unification.
         *         (Note that the individual pair can be
         *         successfully unified but when those results
         *         are accum'd into the composite, assrtSubst,
         *         a substitution "inconsistency" is detected.)
         *
         *     substAnswer[i][j] == something else --> means
         *         success!
         */
        substAnswer = new ParseNode[assrtLogHypArray.length][][];
        for (int i = 0; i < assrtLogHypArray.length; i++)
            substAnswer[i] = new ParseNode[assrtLogHypArray.length][];

        /*
         * SEVEN: go baby.
         */

        // not sure if worth it...but...
        salvagePreliminaryAnswers(assrtLogHypSubstArray);

        // Now, for last time, re-init assrtSubt!
        assrtSubst = initLoadAssrtSubst();

        // ok! add complexity to keep track of substAnswer :)
        impossibleCnt = new int[assrtLogHypArray.length];

        assrtHypUsed = new boolean[assrtLogHypArray.length];

        derivAssrtXRef = new int[assrtLogHypArray.length];
        for (int i = 0; i < derivAssrtXRef.length; i++)
            derivAssrtXRef[i] = -1;

        int currLevel = 0;
        while (true) {
            if (!findNextUnifiedAssrtHyp(currLevel)) {
                if (impossibleCnt[currLevel] >= assrtLogHypArray.length)
                    return false;
                if (--currLevel < 0)
                    return false;
                cleanupOneAssrtSubstLevel(currLevel); // backout prev
                continue;
            }
            if (++currLevel >= assrtLogHypArray.length) {
                markStepUnified(false, // usedUnifyWithWorkVars,
                    false, // no "swap",
                    derivAssrtXRef); // yes, rearrange
                return true;
            }
            continue;
        }
    }

    private boolean checkAssrtLevelMatch() {

        if (derivStep.workVarList != null)
            return true;

        assrtParseTree = assrt.getExprParseTree();

        if (!derivStep.deriveStepFormula) {
            final String assrtLevelOneTwo = assrtParseTree.getLevelOneTwo();
            if (assrtLevelOneTwo.length() > 0) {
                if (!assrtLevelOneTwo.equals(derivStep.formulaParseTree
                    .getLevelOneTwo()))
                    return false; // unification impossible!
            }
            else { // this checks LevelOne
                final Stmt assrtParseRootStmt = assrtParseTree.getRoot()
                    .getStmt();

                if (assrtParseRootStmt != derivStep.formulaParseTree.getRoot()
                    .getStmt() && !assrtParseRootStmt.isVarHyp())
                    return false; // unification impossible!
            }
        }
        return true;
    }

    private boolean checkHypLevelMatch() {

        if (!derivStep.deriveStepHyps) {
            final String assrtLogHypsL1HiLoKey = assrt.getLogHypsL1HiLoKey();
            // note: derivStep L1HiLo may be "" if its hyps are derived!
            // this step's L1HiLo is not recomputed after
            // its log hyps' formulas are derived in
            // ProofUnifier -- that will slow things down
            // some (unless recomputation is triggered by
            // "Derive" formula.)
            if (assrtLogHypsL1HiLoKey.length() > 0
                && derivStep.logHypsL1HiLoKey.length() > 0 // see note
                && !assrtLogHypsL1HiLoKey.equals(derivStep.logHypsL1HiLoKey))
                return false;
        }
        return true;
    }

    private boolean findNextUnifiedAssrtHyp(final int currLevel) {

        final ParseNode[][] currLevelSubstAnswer = substAnswer[currLevel];

        int nextAssrtHypIndex = derivAssrtXRef[currLevel];
        if (nextAssrtHypIndex != -1) {
            assrtHypUsed[nextAssrtHypIndex] = false;
            derivAssrtXRef[currLevel] = -1;
        }

        int governorLimit = assrtLogHypArray.length;
        while (governorLimit-- > 0) {
            ++nextAssrtHypIndex;
            if (nextAssrtHypIndex >= assrtLogHypArray.length)
                return false;
            if (assrtHypUsed[nextAssrtHypIndex])
                continue;
            if (currLevelSubstAnswer[nextAssrtHypIndex] == substAnswerImpossible)
                continue;
            if (derivStepHypArray[currLevel] == null
                && derivStep.deriveStepHyps)
            {
                // skip following heuristic
            }
            else {
                final int maxDepth = derivStepHypArray[currLevel].formulaParseTree
                    .getMaxDepth();
                if (maxDepth > 0
                    && assrtLogHypArray[nextAssrtHypIndex].getExprParseTree()
                        .getMaxDepth() > maxDepth)
                {
                    currLevelSubstAnswer[nextAssrtHypIndex] = substAnswerImpossible;
                    ++impossibleCnt[currLevel];
                    continue;
                }
            }
            if (currLevelSubstAnswer[nextAssrtHypIndex] == null) {
                if (unifyAndMergeSubst(currLevelSubstAnswer, nextAssrtHypIndex, // hyp
                    currLevel))
                { // stephyp
                    derivAssrtXRef[currLevel] = nextAssrtHypIndex;
                    assrtHypUsed[nextAssrtHypIndex] = true;
                    return true;
                }
                if (currLevelSubstAnswer[nextAssrtHypIndex] == null) {
                    currLevelSubstAnswer[nextAssrtHypIndex] = substAnswerImpossible;
                    ++impossibleCnt[currLevel];
                }
                continue;
            }
            final VarHyp[] assrtLogHypVarHypArray = assrtLogHypArray[nextAssrtHypIndex]
                .getMandVarHypArray();

            // here we (attempt to) merge in the previously computed
            // substitutions for a LogHyp
            if (mergeLogHypSubst(currLevel, assrtLogHypVarHypArray,
                currLevelSubstAnswer[nextAssrtHypIndex]))
            {
                derivAssrtXRef[currLevel] = nextAssrtHypIndex;
                assrtHypUsed[nextAssrtHypIndex] = true;
                return true;
            }
            continue;
        }
        return false;
    }

    /**
     * Attempt to recover the initial unification results from
     * asrtLogHypSubstArray -- which had the user's hyp sequence -- and put the
     * non-null answers into the new substAnswer array. This is complicated by
     * the fact that the substAnswer array row/col coordinates match the sorted
     * assrtLogHypArray and derivStepHypArray sequences. So, we have this
     * tedious lookup operation, which I am not convinced is worth doing. If we
     * skipped this we would just have to re-do unification for 'x' number of
     * hypotheses, but that would happen automatically because of the empty
     * values in substAnswer.
     * 
     * @param assrtLogHypSubstArray the user's old hyp sequence
     */
    private void salvagePreliminaryAnswers(
        final ParseNode[][] assrtLogHypSubstArray)
    {

        LogHyp holdLogHyp;
        ProofStepStmt holdStep;

        final LogHyp[] origSeqLogHypArray = assrt.getLogHypArray();
        final ProofStepStmt[] origSeqStepArray = derivStep.hyp;

        int answerRow;
        int answerCol;
        for (int i = 0; i < assrtLogHypArray.length; i++) {
            if (assrtLogHypSubstArray[i] == null)
                return;
            holdLogHyp = origSeqLogHypArray[i];
            holdStep = origSeqStepArray[i];
            answerRow = -1;
            for (int j = 0; j < assrtLogHypArray.length; j++)
                if (derivStepHypArray[j] == holdStep) {
                    answerRow = j;
                    break;
                }
            answerCol = -1;
            for (int j = 0; j < assrtLogHypArray.length; j++)
                if (assrtLogHypArray[j] == holdLogHyp) {
                    answerCol = j;
                    break;
                }
            substAnswer[answerRow][answerCol] = assrtLogHypSubstArray[i];
        }
    }

    private boolean unifyAndMergeSubst(
        final ParseNode[][] assrtLogHypSubstArray, final int assrtLogHypIndex,
        final int stepLogHypIndex)
    {

        final VarHyp[] assrtLogHypVarHypArray = assrtLogHypArray[assrtLogHypIndex]
            .getMandVarHypArray();

        if (derivStepHypArray[stepLogHypIndex] == null)
            assrtLogHypSubstArray[assrtLogHypIndex] = new ParseNode[assrtLogHypVarHypArray.length];
        else
            assrtLogHypSubstArray[assrtLogHypIndex] = assrtLogHypArray[assrtLogHypIndex]
                .getExprParseTree()
                .getRoot()
                .unifyWithSubtree(
                    derivStepHypArray[stepLogHypIndex].formulaParseTree
                        .getRoot(),
                    assrtLogHypVarHypArray, unifyNodeStack, compareNodeStack);

        return assrtLogHypSubstArray[assrtLogHypIndex] != null
            && mergeLogHypSubst(stepLogHypIndex, assrtLogHypVarHypArray,
                assrtLogHypSubstArray[assrtLogHypIndex]);
    }

    private boolean mergeLogHypSubst(final int cleanupIndex,
        final VarHyp[] assrtLogHypVarHypArray,
        final ParseNode[] assrtLogHypSubst)
    {

        levelCleanupCnt[cleanupIndex] = 0;
        levelCleanup[cleanupIndex] = new int[assrtHypArray.length];

        int hypIndex = 0;
        int substIndex = 0;

        substLoop: while (substIndex < assrtLogHypVarHypArray.length) {

            hypLoop: while (hypIndex < assrtHypArray.length) {

                if (assrtHypArray[hypIndex] != assrtLogHypVarHypArray[substIndex])
                {

                    ++hypIndex;
                    continue hypLoop;
                }

                // a null unification
                if (assrtLogHypSubst[substIndex] == null
                    && derivStep.deriveStepHyps)
                {
                    ++hypIndex;
                    ++substIndex;
                    continue substLoop;
                }

                if (assrtSubst[hypIndex] == null) {

                    assrtSubst[hypIndex] = assrtLogHypSubst[substIndex];
                    levelCleanup[cleanupIndex][levelCleanupCnt[cleanupIndex]++] = hypIndex;
                    ++hypIndex;
                    ++substIndex;
                    continue substLoop;
                }

                if (assrtSubst[hypIndex].isDeepDup(
                    assrtLogHypSubst[substIndex], compareNodeStack))
                {
                    ++hypIndex;
                    ++substIndex;
                    continue substLoop;
                }
                cleanupOneAssrtSubstLevel(cleanupIndex);

                return false;
            }
            throw new IllegalArgumentException(
                PaConstants.ERRMSG_MERGE_LOGHYP_SUBST_ERR_1
                    + getErrorLabelIfPossible(proofWorksheet)
                    + PaConstants.ERRMSG_MERGE_LOGHYP_SUBST_ERR_2
                    + derivStep.step
                    + PaConstants.ERRMSG_MERGE_LOGHYP_SUBST_ERR_3);
        }

        return true;
    }

    private void cleanupOneAssrtSubstLevel(final int level) {
        int cnt;
        if ((cnt = levelCleanupCnt[level]) > 0) {
            final int[] cleaner = levelCleanup[level];
            for (int i = 0; i < cnt; i++)
                assrtSubst[cleaner[i]] = null;
        }
    }

    /**
     * Creates ParseNode array containing a derivation step's formula's
     * substitutions.
     * <p>
     * The main task here is to move the step's formula's substitutions into a
     * different parallel array that also contains LogHyp hyps.
     * <p>
     * Global input variables:
     * <ul>
     * <li>assrtFormulaSubst - step's formula's substitutions in ParseNode array
     * with length = assrtVarHypArray.length.
     * <li>assrtVarHypArray - from Assrt, is a parallel array for
     * assrtFormulaSubst.
     * <li>assrtHypArray - from Assrt, is a superset of assrtVarHypArray (also
     * holds LogHyp hyps...)
     * </ul>
     * 
     * @return the new array
     */
    private ParseNode[] initLoadAssrtSubst() {
        final ParseNode[] outSubst = new ParseNode[assrtHypArray.length];

        int hypIndex = 0;
        int substIndex = 0;

        substLoop: while (substIndex < assrtFormulaSubst.length) {
            hypLoop: while (hypIndex < assrtHypArray.length) {
                if (assrtVarHypArray[substIndex] != assrtHypArray[hypIndex]) {
                    ++hypIndex;
                    continue hypLoop;
                }
                outSubst[hypIndex++] = assrtFormulaSubst[substIndex++];
                continue substLoop;
            }
            throw new IllegalArgumentException(
                PaConstants.ERRMSG_INIT_FORMULA_SUBST_ERR_1
                    + getErrorLabelIfPossible(proofWorksheet)
                    + PaConstants.ERRMSG_INIT_FORMULA_SUBST_ERR_2
                    + derivStep.step
                    + PaConstants.ERRMSG_INIT_FORMULA_SUBST_ERR_3);
        }

        return outSubst;
    }

    private void markStepUnified(final boolean usedUnifyWithWorkVars,
        final boolean swapHyps, final int[] rearrangeDerivAssrtXRef)
    {

        String djMsg = null;

        // if not first unification for this step
        if (derivStep.unificationStatus == PaConstants.UNIFICATION_STATUS_UNIFIED
            || derivStep.unificationStatus == PaConstants.UNIFICATION_STATUS_UNIFIED_W_INCOMPLETE_HYPS
            || derivStep.unificationStatus == PaConstants.UNIFICATION_STATUS_UNIFIED_W_WORK_VARS)
        {

            // yes, ok to check/recheck Dj Vars if Work Vars involved...
//          if (derivStep.hasWorkVarsInStepOrItsHyps()) {
//              return;
//          }

            // ref was entered and now we're seeing the
            // original ref again on the pass through the
            // assertions, so bypass it to save time...
            if (assrt == derivStep.ref)
                return;

            djMsg = checkDerivStepUnifyAgainstDjVars(derivStep, assrt,
                assrtSubst);

            if (derivStep.djVarsErrorStatus == PaConstants.DJ_VARS_ERROR_STATUS_NO_ERRORS)
            {
                addToAlternatesList(assrt);
                return;
            }

            if (derivStep.djVarsErrorStatus == PaConstants.DJ_VARS_ERROR_STATUS_HARD_ERRORS)
            {
                if (djMsg == null) {
                    derivStep.heldDjErrorMessage = null;
                    if (holdSoftDjVarsErrorList == null
                        || holdSoftDjVarsErrorList.isEmpty())
                        derivStep.djVarsErrorStatus = PaConstants.DJ_VARS_ERROR_STATUS_NO_ERRORS;
                    else {
                        derivStep.djVarsErrorStatus = PaConstants.DJ_VARS_ERROR_STATUS_SOFT_ERRORS;
                        if (proofAsstPreferences.getDjVarsSoftErrorsReport())
                            derivStep
                                .buildSoftDjErrorMessage(holdSoftDjVarsErrorList);
                    }
                    addToAlternatesList((Assrt)derivStep.ref);
                    rearrangeHyps(swapHyps, rearrangeDerivAssrtXRef);
                    saveOtherDerivStepResults();
                }
                else
                    addToAlternatesList(assrt);
                return;
            }
            else { // ok, has DJ_VARS_ERROR_STATUS_SOFT_ERRORS
                if (djMsg == null
                    && (holdSoftDjVarsErrorList == null || holdSoftDjVarsErrorList
                        .isEmpty()))
                {

                    derivStep.djVarsErrorStatus = PaConstants.DJ_VARS_ERROR_STATUS_NO_ERRORS;

                    derivStep.heldDjErrorMessage = null;
                    addToAlternatesList((Assrt)derivStep.ref);
                    rearrangeHyps(swapHyps, rearrangeDerivAssrtXRef);
                    saveOtherDerivStepResults();
                }
                else
                    addToAlternatesList(assrt);
                return;
            }
        }

        // OK! First unification for this step
        derivStep.unificationStatus = PaConstants.UNIFICATION_STATUS_UNIFIED;

        rearrangeHyps(swapHyps, rearrangeDerivAssrtXRef);

        if (usedUnifyWithWorkVars) {

            if (derivStep.deriveStepFormula)
                generateDerivStepFormula();

            if (derivStep.deriveStepHyps) {
                derivStep.nbrHypsGenerated = 0; // oy
                generateDerivStepHyps();
            }

            saveOtherDerivStepRefStuff(derivStep, assrt, assrtSubst);

            // this *must* follow the derives so that the
            // following proof steps can take into account
            // the changes to the derived work vars.
            doUpdateWorksheetWorkVars(derivStep);

        }
        else
            saveOtherDerivStepRefStuff(derivStep, assrt, assrtSubst);

        if (derivStep.workVarList != null)
            derivStep.unificationStatus = PaConstants.UNIFICATION_STATUS_UNIFIED_W_WORK_VARS;
        for (int i = 0; i < derivStep.hyp.length; i++) {
            if (!derivStep.hyp[i].isDerivationStep())
                continue;
            final DerivationStep dH = (DerivationStep)derivStep.hyp[i];

            if (dH.workVarList != null
                || dH.unificationStatus == PaConstants.UNIFICATION_STATUS_UNIFIED_W_WORK_VARS)
                derivStep.unificationStatus = PaConstants.UNIFICATION_STATUS_UNIFIED_W_WORK_VARS;

            if (dH.hypFldIncomplete) {
                messages.accumInfoMessage(PaConstants.ERRMSG_INCOMPLETE_HYPS_1
                    + getErrorLabelIfPossible(proofWorksheet)
                    + PaConstants.ERRMSG_INCOMPLETE_HYPS_2 + derivStep.step
                    + PaConstants.ERRMSG_INCOMPLETE_HYPS_3 + derivStep.refLabel
                    + PaConstants.ERRMSG_INCOMPLETE_HYPS_4);

                derivStep.unificationStatus = PaConstants.UNIFICATION_STATUS_UNIFIED_W_INCOMPLETE_HYPS;
                break;
            }
        }

        doInitialStepDjEdits(derivStep, assrt, assrtSubst);

        saveOtherDjVarsEditResults(derivStep);
    }

    private void saveOtherDerivStepResults() {

        saveOtherDerivStepRefStuff(derivStep, assrt, assrtSubst);
        saveOtherDjVarsEditResults(derivStep);
    }

    // updates derivation step
    // - djVarsErrorStatus
    // - heldDjErrorMessage
    private void doInitialStepDjEdits(final DerivationStep d,
        final Assrt assrt, final ParseNode[] assrtSubst)
    {

        final String djMsg = checkDerivStepUnifyAgainstDjVars(d, assrt,
            assrtSubst);
        if (djMsg == null) {
            d.heldDjErrorMessage = null;
            if (holdSoftDjVarsErrorList == null
                || holdSoftDjVarsErrorList.isEmpty())
                d.djVarsErrorStatus = PaConstants.DJ_VARS_ERROR_STATUS_NO_ERRORS;
            else {
                d.djVarsErrorStatus = PaConstants.DJ_VARS_ERROR_STATUS_SOFT_ERRORS;

                if (proofAsstPreferences.getDjVarsSoftErrorsReport())
                    d.buildSoftDjErrorMessage(holdSoftDjVarsErrorList);
            }
        }
        else { // HARD dj error on first unification for this step

            d.djVarsErrorStatus = PaConstants.DJ_VARS_ERROR_STATUS_HARD_ERRORS;

            d.heldDjErrorMessage = djMsg;
        }
    }

    private void addToAlternatesList(final Assrt a) {
        if (derivStep.alternateRefList == null)
            derivStep.alternateRefList = new ArrayList<Assrt>();
        derivStep.alternateRefList.add(a);
    }

    private void saveOtherDerivStepRefStuff(final DerivationStep d,
        final Assrt assrt, final ParseNode[] assrtSubst)
    {

        d.setAssrtSubst(assrtSubst);
        d.setRef(assrt);
        d.setRefLabel(assrt.getLabel());
        d.reloadStepHypRefInStmtText();
    }

    private void saveOtherDjVarsEditResults(final DerivationStep d) {

        if (holdSoftDjVarsErrorList == null
            || holdSoftDjVarsErrorList.isEmpty())
            d.softDjVarsErrorList = null;
        else
            d.softDjVarsErrorList = new ArrayList<DjVars>(
                holdSoftDjVarsErrorList);
    }

    /**
     * Changes the order of a derivation step's Hyp entries to match the order
     * on the Ref assertion.
     * <p>
     * Note: a bypass was inserted to NOT rearrange the hyps if *this*
     * unification is just for the purpose of creating an AlternateUnification
     * or DjVars error message -- the step has already *been* unified and
     * changing the Hyp order would be unhelpful.
     * 
     * @param swapHyps true to rearrange Hyps
     * @param rearrangeDerivAssrtXRef a set of indexes into
     *            {@code assrtLogHypArray}
     */
    private void rearrangeHyps(final boolean swapHyps,
        final int[] rearrangeDerivAssrtXRef)
    {

        if (swapHyps) {
            derivStep.swapHyps(0, 1); // go undercover and do the deed
            return;
        }
        if (rearrangeDerivAssrtXRef == null)
            return;

        final ProofStepStmt[] newDerivStepHypArray = new ProofStepStmt[assrtLogHypArray.length];

        final String[] newHypStep = new String[assrtLogHypArray.length];

        final LogHyp[] origLogHypArray = assrt.getLogHypArray();
        LogHyp holdLogHyp;
        xrefLoop: for (int i = 0; i < rearrangeDerivAssrtXRef.length; i++) {
            holdLogHyp = assrtLogHypArray[rearrangeDerivAssrtXRef[i]];
            for (int j = 0; j < origLogHypArray.length; j++)
                if (holdLogHyp == origLogHypArray[j]) {
                    newDerivStepHypArray[j] = derivStepHypArray[i];

                    // this takes into account a null unification
                    // with a "?" Hyp entry and the ith LogHyp,
                    // (is set up to fail quickly if we're not doing
                    // deriveStepHyps :)
                    if (newDerivStepHypArray[j] == null
                        && derivStep.deriveStepHyps)
                        newHypStep[j] = PaConstants.DEFAULT_STMT_LABEL;
                    else
                        newHypStep[j] = newDerivStepHypArray[j].step;
                    continue xrefLoop;
                }
            throw new IllegalArgumentException(
                PaConstants.ERRMSG_REARRANGE_HYPS_ERR_1
                    + getErrorLabelIfPossible(proofWorksheet)
                    + PaConstants.ERRMSG_REARRANGE_HYPS_ERR_2 + derivStep.step
                    + PaConstants.ERRMSG_REARRANGE_HYPS_ERR_3);
        }
        derivStep.hyp = newDerivStepHypArray;
        derivStep.hypStep = newHypStep;

        // tidy up just in case (no bogus references left around...)
        derivStepHypArray = derivStep.hyp; // redundant? maybe
    }

    private String checkDerivStepUnifyAgainstDjVars(final DerivationStep d,
        final Assrt checkUnificationRef,
        final ParseNode[] checkUnificationAssrtSubst)
    {

        if (holdSoftDjVarsErrorList != null)
            holdSoftDjVarsErrorList.clear();

        if (checkUnificationRef.getMandFrame().djVarsArray.length == 0)
            return null; // success, no error message

        final String errmsg = verifyProofs.verifyDerivStepDjVars(
            d.step, // step number output string,
            proofWorksheet.getTheoremLabel() + PaConstants.DOT_STEP_CAPTION
                + derivStep.step, checkUnificationRef,
            checkUnificationAssrtSubst, proofWorksheet.getComboFrame(),
            proofAsstPreferences.getDjVarsSoftErrorsIgnore(),
            proofAsstPreferences.getDjVarsSoftErrorsGenerateNew(),
            holdSoftDjVarsErrorList);

        if (errmsg == null)
            return null;

        return PaConstants.ERRMSG_DV_VERIFY_ERR_1
            + getErrorLabelIfPossible(proofWorksheet)
            + PaConstants.ERRMSG_DV_VERIFY_ERR_2 + d.step
            + PaConstants.ERRMSG_DV_VERIFY_ERR_3
            + checkUnificationRef.getLabel()
            + PaConstants.ERRMSG_DV_VERIFY_ERR_4 + errmsg;
    }

    private void markRefUnificationFailure(final Assrt assrt) {

        derivStep.unificationStatus = PaConstants.UNIFICATION_STATUS_UNIFICATION_ERROR;

        messages.accumErrorMessage(PaConstants.ERRMSG_REF_UNIFY_ERR_1
            + getErrorLabelIfPossible(proofWorksheet)
            + PaConstants.ERRMSG_REF_UNIFY_ERR_2 + derivStep.step
            + PaConstants.ERRMSG_REF_UNIFY_ERR_3 + derivStep.refLabel
            + PaConstants.ERRMSG_REF_UNIFY_ERR_4);

        proofWorksheet.getProofCursor().setCursorAtProofWorkStmt(derivStep,
            PaConstants.FIELD_ID_REF);
    }

    private void markUnificationFailure() {

        if (derivStep.generatedByDeriveFeature && derivStep.hyp.length == 0) {

            // An attempt was made -- to get lucky --
            // unifying the generated hypothesis step,
            // but since it failed, there is no error,
            // just an incompleteness for the user to
            // deal with.
            derivStep.hypFldIncomplete = true;
            derivStep.hyp = new ProofStepStmt[1];
            derivStep.hypStep = new String[1];
            derivStep.hypStep[0] = PaConstants.DEFAULT_STMT_LABEL;
            derivStep.reloadStepHypRefInStmtText();
        }
        else {

            derivStep.unificationStatus = PaConstants.UNIFICATION_STATUS_UNIFICATION_ERROR;

            messages.accumErrorMessage(PaConstants.ERRMSG_STEP_UNIFY_ERR_1
                + getErrorLabelIfPossible(proofWorksheet)
                + PaConstants.ERRMSG_STEP_UNIFY_ERR_2 + derivStep.step
                + PaConstants.ERRMSG_STEP_UNIFY_ERR_3);

            proofWorksheet.getProofCursor().setCursorAtProofWorkStmt(derivStep,
                PaConstants.FIELD_ID_REF);
        }
    }

    private String getErrorLabelIfPossible(final ProofWorksheet proofWorksheet)
    {
        String label = "unknownLabel";
        if (proofWorksheet != null && proofWorksheet.getTheoremLabel() != null)
            label = proofWorksheet.getTheoremLabel();
        return label;
    }

    private Cnst getProvableLogicStmtTyp() {
        return grammar.getProvableLogicStmtTypArray()[0];
    }

    private void generateDerivStepHyps() {

        final LogHyp[] logHypArray = assrt.getLogHypArray();
        LogHyp holdLogHyp;
        ParseTree logHypParseTree;
        List<WorkVar> workVarList;
        ParseTree generatedFormulaParseTree;
        Formula generatedFormula;

        ProofStepStmt foundMatchingFormulaStep;
        DerivationStep generatedDerivStep;

        for (int i = 0; i < derivStepHypArray.length; i++) {
            if (derivStepHypArray[i] != null)
                continue;

            holdLogHyp = logHypArray[i];
            logHypParseTree = holdLogHyp.getExprParseTree();
            workVarList = new ArrayList<WorkVar>(3); // 3=guess

            generatedFormulaParseTree = logHypParseTree
                .deepCloneApplyingAssrtSubst(assrtHypArray, assrtSubst,
                    workVarList);

            generatedFormula = verifyProofs.convertRPNToFormula(
                generatedFormulaParseTree.convertToRPN(), " "); // abend
                                                                // diagnostic,
                                                                // leave blank
                                                                // for now.

            // oops, almost forgot this.
            generatedFormula.setTyp(provableLogicStmtTyp);

            foundMatchingFormulaStep = proofWorksheet.findMatchingStepFormula(
                generatedFormula, derivStep);

            if (foundMatchingFormulaStep != null) {
                derivStep.hyp[i] = foundMatchingFormulaStep;
                derivStep.hypStep[i] = foundMatchingFormulaStep.step;
                derivStep.hyp[i].loadProofLevel(derivStep.proofLevel + 1);
                continue;
            }

            generatedDerivStep = proofWorksheet.addDerivStepForDeriveFeature(
                workVarList, generatedFormula, generatedFormulaParseTree,
                derivStep);

            derivStep.hyp[i] = generatedDerivStep;
            derivStep.hypStep[i] = generatedDerivStep.step;
            derivStep.hyp[i].loadProofLevel(derivStep.proofLevel + 1);

            ++derivStep.nbrHypsGenerated;
        }
    }

    private void generateDerivStepFormula() {

        final ParseTree origParseTree = assrt.getExprParseTree();

        // keeps count of dummies in new formula
        final List<WorkVar> workVarList = new ArrayList<WorkVar>(3); // 3 is a
        // guess.

        final ParseTree generatedFormulaParseTree = origParseTree
            .deepCloneApplyingAssrtSubst(assrtHypArray, assrtSubst, workVarList);

        final Formula generatedFormula = verifyProofs.convertRPNToFormula(
            generatedFormulaParseTree.convertToRPN(),
            PaConstants.DOT_STEP_CAPTION + derivStep.step);

        // oops, almost forgot this.
        generatedFormula.setTyp(provableLogicStmtTyp);

        derivStep.loadGeneratedFormulaIntoDerivStep(generatedFormula,
            generatedFormulaParseTree, false); // false = stmtText not already
                                               // loaded!

        if (!workVarList.isEmpty())
            derivStep.formulaFldIncomplete = true;
        derivStep.updateWorkVarList(workVarList);
    }

    // ***********************************************************
    // ***********************************************************
    // ************ ****************
    // NEW CODE FOR Unifying Steps With Work Vars FOLLOWS!!!!!
    // ************ ****************
    // ***********************************************************
    // ***********************************************************

    private void unifyStepsInvolvingWorkVars() throws VerifyException {

        int wIndex = -1;
        int wIndexInsertedCnt = 0;
        ProofWorkStmt proofWorkStmtObject;

        ProofWorkStmt selectorSearchStmt = null;
        if (proofWorksheet.stepRequest != null
            && (proofWorksheet.stepRequest.request == PaConstants.STEP_REQUEST_SELECTOR_SEARCH
                || proofWorksheet.stepRequest.request == PaConstants.STEP_REQUEST_STEP_SEARCH || proofWorksheet.stepRequest.request == PaConstants.STEP_REQUEST_SEARCH_OPTIONS))
            selectorSearchStmt = (DerivationStep)proofWorksheet.stepRequest.param1;

        stepLoop: while (true) {

            wIndex = wIndex + 1 + wIndexInsertedCnt;
            if (wIndex >= proofWorksheet.proofWorkStmtList.size())
                break;
            wIndexInsertedCnt = 0;

            proofWorkStmtObject = proofWorksheet.proofWorkStmtList.get(wIndex);
            if (!proofWorkStmtObject.isDerivationStep())
                continue stepLoop;

            derivStep = (DerivationStep)proofWorkStmtObject;

            if (selectorSearchStmt == derivStep) {
                if (proofWorksheet.stepRequest.request == PaConstants.STEP_REQUEST_SELECTOR_SEARCH)
                    proofWorksheet.stepSelectorResults = stepSelectorSearch
                        .loadStepSelectorResults(derivStep);
                else if (proofWorksheet.stepRequest.request == PaConstants.STEP_REQUEST_STEP_SEARCH)
                    proofWorksheet.searchOutput = proofAsstPreferences
                        .getSearchMgr().execStepSearch(derivStep);
                return; // our work here is complete ;-)
            }

            if (derivStep.ref == null || !derivStep.deriveStepFormula
                && !derivStep.deriveStepHyps
                && !derivStep.hasWorkVarsInStepOrItsHyps())
                continue;

            assrt = (Assrt)derivStep.ref;
            assrtNbrLogHyps = assrt.getLogHypArrayLength();

            if (derivStep.hyp.length != assrtNbrLogHyps)
                throw new IllegalArgumentException(
                    PaConstants.ERRMSG_STEP_REF_HYP_NBR_ERR_1
                        + getErrorLabelIfPossible(proofWorksheet)
                        + PaConstants.ERRMSG_STEP_REF_HYP_NBR_ERR_2
                        + derivStep.step
                        + PaConstants.ERRMSG_STEP_REF_HYP_NBR_ERR_3);

            for (int i = 0; i < derivStep.hyp.length; i++)
                // this is BS -- but a hyp[i]'s deriveStepFormula
                // could have errored out...
                if (derivStep.hyp[i] != null
                    && derivStep.hyp[i].formulaParseTree == null)
                {

                    derivStep.unificationStatus =
                    // this will
                    // just be bypassed and considered incomplete
                    PaConstants.UNIFICATION_STATUS_ATTEMPT_CANCELLED;
                    continue stepLoop;
                }

            if (!unifyStepWithWorkVars()) {
                markRefUnificationFailure(assrt);
                continue;
            }

            wIndexInsertedCnt = derivStep.nbrHypsGenerated; // oy

        }
    }

    private boolean unifyStepWithWorkVars() throws VerifyException {

        // unify step formula, return false if error
        if (!unifyStepFormulaWithWorkVars())
            return false;

        if (derivStep.hyp.length == 0) {
            assrtSubst = stepUnifier.finalizeAndLoadAssrtSubst();
            markStepUnified(true, // usedUnifyWithWorkVars,
                false, // no "swap",
                null); // no rearrange
            return true;
        }

        derivStepHypArray = derivStep.hyp;
        if ((assrtSubst = stepUnifier
            .unifyAndMergeHypsUnsorted(derivStepHypArray)) != null)
        {

            markStepUnified(true, // usedUnifyWithWorkVars,
                false, // no "swap",
                null); // no rearrange
            return true;
        }

        assrtLogHypArray = assrt.getSortedLogHypArray();
        derivStepHypArray = derivStep.getSortedHypArray();

        if ((assrtSubst = stepUnifier.unifyAndMergeHypsSorted(assrtLogHypArray,
            derivStepHypArray)) != null)
        {

            derivAssrtXRef = stepUnifier.getDerivAssrtXRef();

            markStepUnified(true, // usedUnifyWithWorkVars,
                false, // no "swap",
                derivAssrtXRef); // rearrange

            return true;
        }
        return false;
    }

    private boolean unifyStepFormulaWithWorkVars() throws VerifyException {

        assrtVarHypArray = assrt.getMandVarHypArray();
        assrtHypArray = assrt.getMandFrame().hypArray;
        assrtLogHypArray = assrt.getLogHypArray();
        assrtParseTree = assrt.getExprParseTree();

        ParseNode stepRoot = null;
        if (derivStep.formulaParseTree != null)
            stepRoot = derivStep.formulaParseTree.getRoot();
        return stepUnifier.unifyAndMergeStepFormula(
        /* commit = */true, assrtParseTree.getRoot(), stepRoot, assrtHypArray,
            assrtLogHypArray);

    }

    /**
     * Update derivation steps in the Proof Worksheet which contain instances of
     * Work Vars that were updated by unification of the current derivation
     * step.
     * <p>
     * Objectives for derivation steps whose workVarList is not null: - see if
     * any work vars in the workVarList are updated by the current step's
     * unification. if not, exit -- no action. - build a new workVarList
     * containing the Work Vars still in use after updating -- or null, if none
     * still exist after the update. - if derivation step marked
     * "unified but incomplete" or "unified" and the workVarList is emptied by
     * the updates, reset the unification status to "not unified" to trigger a
     * re-unification process (inefficient and ugly, but..) - clone-copy the
     * parse tree to reflect the Work Var updates - update assrtSubst, if not
     * null - reformat the formula text using TMFF and the clone-copied parse
     * tree, updating the heuristics fields too.)
     * 
     * @param currentDerivStep the current DerivationStep
     */
    private void doUpdateWorksheetWorkVars(final DerivationStep currentDerivStep)
    {

        for (final ProofWorkStmt proofWorkStmtObject : proofWorksheet
            .getProofWorkStmtList())
        {
            if (!proofWorkStmtObject.isDerivationStep())
                continue;

            final DerivationStep d = (DerivationStep)proofWorkStmtObject;

            if (d.workVarList != null)
                doUpdateDerivationStepWorkVars(d);

            if (d == currentDerivStep)
                continue; // skip the rest of this stuff...

            doUpdateDerivationStepAssrtSubst(d);

            final int saveUnificationStatus = d.unificationStatus;
            doUpdateWorkVarUnificationStatus(d);

            if (saveUnificationStatus != d.unificationStatus
                && d.unificationStatus == PaConstants.UNIFICATION_STATUS_UNIFIED)
            {
                // must redo DjVars edits!!!
                doInitialStepDjEdits(d, (Assrt)d.ref, d.assrtSubst);
                saveOtherDjVarsEditResults(d);
            }
        }
    }

    private void doUpdateDerivationStepWorkVars(final DerivationStep d) {

        // ok, see if workVarList Work Vars actually updated
        List<WorkVar> newWorkVarList = new ArrayList<WorkVar>(
            d.workVarList.size());

        final List<WorkVar> updatedWorkVarList = new ArrayList<WorkVar>(
            d.workVarList.size());

        WorkVar workVar;

        final WorkVarManager workVarManager = proofAsstPreferences
            .getWorkVarManager();

        // !isAllocated(workVar) means update made and the work var
        // will be removed from use, replaced by the updates
        // *by this process* (and before unification of the next
        // step.)
        for (int i = 0; i < d.workVarList.size(); i++) {

            workVar = d.workVarList.get(i);
            if (workVarManager.isAllocated(workVar))
                newWorkVarList.add(workVar);
            else
                updatedWorkVarList.add(workVar);
        }
        if (updatedWorkVarList.isEmpty())
            return; // no change to derivation step!!!

        // ok, accum newWorkVarList using list of updated work vars
        // (i.e. &w1 = &w2 -> &w3 has possibly new work vars &w2
        // and &w3)
        ParseNode substNode;
        for (int i = 0; i < updatedWorkVarList.size(); i++) {
            workVar = updatedWorkVarList.get(i);
            substNode = ((WorkVarHyp)workVar.getActiveVarHyp()).paSubst;
            if (substNode == null)
                throw new IllegalArgumentException(
                    PaConstants.ERRMSG_UPD_WV_ASSIGNED_NULL_VALUE_1);
            substNode.accumSetOfWorkVarsUsed(newWorkVarList);
        }

        /*
         * ok, now we need to update:
         *     - formulaParseTree
         *     - workVarList
         *     - formulaFldIncomplete = false IF no work vars now
         *     - formula
         *     - logHypsL1HiLoKey AND logHypsMaxDepth in
         *       steps that refer to curr step as a hyp
         */
        // null means step no longer has work variables!!!
        if (newWorkVarList.isEmpty())
            newWorkVarList = null;

        doUpdateDerivationStepFormulaStuff(d, newWorkVarList);
    }

    private void doUpdateDerivationStepFormulaStuff(final DerivationStep d,
        final List<WorkVar> newWorkVarList)
    {

        final ParseTree newFormulaParseTree = d.formulaParseTree
            .deepCloneApplyingWorkVarUpdates();

        final Formula newFormula = verifyProofs.convertRPNToFormula(
            newFormulaParseTree.convertToRPN(), " "); // abend diagnostic, leave
                                                      // blank for now.

        newFormula.setTyp(provableLogicStmtTyp);

        boolean stmtTextAlreadyUpdated = false;
        if (!proofAsstPreferences.getAutoReformat())
            stmtTextAlreadyUpdated = d
                .updateStmtTextWithWorkVarUpdates(verifyProofs);

        d.updateWorkVarList(newWorkVarList);

        d.loadGeneratedFormulaIntoDerivStep(newFormula, newFormulaParseTree,
            stmtTextAlreadyUpdated);
    }

    private void doUpdateDerivationStepAssrtSubst(final DerivationStep d) {

        if (d.assrtSubst == null)
            return;

        for (int i = 0; i < d.assrtSubst.length; i++)
            // note: log hyp array entries will be null at
            // this point (prior to proof construction).
            if (d.assrtSubst[i] != null)
                d.assrtSubst[i] = d.assrtSubst[i]
                    .deepCloneApplyingWorkVarUpdates();
    }

    private void doUpdateWorkVarUnificationStatus(final DerivationStep d) {
        // ok, now adjust unification status for the revised
        // work var situation

        // this weird looking if statement means status is
        // unified but not equal to UNIFIED_W_INCOMPLETE_HYPS
        if (d.unificationStatus == PaConstants.UNIFICATION_STATUS_UNIFIED_W_WORK_VARS
            || d.unificationStatus == PaConstants.UNIFICATION_STATUS_UNIFIED)
        {
            d.unificationStatus = PaConstants.UNIFICATION_STATUS_UNIFIED;
            if (d.workVarList != null)
                d.unificationStatus = PaConstants.UNIFICATION_STATUS_UNIFIED_W_WORK_VARS;
            else
                for (int i = 0; i < d.hyp.length; i++) {
                    if (!d.hyp[i].isDerivationStep())
                        continue;
                    if (d.hyp[i].workVarList != null
                        || ((DerivationStep)d.hyp[i]).unificationStatus == PaConstants.UNIFICATION_STATUS_UNIFIED_W_WORK_VARS)
                        d.unificationStatus = PaConstants.UNIFICATION_STATUS_UNIFIED_W_WORK_VARS;
                }
        }
    }

    public static void separateMandAndOptFrame(
        final ProofWorksheet proofWorksheet, final DerivationStep qedStep,
        final List<Hyp> mandHypList, final List<Hyp> optHypList,
        final boolean addLogHyps)
    {
        qedStep.formulaParseTree.getRoot()
            .accumVarHypUsedListBySeq(mandHypList);

        if (proofWorksheet.hypStepCnt > 0) {

            ProofWorkStmt proofWorkStmt;

            int hypsFound = 0;
            int stepIndex = 0;
            while (true) {

                proofWorkStmt = proofWorksheet.proofWorkStmtList.get(stepIndex);

                if (proofWorkStmt.isHypothesisStep()) {
                    ((ProofStepStmt)proofWorkStmt).formulaParseTree.getRoot()
                        .accumVarHypUsedListBySeq(mandHypList);
                    if (++hypsFound >= proofWorksheet.hypStepCnt)
                        break;
                }

                if (++stepIndex > proofWorksheet.proofWorkStmtList.size())
                    break;
            }

            if (hypsFound != proofWorksheet.hypStepCnt)
                throw new IllegalArgumentException(
                    PaConstants.ERRMSG_HYP_STEP_CNT_IN_WORKKSHEET_ERROR_1);
        }

        final Hyp[] frameHypArray = proofWorksheet.comboFrame.hypArray;

        for (final Hyp element : frameHypArray)
            if (element.isVarHyp()) {
                final VarHyp vH = (VarHyp)element;
                if (!vH.containedInVarHypListBySeq(mandHypList))
                    vH.accumVarHypListBySeq(optHypList);
            }
            else if (addLogHyps)
                mandHypList.add(element);
    }

    /**
     * Any remaining Work Vars left in the Proof Worksheet after unification are
     * converted to dummy variables.
     * <p>
     * Dummy Variables are members of the Optional Frame of the theorem. That
     * is, they are in scope for the theorem, and "active", but are not used in
     * the 'qed' step or the theorem's hypotheses.
     * <p>
     * Note though that Work Variables are an add-on and are stored globally,
     * not in the theorem's frames.
     * <p>
     * Also, even though we are converting *variables* we never work with the
     * formulas directly because variables can be redefined, and may even be
     * locally defined so that by the time we get *here* the hypothesis
     * originally associated with a variable may no longer be active and
     * available via the Var object. So we do our main work with Optional Var
     * Hyps and convert back to Variables. This requires using the
     * step.formulaParseTree instead of step.formula.
     * 
     * @param qedStep the last (QED) step of the proof
     * @throws VerifyException if an error occurs
     */
    private void convertWorkVarsToDummyVars(final DerivationStep qedStep)
        throws VerifyException
    {

        VarHyp vH;
        Cnst typ;

        // 1) construct mandatoryVarHypList and optionalVarHypList
        // (new theorems not in the database require these to
        // be computed, so we do the computations for both old
        // and new to "simplify".)

        final List<Hyp> mandatoryVarHypList = new ArrayList<Hyp>(
            proofWorksheet.comboFrame.hypArray.length);

        final List<Hyp> optionalVarHypList = new ArrayList<Hyp>(
            proofWorksheet.comboFrame.hypArray.length);

        separateMandAndOptFrame(proofWorksheet, qedStep, mandatoryVarHypList,
            optionalVarHypList, false);

        // 2) initialize list of disjointWorkVarList's and
        // optionalVarHypsInUseList

        final List<List<Var>> disjointWorkVarList // list of lists
        = new ArrayList<List<Var>>();
        final List<Hyp> optionalVarHypsInUseList // list of Var Hyps
        = new ArrayList<Hyp>();

        // 3) make recursive pass through the qed derivation
        // subtree loading the disjointWorkVarHypList and
        // optionalVarHypsInUseList

        recursiveLoadWvAndOptsUsedLists(qedStep, optionalVarHypList,
            optionalVarHypsInUseList, disjointWorkVarList);

        // 3.5 see doc in function below
        final List<List<VarHyp>> disjointWorkVarHypList = buildDisjointWorkVarHypList(disjointWorkVarList);

        // 4) construct unusedOptionalVarHypTypList and
        // unusedOptionalVarHypsByTypList using
        // optionalVarHypList and optionalVarHypsInUseList
        final List<Cnst> unusedOptionalVarHypTypList // list of Cnst
        = new ArrayList<Cnst>();
        final List<List<Hyp>> unusedOptionalVarHypsByTypList // list
                                                             // of
                                                             // lists
        = new ArrayList<List<Hyp>>();
        for (int i = 0; i < optionalVarHypList.size(); i++) {

            vH = (VarHyp)optionalVarHypList.get(i);

            if (vH.containedInVarHypListBySeq(optionalVarHypsInUseList))
                continue;

            // ok, optional varHyp "vH" is unused

            typ = vH.getTyp();

            int typIndex = -1;
            for (int j = 0; j < unusedOptionalVarHypTypList.size(); j++)
                if (typ == unusedOptionalVarHypTypList.get(j)) {
                    typIndex = j;
                    break;
                }

            if (typIndex == -1) {

                unusedOptionalVarHypTypList.add(typ);

                typIndex = unusedOptionalVarHypTypList.size() - 1;

                unusedOptionalVarHypsByTypList.add(new ArrayList<Hyp>());
            }

            final List<Hyp> typUnusedOptionalVarHyps = unusedOptionalVarHypsByTypList
                .get(typIndex);

            vH.accumVarHypListBySeq(typUnusedOptionalVarHyps);
        }

        // 5) for each disjointWorkVarHypList:
        // - for each Type Code in a disjointWorkVarHypList
        // - start at index 0 in unusedOptionalVarHypsList
        // - assign next unusedOptionalVarHypList element
        // of the current Type to the corresponding
        // VarHyp.paSubst;
        // --> if not enough unusedOptionalVarHyp entries,
        // kick out an error message and quit.
        //
        for (int i = 0; i < disjointWorkVarHypList.size(); i++) {
            final List<?> workVarHypList = disjointWorkVarHypList.get(i);

            for (int j = 0; j < unusedOptionalVarHypTypList.size(); j++) {
                typ = unusedOptionalVarHypTypList.get(j);

                final List<?> typUnusedOptionalVarHyps = unusedOptionalVarHypsByTypList
                    .get(j);

                int next = 0;
                for (int k = 0; k < workVarHypList.size(); k++) {
                    vH = (VarHyp)workVarHypList.get(k);
                    if (vH.getTyp() != typ)
                        continue;
                    if (next >= typUnusedOptionalVarHyps.size())
                        throw new VerifyException(
                            PaConstants.ERRMSG_WV_CLEANUP_SHORTAGE_1
                                + proofWorksheet.getTheorem().getLabel()
                                + PaConstants.ERRMSG_WV_CLEANUP_SHORTAGE_2
                                + typ
                                + PaConstants.ERRMSG_WV_CLEANUP_SHORTAGE_3
                                + typUnusedOptionalVarHyps.size()
                                + PaConstants.ERRMSG_WV_CLEANUP_SHORTAGE_4);

                    vH.paSubst = // this is a really key step :-)
                    new ParseNode((VarHyp)typUnusedOptionalVarHyps.get(next));

                    ++next;
                }
            }
        }

        // 6) make recursive pass through the qed derivation
        // subtree by calling convertStepWorkVarsToDummies
        // passing the 'qed' derivation step.
        //
        // NOTE: redo the Dj Vars edits for any derivation
        // step with Work Variables or with an
        // immediate hypothesis containing Work Variables.
        //
        recursiveConvertStepWorkVarHypsToDummies(qedStep);

    }

    private void recursiveConvertStepWorkVarHypsToDummies(final DerivationStep d)
    {
        boolean redoDjVarsEdits = false;
        DerivationStep dH;
        for (final ProofStepStmt element : d.hyp)
            if (element.isDerivationStep()) {
                dH = (DerivationStep)element;
                if (dH.workVarList != null)
                    redoDjVarsEdits = true;
                recursiveConvertStepWorkVarHypsToDummies(dH);
            }

        if (d.workVarList != null) {
            redoDjVarsEdits = true;
            doUpdateDerivationStepFormulaStuff(d, null);
        }

        doUpdateDerivationStepAssrtSubst(d);

        if (d.unificationStatus == PaConstants.UNIFICATION_STATUS_UNIFIED_W_WORK_VARS)
            d.unificationStatus = PaConstants.UNIFICATION_STATUS_UNIFIED;

        if (redoDjVarsEdits) {
            doInitialStepDjEdits(d, (Assrt)d.ref, d.assrtSubst);
            saveOtherDjVarsEditResults(d);
        }
    }

    // note: neither the 'qed' step, nor the theorem
    // hypotheses can contain work variables or
    // optional/dummy variables.
    private void recursiveLoadWvAndOptsUsedLists(final DerivationStep d,
        final List<Hyp> optionalVarHypList,
        final List<Hyp> optionalVarHypsInUseList,
        final List<List<Var>> disjointWorkVarList)
    {

        for (int i = 0; i < d.hyp.length; i++) {
            if (!d.hyp[i].isDerivationStep())
                continue;
            recursiveLoadWvAndOptsUsedLists((DerivationStep)d.hyp[i],
                optionalVarHypList, optionalVarHypsInUseList,
                disjointWorkVarList);
        }

        // accumulate optional formula var hyps into "in use" list
        if (d.step.compareToIgnoreCase(PaConstants.QED_STEP_NBR) != 0)
            d.formulaParseTree.getRoot().accumListVarHypUsedListBySeq(
                optionalVarHypList, optionalVarHypsInUseList);

        // accumulate a new workVarList containing the step
        // AND its hyp work vars. (later we make the conversion
        // from WorkVar to WorkVarHyp, but not yet...)
        final List<Var> stepAndHypWorkVarList = new ArrayList<Var>();
        if (d.workVarList != null)
            mergeVarList1IntoList2(d.workVarList, stepAndHypWorkVarList);
        for (int i = 0; i < d.hyp.length; i++) {
            if (!d.hyp[i].isDerivationStep())
                continue;
            if (((DerivationStep)d.hyp[i]).workVarList != null)
                mergeVarList1IntoList2(((DerivationStep)d.hyp[i]).workVarList,
                    stepAndHypWorkVarList);
        }

        if (stepAndHypWorkVarList.isEmpty())
            return;

        // if stepAndHypWorkVarList disjoint with all other lists
        // in disjointWorkVarList, add it; otherwise,
        // union it with the first list having a var in common:
        List<Var> x;
        Var v;
        boolean isDisjoint = true;
        loopI: for (int i = 0; i < disjointWorkVarList.size(); i++) {

            x = disjointWorkVarList.get(i);

            for (int j = 0; j < stepAndHypWorkVarList.size(); j++) {

                v = stepAndHypWorkVarList.get(j);

                if (v.containedInVarListBySeq(x)) {

                    isDisjoint = false;

                    mergeVarList1IntoList2(stepAndHypWorkVarList, x);
                    break loopI;
                }
            }
        }
        if (isDisjoint)
            disjointWorkVarList.add(stepAndHypWorkVarList);
    }

    // 3.5) NOTE!!! The disjointWorkVarList is just
    // a preliminary list. Assume we have steps
    // 63->65 in proof of ax11eq (we do):
    //
    // 63::a9e |- E. &S3 &S3 = w
    // 64::a9e |- E. &S5 &S5 = z
    // 65::ax-1 |- ( &S5 = &S3 -> ( x = y -> &S5 = &S3 ) )
    //
    // And assume the disjointWorkVarHypList of lists
    // was built in proof step order. The initial
    // result would be { { &W3 , &W5 }, { &W5 } }
    // and in 5) below, the second occurrence of
    // &W5 would wipe out the first, so if the
    // first assigned "v" to &W5, the second would
    // change that to "u" and then both &W3 and
    // &W5 would be set to "u", which would cause
    // a bug for ax11eq (and 88 other theorems!).
    //
    // So, reprocess the list in to merge+eliminate
    // any entries that are not truly disjoint, and
    // make the conversion from WorkVar to WorkVarHyp.
    private List<List<VarHyp>> buildDisjointWorkVarHypList(
        final List<List<Var>> inVarListOfLists)
    {

        final List<List<VarHyp>> outVarHypListOfLists = new ArrayList<List<VarHyp>>(
            inVarListOfLists.size());

        final int iMax = inVarListOfLists.size();
        final int jMax = iMax;

        List<Var> candidateI;
        List<Var> candidateJ;

        loopI: for (int i = 0; i < iMax; i++) {

            candidateI = inVarListOfLists.get(i);

            loopJ: for (int j = i + 1; j < jMax; j++) {

                candidateJ = inVarListOfLists.get(j);

                if (isVarList1DisjointWithList2(candidateI, candidateJ))
                    continue loopJ;

                mergeVarList1IntoList2(candidateI, candidateJ);
                continue loopI;
            }

            // convert WorkVarList to WorkVarHyp list
            // note: WorkVars are always global and "active"
            final List<VarHyp> workVarHypList = new ArrayList<VarHyp>(
                candidateI.size());
            for (int k = 0; k < candidateI.size(); k++)
                workVarHypList.add(candidateI.get(k).getActiveVarHyp());
            outVarHypListOfLists.add(workVarHypList);
        }

        return outVarHypListOfLists;
    }

    private boolean isVarList1DisjointWithList2(final List<Var> list1,
        final List<Var> list2)
    {

        Var v;
        for (int i = 0; i < list1.size(); i++) {
            v = list1.get(i);
            if (v.containedInVarListBySeq(list2))
                return false;
        }
        return true;
    }

    private <T extends Var> void mergeVarList1IntoList2(final List<T> list1,
        final List<Var> list2)
    {
        Var v;
        for (int i = 0; i < list1.size(); i++) {
            v = list1.get(i);
            v.accumVarListBySeq(list2);
        }
    }
}
