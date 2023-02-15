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

import static mmj.pa.ProofWorksheet.addLabelContext;

import java.util.*;
import java.util.Map.Entry;

import mmj.lang.*;
import mmj.pa.MacroManager.CallbackType;
import mmj.pa.PaConstants.*;
import mmj.pa.StepRequest.StepRequestType;
import mmj.transforms.TransformationManager;
import mmj.verify.*;

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
 * the VerifyProofs.java object -- i.e. the Metamath "Proof Verification Engine"
 * . The default option is NO, but should probably be YES in practice given that
 * response time is not a problem (yet?) This will catch false unifications
 * early.
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

    private final Deque<ParseNode> unifyNodeStack = new ArrayDeque<>();

    private final Deque<ParseNode> compareNodeStack = new ArrayDeque<>();

    private TransformationManager trManager;

    public PostUnifyHook postUnifyHook;

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

    private int derivStepsWithEmptyRefCount;
    private DerivationStep[] derivStepsWithEmptyRef;

    private int autoDerivStepsCount;
    private DerivationStep[] autoDerivSteps;

    private ProofStepStmt[] derivStepHypArray;
    private final static ParseNode[] substAnswerImpossible = new ParseNode[0];

    /**
     * The map from hypothesis substitution "level" into the number of variable
     * which become substituted at that level. The level is the number of
     * successfully substituted hypothesis. This map is used to erase fail
     * substitutions from assrtSubst map.
     */
    private final int[] levelCleanupCnt = new int[PaConstants.UNIFIER_MAX_LOG_HYPS];

    /**
     * The map from hypothesis substitution "level" into the array of variables
     * (indexes in assrtSubst array) which become substituted at that level. The
     * level is the number of successfully substituted hypothesis. This map is
     * used to erase fail substitutions from assrtSubst map.
     */
    private final int[][] levelCleanup = new int[PaConstants.UNIFIER_MAX_LOG_HYPS][];

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
     * @param proofAsst the main ProofAsst object
     */
    public ProofUnifier(final ProofAsst proofAsst) {
        proofAsstPreferences = proofAsst.getPreferences();
        logicalSystem = proofAsst.getLogicalSystem();
        grammar = proofAsst.getGrammar();
        verifyProofs = proofAsst.getVerifyProofs();

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

        final Map<String, Stmt> stmtTbl = logicalSystem.getStmtTbl();

        final List<Assrt> unifySearchListUnsorted = new ArrayList<>(
            stmtTbl.size());

        final Set<String> excl = proofAsstPreferences.unifySearchExclude.get();
        if (!excl.isEmpty())
            messages.accumMessage(PaConstants.ERRMSG_UNIFY_SEARCH_EXCLUDE,
                excl);

        for (final String label : excl) {
            final Stmt stmt = stmtTbl.get(label);
            if (stmt instanceof Assrt)
                ((Assrt)stmt).setExcluded(true);
        }

        final boolean excludeDiscouraged = proofAsstPreferences.excludeDiscouraged
            .get();

        for (final Stmt stmt : stmtTbl.values())
            if (stmt instanceof Assrt) {
                if (excludeDiscouraged && stmt.getDescription() != null
                    && stmt.getDescriptionForSearch()
                        .contains("(New usage is discouraged.)"))
                    ((Assrt)stmt).setExcluded(true);
                if (stmt.getFormula().getTyp() == provableLogicStmtTyp
                    && !((Assrt)stmt).isExcluded())
                    unifySearchListUnsorted.add((Assrt)stmt);
            }

        final int listSize = unifySearchListUnsorted.size()
            * (100 + proofAsstPreferences.assrtListFreespace.get()) / 100;
        unifySearchList = new ArrayList<>(listSize);

        unifySearchList.addAll(unifySearchListUnsorted);

        Collections.sort(unifySearchList, MObj.SEQ);

        stepSelectorSearch = new StepSelectorSearch(proofAsstPreferences,
            verifyProofs, provableLogicStmtTyp, unifySearchList);

        return tablesInitialized = true;
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
     * @param noConvertWV true if we should not replace work vars with dummy
     *            vars in derivation steps
     * @throws VerifyException if unification was unsuccessful
     */
    public void unifyAllProofDerivationSteps(
        final ProofWorksheet proofWorksheet, final Messages messages,
        final boolean noConvertWV) throws VerifyException
    {

        if (!getTablesInitialized())
            throw new IllegalStateException(new ProofAsstException(
                PaConstants.ERRMSG_UNIFY_TABLES_NOT_INIT));

        this.proofWorksheet = proofWorksheet;

        holdSoftDjVarsErrorList = new ArrayList<>();

        this.messages = messages;

        if (proofWorksheet.stepRequest != null
            && (proofWorksheet.stepRequest.type == StepRequestType.GeneralSearch
                || proofWorksheet.stepRequest.type == StepRequestType.SearchOptions
                    && proofWorksheet.stepRequest.param1 == null))
            return;

        // ...also loads dsa1 array for input to
        // parallelStepUnificationMethod
        unifyStepsHavingRefLabels();
        proofWorksheet.runCallback(CallbackType.AFTER_UNIFY_REFS);

        if (proofWorksheet.stepRequest != null
            && proofWorksheet.stepRequest.type.simple)
            return; // our work here is complete :-)

        if (derivStepsWithEmptyRefCount > 0)
            // still some left to unify!
            emptyRefStepUnificationMethod();
        proofWorksheet.runCallback(CallbackType.AFTER_UNIFY_EMPTY);

        if (autoDerivStepsCount > 0)
            autoStepSearchForDuplicates();
        if (autoDerivStepsCount > 0)
            autoStepUnificationMethod();
        proofWorksheet.runCallback(CallbackType.AFTER_UNIFY_AUTO);

        // It is possible that auto step unification can introduce steps with
        // local refs, so we fix this here before going to press
        proofWorksheet.makeLocalRefRevisionsToWorksheet();

        final DerivationStep qed = proofWorksheet.getQedStep();

        if (qed.unificationStatus.proper)
            // do fixup before convertWorkVarsToDummyVars()
            chainWorkVarAndIncompleteHypStatuses();

        if (qed.unificationStatus == UnificationStatus.UnifiedWWorkVars)
            if (noConvertWV) {
                final DerivationStep d = getFirstWorkVarStep(qed);
                if (d != null) {
                    messages.accumException(addLabelContext(proofWorksheet,
                        PaConstants.ERRMSG_NO_CONVERT_WV));
                    proofWorksheet.getProofCursor().setCursorAtProofWorkStmt(d,
                        PaConstants.FIELD_ID_REF);
                    return;
                }
            }
            else
                try {
                    convertWorkVarsToDummyVars(qed);
                } catch (final VerifyException e) {
                    messages.accumException(e);
                    proofWorksheet.getProofCursor().setCursorAtProofWorkStmt(
                        qed, PaConstants.FIELD_ID_REF);
                }

        final boolean hasHardDjVarsErrors = buildProofsAndErrorUnUnifiedSteps();

        if (qed.getProofTree() == null || hasHardDjVarsErrors)
            reportAlternateUnifications();
    }

    private DerivationStep getFirstWorkVarStep(final DerivationStep step) {
        for (final ProofStepStmt e : step.getHypList())
            if (e instanceof DerivationStep) {
                final DerivationStep d = getFirstWorkVarStep((DerivationStep)e);
                if (d != null)
                    return d;
            }
        for (final Sym s : step.getFormula().getSym())
            if (s instanceof WorkVar)
                return step;
        return null;
    }
    /**
     * a final fixup to ensure that we do not attempt work var -> dummy var
     * conversion unless everything is kosher (and no incomplete unifications
     * among the hyps).
     */
    private void chainWorkVarAndIncompleteHypStatuses() {

        for (final ProofWorkStmt proofWorkStmtObject : proofWorksheet
            .getProofWorkStmtList())
        {

            if (proofWorkStmtObject instanceof DerivationStep)
                derivStep = (DerivationStep)proofWorkStmtObject;
            else
                continue;

            if (!derivStep.unificationStatus.proper)
                continue;

            for (int i = 0; i < derivStep.getHypNumber(); i++) {

                if (!(derivStep.getHyp(i) instanceof DerivationStep))
                    continue;
                final DerivationStep dH = (DerivationStep)derivStep.getHyp(i);

                if (derivStep.unificationStatus == UnificationStatus.Unified)
                    if (dH.workVarList != null
                        || dH.unificationStatus == UnificationStatus.UnifiedWWorkVars)
                        derivStep.unificationStatus = UnificationStatus.UnifiedWWorkVars;

                if (dH.unificationStatus == UnificationStatus.UnifiedWIncompleteHyps)
                    derivStep.unificationStatus = UnificationStatus.UnifiedWIncompleteHyps;

                if (dH.isHypFldIncomplete() || !dH.unificationStatus.proper) {

                    derivStep.unificationStatus = UnificationStatus.UnifiedWIncompleteHyps;

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
                && proofWorksheet.stepRequest.type.simple)
            unifyStepsInvolvingWorkVars();

        if (proofWorksheet.stepRequest != null
            && proofWorksheet.stepRequest.type.simple)
            return; // our work here is complete :-)

        // 2) Make pass for steps with Ref labels that don't
        // involve work vars or derives.
        derivStepsWithEmptyRefCount = 0;
        derivStepsWithEmptyRef = new DerivationStep[proofWorksheet
            .getProofWorkStmtListCnt()];

        autoDerivStepsCount = 0;
        autoDerivSteps = new DerivationStep[proofWorksheet
            .getProofWorkStmtListCnt()];

        stepLoop: for (final ProofWorkStmt proofWorkStmtObject : proofWorksheet
            .getProofWorkStmtList())
        {
            if (!(proofWorkStmtObject instanceof DerivationStep))
                continue;

            derivStep = (DerivationStep)proofWorkStmtObject;

            if (derivStep.isHypFldIncomplete())
                continue;

            if (derivStep.getRef() == null
                && derivStep.hasWorkVarsInStepOrItsHyps())
                continue;

            if (derivStep.unificationStatus != UnificationStatus.NotUnified)
                // already unified/failed in
                // unifyStepsInvolvingWorkVars()
                continue;

            // this is b.s. but in theory a hyp step could have
            // failed in deriveFormula...
            for (int i = 0; i < derivStep.getHypNumber(); i++)
                if (derivStep.getHyp(i) != null
                    && derivStep.getHyp(i).formulaParseTree == null)
                {

                    // this will
                    // just be bypassed and considered incomplete
                    // based on the idea that the failed earlier
                    // step will generate a message
                    derivStep.unificationStatus = UnificationStatus.AttemptCancelled;
                    continue stepLoop;
                }

            // build array of steps for parallel unification loop
            if (derivStep.getRef() == null) {
                if (derivStep.isAutoStep())
                    autoDerivSteps[autoDerivStepsCount++] = derivStep;
                else
                    derivStepsWithEmptyRef[derivStepsWithEmptyRefCount++] = derivStep;
                continue;
            }

            assrt = (Assrt)derivStep.getRef();
            assrtNbrLogHyps = assrt.getLogHypArrayLength();

            if (derivStep.getHypNumber() != assrtNbrLogHyps)
                throw new IllegalArgumentException(
                    addLabelContext(proofWorksheet,
                        new VerifyException(
                            PaConstants.ERRMSG_STEP_REF_HYP_NBR_ERR,
                            derivStep.getStep())));

            if (!unifyStepWithoutWorkVars().possible()) {
                markRefUnificationFailure(assrt);
                continue;
            }

            if (derivStep.djVarsErrorStatus != DjVarsErrorStatus.None)
                // unified but build alternates list!!!
                derivStepsWithEmptyRef[derivStepsWithEmptyRefCount++] = derivStep;
            continue;
        }
    }

    private void autoStepSearchForDuplicates() {
        int nbrCompleted = 0;
        for (int i = 0; i < autoDerivStepsCount; i++) {
            derivStep = autoDerivSteps[i];
            final ProofStepStmt stmt = proofWorksheet
                .findMatchingStepFormula(derivStep.getFormula(), derivStep);
            if (stmt != null) {
                derivStep.setLocalRef(stmt);
                autoDerivSteps[i] = null;
                nbrCompleted++;
            }
        }
        if (nbrCompleted > 0) {
            shiftEmptyElements(autoDerivSteps, autoDerivStepsCount);
            autoDerivStepsCount -= nbrCompleted;
            nbrCompleted = 0;
        }
    }

    /**
     * This function deals only with auto steps
     *
     * @throws VerifyException Verification exception
     */
    private void autoStepUnificationMethod() throws VerifyException {
        int nbrCompleted = 0;
        final int maxSeq = proofWorksheet.getMaxSeq();

        final Map<DerivationStep, UnifyResult> autoBestResults = new HashMap<>();

        for (final Assrt a : unifySearchList) {
            if (a.getSeq() >= maxSeq)
                // halt the scan -- the list is sorted!!!
                break;

            assrt = a;
            assrtNbrLogHyps = assrt.getLogHypArrayLength();

            int i = -1;
            final int max = autoDerivStepsCount; // fool optimizer
            while (true) {
                if (++i >= max)
                    break;
                derivStep = autoDerivSteps[i];
                assert derivStep.isAutoStep();

                if (derivStep.getRef() != null)
                    if (assrt != derivStep.getRef())
                        continue;

                // TODO: use derivStep.getHypNumber() <= assrtNbrLogHyps

                final UnifyResult res = unifyStepWithoutWorkVars();
                if (res.proper()) {
                    // for auto step unification this assert should be true:
                    assert derivStep.djVarsErrorStatus == DjVarsErrorStatus.None;
                    // stick fork in it, this one is done!
                    autoDerivSteps[i] = null;
                    nbrCompleted++;

                    if (autoBestResults.get(derivStep) != null)
                        autoBestResults.remove(derivStep);
                }
                else if (res.possible()) {
                    // the step in this case should remains auto step
                    assert derivStep.isAutoStep();
                    final UnifyResult old = autoBestResults.get(derivStep);
                    final UnifyResult best = bestAutoResult(old, res);
                    if (best != old)
                        autoBestResults.put(derivStep, best);
                }
            }

            // this could be optimized a little :-)
            if (nbrCompleted > 0) {
                shiftEmptyElements(autoDerivSteps, autoDerivStepsCount);
                autoDerivStepsCount -= nbrCompleted;
                nbrCompleted = 0;
            }

            if (autoDerivStepsCount > 0)
                continue;
            break;
        }

        for (final Entry<DerivationStep, UnifyResult> elem : autoBestResults
            .entrySet())
        {
            derivStep = elem.getKey();

            for (int i = 0; i < autoDerivStepsCount; i++)
                if (derivStep == autoDerivSteps[i])
                    autoDerivSteps[i] = null;

            final UnifyResult res = elem.getValue();

            final AutoUnifyResultDetails details = res.details();
            assrt = details.assertion;
            assrtSubst = details.assrtSubst;

            if (proofAsstPreferences.djVarsSoftErrors
                .get() == DjVarsSoftErrors.Report)
                messages.accumException(addStepContext(
                    new ProofAsstException(PaConstants.ERRMSG_POSSIBLE_SUBST,
                        assrt, details.softDjVarsErrorList)));
            else
                markAutoStepUnified(details.hypSortDerivArray);
        }

        if (trManager == null)
            return;

        shiftEmptyElements(autoDerivSteps, autoDerivStepsCount);
        autoDerivStepsCount -= autoBestResults.size();

        for (int i = 0; i < autoDerivStepsCount; i++) {
            final List<DerivationStep> list = trManager
                .tryToFindTransformations(proofWorksheet, autoDerivSteps[i]);
            if (list != null) {
                for (final DerivationStep d : list) {
                    derivStep = d;
                    assrt = (Assrt)derivStep.getRef();
                    if (assrt != null) {
                        assrtNbrLogHyps = assrt.getLogHypArrayLength();
                        final UnifyResult res = unifyStepWithoutWorkVars();
                        assert res.proper();
                    }
                }
                derivStepHypArray = derivStep.getHypList();
                markStepUnified(false, false, null);
            }
        }
    }

    /**
     * This function deals with classic steps without 'ref' part
     *
     * @throws VerifyException Verification exception
     */
    private void emptyRefStepUnificationMethod() throws VerifyException {
        final int maxSeq = proofWorksheet.getMaxSeq();
        int nbrCompleted = 0;

        for (final Assrt a : unifySearchList) {
            if (a.getSeq() >= maxSeq)
                // halt the scan -- the list is sorted!!!
                break;

            assrt = a;
            assrtNbrLogHyps = assrt.getLogHypArrayLength();

            int i = -1;
            final int max = derivStepsWithEmptyRefCount; // fool optimizer
            while (true) {
                if (++i >= max)
                    break;
                derivStep = derivStepsWithEmptyRef[i];
                assert !derivStep.isAutoStep();

                if (derivStep.getHypNumber() == assrtNbrLogHyps) {
                    // this property could be changed after the next call, so
                    // save it
                    final UnifyResult res = unifyStepWithoutWorkVars();
                    if (res.proper())
                        if (derivStep.djVarsErrorStatus == DjVarsErrorStatus.None) {
                            // stick fork in it, this one is done!
                            derivStepsWithEmptyRef[i] = null;
                            nbrCompleted++;
                        }
                }
                continue;
            }

            // this could be optimized a little :-)
            if (nbrCompleted > 0) {
                shiftEmptyElements(derivStepsWithEmptyRef,
                    derivStepsWithEmptyRefCount);
                derivStepsWithEmptyRefCount -= nbrCompleted;
                nbrCompleted = 0;
            }

            if (derivStepsWithEmptyRefCount > 0)
                continue;
            break;
        }
    }
    private boolean buildProofsAndErrorUnUnifiedSteps() {
        // ok, finish: error un-unified steps and
        // build proofs for the unified steps!

        boolean hardDjVarsErrorsFound = false;

        for (final ProofWorkStmt proofWorkStmtObject : proofWorksheet
            .getProofWorkStmtList())
        {

            if (proofWorkStmtObject instanceof DerivationStep)
                derivStep = (DerivationStep)proofWorkStmtObject;
            else
                continue;

            if (derivStep.djVarsErrorStatus == DjVarsErrorStatus.Hard)
                hardDjVarsErrorsFound = true;

            if (derivStep.unificationStatus == UnificationStatus.NotUnified) {
                // if (!derivStep.isHypFldIncomplete())
                // markUnificationFailure();
            }
            else if (derivStep.unificationStatus.proper)
                attemptProofOfDerivStep();

            if (derivStep.djVarsErrorStatus == DjVarsErrorStatus.None)
                continue;

            // accum ArrayLists of DjVars into ProofWorksheet
            // ArrayList of Arraylists of DjVars...
            if (derivStep.djVarsErrorStatus == DjVarsErrorStatus.Soft
                && derivStep.softDjVarsErrorList != null
                && !derivStep.softDjVarsErrorList.isEmpty())
            {
                // that means we are generating $d's!
                if (proofWorksheet.proofSoftDjVarsErrorList == null)
                    proofWorksheet.proofSoftDjVarsErrorList = new ArrayList<>();
                proofWorksheet.proofSoftDjVarsErrorList
                    .add(derivStep.softDjVarsErrorList);
            }

            if (derivStep.djVarsErrorStatus == DjVarsErrorStatus.Hard
                || proofAsstPreferences.djVarsSoftErrors
                    .get() == DjVarsSoftErrors.Report
                    && derivStep.djVarsErrorStatus == DjVarsErrorStatus.Soft)
            {

                if (derivStep.alternateRefList != null) {
                    messages.accumException(derivStep.heldDjErrorMessage);
                    accumAlternateRefMessage();
                }
                else
                    messages.accumException(derivStep.heldDjErrorMessage);
                proofWorksheet.getProofCursor().setCursorAtProofWorkStmt(
                    derivStep, PaConstants.FIELD_ID_REF);
            }
        }
        return hardDjVarsErrorsFound;
    }

    private void reportAlternateUnifications() {

        for (final ProofWorkStmt proofWorkStmtObject : proofWorksheet
            .getProofWorkStmtList())
            if (proofWorkStmtObject instanceof DerivationStep) {

                derivStep = (DerivationStep)proofWorkStmtObject;

                if (derivStep.djVarsErrorStatus == // already reported
                DjVarsErrorStatus.None && derivStep.alternateRefList != null
                    && !derivStep.alternateRefList.isEmpty())
                {
                    accumAlternateRefMessage();
                    proofWorksheet.getProofCursor().setCursorAtProofWorkStmt(
                        derivStep, PaConstants.FIELD_ID_REF);
                }

            }
    }

    private void accumAlternateRefMessage() {
        if (!derivStep.alternateRefList.isEmpty())
            messages.accumException(getException(
                PaConstants.ERRMSG_ALT_UNIFY_REFS, derivStep.alternateRefList));
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
        for (int i = 0; i < derivStep.getHypNumber(); i++) {
            holdHypProofStepStmt = derivStep.getHyp(i);

            // Must be a HypothesisStep (otherwise status
            // would have been changed)
            if (holdHypProofStepStmt instanceof HypothesisStep) {
                if (proofWorksheet.isNewTheorem()
                    && holdHypProofStepStmt.getRef() == null)
                    buildNewTheoremLogHyp(i,
                        (HypothesisStep)holdHypProofStepStmt);
                holdHypNode = new ParseNode(holdHypProofStepStmt.getRef());
            }
            else {
                final DerivationStep d = (DerivationStep)holdHypProofStepStmt;
                final ParseTree hypProofTree = d.getProofTree();
                if (hypProofTree != null) {
                    holdHypNode = hypProofTree.getRoot();
                    if (d.unificationStatus == UnificationStatus.UnifiedWWorkVars)
                        derivStep.unificationStatus = UnificationStatus.UnifiedWWorkVars;
                }
                else {
                    // No need for message as the prev step has one.
                    derivStep.unificationStatus = UnificationStatus.UnifiedWWorkVars;
                    return;
                }
            }

            while (++substIndex < derivStep.getAssrtSubstNumber())
                if (derivStep.getAssrtSubst(substIndex) == null) {
                    derivStep.setAssrtSubst(substIndex, holdHypNode);
                    break;
                }

            // If no empty entry in array for hyp's proof tree root
            // then we are FUBAR
            if (substIndex >= derivStep.getAssrtSubstNumber())
                throw new IllegalArgumentException(
                    getException(PaConstants.ERRMSG_ASSRT_SUBST_SLOT, i));
        }

        // Note that the proof tree root has a "child" array
        // that is merely a parallel array of the ref's
        // hypArray (holding VarHyp and LogHyp entires)!
        final ParseNode proofRoot = new ParseNode(derivStep.getRef(),
            derivStep.getAssrtSubstList());

        derivStep.setProofTree(new ParseTree(proofRoot));

        if (proofAsstPreferences.recheckProofAsstUsingProofVerifier.get())
            if (!checkDerivStepProofUsingVerify()) {
                messages.accumException(addStepContext(new ProofAsstException(
                    PaConstants.ERRMSG_VERIFY_RECHECK_ERR)));

                derivStep.verifyProofError = true;
                proofWorksheet.getProofCursor().setCursorAtProofWorkStmt(
                    derivStep, PaConstants.FIELD_ID_REF);
                return;
            }
    }

    /**
     * Build a temporary LogHyp so that the proof tree conforms to the norm --
     * and when it is time to do other things like verify proof or generate the
     * RPN, we can use existing code!
     *
     * @param hypIndexNbr the index of the hypothesis
     * @param hypothesisStep the step for which to build the LogHyp
     */
    private void buildNewTheoremLogHyp(final int hypIndexNbr,
        final HypothesisStep hypothesisStep)
    {

        hypothesisStep.setRef(LogHyp.BuildTempLogHypObject(
            proofWorksheet.getMaxSeq() + hypIndexNbr + 1,
            hypothesisStep.getRefLabel(), hypothesisStep.getFormula(),
            proofWorksheet.getComboFrame().hypArray,
            hypothesisStep.formulaParseTree));
    }

    private boolean checkDerivStepProofUsingVerify() {
        final VerifyException errmsg = verifyProofs.verifyDerivStepProof(
            proofWorksheet.getTheoremLabel() + PaConstants.DOT_STEP_CAPTION
                + derivStep.getStep(),
            derivStep.getFormula(), derivStep.getProofTree(),
            proofWorksheet.getComboFrame());

        if (errmsg != null) {

            messages.accumException(errmsg);

            proofWorksheet.getProofCursor().setCursorAtProofWorkStmt(derivStep,
                PaConstants.FIELD_ID_REF);
            return false;
        }

        return true;
    }

    private static interface UnifyResult {
        boolean possible();
        boolean proper();
        AutoUnifyResultDetails details();
    }

    /** The successful proper unification result */
    private static UnifyResult okUnification = new UnifyResult() {
        public boolean proper() {
            return true;
        }
        public boolean possible() {
            return true;
        }
        public AutoUnifyResultDetails details() {
            return null;
        }
    };

    /** The unsuccessful, impossible unification result */
    private static UnifyResult badUnification = new UnifyResult() {
        public boolean proper() {
            return false;
        }
        public boolean possible() {
            return false;
        }
        public AutoUnifyResultDetails details() {
            return null;
        }
    };

    /**
     * Attempt to unify -- provide consistent set of variable substitutions -- a
     * derivation proof step with an assertion.
     * <p>
     * Note that the assertion here is merely a "candidate" for unification,
     * which if successfully unified with the step will become the Ref field on
     * the proof step line (and, of course, will determine the proof tree for
     * this step.)
     *
     * @return the result unification status
     * @throws VerifyException if an error occurs
     */
    private UnifyResult unifyStepWithoutWorkVars() throws VerifyException {

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

        if (derivStep.getFormula() == null || !checkAssrtLevelMatch()
            || !checkHypLevelMatch())
            return badUnification;

        if (!derivStep.hasDeriveStepFormula())
            if (derivStep.formulaParseTree.getMaxDepth() > 0 && assrtParseTree
                .getMaxDepth() > derivStep.formulaParseTree.getMaxDepth())
                return badUnification;

        if (!derivStep.hasDeriveStepHyps()
            && derivStep.getLogHypsMaxDepth() > 0)
            if (assrt.getLogHypsMaxDepth() > derivStep.getLogHypsMaxDepth())
                return badUnification;

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
        if (derivStep.hasDeriveStepFormula())
            assrtFormulaSubst = new ParseNode[assrtVarHypArray.length];
        else {
            if (!assrt.getFormula().preunificationCheck(derivStep.getFormula()))
                return badUnification;

            assrtFormulaSubst = assrtParseTree.getRoot().unifyWithSubtree(
                derivStep.formulaParseTree.getRoot(), assrtVarHypArray,
                unifyNodeStack, compareNodeStack);
            if (assrtFormulaSubst == null)
                return badUnification;
        }

        assrtHypArray = assrt.getMandFrame().hypArray;
        assrtSubst = initLoadAssrtSubst();

        assrtLogHypArray = assrt.getLogHypArray();
        derivStepHypArray = derivStep.getHypList();

        // In case of autocomplete step we should perform specified unification
        if (derivStep.getRef() == null && derivStep.isAutoStep())
            return autocompleteUnifyWithoutWorkVars();

        if (assrtLogHypArray.length == 0) {
            markStepUnified(false, // usedUnifyWithWorkVars,
                false, // no "swap",
                null); // no rearrange
            return okUnification; // Special Case 0
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
                    return okUnification;
                }
                else
                    continue;
            if (assrtLogHypArray.length == 1)
                return badUnification; // Special Case 1
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
                    return okUnification;
                }
            return badUnification;
        }

        // The last (general) case is here:
        if (unifyHypsWithoutWorkVarsGeneralCase(assrtLogHypSubstArray))
            return okUnification;
        else
            return badUnification;
    }

    private boolean checkAssrtLevelMatch() {

        if (derivStep.workVarList != null)
            return true;

        assrtParseTree = assrt.getExprParseTree();

        if (!derivStep.hasDeriveStepFormula()) {
            final String assrtLevelOneTwo = assrtParseTree.getLevelOneTwo();
            if (assrtLevelOneTwo.length() > 0) {
                if (!assrtLevelOneTwo
                    .equals(derivStep.formulaParseTree.getLevelOneTwo()))
                    return false; // unification impossible!
            }
            else { // this checks LevelOne
                final Stmt assrtParseRootStmt = assrtParseTree.getRoot().stmt;

                if (assrtParseRootStmt != derivStep.formulaParseTree
                    .getRoot().stmt && !(assrtParseRootStmt instanceof VarHyp))
                    return false; // unification impossible!
            }
        }
        return true;
    }

    private boolean checkHypLevelMatch() {

        if (!derivStep.hasDeriveStepHyps()) {
            final String assrtLogHypsL1HiLoKey = assrt.getLogHypsL1HiLoKey();
            // note: derivStep L1HiLo may be "" if its hyps are derived!
            // this step's L1HiLo is not recomputed after
            // its log hyps' formulas are derived in
            // ProofUnifier -- that will slow things down
            // some (unless recomputation is triggered by
            // "Derive" formula.)
            if (assrtLogHypsL1HiLoKey.length() > 0
                && derivStep.getLogHypsL1HiLoKey().length() > 0 // see note
                && !assrtLogHypsL1HiLoKey
                    .equals(derivStep.getLogHypsL1HiLoKey()))
                return false;
        }
        return true;
    }

    /**
     * This class contains data used in unifyHypsWithoutWorkVarsGeneralCase()
     * algorithm
     */
    private static class UnificationDataForGeneralCase {
        /*
         * substAnswer array contains the
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
        final ParseNode[][][] substAnswer;

        // complexity to keep track of substAnswer :)
        final int[] impossibleCnt;

        final int[] derivAssrtXRef;
        final boolean[] assrtHypUsed;

        public UnificationDataForGeneralCase(final int length) {
            substAnswer = new ParseNode[length][][];
            for (int i = 0; i < length; i++)
                substAnswer[i] = new ParseNode[length][];

            impossibleCnt = new int[length];

            assrtHypUsed = new boolean[length];

            derivAssrtXRef = new int[length];
            for (int i = 0; i < length; i++)
                derivAssrtXRef[i] = -1;

        }
    }

    /**
     * General case of hypothesis unification
     *
     * @param assrtLogHypSubstArray a temporary-use array with some unification
     *            results
     * @return true if unification was successful
     */
    public boolean unifyHypsWithoutWorkVarsGeneralCase(
        final ParseNode[][] assrtLogHypSubstArray)
    {
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

        final UnificationDataForGeneralCase data = new UnificationDataForGeneralCase(
            assrtLogHypArray.length);
        final ParseNode[][][] substAnswer = data.substAnswer;

        final int[] impossibleCnt = data.impossibleCnt;
        final int[] derivAssrtXRef = data.derivAssrtXRef;

        /*
         * SEVEN: go baby.
         */

        // not sure if worth it...but...
        salvagePreliminaryAnswers(substAnswer, assrtLogHypSubstArray);

        // Now, for last time, re-init assrtSubt!
        assrtSubst = initLoadAssrtSubst();

        int currLevel = 0;
        while (true) {
            if (!findNextUnifiedAssrtHyp(data, currLevel)) {
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

    private boolean findNextUnifiedAssrtHyp(
        final UnificationDataForGeneralCase data, final int currLevel)
    {
        final ParseNode[][][] substAnswer = data.substAnswer;
        final int[] impossibleCnt = data.impossibleCnt;
        final boolean[] assrtHypUsed = data.assrtHypUsed;
        final int[] derivAssrtXRef = data.derivAssrtXRef;

        final ParseNode[][] currLevelSubstAnswer = substAnswer[currLevel];

        int nextAssrtHypIndex = derivAssrtXRef[currLevel];
        if (nextAssrtHypIndex != -1) {
            assrtHypUsed[nextAssrtHypIndex] = false;
            derivAssrtXRef[currLevel] = -1;
        }

        int governorLimit = assrtLogHypArray.length;
        while (governorLimit-- > 0) {
            nextAssrtHypIndex++;
            if (nextAssrtHypIndex >= assrtLogHypArray.length)
                return false;
            if (assrtHypUsed[nextAssrtHypIndex])
                continue;
            if (currLevelSubstAnswer[nextAssrtHypIndex] == substAnswerImpossible)
                continue;
            if (derivStepHypArray[currLevel] == null
                && derivStep.hasDeriveStepHyps())
            {
                // skip following heuristic
            }
            else {
                final int maxDepth = derivStepHypArray[currLevel].formulaParseTree
                    .getMaxDepth();
                if (maxDepth > 0 && assrtLogHypArray[nextAssrtHypIndex]
                    .getExprParseTree().getMaxDepth() > maxDepth)
                {
                    currLevelSubstAnswer[nextAssrtHypIndex] = substAnswerImpossible;
                    impossibleCnt[currLevel]++;
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
                    impossibleCnt[currLevel]++;
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

    /** The information about possible auto unification */
    private static class AutoUnifyResultDetails {
        final ProofStepStmt[] hypSortDerivArray;
        final ParseNode[] assrtSubst;
        final Assrt assertion;
        final List<DjVars> softDjVarsErrorList;

        AutoUnifyResultDetails(final ParseNode[] assrtSubst,
            final ProofStepStmt[] hypSortDerivArray, final Assrt assertion,
            final List<DjVars> softDjVarsErrorList)
        {
            this.assrtSubst = Arrays.copyOf(assrtSubst, assrtSubst.length);

            this.hypSortDerivArray = Arrays.copyOf(hypSortDerivArray,
                hypSortDerivArray.length);
            this.assertion = assertion;
            this.softDjVarsErrorList = new ArrayList<>(softDjVarsErrorList);
        }

    }

    /** The container for information about possible auto unification */
    private static class AutoUnifyPossibleResult implements UnifyResult {
        final AutoUnifyResultDetails details;

        public AutoUnifyPossibleResult(final ParseNode[] assrtSubst,
            final ProofStepStmt[] hypSortDerivArray, final Assrt assertion,
            final List<DjVars> softDjVarsErrorList)
        {
            details = new AutoUnifyResultDetails(assrtSubst, hypSortDerivArray,
                assertion, softDjVarsErrorList);
        }
        public boolean possible() {
            return true;
        }
        public boolean proper() {
            return false;
        }
        public AutoUnifyResultDetails details() {
            return details;
        }

        @Override
        public String toString() {
            return details.assertion.toString();
        }
    }

    /**
     * Returns the best auto unification result. No the best unification is an
     * unification with minimal dj variable restrictions.
     *
     * @param r1 the first unification result
     * @param r2 the second unification result
     * @return the best one
     */
    private static UnifyResult bestAutoResult(final UnifyResult r1,
        final UnifyResult r2)
    {
        if (r1 == null)
            return r2;
        if (r2 == null)
            return r1;
        final AutoUnifyResultDetails d1 = r1.details();
        assert d1 != null;
        final AutoUnifyResultDetails d2 = r1.details();
        assert d2 != null;
        if (d1.softDjVarsErrorList.size() <= d2.softDjVarsErrorList.size())
            return r1;
        else
            return r2;
    }

    /**
     * Simple recursive algorithm.
     * <p>
     * Possible future optimizations:
     * <ol>
     * <li>Cache substitution results
     * </ol>
     *
     * @param assrtLogHypSubstArray the temporary-use array to hold results of
     *            substitutions.
     * @param hypSortDerivArray the result array with used hypotheses
     * @param i the hypothesis number
     * @return unification result
     */
    private UnifyResult recursiveAutocomplete(
        final ParseNode[][] assrtLogHypSubstArray,
        final ProofStepStmt[] hypSortDerivArray, final int i)
    {
        if (i >= assrtLogHypArray.length) {
            // Great, we successfully substituted all logical hypotheses.
            // Now we could check dj variable restrictions.

            final VerifyException msg = checkDerivStepUnifyAgainstDjVars(
                derivStep, assrt, assrtSubst);

            if (msg == null && holdSoftDjVarsErrorList.isEmpty())
                return okUnification;
            else if (!holdSoftDjVarsErrorList.isEmpty())
                return new AutoUnifyPossibleResult(assrtSubst,
                    hypSortDerivArray, assrt, holdSoftDjVarsErrorList);
            else
                return badUnification;
        }

        // the best (with minimal dj variables) unification result
        UnifyResult best = null;
        for (final ProofWorkStmt proofWorkStmtObject : proofWorksheet
            .getProofWorkStmtList())
        {

            if (proofWorkStmtObject == derivStep)
                break;

            if (!(proofWorkStmtObject instanceof ProofStepStmt))
                continue;

            final ProofStepStmt candidate = (ProofStepStmt)proofWorkStmtObject;

            if (candidate.formulaParseTree == null)
                continue;

            final boolean ok = unifyAndMergeSubst(assrtLogHypSubstArray, i,
                candidate, i);
            if (ok) {
                hypSortDerivArray[i] = candidate;

                // recursively check remains hypotheses
                final UnifyResult res = recursiveAutocomplete(
                    assrtLogHypSubstArray, hypSortDerivArray, i + 1);

                if (res.proper())
                    return res;
                else {
                    if (res.possible())
                        // we found some possible unification but better try
                        // another one.
                        // If will found nothing better then use the best one.
                        best = bestAutoResult(best, res);

                    // clear the data for this substitution
                    hypSortDerivArray[i] = null;
                    cleanupOneAssrtSubstLevel(i);
                }
            }
        }

        if (best == null)
            return badUnification;
        else
            return best;
    }
    private UnifyResult autocompleteUnifyWithoutWorkVars()
        throws VerifyException
    {
        // get sorted logical hypotheses array
        assrtLogHypArray = assrt.getSortedLogHypArray();
        // it is not necessary but lets check that this array is used nowhere in
        // this function
        derivStepHypArray = null;

        final ParseNode[][] assrtLogHypSubstArray = new ParseNode[assrtLogHypArray.length][];
        final ProofStepStmt[] hypSortDerivArray = new ProofStepStmt[assrtLogHypArray.length];

        // Now we are using simple recursive algorithm because the speed is good
        // enough
        final UnifyResult res = recursiveAutocomplete(assrtLogHypSubstArray,
            hypSortDerivArray, 0);

        // If the unification is possible then it could be proper (all is ok) or
        // possible but we will look for better!

        if (res.possible())
            if (res.proper()) {
                // all is ok, so unify it here
                markAutoStepUnified(hypSortDerivArray);
                return okUnification;
            }
            else {
                assert res.possible();
                final AutoUnifyResultDetails autoRes = res.details();
                assert autoRes != null;
                return res;
            }
        return badUnification;
    }

    private void markAutoStepUnified(final ProofStepStmt[] hypSortDerivArray) {
        // rearrange it, because we used sorted logical hypothesis array
        final ProofStepStmt[] hypDerivArray = new ProofStepStmt[assrt
            .getLogHypArrayLength()];
        final int[] rearrange = assrt.getReversePermutationForSortedHyp();
        for (int i = 0; i < hypDerivArray.length; i++)
            hypDerivArray[i] = hypSortDerivArray[rearrange[i]];

        final String[] hypStep = new String[hypDerivArray.length];
        for (int i = 0; i < hypStep.length; i++)
            hypStep[i] = hypDerivArray[i].getStep();

        derivStep.setHypList(hypDerivArray);
        derivStep.setHypStepList(hypStep);

        derivStepHypArray = derivStep.getHypList();

        markStepUnified(false, false, null);
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
     * @param substAnswer the new container for answers
     * @param assrtLogHypSubstArray the user's old hyp sequence
     */
    private void salvagePreliminaryAnswers(final ParseNode[][][] substAnswer,
        final ParseNode[][] assrtLogHypSubstArray)
    {

        LogHyp holdLogHyp;
        ProofStepStmt holdStep;

        final LogHyp[] origSeqLogHypArray = assrt.getLogHypArray();
        final ProofStepStmt[] origSeqStepArray = derivStep.getHypList();

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

    /**
     * This function attempts to substitute assert logical hypothesis
     * (assrtLogHypArray[assrtLogHypIndex]) with derivation step logical
     * hypothesis (derivStepHypArray[stepLogHypIndex]). The function uses
     * assrtSubst through mergeLogHypSubst() function.
     *
     * @param assrtLogHypSubstArray the temporary-use array to hold results of
     *            substitutions.
     * @param assrtLogHypIndex the index in assrtLogHypArray
     * @param stepLogHypIndex the index in derivStepHypArray
     * @return true if unification was successful
     */
    private boolean unifyAndMergeSubst(
        final ParseNode[][] assrtLogHypSubstArray, final int assrtLogHypIndex,
        final int stepLogHypIndex)
    {

        final ProofStepStmt derivHyp = derivStepHypArray[stepLogHypIndex];

        return unifyAndMergeSubst(assrtLogHypSubstArray, assrtLogHypIndex,
            derivHyp, stepLogHypIndex);
    }

    /**
     * This function attempts to substitute assert logical hypothesis
     * (assrtLogHypArray[assrtLogHypIndex]) with derivation step logical
     * hypothesis (derivStepHypArray[stepLogHypIndex]). The function uses
     * assrtSubst through mergeLogHypSubst() function.
     *
     * @param assrtLogHypSubstArray the temporary-use array to hold results of
     *            substitutions.
     * @param assrtLogHypIndex the index in assrtLogHypArray
     * @param derivHyp the derivation hypothesis
     * @param cleanupIndex the current level hypothesis substitution. In simple
     *            cases it could be the current hypothesis index.
     * @return true if unification was successful
     */
    private boolean unifyAndMergeSubst(
        final ParseNode[][] assrtLogHypSubstArray, final int assrtLogHypIndex,
        final ProofStepStmt derivHyp, final int cleanupIndex)
    {

        final VarHyp[] assrtLogHypVarHypArray = assrtLogHypArray[assrtLogHypIndex]
            .getMandVarHypArray();

        if (derivHyp == null)
            assrtLogHypSubstArray[assrtLogHypIndex] = new ParseNode[assrtLogHypVarHypArray.length];
        else {
            if (!assrtLogHypArray[assrtLogHypIndex].getFormula()
                .preunificationCheck(derivHyp.getFormula()))
                return false;

            assrtLogHypSubstArray[assrtLogHypIndex] = assrtLogHypArray[assrtLogHypIndex]
                .getExprParseTree().getRoot()
                .unifyWithSubtree(derivHyp.formulaParseTree.getRoot(),
                    assrtLogHypVarHypArray, unifyNodeStack, compareNodeStack);
        }

        return assrtLogHypSubstArray[assrtLogHypIndex] != null
            && mergeLogHypSubst(cleanupIndex, assrtLogHypVarHypArray,
                assrtLogHypSubstArray[assrtLogHypIndex]);
    }

    /**
     * @param cleanupIndex the current level hypothesis substitution. In simple
     *            cases it could be the current hypothesis index.
     * @param assrtLogHypVarHypArray the array of variable hypothesis
     * @param assrtLogHypSubst the array of substitutions corresponding to
     *            assrtLogHypVarHypArray
     * @return true the unification has no conflicts with previous unifications
     */
    private boolean mergeLogHypSubst(final int cleanupIndex,
        final VarHyp[] assrtLogHypVarHypArray,
        final ParseNode[] assrtLogHypSubst)
    {

        levelCleanupCnt[cleanupIndex] = 0;
        levelCleanup[cleanupIndex] = new int[assrtHypArray.length];

        int hypIndex = 0;
        int substIndex = 0;

        substLoop: while (substIndex < assrtLogHypVarHypArray.length) {
            while (hypIndex < assrtHypArray.length) {
                if (assrtHypArray[hypIndex] != assrtLogHypVarHypArray[substIndex]) {
                    hypIndex++;
                    continue;
                }

                // a null unification
                if (assrtLogHypSubst[substIndex] == null
                    && derivStep.hasDeriveStepHyps())
                {
                    hypIndex++;
                    substIndex++;
                    continue substLoop;
                }

                if (assrtSubst[hypIndex] == null) {

                    assrtSubst[hypIndex] = assrtLogHypSubst[substIndex];
                    levelCleanup[cleanupIndex][levelCleanupCnt[cleanupIndex]++] = hypIndex;
                    hypIndex++;
                    substIndex++;
                    continue substLoop;
                }

                if (assrtSubst[hypIndex].isDeepDup(assrtLogHypSubst[substIndex],
                    compareNodeStack))
                {
                    hypIndex++;
                    substIndex++;
                    continue substLoop;
                }
                cleanupOneAssrtSubstLevel(cleanupIndex);

                return false;
            }
            throw new IllegalArgumentException(
                getException(PaConstants.ERRMSG_MERGE_LOGHYP_SUBST_ERR));
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
            while (hypIndex < assrtHypArray.length) {
                if (assrtVarHypArray[substIndex] != assrtHypArray[hypIndex]) {
                    hypIndex++;
                    continue;
                }
                outSubst[hypIndex++] = assrtFormulaSubst[substIndex++];
                continue substLoop;
            }
            throw new IllegalArgumentException(
                getException(PaConstants.ERRMSG_INIT_FORMULA_SUBST_ERR));
        }

        return outSubst;
    }

    private void markStepUnified(final boolean usedUnifyWithWorkVars,
        final boolean swapHyps, final int[] rearrangeDerivAssrtXRef)
    {

        VerifyException djMsg = null;

        // if not first unification for this step
        if (derivStep.unificationStatus.proper
            || derivStep.unificationStatus == UnificationStatus.UnifiedWIncompleteHyps)
        {

            // yes, ok to check/recheck Dj Vars if Work Vars involved...
//          if (derivStep.hasWorkVarsInStepOrItsHyps()) {
//              return;
//          }

            // ref was entered and now we're seeing the
            // original ref again on the pass through the
            // assertions, so bypass it to save time...
            if (assrt == derivStep.getRef())
                return;

            djMsg = checkDerivStepUnifyAgainstDjVars(derivStep, assrt,
                assrtSubst);

            if (derivStep.djVarsErrorStatus == DjVarsErrorStatus.None) {
                addToAlternatesList(assrt);
                return;
            }

            if (derivStep.djVarsErrorStatus == DjVarsErrorStatus.Hard) {
                if (djMsg == null) {
                    derivStep.heldDjErrorMessage = null;
                    if (holdSoftDjVarsErrorList == null
                        || holdSoftDjVarsErrorList.isEmpty())
                        derivStep.djVarsErrorStatus = DjVarsErrorStatus.None;
                    else {
                        derivStep.djVarsErrorStatus = DjVarsErrorStatus.Soft;
                        if (proofAsstPreferences.djVarsSoftErrors
                            .get() == DjVarsSoftErrors.Report)
                            derivStep.buildSoftDjErrorMessage(
                                holdSoftDjVarsErrorList);
                    }
                    addToAlternatesList((Assrt)derivStep.getRef());
                    rearrangeHyps(swapHyps, rearrangeDerivAssrtXRef);
                    saveOtherDerivStepResults();
                }
                else
                    addToAlternatesList(assrt);
                return;
            }
            else { // ok, has DJ_VARS_ERROR_STATUS_SOFT_ERRORS
                if (djMsg == null && (holdSoftDjVarsErrorList == null
                    || holdSoftDjVarsErrorList.isEmpty()))
                {

                    derivStep.djVarsErrorStatus = DjVarsErrorStatus.None;

                    derivStep.heldDjErrorMessage = null;
                    addToAlternatesList((Assrt)derivStep.getRef());
                    rearrangeHyps(swapHyps, rearrangeDerivAssrtXRef);
                    saveOtherDerivStepResults();
                }
                else
                    addToAlternatesList(assrt);
                return;
            }
        }

        // OK! First unification for this step
        derivStep.unificationStatus = UnificationStatus.Unified;

        rearrangeHyps(swapHyps, rearrangeDerivAssrtXRef);

        if (derivStep.isAutoStep() && postUnifyHook != null)
            postUnifyHook.process(derivStep, assrt, assrtSubst);

        if (usedUnifyWithWorkVars) {

            if (derivStep.hasDeriveStepFormula())
                generateDerivStepFormula();

            if (derivStep.hasDeriveStepHyps()) {
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
            derivStep.unificationStatus = UnificationStatus.UnifiedWWorkVars;
        for (int i = 0; i < derivStep.getHypNumber(); i++) {
            if (!(derivStep.getHyp(i) instanceof DerivationStep))
                continue;
            final DerivationStep dH = (DerivationStep)derivStep.getHyp(i);

            if (dH.workVarList != null
                || dH.unificationStatus == UnificationStatus.UnifiedWWorkVars)
                derivStep.unificationStatus = UnificationStatus.UnifiedWWorkVars;

            if (dH.isHypFldIncomplete()) {
                messages.accumException(
                    getException(PaConstants.ERRMSG_INCOMPLETE_HYPS,
                        derivStep.getRefLabel()));

                derivStep.unificationStatus = UnificationStatus.UnifiedWIncompleteHyps;
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
    private void doInitialStepDjEdits(final DerivationStep d, final Assrt assrt,
        final ParseNode[] assrtSubst)
    {

        final VerifyException djMsg = checkDerivStepUnifyAgainstDjVars(d, assrt,
            assrtSubst);
        if (djMsg == null) {
            d.heldDjErrorMessage = null;
            if (holdSoftDjVarsErrorList == null
                || holdSoftDjVarsErrorList.isEmpty())
                d.djVarsErrorStatus = DjVarsErrorStatus.None;
            else {
                d.djVarsErrorStatus = DjVarsErrorStatus.Soft;

                if (proofAsstPreferences.djVarsSoftErrors
                    .get() == DjVarsSoftErrors.Report)
                    d.buildSoftDjErrorMessage(holdSoftDjVarsErrorList);
            }
        }
        else { // HARD dj error on first unification for this step

            d.djVarsErrorStatus = DjVarsErrorStatus.Hard;

            d.heldDjErrorMessage = djMsg;
        }
    }

    private void addToAlternatesList(final Assrt a) {
        if (derivStep.alternateRefList == null)
            derivStep.alternateRefList = new ArrayList<>();
        derivStep.alternateRefList.add(a);
    }

    private void saveOtherDerivStepRefStuff(final DerivationStep d,
        final Assrt assrt, final ParseNode[] assrtSubst)
    {
        // if we resolved autocomplete step then convert it to ordinary step!
        d.setAutoStep(false);

        d.setAssrtSubstList(assrtSubst);
        d.setRef(assrt);
        d.setRefLabel(assrt.getLabel());
        d.reloadStepHypRefInStmtText();
    }

    private void saveOtherDjVarsEditResults(final DerivationStep d) {

        if (holdSoftDjVarsErrorList == null
            || holdSoftDjVarsErrorList.isEmpty())
            d.softDjVarsErrorList = null;
        else
            d.softDjVarsErrorList = new ArrayList<>(holdSoftDjVarsErrorList);
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
                        && derivStep.hasDeriveStepHyps())
                        newHypStep[j] = PaConstants.DEFAULT_STMT_LABEL;
                    else
                        newHypStep[j] = newDerivStepHypArray[j].getStep();
                    continue xrefLoop;
                }
            throw new IllegalArgumentException(
                getException(PaConstants.ERRMSG_REARRANGE_HYPS_ERR));
        }
        derivStep.setHypList(newDerivStepHypArray);
        derivStep.setHypStepList(newHypStep);

        // tidy up just in case (no bogus references left around...)
        derivStepHypArray = derivStep.getHypList(); // redundant? maybe
    }

    private VerifyException checkDerivStepUnifyAgainstDjVars(
        final DerivationStep d, final Assrt checkUnificationRef,
        final ParseNode[] checkUnificationAssrtSubst)
    {

        if (holdSoftDjVarsErrorList != null)
            holdSoftDjVarsErrorList.clear();

        if (checkUnificationRef.getMandFrame().djVarsArray.length == 0)
            return null; // success, no error message

        final VerifyException errmsg = verifyProofs.verifyDerivStepDjVars(
            d.getStep(),
            proofWorksheet.getTheoremLabel() + PaConstants.DOT_STEP_CAPTION
                + derivStep.getStep(),
            checkUnificationRef, checkUnificationAssrtSubst,
            proofWorksheet.getComboFrame(),
            proofAsstPreferences.djVarsSoftErrors.get(),
            holdSoftDjVarsErrorList);

        if (errmsg == null)
            return null;

        return addStepContext(errmsg);
    }

    private void markRefUnificationFailure(final Assrt assrt) {

        derivStep.unificationStatus = UnificationStatus.UnificationError;

        messages.accumException(getException(PaConstants.ERRMSG_REF_UNIFY_ERR,
            derivStep.getRefLabel()));

        proofWorksheet.getProofCursor().setCursorAtProofWorkStmt(derivStep,
            PaConstants.FIELD_ID_REF);
    }

    public void reportUnificationFailures() {
        final Set<DerivationStep> accessibleSteps = new HashSet<>();
        addAccessibleSteps(accessibleSteps, proofWorksheet.qedStep);

        for (final ProofWorkStmt s : proofWorksheet.proofWorkStmtList)
            if (accessibleSteps.contains(s)
                && (derivStep = (DerivationStep)s).getRef() == null)
                messages.accumException(
                    getException(PaConstants.ERRMSG_STEP_UNIFY_ERR));
    }

    private void addAccessibleSteps(final Set<DerivationStep> accessibleSteps,
        final DerivationStep step)
    {
        if (accessibleSteps.contains(step))
            return;

        accessibleSteps.add(step);
        for (final ProofStepStmt hyp : step.getHypList())
            if (hyp instanceof DerivationStep)
                addAccessibleSteps(accessibleSteps, (DerivationStep)hyp);
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
            workVarList = new ArrayList<>(3); // 3=guess

            generatedFormulaParseTree = logHypParseTree
                .deepCloneApplyingAssrtSubst(assrtHypArray, assrtSubst,
                    workVarList);

            // abend diagnostic, leave blank for now.
            generatedFormula = verifyProofs.convertRPNToFormula(
                generatedFormulaParseTree.convertToRPN(), " ");

            // oops, almost forgot this.
            generatedFormula.setTyp(provableLogicStmtTyp);

            foundMatchingFormulaStep = proofWorksheet
                .findMatchingStepFormula(generatedFormula, derivStep);

            if (foundMatchingFormulaStep != null) {
                derivStep.setHyp(i, foundMatchingFormulaStep);
                derivStep.setHypStep(i, foundMatchingFormulaStep.getStep());
                derivStep.getHyp(i).loadProofLevel(derivStep.proofLevel + 1);
                continue;
            }

            generatedDerivStep = proofWorksheet.addDerivStepForDeriveFeature(
                workVarList, generatedFormula, generatedFormulaParseTree,
                derivStep);

            derivStep.setHyp(i, generatedDerivStep);
            derivStep.setHypStep(i, generatedDerivStep.getStep());
            derivStep.getHyp(i).loadProofLevel(derivStep.proofLevel + 1);

            derivStep.nbrHypsGenerated++;
        }
    }

    private void generateDerivStepFormula() {

        final ParseTree origParseTree = assrt.getExprParseTree();

        // keeps count of dummies in new formula
        final List<WorkVar> workVarList = new ArrayList<>(3); // 3 is a
        // guess.

        final ParseTree generatedFormulaParseTree = origParseTree
            .deepCloneApplyingAssrtSubst(assrtHypArray, assrtSubst,
                workVarList);

        final Formula generatedFormula = verifyProofs.convertRPNToFormula(
            generatedFormulaParseTree.convertToRPN(),
            PaConstants.DOT_STEP_CAPTION + derivStep.getStep());

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
            && proofWorksheet.stepRequest.type.simple)
            selectorSearchStmt = (DerivationStep)proofWorksheet.stepRequest.param1;

        stepLoop: while (true) {

            wIndex = wIndex + 1 + wIndexInsertedCnt;
            if (wIndex >= proofWorksheet.proofWorkStmtList.size())
                break;
            wIndexInsertedCnt = 0;

            proofWorkStmtObject = proofWorksheet.proofWorkStmtList.get(wIndex);
            if (!(proofWorkStmtObject instanceof DerivationStep))
                continue;

            derivStep = (DerivationStep)proofWorkStmtObject;

            if (selectorSearchStmt == derivStep) {
                if (proofWorksheet.stepRequest.type == StepRequestType.SelectorSearch)
                    proofWorksheet.stepSelectorResults = stepSelectorSearch
                        .loadStepSelectorResults(derivStep);
                else if (proofWorksheet.stepRequest.type == StepRequestType.StepSearch)
                    proofWorksheet.searchOutput = proofAsstPreferences
                        .getSearchMgr().execStepSearch(derivStep);
                return; // our work here is complete ;-)
            }

            if (derivStep.getRef() == null || !derivStep.hasDeriveStepFormula()
                && !derivStep.hasDeriveStepHyps()
                && !derivStep.hasWorkVarsInStepOrItsHyps())
                continue;

            assrt = (Assrt)derivStep.getRef();
            assrtNbrLogHyps = assrt.getLogHypArrayLength();

            if (derivStep.getHypNumber() != assrtNbrLogHyps)
                throw new IllegalArgumentException(
                    getException(PaConstants.ERRMSG_STEP_REF_HYP_NBR_ERR));

            for (int i = 0; i < derivStep.getHypNumber(); i++)
                // this is BS -- but a hyp[i]'s deriveStepFormula
                // could have errored out...
                if (derivStep.getHyp(i) != null
                    && derivStep.getHyp(i).formulaParseTree == null)
                {
                    // this will just be bypassed and considered incomplete
                    derivStep.unificationStatus = UnificationStatus.AttemptCancelled;
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

        if (derivStep.getHypNumber() == 0) {
            assrtSubst = stepUnifier.finalizeAndLoadAssrtSubst();
            markStepUnified(true, // usedUnifyWithWorkVars,
                false, // no "swap",
                null); // no rearrange
            return true;
        }

        derivStepHypArray = derivStep.getHypList();
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

            final int[] derivAssrtXRef = stepUnifier.getDerivAssrtXRef();

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
        return stepUnifier.unifyAndMergeStepFormula(/* commit = */true, assrt,
            stepRoot);

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
     * still exist after the update. - if derivation step marked "unified but
     * incomplete" or "unified" and the workVarList is emptied by the updates,
     * reset the unification status to "not unified" to trigger a re-unification
     * process (inefficient and ugly, but..) - clone-copy the parse tree to
     * reflect the Work Var updates - update assrtSubst, if not null - reformat
     * the formula text using TMFF and the clone-copied parse tree, updating the
     * heuristics fields too.)
     *
     * @param currentDerivStep the current DerivationStep
     */
    private void doUpdateWorksheetWorkVars(
        final DerivationStep currentDerivStep)
    {

        for (final ProofWorkStmt proofWorkStmtObject : proofWorksheet
            .getProofWorkStmtList())
        {
            if (!(proofWorkStmtObject instanceof DerivationStep))
                continue;

            final DerivationStep d = (DerivationStep)proofWorkStmtObject;

            if (d.workVarList != null)
                doUpdateDerivationStepWorkVars(d);

            if (d == currentDerivStep)
                continue; // skip the rest of this stuff...

            doUpdateDerivationStepAssrtSubst(d);

            final UnificationStatus saveUnificationStatus = d.unificationStatus;
            doUpdateWorkVarUnificationStatus(d);

            if (saveUnificationStatus != d.unificationStatus
                && d.unificationStatus == UnificationStatus.Unified)
            {
                // must redo DjVars edits!!!
                doInitialStepDjEdits(d, (Assrt)d.getRef(),
                    d.getAssrtSubstList());
                saveOtherDjVarsEditResults(d);
            }
        }
    }

    private void doUpdateDerivationStepWorkVars(final DerivationStep d) {

        // ok, see if workVarList Work Vars actually updated
        List<WorkVar> newWorkVarList = new ArrayList<>(d.workVarList.size());

        final List<WorkVar> updatedWorkVarList = new ArrayList<>(
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
                throw new IllegalArgumentException(getException(
                    PaConstants.ERRMSG_UPD_WV_ASSIGNED_NULL_VALUE));
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

        final Formula newFormula = verifyProofs
            .convertRPNToFormula(newFormulaParseTree.convertToRPN(), " "); // abend
                                                                           // diagnostic,
                                                                           // leave
                                                                           // blank
                                                                           // for
                                                                           // now.

        newFormula.setTyp(provableLogicStmtTyp);

        boolean stmtTextAlreadyUpdated = false;
        if (!proofAsstPreferences.autoReformat.get())
            stmtTextAlreadyUpdated = d
                .updateStmtTextWithWorkVarUpdates(verifyProofs);

        d.updateWorkVarList(newWorkVarList);

        d.loadGeneratedFormulaIntoDerivStep(newFormula, newFormulaParseTree,
            stmtTextAlreadyUpdated);
    }

    private void doUpdateDerivationStepAssrtSubst(final DerivationStep d) {

        if (d.getAssrtSubstList() == null)
            return;

        for (int i = 0; i < d.getAssrtSubstNumber(); i++)
            // note: log hyp array entries will be null at
            // this point (prior to proof construction).
            if (d.getAssrtSubst(i) != null)
                d.setAssrtSubst(i,
                    d.getAssrtSubst(i).deepCloneApplyingWorkVarUpdates());
    }

    private void doUpdateWorkVarUnificationStatus(final DerivationStep d) {
        // ok, now adjust unification status for the revised
        // work var situation

        // this weird looking if statement means status is
        // unified but not equal to UnifiedWIncompleteHyps
        if (d.unificationStatus.proper) {
            d.unificationStatus = UnificationStatus.Unified;
            if (d.workVarList != null)
                d.unificationStatus = UnificationStatus.UnifiedWWorkVars;
            else
                for (int i = 0; i < d.getHypNumber(); i++) {
                    if (!(d.getHyp(i) instanceof DerivationStep))
                        continue;
                    if (d.getHyp(i).workVarList != null
                        || ((DerivationStep)d.getHyp(
                            i)).unificationStatus == UnificationStatus.UnifiedWWorkVars)
                        d.unificationStatus = UnificationStatus.UnifiedWWorkVars;
                }
        }
    }

    @SuppressWarnings("unchecked")
    public static void separateMandAndOptFrame(
        final ProofWorksheet proofWorksheet, final DerivationStep qedStep,
        final List<? super VarHyp> mandHypList,
        final List<VarHyp> optionalVarHypList, final boolean addLogHyps)
    {
        qedStep.formulaParseTree.getRoot()
            .accumVarHypUsedListBySeq(mandHypList);

        if (proofWorksheet.hypStepCnt > 0) {

            ProofWorkStmt proofWorkStmt;

            int hypsFound = 0;
            int stepIndex = 0;
            while (true) {

                proofWorkStmt = proofWorksheet.proofWorkStmtList.get(stepIndex);

                if (proofWorkStmt instanceof HypothesisStep) {
                    ((ProofStepStmt)proofWorkStmt).formulaParseTree.getRoot()
                        .accumVarHypUsedListBySeq(mandHypList);
                    if (++hypsFound >= proofWorksheet.hypStepCnt)
                        break;
                }

                if (++stepIndex > proofWorksheet.proofWorkStmtList.size())
                    break;
            }

            if (hypsFound != proofWorksheet.hypStepCnt)
                throw new IllegalArgumentException(new ProofAsstException(
                    PaConstants.ERRMSG_HYP_STEP_CNT_IN_WORKSHEET_ERROR));
        }

        final Hyp[] frameHypArray = proofWorksheet.comboFrame.hypArray;

        for (final Hyp element : frameHypArray)
            if (element instanceof VarHyp) {
                final VarHyp vH = (VarHyp)element;
                if (!vH.containedInVarHypListBySeq(mandHypList))
                    vH.accumVarHypListBySeq(optionalVarHypList);
            }
            else if (addLogHyps)
                ((List<? super Hyp>)mandHypList).add(element);
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
        // 1) construct mandatoryVarHypList and optionalVarHypList
        // (new theorems not in the database require these to
        // be computed, so we do the computations for both old
        // and new to "simplify".)

        final List<Hyp> mandatoryVarHypList = new ArrayList<>(
            proofWorksheet.comboFrame.hypArray.length);

        final List<VarHyp> optionalVarHypList = new ArrayList<>(
            proofWorksheet.comboFrame.hypArray.length);

        separateMandAndOptFrame(proofWorksheet, qedStep, mandatoryVarHypList,
            optionalVarHypList, false);

        // 2) initialize list of disjointWorkVarList's and
        // optionalVarHypsInUseList

        final List<List<Var>> disjointWorkVarList = new ArrayList<>();
        final List<VarHyp> optionalVarHypsInUseList = new ArrayList<>();

        // 3) make recursive pass through the qed derivation
        // subtree loading the disjointWorkVarHypList and
        // optionalVarHypsInUseList

        recursiveLoadWvAndOptsUsedLists(qedStep, optionalVarHypList,
            optionalVarHypsInUseList, disjointWorkVarList);

        // 3.5 see doc in function below
        final List<List<VarHyp>> disjointWorkVarHypList = buildDisjointWorkVarHypList(
            disjointWorkVarList);

        // 4) construct unusedOptionalVarHypTypList and
        // unusedOptionalVarHypsByTypList using
        // optionalVarHypList and optionalVarHypsInUseList
        final List<Cnst> unusedOptionalVarHypTypList = new ArrayList<>();
        final List<List<Hyp>> unusedOptionalVarHypsByTypList = new ArrayList<>();
        for (final VarHyp vH : optionalVarHypList) {
            if (vH.containedInVarHypListBySeq(optionalVarHypsInUseList))
                continue;

            // ok, optional varHyp "vH" is unused

            final Cnst typ = vH.getTyp();
            int typIndex = unusedOptionalVarHypTypList.indexOf(typ);
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
        for (final List<VarHyp> workVarHypList : disjointWorkVarHypList) {
            final Iterator<List<Hyp>> iterator = unusedOptionalVarHypsByTypList
                .iterator();
            for (final Cnst typ : unusedOptionalVarHypTypList) {
                final List<Hyp> typUnusedOptionalVarHyps = iterator.next();
                int next = 0;
                for (final VarHyp vH : workVarHypList) {
                    if (vH.getTyp() != typ)
                        continue;
                    if (next >= typUnusedOptionalVarHyps.size())
                        throw new VerifyException(
                            PaConstants.ERRMSG_WV_CLEANUP_SHORTAGE,
                            proofWorksheet.getTheorem().getLabel(), typ,
                            typUnusedOptionalVarHyps.size());

                    vH.paSubst = // this is a really key step :-)
                        new ParseNode(
                            (VarHyp)typUnusedOptionalVarHyps.get(next++));
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

    private void recursiveConvertStepWorkVarHypsToDummies(
        final DerivationStep d)
    {
        boolean redoDjVarsEdits = false;
        DerivationStep dH;
        for (final ProofStepStmt element : d.getHypList())
            if (element instanceof DerivationStep) {
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

        if (d.unificationStatus == UnificationStatus.UnifiedWWorkVars)
            d.unificationStatus = UnificationStatus.Unified;

        if (redoDjVarsEdits) {
            doInitialStepDjEdits(d, (Assrt)d.getRef(), d.getAssrtSubstList());
            saveOtherDjVarsEditResults(d);
        }
    }

    // note: neither the 'qed' step, nor the theorem
    // hypotheses can contain work variables or
    // optional/dummy variables.
    private void recursiveLoadWvAndOptsUsedLists(final DerivationStep d,
        final List<VarHyp> optionalVarHypList,
        final List<VarHyp> optionalVarHypsInUseList,
        final List<List<Var>> disjointWorkVarList)
    {

        for (int i = 0; i < d.getHypNumber(); i++) {
            if (!(d.getHyp(i) instanceof DerivationStep))
                continue;
            recursiveLoadWvAndOptsUsedLists((DerivationStep)d.getHyp(i),
                optionalVarHypList, optionalVarHypsInUseList,
                disjointWorkVarList);
        }

        // accumulate optional formula var hyps into "in use" list
        if (!d.getStep().equalsIgnoreCase(PaConstants.QED_STEP_NBR))
            d.formulaParseTree.getRoot().accumListVarHypUsedListBySeq(
                optionalVarHypList, optionalVarHypsInUseList);

        // accumulate a new workVarList containing the step
        // AND its hyp work vars. (later we make the conversion
        // from WorkVar to WorkVarHyp, but not yet...)
        final List<Var> stepAndHypWorkVarList = new ArrayList<>();
        if (d.workVarList != null)
            mergeVarList1IntoList2(d.workVarList, stepAndHypWorkVarList);
        for (int i = 0; i < d.getHypNumber(); i++) {
            if (!(d.getHyp(i) instanceof DerivationStep))
                continue;
            if (((DerivationStep)d.getHyp(i)).workVarList != null)
                mergeVarList1IntoList2(
                    ((DerivationStep)d.getHyp(i)).workVarList,
                    stepAndHypWorkVarList);
        }

        if (stepAndHypWorkVarList.isEmpty())
            return;

        // if stepAndHypWorkVarList disjoint with all other lists
        // in disjointWorkVarList, add it; otherwise,
        // union it with the first list having a var in common:
        boolean isDisjoint = true;
        loopI: for (final List<Var> x : disjointWorkVarList)
            for (final Var v : stepAndHypWorkVarList)
                if (v.containedInVarListBySeq(x)) {
                    isDisjoint = false;

                    mergeVarList1IntoList2(stepAndHypWorkVarList, x);
                    break loopI;
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

        final List<List<VarHyp>> outVarHypListOfLists = new ArrayList<>(
            inVarListOfLists.size());

        final int iMax = inVarListOfLists.size();
        final int jMax = iMax;

        List<Var> candidateI;
        List<Var> candidateJ;

        loopI: for (int i = 0; i < iMax; i++) {

            candidateI = inVarListOfLists.get(i);

            for (int j = i + 1; j < jMax; j++) {

                candidateJ = inVarListOfLists.get(j);

                if (isVarList1DisjointWithList2(candidateI, candidateJ))
                    continue;

                mergeVarList1IntoList2(candidateI, candidateJ);
                continue loopI;
            }

            // convert WorkVarList to WorkVarHyp list
            // note: WorkVars are always global and "active"
            final List<VarHyp> workVarHypList = new ArrayList<>(
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

    private static void shiftEmptyElements(final DerivationStep[] array,
        final int arrayCount)
    {
        loopJ: for (int j = 0; j < arrayCount - 1; j++)
            if (array[j] == null) {
                int m = j;
                int n = j;
                while (true) {
                    if (++n >= arrayCount)
                        break loopJ;
                    if (array[n] == null)
                        continue;
                    break;
                }
                while (n < arrayCount)
                    array[m++] = array[n++];
            }
    }

    public void setTransformationManager(
        final TransformationManager trManager)
    {
        this.trManager = trManager;
    }

    public <T extends MMJException> T addStepContext(final T e) {
        return ProofWorksheet.addLabelContext(proofWorksheet, StepContext
            .addStepContext(derivStep == null ? null : derivStep.getStep(), e));
    }

    public ProofAsstException getException(final ErrorCode code,
        final Object... args)
    {
        return addStepContext(new ProofAsstException(code, args));
    }

    public interface PostUnifyHook {
        void process(final DerivationStep d, final Assrt assrt,
            final ParseNode[] assrtSubst);
    }
}
