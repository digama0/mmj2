//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * RegExprSearchDataLine.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

public class RegExprSearchDataLine extends SearchDataLine {

    public RegExprSearchDataLine(final CompiledSearchArgs csa,
        final int i, final SearchDataGetter searchDataGetter)
    {
        super(csa, i, searchDataGetter);
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
        if (searchPartChoice == 2) {
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
}