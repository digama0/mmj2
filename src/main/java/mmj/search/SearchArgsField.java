//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchArgsField.java  0.01 20/09/2012
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

public abstract class SearchArgsField {

    protected final int fieldId;

    public SearchArgsField(final int id) {
        fieldId = id;
    }

    public int getFieldId() {
        return fieldId;
    }

    public abstract String get();

    public abstract void set(String value);

    public abstract void compile(CompiledSearchArgs csa, SearchMgr searchMgr,
        BookManager bookManager, SearchOutput searchOutput,
        ProofAsst proofAsst, ProofAsstPreferences proofAsstPreferences,
        VerifyProofs verifyProofs, Cnst cnst);

    public void unsupportedFeatureError(final SearchOutput searchOutput) {
        storeArgError(searchOutput, get().trim(),
            SearchConstants.ERRMSG_UNSUPPORTED_FEATURE_ERROR, "");
    }

    public void requiredInputError(final SearchOutput searchOutput) {
        storeArgError(searchOutput, get().trim(),
            SearchConstants.ERRMSG_REQUIRED_INPUT_ERROR, "");
    }

    public void storeArgError(final SearchOutput searchOutput, final String s,
        final String s1, final String s2)
    {
        searchOutput.storeError(1, fieldId, SearchConstants.ERRMSG_ARG_ERROR_1
            + SearchOptionsConstants.FIELD_ATTR[fieldId].label.trim()
            + SearchConstants.ERRMSG_ARG_ERROR_2 + s
            + SearchConstants.ERRMSG_ARG_ERROR_3 + s1 + s2);
    }
}
