// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ThruSec.java

package mmj.search;

import mmj.lang.*;
import mmj.pa.ProofAsst;
import mmj.pa.ProofAsstPreferences;
import mmj.verify.VerifyProofs;

// Referenced classes of package mmj.search:
//            SearchArgsComboBox, CompiledSearchArgs, SearchMgr, SearchOutput

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