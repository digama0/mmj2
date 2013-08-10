//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * SearchOptionsFrame.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.*;

import mmj.lang.BookManager;
import mmj.pa.ProofAsstPreferences;

public class SearchOptionsFrame extends JFrame implements
    SearchOptionsButtonHandler
{

    private final SearchMgr searchMgr;
    private SearchOptionsScrnMap searchOptionsScrnMap;
    private Box frameBox;
    private boolean shown;

    public SearchOptionsFrame(final SearchMgr searchMgr) {
        shown = false;
        this.searchMgr = searchMgr;
        buildFrame();
    }

    public void uploadFromScrnMap(final SearchArgs args) {
        searchOptionsScrnMap.uploadFromScrnMap(args);
    }

    void increaseFontSize() {
        searchMgr.increaseSearchOptionsFontSize();
        searchOptionsScrnMap.setSearchOptionsFont(searchMgr
            .getSearchOptionsFont());
        pack();
    }

    void decreaseFontSize() {
        searchMgr.decreaseSearchOptionsFontSize();
        searchOptionsScrnMap.setSearchOptionsFont(searchMgr
            .getSearchOptionsFont());
        pack();
    }

    void showPopupErrorMessage(final String s) {
        processLocalRequestGUI(new ShowPopupErrorMessage(this, s));
    }

    void reshowSearchOptions() {
        if (getShown())
            processLocalRequestGUI(new ReshowSearchOptions());
        else
            showSearchOptions();
    }

    void showSearchOptions() {
        showSearchOptionsDisplay();
    }

    void mockupSearchOptions() {
        processLocalRequestGUI(new ShowSearchOptions(searchMgr,
            searchOptionsScrnMap));
    }

    void showSearchOptionsErrors() {
        showSearchOptionsErrorsDisplay();
    }

    void processLocalRequestGUI(final RequestGUI requestgui) {
        final UpdateGUI updategui = new UpdateGUI(requestgui);
        EventQueue.invokeLater(updategui);
    }

    public void searchOptionsButtonPressed(final int i) {
        switch (i) {
            case 0: // '\0'
                processLocalRequestGUI(new UpdateForWhatHistory(
                    searchOptionsScrnMap));
                searchMgr.searchOptionsSearchButtonPressed();
                break;

            case 1: // '\001'
                searchMgr.searchOptionsCancelButtonPressed();
                break;

            case 2: // '\002'
                searchMgr.searchOptionsNewButtonPressed();
                break;

            case 3: // '\003'
                processLocalRequestGUI(new UpdateForWhatHistory(
                    searchOptionsScrnMap));
                searchMgr.searchOptionsRefineButtonPressed();
                break;

            case 4: // '\004'
                processLocalRequestGUI(new SetDefaultsToCurrentValues(
                    searchOptionsScrnMap));
                break;

            case 5: // '\005'
                searchMgr.searchOptionsPAButtonPressed();
                break;

            case 6: // '\006'
                searchMgr.searchOptionsSRButtonPressed();
                break;

            case 7: // '\007'
                searchMgr.showSearchOptionsHelp();
                break;

            case 8: // '\b'
                searchMgr.searchOptionsPlusButtonPressed();
                break;

            case 9: // '\t'
                searchMgr.searchOptionsMinusButtonPressed();
                break;

            case 10: // '\n'
                processLocalRequestGUI(new ResetSearchDataDefaults(
                    searchOptionsScrnMap));
                break;

            case 11: // '\013'
                processLocalRequestGUI(new ResetSearchControlDefaults(
                    searchOptionsScrnMap));
                break;

            default:
                throw new IllegalArgumentException(
                    SearchOptionsConstants.ERRMSG_UNRECOGNIZED_BUTTON_ID_1 + i);
        }
    }

    private void buildFrame() {
        setTitle(buildTitle());
        setDefaultCloseOperation(2);
        final BookManager bookManager = searchMgr.getBookManager();
        String[] as;
        String[][] as1;
        if (bookManager == null) {
            as = SearchOptionsConstants.DEFAULT_CHAP_VALUES;
            as1 = SearchOptionsConstants.DEFAULT_CHAP_SEC_VALUES;
        }
        else {
            as = bookManager.getChapterValuesForSearch();
            as1 = bookManager.getSectionValuesForSearch();
        }
        searchOptionsScrnMap = new SearchOptionsScrnMap(
            searchMgr.getSearchOptionsFont(), this, as, as1,
            SearchOptionsConstants.INITIAL_FOR_WHAT_PRIOR_VALUES,
            searchMgr.getStepSearchMode(), searchMgr, searchMgr.getSearchArgs());
        frameBox = Box.createVerticalBox();
        frameBox.add(searchOptionsScrnMap.getSearchOptionsBox());
        getContentPane().add(frameBox);
        setAutoRequestFocus(true);
    }

    private String buildTitle() {
        final SearchArgs args = searchMgr.getSearchArgs();
        if (searchMgr.getStepSearchMode())
            return SearchOptionsConstants.STEP_SEARCH_TITLE_LITERAL_1
                + SearchOptionsConstants.STEP_SEARCH_TITLE_STEP_LITERAL
                + args.stepSearchStmt.getStep()
                + SearchOptionsConstants.TITLE_THEOREM_LITERAL
                + args.theoremLabel
                + SearchOptionsConstants.TITLE_LOC_AFTER_LITERAL
                + args.locAfterLabel;
        if (args.theoremLabel != null)
            return SearchOptionsConstants.GENERAL_SEARCH_TITLE_LITERAL_1
                + SearchOptionsConstants.TITLE_THEOREM_LITERAL
                + args.theoremLabel
                + SearchOptionsConstants.TITLE_LOC_AFTER_LITERAL
                + args.locAfterLabel;
        if (args.generalSearchStmtLabel != null)
            return SearchOptionsConstants.GENERAL_SEARCH_TITLE_LITERAL_1
                + SearchOptionsConstants.TITLE_STMT_LITERAL
                + args.generalSearchStmtLabel;
        else
            return SearchOptionsConstants.GENERAL_SEARCH_TITLE_LITERAL_1
                + SearchOptionsConstants.TITLE_STMT_LITERAL + " N/A";
    }

    private void showSearchOptionsDisplay() {
        setTitle(buildTitle());
        searchOptionsScrnMap.setStepSearchFieldsEnabled(searchMgr
            .getStepSearchMode());
        searchOptionsScrnMap.setSearchOptionsFont(searchMgr
            .getSearchOptionsFont());
        pack();
        setVisible(true);
        setShown(true);
    }

    private void showSearchOptionsErrorsDisplay() {
        final SearchOutput searchOutput = searchMgr.getSearchOutput();
        setTitle(buildTitle());
        searchOptionsScrnMap.setStepSearchFieldsEnabled(searchMgr
            .getStepSearchMode());
        SearchError searchError = null;
        if (searchOutput != null) {
            searchError = searchOutput.getFirstError();
            if (searchError != null)
                searchOptionsScrnMap.positionCursor(
                    searchError.searchArgFieldId, 0);
        }
        searchOptionsScrnMap.setSearchOptionsFont(searchMgr
            .getSearchOptionsFont());
        pack();
        setVisible(true);
        setShown(true);
        if (searchError != null)
            JOptionPane.showMessageDialog(this, searchError.message,
                SearchOptionsConstants.POPUP_ERROR_MESSAGE_TITLE, 0);
    }

    private void setShown(final boolean flag) {
        shown = flag;
    }

    private boolean getShown() {
        return shown;
    }

    public static void main(final String[] args) {
        new ProofAsstPreferences().getSearchMgr().execMockupSearchOptions(null);
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

    class UpdateForWhatHistory extends RequestGUI {
        SearchOptionsScrnMap searchOptionsScrnMap;

        UpdateForWhatHistory(final SearchOptionsScrnMap map) {
            searchOptionsScrnMap = map;
        }

        @Override
        void doIt() {
            searchOptionsScrnMap.updateForWhatHistory();
        }
    }

    class ResetSearchControlDefaults extends RequestGUI {
        SearchOptionsScrnMap searchOptionsScrnMap;

        ResetSearchControlDefaults(
            final SearchOptionsScrnMap searchOptionsScrnMap)
        {
            this.searchOptionsScrnMap = searchOptionsScrnMap;
        }

        @Override
        void doIt() {
            searchOptionsScrnMap.resetSearchControlDefaults();
        }
    }

    class ResetSearchDataDefaults extends RequestGUI {
        SearchOptionsScrnMap searchOptionsScrnMap;

        ResetSearchDataDefaults(final SearchOptionsScrnMap searchOptionsScrnMap)
        {
            this.searchOptionsScrnMap = searchOptionsScrnMap;
        }

        @Override
        void doIt() {
            searchOptionsScrnMap.resetSearchDataDefaults();
        }
    }

    class SetDefaultsToCurrentValues extends RequestGUI {

        SearchOptionsScrnMap searchOptionsScrnMap;

        SetDefaultsToCurrentValues(
            final SearchOptionsScrnMap searchOptionsScrnMap)
        {
            this.searchOptionsScrnMap = searchOptionsScrnMap;
        }

        @Override
        void doIt() {
            searchOptionsScrnMap.setDefaultsToCurrentValues();
        }
    }

    class ShowSearchOptions extends RequestGUI {
        SearchMgr searchMgr;
        SearchOptionsScrnMap searchOptionsScrnMap;

        ShowSearchOptions(final SearchMgr searchMgr,
            final SearchOptionsScrnMap searchOptionsScrnMap)
        {
            this.searchMgr = searchMgr;
            this.searchOptionsScrnMap = searchOptionsScrnMap;
        }

        @Override
        void doIt() {
            showSearchOptionsDisplay();
        }
    }

    class ReshowSearchOptions extends RequestGUI {
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
