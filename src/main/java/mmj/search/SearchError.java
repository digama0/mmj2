//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchError.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

public class SearchError {

    final int returnCode;
    final int searchArgFieldId;
    final String message;

    public SearchError() {
        this(0, 0, "");
    }

    public SearchError(final int returnCode, final int searchArgFieldId,
        final String message)
    {
        this.returnCode = returnCode;
        this.searchArgFieldId = searchArgFieldId;
        this.message = SearchMgr.reformatMessage(message);
    }
}
