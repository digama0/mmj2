//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchArgsCheckBox.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import mmj.lang.BookManager;
import mmj.lang.Cnst;
import mmj.pa.ProofAsst;
import mmj.pa.ProofAsstPreferences;
import mmj.verify.VerifyProofs;

public abstract class SearchArgsCheckBox extends SearchArgsField {

    private boolean boolValue;

    public SearchArgsCheckBox(final int id, final String value) {
        super(id);
        set(value);
    }

    public SearchArgsCheckBox(final int id) {
        this(id, SearchOptionsConstants.FIELD_ATTR[id].defaultText);
    }

    @Override
    public String get() {
        return Boolean.toString(boolValue);
    }

    @Override
    public void set(final String value) {
        boolValue = Boolean.parseBoolean(value);
    }

    public boolean getBool() {
        return boolValue;
    }

    @Override
    public void compile(final CompiledSearchArgs csa,
        final SearchMgr searchMgr, final BookManager bookManager,
        final SearchOutput searchOutput, final ProofAsst proofAsst,
        final ProofAsstPreferences proofAsstPreferences,
        final VerifyProofs verifyProofs, final Cnst cnst)
    {}
}
