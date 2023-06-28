//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * ThruSec.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import mmj.lang.*;
import mmj.pa.ProofAsst;
import mmj.pa.ProofAsstPreferences;
import mmj.verify.VerifyProofs;

public class ThruSec extends SearchArgsComboBox {

    public ThruSec() {
        super(46);
    }

    @Override
    public void compile(final CompiledSearchArgs csa,
        final SearchMgr searchMgr, final BookManager bookManager,
        final SearchOutput searchOutput, final ProofAsst proofAsst,
        final ProofAsstPreferences proofAsstPreferences,
        final VerifyProofs verifyProofs, final Cnst cnst)
    {
        final String s = get().trim();
        Section section = null;
        if (!s.equals("") && csa.searchThruChap != null) {
            section = bookManager.lookupSectionByChapterAndTitle(
                csa.searchThruChap, s, 4);
            if (section == null)
                storeArgError(searchOutput, s,
                    SearchConstants.ERRMSG_CHOICE_ERROR, "");
        }
        csa.searchThruSec = section;
    }
}
