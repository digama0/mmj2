// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SearchArgsCheckBox.java

package mmj.search;

import mmj.lang.BookManager;
import mmj.lang.Cnst;
import mmj.pa.ProofAsst;
import mmj.pa.ProofAsstPreferences;
import mmj.verify.VerifyProofs;

// Referenced classes of package mmj.search:
//            SearchArgsField, SearchOptionsConstants, SearchOptionsFieldAttr, CompiledSearchArgs,
//            SearchMgr, SearchOutput

public abstract class SearchArgsCheckBox extends SearchArgsField {

    public SearchArgsCheckBox(final int i, final String s) {
        super(i);
        set(s);
    }

    public SearchArgsCheckBox(final int i) {
        this(i, SearchOptionsConstants.FIELD_ATTR[i].defaultText);
    }

    @Override
    public String get() {
        return Boolean.toString(boolValue);
    }

    @Override
    public void set(final String s) {
        boolValue = Boolean.parseBoolean(s);
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

    private boolean boolValue;
}