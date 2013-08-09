// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   InWhat.java

package mmj.search;

import mmj.lang.BookManager;
import mmj.lang.Cnst;
import mmj.pa.ProofAsst;
import mmj.pa.ProofAsstPreferences;
import mmj.verify.VerifyProofs;

// Referenced classes of package mmj.search:
//            SearchArgsComboBox, SearchOptionsConstants, CompiledSearchArgs, SearchOptionsFieldAttr, 
//            SearchMgr, SearchOutput

public class InWhat extends SearchArgsComboBox {

    public InWhat(final int i) {
        super(SearchOptionsConstants.IN_WHAT_FIELD_ID[i]);
    }

    @Override
    public void compile(final CompiledSearchArgs csa,
        final SearchMgr searchMgr, final BookManager bookManager,
        final SearchOutput searchOutput, final ProofAsst proofAsst,
        final ProofAsstPreferences proofAsstPreferences,
        final VerifyProofs verifyProofs, final Cnst cnst)
    {
        csa.searchInWhatChoice[SearchOptionsConstants.FIELD_ATTR[fieldId].rowIndex] = getAndErrorIfBadChoice(
            searchOutput, SearchOptionsConstants.IN_WHAT_VALUES);
    }
}