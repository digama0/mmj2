// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SearchResultsFrame.java

package mmj.search;

import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.*;

import mmj.pa.ProofAsstPreferences;

// Referenced classes of package mmj.search:
//            SearchResultsScrnMap, SearchResultsButtonHandler, SearchArgs, SearchMgr

public class SearchResultsFrame extends JFrame implements
    SearchResultsButtonHandler
{
    private final SearchMgr searchMgr;
    private SearchResultsScrnMap searchResultsScrnMap;
    private Box frameBox;
    private boolean shown;

    public SearchResultsFrame(final SearchMgr searchMgr) {
        shown = false;
        this.searchMgr = searchMgr;
        buildFrame();
    }

    void increaseFontSize() {
        searchMgr.increaseSearchResultsFontSize();
        setFontSizeDisplay();
    }

    void decreaseFontSize() {
        searchMgr.decreaseSearchResultsFontSize();
        setFontSizeDisplay();
    }

    int getSelectedIndex() {
        return searchResultsScrnMap.getSelectedIndex();
    }

    void showPopupErrorMessage(final String s) {
        processLocalRequestGUI(new ShowPopupErrorMessage(this, s));
    }

    void reshowSearchResults() {
        if (getShown())
            processLocalRequestGUI(new ReshowSearchResults());
        else
            showSearchResults();
    }

    void showSearchResults() {
        showSearchResultsDisplay();
    }

    void mockupSearchResults() {
        processLocalRequestGUI(new ShowSearchResults(searchMgr,
            searchResultsScrnMap));
    }

    void processLocalRequestGUI(final RequestGUI requestgui) {
        final UpdateGUI updategui = new UpdateGUI(requestgui);
        EventQueue.invokeLater(updategui);
    }

    public void searchResultsButtonPressed(final int i) {
        switch (i) {
            case 0: // '\0'
                searchMgr.searchResultsApplyButtonPressed();
                break;

            case 1: // '\001'
                searchMgr.searchResultsCancelButtonPressed();
                break;

            case 2: // '\002'
                searchMgr.searchResultsPAButtonPressed();
                break;

            case 3: // '\003'
                searchMgr.searchResultsSOButtonPressed();
                break;

            case 4: // '\004'
                searchMgr.showSearchResultsHelp();
                break;

            case 5: // '\005'
                searchMgr.searchResultsPlusButtonPressed();
                break;

            case 6: // '\006'
                searchMgr.searchResultsMinusButtonPressed();
                break;

            default:
                throw new IllegalArgumentException(
                    SearchResultsConstants.ERRMSG_UNRECOGNIZED_BUTTON_ID_1 + i);
        }
    }

    private void buildFrame() {
        setTitle(buildTitle());
        setDefaultCloseOperation(2);
        searchResultsScrnMap = new SearchResultsScrnMap(
            searchMgr.getSearchResultsFont(), this,
            searchMgr.getStepSearchMode(), searchMgr.getSearchOutput(),
            searchMgr);
        frameBox = Box.createVerticalBox();
        frameBox.add(searchResultsScrnMap.getSearchResultsBox());
        getContentPane().add(frameBox);
        setAutoRequestFocus(true);
    }

    private String buildTitle() {
        final SearchArgs args = searchMgr.getSearchArgs();
        if (searchMgr.getStepSearchMode()) {
            final String s = SearchResultsConstants.STEP_SEARCH_TITLE_LITERAL_1
                + SearchResultsConstants.STEP_SEARCH_TITLE_STEP_LITERAL
                + args.stepSearchStmt.getStep()
                + SearchResultsConstants.TITLE_THEOREM_LITERAL
                + args.theoremLabel
                + SearchResultsConstants.TITLE_LOC_AFTER_LITERAL
                + args.locAfterLabel;
            return s;
        }
        if (args.theoremLabel != null) {
            final String s1 = SearchResultsConstants.GENERAL_SEARCH_TITLE_LITERAL_1
                + SearchResultsConstants.TITLE_THEOREM_LITERAL
                + args.theoremLabel
                + SearchResultsConstants.TITLE_LOC_AFTER_LITERAL
                + args.locAfterLabel;
            return s1;
        }
        if (args.generalSearchStmtLabel != null) {
            final String s2 = SearchResultsConstants.GENERAL_SEARCH_TITLE_LITERAL_1
                + SearchResultsConstants.TITLE_STMT_LITERAL
                + args.generalSearchStmtLabel;
            return s2;
        }
        else
            return SearchOptionsConstants.DEFAULT_TITLE;
    }

    private void showSearchResultsDisplay() {
        setTitle(buildTitle());
        searchResultsScrnMap.downloadToScrnMap(searchMgr);
        searchResultsScrnMap.setStepSearchFieldsEnabled(searchMgr
            .getStepSearchMode());
        searchResultsScrnMap.setSearchResultsFont(searchMgr
            .getSearchResultsFont());
        pack();
        setVisible(true);
        setShown(true);
    }

    private void setFontSizeDisplay() {
        searchResultsScrnMap.setSearchResultsFont(searchMgr
            .getSearchResultsFont());
        pack();
    }

    private void setShown(final boolean flag) {
        shown = flag;
    }

    private boolean getShown() {
        return shown;
    }

    public static void main(final String[] args) {
        new ProofAsstPreferences().getSearchMgr().execMockupSearchResults();
    }

    abstract class RequestGUI {
        abstract void doIt();
    }

    protected static class UpdateGUI implements Runnable {
        RequestGUI requestGUI;

        public UpdateGUI(final RequestGUI requestgui) {
            requestGUI = requestgui;
        }

        public void run() {
            requestGUI.doIt();
        }
    }

    class ShowSearchResults extends RequestGUI {

        SearchMgr searchMgr;
        SearchResultsScrnMap searchResultsScrnMap;

        ShowSearchResults(final SearchMgr searchMgr,
            final SearchResultsScrnMap searchResultsScrnMap)
        {
            this.searchMgr = searchMgr;
            this.searchResultsScrnMap = searchResultsScrnMap;
        }

        @Override
        void doIt() {
            showSearchResultsDisplay();
        }
    }

    class ReshowSearchResults extends RequestGUI {
        @Override
        void doIt() {
            setVisible(true);
        }
    }

    class ShowPopupErrorMessage extends RequestGUI {
        Component parentComponent;
        String errorMessage;

        ShowPopupErrorMessage(final Component component, final String s) {
            parentComponent = component;
            errorMessage = s;
        }

        @Override
        void doIt() {
            JOptionPane.showMessageDialog(parentComponent, errorMessage,
                SearchOptionsConstants.POPUP_ERROR_MESSAGE_TITLE, 0);
        }
    }
}
