//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchOptionsHelp.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import mmj.pa.AuxFrameGUI;
import mmj.pa.ProofAsstPreferences;

public class SearchOptionsHelp extends AuxFrameGUI {

    public SearchOptionsHelp(final ProofAsstPreferences proofAsstPreferences) {
        super(proofAsstPreferences);
        setFrameValues();
    }

    private void setFrameValues() {
        setFrameText(SearchOptionsConstants.GENERAL_HELP_INFO_TEXT);
        setFrameTitle(SearchOptionsConstants.GENERAL_HELP_FRAME_TITLE);
        setFrameFontSize(
            proofAsstPreferences.getSearchMgr().getSearchOptionsFontSize());
        setFrameFont(
            proofAsstPreferences.getSearchMgr().getSearchOptionsFont());
    }

    public static void main(final String[] args) {
        final SearchOptionsHelp searchOptionsHelp = new SearchOptionsHelp(
            new ProofAsstPreferences());
        searchOptionsHelp.showFrame(searchOptionsHelp.buildFrame());
    }
}
