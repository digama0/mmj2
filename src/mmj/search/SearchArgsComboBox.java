// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SearchArgsComboBox.java

package mmj.search;

import mmj.lang.BookManager;
import mmj.lang.Cnst;
import mmj.pa.ProofAsst;
import mmj.pa.ProofAsstPreferences;
import mmj.verify.VerifyProofs;

// Referenced classes of package mmj.search:
//            SearchArgsField, SearchOptionsConstants, SearchOptionsFieldAttr, CompiledSearchArgs,
//            SearchMgr, SearchOutput

public abstract class SearchArgsComboBox extends SearchArgsField {

    public SearchArgsComboBox(final int i, final String s) {
        super(i);
        textValue = s;
    }

    public SearchArgsComboBox(final int i) {
        this(i, SearchOptionsConstants.FIELD_ATTR[i].defaultText);
    }

    @Override
    public String get() {
        return textValue;
    }

    @Override
    public void set(final String s) {
        textValue = s;
    }

    @Override
    public void compile(final CompiledSearchArgs csa,
        final SearchMgr searchMgr, final BookManager bookManager,
        final SearchOutput searchOutput, final ProofAsst proofAsst,
        final ProofAsstPreferences proofAsstPreferences,
        final VerifyProofs verifyProofs, final Cnst cnst)
    {}

    public int findChoice(final String[] as) {
        final String s = get().trim();
        for (int i = 0; i < as.length; i++)
            if (as[i].equals(s))
                return i;

        return -1;
    }

    public int getAndErrorIfBadChoice(final SearchOutput searchOutput,
        final String[] as)
    {
        final int i = findChoice(as);
        if (i == -1)
            storeArgError(searchOutput, get(),
                SearchConstants.ERRMSG_CHOICE_ERROR, "");
        return i;
    }

    private String textValue;
}