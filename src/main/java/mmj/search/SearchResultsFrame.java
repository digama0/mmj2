//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchResultsFrame.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.*;

import javax.swing.*;

import mmj.pa.ProofAsstPreferences;

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

    public void increaseFontSize() {
        searchMgr.increaseSearchResultsFontSize();
        setFontSizeDisplay();
    }

    public void decreaseFontSize() {
        searchMgr.decreaseSearchResultsFontSize();
        setFontSizeDisplay();
    }

    public int getSelectedIndex() {
        return searchResultsScrnMap.getSelectedIndex();
    }

    public void showPopupErrorMessage(final String s) {
        EventQueue.invokeLater(new ShowPopupErrorMessage(this, s));
    }

    public void reshowSearchResults() {
        if (getShown())
            EventQueue.invokeLater(new ReshowSearchResults());
        else
            showSearchResults();
    }

    public void mockupSearchResults() {
        EventQueue.invokeLater(new ShowSearchResults(searchMgr,
            searchResultsScrnMap));
    }

    public void searchResultsButtonPressed(final int i) {
        switch (i) {
            case SearchResultsConstants.APPLY_BUTTON_ID:
                searchMgr.searchResultsApplyButtonPressed();
                break;

            case SearchResultsConstants.CANCEL_BUTTON_ID:
                searchMgr.searchResultsCancelButtonPressed();
                break;

            case SearchResultsConstants.PA_BUTTON_ID:
                searchMgr.searchResultsPAButtonPressed();
                break;

            case SearchResultsConstants.SO_BUTTON_ID:
                searchMgr.searchResultsSOButtonPressed();
                break;

            case SearchResultsConstants.HELP_BUTTON_ID:
                searchMgr.showSearchResultsHelp();
                break;

            case SearchResultsConstants.PLUS_BUTTON_ID:
                searchMgr.searchResultsPlusButtonPressed();
                break;

            case SearchResultsConstants.MINUS_BUTTON_ID:
                searchMgr.searchResultsMinusButtonPressed();
                break;

            default:
                throw new IllegalArgumentException(
                    SearchResultsConstants.ERRMSG_UNRECOGNIZED_BUTTON_ID_1 + i);
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

        searchResultsScrnMap = new SearchResultsScrnMap(
            searchMgr.getSearchResultsFont(), this,
            searchMgr.getStepSearchMode(), searchMgr.getSearchOutput(),
            searchMgr);
        frameBox = Box.createVerticalBox();
        frameBox.add(searchResultsScrnMap.getSearchResultsBox());
        getContentPane().add(frameBox);
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

    public void showSearchResults() {
        setTitle(buildTitle());
        searchResultsScrnMap.downloadToScrnMap(searchMgr);
        searchResultsScrnMap.setStepSearchFieldsEnabled(searchMgr
            .getStepSearchMode());
        searchResultsScrnMap.setSearchResultsFont(searchMgr
            .getSearchResultsFont());
        pack();
        setVisible(true);
        setShown(true);
        toFront();
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

    class ShowSearchResults implements Runnable {

        SearchMgr searchMgr;
        SearchResultsScrnMap searchResultsScrnMap;

        ShowSearchResults(final SearchMgr searchMgr,
            final SearchResultsScrnMap searchResultsScrnMap)
        {
            this.searchMgr = searchMgr;
            this.searchResultsScrnMap = searchResultsScrnMap;
        }

        public void run() {
            showSearchResults();
        }
    }

    class ReshowSearchResults implements Runnable {
        public void run() {
            setVisible(true);
            toFront();
        }
    }

    class ShowPopupErrorMessage implements Runnable {
        Component parentComponent;
        String errorMessage;

        ShowPopupErrorMessage(final Component component, final String s) {
            parentComponent = component;
            errorMessage = s;
        }

        public void run() {
            JOptionPane.showMessageDialog(parentComponent, errorMessage,
                SearchOptionsConstants.POPUP_ERROR_MESSAGE_TITLE, 0);
        }
    }
}
