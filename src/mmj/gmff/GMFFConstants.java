//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * GMFFConstants.java  0.01 11/01/2011
 *
 * Version 0.01:
 * Nov-01-2011: new.
 */

package mmj.gmff;

import java.io.File;

import mmj.mmio.MMIOConstants;
import mmj.pa.PaConstants;
import mmj.util.UtilConstants;
import mmj.verify.GrammarConstants;
import mmj.verify.ProofConstants;

/**
 * Constants used in mmj.gmff package.
 * <p>
 * There are two primary types of constants: parameters that are "hardcoded"
 * which affect/control processing, and error/info messages.
 * <p>
 * Each mmj message begins with a code, such as this:
 * <p>
 * <code>E-LA-0007</code>
 * <p>
 * where the format of the code is {@code X-YY-9999}
 * <p>
 * <b>{@code X}</b> : error level
 * <ul>
 * <li>{@code E} = Error
 * <li>{@code I} = Information
 * <li>{@code A} = Abort (processing terminates, usually a bug).
 * </ul>
 * <p>
 * <b>{@code YY}</b> : source code
 * <ul>
 * <li>{@code GM} = mmj.gmff package (see {@link GMFFConstants})
 * <li>{@code GR} = mmj.verify.Grammar and related code (see
 * {@link GrammarConstants})
 * <li>{@code IO} = mmj.mmio package (see {@link MMIOConstants})
 * <li>{@code LA} = mmj.lang package (see {@link GMFFConstants})
 * <li>{@code PA} = mmj.pa package (proof assistant) (see {@link PaConstants})
 * <li>{@code PR} = mmj.verify.VerifyProof and related code (see
 * {@link ProofConstants})
 * <li>{@code TL} = mmj.tl package (Theorem Loader).
 * <li>{@code TM} = mmj.tmff.AlignColumn and related code
 * <li>{@code UT} = mmj.util package. (see {@link UtilConstants})
 * </ul>
 * <p>
 * <b>{@code 9999}</b> : sequential number within the source code, 0001 through
 * 9999.
 */
public class GMFFConstants {

    // ==================================================
    // Constants for GMFFManager:
    // ==================================================

    public static final String DEFAULT_OUTPUT_FILE_NAME = "general";

    public static final String EXPORT_PARM_ON = "ON";
    public static final String EXPORT_PARM_OFF = "OFF";

    public static final String CHARSET_ENCODING_ISO_8859_1 = "ISO-8859-1";

    public static final String EXPORT_TYPE_HTML = "html";
    public static final String EXPORT_TYPE_ALTHTML = "althtml";

    public static final String MODEL_A = "A";

    public static final String USER_EXPORT_CHOICE_ALL = "ALL";

    public static final char FILE_TYPE_DOT = '.';

    public static final String METAMATH_DOLLAR_T_MESSAGE_DESCRIPTOR = "Metamath $t Comment";

    public static final int METAMATH_DOLLAR_T_BUFFER_SIZE = 8192;

    public static final int METAMATH_DOLLAR_T_MAP_SIZE = 1500;

    public static final String OPTION_VALUE_ALL = "*";
    public static final String PROOF_WORKSHEET_MESSAGE_DESCRIPTOR = "Proof Worksheet";

    public static final int PROOF_WORKSHEET_BUFFER_SIZE = 8192;

    public static final char APPEND_FILE_NAME_ERR_CHAR_1 = '/';
    public static final char APPEND_FILE_NAME_ERR_CHAR_2 = '\\';
    public static final char APPEND_FILE_NAME_ERR_CHAR_3 = ':';

    public static final char OUTPUT_FILE_NAME_ERR_CHAR_1 = '/';
    public static final char OUTPUT_FILE_NAME_ERR_CHAR_2 = '\\';
    public static final char OUTPUT_FILE_NAME_ERR_CHAR_3 = ':';

    public static final char AUDIT_REPORT_COMMA = ',';
    public static final char AUDIT_REPORT_DOUBLE_QUOTE = '"';

    public static final String PRINT_TYPESETTING_DEFINITIONS = "PrintTypesettingDefinitions";

    public static final String RUNPARM_PRINT_OPTION = "PRINT";

    public static final GMFFUserExportChoice DEFAULT_USER_EXPORT_CHOICE = new GMFFUserExportChoice(
        GMFFConstants.USER_EXPORT_CHOICE_ALL);

    public static final String DEFAULT_GMFF_HTML_DIRECTORY = "gmff"
        + File.separator + "html";

    public static final String DEFAULT_GMFF_MODELS_HTML_DIRECTORY = "gmff"
        + File.separator + "models" + File.separator + "html";

    public static final GMFFExportParms DEFAULT_EXPORT_PARMS_HTML = new GMFFExportParms(
        GMFFConstants.EXPORT_TYPE_HTML, GMFFConstants.EXPORT_PARM_ON,
        "htmldef", GMFFConstants.DEFAULT_GMFF_HTML_DIRECTORY, ".html",
        GMFFConstants.DEFAULT_GMFF_MODELS_HTML_DIRECTORY,
        GMFFConstants.MODEL_A, GMFFConstants.CHARSET_ENCODING_ISO_8859_1,
        GMFFConstants.DEFAULT_OUTPUT_FILE_NAME);

    public static final String DEFAULT_GMFF_ALTHTML_DIRECTORY = "gmff"
        + File.separator + "althtml";

    public static final String DEFAULT_GMFF_MODELS_ALTHTML_DIRECTORY = "gmff"
        + File.separator + "models" + File.separator + "althtml";

    public static final GMFFExportParms DEFAULT_EXPORT_PARMS_ALTHTML = new GMFFExportParms(
        GMFFConstants.EXPORT_TYPE_ALTHTML, GMFFConstants.EXPORT_PARM_ON,
        "althtmldef", GMFFConstants.DEFAULT_GMFF_ALTHTML_DIRECTORY, ".html",
        GMFFConstants.DEFAULT_GMFF_MODELS_ALTHTML_DIRECTORY,
        GMFFConstants.MODEL_A, GMFFConstants.CHARSET_ENCODING_ISO_8859_1,
        GMFFConstants.DEFAULT_OUTPUT_FILE_NAME);

    public static final GMFFExportParms[] DEFAULT_EXPORT_PARMS = {
            GMFFConstants.DEFAULT_EXPORT_PARMS_HTML,
            GMFFConstants.DEFAULT_EXPORT_PARMS_ALTHTML};

    public static final EscapePair DEFAULT_ESCAPE_PAIR_32 = new EscapePair(32,
        "&nbsp;");
    public static final EscapePair DEFAULT_ESCAPE_PAIR_34 = new EscapePair(34,
        "&quot;");
    public static final EscapePair DEFAULT_ESCAPE_PAIR_38 = new EscapePair(38,
        "&amp;");
    public static final EscapePair DEFAULT_ESCAPE_PAIR_60 = new EscapePair(60,
        "&lt;");
    public static final EscapePair DEFAULT_ESCAPE_PAIR_62 = new EscapePair(62,
        "&gt;");
    public static final EscapePair[] DEFAULT_ESCAPE_PAIRS = {
            GMFFConstants.DEFAULT_ESCAPE_PAIR_32,
            GMFFConstants.DEFAULT_ESCAPE_PAIR_34,
            GMFFConstants.DEFAULT_ESCAPE_PAIR_38,
            GMFFConstants.DEFAULT_ESCAPE_PAIR_60,
            GMFFConstants.DEFAULT_ESCAPE_PAIR_62};

    public static final GMFFUserTextEscapes GMFF_DEFAULT_USER_TEXT_ESCAPES_HTML = new GMFFUserTextEscapes(
        GMFFConstants.EXPORT_TYPE_HTML, GMFFConstants.DEFAULT_ESCAPE_PAIRS);

    public static final GMFFUserTextEscapes GMFF_DEFAULT_USER_TEXT_ESCAPES_ALTHTML = new GMFFUserTextEscapes(
        GMFFConstants.EXPORT_TYPE_ALTHTML, GMFFConstants.DEFAULT_ESCAPE_PAIRS);

    public static final GMFFUserTextEscapes[] DEFAULT_USER_TEXT_ESCAPES = {
            GMFFConstants.GMFF_DEFAULT_USER_TEXT_ESCAPES_HTML,
            GMFFConstants.GMFF_DEFAULT_USER_TEXT_ESCAPES_ALTHTML};

    // ==================================================
    // Constants for EscapePair
    // ==================================================

    public static final int ESCAPE_PAIR_NUM_MIN = 0;
    public static final int ESCAPE_PAIR_NUM_MAX = 255;

    // ==================================================
    // Constants for GMFFExporter
    // ==================================================

    public static final int EXPORTER_MODEL_CACHE_INIT_SIZE = 40;

    public static final String MODEL_ERROR_MESSAGE_DESCRIPTOR = "Model";

    public static final int DEFAULT_MODEL_FILE_BUFFER_SIZE = 512;

    // ==================================================
    // Constants for GMFFModelFile
    // ==================================================

    // ==================================================
    // Constants for MinProofWorksheet:
    // ==================================================

    public static final int EXPORT_BUFFER_DEFAULT_SIZE = 4096;

    // ==================================================
    // Model A File Names for MinProofWorksheet
    // ==================================================

    public static final String MODEL_A_FILE0_NAME = "AM-file0.txt";
    public static final String MODEL_A_FILE2_NAME = "AM-file2.txt";

    // ==================================================
    // Model A File Names for MinStepStmt
    // ==================================================

    public static final String MODEL_A_STEP0_NAME = "AM-step0.txt";
    public static final String MODEL_A_STEP1X_NAME = "AM-step1X.txt";
    public static final String MODEL_A_STEP2_NAME = "AM-step2.txt";
    public static final String MODEL_A_STEP3X_NAME = "AM-step3X.txt";
    public static final String MODEL_A_STEP4_NAME = "AM-step4.txt";

    // ==================================================
    // Model A File Names for MinHeaderStmt
    // ==================================================

    public static final String MODEL_A_HEADER0_NAME = "AO-header0.txt";
    public static final String MODEL_A_HEADER1X_NAME = "AO-header1X.txt";
    public static final String MODEL_A_HEADER2_NAME = "AO-header2.txt";
    public static final String MODEL_A_HEADER3X_NAME = "AO-header3X.txt";
    public static final String MODEL_A_HEADER4_NAME = "AO-header4.txt";
    public static final String MODEL_A_HEADER5X_NAME = "AO-header5X.txt";
    public static final String MODEL_A_HEADER6_NAME = "AO-header6.txt";
    public static final String MODEL_A_HEADER7_NAME = "AO-header7.txt";

    // ==================================================
    // Model A File Names for MinCommentStmt
    // ==================================================

    public static final String MODEL_A_COMMENT0_NAME = "AO-comment0.txt";
    public static final String MODEL_A_COMMENT1X_NAME = "AO-comment1X.txt";
    public static final String MODEL_A_COMMENT2_NAME = "AO-comment2.txt";

    // ==================================================
    // Model A File Names for MinDistinctVariablesStmt
    // ==================================================

    public static final String MODEL_A_DISTINCTVAR0_NAME = "AO-distinctvar0.txt";
    public static final String MODEL_A_DISTINCTVAR1X_NAME = "AO-distinctvar1X.txt";
    public static final String MODEL_A_DISTINCTVAR2_NAME = "AO-distinctvar2.txt";
    public static final String MODEL_A_DISTINCTVAR3X_NAME = "AO-distinctvar3X.txt";
    public static final String MODEL_A_DISTINCTVAR4_NAME = "AO-distinctvar4.txt";

    // ==================================================
    // Model A File Names for MinGeneratedProofStmt
    // ==================================================

    public static final String MODEL_A_GENPROOF0_NAME = "AO-genproof0.txt";
    public static final String MODEL_A_GENPROOF1X_NAME = "AO-genproof1X.txt";
    public static final String MODEL_A_GENPROOF2_NAME = "AO-genproof2.txt";

    // ==================================================
    // Model A File Names for MinFooterStmt
    // ==================================================

    public static final String MODEL_A_FOOTER0_NAME = "AO-footer0.txt";

    // ==================================================
    // Constants For ModelAExporter
    // ==================================================

    // ==================================================
    // Messages for MinProofWorksheet:
    // ==================================================
    public static final String ERRMSG_TOKENIZER_IO_ERROR_1 = "E-GM-0001 GMFF: Theorem label = ";
    public static final String ERRMSG_TOKENIZER_IO_ERROR_1B = " IOException while tokenizing line ";
    public static final String ERRMSG_TOKENIZER_IO_ERROR_2 = " in Proof Worksheet. Detailed message follows: ";

    public static final String ERRMSG_EMPTY_PROOF_1 = "E-GM-0002 GMFF: Theorem label = ";
    public static final String ERRMSG_EMPTY_PROOF_1B = " Invalid Proof Worksheet! Text area is empty.";

    public static final String ERRMSG_BOGUS_LINE_1_1 = "E-GM-0003 GMFF: Theorem label = ";
    public static final String ERRMSG_BOGUS_LINE_1_1B = " Invalid Proof Worksheet! First line is not"
        + " a valid Proof Worksheet Header.";

    public static final String ERRMSG_LOAD_READLINE_IO_ERROR_1 = "E-GM-0004 GMFF: Proof Worksheet load failed due to IO error"
        + " while reading line ";
    public static final String ERRMSG_LOAD_READLINE_IO_ERROR_2 = " of the Proof Worksheet. Detailed message follows: ";

    public static final String ERRMSG_WORK_STMT_LINE_START_ERROR_1 = "E-GM-0005 GMFF: Theorem label = ";
    public static final String ERRMSG_WORK_STMT_LINE_START_ERROR_1B = " Proof Worksheet load failed due to invalid"
        + " starting token on line ";
    public static final String ERRMSG_WORK_STMT_LINE_START_ERROR_2 = " of the Proof Worksheet. Input token = ";
    public static final String ERRMSG_WORK_STMT_LINE_START_ERROR_3 = ". Please note: Column 1 is used to identify the start of"
        + " new Proof Worksheet statements. A blank in Column 1"
        + " indicates a continuation of the previous statement.";

    public static final String ERRMSG_WORK_STMT_CONSTRUCTOR_ERROR_1 = "E-GM-0006 GMFF: Theorem label = ";
    public static final String ERRMSG_WORK_STMT_CONSTRUCTOR_ERROR_1B = " Error while constructing Proof Work statement"
        + " for line ";
    public static final String ERRMSG_WORK_STMT_CONSTRUCTOR_ERROR_2 = " of the Proof Worksheet. Detail message follows: ";

    public static final String ERRMSG_INVALID_LINE_LIST_ERROR_1 = "A-GM-0007 GMFF: Theorem label = ";
    public static final String ERRMSG_INVALID_LINE_LIST_ERROR_1B = " Invalid lineList argument (lineList) for "
        + " Proof Worksheet MinProofWorksheet.constructStmt() "
        + " function. Indicates a program bug. Contact programmer!"
        + " Proof Worksheet line number = ";

    public static final String ERRMSG_INVALID_HEADER_CONSTANTS_ERROR_1 = "E-GM-0008 GMFF: Theorem label = ";
    public static final String ERRMSG_INVALID_HEADER_CONSTANTS_ERROR_1B = " Proof Worksheet Header does not begin with"
        + " the required identifying constant: "
        + "'$( <MM> <PROOF_ASST> THEOREM='. Line number = ";

    public static final String ERRMSG_INVALID_THEOREM_LABEL_ERROR_1 = "E-GM-0009 GMFF: Proof Worksheet Header theorem label"
        + " invalid. Found label = ";
    public static final String ERRMSG_INVALID_THEOREM_LABEL_ERROR_2 = " Label must not be blank or '?', must consist of valid"
        + " Metamath label characters, and must not be on the"
        + " list of prohibited Metamath label (see Metamath.pdf.)"
        + " Line number = ";

    public static final String ERRMSG_BUILD_EMPTY_OR_INVALID_WORKSHEET_ERROR_1 = "A-GM-0010 GMFF: Theorem label = ";
    public static final String ERRMSG_BUILD_EMPTY_OR_INVALID_WORKSHEET_ERROR_1B = " MinProofWorksheet export build function called but"
        + " Proof Worksheet is empty or has structural errors."
        + " Indicates a program bug. Contact programmer!";

    // ==================================================
    // Messages for GMFFManager
    // ==================================================
    public static final String ERRMSG_NO_EXPORT_TYPES_SELECTED_ERROR_1 = "E-GM-0101 GMFF: No export types selected. Update RunParms.txt"
        + " if you wish to export ProofWorksheets via GMFF. Then"
        + " restart mmj2 and try again.";

    public static final String ERRMSG_INVALID_MODEL_ID_ERROR_1 = "E-GM-0102 Invalid Export Model Id input = ";
    public static final String ERRMSG_INVALID_MODEL_ID_ERROR_2 = ". Input export Type = ";

    public static final String ERRMSG_EXPORT_PARMS_LIST_ERROR_1 = "E-GM-0105 GMFF: Validation errors in consolidated list"
        + " of Export Parms (defaults plus RunParms)."
        + " Refer to prior messages for details.";

    public static final String ERRMSG_USER_TEXT_ESCAPES_LIST_ERROR_1 = "E-GM-0106 GMFF: Validation errors in consolidated list"
        + " of User Text Escapes parms (defaults plus RunParms)."
        + " Refer to prior messages for details.";

    public static final String ERRMSG_INVALID_METAMATH_TYPESET_COMMENT_ERROR_1 = "E-GM-0107 GMFF: Invalid Metamath Typeset Comment input."
        + " Did not find valid $( and $) delimiters.";

    public static final String ERRMSG_NO_PROOF_FILES_SELECTED_ERROR_1 = "E-GM-0108 GMFF: No Proof Worksheet files were found"
        + " in Folder ";
    public static final String ERRMSG_NO_PROOF_FILES_SELECTED_ERROR_2 = " with File Type ";

    public static final String ERRMSG_FILE_TYPE_BAD_MISSING_1 = "E-GM-0109 File Type error. File Type must contain"
        + " no whitespace and must begin with '.'. Found = ";

    public static final String ERRMSG_MAX_NBR_TO_EXPORT_BAD_MISSING_1 = "E-GM-0110 Maximum number to export parameter invalid."
        + " Must be a positive integer with no embedded"
        + " whitespace, etc. Input = ";

    public static final String ERRMSG_LABEL_OR_ASTERISK_BAD_MISSING_1 = "E-GM-0111 Theorem label invalid."
        + " Required input. Must have no embedded whitespace."
        + " Input '*' to extract ALL (up to Max Number of)" + " theorems.";

    public static final String ERRMSG_APPEND_FILE_NAME_ERROR_1 = "E-GM-0112 AppendFileName invalid. Must contain no"
        + "whitespace characters or '/' or '\' or ':'. Found = ";

    public static final String ERRMSG_GMFF_THEOREM_EXPORT_PA_ERROR_1 = "E-GM-0113 Error in Proof Assistant during extraction"
        + " of Proof Worksheet for export. Theorem label = ";
    public static final String ERRMSG_GMFF_THEOREM_EXPORT_PA_ERROR_2 = " Detailed error message follows:";

    public static final String ERRMSG_NO_THEOREMS_SELECTED_ERROR_1 = "E-GM-0114 No theorems selected for export."
        + " Request parameter (theorem label or '*', ALL) = ";

    public static final String INITIALIZATION_AUDIT_REPORT_1 = "I-GM-0115 GMFFInitialize RunParm Audit Report:";
    public static final String INITIALIZATION_AUDIT_REPORT_2_UC_1 = "  ";
    public static final String INITIALIZATION_AUDIT_REPORT_3_SE_1 = "  SelectedExporter[";
    public static final String INITIALIZATION_AUDIT_REPORT_3_SE_2 = "]";
    public static final String INITIALIZATION_AUDIT_REPORT_4_EP_1 = "    ";
    public static final String INITIALIZATION_AUDIT_REPORT_5_TE_1 = "    ";
    public static final String INITIALIZATION_AUDIT_REPORT_6_TD_1 = "    ";
    public static final String INITIALIZATION_AUDIT_REPORT_6_TD_2 = "Typesetting Definitions: .mm $t keyword = ";
    public static final String INITIALIZATION_AUDIT_REPORT_6_TD_3 = ", Number Of Defined Symbols = ";

    public static final String ERRMSG_INPUT_DOLLAR_T_COMMENT_MM_FILE_1 = "I-GM-0116 GMFFParseMetamathTypesetComment input directory: ";
    public static final String ERRMSG_INPUT_DOLLAR_T_COMMENT_MM_FILE_2 = ", File Name: ";
    public static final String ERRMSG_INPUT_DOLLAR_T_COMMENT_MM_FILE_3 = ", TypesetDefKeyword: ";
    public static final String ERRMSG_INPUT_DOLLAR_T_COMMENT_MM_FILE_4 = ". Input $t comment follows:\n";
    public static final String ERRMSG_INPUT_DOLLAR_T_COMMENT_MM_FILE_5 = "\n";

    // ==================================================
    // Messages for GMFFExporter
    // ==================================================
    public static final String ERRMSG_TYPESET_DEF_NOT_FOUND_ERROR_1 = "I-GM-0201 Typesetting definition entry not found. Theorem = ";
    public static final String ERRMSG_TYPESET_DEF_NOT_FOUND_ERROR_1B = ", Symbol = ";
    public static final String ERRMSG_TYPESET_DEF_NOT_FOUND_ERROR_2 = ", Typesetting Def Type = ";
    public static final String ERRMSG_TYPESET_DEF_NOT_FOUND_ERROR_3 = ". To correct, fix input Metamath (set.mm) $t comment."
        + "To see the typesetting definitions as loaded by mmj2,"
        + " add a GMFFInitialize RunParm immediately before the"
        + " RunProofAsstGUI RunParm, as follows:\n"
        + "\nGMFFInitialize,PrintTypesettingDefinitions"
        + "\nRunProofAsstGUI"
        + "\n\nThen restart mmj2.";

    public static final String ERRMSG_MANDATORY_MODEL_NOT_FOUND_ERROR_1 = "E-GM-0202 Mandatory export model file not found. Theorem = ";
    public static final String ERRMSG_MANDATORY_MODEL_NOT_FOUND_ERROR_1B = " Export type = ";
    public static final String ERRMSG_MANDATORY_MODEL_NOT_FOUND_ERROR_2 = ". File name = ";
    public static final String ERRMSG_MANDATORY_MODEL_NOT_FOUND_ERROR_3 = ". To correct, copy file into export type Model folder and"
        + " retry -- no need to restart mmj2 in this situation."
        + " Detailed error message follows: ";;

    public static final String ERRMSG_EXPORT_CONFIRMATION_1 = "I-GM-0203 Theorem ";
    public static final String ERRMSG_EXPORT_CONFIRMATION_2 = " exported to ";
    public static final String ERRMSG_EXPORT_CONFIRMATION_3 = "\n";

    // ==================================================
    // Messages for GMFFInputFile
    // ==================================================

    public static final String ERRMSG_INPUT_FILE_EXISTS_NOT_A_FILE_1 = "E-GM-0301 ";
    public static final String ERRMSG_INPUT_FILE_EXISTS_NOT_A_FILE_1B = " file name (exists but is) not a file."
        + " File name = ";
    public static final String ERRMSG_INPUT_FILE_EXISTS_NOT_A_FILE_2 = ". Export type = ";
    public static final String ERRMSG_INPUT_FILE_EXISTS_NOT_A_FILE_3 = ". System absolute pathname = ";

    public static final String ERRMSG_INPUT_FILE_MISC_ERROR_1 = "E-GM-0302 ";
    public static final String ERRMSG_INPUT_FILE_MISC_ERROR_1B = " file name miscellaneous I/O error!"
        + " File name = ";
    public static final String ERRMSG_INPUT_FILE_MISC_ERROR_2 = ". Export type = ";
    public static final String ERRMSG_INPUT_FILE_MISC_ERROR_3 = ". System absolute pathname = ";
    public static final String ERRMSG_INPUT_FILE_MISC_ERROR_4 = ". Detailed system error message = ";

    public static final String ERRMSG_INPUT_FILE_READ_IO_ERROR_1 = "E-GM-0303 ";
    public static final String ERRMSG_INPUT_FILE_READ_IO_ERROR_1B = " file read I/O error!"
        + " File name = ";
    public static final String ERRMSG_INPUT_FILE_READ_IO_ERROR_2 = ". Export type = ";
    public static final String ERRMSG_INPUT_FILE_READ_IO_ERROR_3 = ". System absolute pathname = ";
    public static final String ERRMSG_INPUT_FILE_READ_IO_ERROR_4 = ". Detailed system error message = ";

    public static final String ERRMSG_GMFF_INPUT_FILE_NAME_BLANK_1 = "E-GM-0304 ";
    public static final String ERRMSG_GMFF_INPUT_FILE_NAME_BLANK_1B = " file name blank. Export type = ";

    public static final String ERRMSG_INPUT_FILE_NOT_FOUND_1 = "E-GM-0305 ";
    public static final String ERRMSG_INPUT_FILE_NOT_FOUND_1B = " file not found. File name = ";
    public static final String ERRMSG_INPUT_FILE_NOT_FOUND_2 = ". Export type = ";
    public static final String ERRMSG_INPUT_FILE_NOT_FOUND_3 = ". System absolute pathname = ";

    // ==================================================
    // Messages for GMFFFolder
    // ==================================================

    public static final String ERRMSG_GMFF_FOLDER_NAME_BLANK_1 = "E-GM-0401 GMFF Folder name blank!"
        + " Export type = ";

    public static final String ERRMSG_NOT_A_GMFF_FOLDER_1 = "E-GM-0402 GMFF folder name (exists but is) not a folder."
        + " Folder name = ";
    public static final String ERRMSG_NOT_A_GMFF_FOLDER_2 = ". Export type = ";
    public static final String ERRMSG_NOT_A_GMFF_FOLDER_3 = ". System absolute pathname = ";

    public static final String ERRMSG_GMFF_FOLDER_NOTFND_1 = "E-GM-0403 GMFF folder does not exist."
        + " Folder name = ";
    public static final String ERRMSG_GMFF_FOLDER_NOTFND_2 = ". Export type = ";
    public static final String ERRMSG_GMFF_FOLDER_NOTFND_3 = ". System absolute pathname = ";

    public static final String ERRMSG_GMFF_FOLDER_MISC_ERROR_1 = "E-GM-0404 GMFF folder miscellaneous I/O error!"
        + " Folder name = ";
    public static final String ERRMSG_GMFF_FOLDER_MISC_ERROR_2 = ". Export type = ";
    public static final String ERRMSG_GMFF_FOLDER_MISC_ERROR_3 = ". System absolute pathname = ";
    public static final String ERRMSG_GMFF_FOLDER_MISC_ERROR_4 = ". Detailed system error message = ";

    public static final String ERRMSG_GMFF_FOLDER_READ_ERROR_1 = "E-GM-0405 Folder Name ";
    public static final String ERRMSG_GMFF_FOLDER_READ_ERROR_2 = "\n -- unable to read FILES from this folder because"
        + " of unknown I/O error or it is (now) not a directory.";

    // ==================================================
    // Messages for GMFFExportFile
    // ==================================================

    public static final String ERRMSG_GMFF_EXPORT_FILE_NAME_BLANK_1 = "E-GM-0501 GMFF export file name blank!"
        + " Export type = ";

    public static final String ERRMSG_EXPORT_FILE_EXISTS_NOT_A_FILE_1 = "E-GM-0502 Export file name (exists but is) not a file."
        + " Export file name = ";
    public static final String ERRMSG_EXPORT_FILE_EXISTS_NOT_A_FILE_2 = ". Export type = ";
    public static final String ERRMSG_EXPORT_FILE_EXISTS_NOT_A_FILE_3 = ". System absolute pathname = ";

    public static final String ERRMSG_EXPORT_FILE_EXISTS_CANNOT_UPDATE_1 = "E-GM-0503 Export file name exists but is not updateable."
        + " Export file name = ";
    public static final String ERRMSG_EXPORT_FILE_EXISTS_CANNOT_UPDATE_2 = ". Export type = ";
    public static final String ERRMSG_EXPORT_FILE_EXISTS_CANNOT_UPDATE_3 = ". System absolute pathname = ";

    public static final String ERRMSG_EXPORT_FILE_MISC_ERROR_1 = "E-GM-0504 Export file miscellaneous I/O error!"
        + " Export file name = ";
    public static final String ERRMSG_EXPORT_FILE_MISC_ERROR_2 = ". Export type = ";
    public static final String ERRMSG_EXPORT_FILE_MISC_ERROR_3 = ". System absolute pathname = ";
    public static final String ERRMSG_EXPORT_FILE_MISC_ERROR_4 = ". Detailed system error message = ";

    public static final String ERRMSG_EXPORT_FILE_CLOSE_IO_ERROR_1 = "E-GM-0505 Export file I/O error occured at file close!"
        + " Export file name = ";
    public static final String ERRMSG_EXPORT_FILE_CLOSE_IO_ERROR_2 = ". Export type = ";
    public static final String ERRMSG_EXPORT_FILE_CLOSE_IO_ERROR_3 = ". System absolute pathname = ";
    public static final String ERRMSG_EXPORT_FILE_CLOSE_IO_ERROR_4 = ". Detailed system error message = ";

    public static final String ERRMSG_EXPORT_FILE_IO_ERROR_1 = "E-GM-0506 Export file I/O error!"
        + " Export file name = ";
    public static final String ERRMSG_EXPORT_FILE_IO_ERROR_2 = ". Export type = ";
    public static final String ERRMSG_EXPORT_FILE_IO_ERROR_3 = ". System absolute pathname = ";
    public static final String ERRMSG_EXPORT_FILE_IO_ERROR_4 = ". Detailed system error message = ";

    public static final String ERRMSG_EXPORT_FILE_CHARSET_ERROR_1 = "E-GM-0507 Charset Encoding parm invalid."
        + " Export file name = ";
    public static final String ERRMSG_EXPORT_FILE_CHARSET_ERROR_2 = ". Export type = ";
    public static final String ERRMSG_EXPORT_FILE_CHARSET_ERROR_3 = ". charsetEncoding = ";
    public static final String ERRMSG_EXPORT_FILE_CHARSET_ERROR_4 = ". Detailed system error message = ";

    public static final String ERRMSG_GMFF_EXPORT_FILE_CHARSET_BLANK_1 = "E-GM-0508 GMFF export file Charset Encoding blank!"
        + " Export File = ";

    // ==================================================
    // Messages for TypesetDefCommentParser
    // ==================================================

    public static final String ERRMSG_LOC_INFO_1 = "\n    .mm file $t parse error loc info:"
        + " $t comment line nbr = ";
    public static final String ERRMSG_LOC_INFO_2 = "; curr def keyword = ";
    public static final String ERRMSG_LOC_INFO_3 = "; curr def symbol = ";

    public static final String ERRMSG_PREMATURE_END_OF_DEF_1 = "E-GM-0601 $t parser: premature end of definition";

    public static final String ERRMSG_MISSING_DOLLAR_T_TOKEN_1 = "E-GM-0602 $t parser: missing $t token at start of"
        + " Metamath typsetting comment ($t comment). Expecting it"
        + " to be the first non-whitespace token after the $(";

    public static final String ERRMSG_NESTED_COMMENTS_1 = "E-GM-0603 $t parser: found C-style comment inside C-style"
        + " comment. Cause may be missing end of first comment.";

    public static final String ERRMSG_MISSING_END_COMMENT_1 = "E-GM-0604 $t parser: did not find end of C-style comment.";

    public static final String ERRMSG_INVALID_KEYWORD_CHAR_1 = "E-GM-0605 $t parser: invalid char in definition type"
        + " keyword. Found ";
    public static final String ERRMSG_INVALID_KEYWORD_CHAR_2 = " following start of keyword = ";

    public static final String ERRMSG_KEYWORD_EMPTY_STRING_1 = "E-GM-0606 $t parser: expecting/looking for definition"
        + " type keyword. Found nothing there?!";

    public static final String ERRMSG_INVALID_SYM_1 = "E-GM-0607 $t parser: invalid definition sym. Must"
        + " have length > 0 and contain no whitespace.";

    public static final String ERRMSG_AS_LITERAL_MISSING_1 = "E-GM-0608 $t parser: did not find ' as ' literal"
        + " in sym definition";

    public static final String ERRMSG_MISSING_SEMICOLON_1 = "E-GM-0609 $t parser: did not find the requisite"
        + " semicolon needed to terminate the current" + " definition.";

    public static final String ERRMSG_NOT_A_QUOTED_STRING_1 = "E-GM-0610 $t parser: did not find the requisite"
        + " single-quote or double-quote needed to begin"
        + " a typesetting definition value. Found char = ";

    public static final String ERRMSG_MISSING_END_DELIM_1 = "E-GM-0611 $t parser: did not find the requisite"
        + " closing quote, single or double, needed to enclose"
        + " a typesetting definition value.";

    public static final String ERRMSG_DUP_SYM_TYPESET_DEF_1 = "I-GM-0612 $t parser: duplicate typesetting definition"
        + " found for sym.";

    // ==================================================
    // Messages for GMFFExportParms
    // ==================================================

    public static final String ERRMSG_EXPORT_TYPE_BAD_MISSING_1 = "E-GM-0701 ExportParms error. Export Type is"
        + " required and must contain no whitespace. Found = ";

    public static final String ERRMSG_ON_OFF_BAD_MISSING_1 = "E-GM-0702 ExportParms error for Export Type = ";
    public static final String ERRMSG_ON_OFF_BAD_MISSING_2 = ": ON/OFF is required and must equal 'ON' or 'OFF'. Found = ";

    public static final String ERRMSG_TYPESET_DEF_KEYWORD_BAD_MISSING_1 = "E-GM-0703 ExportParms error for Export Type = ";
    public static final String ERRMSG_TYPESET_DEF_KEYWORD_BAD_MISSING_2 = ": Typesetting Definition Keyword is required and"
        + " must contain no whitespace. Found = ";

    public static final String ERRMSG_EXPORT_DIRECTORY_BAD_MISSING_1 = "E-GM-0704 ExportParms error for Export Type = ";
    public static final String ERRMSG_EXPORT_DIRECTORY_BAD_MISSING_2 = ": Export Directory is required and"
        + " must contain no whitespace. Found = ";

    public static final String ERRMSG_EXPORT_DIRECTORY_BAD2_1 = "E-GM-0705 ExportParms error for Export Type = ";
    public static final String ERRMSG_EXPORT_DIRECTORY_BAD2_2 = ": Export Directory error. Detailed message follows: ";

    public static final String ERRMSG_EXPORT_FILE_TYPE_BAD_MISSING_1 = "E-GM-0706 ExportParms error for Export Type = ";
    public static final String ERRMSG_EXPORT_FILE_TYPE_BAD_MISSING_2 = ": Export File Type is required, must contain no whitespace"
        + " and must begin with '.'. Found = ";

    public static final String ERRMSG_MODELS_DIRECTORY_BAD_MISSING_1 = "E-GM-0707 ExportParms error for Export Type = ";
    public static final String ERRMSG_MODELS_DIRECTORY_BAD_MISSING_2 = ": Models Directory is required and"
        + " must contain no whitespace. Found = ";

    public static final String ERRMSG_MODELS_DIRECTORY_BAD2_1 = "E-GM-0708 ExportParms error for Export Type = ";
    public static final String ERRMSG_MODELS_DIRECTORY_BAD2_2 = ": Models Directory error. Detailed message follows: ";

    public static final String ERRMSG_MODEL_ID_BAD_MISSING_1 = "E-GM-0709 ExportParms error for Export Type = ";
    public static final String ERRMSG_MODEL_ID_BAD_MISSING_2 = ": Model Id is required and must contain no whitespace."
        + " Model Id 'A' is the only defined Model Id at this time."
        + " Found = ";

    public static final String ERRMSG_CHARSET_ENCODING_BAD_MISSING_1 = "E-GM-0710 ExportParms error for Export Type = ";
    public static final String ERRMSG_CHARSET_ENCODING_BAD_MISSING_2 = ": Charset Encoding is required and must contain no whitespace."
        + " Valid Charset Encodings include US-ASCII, ISO-8859-1,"
        + " UTF-8, UTF-16BE, UTF-16LE, and UTF-16";

    public static final String ERRMSG_CHARSET_ENCODING_INVALID_1 = "E-GM-0711 ExportParms error for Export Type = ";
    public static final String ERRMSG_CHARSET_ENCODING_INVALID_2 = ". Input Charset Encoding = ";
    public static final String ERRMSG_CHARSET_ENCODING_INVALID_3 = " is not a valid Charset name. Detailed message from"
        + " system follows: ";

    public static final String ERRMSG_CHARSET_ENCODING_UNSUPPORTED_1 = "E-GM-0712 ExportParms error for Export Type = ";
    public static final String ERRMSG_CHARSET_ENCODING_UNSUPPORTED_2 = ". Input Charset Encoding = ";
    public static final String ERRMSG_CHARSET_ENCODING_UNSUPPORTED_3 = " is a valid Charset name but is not"
        + " supported by your Java system environment.";

    public static final String ERRMSG_OUTPUT_FILE_NAME_ERROR_1 = "E-GM-0713 OutputFileName invalid for Export Type = ";
    public static final String ERRMSG_OUTPUT_FILE_NAME_ERROR_2 = ". Must contain no whitespace characters or"
        + " '/' or '\' or ':'. Found = ";

    // ==================================================
    // Messages for GMFFUserTextEscapes
    // ==================================================

    public static final String ERRMSG_ESCAPE_EXPORT_TYPE_BAD_MISSING_1 = "E-GM-0801 User Text Escapes Parm error. Export Type"
        + " is required, must contain no whitespace, and must"
        + " match one of the GMFFExportParms export types." + " Found = ";

    public static final String ERRMSG_ESCAPE_PAIR_BAD2_1 = "E-GM-0802 ExportParms error for Export Type = ";
    public static final String ERRMSG_ESCAPE_PAIR_BAD2_2 = ": User Text Escape Pair error. Detailed message follows: ";

    // ==================================================
    // Messages for EscapePair
    // ==================================================

    public static final String ERRMSG_ESCAPE_PAIR_NUM_BAD_1 = "E-GM-0901 User Text Escapes Parm error. Export Type = ";
    public static final String ERRMSG_ESCAPE_PAIR_NUM_BAD_1B = " Escape Pair number must be less than 256 and greater"
        + " than or equal to 0. Found = ";

    public static final String ERRMSG_ESCAPE_PAIR_REPLACEMENT_MISSING_1 = "E-GM-0902 User Text Escapes Parm error. Export Type = ";
    public static final String ERRMSG_ESCAPE_PAIR_REPLACEMENT_MISSING_1B = " Escape Pair replacement string is required.";

    public static final String ERRMSG_CAPTION_ESCAPE_PAIR_NUM = "GMFFUserTextEscapes.num";

    // ==================================================
    // Messages for GMFFUserExportChoice
    // ==================================================

    public static final String ERRMSG_USER_EXPORT_CHOICE_BAD_MISSING_1 = "E-GM-1001 User Export Choice Parm error. Choice is"
        + " required (if parm entered), and must contain no"
        + " whitespace. Choice may be any of the Export Types"
        + " or 'ALL'. Found = ";

    // ==================================================
    // Messages for GMFFExporterTypesetDefs
    // ==================================================

    public static final String ERRMSG_TYPESET_DEFS_AUDIT_1 = "I-GM-1101 Metamath Typesetting Definitions Loaded"
        + " for typeset def keyword = ";
    public static final String ERRMSG_TYPESET_DEFS_AUDIT_2 = "  Sym: ";
    public static final String ERRMSG_TYPESET_DEFS_AUDIT_3 = " Repl: ";

    // ==================================================
    // Messages for GMFFBoss
    // ==================================================

    public static final String ERRMSG_GMFF_INITIALIZE_PARM_0_ERR_1 = "A-GM-1201 Invalid parameter on GMFFInitialize RunParm: "
        + " expecting blank or 'PrintTypesettingDefinitions'." + " Found = ";

    public static final String ERRMSG_GMFF_INITIALIZATION_ERROR_1 = "A-GM-1202 Error found during GMFF initialization."
        + " Please review previous messages to determine the"
        + " cause of the problem.";

    public static final String ERRMSG_GMFF_PARSE_RUNPARM_PARM_4_ERR_1 = "A-GM-1203 Invalid parameter on GMFFParseMetamathTypesetComment"
        + " RunParm: expecting blank or 'PRINT'." + " Found = ";

}
