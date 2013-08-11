//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchArgsComboBox.java  0.01 20/09/2012
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

public abstract class SearchArgsComboBox extends SearchArgsField {

    private String textValue;

    public SearchArgsComboBox(final int id, final String value) {
        super(id);
        textValue = value;
    }

    public SearchArgsComboBox(final int id) {
        this(id, SearchOptionsConstants.FIELD_ATTR[id].defaultText);
    }

    @Override
    public String get() {
        return textValue;
    }

    @Override
    public void set(final String value) {
        textValue = value;
    }

    @Override
    public void compile(final CompiledSearchArgs csa,
        final SearchMgr searchMgr, final BookManager bookManager,
        final SearchOutput searchOutput, final ProofAsst proofAsst,
        final ProofAsstPreferences proofAsstPreferences,
        final VerifyProofs verifyProofs, final Cnst cnst)
    {}

    public int findChoice(final String[] choices) {
        final String s = get().trim();
        for (int i = 0; i < choices.length; i++)
            if (choices[i].equals(s))
                return i;

        return -1;
    }

    public int getAndErrorIfBadChoice(final SearchOutput searchOutput,
        final String[] choices)
    {
        final int i = findChoice(choices);
        if (i == -1)
            storeArgError(searchOutput, get(),
                SearchConstants.ERRMSG_CHOICE_ERROR, "");
        return i;
    }
}
