//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchMgr.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.JOptionPane;

import mmj.lang.*;
import mmj.pa.*;
import mmj.verify.Grammar;
import mmj.verify.VerifyProofs;

public class SearchMgr {

    public static String reformatMessage(final String s) {
        final StringBuffer sb = new StringBuffer(s.length() + 4);
        int i = 0;
        for (int j = 0; j < s.length(); j++) {
            final char c = s.charAt(j);
            if (i >= 80 && Character.isWhitespace(c) || c == '\n' || c == '\r')
            {
                sb.append('\n');
                i = 0;
            }
            else {
                sb.append(c);
                i++;
            }
        }

        return sb.toString();
    }

    private ProofAsst proofAsst = null;
    private final ProofAsstPreferences proofAsstPreferences;
    private LogicalSystem logicalSystem = null;
    private Grammar grammar = null;
    private BookManager bookManager = null;
    private Cnst provableLogicStmtTyp = null;
    private SearchOptionsHelp searchOptionsHelp = null;
    private SearchOptionsFrame searchOptionsFrame = null;
    private int searchOptionsFontSize;
    private final boolean searchOptionsFontBold;
    private Font searchOptionsFont;
    private SearchResultsHelp searchResultsHelp = null;
    private SearchResultsFrame searchResultsFrame = null;
    private int searchResultsFontSize;
    private final boolean searchResultsFontBold;
    private Font searchResultsFont;
    private Dimension searchSelectionPreferredSize = null;
    private final SearchArgs searchArgs = new SearchArgs();
    private SearchOutput searchOutput = new SearchOutput("");
    private SearchEngine searchEngine = null;
    private final SearchUnifier searchUnifier = new SearchUnifier();

    public SearchMgr(final ProofAsstPreferences proofAsstPreferences) {
        this.proofAsstPreferences = proofAsstPreferences;
        SearchOptionsConstants.CHECK_SEARCH_OPTIONS_FIELD_ATTR_IDS();
        SearchOptionsConstants.CHECK_SEARCH_ARGS_FIELD_IDS(searchArgs);
        SearchOptionsConstants.CHECK_SEARCH_OPTIONS_BUTTON_ATTR_IDS();
        searchOptionsFontSize = proofAsstPreferences.getFontSize();
        searchOptionsFontBold = proofAsstPreferences.getFontBold();
        searchOptionsFont = buildInitialSearchFont(searchOptionsFontBold,
            searchOptionsFontSize);
        searchResultsFontSize = proofAsstPreferences.getFontSize();
        searchResultsFontBold = proofAsstPreferences.getFontBold();
        searchResultsFont = buildInitialSearchFont(searchResultsFontBold,
            searchResultsFontSize);
        searchSelectionPreferredSize = new Dimension(
            proofAsstPreferences.getStepSelectorDialogPaneWidth(),
            proofAsstPreferences.getStepSelectorDialogPaneHeight());
    }

    public void initOtherEnvAreas(final ProofAsst proofAsst,
        final LogicalSystem logicalSystem, final Grammar grammar,
        final VerifyProofs verifyProofs, final Messages messages)
    {
        this.proofAsst = proofAsst;
        this.logicalSystem = logicalSystem;
        this.grammar = grammar;
        bookManager = logicalSystem.getBookManager();
        provableLogicStmtTyp = grammar.getProvableLogicStmtTypArray()[0];
        searchArgs.sortedAssrtSearchList = proofAsst.getSortedAssrtSearchList();
        searchEngine = new SearchEngine(this, proofAsst, proofAsstPreferences,
            bookManager, verifyProofs, provableLogicStmtTyp);
    }

    public void execSearchOptionsNewGeneralSearch(final Stmt stmt) {
        searchArgs.loadSearchKeys(stmt, logicalSystem);
        execShowSearchOptions();
    }

    public SearchOutput execStepSearch(final DerivationStep derivationStep) {
        searchArgs.loadSearchKeys(derivationStep.getProofWorksheet(),
            logicalSystem);
        return execSearch();
    }

    public SearchOutput execSearch() {
        searchOutput = new SearchOutput(getSearchOptionsFrame().getTitle());
        getSearchOptionsFrame().uploadFromScrnMap(searchArgs);
        searchArgs.sortedAssrtSearchList = proofAsst.getSortedAssrtSearchList();
        return searchEngine.execSearch();
    }

    public String execCancelRequestAction() {
        String s = reformatMessage(SearchConstants.ERRMSG_NO_TASK_TO_CANCEL);
        if (getProofAsst() != null
            && getProofAsst().getProofAsstGUI().cancelRequestAction())
            s = reformatMessage(SearchConstants.ERRMSG_TASK_CANCEL_REQUESTED);
        return s;
    }

    public SearchOutput execRefineSearch() {
        final List<Assrt> arraylist = proofAsst
            .sortAssrtListForSearch(searchOutput.sortedAssrtResultsList);
        getSearchOptionsFrame().uploadFromScrnMap(searchArgs);
        searchOutput = new SearchOutput(getSearchOptionsFrame().getTitle());
        searchArgs.sortedAssrtSearchList = arraylist;
        if (arraylist.size() > 0)
            return searchEngine.execSearch();
        else
            return searchOutput;
    }

    public void execShowSearchResults() {
        if (searchOutput.searchReturnCode == 0)
            getSearchResultsFrame().showSearchResults();
        else
            execShowSearchOptionsErrors();
    }

    public void execMockupSearchResults() {
        getSearchResultsFrame().mockupSearchResults();
    }

    public void execShowSearchOptions(final ProofWorksheet proofWorksheet) {
        searchArgs.loadSearchKeys(proofWorksheet, logicalSystem);
        execShowSearchOptions();
    }

    public void execMockupSearchOptions(final ProofWorksheet proofWorksheet) {
        searchArgs.loadSearchKeys(proofWorksheet, logicalSystem);
        execMockupSearchOptions();
    }

    public void execShowSearchOptions() {
        getSearchOptionsFrame().showSearchOptions();
    }

    public void execMockupSearchOptions() {
        getSearchOptionsFrame().mockupSearchOptions();
    }

    public void execShowSearchOptionsErrors() {
        getSearchOptionsFrame().showSearchOptionsErrors();
    }

    public void execReshowSearchOptions() {
        getSearchOptionsFrame().reshowSearchOptions();
    }

    public void execReshowSearchResults() {
        getSearchResultsFrame().reshowSearchResults();
    }

    public void execApplySearchSelection(final int i) {
        if (popupMessageSearchResultsTestMode())
            return;
        if (!getStepSearchMode() || searchArgs.stepSearchStmt == null) {
            execShowSearchResultsPopupMessage(SearchConstants.ERRMSG_CANT_APPLY_OLD_SEARCH_RESULTS);
            return;
        }
        Assrt assrt = null;
        if (i != -1)
            assrt = searchOutput.sortedAssrtResultsList.get(i);
        getProofAsst().getProofAsstGUI().unifyWithStepSelectorChoice(
            new StepRequest(82, searchArgs.stepSearchStmt.getStep(), assrt));
    }

    public void execReshowProofAsstGUI() {
        if (getProofAsst() != null)
            getProofAsst().getProofAsstGUI().getMainFrame().setVisible(true);
    }

    public void execShowSearchOptionsPopupMessage(final String s) {
        getSearchOptionsFrame().showPopupErrorMessage(s);
    }

    public void execShowSearchResultsPopupMessage(final String s) {
        getSearchResultsFrame().showPopupErrorMessage(s);
    }

    public void execSearchOptionsIncreaseFontSize() {
        getSearchOptionsFrame().increaseFontSize();
    }

    public void execSearchOptionsDecreaseFontSize() {
        getSearchOptionsFrame().decreaseFontSize();
    }

    public void execSearchResultsIncreaseFontSize() {
        getSearchResultsFrame().increaseFontSize();
    }

    public void execSearchResultsDecreaseFontSize() {
        getSearchResultsFrame().decreaseFontSize();
    }

    boolean popupMessageSearchOptionsTestMode() {
        if (getProofAsst() == null) {
            execShowSearchOptionsPopupMessage(SearchConstants.ERRMSG_FUNCTION_INOPERABLE_IN_TEST_MODE);
            return true;
        }
        else
            return false;
    }

    void searchOptionsSearchButtonPressed() {
        if (popupMessageSearchOptionsTestMode())
            return;
        if (!getProofAsst().getProofAsstGUI()
            .startRequestSearchAndShowResults())
            execShowSearchOptionsPopupMessage(SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    void searchOptionsCancelButtonPressed() {
        if (popupMessageSearchOptionsTestMode())
            return;
        else {
            execShowSearchOptionsPopupMessage(execCancelRequestAction());
            return;
        }
    }

    void searchOptionsNewButtonPressed() {
        if (popupMessageSearchOptionsTestMode())
            return;
        final String s = getSearchOptionsNewStmtLabel();
        if (s != null
            && !getProofAsst().getProofAsstGUI()
                .startRequestNewGeneralSearch(s))
            execShowSearchOptionsPopupMessage(SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    void searchOptionsRefineButtonPressed() {
        if (popupMessageSearchOptionsTestMode())
            return;
        if (!getProofAsst().getProofAsstGUI()
            .startRequestRefineAndShowResults())
            execShowSearchOptionsPopupMessage(SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    void searchOptionsPAButtonPressed() {
        if (popupMessageSearchOptionsTestMode())
            return;
        if (!getProofAsst().getProofAsstGUI().startRequestReshowProofAsstGUI())
            execShowSearchOptionsPopupMessage(SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    void searchOptionsSRButtonPressed() {
        if (popupMessageSearchOptionsTestMode())
            return;
        if (!getProofAsst().getProofAsstGUI().startRequestReshowSearchResults())
            execShowSearchOptionsPopupMessage(SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    void showSearchOptionsHelp() {
        searchOptionsHelp = new SearchOptionsHelp(proofAsstPreferences);
        searchOptionsHelp.showFrame(searchOptionsHelp.buildFrame());
    }

    void searchOptionsPlusButtonPressed() {
        if (popupMessageSearchOptionsTestMode())
            return;
        if (!getProofAsst().getProofAsstGUI()
            .startRequestSearchOptionsPlusButton())
            execShowSearchOptionsPopupMessage(SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    void searchOptionsMinusButtonPressed() {
        if (popupMessageSearchOptionsTestMode())
            return;
        if (!getProofAsst().getProofAsstGUI()
            .startRequestSearchOptionsMinusButton())
            execShowSearchOptionsPopupMessage(SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    int increaseSearchOptionsFontSize() {
        searchOptionsFontSize = computeLargerNewFontSize(searchOptionsFontSize);
        final Font font = searchOptionsFont
            .deriveFont((float)searchOptionsFontSize);
        searchOptionsFont = font;
        return searchOptionsFontSize;
    }

    int decreaseSearchOptionsFontSize() {
        searchOptionsFontSize = computeSmallerNewFontSize(searchOptionsFontSize);
        final Font font = searchOptionsFont
            .deriveFont((float)searchOptionsFontSize);
        searchOptionsFont = font;
        return searchOptionsFontSize;
    }

    boolean popupMessageSearchResultsTestMode() {
        if (getProofAsst() == null) {
            execShowSearchResultsPopupMessage(SearchConstants.ERRMSG_FUNCTION_INOPERABLE_IN_TEST_MODE);
            return true;
        }
        else
            return false;
    }

    void searchResultsApplyButtonPressed() {
        if (popupMessageSearchResultsTestMode())
            return;
        else {
            execApplySearchSelection(getSearchResultsFrame().getSelectedIndex());
            return;
        }
    }

    void searchResultsCancelButtonPressed() {
        if (popupMessageSearchResultsTestMode())
            return;
        else {
            execShowSearchResultsPopupMessage(execCancelRequestAction());
            return;
        }
    }

    void searchResultsPAButtonPressed() {
        if (popupMessageSearchResultsTestMode())
            return;
        if (!getProofAsst().getProofAsstGUI().startRequestReshowProofAsstGUI())
            execShowSearchResultsPopupMessage(SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    void searchResultsSOButtonPressed() {
        if (popupMessageSearchResultsTestMode())
            return;
        if (!getProofAsst().getProofAsstGUI().startRequestReshowSearchOptions())
            execShowSearchResultsPopupMessage(SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    void showSearchResultsHelp() {
        searchResultsHelp = new SearchResultsHelp(proofAsstPreferences);
        searchResultsHelp.showFrame(searchResultsHelp.buildFrame());
    }

    void searchResultsPlusButtonPressed() {
        if (popupMessageSearchResultsTestMode())
            return;
        if (!getProofAsst().getProofAsstGUI()
            .startRequestSearchResultsPlusButton())
            execShowSearchResultsPopupMessage(SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    void searchResultsMinusButtonPressed() {
        if (popupMessageSearchResultsTestMode())
            return;
        if (!getProofAsst().getProofAsstGUI()
            .startRequestSearchResultsMinusButton())
            execShowSearchResultsPopupMessage(SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    int increaseSearchResultsFontSize() {
        searchResultsFontSize = computeLargerNewFontSize(searchResultsFontSize);
        final Font font = searchResultsFont
            .deriveFont((float)searchResultsFontSize);
        searchResultsFont = font;
        return searchResultsFontSize;
    }

    int decreaseSearchResultsFontSize() {
        searchResultsFontSize = computeSmallerNewFontSize(searchResultsFontSize);
        final Font font = searchResultsFont
            .deriveFont((float)searchResultsFontSize);
        searchResultsFont = font;
        return searchResultsFontSize;
    }

    Dimension getSearchSelectionPreferredSize() {
        return searchSelectionPreferredSize;
    }

    Grammar getGrammar() {
        return grammar;
    }

    ScopeFrame getComboFrame() {
        return searchArgs.comboFrame;
    }

    Cnst getProvableLogicStmtTyp() {
        return provableLogicStmtTyp;
    }

    LogicalSystem getLogicalSystem() {
        return logicalSystem;
    }

    boolean getStepSearchMode() {
        return getSearchArgs().stepSearchMode;
    }

    ProofAsst getProofAsst() {
        return proofAsst;
    }

    ProofAsstPreferences getProofAsstPreferences() {
        return proofAsstPreferences;
    }

    StepUnifier getStepUnifier() {
        return getProofAsstPreferences().getStepUnifier();
    }

    SearchUnifier getSearchUnifier() {
        return searchUnifier;
    }

    WorkVarManager getWorkVarManager() {
        return proofAsstPreferences.getWorkVarManager();
    }

    SearchArgs getSearchArgs() {
        return searchArgs;
    }

    SearchOutput getSearchOutput() {
        return searchOutput;
    }

    BookManager getBookManager() {
        return bookManager;
    }

    Font getSearchOptionsFont() {
        return searchOptionsFont;
    }

    int getSearchOptionsFontSize() {
        return searchOptionsFontSize;
    }

    private SearchOptionsFrame getSearchOptionsFrame() {
        if (searchOptionsFrame == null)
            searchOptionsFrame = new SearchOptionsFrame(this);
        return searchOptionsFrame;
    }

    Font getSearchResultsFont() {
        return searchResultsFont;
    }

    int getSearchResultsFontSize() {
        return searchResultsFontSize;
    }

    private SearchResultsFrame getSearchResultsFrame() {
        if (searchResultsFrame == null)
            searchResultsFrame = new SearchResultsFrame(this);
        return searchResultsFrame;
    }

    private int computeLargerNewFontSize(final int i) {
        int j = i + 2;
        if (j > 72)
            j = 72;
        return j;
    }

    private int computeSmallerNewFontSize(final int i) {
        int j = i - 2;
        if (j < 8)
            j = 8;
        return j;
    }

    private Font buildInitialSearchFont(final boolean bold, final int size) {
        if (bold)
            return new Font(proofAsstPreferences.getFontFamily(), Font.BOLD,
                size);
        else
            return new Font(proofAsstPreferences.getFontFamily(), Font.PLAIN,
                size);
    }

    private String getSearchOptionsNewStmtLabel() {
        String s = "";
        String s1 = SearchConstants.SEARCH_OPTIONS_NEW_STMT_LABEL_PROMPT;
        do {
            s = JOptionPane.showInputDialog(searchOptionsFrame, s1, s);
            if (s == null)
                return null;
            s = s.trim();
            if (s.equals(""))
                return s;
            final Stmt stmt = proofAsst.getStmt(s);
            if (stmt != null)
                return s;
            s1 = SearchConstants.SEARCH_OPTIONS_NEW_STMT_LABEL_PROMPT_2_1 + s
                + SearchConstants.SEARCH_OPTIONS_NEW_STMT_LABEL_PROMPT_2_2;
        } while (true);
    }
}
