//********************************************************************/
//* Copyright (C) 2007  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  StepUnifier.java  0.01 03/01/2008
 *
 *  Version 0.01 03/01/2008
 *               - new!
 */

package mmj.pa;
import mmj.lang.*;

/**
 *  StepUnifier implements an algorithm based on Robinson's
 *  original unification algorithm to unify two formulas.
 *  <p>
 *  It is customized for mmj2's needs as discussed in
 *  <code>mmj2/doc/StepUnifier.html</code>.
 *  <p>
 *  StepUnifier has much in common with the unification
 *  algorithm in ProofUnifier. These are the main
 *  differences:
 *  <ol>
 *     <li>It accumulates VarHyp substitution results
 *         into the new "paSubst" slot in mmj.lang.VarHyp
 *         instead of indirectly in the assrtSubst array.</li>
 *     <li>It uses Work Variables instead of Dummy Variables.</li>
 *     <li>SubstAnswer is a 2 dimensional array of UnifySubst
 *         in StepUnifier instead of a 3 dimensional array
 *         of ParseNode, as in ProofUnifier. The payload of
 *         SubstAnswer is a linked list instead of an array
 *         (for a variety of reasons...)</li>
 *     <li>The contents of SubstAnswer[i][j] are "raw",
 *         meaning that two elements of the [i][j] list
 *         may be inconsistent. This reduces the efficiency
 *         of the unification process but is an unavoidable
 *         result of the presence of Work Vars in Step Unifier --
 *         there may be multiple substitutions targeting a
 *         single Var that are different but consistent,
 *         which is not the case in Proof Unifier (only one
 *         substitution is stored however, but a chain of
 *         related substitutions may be generated during
 *         "subunification" and these results are dependent
 *         upon the previously accum'd logical hypotheses...
 *         so, to make it simple, we just store the "raw"
 *         substitutions pre-accumulation/merging.)</li>
 *     <li>The backout process for reversing partial results
 *         during processing of the sorted logical hypothesis
 *         arrays is different. One thing it has in common
 *         with ProofUnifier's backout process is that it
 *         too relies on the fact that only ONE substitution
 *         to a VarHyp is made -- so backout out an assignment
 *         does not require restoring a previous substitution,
 *         but simply null-ing it out.</li>
 *     <li>No attempt is made to salvage the work produced
 *         by unifyAndMergeHypsUnsorted() as is done in
 *         ProofUnifier when going into unifyAndMergeHypsSorted().
 *         "Salvage" would mean pre-loading substAnswer with
 *         the usable unification results from the unsorted
 *         hyp array unification attempt. This salvage op
 *         could be done, but given the way that users are
 *         expected to actually *use* work variables with
 *         the mmj2 Proof Assistant GUI screen, it is likely
 *         that no discernable performance improvement would
 *         be observed. So why do it here?
 *   </ol>
 */
public class StepUnifier {

    // Proof Step Formula assigned number -1
    // while logical hypotheses have indexes 0 -> n.
    public static int       F_LEVEL_NBR
                                  = -1;

    // "commit" means that finalizeAndLoadAssrtSubst()
    // will, if false, not update VarHyp's at finalization
    // time. This allows StepUnifier to be used to
    // generate "trial" unifications which are not actually
    // applied to a Proof Worksheet.
    private boolean         commit;

    private WorkVarManager  workVarManager;

    // "applied" is just an array of UnifySubst's
    // without regard for their internal linked-list
    // "next" pointers. It holds the UnifySubst data
    // currently applied to the VarHyp storage areas
    // and is used for any backouts of those changes.
    private UnifySubst[]    applied;
    private int             appliedCnt;
    private int[]           levelAppliedCnt;

    private int             currLevel;
    private UnifySubst      currLevelDeferred;
    private UnifySubst      currLevelDeferredLast;

    private Hyp[]           assrtHypArray;
    private LogHyp[]        assrtLogHypArray;

    private ProofStepStmt[] derivStepHypArray;

    /*
     *  T "substAnswer" array contains the
     *  results of unifyLogHypFormula()
     *  stored at these coordinates
     *
     *      row   = derivStepHypArray[i]  (1st dimension)
     *      col   = assrtLogHypArray[j]   (2nd dimension)
     *
     *  We use the following conventions:
     *
     *      substAnswer[i][j] == null means that
     *          unifyLogHypFormula() has not been attempted
     *          for the ith derivStepHypArray element
     *          with the jth assrtLogHypArray element.
     *
     *      substAnswer[i][j] == UnifySubst.IMPOSSIBLE
     *          means that unifyLogHypFormula() was attempted and
     *          failure was reported -- no unification.
     *          (Note that the individual hyp pair can be
     *          successfully unified but when those results
     *          are accum'd into the composite data,
     *          a substitution "inconsistency" is detected.)
     *
     *      substAnswer[i][j] == UnifySubst.EMPTY_LIST
     *          means that unifyLogHypFormula() was attempted
     *          no substitutions were reports (probably because
     *          of a "null" unification because a step LogHyp
     *          is missing (for Derive)).
     *
     *      substAnswer[i][j] == something else
     *          means that unifyLogHypFormula() was successful
     */
    private UnifySubst[][]  substAnswer;

    /*
     * This array cross-references the input sorted arrays
     * derivStepHypArray and assrtLogHypArray.
     *
     * derivAssrtXRef[I] = N says:
     *
     *      derivStepHypArray[I] unifies with
     *      assrtLogHypArray[N]
     *
     * in the final, completed deriv step unification.
     */
    private int[]           derivAssrtXRef;

    /*
     * these come into play when unifying sorted Log Hyps
     */
    private boolean[]       assrtHypUsed;
    private int[]           impossibleCnt;

    /**
     *  Constructor for StepUnifier.
     *
     *  @param workVarManager instance of WorkVarManager.
     */
    public StepUnifier(
                            WorkVarManager workVarManager) {
        this.workVarManager       = workVarManager;
        applied                   =
            new UnifySubst[
                PaConstants.STEP_UNIFIER_APPLIED_ARRAY_LEN_INIT];

    }

    /**
     *  Initialization for handling unification of an
     *  entire Proof Worksheet.
     *  <p>
     *  NOTE: This function is vitally important -- it
     *  must be invoked at the start of Proof Worksheet
     *  processing in ProofUnifier.java (or whatever.)
     *  Its function is to prepare the Work Variables
     *  for use in the ProofWorksheet, including
     *  initializing their values and making sure
     *  that none are still allocated from previous
     *  usages. THEREFORE --
     */
    public void startProofWorksheet() {
        workVarManager.deallocAll();
    }

    /**
     *  Resolve chained substitutions into Work Variables
     *  and load the assrtSubst array with the substitutions
     *  generated by successful Unification.
     *  <p>
     *  <code>
     *  NOTE: This is an especially important part of
     *        the zany scheme to hold Work Variable
     *        substitution values inside the actual
     *        VarHyp instances (oy...) The resolution
     *        of chained substitutions into Work Variables
     *        does two vital things:
     *
     *        1) it eliminates multiple assignments, such
     *           as A = B, B = C, C = ( D -> E ), D = null,
     *           E = null, storing  A = ( D -> E ), and
     *           B = ( D -> E ).
     *
     *        2) It *deallocates* A, B, and C!!! They get
     *           deallocated because after updating the
     *           Proof Worksheet with the Work Variable updates,
     *           A, B and C will no longer be present in the
     *           Proof Worksheet -- and!!! -- since deallocation
     *           does not erase any values, the assigned
     *           substitutions will be available during the
     *           time the Proof Worksheet is being updated...
     *           then when the next proof step is unified, A,
     *           B and C will be available for re-allocation,
     *           at which time their assigned values will be
     *           re-initialized.
     *
     *           NOTE ALSO that because
     *           A, B and C are deallocated but still have
     *           instances in use inside the Proof Worksheet,
     *           the Proof Worksheet code can interrogate
     *           work variables it has in hand, and if
     *           one is *now* marked "deallocated" then
     *           it knows, for absolute fact, that that
     *           variable is being updated and removed!
     *           So, if proof step 3 uses work var "A" and
     *           work var "A" is marked as deallocated, then
     *           the program *knows* that it must update
     *           proof step 3's formula and parse tree,
     *           and that maybe proof step 3 no longer has
     *           any work variables (or not...)
     */
    public ParseNode[] finalizeAndLoadAssrtSubst() {
        ParseNode[] assrtSubst    =
            new ParseNode[assrtHypArray.length];
        if (commit) {
            workVarManager.resolveWorkVarUpdates();
            loadAssrtSubst(assrtSubst);
        }
        else {
            loadAssrtSubst(assrtSubst);
            backoutAllHLevelApplieds();
            backoutFLevelApplieds();
        }
        return assrtSubst;
    }

    private void loadAssrtSubst(ParseNode[] assrtSubst) {
        for (int i = 0; i < assrtHypArray.length; i++) {
            if (assrtHypArray[i].isVarHyp()) {
                assrtSubst[i]     =
                    ((VarHyp)assrtHypArray[i]).paSubst;
                if (assrtSubst[i].hasUpdatedWorkVar()) {
                    assrtSubst[i] =
                        assrtSubst[i].
                            cloneResolvingUpdatedWorkVars();
                }
            }
        }
    }


    /**
     *  Returns an int array of indexes that cross references
     *  to the unifying assertion logical hypothesis index.
     *  <p>
     *  Note that derivAssrtXRef index numbers correspond to
     *  the sorted arrays of Logical Hypotheses input to
     *  unifyAndMergeHypsSorted(). For example, if
     *  derivAssrtXRef[0] = 2 that means that the first
     *  (sorted) element of derivStepHypArray unifies
     *  with the 3rd (sorted) element of assrtLogHypArray.
     *  <p>
     *  @return derivAssrtXRef array of int indexes.
     */
    public int[] getDerivAssrtXRef() {
        return derivAssrtXRef;
    }

    /**
     *  Backout Proof Step Formula applied substitutions.
     *  <p>
     *  This function would need to be used if the
     *  full sequence of calls is not performed:
     *  <ol>
     *    <li> unifyAndMergeStepFormula()  </li>
     *    <li> unifyAndMergeHypsUnsorted() </li>
     *    <li> unifyAndMergeHypsSorted()   </li>
     *  </ol>
     *  <p>
     *  If unification succeeds for (1) but
     *  (2) and (3) are not called, or, if (1)
     *  succeeds and (2) fails, but (3) is
     *  not called, then backoutFLevelApplieds()
     *  would be necessary to tidy up...
     *  <p>
     *  But, in normal ProofUnifier processing
     *  this function is not needed because it
     *  does perform the full sequence of calls.
     */
    public void backoutFLevelApplieds() {
        backoutOneLevelApplieds(F_LEVEL_NBR);
    }


    /**
     *  Initialize for start of new Proof Step unification.
     *
     *  Then unify/merge the proof step formula but not, yet, the
     *  logical hypotheses.
     *
     * @return true if the proof step formula unifies with
     *         the input assertion's formula; otherwise false.
     */
    public boolean unifyAndMergeStepFormula(
                                    boolean   commit,
                                    ParseNode assrtRoot,
                                    ParseNode stepRoot,
                                    Hyp[]     assrtHypArray,
                                    LogHyp[]  assrtLogHypArray)
                                throws VerifyException {

        // INITIALIZE/START ProofStep
        // ==========================
        this.commit               = commit;
        this.assrtHypArray        = assrtHypArray;
        this.assrtLogHypArray     = assrtLogHypArray;

        // initializeTargetVarHypPASubst
        for (int i = 0; i < assrtHypArray.length; i++) {
            if (assrtHypArray[i].isVarHyp()) {
                ((VarHyp)assrtHypArray[i]).paSubst
                                  = null;
            }
        }

        // allocateNewProofStepStuff
        appliedCnt                = 0;
        levelAppliedCnt           =
            new int[assrtLogHypArray.length + 1];

        // OK, DO IT!
        // ==========

        if (stepRoot == null) {
            allocWorkVarsForUnassignedSourceVars();
            return true;
        }

        currLevel                 = F_LEVEL_NBR;
        currLevelDeferred         = UnifySubst.EMPTY_LIST;
        currLevelDeferredLast     = null;
        if (unifyLevel(assrtRoot,
                       stepRoot)) {
            allocWorkVarsForUnassignedSourceVars();
            if (mergeCurrLevelSubst()) {
                return true;
            }
        }

        backoutCurrLevelApplieds();

        return false;
    }

    /**
     *  Unifies proof step logical hypotheses against
     *  an assertion's array of logical hypotheses.
     *  <p>
     *  The derivation step hypothesis order used ought to
     *  be the order input by the user -- only if that order
     *  is wrong should unifyAndMergeHypsSorted() be used.
     *
     *  @return array assrtSubst, a ParseNode array parallel
     *                to assrt.MandFrame.hyp.     */
    public ParseNode[] unifyAndMergeHypsUnsorted(
                                ProofStepStmt[] derivStepHypArray) {

        this.derivStepHypArray    = derivStepHypArray;
        ParseNode sourceRoot;

        for (currLevel = 0;
             currLevel < assrtLogHypArray.length;
             currLevel++) {

            if (derivStepHypArray[currLevel] == null) {
                sourceRoot        = null;
            }
            else {
                sourceRoot        =
                    derivStepHypArray[currLevel].
                        formulaParseTree.
                            getRoot();
            }

            if ((unifyLogHypFormula(
                    assrtLogHypArray[currLevel].
                        getExprParseTree().
                            getRoot(),
                    sourceRoot))
                !=
                UnifySubst.IMPOSSIBLE) {

                if (mergeCurrLevelSubst()) {
                    continue;
                }

            }

            for (int i = currLevel; i >= 0; i--) {
                backoutOneLevelApplieds(i);
            }
            return null;
        }

        return finalizeAndLoadAssrtSubst();
    }

    /**
     *  Unifies sorted proof step logical hypotheses against
     *  a sorted array of assertion logical hypotheses.
     *  <p>
     *  This function ought to be called after first
     *  attempting unifyAndMergeHypsUnsorted() because
     *  the user's hypothesis order may be significant:
     *  there can be more than one valid, consistent
     *  unification if work variables are present in
     *  a proof step and its hypotheses. Therefore,
     *  this function ought to be used as a last resort
     *  (especially since it is slow.....)
     *  <p>
     *  The sort order of the input assrtLogHypArray and
     *  derivStepHypArray is particularly important due
     *  to the potential problem of combinatorial explosion
     *  requiring n-factorial hypothesis-pair unification
     *  attempts if the sort order is precisely wrong :-)
     *  ProofUnifier uses sortAssrtLogHypArray() and
     *  sortDerivStepHypArray() to order the hypotheses
     *  in order by descending formula length -- and
     *  if two formulas have equal length, then if one
     *  has a variable in common with the derivation step's
     *  main formula, then that hypothesis is placed ahead
     *  of the other. This sort order is based on empirical
     *  observation -- and seems to work ok.
     *
     *  @return array assrtSubst, a ParseNode array parallel
     *                to assrt.MandFrame.hyp.
     */
    public ParseNode[] unifyAndMergeHypsSorted(
                                LogHyp[]        assrtLogHypArray,
                                ProofStepStmt[] derivStepHypArray) {

        this.derivStepHypArray    = derivStepHypArray;
        this.assrtLogHypArray     = assrtLogHypArray;

        substAnswer               =
            new UnifySubst[assrtLogHypArray.length][];

        for (int i = 0; i < assrtLogHypArray.length; i++) {
            substAnswer[i]        =
                new UnifySubst[assrtLogHypArray.length];
        }

        // ok! add complexity to keep track of substAnswer :)
        impossibleCnt             =
            new int[assrtLogHypArray.length];

        assrtHypUsed              =
            new boolean[assrtLogHypArray.length];

        derivAssrtXRef            =
            new int[assrtLogHypArray.length];
        for (int i = 0; i < derivAssrtXRef.length; i++) {
            derivAssrtXRef[i]     = -1;
        }

        boolean status            = false;
        currLevel                 = 0;
        while (true) {

            if (!findNextUnifiedAssrtHyp()) { // !find at this level

                if (impossibleCnt[currLevel] >=  // no satisfaction
                    assrtLogHypArray.length) {   // with any
                    break;                       // assrtLogHyp!
                }

                if (--currLevel < 0) {
                    break;                  //no prev levels to try
                }

                // backout previous level's merged substitutions
                backoutOneLevelApplieds(currLevel);
            }
            else { // ok, found one at this level --> so go deeper!

                if (++currLevel >= assrtLogHypArray.length) {
                    //success! yay!!!
                    status        = true;
                    break;
                }
            }
        }

        // tidy up :-0)
        substAnswer               = null;
        impossibleCnt             = null;
        assrtHypUsed              = null;

        if (status) {
            return finalizeAndLoadAssrtSubst();
        }
        else {
            // backout all applied subst including level "F" (-1)
            for (int i = currLevel; i >= -1; i--) {
                backoutOneLevelApplieds(i);
            }
            return null;
        }
    }

    private boolean findNextUnifiedAssrtHyp() {

        UnifySubst[] currLevelSubstAnswer
                                  = substAnswer[currLevel];

        int nextAssrtHypIndex     = derivAssrtXRef[currLevel];
        if (nextAssrtHypIndex != -1) {
            assrtHypUsed[nextAssrtHypIndex]
                                  = false;
            derivAssrtXRef[currLevel]
                                  = -1;
        }

        int governorLimit         = assrtLogHypArray.length;
        while (governorLimit-- > 0) {
            ++nextAssrtHypIndex;
            if (nextAssrtHypIndex >= assrtLogHypArray.length) {
                return false;
            }
            if (assrtHypUsed[nextAssrtHypIndex]) {
                continue;
            }
            if (currLevelSubstAnswer[nextAssrtHypIndex] ==
                UnifySubst.IMPOSSIBLE) {
                continue;
            }
            if (currLevelSubstAnswer[nextAssrtHypIndex] == null) {
                if (unifyAndMergeSubstAnswer(currLevelSubstAnswer,
                                             nextAssrtHypIndex)) {
                    derivAssrtXRef[currLevel]
                                  = nextAssrtHypIndex;
                    assrtHypUsed[nextAssrtHypIndex]
                                  = true;
                    return true;
                }
                if (currLevelSubstAnswer[nextAssrtHypIndex]
                        == UnifySubst.IMPOSSIBLE) {
                    ++impossibleCnt[currLevel];
                }
                continue;
            }

            //here we (attempt to) merge in the previously computed
            //substitutions for a LogHyp
            currLevelDeferred     =
                currLevelSubstAnswer[nextAssrtHypIndex];
            if (mergeCurrLevelSubst()) {
                derivAssrtXRef[currLevel]
                              = nextAssrtHypIndex;
                assrtHypUsed[nextAssrtHypIndex]
                              = true;
                return true;
            }
            else {
                backoutCurrLevelApplieds();
            }
        }

        return false;
    }

    private boolean unifyAndMergeSubstAnswer(
                        UnifySubst[]    currLevelSubstAnswer,
                        int             assrtLogHypIndex) {

        ParseNode sourceRoot;
        if (derivStepHypArray[currLevel] == null) {
            sourceRoot        = null;
        }
        else {
            sourceRoot        =
                derivStepHypArray[currLevel].
                    formulaParseTree.
                        getRoot();
        }
        if ((currLevelSubstAnswer[assrtLogHypIndex]
                              =
                unifyLogHypFormula(
                    assrtLogHypArray[assrtLogHypIndex].
                        getExprParseTree().
                            getRoot(),
                    sourceRoot))
            != UnifySubst.IMPOSSIBLE) {

            if (mergeCurrLevelSubst()) {
                return true;
            }
        }

        backoutCurrLevelApplieds();
        return false;
    }

    private UnifySubst unifyLogHypFormula(ParseNode targetRoot,
                                          ParseNode sourceRoot) {

        currLevelDeferred     = UnifySubst.EMPTY_LIST;
        currLevelDeferredLast = null;

        if (sourceRoot != null) {
            if (!unifyLevel(targetRoot,
                            sourceRoot)) {
                return UnifySubst.IMPOSSIBLE;
            }
        }

        return currLevelDeferred;
    }

    private boolean mergeCurrLevelSubst() {

        if (currLevelDeferred != UnifySubst.EMPTY_LIST) {
            UnifySubst curr       = currLevelDeferred;
            while (curr != null) {
                if (!mergeSubst(curr)) {
                    return false;
                }
                curr              = curr.next;
            }
        }
        return true;
    }

    private void allocWorkVarsForUnassignedSourceVars()
                            throws VerifyException {

        VarHyp     sourceVarHyp;
        WorkVarHyp workVarHyp;
        for (int i = 0; i < assrtHypArray.length; i++) {

            if (!assrtHypArray[i].isVarHyp()) {
                continue;
            }

            sourceVarHyp          = (VarHyp)assrtHypArray[i];
            if (sourceVarHyp.paSubst != null) {
                continue;
            }

            workVarHyp            =
                workVarManager.allocWorkVarHyp(
                    sourceVarHyp.getTyp());

            addToAppliedArray(
                new UnifySubst(workVarHyp,
                               null,  //null toNode
                               true), //generatedDuringAccum
                F_LEVEL_NBR); //fLevel

            sourceVarHyp.paSubst  = new ParseNode(workVarHyp);

            addToAppliedArray(
                new UnifySubst(sourceVarHyp,
                               sourceVarHyp.paSubst,
                               true), //generatedDuringAccum
                F_LEVEL_NBR); //fLevel
        }
    }

    private boolean unifyLevel(ParseNode targetNode,
                               ParseNode sourceNode) {

        if (targetNode.stmt.getTyp() !=
            sourceNode.stmt.getTyp()) {
            return false;
        }

        if (targetNode.stmt.isVarHyp()) {
            VarHyp targetVarHyp   = (VarHyp)targetNode.stmt;
            UnifySubst targetSubst
                                  =
                new UnifySubst(targetVarHyp, //fromHyp
                               sourceNode,   //toNode
                               false);       //generatedDuringAccum

            if (currLevel == F_LEVEL_NBR &&
                targetVarHyp.paSubst == null) {
                targetVarHyp.paSubst
                                  = sourceNode;
                addToAppliedArray(targetSubst,
                                  F_LEVEL_NBR); //fLevel index
            }
            else {
                addToCurrLevelDeferred(targetSubst);
            }
            return true;
        }

        if (targetNode.stmt == sourceNode.stmt) {
            for (int i = 0; i < targetNode.child.length; i++) {
                if (!unifyLevel(targetNode.child[i],
                                sourceNode.child[i])) {
                    return false;
                }
            }
            return true;
        }

        if (sourceNode.stmt.isWorkVarHyp()) {
            addToCurrLevelDeferred(
                new UnifySubst((VarHyp)sourceNode.stmt,
                               targetNode,
                               false));
            return true;
        }

        return false;
    }

    private boolean mergeSubst(UnifySubst curr) {

        ParseNode toParseNode     = curr.toNode;

        if (curr.fromHyp.isWorkVarHyp()) {

            if (!curr.generatedDuringAccum) {
                toParseNode       =
                    curr.toNode.cloneTargetToSourceVars();
            }

            if (curr.fromHyp.paSubst == null) {

                int returnCode    =
                    toParseNode.
                        checkWorkVarHasOccursIn(
                            (WorkVarHyp)curr.fromHyp);

                if (returnCode ==
                    LangConstants.WV_OCCURS_IN_RENAME_LOOP) {
                    return true; // ok, but no assignment update!
                }

                if (returnCode ==
                    LangConstants.WV_OCCURS_IN_ERROR) {
                    return false; //naughty!
                }
            }
        }

        if (curr.fromHyp.paSubst == null) {
            curr.fromHyp.paSubst  = toParseNode;
            addToAppliedArray(curr,
                              currLevel);
            return true;
        }

        return subunify(curr.fromHyp.paSubst,
                        toParseNode);
    }

    // clone of mergeSubst()
    private boolean mergeSubst2(VarHyp    currFromHyp,
                                ParseNode currToNode,
                                boolean   currGeneratedDuringAccum) {

        ParseNode toParseNode     = currToNode;

        if (currFromHyp.isWorkVarHyp()) {

            if (!currGeneratedDuringAccum) {
                toParseNode       =
                    currToNode.cloneTargetToSourceVars();
            }

            if (currFromHyp.paSubst == null) {

                int returnCode    =
                    toParseNode.
                        checkWorkVarHasOccursIn(
                            (WorkVarHyp)currFromHyp);

                if (returnCode ==
                    LangConstants.WV_OCCURS_IN_RENAME_LOOP) {
                    return true; // ok, but no assignment update!
                }

                if (returnCode ==
                    LangConstants.WV_OCCURS_IN_ERROR) {
                    return false; //naughty!
                }
            }
        }

        if (currFromHyp.paSubst == null) {
            currFromHyp.paSubst   = toParseNode;
            addToAppliedArray(
                new UnifySubst(currFromHyp,
                               currToNode,
                               currGeneratedDuringAccum),
                currLevel);
            return true;
        }

        return subunify(currFromHyp.paSubst,
                        toParseNode);
    }

    /**
     *  Subunification is consistency checking of
     *  two substitutions to a single source or
     *  target variable where the substitutions
     *  consist only of Source variables and Work
     *  Variables.
     *  <p>
     *  NOTE: target variables are NOT present in
     *        Parse Nodes n1 and n2!!! THEREFORE
     *        any substitutions generated during
     *        this process can only, by definition,
     *        be applied to WORK VARIABLES (because
     *        there are no substitutions to the
     *        source variables themselves.)
     */
    private boolean subunify(ParseNode n1,
                             ParseNode n2) {

        if (n1.stmt == n2.stmt) {
            if (!n1.stmt.isVarHyp()) {
                for (int i = 0; i < n1.child.length; i++) {
                    if (!subunify(n1.child[i],
                                  n2.child[i])) {
                        return false;
                    }
                }
            }
            return true;
        }

        if (n1.stmt.getTyp() !=
            n2.stmt.getTyp()) {
            return false;
        }

        if (n1.stmt.isWorkVarHyp()) {
            return mergeSubst2((VarHyp)n1.stmt,
                               n2,
                               true);    // generatedDuringAccum
        }
        if (n2.stmt.isWorkVarHyp()) {
            return mergeSubst2((VarHyp)n2.stmt,
                               n1,
                               true);    // generatedDuringAccum
        }

        return false;
    }

    // ===================================================
    // ***************************************************
    // *** MISCELLANEOUS HOUSEKEEPING
    // ***************************************************
    // ===================================================

    private void addToCurrLevelDeferred(UnifySubst deferredSubst) {

        currLevelDeferredLast     =
            deferredSubst.insert(currLevelDeferredLast);

        if (currLevelDeferred == UnifySubst.EMPTY_LIST) {
            currLevelDeferred     = deferredSubst;
        }
    }

    private void addToAppliedArray(UnifySubst unifySubst,
                                   int        levelIndex) {
        if (appliedCnt >= applied.length) {
            if (appliedCnt >=
                    PaConstants.STEP_UNIFIER_APPLIED_ARRAY_LEN_MAX) {
                throw new IllegalArgumentException(
                    PaConstants.ERRMSG_ADD_TO_APPLIED_ARRAY_OFLOW_1);
            }
            int n                 =
                applied.length +
                PaConstants.STEP_UNIFIER_APPLIED_ARRAY_LEN_INIT;
            UnifySubst[] x        = new UnifySubst[n];
            for (int i = 0; i < appliedCnt; i++) {
                x[i]              = applied[i];
            }
            applied               = x;
        }

        applied[appliedCnt++]     = unifySubst;
        ++levelAppliedCnt[++levelIndex];
    }

    private void backoutCurrLevelApplieds() {
        backoutOneLevelApplieds(currLevel);
    }

    private void backoutAllHLevelApplieds() {
        for (int i = levelAppliedCnt.length - 2;
                 i >= 0;
                 i--) {
            backoutOneLevelApplieds(i);
        }
    }

    private void backoutOneLevelApplieds(int levelNbr) {

        int backoutCnt            = levelAppliedCnt[++levelNbr];

        UnifySubst appliedSubst;
        while(backoutCnt-- > 0) {
            appliedSubst          = applied[--appliedCnt];

            // Erase fromHyp.paSubst value because we never
            // apply a substitution value more than once per
            // variable during unification -- so if the VarHyp
            // is mentioned in array "applied", erase .paSubst.
            appliedSubst.fromHyp.paSubst
                                  = null;

            if (appliedSubst.fromHyp.isWorkVarHyp() &&
                appliedSubst.toNode == null) {

                // appliedSubst.toNode == null means that
                // allocation was requested during unification;
                // the WorkVarHyp was not part of the original
                // formula, so it can be deallocated.
                workVarManager.dealloc(appliedSubst.fromHyp);
            }

            applied[appliedCnt]   = null;
        }

        levelAppliedCnt[levelNbr] = 0;
    }
}
