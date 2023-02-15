//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchDataLines.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import mmj.lang.Assrt;

public class SearchDataLines {

    public SearchDataLines(final CompiledSearchArgs csa) {
        getter = null;
        line = null;
        getter = new SearchDataGetter();
        line = new SearchDataLine[4];
        for (int i = 0; i < line.length; i++)
            if (csa.searchForWhat[i].equals(""))
                line[i] = null;
            else
                line[i] = SearchDataLine.createSearchDataLine(csa, i, getter);

    }

    public boolean evaluate(final Assrt assrt, final CompiledSearchArgs csa) {
        getter.initForNextSearch(assrt);
        boolean flag = false;
        for (int j = 0; j < line.length; j++) {
            if (line[j] == null)
                continue;
            final int i = line[j].evaluate(csa);
            if (i == 0)
                continue;
            if (i > 0) {
                flag = true;
                if (!line[j].isBoolSetToAnd())
                    break;
                continue;
            }
            flag = false;
            if (line[j].isBoolSetToAnd())
                break;
        }

        return flag;
    }

    SearchDataGetter getter;
    SearchDataLine[] line;
}
