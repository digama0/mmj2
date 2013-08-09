// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SearchOutput.java

package mmj.search;

import java.util.*;
import java.util.regex.Pattern;

import mmj.lang.*;

// Referenced classes of package mmj.search:
//            SearchError, QuotedSearchTerm, CompiledSearchArgs, SearchOptionsConstants,
//            SearchDataLines, SearchDataLine

public class SearchOutput {

    public SearchOutput(final String s) {
        startTimeMillis = 0L;
        endTimeMillis = 0L;
        elapsedMillis = 0L;
        statsInputAssrtListSize = 0;
        statsNbrInputAssrtGets = 0;
        statsNbrSelected = 0;
        statsNbrCompletedSearchResults = 0;
        statsNbrRejectGEMaxSeq = 0;
        statsNbrRejectGTHypIndex = 0;
        statsNbrRejectLTMinProofRefs = 0;
        statsNbrRejectLEMinSeq = 0;
        statsNbrRejectOtherExclCriteria = 0;
        statsNbrRejectExclLabels = 0;
        statsNbrRejectFailUnify = 0;
        statsNbrRejectFailSearchData = 0;
        startTimeMillis = System.currentTimeMillis();
        searchTitle = s;
        searchReturnCode = 0;
        searchErrorList = new ArrayList<SearchError>();
        step = "";
        sortedAssrtResultsList = new ArrayList<Assrt>(1);
        sortedAssrtResultsList.add(null);
        sortedAssrtScoreArray = new int[1];
        sortedAssrtScoreArray[0] = 0;
        refIndexArray = new int[1];
        refIndexArray[0] = 0;
        selectionArray = new String[1];
        selectionArray[0] = SearchResultsConstants.SELECTION_NO_SEARCH_RUN_YET_LITERAL;
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
        final int i = args.searchStats;
        if (i <= 0)
            return;
        messages.accumInfoMessage("SearchOutput.dumpStats(): " + searchTitle);
        if (i >= 5) {
            dumpChapSecHierarchy(args, bookManager, messages);
            return;
        }
        if (i >= 4) {
            dumpSearchResults(args, bookManager, messages);
            return;
        }
        if (i >= 3) {
            dumpSearchArgs(args, bookManager, messages);
            return;
        }
        if (i >= 2) {
            dumpDetailedStats(args, bookManager, messages);
            return;
        }
        if (i >= 1) {
            dumpSummaryStats(args, bookManager, messages);
            return;
        }
        else
            return;
    }

    public void dumpSummaryStats(final CompiledSearchArgs csa,
        final BookManager bookManager, final Messages messages)
    {
        messages.accumInfoMessage("SearchOutput.dumpSummaryStats():");
        messages.accumInfoMessage("    elapsedMillis                   = "
            + elapsedMillis);
        messages.accumInfoMessage("    statsInputAssrtListSize         = "
            + statsInputAssrtListSize);
        messages.accumInfoMessage("    statsNbrInputAssrtGets          = "
            + statsNbrInputAssrtGets);
        messages.accumInfoMessage("    statsNbrSelected                = "
            + statsNbrSelected);
        messages.accumInfoMessage("    statsNbrCompletedSearchResults  = "
            + statsNbrCompletedSearchResults);
        messages.accumInfoMessage("    searchReturnCode                = "
            + searchReturnCode);
        messages.accumInfoMessage("    searchErrorList.size()          = "
            + searchErrorList.size());
        if (searchErrorList.size() > 0) {
            messages.accumErrorMessage("    ***searchErrorList Contents***");
            for (int i = 0; i < searchErrorList.size(); i++) {
                final SearchError searchError = searchErrorList.get(i);
                messages.accumErrorMessage("        fieldId = "
                    + searchError.searchArgFieldId + " message = "
                    + searchError.message);
            }

            messages
                .accumErrorMessage("    ***END searchErrorList Contents***\n");
        }
        messages
            .accumInfoMessage("***END SearchOutput.dumpSummaryStats():***\n");
    }

    public void dumpDetailedStats(final CompiledSearchArgs csa,
        final BookManager bookManager, final Messages messages)
    {
        dumpSummaryStats(csa, bookManager, messages);
        messages.accumInfoMessage("SearchOutput.dumpDetailedStats():");
        messages.accumInfoMessage("    statsNbrRejectGEMaxSeq          = "
            + statsNbrRejectGEMaxSeq);
        messages.accumInfoMessage("    statsNbrRejectGTHypIndex        = "
            + statsNbrRejectGTHypIndex);
        messages.accumInfoMessage("    statsNbrRejectLTMinProofRefs    = "
            + statsNbrRejectLTMinProofRefs);
        messages.accumInfoMessage("    statsNbrRejectLEMinSeq          = "
            + statsNbrRejectLEMinSeq);
        messages.accumInfoMessage("    statsNbrRejectOtherExclCriteria = "
            + statsNbrRejectOtherExclCriteria);
        messages.accumInfoMessage("    statsNbrRejectExclLabels        = "
            + statsNbrRejectExclLabels);
        messages.accumInfoMessage("    statsNbrRejectFailUnify         = "
            + statsNbrRejectFailUnify);
        messages.accumInfoMessage("    statsNbrRejectFailSearchData    = "
            + statsNbrRejectFailSearchData);
        messages
            .accumInfoMessage("***END SearchOutput.dumpDetailedStats():***\n");
    }

    public void dumpSearchArgs(final CompiledSearchArgs args,
        final BookManager bookManager, final Messages messages)
    {
        dumpDetailedStats(args, bookManager, messages);
        messages.accumInfoMessage("SearchOutput.dumpSearchArgs():");
        messages.accumInfoMessage("     searchStepSearchMode         = "
            + args.searchStepSearchMode);
        messages.accumInfoMessage("     nbrDerivStepHyps             = "
            + args.nbrDerivStepHyps);
        messages.accumInfoMessage("     derivStepHypWildcards        = "
            + args.derivStepHypWildcards);
        messages.accumInfoMessage("     searchMinHyps                = "
            + args.searchMinHyps);
        messages.accumInfoMessage("     searchMaxHyps                = "
            + args.searchMaxHyps);
        messages.accumInfoMessage("     searchMinProofRefs           = "
            + args.searchMinProofRefs);
        messages.accumInfoMessage("     searchMinSeq                 = "
            + args.searchMinSeq);
        messages.accumInfoMessage("     searchMaxSeq                 = "
            + args.searchMaxSeq);
        final StringBuffer sb = new StringBuffer();
        String s = "";
        for (final Pattern element : args.searchExclLabelsPattern) {
            sb.append(s);
            sb.append(element.pattern());
            s = ",";
        }

        messages
            .accumInfoMessage("     searchExclLabels             = "
                + args.searchExclLabels + " (regex format: " + sb.toString()
                + " )");
        final Stmt stmt = args.searchReferenceStmt;
        if (stmt != null) {
            final int j = args.searchReferenceStmt.getChapterNbr();
            final int i1 = args.searchReferenceStmt.getSectionNbr();
            final Chapter chapter = bookManager.getChapter(j);
            final Section section = bookManager.getSection(i1);
            messages.accumInfoMessage("     searchReferenceStmt          = "
                + args.searchReferenceStmt.getLabel() + "  MObjSeq="
                + args.searchReferenceStmt.getSeq());
            messages.accumInfoMessage("         .chapterNbr              = "
                + j);
            messages.accumInfoMessage("             ..chapterTitle       = "
                + chapter.getChapterTitle());
            messages.accumInfoMessage("             ..minMObjSeq         = "
                + chapter.getMinMObjSeq());
            messages.accumInfoMessage("             ..maxMObjSeq         = "
                + chapter.getMaxMObjSeq());
            messages.accumInfoMessage("         .sectionNbr              = "
                + i1);
            messages.accumInfoMessage("             ..sectionTitle       = "
                + section.getSectionTitle());
            messages.accumInfoMessage("             ..minMObjSeq         = "
                + section.getMinMObjSeq());
            messages.accumInfoMessage("             ..maxMObjSeq         = "
                + section.getMaxMObjSeq());
        }
        else
            messages.accumInfoMessage("     searchReferenceStmt          =  ");
        messages
            .accumInfoMessage("     searchChapSecHierarchyChoice = "
                + args.searchChapSecHierarchyChoice
                + " : "
                +

                SearchOptionsConstants.CHAP_SEC_HIERARCHY_VALUES[args.searchChapSecHierarchyChoice]);
        Chapter chapter1 = args.searchFromChap;
        if (chapter1 != null) {
            final int k = chapter1.getChapterNbr();
            messages.accumInfoMessage("     searchFrom.chapterNbr        = "
                + k);
            messages.accumInfoMessage("         .chapterTitle            = "
                + chapter1.getChapterTitle());
            messages.accumInfoMessage("         .minMObjSeq              = "
                + chapter1.getMinMObjSeq());
            messages.accumInfoMessage("         .maxMObjSeq              = "
                + chapter1.getMaxMObjSeq());
        }
        else
            messages.accumInfoMessage("     searchFrom.chapterNbr        =  ");
        Section section1 = args.searchFromSec;
        if (section1 != null) {
            final int j1 = section1.getSectionNbr();
            messages.accumInfoMessage("    searchFrom.sectionNbr         = "
                + j1);
            messages.accumInfoMessage("         .sectionTitle            = "
                + section1.getSectionTitle());
            messages.accumInfoMessage("         .minMObjSeq              = "
                + section1.getMinMObjSeq());
            messages.accumInfoMessage("         .maxMObjSeq              = "
                + section1.getMaxMObjSeq());
        }
        else
            messages.accumInfoMessage("     searchFrom.sectionNbr        =  ");
        chapter1 = args.searchThruChap;
        if (chapter1 != null) {
            final int l = chapter1.getChapterNbr();
            messages.accumInfoMessage("     searchThru.chapterNbr        = "
                + l);
            messages.accumInfoMessage("         .chapterTitle            = "
                + chapter1.getChapterTitle());
            messages.accumInfoMessage("         .minMObjSeq              = "
                + chapter1.getMinMObjSeq());
            messages.accumInfoMessage("         .maxMObjSeq              = "
                + chapter1.getMaxMObjSeq());
        }
        else
            messages.accumInfoMessage("     searchFrom.chapterNbr        =  ");
        section1 = args.searchThruSec;
        if (section1 != null) {
            final int k1 = section1.getSectionNbr();
            messages.accumInfoMessage("    searchThru.sectionNbr         = "
                + k1);
            messages.accumInfoMessage("         .sectionTitle            = "
                + section1.getSectionTitle());
            messages.accumInfoMessage("         .minMObjSeq              = "
                + section1.getMinMObjSeq());
            messages.accumInfoMessage("         .maxMObjSeq              = "
                + section1.getMaxMObjSeq());
        }
        else
            messages.accumInfoMessage("     searchThru.sectionNbr        =  ");
        messages.accumInfoMessage("     searchMaxResults             = "
            + args.searchMaxResults);
        messages.accumInfoMessage("     searchOutputSortNbr          = "
            + args.searchOutputSortNbr);
        messages.accumInfoMessage("         .OUTPUT_SORT_VALUES      = " +

        SearchOptionsConstants.OUTPUT_SORT_VALUES[args.searchOutputSortNbr]);
        messages.accumInfoMessage("     searchMaxTime (seconds)      = "
            + args.searchMaxTime);
        messages.accumInfoMessage("     searchSubstitutions          = "
            + args.searchSubstitutions);
        messages.accumInfoMessage("     searchComments               = "
            + args.searchComments);
        messages.accumInfoMessage("     searchStats                  = "
            + args.searchStats);
        for (int l1 = 0; l1 < 4; l1++) {
            if (args.searchForWhat[l1].equals(""))
                continue;
            final String msg = "     searchDataLine #"
                + (l1 + 1)
                + "            = ["
                + SearchOptionsConstants.IN_WHAT_VALUES[args.searchInWhatChoice[l1]]
                + ":"
                + SearchOptionsConstants.PART_VALUES[args.searchPartChoice[l1]]
                + ":"
                + SearchOptionsConstants.FORMAT_VALUES[args.searchFormatChoice[l1]]
                + ":"
                + SearchOptionsConstants.OPER_VALUES[args.searchOperChoice[l1]]
                + ":" + args.searchForWhat[l1] + ":"
                + SearchOptionsConstants.BOOL_VALUES[args.searchBoolChoice[l1]]
                + "]";
            messages.accumInfoMessage(msg);
            String str = "     ....SearchTerms:";
            final List<QuotedSearchTerm> arraylist = args.searchDataLines.line[l1].quotedSearchTermList;
            for (int i2 = 0; i2 < arraylist.size(); i2++) {
                final QuotedSearchTerm quotedSearchTerm = arraylist.get(i2);
                if (quotedSearchTerm.pattern != null) {
                    str += " \"";
                    str += quotedSearchTerm.pattern.pattern();
                    str += "\"";
                }
                else {
                    str += " ";
                    str += quotedSearchTerm.quoteString;
                    str += quotedSearchTerm.text;
                    str += quotedSearchTerm.quoteString;
                }
            }

            messages.accumInfoMessage(str);
        }

        messages.accumInfoMessage("***END SearchOutput.dumpSearchArgs():***\n");
    }
    public void dumpSearchResults(final CompiledSearchArgs csa,
        final BookManager bookManager, final Messages messages)
    {
        dumpSearchArgs(csa, bookManager, messages);
        messages.accumInfoMessage("SearchOutput.dumpSearchResults():");
        for (final String element : selectionArray)
            messages.accumInfoMessage(element);

        messages
            .accumInfoMessage("***END SearchOutput.dumpSearchResults():***\n");
    }

    public void dumpChapSecHierarchy(final CompiledSearchArgs csa,
        final BookManager bookManager, final Messages messages)
    {
        dumpSearchResults(csa, bookManager, messages);
        messages.accumInfoMessage("SearchOutput.dumpChapSecHierarchy():");
        messages
            .accumInfoMessage("     searchChapSecHierarchyChoice = "
                + csa.searchChapSecHierarchyChoice
                + " : "
                +

                SearchOptionsConstants.CHAP_SEC_HIERARCHY_VALUES[csa.searchChapSecHierarchyChoice]);
        final BitSet bitset = csa.searchCombinedDependencies;
        if (bitset != null)
            for (int j = bitset.nextSetBit(0); j >= 0; j = bitset
                .nextSetBit(j + 1))
            {
                String s;
                int i;
                String s1;
                if (csa.searchUseChapHierarchy) {
                    s = "Chap ";
                    i = j;
                    final Chapter chapter = bookManager.getChapter(i);
                    s1 = chapter.getChapterTitle();
                }
                else {
                    s = "Sec  ";
                    i = BookManager.convertOrigSectionNbr(j);
                    final Section section = bookManager.getSection(i);
                    s1 = section.getSectionTitle();
                }
                messages.accumInfoMessage("          " + s + i + " : " + s1);
            }
        messages
            .accumInfoMessage("***END SearchOutput.dumpChapSecHierarchy():***\n");
    }

    public SearchError getFirstError() {
        if (searchErrorList.size() > 0)
            return searchErrorList.get(0);
        else
            return null;
    }

    public void storeError(final SearchError searchError) {
        searchErrorList.add(searchError);
        searchReturnCode = searchError.returnCode;
    }

    public void storeError(final int i, final int j, final String s) {
        final SearchError searchError = new SearchError(i, j, s);
        searchErrorList.add(searchError);
        searchReturnCode = searchError.returnCode;
    }

    String searchTitle;
    int searchReturnCode;
    List<SearchError> searchErrorList;
    String step;
    List<Assrt> sortedAssrtResultsList;
    int[] sortedAssrtScoreArray;
    int[] refIndexArray;
    String[] selectionArray;
    long startTimeMillis;
    long endTimeMillis;
    long elapsedMillis;
    int statsInputAssrtListSize;
    int statsNbrInputAssrtGets;
    int statsNbrSelected;
    int statsNbrCompletedSearchResults;
    int statsNbrRejectGEMaxSeq;
    int statsNbrRejectGTHypIndex;
    int statsNbrRejectLTMinProofRefs;
    int statsNbrRejectLEMinSeq;
    int statsNbrRejectOtherExclCriteria;
    int statsNbrRejectExclLabels;
    int statsNbrRejectFailUnify;
    int statsNbrRejectFailSearchData;
}