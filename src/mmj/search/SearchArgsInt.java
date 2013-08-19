//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchArgsInt.java  0.01 20/09/2012
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

public abstract class SearchArgsInt extends SearchArgsField {

    private String textValue;

    public SearchArgsInt(final int id, final String value) {
        super(id);
        set(value);
    }

    public SearchArgsInt(final int id) {
        this(id, SearchOptionsConstants.FIELD_ATTR[id].defaultText);
    }

    @Override
    public String get() {
        return textValue;
    }

    @Override
    public void set(final String value) {
        textValue = value.trim();
    }

    @Override
    public void compile(final CompiledSearchArgs csa,
        final SearchMgr searchMgr, final BookManager bookManager,
        final SearchOutput searchOutput, final ProofAsst proofAsst,
        final ProofAsstPreferences proofAsstPreferences,
        final VerifyProofs verifyProofs, final Cnst cnst)
    {}

    public int getAndErrorIfInvalid(final SearchOutput searchOutput) {
        try {
            final int value = Integer.parseInt(get());
            if (value < Integer.MAX_VALUE)
                return value;
        } catch (final NumberFormatException numberformatexception) {}
        storeArgError(searchOutput, get(),
            SearchConstants.ERRMSG_BAD_INT_ERROR,
            Integer.toString(Integer.MAX_VALUE));
        return 0;
    }

    public int getAndErrorIfNegative(final SearchOutput searchOutput) {
        final int i = getAndErrorIfInvalid(searchOutput);
        if (i < 0)
            storeArgError(searchOutput, Integer.toString(i),
                SearchConstants.ERRMSG_NEGATIVE_ERROR, "");
        return i;
    }
}
