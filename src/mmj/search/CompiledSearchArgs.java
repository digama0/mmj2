//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * CompiledSearchArgs.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.util.BitSet;
import java.util.List;
import java.util.regex.Pattern;

import mmj.lang.*;
import mmj.pa.*;
import mmj.verify.VerifyProofs;

public class CompiledSearchArgs {

    public CompiledSearchArgs(final SearchArgs args, final SearchMgr searchMgr,
        final BookManager bookManager, final SearchOutput searchOutput,
        final ProofAsst proofAsst,
        final ProofAsstPreferences proofAsstPreferences,
        final VerifyProofs verifyProofs, final Cnst cnst)
    {
        nbrDerivStepHyps = 0;
        derivStepHypWildcards = false;
        searchStepSearchMode = false;
        searchOrSeparator = null;
        searchSingleQuote = null;
        searchDoubleQuote = null;
        searchInWhatChoice = new int[4];
        searchPartChoice = new int[4];
        searchFormatChoice = new int[4];
        searchOperChoice = new int[4];
        searchForWhat = new String[4];
        searchBoolChoice = new int[4];
        searchExclLabels = null;
        searchExclLabelsPattern = new Pattern[0];
        searchMinProofRefs = 0;
        searchResultsChecked = 0;
        searchMaxTime = 0;
        searchMinHyps = 0;
        searchMaxExtResults = 0;
        searchSubstitutions = false;
        searchMaxHyps = 0;
        searchMaxIncompHyps = 0;
        searchComments = false;
        searchMaxResults = 0;
        searchPrevStepsChecked = 0;
        searchAutoSelect = false;
        searchChapSecHierarchyChoice = 0;
        searchReuseDerivSteps = false;
        searchStats = 0;
        searchFromChap = null;
        searchFromSec = null;
        searchThruChap = null;
        searchThruSec = null;
        searchOutputSortNbr = 0;
        searchComboFrame = null;
        searchReferenceStmt = null;
        searchReferenceStmtChapter = null;
        searchReferenceStmtSection = null;
        searchMaxSeq = 0;
        searchMinSeq = 0;
        searchChapSecDependenciesArray = null;
        searchUseChapHierarchy = false;
        searchUseSecHierarchy = false;
        searchReferenceStmtDependencies = null;
        searchThruChapSecDependencies = null;
        searchCombinedDependencies = null;
        searchDataLines = null;
        searchArgs = args;
        this.searchMgr = searchMgr;
        this.bookManager = bookManager;
        this.searchOutput = searchOutput;
        this.proofAsst = proofAsst;
        this.proofAsstPreferences = proofAsstPreferences;
        this.verifyProofs = verifyProofs;
        provableLogicStmtTyp = cnst;
        sortedAssrtSearchList = args.sortedAssrtSearchList;
        logicalSystem = searchMgr.getProofAsst().getLogicalSystem();
        searchStepSearchMode = args.stepSearchMode;
        searchMaxSeq = args.searchMaxSeq;
        for (final SearchArgsField element : args.arg)
            element.compile(this, searchMgr, bookManager, searchOutput,
                proofAsst, proofAsstPreferences, verifyProofs, cnst);

        if (searchOutput.searchReturnCode == 0) {
            relationalCompiles();
            if (searchOutput.searchReturnCode == 0)
                unsupportedFeaturesCheck();
        }
    }

    private void unsupportedFeaturesCheck() {
        if (searchResultsChecked != 0)
            searchArgs.resultsChecked.unsupportedFeatureError(searchOutput);
        if (searchMaxExtResults != 0)
            searchArgs.maxExtResults.unsupportedFeatureError(searchOutput);
        if (searchMaxIncompHyps != 0)
            searchArgs.maxIncompHyps.unsupportedFeatureError(searchOutput);
        if (searchPrevStepsChecked != 0)
            searchArgs.prevStepsChecked.unsupportedFeatureError(searchOutput);
        if (searchReuseDerivSteps)
            searchArgs.reuseDerivSteps.unsupportedFeatureError(searchOutput);
        if (searchAutoSelect)
            searchArgs.autoSelect.unsupportedFeatureError(searchOutput);
    }

    private void relationalCompiles() {
        if (searchOrSeparator.equals(searchSingleQuote)
            || searchOrSeparator.equals(searchDoubleQuote)
            || searchOrSeparator.startsWith(searchSingleQuote)
            || searchOrSeparator.startsWith(searchDoubleQuote)
            || searchSingleQuote.startsWith(searchOrSeparator)
            || searchSingleQuote.startsWith(searchOrSeparator))
            searchArgs.orSeparator.storeArgError(searchOutput,
                searchOrSeparator,
                SearchConstants.ERRMSG_OR_SEPARATOR_EQ_QUOTE_ERROR, "");
        if (searchSingleQuote.equals(searchDoubleQuote)
            || searchSingleQuote.startsWith(searchDoubleQuote)
            || searchDoubleQuote.startsWith(searchSingleQuote))
            searchArgs.singleQuote.storeArgError(searchOutput,
                searchSingleQuote,
                SearchConstants.ERRMSG_SINGLE_EQ_DOUBLE_QUOTE_ERROR, "");
        if (searchMinHyps > searchMaxHyps)
            searchArgs.minHyps.storeArgError(searchOutput,
                Integer.toString(searchMinHyps),
                SearchConstants.ERRMSG_MIN_HYPS_GT_MAX_HYPS_ERROR,
                Integer.toString(searchMaxHyps));
        searchReferenceStmt = searchArgs.searchReferenceStmt;
        searchComboFrame = searchArgs.comboFrame;
        if (searchReferenceStmt != null) {
            searchReferenceStmtChapter = bookManager
                .getChapter(searchReferenceStmt.getChapterNbr());
            searchReferenceStmtSection = bookManager
                .getSection(searchReferenceStmt.getSectionNbr());
        }
        if (searchThruChap != null) {
            int i = searchMaxSeq;
            if (searchThruSec != null)
                i = bookManager.getSectionMaxMObjSeq(searchThruSec);
            else
                i = bookManager.getChapterMaxMObjSeq(searchThruChap);
            if (++i < searchMaxSeq)
                searchMaxSeq = i;
        }
        if (searchFromChap != null) {
            int j = searchMinSeq;
            if (searchFromSec != null)
                j = bookManager.getSectionMinMObjSeq(searchFromSec);
            else
                j = bookManager.getChapterMinMObjSeq(searchFromChap);
            if (--j > searchMinSeq)
                searchMinSeq = j;
        }
        switch (searchChapSecHierarchyChoice) {
            case SearchOptionsConstants.CHAP_SEC_HIERARCHY_CHAP_DIRECT_ID:
                searchUseChapHierarchy = true;
                searchChapSecDependenciesArray = bookManager
                    .getDirectChapterDependencies(logicalSystem);
                break;

            case SearchOptionsConstants.CHAP_SEC_HIERARCHY_CHAP_INDIR_ID:
                searchUseChapHierarchy = true;
                searchChapSecDependenciesArray = bookManager
                    .getChapterDependencies(logicalSystem);
                break;

            case SearchOptionsConstants.CHAP_SEC_HIERARCHY_SEC_DIRECT_ID:
                searchUseSecHierarchy = true;
                searchChapSecDependenciesArray = bookManager
                    .getDirectSectionDependencies(logicalSystem);
                break;

            case SearchOptionsConstants.CHAP_SEC_HIERARCHY_SEC_INDIR_ID:
                searchUseSecHierarchy = true;
                searchChapSecDependenciesArray = bookManager
                    .getSectionDependencies(logicalSystem);
                break;
        }
        if (searchUseChapHierarchy) {
            if (searchReferenceStmt != null) {
                searchReferenceStmtDependencies = searchChapSecDependenciesArray[searchReferenceStmt
                    .getChapterNbr()];
                searchCombinedDependencies = (BitSet)searchReferenceStmtDependencies
                    .clone();
            }
            if (searchThruChap != null) {
                searchThruChapSecDependencies = searchChapSecDependenciesArray[searchThruChap
                    .getChapterNbr()];
                if (searchCombinedDependencies == null)
                    searchCombinedDependencies = (BitSet)searchThruChapSecDependencies
                        .clone();
                else
                    searchCombinedDependencies
                        .or(searchThruChapSecDependencies);
            }
        }
        else if (searchUseSecHierarchy) {
            if (searchReferenceStmt != null) {
                searchReferenceStmtDependencies = searchChapSecDependenciesArray[BookManager
                    .getOrigSectionNbr(searchReferenceStmt.getSectionNbr())];
                searchCombinedDependencies = (BitSet)searchReferenceStmtDependencies
                    .clone();
            }
            if (searchThruSec != null) {
                searchThruChapSecDependencies = searchChapSecDependenciesArray[BookManager
                    .getOrigSectionNbr(searchThruSec.getSectionNbr())];
                if (searchCombinedDependencies == null)
                    searchCombinedDependencies = (BitSet)searchThruChapSecDependencies
                        .clone();
                else
                    searchCombinedDependencies
                        .or(searchThruChapSecDependencies);
            }
        }
        if (searchOutput.searchReturnCode == 0 && searchArgs.stepSearchMode)
            compileStepSearchArgs();
        if (searchOutput.searchReturnCode == 0) {
            int k = 0;
            do {
                if (k >= searchForWhat.length)
                    break;
                if (!searchForWhat[k].equals("")) {
                    searchDataLines = new SearchDataLines(this);
                    break;
                }
                k++;
            } while (true);
        }
    }

    private void compileStepSearchArgs() {
        final DerivationStep derivationStep = searchArgs.stepSearchStmt;
        nbrDerivStepHyps = 0;
        for (final ProofStepStmt element : derivationStep.getHypList()) {
            if (element == null)
                continue;
            if (element.formulaParseTree == null)
                throw new IllegalArgumentException(
                    SearchConstants.ERRMSG_SEARCH_NULL_PARSE_TREE_1
                        + SearchConstants.DOT_STEP_CAPTION
                        + derivationStep.getStep());
            nbrDerivStepHyps++;
        }

        if (nbrDerivStepHyps == derivationStep.getHypNumber())
            derivStepHypWildcards = false;
        else
            derivStepHypWildcards = true;
        if (nbrDerivStepHyps > searchMaxHyps)
            searchArgs.maxHyps.storeArgError(searchOutput,
                Integer.toString(searchMaxHyps),
                SearchConstants.ERRMSG_MAX_HYPS_LT_STEP_HYPS_ERROR,
                Integer.toString(nbrDerivStepHyps));
        if (!derivStepHypWildcards)
            searchMaxHyps = nbrDerivStepHyps;
        if (nbrDerivStepHyps < searchMinHyps) {
            if (!derivStepHypWildcards)
                searchArgs.maxHyps.storeArgError(searchOutput,
                    Integer.toString(searchMinHyps),
                    SearchConstants.ERRMSG_MIN_HYPS_GT_STEP_HYPS_ERROR,
                    Integer.toString(nbrDerivStepHyps));
        }
        else
            searchMinHyps = nbrDerivStepHyps;
    }

    List<Assrt> sortedAssrtSearchList;
    SearchArgs searchArgs;
    SearchMgr searchMgr;
    BookManager bookManager;
    SearchOutput searchOutput;
    ProofAsst proofAsst;
    ProofAsstPreferences proofAsstPreferences;
    VerifyProofs verifyProofs;
    Cnst provableLogicStmtTyp;
    LogicalSystem logicalSystem;
    int nbrDerivStepHyps;
    boolean derivStepHypWildcards;
    boolean searchStepSearchMode;
    String searchOrSeparator;
    String searchSingleQuote;
    String searchDoubleQuote;
    int searchInWhatChoice[];
    int searchPartChoice[];
    int searchFormatChoice[];
    int searchOperChoice[];
    String[] searchForWhat;
    int searchBoolChoice[];
    String searchExclLabels;
    Pattern[] searchExclLabelsPattern;
    int searchMinProofRefs;
    int searchResultsChecked;
    int searchMaxTime;
    int searchMinHyps;
    int searchMaxExtResults;
    boolean searchSubstitutions;
    int searchMaxHyps;
    int searchMaxIncompHyps;
    boolean searchComments;
    int searchMaxResults;
    int searchPrevStepsChecked;
    boolean searchAutoSelect;
    int searchChapSecHierarchyChoice;
    boolean searchReuseDerivSteps;
    int searchStats;
    Chapter searchFromChap;
    Section searchFromSec;
    Chapter searchThruChap;
    Section searchThruSec;
    int searchOutputSortNbr;
    ScopeFrame searchComboFrame;
    Stmt searchReferenceStmt;
    Chapter searchReferenceStmtChapter;
    Section searchReferenceStmtSection;
    int searchMaxSeq;
    int searchMinSeq;
    BitSet[] searchChapSecDependenciesArray;
    boolean searchUseChapHierarchy;
    boolean searchUseSecHierarchy;
    BitSet searchReferenceStmtDependencies;
    BitSet searchThruChapSecDependencies;
    BitSet searchCombinedDependencies;
    SearchDataLines searchDataLines;
}
