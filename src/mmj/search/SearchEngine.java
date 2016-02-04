//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchEngine.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.util.BitSet;
import java.util.List;
import java.util.concurrent.*;

import mmj.lang.*;
import mmj.pa.*;
import mmj.verify.VerifyException;
import mmj.verify.VerifyProofs;

public class SearchEngine {

    private final SearchMgr searchMgr;
    private SearchArgs searchArgs;
    private CompiledSearchArgs compiledSearchArgs = null;
    private SearchOutput searchOutput = null;
    private SearchOutputStore store = null;
    private final ProofAsst proofAsst;
    private final ProofAsstPreferences proofAsstPreferences;
    private final BookManager bookManager;
    private final VerifyProofs verifyProofs;
    private final Cnst provableLogicStmtTyp;
    private final StepUnifier stepUnifier;
    private List<Assrt> assrtAList;
    private DerivationStep derivStep = null;
    private ProofStepStmt[] derivStepHypArray = null;
    private Assrt assrt = null;
    private int assrtNbrLogHyps = 0;
    private Hyp[] assrtHypArray = null;
    private LogHyp[] assrtLogHypArray = null;
    private ParseNode[] assrtSubst = null;
    private boolean stepSearchMode = false;
    private boolean substitutions = false;

    public SearchEngine(final SearchMgr searchMgr, final ProofAsst proofAsst,
        final ProofAsstPreferences proofAsstPreferences,
        final BookManager bookManager, final VerifyProofs verifyProofs,
        final Cnst cnst)
    {
        this.searchMgr = searchMgr;
        this.proofAsst = proofAsst;
        this.proofAsstPreferences = proofAsstPreferences;
        this.bookManager = bookManager;
        this.verifyProofs = verifyProofs;
        provableLogicStmtTyp = cnst;
        searchOutput = searchMgr.getSearchOutput();
        searchArgs = searchMgr.getSearchArgs();
        stepSearchMode = searchArgs.stepSearchMode;
        setAssrtAList(searchArgs.sortedAssrtSearchList);
        stepUnifier = proofAsstPreferences.getStepUnifier();
    }

    public SearchOutput execSearch() {
        searchOutput = searchMgr.getSearchOutput();
        searchArgs = searchMgr.getSearchArgs();
        stepSearchMode = searchArgs.stepSearchMode;
        setAssrtAList(searchArgs.sortedAssrtSearchList);
        store = null;
        proofAsstPreferences.getWorkVarManager()
            .deallocAndReallocAll(searchArgs.proofWorksheet);
        compiledSearchArgs = searchArgs.compile(searchMgr, bookManager,
            searchOutput, proofAsst, proofAsstPreferences, verifyProofs,
            provableLogicStmtTyp);
        if (searchOutput.searchReturnCode == 0) {
            final FutureTask<SearchOutput> search = new FutureTask<>(
                new Callable<SearchOutput>()
            {
                    public SearchOutput call() throws Exception {
                        return searchTask();
                    }
                });
            Executors.newSingleThreadExecutor().execute(search);
            try {
                searchOutput = search.get(compiledSearchArgs.searchMaxTime,
                    TimeUnit.SECONDS);
            } catch (final InterruptedException interruptedexception) {
                searchOutput.storeError(3, 30,
                    SearchConstants.ERRMSG_SEARCH_TASK_INTERRUPTED_1
                        + interruptedexception.getMessage());
            } catch (final ExecutionException executionexception) {
                searchOutput.storeError(4, 30,
                    SearchConstants.ERRMSG_SEARCH_TASK_EXECUTION_1
                        + executionexception.getCause() + " "
                        + executionexception.getMessage());
                throw new IllegalArgumentException(
                    "Rethrowing ExecutionException", executionexception);
            } catch (final TimeoutException timeoutexception) {
                searchOutput.storeError(2, 30,
                    SearchConstants.ERRMSG_SEARCH_TASK_TIMEOUT_1
                        + timeoutexception.getMessage());
            }
        }
        searchOutput.finalize(compiledSearchArgs, bookManager,
            proofAsst.getMessages());
        return searchOutput;
    }

    public SearchOutput searchTask() {
        substitutions = compiledSearchArgs.searchSubstitutions;
        store = new SearchOutputStore(compiledSearchArgs.searchMaxResults,
            compiledSearchArgs.searchOutputSortNbr);
        try {
            loadSearchOutput();
            if (searchOutput.searchReturnCode == 0
                && compiledSearchArgs.searchResultsChecked > 0)
            {
                checkForInterrupt();
                doExtendedSearch();
            }
        } catch (final InterruptedException interruptedexception) {
            searchOutput.storeError(3, 30,
                SearchConstants.ERRMSG_SEARCH_TASK_INTERRUPTED_1
                    + interruptedexception.getMessage());
        }
        return searchOutput;
    }

    public List<Assrt> getAssrtAList() {
        return assrtAList;
    }

    public void setAssrtAList(final List<Assrt> arraylist) {
        if (arraylist.size() == 0)
            throw new IllegalArgumentException(
                SearchConstants.ERRMSG_SEARCH_ASSRT_LIST_EMPTY_1);
        else {
            searchOutput.statsInputAssrtListSize = arraylist.size();
            assrtAList = arraylist;
            return;
        }
    }

    private void loadSearchOutput() throws InterruptedException {
        ProofStepStmt[] aproofStepStmt = null;
        boolean full = false;
        final int minSeq = compiledSearchArgs.searchMinSeq;
        final int maxSeq = compiledSearchArgs.searchMaxSeq;
        final int nbrDerivStepHyps = compiledSearchArgs.nbrDerivStepHyps;
        final int minHyps = compiledSearchArgs.searchMinHyps;
        final int maxHyps = compiledSearchArgs.searchMaxHyps;
        final int minProofRefs = compiledSearchArgs.searchMinProofRefs;
        String step;
        if (stepSearchMode) {
            derivStep = searchArgs.stepSearchStmt;
            aproofStepStmt = derivStep.getSortedHypArray();
            step = derivStep.getStep();
        }
        else {
            derivStep = null;
            aproofStepStmt = null;
            derivStepHypArray = null;
            step = "";
        }
        int k1 = computeSearchStart(minHyps);
        hypLoop: for (int hyp = minHyps; hyp <= maxHyps; hyp++) {
            if (stepSearchMode) {
                derivStepHypArray = new ProofStepStmt[hyp];
                for (int j2 = 0; j2 < nbrDerivStepHyps; j2++)
                    derivStepHypArray[j2] = aproofStepStmt[j2];

            }
            for (; k1 < assrtAList.size(); k1++) {
                checkForInterrupt();
                if (searchOutput.searchReturnCode != 0)
                    break;
                assrt = assrtAList.get(k1);
                searchOutput.statsNbrInputAssrtGets++;
                final int i2 = assrt.getSeq();
                if (i2 >= maxSeq) {
                    searchOutput.statsNbrRejectGEMaxSeq++;
                    k1 = computeSearchStart(hyp + 1);
                    continue hypLoop;
                }
                if (i2 <= minSeq) {
                    searchOutput.statsNbrRejectLEMinSeq++;
                    k1++;
                    continue;
                }
                assrtNbrLogHyps = assrt.getLogHypArrayLength();
                if (assrtNbrLogHyps != hyp) {
                    searchOutput.statsNbrRejectGTHypIndex++;
                    continue hypLoop;
                }
                if (assrt.getNbrProofRefs() < minProofRefs) {
                    searchOutput.statsNbrRejectLTMinProofRefs++;
                    k1++;
                    continue;
                }
                assrtLogHypArray = assrt.getLogHypArray();
                assrtHypArray = assrt.getMandFrame().hypArray;
                if (evaluateOtherExclusionCriteria()) {
                    if (stepSearchMode && !isAssrtUnifiable()) {
                        searchOutput.statsNbrRejectFailUnify++;
                        k1++;
                        continue;
                    }
                    if (evaluateSearchDataLines()) {
                        if (searchOutput.searchReturnCode != 0)
                            break;
                        searchOutput.statsNbrSelected++;
                        if (addAssrtToStore(computeScore())) {
                            full = true;
                            break;
                        }
                    }
                    else
                        searchOutput.statsNbrRejectFailSearchData++;
                }
                else
                    searchOutput.statsNbrRejectOtherExclCriteria++;
            }
        }
        store.loadSearchOutput(searchOutput, step, full);
        return;
    }

    private void doExtendedSearch() throws InterruptedException {
        checkForInterrupt();
    }

    private boolean evaluateOtherExclusionCriteria() {
        final String s = assrt.getLabel();
        final int i = compiledSearchArgs.searchExclLabelsPattern.length;
        for (int j = 0; j < i; j++)
            if (compiledSearchArgs.searchExclLabelsPattern[j].matcher(s)
                .matches())
            {
                searchOutput.statsNbrRejectExclLabels++;
                return false;
            }

        final BitSet bitset = compiledSearchArgs.searchCombinedDependencies;
        if (bitset != null)
            if (compiledSearchArgs.searchUseChapHierarchy) {
                if (!bitset.get(assrt.getChapterNbr()))
                    return false;
            }
            else if (compiledSearchArgs.searchUseSecHierarchy && !bitset
                .get(BookManager.getOrigSectionNbr(assrt.getSectionNbr())))
                return false;
        return true;
    }

    private boolean evaluateSearchDataLines() {
        if (compiledSearchArgs.searchDataLines != null)
            return compiledSearchArgs.searchDataLines.evaluate(assrt,
                compiledSearchArgs);
        else
            return true;
    }

    private int computeSearchStart(final int minHyps) {
        if (assrtAList.get(0).getLogHypArrayLength() >= minHyps)
            return 0;
        int j = assrtAList.size() - 1;
        if (minHyps > assrtAList.get(j).getLogHypArrayLength())
            return Integer.MAX_VALUE;
        int k = 0;
        int l = k + (j - k) / 2;
        int i1;
        do {
            i1 = l;
            if (assrtAList.get(l).getLogHypArrayLength() < minHyps)
                k = l;
            else
                j = l;
            l = k + (j - k) / 2;
        } while (l != i1);
        return ++l;
    }

    private boolean isAssrtUnifiable() {
        try {
            if (unifyStepFormulaWithWorkVars()) {
                if (assrtNbrLogHyps == 0) {
                    assrtSubst = stepUnifier.finalizeAndLoadAssrtSubst();
                    return true;
                }
                if ((assrtSubst = stepUnifier.unifyAndMergeHypsSorted(
                    assrt.getSortedLogHypArray(), derivStepHypArray)) != null)
                    return true;
            }
        } catch (final VerifyException verifyException) {
            throw new IllegalArgumentException(
                " A work var problem: alloc more via RunParms? "
                    + verifyException.getMessage());
        }
        return false;
    }

    private boolean unifyStepFormulaWithWorkVars() throws VerifyException {
        assrt.getExprParseTree().getRoot();
        ParseNode parseNode = null;
        if (derivStep.formulaParseTree != null)
            parseNode = derivStep.formulaParseTree.getRoot();
        return stepUnifier.unifyAndMergeStepFormula(false, assrt, parseNode);
    }

    private int computeScore() {
        if (stepSearchMode && !compiledSearchArgs.derivStepHypWildcards) {
            searchOutput.statsNbrCompletedSearchResults++;
            return 100;
        }
        else
            return 50;
    }

    private boolean addAssrtToStore(final int score) {
        String s;
        if (score == 100)
            s = SearchConstants.COMPLETED_ITEM_OUTPUT_LITERAL;
        else
            s = "";
        String s1 = "";
        int j;
        if (compiledSearchArgs.searchComments) {
            s1 = assrt.getDescription();
            j = 2 + assrtNbrLogHyps;
        }
        else
            j = 1 + assrtNbrLogHyps;
        final String[] selection = new String[j];
        int k = 0;
        final Formula[] aformula = new Formula[assrtNbrLogHyps];
        Formula formula;
        if (stepSearchMode && substitutions) {
            formula = buildSearchSelectionSubstFormula(
                assrt.getExprParseTree());
            for (int l = 0; l < assrtNbrLogHyps; l++)
                aformula[l] = buildSearchSelectionSubstFormula(
                    assrtLogHypArray[l].getExprParseTree());

        }
        else {
            formula = assrt.getFormula();
            for (int i1 = 0; i1 < assrtNbrLogHyps; i1++)
                aformula[i1] = assrtLogHypArray[i1].getFormula();

        }
        if (assrtNbrLogHyps == 0) {
            if (s1.length() > 0) {
                selection[k++] = s + assrt.getLabel() + " " + s1;
                selection[k++] = SearchConstants.SEARCH_OUTPUT_SEARCH_FORMULA_INDENT
                    + SearchConstants.SEARCH_OUTPUT_FORMULA_LABEL_SEPARATOR
                    + formula.toString();
            }
            else
                selection[k++] = s + assrt.getLabel()
                    + SearchConstants.SEARCH_OUTPUT_FORMULA_LABEL_SEPARATOR
                    + formula.toString();
        }
        else {
            String s3;
            if (s1.length() > 0) {
                selection[k++] = s + assrt.getLabel() + " " + s1;
                s3 = SearchConstants.SEARCH_OUTPUT_SEARCH_FORMULA_INDENT;
            }
            else
                s3 = s + assrt.getLabel();
            selection[k++] = s3
                + SearchConstants.SEARCH_OUTPUT_FORMULA_LABEL_SEPARATOR
                + aformula[0].toString();
            for (int j1 = 1; j1 < assrtNbrLogHyps; j1++)
                selection[k++] = SearchConstants.SEARCH_OUTPUT_SEARCH_FORMULA_INDENT
                    + SearchConstants.SEARCH_OUTPUT_FORMULA_LOG_HYP_SEPARATOR
                    + aformula[j1].toString();

            selection[k++] = SearchConstants.SEARCH_OUTPUT_SEARCH_FORMULA_INDENT
                + SearchConstants.SEARCH_OUTPUT_FORMULA_YIELDS_SEPARATOR
                + formula.toString();
        }
        return store.add(assrt, selection, score);
    }
    private Formula buildSearchSelectionSubstFormula(
        final ParseTree parseTree)
    {
        final ParseTree parseTree1 = parseTree
            .deepCloneApplyingAssrtSubst(assrtHypArray, assrtSubst);
        final Formula formula = verifyProofs.convertRPNToFormula(
            parseTree1.convertToRPN(),
            SearchConstants.DOT_STEP_CAPTION + derivStep.getStep());
        formula.setTyp(provableLogicStmtTyp);
        return formula;
    }

    private void checkForInterrupt() throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        else
            return;
    }
}
