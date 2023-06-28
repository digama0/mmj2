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
import mmj.pa.StepRequest.StepRequestType;
import mmj.verify.Grammar;
import mmj.verify.VerifyProofs;

public class SearchMgr {

    public static String reformatMessage(final String s) {
        final StringBuffer sb = new StringBuffer(s.length() + 4);
        int i = 0;
        for (int j = 0; j < s.length(); j++) {
            final char c = s.charAt(j);
            if (i >= 80 && Character.isWhitespace(c) || c == '\n'
                || c == '\r')
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
        searchOptionsFontSize = proofAsstPreferences.fontSize.get();
        searchOptionsFontBold = proofAsstPreferences.fontBold.get();
        searchOptionsFont = buildInitialSearchFont(searchOptionsFontBold,
            searchOptionsFontSize);
        searchResultsFontSize = proofAsstPreferences.fontSize.get();
        searchResultsFontBold = proofAsstPreferences.fontBold.get();
        searchResultsFont = buildInitialSearchFont(searchResultsFontBold,
            searchResultsFontSize);
        searchSelectionPreferredSize = new Dimension(
            proofAsstPreferences.stepSelectorDialogPaneWidth.get(),
            proofAsstPreferences.stepSelectorDialogPaneHeight.get());
    }

    public void initOtherEnvAreas(final ProofAsst proofAsst,
        final LogicalSystem logicalSystem, final Grammar grammar,
        final VerifyProofs verifyProofs, final Messages messages)
    {
        this.proofAsst = proofAsst;
        this.logicalSystem = logicalSystem;
        this.grammar = grammar;
        bookManager = logicalSystem.bookManager;
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
        if (!popupMessageSearchResultsTestMode()
            && (!getStepSearchMode() || searchArgs.stepSearchStmt == null))
            execShowSearchResultsPopupMessage(
                SearchConstants.ERRMSG_CANT_APPLY_OLD_SEARCH_RESULTS);
        final Assrt assrt = i == -1 ? null
            : searchOutput.sortedAssrtResultsList.get(i);
        getProofAsst().getProofAsstGUI().unifyWithStepSelectorChoice(
            new StepRequest(StepRequestType.SelectorChoice,
                searchArgs.stepSearchStmt.getStep(), assrt));
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

    public boolean popupMessageSearchOptionsTestMode() {
        if (getProofAsst() == null) {
            execShowSearchOptionsPopupMessage(
                SearchConstants.ERRMSG_FUNCTION_INOPERABLE_IN_TEST_MODE);
            return true;
        }
        return false;
    }

    public void searchOptionsSearchButtonPressed() {
        if (!popupMessageSearchOptionsTestMode()
            && !getProofAsst().getProofAsstGUI().searchAndShowResults())
            execShowSearchOptionsPopupMessage(
                SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    public void searchOptionsCancelButtonPressed() {
        if (!popupMessageSearchOptionsTestMode())
            execShowSearchOptionsPopupMessage(execCancelRequestAction());
    }

    public void searchOptionsNewButtonPressed() {
        if (popupMessageSearchOptionsTestMode())
            return;
        final String s = getSearchOptionsNewStmtLabel();
        if (s != null && !getProofAsst().getProofAsstGUI().newGeneralSearch(s))
            execShowSearchOptionsPopupMessage(
                SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    public void searchOptionsRefineButtonPressed() {
        if (!popupMessageSearchOptionsTestMode()
            && !getProofAsst().getProofAsstGUI().refineAndShowResults())
            execShowSearchOptionsPopupMessage(
                SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    public void searchOptionsPAButtonPressed() {
        if (!popupMessageSearchOptionsTestMode()
            && !getProofAsst().getProofAsstGUI().reshowProofAsstGUI())
            execShowSearchOptionsPopupMessage(
                SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    public void searchOptionsSRButtonPressed() {
        if (!popupMessageSearchOptionsTestMode()
            && !getProofAsst().getProofAsstGUI().reshowSearchResults())
            execShowSearchOptionsPopupMessage(
                SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    public void showSearchOptionsHelp() {
        searchOptionsHelp = new SearchOptionsHelp(proofAsstPreferences);
        searchOptionsHelp.showFrame(searchOptionsHelp.buildFrame());
    }

    public void searchOptionsPlusButtonPressed() {
        if (!popupMessageSearchOptionsTestMode()
            && !getProofAsst().getProofAsstGUI().searchOptionsPlusButton())
            execShowSearchOptionsPopupMessage(
                SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    public void searchOptionsMinusButtonPressed() {
        if (!popupMessageSearchOptionsTestMode()
            && !getProofAsst().getProofAsstGUI().searchOptionsMinusButton())
            execShowSearchOptionsPopupMessage(
                SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    public int increaseSearchOptionsFontSize() {
        searchOptionsFontSize = computeLargerNewFontSize(searchOptionsFontSize);
        final Font font = searchOptionsFont
            .deriveFont((float)searchOptionsFontSize);
        searchOptionsFont = font;
        return searchOptionsFontSize;
    }

    public int decreaseSearchOptionsFontSize() {
        searchOptionsFontSize = computeSmallerNewFontSize(
            searchOptionsFontSize);
        final Font font = searchOptionsFont
            .deriveFont((float)searchOptionsFontSize);
        searchOptionsFont = font;
        return searchOptionsFontSize;
    }

    public boolean popupMessageSearchResultsTestMode() {
        if (getProofAsst() == null) {
            execShowSearchResultsPopupMessage(
                SearchConstants.ERRMSG_FUNCTION_INOPERABLE_IN_TEST_MODE);
            return true;
        }
        return false;
    }

    public void searchResultsApplyButtonPressed() {
        if (!popupMessageSearchResultsTestMode())
            execApplySearchSelection(
                getSearchResultsFrame().getSelectedIndex());
    }

    public void searchResultsCancelButtonPressed() {
        if (!popupMessageSearchResultsTestMode())
            execShowSearchResultsPopupMessage(execCancelRequestAction());
    }

    public void searchResultsPAButtonPressed() {
        if (!popupMessageSearchResultsTestMode()
            && !getProofAsst().getProofAsstGUI().reshowProofAsstGUI())
            execShowSearchResultsPopupMessage(
                SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    public void searchResultsSOButtonPressed() {
        if (!popupMessageSearchResultsTestMode()
            && !getProofAsst().getProofAsstGUI().reshowSearchOptions())
            execShowSearchResultsPopupMessage(
                SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    public void showSearchResultsHelp() {
        searchResultsHelp = new SearchResultsHelp(proofAsstPreferences);
        searchResultsHelp.showFrame(searchResultsHelp.buildFrame());
    }

    public void searchResultsPlusButtonPressed() {
        if (!popupMessageSearchResultsTestMode()
            && !getProofAsst().getProofAsstGUI().searchResultsPlusButton())
            execShowSearchResultsPopupMessage(
                SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    public void searchResultsMinusButtonPressed() {
        if (!popupMessageSearchResultsTestMode()
            && !getProofAsst().getProofAsstGUI().searchResultsMinusButton())
            execShowSearchResultsPopupMessage(
                SearchConstants.ERRMSG_PA_GUI_TASK_ALREADY_RUNNING);
    }

    public int increaseSearchResultsFontSize() {
        searchResultsFontSize = computeLargerNewFontSize(searchResultsFontSize);
        final Font font = searchResultsFont
            .deriveFont((float)searchResultsFontSize);
        searchResultsFont = font;
        return searchResultsFontSize;
    }

    public int decreaseSearchResultsFontSize() {
        searchResultsFontSize = computeSmallerNewFontSize(
            searchResultsFontSize);
        final Font font = searchResultsFont
            .deriveFont((float)searchResultsFontSize);
        searchResultsFont = font;
        return searchResultsFontSize;
    }

    public Dimension getSearchSelectionPreferredSize() {
        return searchSelectionPreferredSize;
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public ScopeFrame getComboFrame() {
        return searchArgs.comboFrame;
    }

    public Cnst getProvableLogicStmtTyp() {
        return provableLogicStmtTyp;
    }

    public LogicalSystem getLogicalSystem() {
        return logicalSystem;
    }

    public boolean getStepSearchMode() {
        return getSearchArgs().stepSearchMode;
    }

    public ProofAsst getProofAsst() {
        return proofAsst;
    }

    public ProofAsstPreferences getProofAsstPreferences() {
        return proofAsstPreferences;
    }

    public StepUnifier getStepUnifier() {
        return getProofAsstPreferences().getStepUnifier();
    }

    public SearchUnifier getSearchUnifier() {
        return searchUnifier;
    }

    public WorkVarManager getWorkVarManager() {
        return proofAsstPreferences.getWorkVarManager();
    }

    public SearchArgs getSearchArgs() {
        return searchArgs;
    }

    public SearchOutput getSearchOutput() {
        return searchOutput;
    }

    public BookManager getBookManager() {
        return bookManager;
    }

    public Font getSearchOptionsFont() {
        return searchOptionsFont;
    }

    public int getSearchOptionsFontSize() {
        return searchOptionsFontSize;
    }

    private SearchOptionsFrame getSearchOptionsFrame() {
        if (searchOptionsFrame == null)
            searchOptionsFrame = new SearchOptionsFrame(this);
        return searchOptionsFrame;
    }

    public Font getSearchResultsFont() {
        return searchResultsFont;
    }

    public int getSearchResultsFontSize() {
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
        return new Font(proofAsstPreferences.fontFamily.get(),
            bold ? Font.BOLD : Font.PLAIN, size);
    }

    private String getSearchOptionsNewStmtLabel() {
        String label = "";
        String prompt = SearchConstants.SEARCH_OPTIONS_NEW_STMT_LABEL_PROMPT;
        while (true) {
            label = JOptionPane.showInputDialog(searchOptionsFrame, prompt,
                label);
            if (label == null)
                return null;
            label = label.trim();
            if (label.isEmpty() || proofAsst.getStmt(label) != null)
                return label;
            prompt = ErrorCode.format(
                SearchConstants.SEARCH_OPTIONS_NEW_STMT_LABEL_PROMPT_2, label);
        }
    }
}
