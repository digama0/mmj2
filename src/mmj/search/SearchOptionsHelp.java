// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SearchOptionsHelp.java

package mmj.search;

import mmj.pa.AuxFrameGUI;
import mmj.pa.ProofAsstPreferences;

// Referenced classes of package mmj.search:
//            SearchMgr

public class SearchOptionsHelp extends AuxFrameGUI {

    public SearchOptionsHelp(final ProofAsstPreferences proofAsstPreferences) {
        super(proofAsstPreferences);
        setFrameValues();
    }

    private void setFrameValues() {
        setFrameText(SearchOptionsConstants.GENERAL_HELP_INFO_TEXT);
        setFrameTitle(SearchOptionsConstants.GENERAL_HELP_FRAME_TITLE);
        setFrameFontSize(proofAsstPreferences.getSearchMgr()
            .getSearchOptionsFontSize());
        setFrameFont(proofAsstPreferences.getSearchMgr().getSearchOptionsFont());
    }

    public static void main(final String[] args) {
        final SearchOptionsHelp searchOptionsHelp = new SearchOptionsHelp(
            new ProofAsstPreferences());
        searchOptionsHelp.showFrame(searchOptionsHelp.buildFrame());
    }
}