//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchOutput.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.util.*;
import java.util.regex.Pattern;

import mmj.lang.*;

public class SearchOutput {

    String searchTitle;
    int searchReturnCode = 0;
    List<SearchError> searchErrorList = new ArrayList<>();
    String step = "";
    List<Assrt> sortedAssrtResultsList = new ArrayList<>(
        Arrays.asList((Assrt)null));
    int[] sortedAssrtScoreArray = new int[]{0};
    String[] selectionArray = new String[]{SearchResultsConstants.SELECTION_NO_SEARCH_RUN_YET_LITERAL};
    long startTimeMillis = 0;
    long endTimeMillis = 0;
    long elapsedMillis = 0;
    int statsInputAssrtListSize = 0;
    int statsNbrInputAssrtGets = 0;
    int statsNbrSelected = 0;
    int statsNbrCompletedSearchResults = 0;
    int statsNbrRejectGEMaxSeq = 0;
    int statsNbrRejectGTHypIndex = 0;
    int statsNbrRejectLTMinProofRefs = 0;
    int statsNbrRejectLEMinSeq = 0;
    int statsNbrRejectOtherExclCriteria = 0;
    int statsNbrRejectExclLabels = 0;
    int statsNbrRejectFailUnify = 0;
    int statsNbrRejectFailSearchData = 0;

    public SearchOutput(final String searchTitle) {
        startTimeMillis = System.currentTimeMillis();
        this.searchTitle = searchTitle;
    }

    public void finalize(final CompiledSearchArgs csa,
        final BookManager bookManager, final Messages messages)
    {
        endTimeMillis = System.currentTimeMillis();
        elapsedMillis = endTimeMillis - startTimeMillis;
        if (csa.searchStats > 0)
            dumpStats(csa, bookManager, messages);
    }

    public void dumpStats(final CompiledSearchArgs args,
        final BookManager bookManager, final Messages messages)
    {
        if (args.searchStats <= 0)
            return;
        messages.accumInfoMessage("SearchOutput.dumpStats(): " + searchTitle);
        if (args.searchStats >= 5) {
            dumpChapSecHierarchy(args, bookManager, messages);
            return;
        }
        if (args.searchStats >= 4) {
            dumpSearchResults(args, bookManager, messages);
            return;
        }
        if (args.searchStats >= 3) {
            dumpSearchArgs(args, bookManager, messages);
            return;
        }
        if (args.searchStats >= 2) {
            dumpDetailedStats(args, bookManager, messages);
            return;
        }
        if (args.searchStats >= 1) {
            dumpSummaryStats(args, bookManager, messages);
            return;
        }
        return;
    }

    public void dumpSummaryStats(final CompiledSearchArgs csa,
        final BookManager bookManager, final Messages m)
    {
        m.accumInfoMessage("SearchOutput.dumpSummaryStats():");
        m.accumInfoMessage("    elapsedMillis                   = "
            + elapsedMillis);
        m.accumInfoMessage("    statsInputAssrtListSize         = "
            + statsInputAssrtListSize);
        m.accumInfoMessage("    statsNbrInputAssrtGets          = "
            + statsNbrInputAssrtGets);
        m.accumInfoMessage("    statsNbrSelected                = "
            + statsNbrSelected);
        m.accumInfoMessage("    statsNbrCompletedSearchResults  = "
            + statsNbrCompletedSearchResults);
        m.accumInfoMessage("    searchReturnCode                = "
            + searchReturnCode);
        m.accumInfoMessage("    searchErrorList.size()          = "
            + searchErrorList.size());
        if (!searchErrorList.isEmpty()) {
            m.accumErrorMessage("    ***searchErrorList Contents***");
            for (final SearchError error : searchErrorList)
                m.accumErrorMessage("        fieldId = "
                    + error.searchArgFieldId + " message = " + error.message);

            m.accumErrorMessage("    ***END searchErrorList Contents***\n");
        }
        m.accumInfoMessage("***END SearchOutput.dumpSummaryStats():***\n");
    }

    public void dumpDetailedStats(final CompiledSearchArgs csa,
        final BookManager bookManager, final Messages m)
    {
        dumpSummaryStats(csa, bookManager, m);
        m.accumInfoMessage("SearchOutput.dumpDetailedStats():");
        m.accumInfoMessage("    statsNbrRejectGEMaxSeq          = "
            + statsNbrRejectGEMaxSeq);
        m.accumInfoMessage("    statsNbrRejectGTHypIndex        = "
            + statsNbrRejectGTHypIndex);
        m.accumInfoMessage("    statsNbrRejectLTMinProofRefs    = "
            + statsNbrRejectLTMinProofRefs);
        m.accumInfoMessage("    statsNbrRejectLEMinSeq          = "
            + statsNbrRejectLEMinSeq);
        m.accumInfoMessage("    statsNbrRejectOtherExclCriteria = "
            + statsNbrRejectOtherExclCriteria);
        m.accumInfoMessage("    statsNbrRejectExclLabels        = "
            + statsNbrRejectExclLabels);
        m.accumInfoMessage("    statsNbrRejectFailUnify         = "
            + statsNbrRejectFailUnify);
        m.accumInfoMessage("    statsNbrRejectFailSearchData    = "
            + statsNbrRejectFailSearchData);
        m.accumInfoMessage("***END SearchOutput.dumpDetailedStats():***\n");
    }

    public void dumpSearchArgs(final CompiledSearchArgs args,
        final BookManager bookManager, final Messages m)
    {
        dumpDetailedStats(args, bookManager, m);
        m.accumInfoMessage("SearchOutput.dumpSearchArgs():");
        m.accumInfoMessage("     searchStepSearchMode         = "
            + args.searchStepSearchMode);
        m.accumInfoMessage("     nbrDerivStepHyps             = "
            + args.nbrDerivStepHyps);
        m.accumInfoMessage("     derivStepHypWildcards        = "
            + args.derivStepHypWildcards);
        m.accumInfoMessage("     searchMinHyps                = "
            + args.searchMinHyps);
        m.accumInfoMessage("     searchMaxHyps                = "
            + args.searchMaxHyps);
        m.accumInfoMessage("     searchMinProofRefs           = "
            + args.searchMinProofRefs);
        m.accumInfoMessage("     searchMinSeq                 = "
            + args.searchMinSeq);
        m.accumInfoMessage("     searchMaxSeq                 = "
            + args.searchMaxSeq);
        String s = "";
        String delim = "";
        for (final Pattern p : args.searchExclLabelsPattern) {
            s += delim + p.pattern();
            delim = ",";
        }

        m.accumInfoMessage("     searchExclLabels             = "
            + args.searchExclLabels + " (regex format: " + s + " )");
        final Stmt stmt = args.searchReferenceStmt;
        if (stmt != null) {
            final int chapterNbr = args.searchReferenceStmt.getChapterNbr();
            final int sectionNbr = args.searchReferenceStmt.getSectionNbr();
            final Chapter chapter = bookManager.getChapter(chapterNbr);
            final Section section = bookManager.getSection(sectionNbr);
            m.accumInfoMessage("     searchReferenceStmt          = "
                + args.searchReferenceStmt.getLabel() + "  MObjSeq="
                + args.searchReferenceStmt.getSeq());
            m.accumInfoMessage("         .chapterNbr              = "
                + chapterNbr);
            m.accumInfoMessage("             ..chapterTitle       = "
                + chapter.getChapterTitle());
            m.accumInfoMessage("             ..minMObjSeq         = "
                + chapter.getMinMObjSeq());
            m.accumInfoMessage("             ..maxMObjSeq         = "
                + chapter.getMaxMObjSeq());
            m.accumInfoMessage("         .sectionNbr              = "
                + sectionNbr);
            m.accumInfoMessage("             ..sectionTitle       = "
                + section.getSectionTitle());
            m.accumInfoMessage("             ..minMObjSeq         = "
                + section.getMinMObjSeq());
            m.accumInfoMessage("             ..maxMObjSeq         = "
                + section.getMaxMObjSeq());
        }
        else
            m.accumInfoMessage("     searchReferenceStmt          =  ");
        m.accumInfoMessage("     searchChapSecHierarchyChoice = "
            + args.searchChapSecHierarchyChoice
            + " : "
            +

            SearchOptionsConstants.CHAP_SEC_HIERARCHY_VALUES[args.searchChapSecHierarchyChoice]);
        Chapter chapter = args.searchFromChap;
        if (chapter != null) {
            final int chapterNbr = chapter.getChapterNbr();
            m.accumInfoMessage("     searchFrom.chapterNbr        = "
                + chapterNbr);
            m.accumInfoMessage("         .chapterTitle            = "
                + chapter.getChapterTitle());
            m.accumInfoMessage("         .minMObjSeq              = "
                + chapter.getMinMObjSeq());
            m.accumInfoMessage("         .maxMObjSeq              = "
                + chapter.getMaxMObjSeq());
        }
        else
            m.accumInfoMessage("     searchFrom.chapterNbr        =  ");
        Section section = args.searchFromSec;
        if (section != null) {
            final int sectionNbr = section.getSectionNbr();
            m.accumInfoMessage("    searchFrom.sectionNbr         = "
                + sectionNbr);
            m.accumInfoMessage("         .sectionTitle            = "
                + section.getSectionTitle());
            m.accumInfoMessage("         .minMObjSeq              = "
                + section.getMinMObjSeq());
            m.accumInfoMessage("         .maxMObjSeq              = "
                + section.getMaxMObjSeq());
        }
        else
            m.accumInfoMessage("     searchFrom.sectionNbr        =  ");
        chapter = args.searchThruChap;
        if (chapter != null) {
            final int chapterNbr = chapter.getChapterNbr();
            m.accumInfoMessage("     searchThru.chapterNbr        = "
                + chapterNbr);
            m.accumInfoMessage("         .chapterTitle            = "
                + chapter.getChapterTitle());
            m.accumInfoMessage("         .minMObjSeq              = "
                + chapter.getMinMObjSeq());
            m.accumInfoMessage("         .maxMObjSeq              = "
                + chapter.getMaxMObjSeq());
        }
        else
            m.accumInfoMessage("     searchFrom.chapterNbr        =  ");
        section = args.searchThruSec;
        if (section != null) {
            final int sectionNbr = section.getSectionNbr();
            m.accumInfoMessage("    searchThru.sectionNbr         = "
                + sectionNbr);
            m.accumInfoMessage("         .sectionTitle            = "
                + section.getSectionTitle());
            m.accumInfoMessage("         .minMObjSeq              = "
                + section.getMinMObjSeq());
            m.accumInfoMessage("         .maxMObjSeq              = "
                + section.getMaxMObjSeq());
        }
        else
            m.accumInfoMessage("     searchThru.sectionNbr        =  ");
        m.accumInfoMessage("     searchMaxResults             = "
            + args.searchMaxResults);
        m.accumInfoMessage("     searchOutputSortNbr          = "
            + args.searchOutputSortNbr);
        m.accumInfoMessage("         .OUTPUT_SORT_VALUES      = " +

        SearchOptionsConstants.OUTPUT_SORT_VALUES[args.searchOutputSortNbr]);
        m.accumInfoMessage("     searchMaxTime (seconds)      = "
            + args.searchMaxTime);
        m.accumInfoMessage("     searchSubstitutions          = "
            + args.searchSubstitutions);
        m.accumInfoMessage("     searchComments               = "
            + args.searchComments);
        m.accumInfoMessage("     searchStats                  = "
            + args.searchStats);
        for (int i = 0; i < 4; i++) {
            if (args.searchForWhat[i].isEmpty())
                continue;
            m.accumInfoMessage("     searchDataLine #"
                + (i + 1)
                + "            = ["
                + SearchOptionsConstants.IN_WHAT_VALUES[args.searchInWhatChoice[i]]
                + ":"
                + SearchOptionsConstants.PART_VALUES[args.searchPartChoice[i]]
                + ":"
                + SearchOptionsConstants.FORMAT_VALUES[args.searchFormatChoice[i]]
                + ":"
                + SearchOptionsConstants.OPER_VALUES[args.searchOperChoice[i]]
                + ":" + args.searchForWhat[i] + ":"
                + SearchOptionsConstants.BOOL_VALUES[args.searchBoolChoice[i]]
                + "]");
            String msg = "     ....SearchTerms:";
            for (final QuotedSearchTerm term : args.searchDataLines.line[i].quotedSearchTermList)
                if (term.pattern != null)
                    msg += " \"" + term.pattern.pattern() + "\"";
                else
                    msg += " " + term.quoteString + term.text
                        + term.quoteString;

            m.accumInfoMessage(msg);
        }

        m.accumInfoMessage("***END SearchOutput.dumpSearchArgs():***\n");
    }
    public void dumpSearchResults(final CompiledSearchArgs csa,
        final BookManager bookManager, final Messages m)
    {
        dumpSearchArgs(csa, bookManager, m);
        m.accumInfoMessage("SearchOutput.dumpSearchResults():");
        for (final String element : selectionArray)
            m.accumInfoMessage(element);

        m.accumInfoMessage("***END SearchOutput.dumpSearchResults():***\n");
    }

    public void dumpChapSecHierarchy(final CompiledSearchArgs csa,
        final BookManager bookManager, final Messages m)
    {
        dumpSearchResults(csa, bookManager, m);
        m.accumInfoMessage("SearchOutput.dumpChapSecHierarchy():");
        m.accumInfoMessage("     searchChapSecHierarchyChoice = "
            + csa.searchChapSecHierarchyChoice
            + " : "
            + SearchOptionsConstants.CHAP_SEC_HIERARCHY_VALUES[csa.searchChapSecHierarchyChoice]);
        final BitSet dependencies = csa.searchCombinedDependencies;
        if (dependencies != null)
            for (int i = dependencies.nextSetBit(0); i >= 0; i = dependencies
                .nextSetBit(i + 1))
                if (csa.searchUseChapHierarchy)
                    m.accumInfoMessage("          Chap " + i + " : "
                        + bookManager.getChapter(i).getChapterTitle());
                else {
                    final int secNbr = BookManager.convertOrigSectionNbr(i);
                    m.accumInfoMessage("          Sec " + secNbr + " : "
                        + bookManager.getSection(secNbr).getSectionTitle());
                }
        m.accumInfoMessage("***END SearchOutput.dumpChapSecHierarchy():***\n");
    }

    public SearchError getFirstError() {
        return searchErrorList.isEmpty() ? null : searchErrorList.get(0);
    }

    public void storeError(final SearchError error) {
        searchErrorList.add(error);
        searchReturnCode = error.returnCode;
    }

    public void storeError(final int returnCode, final int searchArgFieldId,
        final String message)
    {
        final SearchError error = new SearchError(returnCode, searchArgFieldId,
            message);
        searchErrorList.add(error);
        searchReturnCode = error.returnCode;
    }
}
