// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SearchArgsField.java

package mmj.search;

import mmj.lang.BookManager;
import mmj.lang.Cnst;
import mmj.pa.ProofAsst;
import mmj.pa.ProofAsstPreferences;
import mmj.verify.VerifyProofs;

// Referenced classes of package mmj.search:
//            SearchOptionsConstants, SearchOptionsFieldAttr, SearchOutput, CompiledSearchArgs,
//            SearchMgr

public abstract class SearchArgsField {

    public SearchArgsField(final int i) {
        fieldId = i;
    }

    public int getFieldId() {
        return fieldId;
    }

    public abstract String get();

    public abstract void set(String s);

    public abstract void compile(CompiledSearchArgs csa,
        SearchMgr searchMgr, BookManager bookManager,
        SearchOutput searchOutput, ProofAsst proofAsst,
        ProofAsstPreferences proofAsstPreferences, VerifyProofs verifyProofs,
        Cnst cnst);

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

    protected final int fieldId;
}