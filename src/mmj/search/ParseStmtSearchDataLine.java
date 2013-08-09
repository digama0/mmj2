// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ParseStmtSearchDataLine.java

package mmj.search;

import mmj.lang.ParseTree;

// Referenced classes of package mmj.search:
//            SearchDataLine, QuotedSearchTerm, CompiledSearchArgs, SearchDataGetter,
//            SearchMgr, SearchOutput, ParsedSearchTerm, SearchOptionsConstants,
//            SearchUnifier

public class ParseStmtSearchDataLine extends SearchDataLine {

    public ParseStmtSearchDataLine(final CompiledSearchArgs csa,
        final int i, final SearchDataGetter searchDataGetter)
    {
        super(csa, i, searchDataGetter);
        searchUnifier = null;
        searchUnifier = csa.searchMgr.getSearchUnifier();
        if (csa.searchOutput.searchReturnCode == 0)
            parseSearchTerms(csa);
    }

    @Override
    public void loadAssrtData() {
        loadAssrtDataTreeArray();
    }

    public void parseSearchTerms(final CompiledSearchArgs csa) {
        for (int i = 0; i < quotedSearchTermList.size(); i++) {
            final QuotedSearchTerm quotedSearchTerm = quotedSearchTermList
                .get(i);
            quotedSearchTerm.parsedSearchTerm = ParsedSearchTerm
                .parseStmtUFOText(quotedSearchTerm.text,
                    csa.searchMaxSeq,
                    csa.searchMgr);
            if (quotedSearchTerm.parsedSearchTerm.errorMessage != null) {
                quotedSearchTerm.errorMessage = SearchMgr
                    .reformatMessage(SearchConstants.ERROR_PARSE_STMT_TERM_COMPILE_1
                        + quotedSearchTerm.text
                        + SearchConstants.ERROR_PARSE_STMT_TERM_COMPILE_1_2
                        + quotedSearchTerm.parsedSearchTerm.errorMessage);
                csa.searchOutput.storeError(1,
                    SearchOptionsConstants.FOR_WHAT_FIELD_ID[rowIndex],
                    quotedSearchTerm.errorMessage);
                return;
            }
        }

    }

    @Override
    public boolean evaluateSearchTerm(final QuotedSearchTerm quotedSearchTerm,
        final CompiledSearchArgs csa)
    {
        final int i = quotedSearchTerm.parsedSearchTerm.varHypArray.length;
        for (final ParseTree element : assrtDataTreeArray)
            if (searchUnifier.unifyStmt(i, searchOperChoice, element,
                quotedSearchTerm.parsedSearchTerm.parseTree))
                return true;

        return false;
    }

    private SearchUnifier searchUnifier;
}