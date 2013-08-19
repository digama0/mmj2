//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * QuotedSearchTerm.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.util.regex.Pattern;

public class QuotedSearchTerm {

    public QuotedSearchTerm() {
        quoteString = "";
        startQuoteIndex = 0;
        endQuoteIndex = 0;
        text = "";
        errorMessage = null;
        orIsSet = false;
        pattern = null;
        parsedSearchTerm = null;
    }

    public QuotedSearchTerm(final String s, final int i) {
        quoteString = s;
        startQuoteIndex = i;
        endQuoteIndex = -1;
        text = null;
        errorMessage = null;
        orIsSet = false;
        pattern = null;
        parsedSearchTerm = null;
    }

    public QuotedSearchTerm first(final String s, final String s1,
        final String s2, final String s3)
    {
        final QuotedSearchTerm quotedSearchTerm = new QuotedSearchTerm();
        final QuotedSearchTerm quotedSearchTerm1 = quotedSearchTerm.next(s, s1,
            s2, s3);
        if (quotedSearchTerm.orIsSet)
            quotedSearchTerm.errorMessage = SearchMgr
                .reformatMessage(SearchConstants.ERROR_OR_BEFORE_FIRST_SEARCH_TERM_1);
        if (quotedSearchTerm.errorMessage != null)
            return quotedSearchTerm;
        else
            return quotedSearchTerm1;
    }

    public QuotedSearchTerm next(final String s, final String s1,
        final String s2, final String s3)
    {
        QuotedSearchTerm quotedSearchTerm = null;
        int i;
        for (i = endQuoteIndex + quoteString.length() - 1; i < s.length(); i++)
        {
            if (s.charAt(i) == ' ')
                continue;
            if (!s.startsWith(s3, i))
                break;
            if (orIsSet) {
                errorMessage = SearchMgr
                    .reformatMessage(SearchConstants.ERROR_TWO_ORS_1);
                break;
            }
            orIsSet = true;
            i += s3.length() - 1;
        }

        if (i < s.length())
            if (s.startsWith(s1, i)) {
                quotedSearchTerm = new QuotedSearchTerm(s1, i);
                quotedSearchTerm.load(s);
            }
            else if (s.startsWith(s2, i)) {
                quotedSearchTerm = new QuotedSearchTerm(s2, i);
                quotedSearchTerm.load(s);
            }
            else {
                quotedSearchTerm = new QuotedSearchTerm(quoteString, i);
                quotedSearchTerm.errorMessage = SearchMgr
                    .reformatMessage(SearchConstants.ERROR_MISSING_STARTING_QUOTE_1);
            }
        return quotedSearchTerm;
    }
    public QuotedSearchTerm load(final String s) {
        for (endQuoteIndex = startQuoteIndex + quoteString.length(); endQuoteIndex < s
            .length() && !s.startsWith(quoteString, endQuoteIndex); endQuoteIndex++);
        if (endQuoteIndex >= s.length()) {
            errorMessage = SearchMgr
                .reformatMessage(SearchConstants.ERROR_MISSING_END_QUOTE_1);
            endQuoteIndex = 0;
        }
        else {
            text = s.substring(startQuoteIndex + quoteString.length(),
                endQuoteIndex);
            if (text.length() == 0)
                errorMessage = SearchMgr
                    .reformatMessage(SearchConstants.ERROR_EMPTY_SEARCH_TERM_1);
        }
        return this;
    }

    public void convertSearchTermTextToLowerCase() {
        text = text.toLowerCase();
    }

    public String quoteString;
    public int startQuoteIndex;
    public int endQuoteIndex;
    public String text;
    public String errorMessage;
    public boolean orIsSet;
    Pattern pattern;
    public ParsedSearchTerm parsedSearchTerm;
}
