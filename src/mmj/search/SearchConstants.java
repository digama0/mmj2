//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * {{file}}.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

public class SearchConstants {

    public static final int SEARCH_RETURN_CODE_0 = 0;
    public static final int SEARCH_RETURN_CODE_ARG_ERROR = 1;
    public static final int SEARCH_RETURN_CODE_TIMEOUT_ERROR = 2;
    public static final int SEARCH_RETURN_CODE_INTERRUPTED_ERROR = 3;
    public static final int SEARCH_RETURN_CODE_EXECUTION_ERROR = 4;
    public static final int SEARCH_RETURN_CODE_FATAL_ERROR = 16;
    public static final String SEARCH_FONT_FAMILY = "Monospaced";
    public static final String SEARCH_OPTIONS_NEW_STMT_LABEL_PROMPT = ""
        + "Statement label, or a blank?";
    public static final String SEARCH_OPTIONS_NEW_STMT_LABEL_PROMPT_2 = ""
        + "Label %s invalid: not found or not a Statement. Statement label, or a blank?";
    public static final String SEARCH_OUTPUT_LIST_MORE_LITERAL = "***MORE***";
    public static final String SEARCH_OUTPUT_LIST_END_LITERAL = "***END***";
    public static final int SEARCH_SCORE_COMPLETED_ITEM = 100;
    public static final int SEARCH_SCORE_SELECTED_ITEM = 50;
    public static final int SEARCH_SCORE_NOT_SELECTED_ITEM = 0;
    public static final int SEARCH_SCORE_TRAILER_ITEM = -1;
    public static final String COMPLETED_ITEM_OUTPUT_LITERAL = "(*) ";
    public static final String DOT_STEP_CAPTION = ".Step ";
    public static final String SEARCH_OUTPUT_SEARCH_FORMULA_INDENT = "    ";
    public static final String SEARCH_OUTPUT_FORMULA_LABEL_SEPARATOR = " ::= ";
    public static final String SEARCH_OUTPUT_FORMULA_LOG_HYP_SEPARATOR = " &&  ";
    public static final String SEARCH_OUTPUT_FORMULA_YIELDS_SEPARATOR = " ==> ";
    public static final String ERRMSG_PA_GUI_TASK_ALREADY_RUNNING = "A-SE-0101"
        + " Proof Asst/Search task already running. Please try again"
        + " momentarily, or click the 'Cancel' button?";
    public static final String ERRMSG_NO_TASK_TO_CANCEL = "A-SE-0102"
        + " Oops! Did not find Proof Asst/Search task already running. Try"
        + " your original request again?";
    public static final String ERRMSG_TASK_CANCEL_REQUESTED = "A-SE-0103"
        + " Requested cancel of running Proof Asst/Search task. In a moment,"
        + " please retry your original request?";
    public static final String ERRMSG_FUNCTION_INOPERABLE_IN_TEST_MODE = "A-SE-0104"
        + " Requested function inoperable in test mode.";
    public static final String ERRMSG_CANT_APPLY_OLD_SEARCH_RESULTS = "A-SE-0105"
        + " Cannot 'Apply': must be in Step Search mode, or the results are"
        + " 'out of date'.";
    public static final String ERRMSG_SEARCH_ASSRT_LIST_EMPTY_1 = "A-SE-0201"
        + " Input list of assertions for search is empty!";
    public static final String ERRMSG_SEARCH_NULL_PARSE_TREE_1 = "A-SE-0202"
        + " Null parse tree for Hyp's formula, this should have been caught!";
    public static final String ERRMSG_SEARCH_TASK_TIMEOUT_1 = "E-SE-0203"
        + " Search task timed out. Increase MaxTime option, or modify other"
        + " SearchOptions? System Message = ";
    public static final String ERRMSG_SEARCH_TASK_INTERRUPTED_1 = "E-SE-0204"
        + " Search task interrupted. Increase MaxTime option, or modify other"
        + " SearchOptions? System Message = ";
    public static final String ERRMSG_SEARCH_TASK_EXECUTION_1 = "E-SE-0205"
        + " Search task execution exception! System Message = ";
    public static final String ERRMSG_ARG_ERROR_1 = "A-SE-0301"
        + " Validation error for field ";
    public static final String ERRMSG_ARG_ERROR_2 = " value = ";
    public static final String ERRMSG_ARG_ERROR_3 = ". ";
    public static final String ERROR_SEARCH_DATA_LINE_FORMAT_1 = "A-SE-0401"
        + " Program bug! Bad Format choice found while creating SearchDataLine. Value = ";
    public static final String ERROR_SEARCH_DATA_LINE_PART_1 = "A-SE-0402"
        + " Program bug! Bad Part choice found while creating SearchDataLine. Value = ";
    public static final String ERROR_SEARCH_DATA_LINE_IN_WHAT_1 = "A-SE-0403"
        + " Program bug! Bad InWhat choice found while creating SearchDataLine. Value = ";
    public static final String ERROR_SEARCH_DATA_LINE_PART2_1 = "A-SE-0404"
        + " Program bug! Bad Part choice found while creating SearchDataLine. Value = ";
    public static final String ERROR_NO_SEARCH_TERMS_1 = "A-SE-0405"
        + " Severe program error: expected search terms are missing in ForWhat field.";
    public static final String ERROR_MISSING_STARTING_QUOTE_1 = "E-SE-0406"
        + " Next search term missing starting quote.";
    public static final String ERROR_MISSING_END_QUOTE_1 = "E-SE-0407"
        + " Search term end quote missing.";
    public static final String ERROR_EMPTY_SEARCH_TERM_1 = "E-SE-0408"
        + " Empty string search term is invalid.";
    public static final String ERROR_OR_BEFORE_FIRST_SEARCH_TERM_1 = "E-SE-0409"
        + " An 'or' before the first search term is invalid.";
    public static final String ERROR_TWO_ORS_1 = "E-SE-0410"
        + " Only one 'or' is permitted between each pair of search terms.";
    public static final String ERROR_OR_AFTER_LAST_SEARCH_TERM_1 = "E-SE-0411"
        + " An 'or' after the last search term is invalid.";
    public static final String ERROR_SEARCH_TERM_REGEX_COMPILE_1 = "E-SE-0412"
        + " Failed compilation as a Regular Expression. Text = ";
    public static final String ERROR_SEARCH_TERM_REGEX_COMPILE_1_2 = ". System message = ";
    public static final String ERROR_PARSE_EXPR_TERM_COMPILE_1 = "E-SE-0413"
        + " Failed expression parse. Text = ";
    public static final String ERROR_PARSE_EXPR_TERM_COMPILE_1_2 = ". Detailed message = ";
    public static final String ERROR_PARSE_STMT_TERM_COMPILE_1 = "E-SE-0414"
        + " Failed statement parse. Text = ";
    public static final String ERROR_PARSE_STMT_TERM_COMPILE_1_2 = ". Detailed message = ";
    public static final String ERROR_PARSE_STMT_TERM_EVAL_1 = "E-SE-0415"
        + " ParseStmt search term evaluation error. Text = ";
    public static final String ERROR_PARSE_STMT_TERM_EVAL_1_2 = ". Detailed message = ";
    public static final String ERROR_SEARCH_UNIFIER_OPER_CHOICE_1 = "A-SE-0501"
        + " Program bug! Bad Oper value for ParseStmt in SearchUnifier! Value = ";
    public static final String ERROR_SEARCH_UNIFIER_OPER_CHOICE_2 = "A-SE-0502"
        + " Program bug! Bad Oper value for ParseExpr in SearchUnifier! Value = ";
    public static final String ERRMSG_CHOICE_ERROR = " Not a valid choice.";
    public static final String ERRMSG_NEGATIVE_ERROR = " Is less than zero.";
    public static final String ERRMSG_BAD_INT_ERROR = " Is not an integer or is greater than ";
    public static final String ERRMSG_MAX_HYPS_LT_STEP_HYPS_ERROR = " Is less than number of Proof Step Hyps = ";
    public static final String ERRMSG_MIN_HYPS_GT_STEP_HYPS_ERROR = " Is greater than number of Proof Step Hyps = ";
    public static final String ERRMSG_MIN_HYPS_GT_MAX_HYPS_ERROR = " Is greater than number of MaxHyps = ";
    public static final String ERRMSG_REQUIRED_INPUT_ERROR = " Is required. ";
    public static final String ERRMSG_SINGLE_EQ_DOUBLE_QUOTE_ERROR = " Must not equal or match the leading portion of  DoubleQuote, or vice-versa.";
    public static final String ERRMSG_OR_SEPARATOR_EQ_QUOTE_ERROR = " Must not equal or match the leading portion of SingleQuote or DoubleQuote, or vice-versa.";
    public static final String ERRMSG_UNSUPPORTED_FEATURE_ERROR = " Feature not yet supported (not coded...)";
    public static final String ERRMSG_EXCL_LABELS_SPECIFIER_BAD_ERROR = " Is invalid. Must not contain '$$' or invalid regular expression specifiers. Valid example: *OLD,EE*,4??5*";
    public static final String ERRMSG_EXCL_LABELS_SPECIFIER_BAD_ERROR2 = " Failed compilation as a 'Regular Expression'. Valid example: *OLD,EE*,4??5*";
    public static final String ERRMSG_EXCL_LABELS_SPECIFIER_BAD_ERROR2_2 = " System message = ";
}
