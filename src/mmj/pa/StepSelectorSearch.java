//********************************************************************/
//* Copyright (C) 2008                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  StepSelectorSearch.java  0.02 08/01/2008
 *
 *  Version 0.01: 03/01/2008
 *  ==> New.
 *
 *  Version 0.02: 08/01.2008
 *  ==> chg'd assrtArray to assrtAList, an ArrayList, as preparation
 *      for the TheoremLoader changes.
 *  ==> new stuff for Theorem Loader.
 *  ==> fixed bug in binary search by changing
 *         "mid = (low + high) / 2;"
 *      to "mid = low + ((high - low) / 2);"
 */

package mmj.pa;

import  java.util.ArrayList;
import  java.util.Iterator;
import  java.util.Collections;
import  mmj.lang.*;
import  mmj.verify.*;
import  mmj.util.MergeSortedArrayLists;

/**
 *  StepSelectorSearch builds StepSelectorResults for
 *  a single derivation proof step.
 */
public class StepSelectorSearch {

    private ProofAsstPreferences   proofAsstPreferences;
    private VerifyProofs           verifyProofs;
    private Cnst                   provableLogicStmtTyp;
    private StepUnifier            stepUnifier;

    /* assrtArray loaded and sorted during constructor initialization
     */
//  private Assrt[]                assrtArray;
    private ArrayList              assrtAList;

    /* these instance variables are "global" work items
       stored here for convenience.
     */
    private DerivationStep         derivStep;
    private ProofStepStmt[]        derivStepHypArray;

    private Assrt                  assrt;
    private int                    assrtNbrLogHyps;
    private Hyp[]                  assrtHypArray;
    private LogHyp[]               assrtLogHypArray;
    private ParseNode              assrtRoot;
    private ParseNode[]            assrtSubst;

    /**
     *  Constructor for StepSelectorSearch
     */
    public StepSelectorSearch(
                    ProofAsstPreferences proofAsstPreferences,
                    VerifyProofs         verifyProofs,
                    Cnst                 provableLogicStmtTyp,
                    ArrayList            unifySearchList) {

        this.proofAsstPreferences = proofAsstPreferences;
        this.verifyProofs         = verifyProofs;
        this.provableLogicStmtTyp = provableLogicStmtTyp;

        stepUnifier               =
            proofAsstPreferences.getStepUnifier();

        if (unifySearchList.size() == 0) {
            throw new IllegalArgumentException(
                PaConstants.
                    ERRMSG_SELECTOR_SEARCH_ASSRT_LIST_EMPTY_1);
        }

//      assrtArray                =
//          Assrt.sortListIntoArray(unifySearchList,
//                                  Assrt.NBR_LOG_HYP_SEQ);

        int listSize              =
            (unifySearchList.size()
             * (100 +
                proofAsstPreferences.
                    getAssrtListFreespace())
             / 100);
        assrtAList                = new ArrayList(listSize);

// kept failing w/IndexOutOfBoundsException! and message
// "Source does not fit in dest"
//      Collections.copy(assrtAList,
//                       unifySearchList);
        Iterator i                = unifySearchList.iterator();
        while (i.hasNext()) {
            assrtAList.add(i.next());
        }

        Collections.sort(assrtAList,
                         Assrt.NBR_LOG_HYP_SEQ);
    }

    public void mergeListOfAssrtAddsSortedBySeq(
                            ArrayList listOfAssrtAddsSortedBySeq) {

        ArrayList addList         =
            new ArrayList(
                listOfAssrtAddsSortedBySeq.size());

//      got "source does not fit in dest" message
//      Collections.copy(addList,
//                       listOfAssrtAddsSortedBySeq);
        Iterator iterator         =
                listOfAssrtAddsSortedBySeq.iterator();
        while (iterator.hasNext()) {
            addList.add(iterator.next());
        }

        Collections.sort(addList,
                         Assrt.NBR_LOG_HYP_SEQ);

        MergeSortedArrayLists m   =
            new MergeSortedArrayLists(assrtAList,
                                      addList,
                                      Assrt.NBR_LOG_HYP_SEQ,
                                      true); // abortIfDupsFound
    }


    /**
     *  Finds the assertions which unify with the given step
     *  and loads them into StepSelectorResults.
     *  <p>
     *  Always returns at least one StepSelectorResult item,
     *  "***END***" with Assrt = null (or n items + "***MORE***").
     *  <p>
     *  @param derivStep DerivationStep from ProofWorksheet
     *  @return StepSelectorResults containing unifying assertions
     *          and corresponding formulas.
     *  @throws VerifyException if not enough allocatable WorkVars.
     */
    public StepSelectorResults loadStepSelectorResults(
                                       DerivationStep derivStep)
                                 throws VerifyException {

        boolean storeOverflow     = false;

        this.derivStep            = derivStep;

        int maxSeq                = derivStep.w.getMaxSeq();

        StepSelectorStore store   =
            StepSelectorStore.
                createStepSelectorStore(
                    proofAsstPreferences);

        /* count and double-check input
         */
        int nbrDerivStepHyps      = 0;
        for (int i = 0; i < derivStep.hyp.length; i++) {
            if (derivStep.hyp[i] == null) {
                continue;
            }
            /* This is bogus -- but a hyp[i]'s deriveStepFormula
               could have errored out...
             */
            if (derivStep.hyp[i].formulaParseTree == null) {
                throw new VerifyException(
                    PaConstants.
                        ERRMSG_SELECTOR_SEARCH_NULL_PARSE_TREE_1
                    + PaConstants.DOT_STEP_CAPTION
                    + derivStep.step);
            }
            ++nbrDerivStepHyps;
        }


        /* Now sort to avoid worst case search time
         */
        ProofStepStmt[] derivStepSortedHypArray
                                  = derivStep.getSortedHypArray();

        /*  Binary search for 1st Assrt w/correct number of log hyps.
         */
        int assrtIndex            = computeSearchStart(
                                        nbrDerivStepHyps);

        /* See if log hyp "wildcards" to be searched.
         */
        int maxHyps;
        if (nbrDerivStepHyps == derivStep.hyp.length) {
            maxHyps               = nbrDerivStepHyps;
        }
        else {
            maxHyps               = Integer.MAX_VALUE;
        }

        /* If user specified n hyps we begin the search there at n,
           and if there are "wildcards" (e.g. "2,?,1" or "2,,1")
           then we search assertions with n->9999999 log hyps
         */
        int hypIndex              = nbrDerivStepHyps;
        hypLoop: while (hypIndex <= maxHyps) {

            /* Copy sorted array to new array whose size matches
               the size of the assertion hyp arrays -- note that
               "null" hyps are at the end due to the sort, so
               we lose nothing here...
             */
            derivStepHypArray     = new ProofStepStmt[hypIndex];
            for (int i = 0; i < nbrDerivStepHyps; i++) {
                derivStepHypArray[i]
                                  =
                derivStepSortedHypArray[i];
            }

//          assrtLoop: while (assrtIndex < assrtArray.length) {
            assrtLoop: while (assrtIndex < assrtAList.size()) {
//              assrt             = assrtArray[assrtIndex];
                assrt             = (Assrt)assrtAList.get(assrtIndex);
                if (assrt.getSeq() < maxSeq) {
                    assrtNbrLogHyps
                                  = assrt.getLogHypArrayLength();
                    if (assrtNbrLogHyps != hypIndex) {
                        ++hypIndex;
                        continue hypLoop;
                    }
                    if (isAssrtUnifiable()) {
                        if (addAssrtToStore(store)) { //isFull()
                            storeOverflow
                                  = true;
                            break hypLoop;
                        }
                    }
                }
                else {
                    if (hypIndex <      // 95% of Assrts
                        PaConstants.
                            STEP_SELECTOR_SEARCH_HYP_LOOKUP_MAX) {
                        ++hypIndex;     // so skip forward
                        assrtIndex
                                  = computeSearchStart(hypIndex);
                        continue hypLoop;
                    }
                }
                ++assrtIndex;
                continue assrtLoop;
            }
            break hypLoop;
        }

        return store.
                   createStepSelectorResults(
                       derivStep.step,
                       storeOverflow);
    }

    /**
     *   Boolean search of assrtArray using partial key
     *   to find starting position for scan of Assertions.
     *
     *   Assumes that assrtArray is not empty and that it is
     *   sorted by Assrt.NBR_LOG_HYP_SEQ.
     *
     *   @param nbrHyps number of hypotheses on derivation
     *                  step.
     *   @return if not found, then return Integer.MAX_VALUE,
     *           otherwise the index of the first assertion
     *           with number of LogHyps >= input nbrHyps.
     */
    private int computeSearchStart(int nbrHyps) {

//      if (assrtArray[0].getLogHypArrayLength() >= nbrHyps) {
        if (((Assrt)assrtAList.get(0)).getLogHypArrayLength()
            >= nbrHyps) {

            return 0;
        }

//      int high                  = assrtArray.length - 1;
        int high                  = assrtAList.size() - 1;

//      if (nbrHyps > assrtArray[high].getLogHypArrayLength()) {
        if (nbrHyps >
            ((Assrt)assrtAList.get(high)).getLogHypArrayLength()) {
            return Integer.MAX_VALUE;
        }

        /*
         *   OK! NOW WE HAVE A FACT:
         *
         *       Assrt NbrLogHyps[low] < nbrHyps <= NbrLogHyps[high].
         *
         *   Therefore -> a valid answer exists in the array
         *   and the extra loop (dountil mid == prev) ensures
         *   that the final 'mid' index value is one less than
         *   the correct answer!
         *
         *   The drawback and quirk of this algorithm is that
         *   it never gets "lucky". The number of loops is
         *   always log(2)n + 1 (approx.)
         */
        int low                   = 0;
        int mid                   = low + ((high - low) / 2);
        int prev;
        do {
            prev                  = mid;
//          if (assrtArray[mid].getLogHypArrayLength() < nbrHyps) {
            if (((Assrt)assrtAList.get(mid)).getLogHypArrayLength()
                < nbrHyps) {
                low               = mid;
            }
            else {
                high              = mid;
            }
            mid                   = low + ((high - low) / 2);
        } while (mid != prev);

        return ++mid;
    }

    private boolean isAssrtUnifiable()
                            throws VerifyException {

        if (unifyStepFormulaWithWorkVars()) {

            if (assrtNbrLogHyps == 0) {
                assrtSubst        =
                    stepUnifier.
                        finalizeAndLoadAssrtSubst();
                return true;
            }

            if ((assrtSubst       =
                    stepUnifier.
                        unifyAndMergeHypsSorted(
                            assrt.getSortedLogHypArray(),
                            derivStepHypArray))
                != null) {
                return true;
            }
        }

        return false;
    }

    //cloned from ProofUnifier.java
    private boolean unifyStepFormulaWithWorkVars()
                            throws VerifyException {

        assrtHypArray             = assrt.getMandFrame().hypArray;
        assrtLogHypArray          = assrt.getLogHypArray();
        assrtRoot                 = assrt.getExprParseTree().getRoot();

        ParseNode stepRoot        = null;
        if (derivStep.formulaParseTree != null) {
            stepRoot              =
                derivStep.formulaParseTree.getRoot();
        }
        return stepUnifier.unifyAndMergeStepFormula(
                                      false, //no commit!
                                      assrtRoot,
                                      stepRoot,
                                      assrtHypArray,
                                      assrtLogHypArray);
    }

    private boolean addAssrtToStore(StepSelectorStore store) {

        String[] lineArray        =  new String[1 + assrtNbrLogHyps];

        int      cntLines         = 0;

        Formula conclusionFormula;
        if (proofAsstPreferences.
                getStepSelectorShowSubstitutions()) {
            conclusionFormula     =
                buildStepSelectionSubstFormula(
                    assrt.getExprParseTree());
        }
        else {
            conclusionFormula     =
                assrt.getFormula();
        }


        Formula[] logHypFormula   = new Formula[assrtNbrLogHyps];
        for (int i = 0; i < assrtNbrLogHyps; i++) {
            if (proofAsstPreferences.
                    getStepSelectorShowSubstitutions()) {
                logHypFormula[i]  =
                    buildStepSelectionSubstFormula(
                        assrtLogHypArray[i].
                            getExprParseTree());
            }
            else {
                logHypFormula[i]  =
                    assrtLogHypArray[i].getFormula();
            }
        }

        StringBuffer sb;
        if (assrtNbrLogHyps == 0) {

            sb                    =
                new StringBuffer((conclusionFormula.getCnt()
                                 * 4)       //guess
                                 + 10);     //guess
            sb.append(assrt.getLabel());
            sb.append(
                PaConstants.STEP_SELECTOR_FORMULA_LABEL_SEPARATOR);
            sb.append(conclusionFormula.toString());
            lineArray[cntLines++]
                                  = sb.toString();
        }

        else {

            sb                    =
                new StringBuffer((logHypFormula[0].getCnt()
                                 * 4)
                                 + 10);
            sb.append(assrt.getLabel());
            sb.append(
                PaConstants.
                    STEP_SELECTOR_FORMULA_LABEL_SEPARATOR);
            sb.append(logHypFormula[0].toString());
            lineArray[cntLines++]
                                  = sb.toString();

            for (int i = 1; i < assrtNbrLogHyps; i++) {

                sb                =
                    new StringBuffer((logHypFormula[i].getCnt()
                                     * 4)
                                     + 10);
                sb.append(
                    PaConstants.
                        STEP_SELECTOR_SEARCH_FORMULA_INDENT);
                sb.append(
                    PaConstants.
                        STEP_SELECTOR_FORMULA_LOG_HYP_SEPARATOR);
                sb.append(logHypFormula[i].toString());
                lineArray[cntLines++]
                                      = sb.toString();
            }

            sb                =
                new StringBuffer((conclusionFormula.getCnt()
                             * 4)
                             + 10);
            sb.append(
                    PaConstants.
                        STEP_SELECTOR_SEARCH_FORMULA_INDENT);
            sb.append(
                PaConstants.
                    STEP_SELECTOR_FORMULA_YIELDS_SEPARATOR);
            sb.append(conclusionFormula.toString());
            lineArray[cntLines++]
                              = sb.toString();
        }

        return store.add(assrt,
                         lineArray);
    }

    private Formula buildStepSelectionSubstFormula(
                                ParseTree inParseTree) {

        ParseTree outParseTree
                              =
            inParseTree.deepCloneApplyingAssrtSubst(
                assrtHypArray,
                assrtSubst);

        Formula outFormula    =
            verifyProofs.convertRPNToFormula(
                outParseTree.convertToRPN(),
                PaConstants.DOT_STEP_CAPTION
                    + derivStep.step);

        outFormula.setTyp(provableLogicStmtTyp);

        return outFormula;
    }
}
