//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchResultsConstants.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

public class SearchResultsConstants {

    public static final int SEARCH_SELECTION_FIELD_ID = 0;
    public static String STRING_TRUE = "true";
    public static String STRING_FALSE = "false";
    public static boolean BOOLEAN_TRUE = true;
    public static boolean BOOLEAN_FALSE = false;
    public static final String TOOL_TIP_SEARCH_SELECTION = "Select assertion to Apply to derivation step.";
    public static final SearchResultsFieldAttr FIELD_ATTR[] = {new SearchResultsFieldAttr(
        SEARCH_SELECTION_FIELD_ID, "", TOOL_TIP_SEARCH_SELECTION, 80, "")};
    public static final int APPLY_BUTTON_ID = 0;
    public static final int CANCEL_BUTTON_ID = 1;
    public static final int PA_BUTTON_ID = 2;
    public static final int SO_BUTTON_ID = 3;
    public static final int HELP_BUTTON_ID = 4;
    public static final int PLUS_BUTTON_ID = 5;
    public static final int MINUS_BUTTON_ID = 6;
    public static final int NBR_BUTTONS = 7;
    public static final String TOOL_TIP_APPLY = "Apply selection to derivation proof step.";
    public static final String TOOL_TIP_CANCEL = "Cancel the running process.";
    public static final String TOOL_TIP_PA = "Jump to the Proof Assistant window (without searching.)";
    public static final String TOOL_TIP_SO = "Jump to the Search Options window.";
    public static final String TOOL_TIP_HELP = "Display Help for Search Results.";
    public static final String TOOL_TIP_PLUS = "Increase Font Size For Search Results.";
    public static final String TOOL_TIP_MINUS = "Decrease Font Size For Search Results.";
    public static final SearchResultsButtonAttr BUTTON_ATTR[] = {
            new SearchResultsButtonAttr(APPLY_BUTTON_ID, "Apply",
                TOOL_TIP_APPLY),
            new SearchResultsButtonAttr(CANCEL_BUTTON_ID, "Cancel",
                TOOL_TIP_CANCEL),
            new SearchResultsButtonAttr(PA_BUTTON_ID, "PA", TOOL_TIP_PA),
            new SearchResultsButtonAttr(SO_BUTTON_ID, "SO", TOOL_TIP_SO),
            new SearchResultsButtonAttr(HELP_BUTTON_ID, "Help", TOOL_TIP_HELP),
            new SearchResultsButtonAttr(PLUS_BUTTON_ID, "+", TOOL_TIP_PLUS),
            new SearchResultsButtonAttr(MINUS_BUTTON_ID, "-", TOOL_TIP_MINUS)};
    public static final String GUI_EDIT_MENU_CUT_ITEM_TEXT = "Cut";
    public static final String GUI_EDIT_MENU_COPY_ITEM_TEXT = "Copy";
    public static final String GUI_EDIT_MENU_PASTE_ITEM_TEXT = "Paste";
    public static final String SEARCH_RESULTS_ERROR_TITLE = "Search Results Error";
    public static final String ERRMSG_FIELD_ATTR_TABLE_LOAD_ERROR = "A-SR-0101 SearchResultsConstants.FIELD_ATTR[i].fieldId not equal to ";
    public static final String ERRMSG_BUTTON_ATTR_TABLE_LOAD_ERROR = "A-SR-0103 SearchResultsConstants.BUTTON_ATTR[i].buttonId not equal to ";
    public static final String ERRMSG_UNRECOGNIZED_BUTTON_ID_1 = "A-SR-0201 Unrecognized Button pressed. buttonId = ";
    public static final String SELECTION_NO_SEARCH_RUN_YET_LITERAL = "I-SR-0301 Search not yet run.";
    public static final String STEP_SEARCH_TITLE_LITERAL_1 = "Step Search: Results - ";
    public static final String GENERAL_SEARCH_TITLE_LITERAL_1 = "General Search: Results - ";
    public static final String STEP_SEARCH_TITLE_STEP_LITERAL = "Step ";
    public static final String TITLE_THEOREM_LITERAL = " Theorem ";
    public static final String TITLE_LOC_AFTER_LITERAL = " LOC_AFTER ";
    public static final String TITLE_STMT_LITERAL = " Stmt ";
    public static final String DEFAULT_TITLE = "Search Results, General Search R20121225 @ 20-Sep-2012 20:54";
    public static final String POPUP_ERROR_MESSAGE_TITLE = "Search Error";
    public static final String SEARCH_RESULTS_POPUP_APPLY_BUTTON_CAPTION = "Apply Selection To Step And Unify Proof?";
    public static final String SEARCH_RESULTS_POPUP_SELECTION_CAPTION = "Selected Line";
    public static final String GENERAL_HELP_FRAME_TITLE = "SearchResults Help: General Information, R20121225 @ 20-Sep-2012 20:54";
    public static final String GENERAL_HELP_INFO_TEXT = ""
        + "************\n"
        + "* Contents *\n"
        + "************\n\n"
        + "* Summary Information About The Search Results Window\n\n"
        + "* Main Window Areas Explanation\n\n"
        + "* Input Fields And Buttons, In Alphabetical Order\n\n"
        + "------------------------------------------------------------------------------\n\n"
        + "***********************\n"
        + "* Summary Information *\n"
        + "***********************\n\n"
        + "The Search Results window displays the sorted list of assertions selected by\n"
        + "the most recent Search.\n\n"
        + "The list is 'selectable'. You can 'Apply' a selected list item to a derivation\n"
        + "proof step, or you can 'right-mouse' popup a formatted display of the selected\n"
        + "item for easier viewing. You must be in 'Step Search mode' to use the 'Apply'\n"
        + "feature -- the 'Apply' button is disabled in 'General Search mode'.)\n\n"
        + "To re-sort the Search Results, click the 'SO' button to return to the Search\n"
        + "Options window, then choose a new Output Sort and click the 'Refine' button.\n"
        + "This is very fast because 'Refine' reads just the assertions in the Search\n"
        + "Results!\n\n"
        + "It is similarly convenient to change any of the other arguments on the Search\n"
        + "Options window, and then either Refine the previous search or Search again.\n\n\n"
        + "NOTE: For convenience, due to an issue with code settings for the Search\n"
        + "Results selection 'pane', please use the following RunParms with any desired\n"
        + "customizations to effect your preferred size settings (as opposed to attempting\n"
        + "manual adjustments by 'dragging' the window edges):\n\n"
        + "StepSelectorDialogPaneWidth,720\n"
        + "StepSelectorDialogPaneHeight,440\n\n\n"
        + "------------------------------------------------------------------------------\n\n"
        + "*********************\n"
        + "* Main Window Areas *\n"
        + "*********************\n\n"
        + "-------------\n"
        + "| Title Bar |\n"
        + "-------------\n\n"
        + "Displays either 'Step Search' or 'General Search', plus Theorem or Stmt, and\n"
        + "LOC_AFTER which contain statement labels defining the upper bound of the\n"
        + "search. LOC_AFTER applies only to new theorems and is an inclusive upper\n"
        + "bound.\n\n\n"
        + "---------------------------------\n"
        + "| Search Results Selection List |\n"
        + "---------------------------------\n\n"
        + "The Search Results Selection List is a sorted list of assertions selected by\n"
        + "the most recent Search. For each assertion  one line of output is displayed\n"
        + "for each of:\n\n"
        + "* The assertion's descriptive Metamath Comment statement, if the 'Comments'\n"
        + "Search Option is checked (On).\n\n"
        + "* The assertion's logical hypotheses, if any.\n\n"
        + "* The assertion's conclusion formula.\n\n\n"
        + "Of special note about each assertion's group selection lines:\n\n"
        + "* '(*)' prefixes the first line if the assertion is a 'Completed Search\n"
        + "Result', which means that the assertion unifies with the derivation proof\n"
        + "step on the Proof Asst GUI window and that there are no incomplete hypotheses\n"
        + "-- and thus, the step is 'complete' (though it may still contain Work\n"
        + "Variables.)\n\n"
        + "* If the 'Substitutions' Search Option is checked (On), then in Step\n"
        + "Search mode the assertion formulas are displayed with the unification\n"
        + "substitutions obtained from the originally designated derivation proof step.\n"
        + "Otherwise, if 'Substitutions' is un-checked, the assertion formulas are\n"
        + "shown as-is, the way they appear in the Metamath database.\n\n"
        + "* If Work Variables were assigned during unification, the specific\n"
        + "Work Variables shown may be changed when the selection is 'Apply'ed\n"
        + "to the derivation proof step.\n\n\n"
        + "Note: for all Search Option 'OutputSort' choices except the first, #0,\n"
        + "Completed Search Result lines appear first in the Search Results Selection\n"
        + "List (because they are assigned a Score = 100, the highest defined value.)\n\n\n"
        + "------------------------------------------------------------------------------\n\n"
        + "****************************\n"
        + "* Input Fields And Buttons *\n"
        + "****************************\n\n"
        + "---------\n"
        + "| Apply |\n"
        + "---------\n\n"
        + "The Apply button updates the originally designated derivation proof\n"
        + "step for Step Search mode. The selected assertion's label updates\n"
        + "the 'Ref' field on the proof step and then unification of the\n"
        + "Proof Worksheet is performed.\n\n"
        + "----------\n"
        + "| Cancel |\n"
        + "----------\n\n"
        + "The Cancel button terminates a long-running search, if possible.\n\n\n"
        + "--------\n"
        + "| Help |\n"
        + "--------\n\n"
        + "The Help button displays the help window.\n\n\n"
        + "------\n"
        + "| PA |\n"
        + "------\n\n"
        + "The PA button 'jumps' you to the Proof Asst GUI window.\n\n\n"
        + "------\n"
        + "| SO |\n"
        + "------\n\n"
        + "The SO button 'jumps' you to the Search Options window without affecting\n"
        + "the contents of the Search Results window.\n\n"
        + "------------------------------------------------------------------------------\n\n";

}
