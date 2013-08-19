//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchOptionsConstants.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.awt.Color;

public class SearchOptionsConstants {

    public static void CHECK_SEARCH_OPTIONS_FIELD_ATTR_IDS() {
        for (int i = 0; i < FIELD_ATTR.length; i++)
            if (FIELD_ATTR[i].fieldId != i)
                throw new IllegalArgumentException(
                    "A-SO-0101 SearchOptionsConstants.FIELD_ATTR[i].fieldId not equal to "
                        + i);
    }

    public static void CHECK_SEARCH_ARGS_FIELD_IDS(final SearchArgs args) {
        for (int i = 0; i < args.arg.length; i++)
            if (args.arg[i].getFieldId() != i)
                throw new IllegalArgumentException(
                    "A-SO-0102 SearchArgs.arg[i].getFieldId() not equal to "
                        + i);
    }

    public static void CHECK_SEARCH_OPTIONS_BUTTON_ATTR_IDS() {
        for (int i = 0; i < BUTTON_ATTR.length; i++)
            if (BUTTON_ATTR[i].buttonId != i)
                throw new IllegalArgumentException(
                    "A-SO-0103 SearchOptionsConstants.BUTTON_ATTR[i].buttonId"
                        + " not equal to " + i);
    }

    public static final String SEARCH_CONTROLS_LABEL_TEXT = "SEARCH_CONTROLS ";
    public static final String SEARCH_DATA_LABEL_TEXT = "SEARCH_DATA ";
    public static final String EXCLUSION_LABEL_TEXT = "------Exclusion Criteria------";
    public static final String EXCLUSION_LABEL_TOOL_TIP = " Criteria for exclusion from the Search Results.";
    public static final String EXT_SEARCH_LABEL_TEXT = " -------Extended Search--------";
    public static final String EXT_SEARCH_LABEL_TOOL_TIP = "Controls for Extended Search for Completed Search Results using the Search Results List and prior proof steps.";
    public static final String OUTPUT_LABEL_TEXT = " -------Output Controls-------";
    public static final String OUTPUT_LABEL_TOOL_TIP = "Controls search output processing.";
    public static final String TEXT_SEPARATORS_LABEL_TEXT = " Text-Separators:";
    public static final int NBR_SEARCH_DATA_ROWS = 4;
    public static final String IN_WHAT_VALUE_AEP = "$aep";
    public static final String IN_WHAT_VALUE_AE = "$ae";
    public static final String IN_WHAT_VALUE_AP = "$ap";
    public static final String IN_WHAT_VALUE_EP = "$ep";
    public static final String IN_WHAT_VALUE_A = "$a";
    public static final String IN_WHAT_VALUE_E = "$e";
    public static final String IN_WHAT_VALUE_P = "$p";
    public static final String IN_WHAT_VALUE_EQ = "$=";
    public static final int IN_WHAT_TYPE_STATEMENT = 0;
    public static final int IN_WHAT_TYPE_HYP = 1;
    public static final int IN_WHAT_TYPE_SUB_STATEMENT = 2;
    public static final int IN_WHAT_AEP_ID = 0;
    public static final int IN_WHAT_AE_ID = 1;
    public static final int IN_WHAT_AP_ID = 2;
    public static final int IN_WHAT_EP_ID = 3;
    public static final int IN_WHAT_A_ID = 4;
    public static final int IN_WHAT_E_ID = 5;
    public static final int IN_WHAT_P_ID = 6;
    public static final int IN_WHAT_EQ_ID = 7;
    public static final String[] IN_WHAT_VALUES = {"$aep", "$ae", "$ap", "$ep",
            "$a", "$e", "$p", "$="};
    public static final int[] IN_WHAT_TYPE = {IN_WHAT_TYPE_STATEMENT,
            IN_WHAT_TYPE_STATEMENT, IN_WHAT_TYPE_STATEMENT,
            IN_WHAT_TYPE_STATEMENT, IN_WHAT_TYPE_STATEMENT, IN_WHAT_TYPE_HYP,
            IN_WHAT_TYPE_STATEMENT, IN_WHAT_TYPE_SUB_STATEMENT};
    public static final String PART_VALUE_FORMULAS = "Formulas";
    public static final String PART_VALUE_COMMENTS = "Comments";
    public static final String PART_VALUE_LABELS = "Labels";
    public static final String PART_VALUE_LABELS_RPN = "LabelsRPN";
    public static final int PART_TYPE_FORMULA = 0;
    public static final int PART_TYPE_NOT_FORMULA = 1;
    public static final int PART_FORMULAS_ID = 0;
    public static final int PART_COMMENTS_ID = 1;
    public static final int PART_LABELS_ID = 2;
    public static final int PART_LABELS_RPN_ID = 3;
    public static final String[] PART_VALUES = {"Formulas", "Comments",
            "Labels", "LabelsRPN"};
    public static final int[] PART_TYPE = {PART_TYPE_FORMULA,
            PART_TYPE_NOT_FORMULA, PART_TYPE_NOT_FORMULA, PART_TYPE_NOT_FORMULA};
    public static final String[] PART_VALUES_STATEMENT = {"Formulas",
            "Comments", "Labels"};
    public static final String[] PART_VALUES_HYP = {"Formulas", "Labels"};
    public static final String[] PART_VALUES_SUB_STATEMENT = {"LabelsRPN"};
    public static final int FORMAT_TYPE_TREE = 0;
    public static final int FORMAT_TYPE_SUB_TREE = 1;
    public static final int FORMAT_TYPE_NOT_TREE = 2;
    public static final int FORMAT_METAMATH_ID = 0;
    public static final int FORMAT_REG_EXPR_ID = 1;
    public static final int FORMAT_CHAR_STR_ID = 2;
    public static final int FORMAT_PARSE_EXPR_ID = 3;
    public static final int FORMAT_PARSE_STMT_ID = 4;
    public static final String FORMAT_VALUE_METAMATH = "Metamath";
    public static final String FORMAT_VALUE_REG_EXPR = "RegExpr";
    public static final String FORMAT_VALUE_CHAR_STR = "CharStr";
    public static final String FORMAT_VALUE_PARSE_EXPR = "ParseExpr";
    public static final String FORMAT_VALUE_PARSE_STMT = "ParseStmt";
    public static final String[] FORMAT_VALUES = {"Metamath", "RegExpr",
            "CharStr", "ParseExpr", "ParseStmt"};
    public static final int[] FORMAT_ID = {FORMAT_METAMATH_ID,
            FORMAT_REG_EXPR_ID, FORMAT_CHAR_STR_ID, FORMAT_PARSE_EXPR_ID,
            FORMAT_PARSE_STMT_ID};
    public static final int[] FORMAT_TYPE = {FORMAT_TYPE_NOT_TREE,
            FORMAT_TYPE_NOT_TREE, FORMAT_TYPE_NOT_TREE, FORMAT_TYPE_SUB_TREE,
            FORMAT_TYPE_TREE};
    public static final String[] FORMAT_VALUES_TREE = {"Metamath", "RegExpr",
            "CharStr", "ParseExpr", "ParseStmt"};
    public static final String[] FORMAT_VALUES_NOT_TREE = {"Metamath",
            "RegExpr", "CharStr"};
    public static final String OPER_VALUE_EMPTY_STRING = "";
    public static final String OPER_VALUE_NOT = "NOT";
    public static final String OPER_VALUE_LE = "<=";
    public static final String OPER_VALUE_LT = "<";
    public static final String OPER_VALUE_EQ = "=";
    public static final String OPER_VALUE_EQ_EQ = "==";
    public static final String OPER_VALUE_GE = ">=";
    public static final String OPER_VALUE_GT = ">";
    public static final String OPER_VALUE_LT_GT = "<>";
    public static final int OPER_TYPE_TREE = 0;
    public static final int OPER_TYPE_SUB_TREE = 1;
    public static final int OPER_TYPE_NOT_TREE = 2;
    public static final int OPER_VALUE_EMPTY_STRING_ID = 0;
    public static final int OPER_VALUE_NOT_ID = 1;
    public static final int OPER_VALUE_LE_ID = 2;
    public static final int OPER_VALUE_LT_ID = 3;
    public static final int OPER_VALUE_EQ_ID = 4;
    public static final int OPER_VALUE_EQ_EQ_ID = 5;
    public static final int OPER_VALUE_GE_ID = 6;
    public static final int OPER_VALUE_GT_ID = 7;
    public static final int OPER_VALUE_LT_GT_ID = 8;
    public static final String[] OPER_VALUES = {"", "NOT", "<=", "<", "=",
            "==", ">=", ">", "<>"};
    public static final int[] OPER_TYPE = {OPER_TYPE_NOT_TREE,
            OPER_TYPE_NOT_TREE, OPER_TYPE_SUB_TREE, OPER_TYPE_SUB_TREE,
            OPER_TYPE_SUB_TREE, OPER_TYPE_SUB_TREE, OPER_TYPE_SUB_TREE,
            OPER_TYPE_SUB_TREE, OPER_TYPE_SUB_TREE};
    public static final String[] OPER_VALUES_NOT_TREE = {"", "NOT"};
    public static final String[] OPER_VALUES_TREE = {"<=", "<", "=", "==",
            ">=", ">", "<>"};
    public static final String[] OPER_VALUES_SUB_TREE = {"<=", "<", "=", "==",
            ">=", ">", "<>"};
    public static final int MAX_FOR_WHAT_PRIOR_VALUES = 9;
    public static final String FOR_WHAT_VALUE_EMPTY_STRING = "";
    public static final String[] FOR_WHAT_VALUES = {""};
    public static final Color FOR_WHAT_BORDER_COLOR = Color.BLACK;
    public static final int FOR_WHAT_BORDER_THICKNESS = 3;
    public static final String BOOL_VALUE_AND = "AND";
    public static final String BOOL_VALUE_OR = "OR";
    public static final int BOOL_VALUE_AND_ID = 0;
    public static final int BOOL_VALUE_OR_ID = 1;
    public static final String[] BOOL_VALUES = {"AND", "OR"};
    public static final String CHAP_SEC_HIERARCHY_VALUE_EMPTY_STRING = "";
    public static final String CHAP_SEC_HIERARCHY_VALUE_CHAP_DIRECT = "Chap/Direct";
    public static final String CHAP_SEC_HIERARCHY_VALUE_CHAP_INDIR = "Chap/Indir.";
    public static final String CHAP_SEC_HIERARCHY_VALUE_SEC_DIRECT = "Sec/Direct";
    public static final String CHAP_SEC_HIERARCHY_VALUE_SEC_INDIR = "Sec/Indir.";
    public static final int CHAP_SEC_HIERARCHY_EMPTY_STRING_ID = 0;
    public static final int CHAP_SEC_HIERARCHY_CHAP_DIRECT_ID = 1;
    public static final int CHAP_SEC_HIERARCHY_CHAP_INDIR_ID = 2;
    public static final int CHAP_SEC_HIERARCHY_SEC_DIRECT_ID = 3;
    public static final int CHAP_SEC_HIERARCHY_SEC_INDIR_ID = 4;
    public static final String[] CHAP_SEC_HIERARCHY_VALUES = {
            CHAP_SEC_HIERARCHY_VALUE_EMPTY_STRING,
            CHAP_SEC_HIERARCHY_VALUE_CHAP_DIRECT,
            CHAP_SEC_HIERARCHY_VALUE_CHAP_INDIR,
            CHAP_SEC_HIERARCHY_VALUE_SEC_DIRECT,
            CHAP_SEC_HIERARCHY_VALUE_SEC_INDIR};
    public static final int OR_SEPARATOR_FIELD_ID = 0;
    public static final int SINGLE_QUOTE_FIELD_ID = 1;
    public static final int DOUBLE_QUOTE_FIELD_ID = 2;
    public static final int IN_WHAT_0_FIELD_ID = 3;
    public static final int PART_0_FIELD_ID = 4;
    public static final int FORMAT_0_FIELD_ID = 5;
    public static final int OPER_0_FIELD_ID = 6;
    public static final int FOR_WHAT_0_FIELD_ID = 7;
    public static final int BOOL_0_FIELD_ID = 8;
    public static final int IN_WHAT_1_FIELD_ID = 9;
    public static final int PART_1_FIELD_ID = 10;
    public static final int FORMAT_1_FIELD_ID = 11;
    public static final int OPER_1_FIELD_ID = 12;
    public static final int FOR_WHAT_1_FIELD_ID = 13;
    public static final int BOOL_1_FIELD_ID = 14;
    public static final int IN_WHAT_2_FIELD_ID = 15;
    public static final int PART_2_FIELD_ID = 16;
    public static final int FORMAT_2_FIELD_ID = 17;
    public static final int OPER_2_FIELD_ID = 18;
    public static final int FOR_WHAT_2_FIELD_ID = 19;
    public static final int BOOL_2_FIELD_ID = 20;
    public static final int IN_WHAT_3_FIELD_ID = 21;
    public static final int PART_3_FIELD_ID = 22;
    public static final int FORMAT_3_FIELD_ID = 23;
    public static final int OPER_3_FIELD_ID = 24;
    public static final int FOR_WHAT_3_FIELD_ID = 25;
    public static final int BOOL_3_FIELD_ID = 26;
    public static final int EXCL_LABELS_FIELD_ID = 27;
    public static final int MIN_PROOF_REFS_FIELD_ID = 28;
    public static final int RESULTS_CHECKED_FIELD_ID = 29;
    public static final int MAX_TIME_FIELD_ID = 30;
    public static final int MIN_HYPS_FIELD_ID = 31;
    public static final int MAX_EXT_RESULTS_FIELD_ID = 32;
    public static final int SUBSTITUTIONS_FIELD_ID = 33;
    public static final int MAX_HYPS_FIELD_ID = 34;
    public static final int MAX_INCOMP_HYPS_FIELD_ID = 35;
    public static final int COMMENTS_FIELD_ID = 36;
    public static final int MAX_RESULTS_FIELD_ID = 37;
    public static final int PREV_STEPS_CHECKED_FIELD_ID = 38;
    public static final int AUTO_SELECT_FIELD_ID = 39;
    public static final int CHAP_SEC_HIERARCHY_FIELD_ID = 40;
    public static final int REUSE_DERIV_STEPS_FIELD_ID = 41;
    public static final int STATS_FIELD_ID = 42;
    public static final int FROM_CHAP_FIELD_ID = 43;
    public static final int FROM_SEC_FIELD_ID = 44;
    public static final int THRU_CHAP_FIELD_ID = 45;
    public static final int THRU_SEC_FIELD_ID = 46;
    public static final int OUTPUT_SORT_FIELD_ID = 47;
    public static final int[] IN_WHAT_FIELD_ID = {IN_WHAT_0_FIELD_ID,
            IN_WHAT_1_FIELD_ID, IN_WHAT_2_FIELD_ID, IN_WHAT_3_FIELD_ID};
    public static final int[] PART_FIELD_ID = {PART_0_FIELD_ID,
            PART_1_FIELD_ID, PART_2_FIELD_ID, PART_3_FIELD_ID};
    public static final int[] FORMAT_FIELD_ID = {FORMAT_0_FIELD_ID,
            FORMAT_1_FIELD_ID, FORMAT_2_FIELD_ID, FORMAT_3_FIELD_ID};
    public static final int[] OPER_FIELD_ID = {OPER_0_FIELD_ID,
            OPER_1_FIELD_ID, OPER_2_FIELD_ID, OPER_3_FIELD_ID};
    public static final int[] FOR_WHAT_FIELD_ID = {FOR_WHAT_0_FIELD_ID,
            FOR_WHAT_1_FIELD_ID, FOR_WHAT_2_FIELD_ID, FOR_WHAT_3_FIELD_ID};
    public static final int[] BOOL_FIELD_ID = {BOOL_0_FIELD_ID,
            BOOL_1_FIELD_ID, BOOL_2_FIELD_ID, BOOL_3_FIELD_ID};
    public static String OUTPUT_SORT_VALUES[] = {"#0: Don't Re-sort",
            "#1: Score(D)/Complexity(D)/Popularity(D)/Nbr Hyps/MObjSeq(D)",
            "#2: Score(D)/Popularity(D)/Complexity(D)/Nbr Hyps/MObjSeq(D)",
            "#3: Score(D)/Nbr Hyps/Complexity(D)/Popularity(D)/MObjSeq(D)",
            "#4: Score(D)/Nbr Hyps/Popularity(D)/Complexity(D)/MObjSeq(D)",
            "#5: Score(D)/Complexity(D)/MObjSeq(D)",
            "#6: Score(D)/Popularity(D)/MObjSeq(D)",
            "#7: Score(D)/Nbr Hyps/MObjSeq(D)",
            "#8: Score(D)/Nbr Hyps/MObjSeq", "#9: Score(D)/MObjSeq(D)",
            "#10: Score(D)/Label"};
    public static String[] STATS_VALUES = {"#0: No Stats Output",
            "#1: Print Summary Stats",
            "#2: Print Prior Levels Plus Detailed Stats",
            "#3: Print Prior Levels Plus Search Args",
            "#4: Print Prior Levels Plus Search Results",
            "#5: Print Prior Levels Plus Chap/Sec Hierarchy"};
    public static final int STATS_0_ID = 0;
    public static final int STATS_1_ID_SUMMARY_STATS = 1;
    public static final int STATS_2_ID_DETAILED_STATS = 2;
    public static final int STATS_3_ID_SEARCH_ARGS = 3;
    public static final int STATS_4_ID_SEARCH_RESULTS = 4;
    public static final int STATS_5_ID_CHAP_SEC_HIERARCHY = 5;
    public static final String TOOL_TIP_AUTO_SELECT = "AutoSelect:"
        + " Automatically updates proof step using the first Completed Search"
        + " Result in the Search Results List.";
    public static final String TOOL_TIP_BOOL = "Bool:"
        + " Logical operator (AND, OR, XOR, NAND) connecting Search Data lines"
        + " (evaluation order = top to bottom.)";
    public static final String TOOL_TIP_CHAP_SEC_HIERARCHY = "ChapSecHierarchy:"
        + " restricts search domain to hierarchies of related Chapters or"
        + " Sections of the FromChap/FromSec and the Theorem or Stmt/LOC_AFTER"
        + " being searched.";
    public static final String TOOL_TIP_COMMENTS = "Comments:"
        + " causes assertion comments to be displayed in the Step Selector"
        + " Dialog even if comments are not being searched.";
    public static final String TOOL_TIP_DOUBLE_QUOTE = "DoubleQuote:"
        + " Character string used to quote-enclose search arguments";
    public static final String TOOL_TIP_EXCL_LABELS = "ExclLabels:"
        + " Comma-delimited, Metamath search expression(s) to specify assertion"
        + " labels to exclude from the Search Results (e.g. '*OLD,ee*')";
    public static final String TOOL_TIP_FOR_WHAT = "ForWhat:"
        + " search terms, optionally quote-enclosed. ANDed together by default"
        + " unless OR specified. Pulldown list has 9 prior search values for"
        + " the given Format. See Help.";
    public static final String TOOL_TIP_FORMAT = "Format:"
        + " object format used in search matching process.";
    public static final String TOOL_TIP_FROM_CHAP = "FromChap:"
        + " Optional. Specifies a lower bound Chapter of the search domain on"
        + " the input assertion list.";
    public static final String TOOL_TIP_FROM_SEC = "FromSec:"
        + " Optional. Specifies a lower bound Section of the search domain on"
        + " the input assertion list.";
    public static final String TOOL_TIP_IN_WHAT = "InWhat:"
        + " the Metamath statements to be searched for each input Assertion.";
    public static final String TOOL_TIP_MAX_EXT_RESULTS = "MaxExtResults:"
        + " number of Completed Search Results to find. You may be lucky to get"
        + " one! Zero = disable Ext. Search.";
    public static final String TOOL_TIP_MAX_HYPS = "MaxHyps:"
        + " Maximum number of logical hypotheses in assertions included in the"
        + " search domain.";
    public static final String TOOL_TIP_MAX_INCOMP_HYPS = "MaxIncompHyps:"
        + " Specifies the maximum number of incomplete hypotheses in Search"
        + " Result items input to the Extended Search.";
    public static final String TOOL_TIP_MAX_RESULTS = "MaxResults:"
        + " Limits the size of the Search Results List.";
    public static final String TOOL_TIP_MAX_TIME = "MaxTime:"
        + " Maximum elapsed time (seconds) of the search.";
    public static final String TOOL_TIP_MIN_HYPS = "MinHyps:"
        + " Minimum number of logical hypotheses in assertions included in the"
        + " search domain.";
    public static final String TOOL_TIP_MIN_PROOF_REFS = "MinProofRefs:"
        + " Minimum number of times assertion referenced in other proofs in"
        + " order to be included in the search domain.";
    public static final String TOOL_TIP_OPER = "Oper:"
        + " Lets you specify relational operators for the Format types"
        + " ParseExpr and ParseStmt, and boolean operators (blank or 'NOT' )"
        + " for all other Format types. See Help.";
    public static final String TOOL_TIP_OR = "Or:"
        + " Character string used to specify logical OR between two ForWhat"
        + " search terms. AND is the default operator for consecutive search"
        + " terms if OR not specified.";
    public static final String TOOL_TIP_OUTPUT_SORT = "OutputSort:"
        + " Sort order of the Search Results List. (D) signifies descending"
        + " order, otherwise ascending is used.";
    public static final String TOOL_TIP_PART = "Part:"
        + " the part of the Metamath statements to be searched.";
    public static final String TOOL_TIP_PREV_STEPS_CHECKED = "PrevStepsChecked:"
        + " Specifies the maximum number of previous proof steps to be checked"
        + " in the Extended Search not including steps bypassed if"
        + " ReuseDerivSteps: = False.";
    public static final String TOOL_TIP_REUSE_DERIV_STEPS = "ReuseDerivSteps:"
        + " Previous proof steps already referenced in the proof (as Hyps) will"
        + " not be reused in the current step by the Extended Search -- unless"
        + " first Comment token = <SO:REUSE>. See Help.";
    public static final String TOOL_TIP_RESULTS_CHECKED = "ResultsChecked:"
        + " number of (sorted) Search Results input to the Extended Search."
        + " Zero = disable Ext. Search.";
    public static final String TOOL_TIP_SINGLE_QUOTE = "SingleQuote:"
        + " Character string used to quote-enclose search arguments";
    public static final String TOOL_TIP_STATS = "Stats:"
        + " Causes statistics about each search to be produced and output to"
        + " the Request Message window.";
    public static final String TOOL_TIP_SUBSTITUTIONS = "Substitutions:"
        + " Causes unifiable assertions to be displayed in the Step Selector"
        + " Search Dialog with unification substitutions from the Proof"
        + " Worksheet (instead of as-is.)";
    public static final String TOOL_TIP_THRU_CHAP = "ThruChap:"
        + " Optional. Specifies an upper bound Chapter of the search domain on"
        + " the input assertion list.";
    public static final String TOOL_TIP_THRU_SEC = "ThruSec:"
        + " Optional. Specifies a upper bound Section of the search domain on"
        + " the input assertion list.";
    public static final SearchOptionsFieldAttr[] FIELD_ATTR = new SearchOptionsFieldAttr[]{
            new SearchOptionsFieldAttr(OR_SEPARATOR_FIELD_ID, " Or:",
                TOOL_TIP_OR, 4, "OR", false, -1, null),
            new SearchOptionsFieldAttr(SINGLE_QUOTE_FIELD_ID, " SingleQuote:",
                TOOL_TIP_SINGLE_QUOTE, 2, "'", false, -1, null),
            new SearchOptionsFieldAttr(DOUBLE_QUOTE_FIELD_ID, " DoubleQuote:",
                TOOL_TIP_DOUBLE_QUOTE, 2, "\"", false, -1, null),
            new SearchOptionsFieldAttr(IN_WHAT_0_FIELD_ID, "InWhat:",
                TOOL_TIP_IN_WHAT, -1, IN_WHAT_VALUES[2], false, 0,
                IN_WHAT_VALUES),
            new SearchOptionsFieldAttr(PART_0_FIELD_ID, "Part:", TOOL_TIP_PART,
                -1, PART_VALUES[0], false, 0, PART_VALUES),
            new SearchOptionsFieldAttr(FORMAT_0_FIELD_ID, "Format:",
                TOOL_TIP_FORMAT, -1, FORMAT_VALUES[0], false, 0, FORMAT_VALUES),
            new SearchOptionsFieldAttr(OPER_0_FIELD_ID, "Oper:", TOOL_TIP_OPER,
                -1, OPER_VALUES_NOT_TREE[0], false, 0, OPER_VALUES_NOT_TREE),
            new SearchOptionsFieldAttr(FOR_WHAT_0_FIELD_ID, "ForWhat:",
                TOOL_TIP_FOR_WHAT, -1, FOR_WHAT_VALUES[0], false, 0,
                FOR_WHAT_VALUES),
            new SearchOptionsFieldAttr(BOOL_0_FIELD_ID, "Bool:", TOOL_TIP_BOOL,
                -1, BOOL_VALUES[0], false, 0, BOOL_VALUES),
            new SearchOptionsFieldAttr(IN_WHAT_1_FIELD_ID, "InWhat:",
                TOOL_TIP_IN_WHAT, -1, IN_WHAT_VALUES[2], false, 1,
                IN_WHAT_VALUES),
            new SearchOptionsFieldAttr(PART_1_FIELD_ID, "Part:", TOOL_TIP_PART,
                -1, PART_VALUES[0], false, 1, PART_VALUES),
            new SearchOptionsFieldAttr(FORMAT_1_FIELD_ID, "Format:",
                TOOL_TIP_FORMAT, -1, FORMAT_VALUES[1], false, 1, FORMAT_VALUES),
            new SearchOptionsFieldAttr(OPER_1_FIELD_ID, "Oper:", TOOL_TIP_OPER,
                -1, OPER_VALUES_NOT_TREE[0], false, 1, OPER_VALUES_NOT_TREE),
            new SearchOptionsFieldAttr(FOR_WHAT_1_FIELD_ID, "ForWhat:",
                TOOL_TIP_FOR_WHAT, -1, FOR_WHAT_VALUES[0], false, 1,
                FOR_WHAT_VALUES),
            new SearchOptionsFieldAttr(BOOL_1_FIELD_ID, "Bool:", TOOL_TIP_BOOL,
                -1, BOOL_VALUES[0], false, 1, BOOL_VALUES),
            new SearchOptionsFieldAttr(IN_WHAT_2_FIELD_ID, "InWhat:",
                TOOL_TIP_IN_WHAT, -1, IN_WHAT_VALUES[2], false, 2,
                IN_WHAT_VALUES),
            new SearchOptionsFieldAttr(PART_2_FIELD_ID, "Part:", TOOL_TIP_PART,
                -1, PART_VALUES[0], false, 2, PART_VALUES),
            new SearchOptionsFieldAttr(FORMAT_2_FIELD_ID, "Format:",
                TOOL_TIP_FORMAT, -1, FORMAT_VALUES[2], false, 2, FORMAT_VALUES),
            new SearchOptionsFieldAttr(OPER_2_FIELD_ID, "Oper:", TOOL_TIP_OPER,
                -1, OPER_VALUES_TREE[0], false, 2, OPER_VALUES_TREE),
            new SearchOptionsFieldAttr(FOR_WHAT_2_FIELD_ID, "ForWhat:",
                TOOL_TIP_FOR_WHAT, -1, FOR_WHAT_VALUES[0], false, 2,
                FOR_WHAT_VALUES),
            new SearchOptionsFieldAttr(BOOL_2_FIELD_ID, "Bool:", TOOL_TIP_BOOL,
                -1, BOOL_VALUES[0], false, 2, BOOL_VALUES),
            new SearchOptionsFieldAttr(IN_WHAT_3_FIELD_ID, "InWhat:",
                TOOL_TIP_IN_WHAT, -1, IN_WHAT_VALUES[2], false, 3,
                IN_WHAT_VALUES),
            new SearchOptionsFieldAttr(PART_3_FIELD_ID, "Part:", TOOL_TIP_PART,
                -1, PART_VALUES[0], false, 3, PART_VALUES),
            new SearchOptionsFieldAttr(FORMAT_3_FIELD_ID, "Format:",
                TOOL_TIP_FORMAT, -1, "ParseStmt", false, 3, FORMAT_VALUES),
            new SearchOptionsFieldAttr(OPER_3_FIELD_ID, TOOL_TIP_OPER, "Oper:",
                -1, "<=", false, 3, OPER_VALUES_TREE),
            new SearchOptionsFieldAttr(FOR_WHAT_3_FIELD_ID, "ForWhat:",
                TOOL_TIP_FOR_WHAT, -1, FOR_WHAT_VALUES[0], false, 3,
                FOR_WHAT_VALUES),
            new SearchOptionsFieldAttr(BOOL_3_FIELD_ID, "Bool:", TOOL_TIP_BOOL,
                -1, BOOL_VALUES[0], false, 3, BOOL_VALUES),
            new SearchOptionsFieldAttr(EXCL_LABELS_FIELD_ID, "  ExclLabels:",
                TOOL_TIP_EXCL_LABELS, 15, "", true, -1, null),
            new SearchOptionsFieldAttr(MIN_PROOF_REFS_FIELD_ID,
                "MinProofRefs:    ", TOOL_TIP_MIN_PROOF_REFS, 6, "0", true, -1,
                null),
            new SearchOptionsFieldAttr(RESULTS_CHECKED_FIELD_ID,
                " ResultsChecked:  ", TOOL_TIP_RESULTS_CHECKED, 6, "0", true,
                -1, null),
            new SearchOptionsFieldAttr(MAX_TIME_FIELD_ID, " MaxTime:      ",
                TOOL_TIP_MAX_TIME, 6, "1", true, -1, null),
            new SearchOptionsFieldAttr(MIN_HYPS_FIELD_ID, "MinHyps:         ",
                TOOL_TIP_MIN_HYPS, 6, "0", true, -1, null),
            new SearchOptionsFieldAttr(MAX_EXT_RESULTS_FIELD_ID,
                " MaxExtResults:   ", TOOL_TIP_MAX_EXT_RESULTS, 6, "0", true,
                -1, null),
            new SearchOptionsFieldAttr(SUBSTITUTIONS_FIELD_ID,
                " Substitutions:", TOOL_TIP_SUBSTITUTIONS, -1, "true", true,
                -1, null),
            new SearchOptionsFieldAttr(MAX_HYPS_FIELD_ID, "MaxHyps:         ",
                TOOL_TIP_MAX_HYPS, 6, "99", true, -1, null),
            new SearchOptionsFieldAttr(MAX_INCOMP_HYPS_FIELD_ID,
                " MaxIncompHyps:   ", TOOL_TIP_MAX_INCOMP_HYPS, 6, "0", true,
                -1, null),
            new SearchOptionsFieldAttr(COMMENTS_FIELD_ID, " Comments:     ",
                TOOL_TIP_COMMENTS, -1, "true", true, -1, null),
            new SearchOptionsFieldAttr(MAX_RESULTS_FIELD_ID,
                "MaxResults:      ", TOOL_TIP_MAX_RESULTS, 6, "999999", true,
                -1, null),
            new SearchOptionsFieldAttr(PREV_STEPS_CHECKED_FIELD_ID,
                " PrevStepsChecked:", TOOL_TIP_PREV_STEPS_CHECKED, 6, "0",
                true, -1, null),
            new SearchOptionsFieldAttr(AUTO_SELECT_FIELD_ID, " AutoSelect:   ",
                TOOL_TIP_AUTO_SELECT, 6, "false", true, -1, null),
            new SearchOptionsFieldAttr(CHAP_SEC_HIERARCHY_FIELD_ID,
                "ChapSecHierarchy:", TOOL_TIP_CHAP_SEC_HIERARCHY, -1, "", true,
                -1, CHAP_SEC_HIERARCHY_VALUES),
            new SearchOptionsFieldAttr(REUSE_DERIV_STEPS_FIELD_ID,
                " ReuseDerivSteps: ", TOOL_TIP_REUSE_DERIV_STEPS, -1, "false",
                true, -1, null),
            new SearchOptionsFieldAttr(STATS_FIELD_ID, " Stats:        ",
                TOOL_TIP_STATS, 6, "5", true, -1, null),
            new SearchOptionsFieldAttr(FROM_CHAP_FIELD_ID, "FromChap:  ",
                TOOL_TIP_FROM_CHAP, -1, "", true, -1, null),
            new SearchOptionsFieldAttr(FROM_SEC_FIELD_ID, "FromSec:   ",
                TOOL_TIP_FROM_SEC, -1, "", true, -1, null),
            new SearchOptionsFieldAttr(THRU_CHAP_FIELD_ID, "ThruChap:  ",
                TOOL_TIP_THRU_CHAP, -1, "", true, -1, null),
            new SearchOptionsFieldAttr(THRU_SEC_FIELD_ID, "ThruSec:   ",
                TOOL_TIP_THRU_SEC, -1, "", true, -1, null),
            new SearchOptionsFieldAttr(OUTPUT_SORT_FIELD_ID, "OutputSort:",
                TOOL_TIP_OUTPUT_SORT, -1, OUTPUT_SORT_VALUES[0], true, -1,
                OUTPUT_SORT_VALUES)};
    public static final int SEARCH_BUTTON_ID = 0;
    public static final int CANCEL_BUTTON_ID = 1;
    public static final int NEW_BUTTON_ID = 2;
    public static final int REFINE_BUTTON_ID = 3;
    public static final int SET_DEF_BUTTON_ID = 4;
    public static final int PA_BUTTON_ID = 5;
    public static final int SR_BUTTON_ID = 6;
    public static final int HELP_BUTTON_ID = 7;
    public static final int PLUS_BUTTON_ID = 8;
    public static final int MINUS_BUTTON_ID = 9;
    public static final int RESET_DATA_BUTTON_ID = 10;
    public static final int RESET_CONTROLS_BUTTON_ID = 11;
    public static final int NBR_BUTTONS = 12;
    public static final String TOOL_TIP_SEARCH = "Run the search!";
    public static final String TOOL_TIP_CANCEL = "Cancel the search.";
    public static final String TOOL_TIP_NEW = "Begin a new search.";
    public static final String TOOL_TIP_REFINE = "Refine the prior search using"
        + " its Search Results as input.";
    public static final String TOOL_TIP_SET_DEF = "Set defaults to current"
        + " Search Data/Control values (except ForWhat:)";
    public static final String TOOL_TIP_PA = "Jump to the Proof Assistant window"
        + " (without searching.)";
    public static final String TOOL_TIP_SR = "Jump to the Search Results window"
        + " (without searching.)";
    public static final String TOOL_TIP_HELP = "Display Help for Search Options.";
    public static final String TOOL_TIP_PLUS = "Increase Font Size For Search Options.";
    public static final String TOOL_TIP_MINUS = "Decrease Font Size For Search Options.";
    public static final String TOOL_TIP_RESET_DATA = "Reset the Search Data"
        + " fields to the default settings.";
    public static final String TOOL_TIP_RESET_CONTROLS = "Reset the Search Control"
        + " fields to the default settings.";
    public static final SearchOptionsButtonAttr BUTTON_ATTR[] = {
            new SearchOptionsButtonAttr(SEARCH_BUTTON_ID, "Search",
                TOOL_TIP_SEARCH),
            new SearchOptionsButtonAttr(CANCEL_BUTTON_ID, "Cancel",
                TOOL_TIP_CANCEL),
            new SearchOptionsButtonAttr(NEW_BUTTON_ID, "New:", TOOL_TIP_NEW),
            new SearchOptionsButtonAttr(REFINE_BUTTON_ID, "Refine",
                TOOL_TIP_REFINE),
            new SearchOptionsButtonAttr(SET_DEF_BUTTON_ID, "SetDef",
                TOOL_TIP_SET_DEF),
            new SearchOptionsButtonAttr(PA_BUTTON_ID, "PA", TOOL_TIP_PA),
            new SearchOptionsButtonAttr(SR_BUTTON_ID, "SR", TOOL_TIP_SR),
            new SearchOptionsButtonAttr(HELP_BUTTON_ID, "Help", TOOL_TIP_HELP),
            new SearchOptionsButtonAttr(PLUS_BUTTON_ID, "+", TOOL_TIP_PLUS),
            new SearchOptionsButtonAttr(MINUS_BUTTON_ID, "-", TOOL_TIP_MINUS),
            new SearchOptionsButtonAttr(RESET_DATA_BUTTON_ID, "ResetData",
                TOOL_TIP_RESET_DATA),
            new SearchOptionsButtonAttr(RESET_CONTROLS_BUTTON_ID,
                "ResetControls", TOOL_TIP_RESET_CONTROLS)};
    public static final String GUI_EDIT_MENU_CUT_ITEM_TEXT = "Cut";
    public static final String GUI_EDIT_MENU_COPY_ITEM_TEXT = "Copy";
    public static final String GUI_EDIT_MENU_PASTE_ITEM_TEXT = "Paste";
    public static final String SEARCH_OPTIONS_ERROR_TITLE = "Search Options Error";
    public static final String ERRMSG_FIELD_ATTR_TABLE_LOAD_ERROR = "A-SO-0101"
        + " SearchOptionsConstants.FIELD_ATTR[i].fieldId not equal to ";
    public static final String ERRMSG_SEARCH_ARGS_TABLE_LOAD_ERROR = "A-SO-0102"
        + " SearchArgs.arg[i].getFieldId() not equal to ";
    public static final String ERRMSG_BUTTON_ATTR_TABLE_LOAD_ERROR = "A-SO-0103"
        + " SearchOptionsConstants.BUTTON_ATTR[i].buttonId not equal to ";
    public static final String ERRMSG_ARG_INTEGER_TEXT_INVALID = "A-SO-0501"
        + " Severe program bug found: SearchArgsInt text value not numeric."
        + " Value = %s. fieldId = %d field label = %s";
    public static final String ERRMSG_IN_WHAT_SEL_INVALID = "A-SO-0601"
        + " Severe program bug found: InWhatScrnMap selected item invalid. Item value = %s";
    public static final String ERRMSG_PART_SEL_INVALID = "A-SO-0701"
        + " Severe program bug found: PartScrnMap selected item invalid. Item value = %s";
    public static final String ERRMSG_FORMAT_SEL_INVALID = "A-SO-0801"
        + " Severe program bug found: FormatScrnMap selected item invalid. Item value = %s";
    public static final String ERRMSG_OPER_SEL_INVALID = "A-SO-0901"
        + " Severe program bug found: OperScrnMap selected item invalid. Item value = %s";
    public static final String ERRMSG_FROM_CHAP_SEL_INVALID = "A-SO-1001"
        + " Severe program bug found: FromChapScrnMap selected item invalid. Item value = %s";
    public static final String ERRMSG_THRU_CHAP_SEL_INVALID = "A-SO-1101"
        + " Severe program bug found: ThruChapScrnMap selected item invalid. Item value = %s";
    public static final String ERRMSG_UNRECOGNIZED_BUTTON_ID = "A-SO-1202"
        + " Unrecognized Button pressed. buttonId = %d";
    public static final String STEP_SEARCH_TITLE_LITERAL_1 = "Step Search: Options - ";
    public static final String GENERAL_SEARCH_TITLE_LITERAL_1 = "General Search: Options - ";
    public static final String STEP_SEARCH_TITLE_STEP_LITERAL = "Step ";
    public static final String TITLE_THEOREM_LITERAL = " THEOREM=";
    public static final String TITLE_LOC_AFTER_LITERAL = " LOC_AFTER=";
    public static final String TITLE_STMT_LITERAL = " Stmt ";
    public static final String DEFAULT_TITLE = "Search Options, General Search R20121225 @ 20-Sep-2012 20:54";
    public static final String POPUP_ERROR_MESSAGE_TITLE = "Search Error";
    public static final String[] DEFAULT_CHAP_VALUES = {""};
    public static final String[][] DEFAULT_CHAP_SEC_VALUES = {{""}};
    public static final String[][] INITIAL_FOR_WHAT_PRIOR_VALUES = {
            {"                               "},
            {"                               "},
            {"                               "},
            {"                               "}};
    public static final String GENERAL_HELP_FRAME_TITLE = "SearchOptions Help: General Information, R20121225 @ 20-Sep-2012 20:54";
    public static final String GENERAL_HELP_INFO_TEXT = ""
        + "************\n"
        + "* Contents *\n"
        + "************\n\n"
        + "* Summary Information About The Search Options Window\n\n"
        + "* Main Window Areas Explanation\n\n"
        + "* Input Fields And Buttons, In Alphabetical Order\n\n"
        + "------------------------------------------------------------------------------\n\n"
        + "***********************\n"
        + "* Summary Information *\n"
        + "***********************\n\n"
        + "The main purpose of the Search Options window is to help you find assertions\n"
        + "-- either axioms or theorems -- that can be used to justify a proof step. Or,\n"
        + "to say it another way, to find assertions that are 'unifiable'; where the\n"
        + "proof step, including its Hyp entry, is an instance of each assertion.\n\n"
        + "This is called 'Step Search mode'. In Step Search mode the requirement that\n"
        + "every search result is unifiable is implicit and applied automatically in\n"
        + "addition to any other search criteria you specify.\n\n"
        + "Nuances of Step Search are explained in mmj2/doc/StepSearch.html (previously\n"
        + "named StepSelectorSearch.html.)\n\n"
        + "To enter Step Search mode simply double-click a derivation proof step on the\n"
        + "Proof Asst GUI window, or position the cursor to a step and select 'Step\n"
        + "Search' from the Search menu or the right-mouse button popup window. Step\n"
        + "Search goes directly to the Search Results window, initially bypassing Search\n"
        + "Options (though you can jump back to Search Options and refine your search.)\n"
        + "Alternatively, you can select Search Options on the Proof Asst GUI window and\n"
        + "if the cursor is positioned on a derivation proof step, the Search Options\n"
        + "window is displayed in 'Step Search' mode.\n\n"
        + "'General Search mode' is the other modus operandi of Search Options. It is not\n"
        + "tied to a particular proof step, or even to a particular theorem if you prefer\n"
        + "a 'global' search of the Metamath database loaded into mmj2. General Search\n"
        + "allows you to search for assertions, and to snoop around, in cases where you\n"
        + "are, perhaps, still trying to develop a proof strategy.\n\n"
        + "To access General Search mode, select it from the Proof Asst GUI search menu\n"
        + "or the right-mouse button popup menu, or position the cursor anywhere other\n"
        + "than a derivation proof step and select the 'Search Options' menu item. You\n"
        + "can also initiate a new search on the Search Options window -- which will be,\n"
        + "by definition, a General Search.\n\n"
        + "Certain search options are disabled in General Search mode: the Extended\n"
        + "Search Options, Substitutions, and Auto-Select.\n\n\n"
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
        + "---------------\n"
        + "| Search Data |\n"
        + "---------------\n\n"
        + "Consists of the Text Separators fields plus four lines of search criteria. At\n"
        + "start-up the ForWhat Search Data fields are blank, but afterwards the contents\n"
        + "of the Search Data fields are never changed by the program.\n\n"
        + "* Text Separators are intended for use when you are searching for text that\n"
        + "contains 'OR', single quotes or double quotes. Use '$(' or '$)' as single or\n"
        + "double quote symbols to guarantee conflict-free searching (due to the\n"
        + "specifications in the Metamath.pdf.)\n\n"
        + "* The four Search Data lines are optional, and a line with a blank ForWhat\n"
        + "field is ignored completely. Evaluation of the Search Data against assertions\n"
        + "is performed line by line from top to bottom, and is halted as soon as truth\n"
        + "or falsity can be determined. (This eliminates the need for parentheses but\n"
        + "may require some getting used to...)\n\n"
        + "* Multiple search terms can be entered in a ForWhat field. They should be\n"
        + "quote-enclosed. If 'OR' is not specified between two search terms in a ForWhat\n"
        + "field the default operation 'AND' is assumed. Evaluation is from left to right\n"
        + "within ForWhat. Evaluation halts as soon as truth or falsity can be determined\n"
        + "(again, eliminating the need for parentheses...)\n\n\n"
        + "-------------------\n"
        + "| Search Controls |\n"
        + "-------------------\n\n"
        + "Consists of Search Options that can be categorized (loosely) as Exclusion\n"
        + "Criteria, Extended Search controls and Output Controls.\n\n"
        + "* Exclusion Controls are search parameters (or arguments if you prefer) that\n"
        + "can be used to narrow the search range.\n\n"
        + "* Extended Search is a somewhat quixotic algorithm to find your proofs for\n"
        + "you. It uses a depth 1.5 search and does two things: it examines the sorted,\n"
        + "output Search Results looking for assertions whose hypotheses are completely\n"
        + "satisfied by the given proof step hypotheses, and if necessary -- to fulfill\n"
        + "its quota -- it automatically checks prior proof steps and assertions with no\n"
        + "hypotheses to supply missing hypotheses.\n\n"
        + "* Output Controls specify how the Search Results should be presented and how\n"
        + "long you are willing to wait for the search to complete :-) The 'Auto-Select'\n"
        + "option works (optimistically) in conjunction with Extended Search: the first\n"
        + "justifying assertion with no missing hypotheses is used to automatically\n"
        + "update the proof step on the Proof Worksheet (just another reason to be glad\n"
        + "that the proof asst. Gui has undo :-)\n\n"
        + "------------------------------------------------------------------------------\n\n"
        + "****************************\n"
        + "* Input Fields And Buttons *\n"
        + "****************************\n\n"
        + "----------------\n"
        + "| Auto-Select: |\n"
        + "----------------\n\n"
        + "In Step Search mode, when Auto-Select is 'On' the first completed search\n"
        + "result in the sorted Search Results is automatically selected to update the\n"
        + "user-designated proof step in the Proof Worksheet.\n\n"
        + "'Completed Search Result' items have unifiable assertions whose hypotheses are\n"
        + "fully satisfied by the Hyp field of the designated proof step -- I.E. no\n"
        + "missing hyps. For example, assume the assertion is ax-mp and it is unifiable\n"
        + "with proof step 3 of theorem xyz, for which the user specified Hyp field '1,2'\n"
        + "(or '2,1', or '1,2,?', etc.) ...or... The Extended Search feature discovered\n"
        + "steps 1 and/or 2 as suitable hypotheses for ax-mp (in which case the user\n"
        + "could have specified the Hyp field as simply '?' -- or '1,?', or '2,?', or\n"
        + "'?,1', or '?,2').\n\n\n"
        + "---------\n"
        + "| Bool: |\n"
        + "---------\n\n"
        + "Boolean operator connecting a Search Data line to the following line.\n\n"
        + "* AND = assertion rejected unless both Search Data lines are satisfied.\n\n"
        + "* OR = assertion rejected if neither Search Data line is satisfied.\n\n\n"
        + "---------------------\n"
        + "| ChapSecHierarchy: |\n"
        + "---------------------\n\n"
        + "The purpose of the ChapSecHierarchy option is to preemptively eliminate\n"
        + "irrelevant assertions from the Search Results:\n\n"
        + "* If 'On' the ChapSecHierarchy option restricts the search domain to\n"
        + "assertions belonging to the defined hierarchy of Chapters or Sections.\n\n"
        + "* Hierarchies are based on the proof relationships of theorems in a Chapter\n"
        + "or Section to theorems and axioms defined in other Chapters or Sections --\n"
        + "plus, by definition, for the convenience of Search Options users, a\n"
        + "Chapter or Section is always related to itself.\n\n\n"
        + "The options are are follows:\n\n"
        + "* blank = turn off ChapSecHierarchy feature\n\n"
        + "* Chap/Direct = Granularity of hierarchies is at the Metamath Chapter level;\n"
        + "  and hierarchies are restricted to direct proof relationships between\n"
        + "  Chapters.\n\n"
        + "* Chap/Indir. = Granularity of hierarchies is at the Metamath Chapter level;\n"
        + "  and hierarchies include both direct and indirect proof relationships\n"
        + "  between Chapters.\n\n"
        + "* Sec/Direct = Granularity of hierarchies is at the Metamath Section level;\n"
        + "  and hierarchies are restricted to direct proof relationships between\n"
        + "  Sections.\n\n"
        + "* Sec/Indir. = Granularity of hierarchies is at the Metamath Section level;\n"
        + "  and hierarchies include both direct and indirect proof relationships\n"
        + "  between Sections.\n\n\n"
        + "Either one or two hierarchies can be specified; if two are specified then\n"
        + "their set union is used -- that is, a composite hierarchy is used.\n\n"
        + "* Hierarchy 1 has as root (or apex) the Chapter or Section of the Theorem/\n"
        + "  Stmt/LOC_AFTER of the search;\n\n"
        + "* Hierarchy 2 has as root the ThruChap or ThruSec Chapter or Section.\n\n\n"
        + "The FromChap, FromSec, ThruChap and ThruSec fields are used to 'clip' the\n"
        + "lower and upper endpoints hierarchy ranges:\n\n"
        + "* The lower endpoint of the hierarcies' range of Chapters or Sections is given\n"
        + "  by FromChap or FromSec, respectively; if both are blank then the low end of\n"
        + "  the hiararchies is the start of the database.\n\n"
        + "* The upper endpoint of the hierarchies is given by the Chapter or Section of\n"
        + "  the Theorem/Stmt/LOC_AFTER and the ThruChap or ThruSec, whichever is lower.\n\n\n"
        + "A global search, with blank Theorem/Stmt/LOC_AFTER, and blank ThruChap and\n"
        + "ThruSec effectively disables the ChapSecHierarchy option.\n\n\n"
        + "-------------\n"
        + "| Comments: |\n"
        + "-------------\n\n"
        + "If 'On', each assertion's Comment statement is displayed in the Search Results\n"
        + "even if Comments were not part of the search criteria -- if they were,\n"
        + "Comments are displayed even if this option is 'Off'.\n\n"
        + "The Comment displayed is the Metamath $( $) statement immediately preceding\n"
        + "the $a or $p statement of the assertion. $e Comments are never displayed (and\n"
        + "in fact, cannot be searched.)\n\n\n"
        + "----------------\n"
        + "| DoubleQuote: |\n"
        + "----------------\n\n"
        + "DoubleQuote (and SingleQuote) let you change the character(s) you will use to\n"
        + "quote-enclose search terms in the ForWhat fields.\n\n"
        + "Quotes are necessary only if more than one search term is used in a single\n"
        + "ForWhat field.\n\n"
        + "Hint: '$' is guaranteed by the Metamath.pdf spec to never occur in a label of\n"
        + "formula, while '$(' and '$)' cannot occur inside Comment statements.\n\n\n"
        + "---------------\n"
        + "| ExclLabels: |\n"
        + "---------------\n\n"
        + "The purpose of ExclLabels: is to exclude assertions with labels matching\n"
        + "the given label specifications.\n\n"
        + "This option uses a superset of Metamath 'search' command label format\n"
        + "specification:\n\n"
        + "* Wildcard '$*' or '*' accepted, signifies 0 or more of any label character.\n\n"
        + "* Wildcard '$?' or '?' ok, signifies 0 or 1 of any label character\n\n"
        + "* mmj2 converts the ExclLabels search terms into Java Regular Expression\n"
        + "('regex') terms and 'compiles' them -- taking due care to handle Metamath\n"
        + "label symbol '.' as a quoted character in the compiled regex expression.\n\n"
        + "Search terms are delimited by either whitespace or commas (',') and are\n"
        + "implicitly OR'ed. Examples: *OLD,ee* and *OLD EE* are valid.\n\n"
        + "Note: two consecutive '$' characters are invalid by definition to outlaw\n"
        + "problems with recursive specifiers such as '$$*?' and '$$*' -- the program\n"
        + "makes one pass through the label specifier, left to right, and therefore\n"
        + "'$$*' does not tranlate to '*', and is an error.\n\n\n"
        + "-----------\n"
        + "| Format: |\n"
        + "-----------\n\n"
        + "There are five different search term formats.\n\n"
        + "IMPORTANT TO KNOW: Formats 'Metamath', 'RegExpr' and 'CharStr' operate\n"
        + "on 'normalized' string versions of the underlying Metamath objects -- formulas,\n"
        + "comments, labels and RPN proof label lists. The 'normalized' string\n"
        + "consists of the non-whitespace math, label and (lower-cased) Comment tokens\n"
        + "separated by single, individual space characters, ' '.\n\n"
        + "NOTE: Comment tokens are converted internally to lower-case for searching\n"
        + "purposes, as are the Metamath and CharStr ForWhat search strings -- RegExpr\n"
        + "ForWhat search strings should be written to target lower-case Comment tokens.)\n\n"
        + "The other two Formats, 'ParseExpr' and 'ParseStmt' operate on syntactic\n"
        + "parse trees (and so are restricted to formula objects...) When using these\n"
        + "Formats the contents of the ForWhat field are parsed into syntax trees for\n"
        + "use in the searching process.\n\n"
        + "* Metamath : very similar to the RegExpr format except that the Metamath\n"
        + "  Format uses the '$?' and '$*' wildcards instead of '.?' and '.*' --\n"
        + "  signifying 1 character of anything, and 0 or more characters or tokens of any\n"
        + "  value, respectively. When searching labels, '$?' and '$*' can be abbreviated\n"
        + "  to just '?' and '*'. (Note: Metamath Format ForWhat values are converted\n"
        + "  internally by mmj2 into RegExpr values, so you might see an error message\n"
        + "  talking about Regular Expression errors...) Also, Comment searches are not\n"
        + "  case sensitive.\n\n"
        + "* RegExpr : uses the Java-defined version of regular expressions as defined at\n"
        + "  http://docs.oracle.com/javase/tutorial/essential/regex/index.html. The\n"
        + "  ForWhat value is 'compiled' into Java Regular Expression object(s), and\n"
        + "  so must be valid according to the aforementioned Java specs.\n\n"
        + "* CharStr : a character string search for at least one occurrence of the\n"
        + "  search term within the normalized character string version of the Metamath\n"
        + "  object. Comment searches are not case sensitive.\n\n"
        + "* ParseExpr : a ParseExpr search term is parsed to construct a parse tree for\n"
        + "  the expression (may be any Metamath type, not just wff). The expression's\n"
        + "  parse tree is then used in a unification-like process to search for any\n"
        + "  occurrence of the ParseExpr in a formula parse tree. The precise match\n"
        + "  requirement is given by the Oper field -- please refer to the Oper help text\n"
        + "  for details. Note: ParseExpr search terms may not contain Work Variables.\n\n"
        + "* ParseStmt : a ParseStmt search term is parsed to construct a parse tree for\n"
        + "  the statement (in set.mm it must be a wff). The statement's parse tree is\n"
        + "  then used in a unification-like process to search for a match with a formula\n"
        + "  parse tree. The precise match requirement is given by the Oper field --\n"
        + "  please refer to the Oper help text for details. Note: ParseExpr search terms\n"
        + "  may not contain Work Variables.\n\n\n"
        + "------------\n"
        + "| ForWhat: |\n"
        + "------------\n\n"
        + "The ForWhat field is used to input one or more search terms on a search data\n"
        + "lines.\n\n"
        + "IMPORTANT TO KNOW: ForWhat is optional! Pressing 'Search' with all ForWhat\n"
        + "fields blank still produces a Search Result -- the contents of the input\n"
        + "assertion list minus those rejected because of the Exclusion Criteria, or\n"
        + "if in Step Search mode beccause unification failed.\n\n"
        + "* If left blank that Search Data line is ignored.\n\n"
        + "* Quotes are required if more than one search term is input in ForWhat.\n\n"
        + "* If the ForWhat field begins with a SingleQuote or a DoubleQuote then the\n"
        + "contents of ForWhat must be a space-delimited list of quoted search terms,\n"
        + "optionally interspersed with occurrences of the Or: field string (likely\n"
        + "to be 'OR').\n\n"
        + "* Note: an empty-string search term is invalid.\n\n"
        + "* If ForWhat does not begin with either a SingleQuote or a DoubleQuote then\n"
        + "the contents of ForWhat are taken to be a single, unquoted search term (which\n"
        + "is still subject to further validation.)\n\n"
        + "* 'OR' or the value specified in the or field can be specified between any two\n"
        + "ForWhat search terms. 'AND' is assumed if two search terms are not separated\n"
        + "by an 'OR'.\n\n"
        + "* Evaluation of multiple search terms in ForWhat is performed from left to\n"
        + "right with evaluation halting as soon as truth or falsity can be determined.\n"
        + "Parentheses are not used to change the evaluation order.\n\n"
        + "* Search terms are validated according to the Format field specification at\n"
        + "the start of the search (i.e. not when you tab or cursor out of ForWhat after\n"
        + "input.) Errors, such as an invalid RegExpr, or a parse error in a ParseExpr or\n"
        + "ParseStmt produce an error message displayed in a popup message window.\n\n"
        + "* Each ForWhat field has a pull-down list that provides the nine most recently\n"
        + "searched for values.\n\n"
        + "* The precise meaning and format of search terms depends on the Format field\n"
        + "on the same line. Please refer to the Format help text for additional\n"
        + "information.\n\n\n"
        + "-------------\n"
        + "| FromChap: |\n"
        + "-------------\n\n"
        + "FromChap specifies the (inclusive) low end of the range of Chapters input to\n"
        + "the search. Only assertions defined in the specified Chapter or later are\n"
        + "input.\n\n"
        + "If FromChap is blank then the low end of the range is the start of the\n"
        + "Metamath database -- and FromSec is automatically set to blank.\n\n"
        + "Note: FromChap and FromSec are also used to 'clip' or restrict the range of\n"
        + "the hierarchy(s) used by the ChapSecHierarchy feature.\n\n\n"
        + "------------\n"
        + "| FromSec: |\n"
        + "------------\n\n"
        + "FromSec specifies the (inclusive) low end of the range of Sections within the\n"
        + "current FromChap Chapter input to the search. Only assertions defined in the\n"
        + "specified Chapter/Section or later are input.\n\n"
        + "If FromSec is blank then the low end of the range is the start of current\n"
        + "FromChap Chapter, or the start of the Metamath database if FromChap is blank.\n\n"
        + "Note: FromChap and FromSec are also used to 'clip' or restrict the range of\n"
        + "the hierarchy(s) used by the ChapSecHierarchy feature.\n\n\n"
        + "----------\n"
        + "| Cancel |\n"
        + "----------\n\n"
        + "The Cancel button terminates a long-running search, if possible.\n\n\n"
        + "--------\n"
        + "| Help |\n"
        + "--------\n\n"
        + "The Help button displays the help window.\n\n\n"
        + "-----------\n"
        + "| InWhat: |\n"
        + "-----------\n\n"
        + "InWhat is a pulldown list field on each Search Data line that lets you specify\n"
        + "which assertion types and associated Metamath statements will be searched\n"
        + "using the criteria given on the Search Data line:\n\n"
        + "* $aep = axioms, theorems and associated (logical) hypotheses (not varHyps).\n"
        + "* $ae = axioms and associated (logical) hypotheses (not varHyps).\n"
        + "* $ap = axioms and theorems but not hypotheses.\n"
        + "* $ep = theorems and associated (logical) hypotheses (not varHyps).\n"
        + "* $a = axioms but not the associated hypotheses.\n"
        + "* $e = just the logical hypotheses of axioms and theorems.\n"
        + "* $p = theorems but not the associated hypotheses.\n"
        + "* $= RPN proof label list on theorems.\n\n"
        + "Note: the Comment statement immediately preceding each axiom and theorem is\n"
        + "automatically included and can be searched, but hypothesis Comments are not\n"
        + "available.\n\n\n"
        + "------------------\n"
        + "| MaxExtResults: |\n"
        + "------------------\n\n"
        + "The number of completed Search Results to find.\n\n"
        + "'Completed Search Result' items have unifiable assertions whose hypotheses are\n"
        + "fully satisfied by the Hyp field of the designated proof step -- i.e. no\n"
        + "missing hyps.\n\n"
        + "The Extended Search feature is not run when MaxExtResults is zero -- or when\n"
        + "the regular search process finds at least MaxExtResults number of Completed\n"
        + "Search Results.\n\n\n"
        + "------------\n"
        + "| MaxHyps: |\n"
        + "------------\n\n"
        + "Excludes any assertion with more than 'MaxHyps' number of logical hypotheses.\n\n"
        + "Note: the input sorted assertion list is sorted by number of hyps and MObjSeq\n"
        + "number. Skip-sequential processing is used in the search to access the list\n"
        + "very efficiently. Also, the Extended Search process first looks for assertions\n"
        + "with one missing hyp, then two missing hyps, etc. So, it is possible to reduce\n"
        + "the search elapsed time significantly by specifying MinHyps and MaxHyps,\n"
        + "especially if you are using Extended Search. If your searches are taking too\n"
        + "long or are timing out because of the MaxTime setting you can efficiently\n"
        + "break up a search into ranges of hyps: e.g. 0 thru 2, 3 thru 3, 4 thru 4, etc.\n"
        + "Something like 95% of assertions in set.mm use 2 or fewer hyps, and the\n"
        + "assertions with larger numbers of hyps are the ones most likely to cause the\n"
        + "dreaded problem of Combinatorial Explosion of Possibilities leading to a\n"
        + "timeout.\n\n\n"
        + "------------------\n"
        + "| MaxIncompHyps: |\n"
        + "------------------\n\n"
        + "Specifies the maximum number of incomplete hypotheses to be checked during\n"
        + "Extended Search for a single assertion in the Search Results list.\n\n"
        + "Note: setting this value greater than two risks the dreaded problem of\n"
        + "Combinatorial Explosion of Possibilities :-)\n\n\n"
        + "---------------\n"
        + "| MaxResults: |\n"
        + "---------------\n\n"
        + "Limits the size of the Search Results list.\n\n"
        + "Note: MaxResults is applied prior to sorting the output. This degrades the\n"
        + "quality of the results but speeds up the search. For maximum quality set to\n"
        + "999999.\n\n\n"
        + "------------\n"
        + "| MaxTime: |\n"
        + "------------\n\n"
        + "Specifies the maximum number of seconds a search is allowed to run.\n\n"
        + "A helpful 'timeout' message provides the progress of the search at the time it\n"
        + "was terminated.\n\n\n"
        + "------------\n"
        + "| MinHyps: |\n"
        + "------------\n\n"
        + "Excludes any assertion with fewer than 'MaxHyps' number of logical hypotheses.\n\n\n"
        + "-----------------\n"
        + "| MinProofRefs: |\n"
        + "-----------------\n\n"
        + "Excludes any assertion referenced in proofs fewer than MinProofRefs times.\n\n"
        + "The idea behind this option is that, for whatever reason, some theorems in a\n"
        + "Metamath database are not useful or not intended to be used by other theorems.\n"
        + "And, lemmas are typically used just once.\n\n\n"
        + "--------\n"
        + "| New: |\n"
        + "--------\n\n"
        + "The New button lets you start a new search in General Search mode.\n\n"
        + "You will be prompted for a statement label. This is optional and will be used\n"
        + "to set an exclusive upper range for the search, and to optionally designate\n"
        + "the root (apex) of a ChapSecHierarchy (to further narrow the search.)\n\n\n"
        + "---------\n"
        + "| Oper: |\n"
        + "---------\n\n"
        + "The Search Data line Oper field lets you specify relational operators for the\n"
        + "format types ParseExpr and ParseStmt, and a boolean operator (blank or 'NOT' )\n"
        + "for the other format types: Metamath, RegExpr and charstr.\n\n"
        + "The boolean operator 'NOT' is the simplest to explain. If Oper = 'NOT' then\n"
        + "the logical value of the ForWhat search evaluation is reversed. True becomes\n"
        + "false, and vice-versa. For example, Oper = 'NOT' and ForWhat = '<->' will be\n"
        + "false if the formula (or Comment) actually does contain '<->'.\n\n"
        + "The ParseExpr and ParseStmt relational operators are more complicated.\n\n"
        + "In the descriptions below, 'x' refers to the assertion's formula.\n\n"
        + "ParseStmt relational operator meanings:\n\n"
        + "* '<=' means ForWhat is an instance of x.\n\n"
        + "* '='  means ForWhat and x are equal except for variable names.\n\n"
        + "* '<'  means ForWhat is an instance of x but is not equal to it.\n\n"
        + "* '==' means ForWhat and x are identical (equal including variable names).\n\n"
        + "* '>=' means x is an instance of ForWhat.\n\n"
        + "* '>'  means x is an instance of ForWhat but is not equal to it.\n\n"
        + "* '<>' means ForWhat is an instance of x, or x is an instance of ForWhat, but\n"
        + "       they are not equal ( '=' ) to each other.\n\n"
        + "ParseExpr relational operator meanings:\n\n"
        + "NOTE: Variable Hypothesis sub-expressions are excluded from the searches\n"
        + "below for Format type ParseExpr unless the ForWhat search term is a\n"
        + "simple variable. So instead of saying 'sub-expression' below we could say\n"
        + "'syntactically matching sub-expression' ...but for brevity, let's not...\n\n"
        + "* '<=' means ForWhat is an instance of a sub-expression of x\n\n"
        + "* '='  means ForWhat is equal to a sub-expression of x, except for variable\n"
        + "       names.\n\n"
        + "* '<'  means ForWhat is an instance of a sub-expression of x but is not\n"
        + "       equal to that sub-expression.\n\n"
        + "* '==' means ForWhat is identical to a sub-expression of x (equal including\n"
        + "       variable names).\n\n"
        + "* '>=' means a sub-expression of x is an instance of ForWhat.\n\n\n"
        + "* '>'  means a sub-expression of x is an instance of ForWhat but is not\n"
        + "       equal to ForWhat.\n\n"
        + "* '<>' means ForWhat is an instance of a sub-expression of x but is not\n"
        + "       equal to that sub-expression...OR...a sub-expression of x is an\n"
        + "       instance of ForWhat but is not equal to ForWhat.\n\n"
        + "-------\n"
        + "| OR: |\n"
        + "-------\n\n"
        + "OR enables you to specify an alternative to the logical 'OR' operator in the\n"
        + "ForWhat fields when multiple search terms are used.\n\n"
        + "By default, if no logical operator separates two search terms the 'AND'\n"
        + "operator is assumed (only 'OR' and 'AND' are valid, though the Oper field\n"
        + "contains a 'NOT' option that applies to the entire ForWhat field.)\n\n"
        + "Note: evaluation within a ForWhat field is left-to-right, with evaluation\n"
        + "stopping as soon as truth or falsity is determined -- e.g. evaluation of the\n"
        + "terms to the right of an OR is not performed if evaluation of the terms to the\n"
        + "left of the OR yields true.\n\n"
        + "---------------\n"
        + "| OutputSort: |\n"
        + "---------------\n\n"
        + "This is a pulldown list that allows the user to select the sort sequence of\n"
        + "the output Search Results list.\n\n"
        + "The sort key field choices are listed in major to minor order. Minor sort keys\n"
        + "only come into play when two items are being compared if all of the more major\n"
        + "sort keys are equal.\n\n"
        + "If not otherwise mentioned, field sort sequence is ascending order. Descending\n"
        + "order is indicated with '(d)' suffixing the field name.\n\n"
        + "The sort key fields are:\n\n"
        + "* Complexity = combination of two fields: ParseDepth and FormulaLength.\n"
        + "  Combined here to simplify usage and documentation. 'Complexity(d)' means\n"
        + "  ParseDepth(d) followed by FormulaLength(d). ParseDepth = assertion's\n"
        + "  conclusion's formula's parse tree depth. Corresponds roughly to formula\n"
        + "  complexity and hence, specificity. Because in standard practice the\n"
        + "  designated proof step has already been unified with each assertion in the\n"
        + "  Search Results list, a greater parse depth corresponds to a higher degree of\n"
        + "  similarity -- and hence, increased likelihood of usefulness in the\n"
        + "  designated proof step. FormulaLength = assertion's conclusion's formula\n"
        + "  length (in tokens, not characters). Corresponds roughly to formula\n"
        + "  complexity.\n\n"
        + "* Label = Assertion statement label.\n\n"
        + "* MObjSeq = Metamath object sequence number: corresponds to position within\n"
        + "  the input Metamath file. A higher number indicates a more advanced assertion\n"
        + "  which may be more desirable in a proof step.\n\n"
        + "* Nbr Hyps = number of logical hypotheses in assertion. Fewer hypotheses tends\n"
        + "  to result in shorter proofs. Approximately 95% of set.mm assertions have two\n"
        + "  or fewer hypotheses.\n\n"
        + "* Popularity = number of times assertion used in proofs of other assertions.\n\n"
        + "* Score = this is a ranking number based on the Search Results and search\n"
        + "  controls/criteria:\n\n"
        + "000 = Assertion not selected (initial score value)\n\n"
        + "050 = Assertion satisfies search criteria, if any, but is not a Completed\n"
        + "      Search Result.\n\n"
        + "100 = Assertion is a Completed Search Result: it unifies with the designated\n"
        + "      proof step and there are no missing hypotheses.\n\n\n"
        + "------\n"
        + "| PA |\n"
        + "------\n\n"
        + "The PA button 'jumps' you to the Proof Asst GUI window.\n\n\n"
        + "---------\n"
        + "| Part: |\n"
        + "---------\n\n"
        + "The Search Data line field Part lets you specify which part of the Metamath\n"
        + "statement is to be searched: Formulas, Comments, Labels and LabelsRPN.\n\n"
        + "---------------------\n"
        + "| PrevStepsChecked: |\n"
        + "---------------------\n\n"
        + "PrevStepsChecked specifies the maximum number of previous proof steps to be\n"
        + "checked in the Extended Search. In other words, this lets you control how far\n"
        + "back in the proof you want the Extended Search to look for hypotheses for the\n"
        + "current step.\n\n"
        + "This number does not include previous steps that are bypassed because\n"
        + "ReuseDerivSteps is 'Off'.\n\n"
        + "The motivation for this option is that in a long proof with, say, 100 steps,\n"
        + "if the current proof step has two missing hypotheses then up to 10,000 trial\n"
        + "unifications might be needed...for each assertion in the Search Results!\n\n\n"
        + "----------\n"
        + "| Refine |\n"
        + "----------\n\n"
        + "Refine the prior search by using the prior Search Results as input to a search\n"
        + "using the current search option settings.\n\n"
        + "This is a very powerful feature!\n\n\n"
        + "-----------------\n"
        + "| ResetControls |\n"
        + "-----------------\n\n"
        + "The ResetControls button resets the Search Control fields to the default\n"
        + "settings.\n\n"
        + "See also: SetDef button.\n\n\n"
        + "-------------\n"
        + "| ResetData |\n"
        + "-------------\n\n"
        + "The ResetData button resets the Search Data fields to the default settings.\n\n"
        + "See also: SetDef button.\n\n\n"
        + "-------------------\n"
        + "| ResultsChecked: |\n"
        + "-------------------\n\n"
        + "Restricts the Extended Search input to the first ResultsChecked number of\n"
        + "(sorted) Search Results.\n\n"
        + "Although setting ResultsChecked to a large number might improve the chances\n"
        + "that Extended Search will be successful, the amount of work performed by the\n"
        + "Extended Search is significant and potentially very time-consuming, so you may\n"
        + "need to adjust the MaxTime control to prevent timeouts.\n\n\n"
        + "--------------------\n"
        + "| ReuseDerivSteps: |\n"
        + "--------------------\n\n"
        + "If 'Off', previous proof derivation steps (this excludes the theorem's\n"
        + "hypotheses,) which have already been referenced in a prior derivation step\n"
        + "will not be checked in the Extended Search.\n\n"
        + "The idea of this is that many proofs do not reuse derivations, so steps that\n"
        + "have already been referenced need not be checked in the Extended Search.\n\n"
        + "For long proofs the problem of Combinatorial Explosion of Possibilities may be\n"
        + "dramatically reduced when ReuseDerivSteps is 'Off'. For example, in a long\n"
        + "proof with, say, 100 steps, if the current proof step has two missing\n"
        + "hypotheses then up to 10,000 trial unifications might be needed...for each\n"
        + "assertion in the Search Results!\n\n"
        + "Because some proofs do require reuse of derivations, the user can input a\n"
        + "special mmj2 Proof Worksheet Comment statement immediately prior to a\n"
        + "derivation step, specifying '<so:reuse>' as the first non-blank token after\n"
        + "the '*' in column 1. This special Comment designates the following derivation\n"
        + "step as a candidate for reuse even though ReuseDerivSteps is 'Off'.\n\n\n"
        + "----------\n"
        + "| Search |\n"
        + "----------\n\n"
        + "The Search button initiates the search. Unless there are errors the Search\n"
        + "Results window will be displayed.\n\n\n"
        + "----------\n"
        + "| SetDef |\n"
        + "----------\n\n"
        + "The SetDef button stores the current values of the SearchData and\n"
        + "SearchControls fields as the defaults. You can then use the ResetData and\n"
        + "ResetControls buttons to reset the window data.\n\n"
        + "Note: the follow-on project to Release 20121225 will provide Persistent\n"
        + "Storage Of User Preferences, which will include your chosen Search Options\n"
        + "defaults. Until then, the customized defaults are only retained until mmj2 is\n"
        + "exited.\n\n\n"
        + "----------------\n"
        + "| SingleQuote: |\n"
        + "----------------\n\n"
        + "SingleQuote (and DoubleQuote) let you change the character(s) you will use to\n"
        + "quote-enclose search terms in the ForWhat fields.\n\n"
        + "Quotes are necessary only if more than one search term is used in a single\n"
        + "ForWhat field.\n\n"
        + "Hint: '$' is guaranteed by the Metamath.pdf spec to never occur in a label of\n"
        + "formula, while '$(' and '$)' cannot occur inside Comment statements.\n\n\n"
        + "------\n"
        + "| SR |\n"
        + "------\n\n"
        + "The SR button 'jumps' you to the Search Results window without executing a\n"
        + "search.\n\n\n"
        + "----------\n"
        + "| Stats: |\n"
        + "----------\n\n"
        + "Stats controls output of various statistics to the request messages window\n"
        + "about each search :\n\n"
        + "* Stats = 0 means no Stats output\n"
        + "* Stats = 1 means print Summary Statistics\n"
        + "* Stats = 2 print prior levels plus Detailed Statistics\n"
        + "* Stats = 3 print prior levels plus Search Arguments\n"
        + "* Stats = 4 print prior levels plus Search Results\n"
        + "* Stats >=5 print prior levels plus Chap/Sec Hierarchy used\n\n\n"
        + "------------------\n"
        + "| Substitutions: |\n"
        + "------------------\n\n"
        + "In Step Search mode, if Substitutions is 'On', formulas are shown in the\n"
        + "Search Results window with unification substitutions (from the Proof Worksheet\n"
        + "into the assertion and its hypotheses).\n\n"
        + "If 'Off', assertion formulas are shown without substitutions, just as they\n"
        + "exist in the input Metamath database.\n\n\n"
        + "-------------\n"
        + "| ThruChap: |\n"
        + "-------------\n\n"
        + "ThruChap specifies the (inclusive) high end of the range of Chapters input to\n"
        + "the search. Only assertions defined in the specified Chapter or before are\n"
        + "input.\n\n"
        + "If ThruChap is blank then the high end of the range is the end of the Metamath\n"
        + "database -- or the Theorem/Stmt/LOC_AFTER position, whichever is lower. Also,\n"
        + "if ThruChap is blank then ThruSec is automatically set to blank.\n\n"
        + "Note: ThruChap and ThruSec, if not both blank, specify the root (apex) of one\n"
        + "of the hierarchies used by the ChapSecHierarchy feature.\n\n\n"
        + "------------\n"
        + "| ThruSec: |\n"
        + "------------\n\n"
        + "ThruSec specifies the (inclusive) high end of the range of Sections within the\n"
        + "current ThruChap Chapter input to the search. Only assertions defined in the\n"
        + "specified Chapter/Section or before are input.\n\n"
        + "If ThruSec is blank then the high end of the range is the end of the current\n"
        + "ThruChap Chapter -- or the Theorem/Stmt/LOC_AFTER position, whichever is\n"
        + "lower.\n\n"
        + "Note: ThruChap and ThruSec, if not both blank, specify the root (apex) of one\n"
        + "of the hierarchies used by the ChapSecHierarchy feature.\n\n"
        + "------------------------------------------------------------------------------\n\n"
        + "****The End!****\n";
}
