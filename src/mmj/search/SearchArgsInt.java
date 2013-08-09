// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SearchArgsInt.java

package mmj.search;

import mmj.lang.BookManager;
import mmj.lang.Cnst;
import mmj.pa.ProofAsst;
import mmj.pa.ProofAsstPreferences;
import mmj.verify.VerifyProofs;

// Referenced classes of package mmj.search:
//            SearchArgsField, SearchOutput, SearchOptionsConstants, SearchOptionsFieldAttr,
//            CompiledSearchArgs, SearchMgr

public abstract class SearchArgsInt extends SearchArgsField {

    public SearchArgsInt(final int i, final String s) {
        super(i);
        set(s);
    }

    public SearchArgsInt(final int i) {
        this(i, SearchOptionsConstants.FIELD_ATTR[i].defaultText);
    }

    @Override
    public String get() {
        return textValue;
    }

    @Override
    public void set(final String s) {
        textValue = s.trim();
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
            final int i = Integer.parseInt(get());
            if (i < 0x7fffffff)
                return i;
        } catch (final NumberFormatException numberformatexception) {}
        storeArgError(searchOutput, get(),
            SearchConstants.ERRMSG_BAD_INT_ERROR, Integer.toString(0x7fffffff));
        return 0;
    }

    public int getAndErrorIfNegative(final SearchOutput searchOutput) {
        final int i = getAndErrorIfInvalid(searchOutput);
        if (i < 0)
            storeArgError(searchOutput, Integer.toString(i),
                SearchConstants.ERRMSG_NEGATIVE_ERROR, "");
        return i;
    }

    private String textValue;
}