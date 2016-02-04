//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchOptionsFrame.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.*;

import javax.swing.*;

import mmj.lang.BookManager;
import mmj.pa.ErrorCode;
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
            case SearchOptionsConstants.SEARCH_BUTTON_ID:
                processLocalRequestGUI(new UpdateForWhatHistory(
                    searchOptionsScrnMap));
                searchMgr.searchOptionsSearchButtonPressed();
                break;

            case SearchOptionsConstants.CANCEL_BUTTON_ID:
                searchMgr.searchOptionsCancelButtonPressed();
                break;

            case SearchOptionsConstants.NEW_BUTTON_ID:
                searchMgr.searchOptionsNewButtonPressed();
                break;

            case SearchOptionsConstants.REFINE_BUTTON_ID:
                processLocalRequestGUI(new UpdateForWhatHistory(
                    searchOptionsScrnMap));
                searchMgr.searchOptionsRefineButtonPressed();
                break;

            case SearchOptionsConstants.SET_DEF_BUTTON_ID:
                processLocalRequestGUI(new SetDefaultsToCurrentValues(
                    searchOptionsScrnMap));
                break;

            case SearchOptionsConstants.PA_BUTTON_ID:
                searchMgr.searchOptionsPAButtonPressed();
                break;

            case SearchOptionsConstants.SR_BUTTON_ID:
                searchMgr.searchOptionsSRButtonPressed();
                break;

            case SearchOptionsConstants.HELP_BUTTON_ID:
                searchMgr.showSearchOptionsHelp();
                break;

            case SearchOptionsConstants.PLUS_BUTTON_ID:
                searchMgr.searchOptionsPlusButtonPressed();
                break;

            case SearchOptionsConstants.MINUS_BUTTON_ID:
                searchMgr.searchOptionsMinusButtonPressed();
                break;

            case SearchOptionsConstants.RESET_DATA_BUTTON_ID:
                processLocalRequestGUI(new ResetSearchDataDefaults(
                    searchOptionsScrnMap));
                break;

            case SearchOptionsConstants.RESET_CONTROLS_BUTTON_ID:
                processLocalRequestGUI(new ResetSearchControlDefaults(
                    searchOptionsScrnMap));
                break;

            default:
                throw new IllegalArgumentException(ErrorCode.format(
                    SearchOptionsConstants.ERRMSG_UNRECOGNIZED_BUTTON_ID, i));
        }
    }

    private void buildFrame() {
        setTitle(buildTitle());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getRootPane().registerKeyboardAction(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                setVisible(false);
                dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);

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
        toFront();
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
        toFront();
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
            toFront();
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
