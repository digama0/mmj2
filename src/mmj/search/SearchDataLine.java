//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchDataLine.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import mmj.lang.*;

public abstract class SearchDataLine {

    public static SearchDataLine createSearchDataLine(
        final CompiledSearchArgs csa, final int i,
        final SearchDataGetter searchDataGetter)
    {
        if (csa.searchForWhat[i].equals(""))
            return null;
        switch (csa.searchFormatChoice[i]) {
            case SearchOptionsConstants.FORMAT_METAMATH_ID:
                return new MetamathSearchDataLine(csa, i, searchDataGetter);

            case SearchOptionsConstants.FORMAT_REG_EXPR_ID:
                return new RegExprSearchDataLine(csa, i, searchDataGetter);

            case SearchOptionsConstants.FORMAT_CHAR_STR_ID:
                return new CharStrSearchDataLine(csa, i, searchDataGetter);

            case SearchOptionsConstants.FORMAT_PARSE_EXPR_ID:
                return new ParseExprSearchDataLine(csa, i, searchDataGetter);

            case SearchOptionsConstants.FORMAT_PARSE_STMT_ID:
                return new ParseStmtSearchDataLine(csa, i, searchDataGetter);
        }
        throw new IllegalArgumentException(
            SearchConstants.ERROR_SEARCH_DATA_LINE_FORMAT_1
                + csa.searchFormatChoice[i]);
    }

    public SearchDataLine(final CompiledSearchArgs csa, final int i,
        final SearchDataGetter searchDataGetter)
    {
        rowIndex = i;
        this.searchDataGetter = searchDataGetter;
        searchInWhatChoice = csa.searchInWhatChoice[i];
        searchPartChoice = csa.searchPartChoice[i];
        searchFormatChoice = csa.searchFormatChoice[i];
        searchOperChoice = csa.searchOperChoice[i];
        searchForWhat = csa.searchForWhat[i];
        searchBoolChoice = csa.searchBoolChoice[i];
        wantsAxioms = false;
        wantsTheorems = false;
        wantsLogHyps = false;
        switch (searchInWhatChoice) {
            case SearchOptionsConstants.IN_WHAT_AEP_ID:
                wantsAxioms = true;
            case SearchOptionsConstants.IN_WHAT_EP_ID:
                wantsLogHyps = true;
            case SearchOptionsConstants.IN_WHAT_P_ID:
            case SearchOptionsConstants.IN_WHAT_EQ_ID:
                wantsTheorems = true;
                break;

            case SearchOptionsConstants.IN_WHAT_AE_ID:
                wantsAxioms = true;
            case SearchOptionsConstants.IN_WHAT_E_ID:
                wantsLogHyps = true;
                break;

            case SearchOptionsConstants.IN_WHAT_AP_ID:
                wantsTheorems = true;
            case SearchOptionsConstants.IN_WHAT_A_ID:
                wantsAxioms = true;
                break;

            default:
                throw new IllegalArgumentException(
                    SearchConstants.ERROR_SEARCH_DATA_LINE_IN_WHAT_1
                        + searchInWhatChoice);
        }
        initForNextSearch();
        quotedSearchTermList = new ArrayList<>();
        loadQuotedSearchTermList(csa);
    }

    public abstract void loadAssrtData();

    public abstract boolean evaluateSearchTerm(
        QuotedSearchTerm quotedSearchTerm, CompiledSearchArgs csa);

    public int evaluate(final CompiledSearchArgs csa) {
        if (!searchDataLineWantsThisAssrt())
            return 0;
        initForNextSearch();
        loadAssrtData();
        if (evaluateSearchTerms(csa))
            return !isOperSetToNot() ? 1 : -1;
        return !isOperSetToNot() ? -1 : 1;
    }

    private void initForNextSearch() {
        assrtDataStringArray = null;
        assrtDataTreeArray = null;
    }

    protected boolean searchDataLineWantsThisAssrt() {
        if (searchDataGetter.assrt instanceof Axiom) {
            if (wantsAxioms)
                return true;
        }
        else if (wantsTheorems)
            return true;
        return searchDataGetter.assrtHasLogHyps() && wantsLogHyps;
    }

    protected boolean evaluateSearchTerms(final CompiledSearchArgs csa) {
        boolean flag = false;
        int i = 0;
        do {
            if (i >= quotedSearchTermList.size())
                break;
            final QuotedSearchTerm quotedSearchTerm = quotedSearchTermList
                .get(i);
            flag = evaluateSearchTerm(quotedSearchTerm, csa);
            if (flag ? quotedSearchTerm.orIsSet : !quotedSearchTerm.orIsSet)
                break;
            i++;
        } while (true);
        return flag;
    }

    public boolean isOperSetToNot() {
        return searchOperChoice == 1;
    }

    public boolean isBoolSetToAnd() {
        return searchBoolChoice == 0;
    }

    protected void loadAssrtDataStringArray() {
        switch (searchPartChoice) {
            case SearchOptionsConstants.PART_FORMULAS_ID:
                assrtDataStringArray = searchDataGetter
                    .getFormulasAssrtDataStringArray(wantsAxioms, wantsLogHyps,
                        wantsTheorems);
                break;

            case SearchOptionsConstants.PART_COMMENTS_ID:
                assrtDataStringArray = searchDataGetter
                    .getCommentsAssrtDataStringArray(wantsAxioms, wantsLogHyps,
                        wantsTheorems);
                break;

            case SearchOptionsConstants.PART_LABELS_ID:
                assrtDataStringArray = searchDataGetter
                    .getLabelsAssrtDataStringArray(wantsAxioms, wantsLogHyps,
                        wantsTheorems);
                break;

            case SearchOptionsConstants.PART_LABELS_RPN_ID:
                assrtDataStringArray = searchDataGetter
                    .getLabelsRPNAssrtDataStringArray(wantsAxioms,
                        wantsLogHyps, wantsTheorems);
                break;

            default:
                throw new IllegalArgumentException(
                    SearchConstants.ERROR_SEARCH_DATA_LINE_PART_1
                        + searchPartChoice);
        }
    }

    protected void loadAssrtDataTreeArray() {
        switch (searchPartChoice) {
            case SearchOptionsConstants.PART_FORMULAS_ID:
                assrtDataTreeArray = searchDataGetter
                    .getFormulasAssrtDataTreeArray(wantsAxioms, wantsLogHyps,
                        wantsTheorems);
                assrtDataVarHypArray = searchDataGetter
                    .getFormulasAssrtDataVarHypArray(wantsAxioms, wantsLogHyps,
                        wantsTheorems);
                break;

            default:
                throw new IllegalArgumentException(
                    SearchConstants.ERROR_SEARCH_DATA_LINE_PART2_1
                        + searchPartChoice);
        }
    }

    protected void loadQuotedSearchTermList(final CompiledSearchArgs csa) {
        final String s = csa.searchSingleQuote;
        final String s1 = csa.searchDoubleQuote;
        final String s2 = csa.searchOrSeparator;
        final int i = 0;
        if (searchForWhat.startsWith(s, i) || searchForWhat.startsWith(s1, i)
            || searchForWhat.startsWith(s2, i))
        {
            QuotedSearchTerm quotedSearchTerm = new QuotedSearchTerm().first(
                searchForWhat, s, s1, s2);
            if (quotedSearchTerm == null)
                throw new IllegalArgumentException(
                    SearchConstants.ERROR_NO_SEARCH_TERMS_1);
            do {
                if (quotedSearchTerm == null)
                    break;
                if (quotedSearchTerm.errorMessage != null) {
                    csa.searchOutput.storeError(1,
                        SearchOptionsConstants.FOR_WHAT_FIELD_ID[rowIndex],
                        quotedSearchTerm.errorMessage);
                    break;
                }
                quotedSearchTermList.add(quotedSearchTerm);
                quotedSearchTerm = quotedSearchTerm.next(searchForWhat, s, s1,
                    s2);
            } while (true);
        }
        else {
            final QuotedSearchTerm quotedSearchTerm1 = new QuotedSearchTerm();
            quotedSearchTerm1.text = searchForWhat;
            quotedSearchTerm1.endQuoteIndex = searchForWhat.length() - 1;
            quotedSearchTermList.add(quotedSearchTerm1);
        }
        final QuotedSearchTerm quotedSearchTerm2 = quotedSearchTermList
            .get(quotedSearchTermList.size() - 1);
        if (quotedSearchTerm2.orIsSet && quotedSearchTerm2.errorMessage == null)
            quotedSearchTerm2.errorMessage = SearchMgr
                .reformatMessage(SearchConstants.ERROR_OR_AFTER_LAST_SEARCH_TERM_1);
    }

    protected void convertSearchTermTextToLowerCase() {
        for (int i = 0; i < quotedSearchTermList.size(); i++)
            quotedSearchTermList.get(i).convertSearchTermTextToLowerCase();

    }

    protected void compileSearchTermTextToRegex(final CompiledSearchArgs csa) {
        for (int i = 0; i < quotedSearchTermList.size(); i++) {
            final QuotedSearchTerm quotedSearchTerm = quotedSearchTermList
                .get(i);
            try {
                quotedSearchTerm.pattern = Pattern
                    .compile(quotedSearchTerm.text);
            } catch (final PatternSyntaxException patternsyntaxexception) {
                quotedSearchTerm.errorMessage = SearchMgr
                    .reformatMessage(SearchConstants.ERROR_SEARCH_TERM_REGEX_COMPILE_1
                        + quotedSearchTerm.text
                        + SearchConstants.ERROR_SEARCH_TERM_REGEX_COMPILE_1_2
                        + patternsyntaxexception.getMessage());
                csa.searchOutput.storeError(1,
                    SearchOptionsConstants.FOR_WHAT_FIELD_ID[rowIndex],
                    quotedSearchTerm.errorMessage);
                return;
            }
        }

    }

    int rowIndex;
    SearchDataGetter searchDataGetter;
    boolean wantsAxioms;
    boolean wantsLogHyps;
    boolean wantsTheorems;
    int searchInWhatChoice;
    int searchPartChoice;
    int searchFormatChoice;
    int searchOperChoice;
    String searchForWhat;
    int searchBoolChoice;
    List<QuotedSearchTerm> quotedSearchTermList;
    String[] assrtDataStringArray;
    ParseTree[] assrtDataTreeArray;
    VarHyp[][] assrtDataVarHypArray;
}
