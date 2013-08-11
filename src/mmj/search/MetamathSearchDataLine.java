//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * MetamathSearchDataLine.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.util.regex.Pattern;

public class MetamathSearchDataLine extends SearchDataLine {

    public MetamathSearchDataLine(final CompiledSearchArgs csa, final int i,
        final SearchDataGetter searchDataGetter)
    {
        super(csa, i, searchDataGetter);
        if (csa.searchOutput.searchReturnCode == 0
            && searchPartChoice == SearchOptionsConstants.PART_COMMENTS_ID)
            convertSearchTermTextToLowerCase();
        if (csa.searchOutput.searchReturnCode == 0)
            convertMetamathSearchTermTextToRegex();
        if (csa.searchOutput.searchReturnCode == 0)
            compileSearchTermTextToRegex(csa);
    }

    @Override
    public void loadAssrtData() {
        loadAssrtDataStringArray();
    }

    @Override
    public boolean evaluateSearchTerm(final QuotedSearchTerm quotedSearchTerm,
        final CompiledSearchArgs csa)
    {
        if (searchPartChoice == SearchOptionsConstants.PART_LABELS_ID) {
            for (final String element : assrtDataStringArray)
                if (quotedSearchTerm.pattern.matcher(element).matches())
                    return true;

        }
        else
            for (final String element : assrtDataStringArray)
                if (quotedSearchTerm.pattern.matcher(element).find(0))
                    return true;
        return false;
    }

    protected void convertMetamathSearchTermTextToRegex() {
        for (int i = 0; i < quotedSearchTermList.size(); i++) {
            final QuotedSearchTerm quotedSearchTerm = quotedSearchTermList
                .get(i);
            final StringBuffer stringbuffer = new StringBuffer();
            int j = 0;
            StringBuffer stringbuffer1 = new StringBuffer();
            do {
                if (j >= quotedSearchTerm.text.length())
                    break;
                final char c = quotedSearchTerm.text.charAt(j++);
                if (c == '$' && j < quotedSearchTerm.text.length()) {
                    final char c1 = quotedSearchTerm.text.charAt(j);
                    if (c1 == '*' || c1 == '?') {
                        if (stringbuffer1.length() > 0) {
                            stringbuffer.append(Pattern.quote(stringbuffer1
                                .toString()));
                            stringbuffer1 = new StringBuffer();
                        }
                        stringbuffer.append('.');
                        stringbuffer.append(c1);
                        j++;
                        continue;
                    }
                }
                if ((c == '*' || c == '?')
                    && (searchPartChoice == SearchOptionsConstants.PART_LABELS_ID || searchPartChoice == SearchOptionsConstants.PART_LABELS_RPN_ID))
                {
                    if (stringbuffer1.length() > 0) {
                        stringbuffer.append(Pattern.quote(stringbuffer1
                            .toString()));
                        stringbuffer1 = new StringBuffer();
                    }
                    stringbuffer.append('.');
                    stringbuffer.append(c);
                }
                else
                    stringbuffer1.append(c);
            } while (true);
            if (stringbuffer1.length() > 0)
                stringbuffer.append(Pattern.quote(stringbuffer1.toString()));
            quotedSearchTerm.text = stringbuffer.toString();
        }

    }
}
