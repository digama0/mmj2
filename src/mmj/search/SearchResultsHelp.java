// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SearchResultsHelp.java

package mmj.search;

import mmj.pa.AuxFrameGUI;
import mmj.pa.ProofAsstPreferences;

// Referenced classes of package mmj.search:
//            SearchMgr

public class SearchResultsHelp extends AuxFrameGUI {

    public SearchResultsHelp(final ProofAsstPreferences proofAsstPreferences) {
        super(proofAsstPreferences);
        setFrameValues();
    }

    private void setFrameValues() {
        setFrameText(SearchResultsConstants.GENERAL_HELP_INFO_TEXT);
        setFrameTitle(SearchResultsConstants.GENERAL_HELP_FRAME_TITLE);
        setFrameFontSize(proofAsstPreferences.getSearchMgr()
            .getSearchResultsFontSize());
        setFrameFont(proofAsstPreferences.getSearchMgr().getSearchResultsFont());
    }

    public static void main(final String[] args) {
        final SearchResultsHelp searchResultsHelp = new SearchResultsHelp(
            new ProofAsstPreferences());
        searchResultsHelp.showFrame(searchResultsHelp.buildFrame());
    }
}