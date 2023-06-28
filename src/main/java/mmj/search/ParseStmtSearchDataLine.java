//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * ParseStmtSearchDataLine.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import mmj.lang.ParseTree;

public class ParseStmtSearchDataLine extends SearchDataLine {

    public ParseStmtSearchDataLine(final CompiledSearchArgs csa, final int i,
        final SearchDataGetter searchDataGetter)
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
                .parseStmtUFOText(quotedSearchTerm.text, csa.searchMaxSeq,
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
        final int numHyps = quotedSearchTerm.parsedSearchTerm.varHypArray.length;
        for (final ParseTree element : assrtDataTreeArray)
            if (searchUnifier.unifyStmt(numHyps, searchOperChoice, element,
                quotedSearchTerm.parsedSearchTerm.parseTree))
                return true;

        return false;
    }

    private SearchUnifier searchUnifier;
}
