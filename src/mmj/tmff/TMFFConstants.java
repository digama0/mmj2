//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/**
 *  TMFFConstants.java  0.03 11/01/2011
 *
 *  Aug-31-2006: new.
 *
 *  Version 0.02 11/01/2007
 *      -->Add TMFF_ALT_FORMAT_NBR_DEFAULT,
 *             TMFF_USE_INDENT_DEFAULT
 *             TMFF_ALT_INDENT_DEFAULT
 *
 *  Version 0.03 - Nov-01-2011:  comment update.
 *      - Add TMFF_CURR_FORMAT_NBR_DEFAULT (13)
 */

package mmj.tmff;

/**
 *  Constants used in mmj.tmff package.
 *  <p>
 *  There are two primary types of constants: parameters that
 *  are "hardcoded" which affect/control processing, and
 *  error/info messages.
 *  <p>
 *  Each mmj message begins with a code, such as this:<br>
 *  <code>
 *  E-LA-0007<br>
 *  <p>
 *  where the format of the code is "X-YY-9999"<br>
 *
 *  <b>X</b>     : error level
 *  <ul>
 *      <li>E = Error
 *      <li>I = Information
 *      <li>A = Abort (processing terminates, usually a bug).
 *  </ul><br>
 *  <br>
 *
 *  <b>YY</b>    : source code
 *  <ul>
 *      <li>GM = mmj.gmff package (see mmj.gmff.GMFFConstants)
 *      <li>GR = mmj.verify.Grammar and related code
 *               (see mmj.verify.GrammarConstants)
 *      <li>IO = mmj.mmio package (see mmj.mmio.MMIOConstants)
 *      <li>LA = mmj.lang package (see mmj.lang.GMFFConstants)
 *      <li>PA = mmj.pa package (proof assistant)
 *               (see mmj.pa.PaConstants)
 *      <li>PR = mmj.verify.VerifyProof and related code
 *               (see mmj.verify.ProofConstants)
 *      <li>TL = mmj.tl package (Theorem Loader).
 *      <li>TM = mmj.tmff.AlignColumn and related code
 *      <li>UT = mmj.util package.
 *               (see mmj.util.UtilConstants)
 *  </ul><br>
 *  <br>
 *  <b>9999</b>   : sequential number within the source code, 0001
 *       through 9999.
 *
 *  </code>
 */
public class TMFFConstants {

    /*  *------------------------------------------------*
     *    For TMFFAlignColumn.java
     *  *------------------------------------------------*
     */

    /**
     *  Arbitrary code number signifying formatting alignment
     *  by Sym within Syntax Axiom.
     */
    public static final int ALIGN_SYM = 1;
    /**
     *  Arbitrary code number signifying formatting alignment
     *  by Cnst within Syntax Axiom.
     */
    public static final int ALIGN_CNST = 2;
    /**
     *  Arbitrary code number signifying formatting alignment
     *  by Var within Syntax Axiom.
     */
    public static final int ALIGN_VAR = 3;

    /**
     *  List of alignment type names as input by users.
     *  <p>
     *  The sequence of elements of this table must match
     *  the numeric values for ALIGN_SYM, etc. The array
     *  index + 1 must equal the ALIGN_SYM, etc. number!!!
     */
    public static final String[] ALIGN_TYPE = {"Sym", "Cnst", "Var"};

    /**
     *  Minimum valid (useful) AtNbr for TMFFAlignColumn.
     */
    public static final int MIN_ALIGN_AT_NBR = 1;

    /**
     *  Maximum valid (useful) AtNbr for TMFFAlignColumn.
     */
    public static final int MAX_ALIGN_AT_NBR = 3;

    /*  *------------------------------------------------*
     *    For TMFFPreferences.java
     *  *------------------------------------------------*
     */

    /**
     *  Hardcoded TMFFScheme name for Unformatted output.
     *  This name is RESERVED and cannot be input or
     *  modified by the user.
     */
    public static final String TMFF_UNFORMATTED_SCHEME_NAME = "Unformatted";

    /**
     *  Format 'Unformatted' is assigned Format Number 0.
     *  Do not change this without careful review and
     *  some deep thought -- some other coding changes
     *  would be needed!
     */
    public static final int TMFF_UNFORMATTED_FORMAT_NBR_0 = 0;

    /**
     *  The highest permissible Format Number. It is
     *  arbitrarily set to a low number, because at
     *  some point in the future we might "enhance"
     *  the system and be required to hold an array
     *  of TMFFFormat objects inside each mmj.lang.Axiom,
     *  or even every mmj.lang.MObj (though that latter
     *  is not foreseen.)
     */
    public static final int TMFF_MAX_FORMAT_NBR = 15;

    /**
     *  The highest permissible Indent amount.
     */
    public static final int TMFF_MAX_INDENT = 5;

    // * ==== FOLLOWING ARRAYS ARE A SET USED TO DEFINE THE
    // * BUILT-IN SCHEMES/FORMATS AVAILABLE "OUT OF THE BOX".

    public static final String[][] TMFF_DEFAULT_DEFINE_SCHEME_PARAMS = {
            {"AlignVarDepth1", "AlignColumn", "1", "Var", "1", "Var"},
            {"AlignVarDepth2", "AlignColumn", "2", "Var", "1", "Var"},
            {"AlignVarDepth3", "AlignColumn", "3", "Var", "1", "Var"},
            {"AlignVarDepth4", "AlignColumn", "4", "Var", "1", "Var"},
            {"AlignVarDepth5", "AlignColumn", "5", "Var", "1", "Var"},
            {"AlignVarDepth99", "AlignColumn", "99", "Var", "1", "Var"},
            {"PrefixDepth3", "AlignColumn", "3", "Sym", "2", "Sym"},
            {"PostfixDepth3", "AlignColumn", "3", "Sym", "1", "Sym"},
            {"AlignVarDepth99", "AlignColumn", "99", "Var", "1", "Var"},
            {"Flat", "Flat", "", "", "", ""},
            {"Unformatted", "Unformatted", "", "", "", ""},
            {"TwoColumnAlignmentDepth1", "TwoColumnAlignment", "1", "", "", ""},
            {"TwoColumnAlignmentDepth2", "TwoColumnAlignment", "2", "", "", ""},
            {"TwoColumnAlignmentDepth3", "TwoColumnAlignment", "3", "", "", ""},
            {"TwoColumnAlignmentDepth4", "TwoColumnAlignment", "4", "", "", ""},
            {"TwoColumnAlignmentDepth5", "TwoColumnAlignment", "5", "", "", ""},
            {"TwoColumnAlignmentDepth99", "TwoColumnAlignment", "99", "", "",
                    ""}};

    public static final String[][] TMFF_DEFAULT_DEFINE_FORMAT_PARAMS = {
            {"0", "Unformatted"}, {"1", "AlignVarDepth1"},
            {"2", "AlignVarDepth2"}, {"3", "AlignVarDepth3"},
            {"4", "AlignVarDepth4"}, {"5", "AlignVarDepth5"},
            {"6", "AlignVarDepth99"}, {"7", "Flat"}, {"8", "PrefixDepth3"},
            {"9", "PostFixDepth3"}, {"10", "TwoColumnAlignmentDepth99"},
            {"11", "TwoColumnAlignmentDepth1"},
            {"12", "TwoColumnAlignmentDepth2"},
            {"13", "TwoColumnAlignmentDepth3"},
            {"14", "TwoColumnAlignmentDepth4"},
            {"15", "TwoColumnAlignmentDepth5"}};

    /**
     *  Hardcoded user name of TMFFMethod TMFFUnformatted.
     */
    public static final String TMFF_METHOD_USER_NAME_UNFORMATTED = "Unformatted";

    /**
     *  Hardcoded user name of TMFFMethod TMFFAlignColumn.
     */
    public static final String TMFF_METHOD_USER_NAME_ALIGN_COLUMN = "AlignColumn";

    /**
     *  Hardcoded user name of TMFFMethod TMFFTwoColumnAlignment.
     */
    public static final String TMFF_METHOD_USER_NAME_TWO_COLUMN_ALIGNMENT = "TwoColumnAlignment";

    /**
     *  Hardcoded user name of TMFFMethod TMFFFlat.
     */
    public static final String TMFF_METHOD_USER_NAME_FLAT = "Flat";

    /**
     *  Default setting for Current Format Number.
     *
     *  "13" = TwoColumnAlignmentDepth2.
     */
    public static final int TMFF_CURR_FORMAT_NBR_DEFAULT = 13;

    /**
     *  Default setting for Alternate Format Number.
     *
     *  "7" = Flat.
     */
    public static final int TMFF_ALT_FORMAT_NBR_DEFAULT = 7;

    /**
     *  Default setting for TMFF Use Indent amount
     *
     *  "0" = No indentation
     */
    public static final int TMFF_USE_INDENT_DEFAULT = 0;

    /**
     *  Default setting for TMFF Alt Indent amount
     *
     *  "1" = 1 column indentation per proof level below the root.
     */
    public static final int TMFF_ALT_INDENT_DEFAULT = 1;

    // ==================================================
    // Messages for TMFFAlignColumn:
    // ==================================================
    public static final String ERRMSG_BAD_BY_VALUE_1 = "A-TM-0001 Invalid 'byValue' String = ";
    public static final String ERRMSG_BAD_BY_VALUE_2 = " input! Should be 'Cnst', 'Var' or 'Sym'.";

    public static final String ERRMSG_MISSING_BY_VALUE_1 = "A-TM-0002 Missing 'byValue' String parameter!";

    public static final String ERRMSG_BAD_AT_NBR_1 = "A-TM-0003 Invalid 'atNbr' = ";
    public static final String ERRMSG_BAD_AT_NBR_2 = " input! Should be 1, 2, or 3.";

    public static final String ERRMSG_BAD_AT_VALUE_1 = "A-TM-0004 Invalid 'atValue' String = ";
    public static final String ERRMSG_BAD_AT_VALUE_2 = " input! Should be 'Cnst', 'Var' or 'Sym'.";

    public static final String ERRMSG_MISSING_AT_VALUE_1 = "A-TM-0005 Missing 'atValue' String parameter!";

    public static final String ERRMSG_BAD_SUB_EXPR_NODE_1 = "A-TM-0006 Invalid ParseNode passed for Sub-Expression"
        + " output. Node must be either a VarHyp node or a"
        + " SyntaxAxiom node -- i.e. a parse ParseNode,"
        + " not a *proof* ParseNode!";

    public static final String ERRMSG_NO_ROOM_SUB_EXPR_1 = "I-TM-0007 Ran out of room on line formatting"
        + " sub-expression. Either the lines are too narrow"
        + " or the formula is so complex that the total"
        + " indentation cannot fit on a normal line.";

    // ==================================================
    // Messages for TMFFFormat:
    // ==================================================
    public static final String ERRMSG_FORMAT_SCHEME_MISSING_1 = "A-TM-0101 Cannot create TMFFFormat without Scheme!";

    public static final String ERRMSG_CANNOT_UPD_FORMAT_0_1 = "A-TM-0102 Format Number ";
    public static final String ERRMSG_CANNOT_UPD_FORMAT_0_2 = " is RESERVED and cannot be updated.";

    public static final String ERRMSG_BAD_UPD_FORMAT_NBR_1 = "A-TM-0103 Input format number = ";
    public static final String ERRMSG_BAD_UPD_FORMAT_NBR_2 = " is invalid. Must be within the range of 1 and ";

    public static final String ERRMSG_BAD_NEW_FORMAT_NBR_1 = "A-TM-0104 Input format number = ";
    public static final String ERRMSG_BAD_NEW_FORMAT_NBR_2 = " is invalid. Must be within the range of 0 and ";

    public static final String ERRMSG_FORMAT_SCHEME_NAME_NOTFND2_1 = "A-TM-0105 TMFFDefineFormat Scheme Name not found "
        + "among previously defined Schemes. Input name = ";

    public static final String ERRMSG_FORMAT_NBR_MISSING_1 = "A-TM-0106 TMFFDefineFormat Format Number not input!";

    public static final String ERRMSG_SCHEME_NAME_MISSING_1 = "A-TM-0107 TMFFDefineFormat Scheme Name not input!";

    // ==================================================
    // Messages for TMFFMethod:
    // ==================================================
    public static final String ERRMSG_BAD_MAX_DEPTH_1 = "A-TM-0201 Invalid maxDepth parameter, must be a"
        + " positive integer. Input = ";

    public static final String ERRMSG_BAD_USER_METHOD_NAME_1 = "A-TM-0202 Invalid (user) name of TMFF Method = ";

    public static final String ERRMSG_MISSING_USER_METHOD_NAME_1 = "A-TM-0203 Missing (user) name of TMFF Method";

    // ==================================================
    // Messages for TMFFPreferences:
    // ==================================================

    public static final String ERRMSG_RENDER_FORMULA_ERROR_1 = "A-TM-0301 Programmer Error. The default formula"
        + " formatting routine failed horribly. Call 911.";

    public static final String ERRMSG_BAD_PREF_FORMAT_NBR_1 = "A-TM-0302 Input format number = ";
    public static final String ERRMSG_BAD_PREF_FORMAT_NBR_2 = " is invalid. Must be within the range of 0 and ";

    public static final String ERRMSG_ERR_FORMAT_NBR_INPUT_1 = " Input Format Nbr must be a number between 0 and ";

    public static final String ERRMSG_FORMAT_SCHEME_NAME_NOTFND_1 = "A-TM-0303 TMFFDefineFormat Scheme Name not found "
        + "among previously defined Schemes. Input name = ";

    public static final String ERRMSG_FORMAT_NBR_MISSING2_1 = "A-TM-0304 TMFFUseFormat Format Number not input!";

    public static final String ERRMSG_UPDATE_SCHEME_NOTFND_BUG_1 = "A-TM-0305 TMFFDefineScheme not found to update."
        + " Call 911, programmer error because the name had"
        + " already been looked up. Name = ";

    public static final String ERRMSG_ALT_FORMAT_NBR_MISSING2_1 = "A-TM-0306 TMFFAltFormat Format Number not input!";

    public static final String ERRMSG_BAD_ALT_FORMAT_NBR_1 = "A-TM-0307 Input alt format number = ";
    public static final String ERRMSG_BAD_ALT_FORMAT_NBR_2 = " is invalid. Must be within the range of 0 and ";

    public static final String ERRMSG_USE_INDENT_MISSING2_1 = "A-TM-0308 TMFFUseIndent Use Indent amount not input!";

    public static final String ERRMSG_BAD_USE_INDENT_1 = "A-TM-0309 Input Use Indent amount = ";
    public static final String ERRMSG_BAD_USE_INDENT_2 = " is invalid. Must be within the range of 0 and ";

    public static final String ERRMSG_ALT_INDENT_MISSING2_1 = "A-TM-0310 TMFFAltIndent Alt Indent amount not input!";

    public static final String ERRMSG_BAD_ALT_INDENT_1 = "A-TM-0311 Input Alt Indent amount = ";
    public static final String ERRMSG_BAD_ALT_INDENT_2 = " is invalid. Must be within the range of 0 and ";

    public static final String ERRMSG_ERR_INDENT_INPUT_1 = " Input Indent amount must be a number between 0 and ";

    // ==================================================
    // Messages for TMFFScheme:
    // ==================================================

    public static final String ERRMSG_SCHEME_METHOD_MISSING_1 = "A-TM-0401 TMFFScheme Method is required!";

    public static final String ERRMSG_SCHEME_CANNOT_BE_UPDATED_1 = "A-TM-0402 Scheme ";
    public static final String ERRMSG_SCHEME_CANNOT_BE_UPDATED_2 = " is RESERVED and cannot be updated.";

    public static final String ERRMSG_SCHEME_NAME_REQUIRED_1 = "A-TM-0403 Scheme Name is required!";

    public static final String ERRMSG_SCHEME_NM_CANT_BE_ASSIGNED_1 = "A-TM-0404 Scheme Name ";
    public static final String ERRMSG_SCHEME_NM_CANT_BE_ASSIGNED_2 = " is RESERVED and cannot be assigned.";

    // ==================================================
    // Messages for TMFFUnformatted:
    // ==================================================

    public static final String ERRMSG_UNFORMATTED_BAD_CALL_UNF_1 = "A-TM-0501 TMFFUnformatted.renderSubExprWithBreaks()"
        + " was invoked. This indicates fried programmer. Bad." + " Call 911!";

    // ==================================================
    // Messages for TMFFFlat:
    // ==================================================

    public static final String ERRMSG_UNFORMATTED_BAD_CALL_FLAT_1 = "A-TM-0601 TMFFFlat.renderSubExprWithBreaks()"
        + " was invoked. This indicates fried programmer. Bad." + " Call 911!";

}
