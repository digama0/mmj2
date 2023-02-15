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

import static mmj.pa.ErrorCode.of;

import java.io.File;

import mmj.mmio.MMIOConstants;
import mmj.pa.ErrorCode;
import mmj.pa.MMJException.ErrorContext;
import mmj.pa.PaConstants;
import mmj.transforms.TrConstants;
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
 * <li>{@code TR} = mmj.transforms package (proof assistant) (see
 * {@link TrConstants})
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

    public static final String CHARSET_ENCODING_ISO_8859 = "ISO-8859-1";

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

    public static final String PRINT_TYPESETTING_DEFINITIONS = "PrintTypesettingDefinitions";

    public static final String RUNPARM_PRINT_OPTION = "PRINT";

    public static final GMFFUserExportChoice DEFAULT_USER_EXPORT_CHOICE = new GMFFUserExportChoice(
        GMFFConstants.USER_EXPORT_CHOICE_ALL);

    public static final String DEFAULT_GMFF_HTML_DIRECTORY = "gmff"
        + File.separator + "html";

    public static final String DEFAULT_GMFF_MODELS_HTML_DIRECTORY = "gmff"
        + File.separator + "models" + File.separator + "html";

    public static final GMFFExportParms DEFAULT_EXPORT_PARMS_HTML = new GMFFExportParms(
        GMFFConstants.EXPORT_TYPE_HTML, GMFFConstants.EXPORT_PARM_ON, "htmldef",
        GMFFConstants.DEFAULT_GMFF_HTML_DIRECTORY, ".html",
        GMFFConstants.DEFAULT_GMFF_MODELS_HTML_DIRECTORY, GMFFConstants.MODEL_A,
        GMFFConstants.CHARSET_ENCODING_ISO_8859,
        GMFFConstants.DEFAULT_OUTPUT_FILE_NAME);

    public static final String DEFAULT_GMFF_ALTHTML_DIRECTORY = "gmff"
        + File.separator + "althtml";

    public static final String DEFAULT_GMFF_MODELS_ALTHTML_DIRECTORY = "gmff"
        + File.separator + "models" + File.separator + "althtml";

    public static final GMFFExportParms DEFAULT_EXPORT_PARMS_ALTHTML = new GMFFExportParms(
        GMFFConstants.EXPORT_TYPE_ALTHTML, GMFFConstants.EXPORT_PARM_ON,
        "althtmldef", GMFFConstants.DEFAULT_GMFF_ALTHTML_DIRECTORY, ".html",
        GMFFConstants.DEFAULT_GMFF_MODELS_ALTHTML_DIRECTORY,
        GMFFConstants.MODEL_A, GMFFConstants.CHARSET_ENCODING_ISO_8859,
        GMFFConstants.DEFAULT_OUTPUT_FILE_NAME);

    public static final GMFFExportParms[] DEFAULT_EXPORT_PARMS = {
            GMFFConstants.DEFAULT_EXPORT_PARMS_HTML,
            GMFFConstants.DEFAULT_EXPORT_PARMS_ALTHTML};

    public static final EscapePair[] DEFAULT_ESCAPE_PAIRS = {
            new EscapePair(' ', "&nbsp;"), new EscapePair('"', "&quot;"),
            new EscapePair('&', "&amp;"), new EscapePair('<', "&lt;"),
            new EscapePair('>', "&gt;")};

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
    public static final ErrorCode ERRMSG_TOKENIZER_IO_ERROR = of("E-GM-0001",
        "GMFF: Theorem label = %s IOException while tokenizing line %d"
            + " in Proof Worksheet. Detailed message follows: %s");

    public static final ErrorCode ERRMSG_EMPTY_PROOF = of(
        "E-GM-0002 GMFF: Theorem label = %s Invalid Proof Worksheet! Text area is empty.");

    public static final ErrorCode ERRMSG_BOGUS_LINE_1 = of(
        "E-GM-0003 GMFF: Theorem label = %s Invalid Proof Worksheet! First line is not"
            + " a valid Proof Worksheet Header.");

    public static final ErrorCode ERRMSG_LOAD_READLINE_IO_ERROR = of(
        "E-GM-0004 GMFF: Proof Worksheet load failed due to IO error"
            + " while reading line %d of the Proof Worksheet. Detailed message follows: %s");

    public static final ErrorCode ERRMSG_WORK_STMT_LINE_START_ERROR = of(
        "E-GM-0005 GMFF: Theorem label = %s Proof Worksheet load failed due to invalid"
            + " starting token on line %d of the Proof Worksheet. Input token = %s."
            + " Please note: Column 1 is used to identify the start of"
            + " new Proof Worksheet statements. A blank in Column 1"
            + " indicates a continuation of the previous statement.");

    public static final ErrorCode ERRMSG_WORK_STMT_CONSTRUCTOR_ERROR = of(
        "E-GM-0006 GMFF: Theorem label = %s Error while constructing Proof Work statement"
            + " for line %s of the Proof Worksheet. Detail message follows: %s");

    public static final ErrorCode ERRMSG_INVALID_LINE_LIST_ERROR = of(
        "A-GM-0007 GMFF: Theorem label = %s Invalid lineList argument (lineList) for "
            + " Proof Worksheet MinProofWorksheet.constructStmt() "
            + " function. Indicates a program bug. Contact programmer!"
            + " Proof Worksheet line number = %s");

    public static final ErrorCode ERRMSG_INVALID_HEADER_CONSTANTS_ERROR = of(
        "E-GM-0008 GMFF: Theorem label = %s Proof Worksheet Header does not begin with"
            + " the required identifying constant: "
            + "'$( <MM> <PROOF_ASST> THEOREM='. Line number = %d");

    public static final ErrorCode ERRMSG_INVALID_THEOREM_LABEL_ERROR = of(
        "E-GM-0009 GMFF: Proof Worksheet Header theorem label"
            + " invalid. Found label = %s Label must not be blank or '?',"
            + " must consist of valid"
            + " Metamath label characters, and must not be on the"
            + " list of prohibited Metamath label (see Metamath.pdf.)"
            + " Line number = %s");

    public static final ErrorCode ERRMSG_BUILD_EMPTY_OR_INVALID_WORKSHEET_ERROR = of(
        "A-GM-0010 GMFF: Theorem label = %s MinProofWorksheet export build function called but"
            + " Proof Worksheet is empty or has structural errors."
            + " Indicates a program bug. Contact programmer!");

    // ==================================================
    // Messages for GMFFManager
    // ==================================================
    public static final ErrorCode ERRMSG_NO_EXPORT_TYPES_SELECTED_ERROR = of(
        "E-GM-0101 GMFF: No export types selected. Update RunParms.txt"
            + " if you wish to export ProofWorksheets via GMFF. Then"
            + " restart mmj2 and try again.");

    public static final ErrorCode ERRMSG_INVALID_MODEL_ID_ERROR = of(
        "E-GM-0102 Invalid Export Model Id input = %s. Input export Type = %s");

    public static final ErrorCode ERRMSG_EXPORT_PARMS_LIST_ERROR = of(
        "E-GM-0105 GMFF: Validation errors in consolidated list"
            + " of Export Parms (defaults plus RunParms)."
            + " Refer to prior messages for details.");

    public static final ErrorCode ERRMSG_USER_TEXT_ESCAPES_LIST_ERROR = of(
        "E-GM-0106 GMFF: Validation errors in consolidated list"
            + " of User Text Escapes parms (defaults plus RunParms)."
            + " Refer to prior messages for details.");

    public static final ErrorCode ERRMSG_INVALID_METAMATH_TYPESET_COMMENT_ERROR = of(
        "E-GM-0107 GMFF: Invalid Metamath Typeset Comment input."
            + " Did not find valid $( and $) delimiters.");

    public static final ErrorCode ERRMSG_NO_PROOF_FILES_SELECTED_ERROR = of(
        "E-GM-0108 GMFF: No Proof Worksheet files were found"
            + " in Folder %s with File Type %s");

    public static final ErrorCode ERRMSG_FILE_TYPE_BAD_MISSING = of(
        "E-GM-0109 File Type error. File Type must contain"
            + " no whitespace and must begin with '.'. Found = %s");

    public static final ErrorCode ERRMSG_MAX_NBR_TO_EXPORT_BAD_MISSING = of(
        "E-GM-0110 Maximum number to export parameter invalid."
            + " Must be a positive integer with no embedded"
            + " whitespace, etc. Input = %s");

    public static final ErrorCode ERRMSG_LABEL_OR_ASTERISK_BAD_MISSING = of(
        "E-GM-0111 Theorem label invalid."
            + " Required input. Must have no embedded whitespace."
            + " Input '*' to extract ALL (up to Max Number of)" + " theorems.");

    public static final ErrorCode ERRMSG_APPEND_FILE_NAME_ERROR = of(
        "E-GM-0112 AppendFileName invalid. Must contain no"
            + "whitespace characters or '/' or '\' or ':'. Found = %s");

    public static final ErrorCode ERRMSG_GMFF_THEOREM_EXPORT_PA_ERROR = of(
        "E-GM-0113 Error in Proof Assistant during extraction"
            + " of Proof Worksheet for export. Theorem label = %s."
            + " Detailed error message follows: %s");

    public static final ErrorCode ERRMSG_NO_THEOREMS_SELECTED_ERROR = of(
        "E-GM-0114 No theorems selected for export."
            + " Request parameter (theorem label or '*', ALL) = %s");

    public static final ErrorCode INITIALIZATION_AUDIT_REPORT = of(
        "I-GM-0115 GMFFInitialize RunParm Audit Report:\n\n%s");
    public static final String INITIALIZATION_AUDIT_REPORT_2 = "  %s\n\n";
    public static final String INITIALIZATION_AUDIT_REPORT_3 = ""
        + "  SelectedExporter[%s]\n\n    %s\n\n    %s\n\n    %s\n\n";

    public static final String EXPORTER_AUDIT_REPORT = "Typesetting Definitions:"
        + " .mm $t keyword = %s, Number Of Defined Symbols = %d";

    public static final ErrorCode ERRMSG_INPUT_DOLLAR_T_COMMENT_MM_FILE = of(
        "I-GM-0116 GMFFParseMetamathTypesetComment input directory: %s, File Name: %s,"
            + " TypesetDefKeyword: %s. Input $t comment follows:\n%s\n%s");

    // ==================================================
    // Messages for GMFFExporter
    // ==================================================
    public static final ErrorCode ERRMSG_TYPESET_DEF_NOT_FOUND_ERROR = of(
        "I-GM-0201 Typesetting definition entry not found. Theorem = %s,"
            + " Symbol = %s, Typesetting Def Type = %s. To correct, fix input Metamath (set.mm) $t comment."
            + "To see the typesetting definitions as loaded by mmj2,"
            + " add a GMFFInitialize RunParm immediately before the"
            + " RunProofAsstGUI RunParm, as follows:\n"
            + "\nGMFFInitialize,PrintTypesettingDefinitions"
            + "\nRunProofAsstGUI" + "\n\nThen restart mmj2.");

    public static final ErrorCode ERRMSG_MANDATORY_MODEL_NOT_FOUND_ERROR = of(
        "E-GM-0202 Mandatory export model file not found. Theorem = %s"
            + " Export type = %s. File name = %s."
            + " To correct, copy file into export type Model folder and"
            + " retry -- no need to restart mmj2 in this situation."
            + " Detailed error message follows: %s");

    public static final ErrorCode ERRMSG_EXPORT_CONFIRMATION = of(
        "I-GM-0203 Theorem %s exported to %s");

    // ==================================================
    // Messages for GMFFInputFile
    // ==================================================

    public static final ErrorCode ERRMSG_INPUT_FILE_EXISTS_NOT_A_FILE = of(
        "E-GM-0301 %s file name (exists but is) not a file."
            + " File name = %s. Export type = %s. System absolute pathname = %s.");

    public static final ErrorCode ERRMSG_INPUT_FILE_MISC_ERROR = of(
        "E-GM-0302 %s file name miscellaneous I/O error!"
            + " File name = %s. Export type = %s. System absolute pathname = %s."
            + " Detailed system error message = %s");

    public static final ErrorCode ERRMSG_GMFF_INPUT_FILE_NAME_BLANK = of(
        "E-GM-0304 %s file name blank. Export type = %s");

    public static final ErrorCode ERRMSG_INPUT_FILE_NOT_FOUND = of(
        "E-GM-0305 %s file not found. File name = %s. Export type = %s."
            + " System absolute pathname = %s.");

    // ==================================================
    // Messages for GMFFFolder
    // ==================================================

    public static final ErrorCode ERRMSG_GMFF_FOLDER_NAME_BLANK = of(
        "E-GM-0401 GMFF Folder name blank!" + " Export type = %s");

    public static final ErrorCode ERRMSG_NOT_A_GMFF_FOLDER = of(
        "E-GM-0402 GMFF folder name (exists but is) not a folder."
            + " Folder name = %s. Export type = %s. System absolute pathname = %s.");

    public static final ErrorCode ERRMSG_GMFF_FOLDER_NOTFND = of(
        "E-GM-0403 GMFF folder does not exist."
            + " Folder name = %s. Export type = %s. System absolute pathname = %s.");

    public static final ErrorCode ERRMSG_GMFF_FOLDER_MISC_ERROR = of(
        "E-GM-0404 GMFF folder miscellaneous I/O error!"
            + " Folder name = %s. Export type = %s. System absolute pathname = %s."
            + " Detailed system error message = %s");

    public static final ErrorCode ERRMSG_GMFF_FOLDER_READ_ERROR = of(
        "E-GM-0405 Folder Name %s\n -- unable to read FILES from this folder because"
            + " of unknown I/O error or it is (now) not a directory.%s");

    // ==================================================
    // Messages for GMFFExportFile
    // ==================================================

    public static final ErrorCode ERRMSG_GMFF_EXPORT_FILE_NAME_BLANK = of(
        "E-GM-0501 GMFF export file name blank!" + " Export type = %s");

    public static final ErrorCode ERRMSG_EXPORT_FILE_EXISTS_NOT_A_FILE = of(
        "E-GM-0502 Export file name (exists but is) not a file."
            + " Export file name = %s. Export type = %s. System absolute pathname = %s.");

    public static final ErrorCode ERRMSG_EXPORT_FILE_EXISTS_CANNOT_UPDATE = of(
        "E-GM-0503 Export file name exists but is not updateable."
            + " Export file name = %s. Export type = %s. System absolute pathname = %s.");

    public static final ErrorCode ERRMSG_EXPORT_FILE_MISC_ERROR = of(
        "E-GM-0504 Export file miscellaneous I/O error!"
            + " Export file name = %s. Export type = %s. System absolute pathname = %s."
            + " Detailed system error message = %s");

    public static final ErrorCode ERRMSG_EXPORT_FILE_IO_ERROR = of(
        "E-GM-0506 Export file I/O error!"
            + " Export file name = %s. Export type = %s. System absolute pathname = %s."
            + " Detailed system error message = %s");

    public static final ErrorCode ERRMSG_EXPORT_FILE_CHARSET_ERROR = of(
        "E-GM-0507 Charset Encoding parm invalid."
            + " Export file name = %s. Export type = %s. charsetEncoding = %s."
            + " Detailed system error message = %s");

    public static final ErrorCode ERRMSG_GMFF_EXPORT_FILE_CHARSET_BLANK = of(
        "E-GM-0508 GMFF export file Charset Encoding blank!"
            + " Export File = %s.");

    // ==================================================
    // Messages for TypesetDefCommentParser
    // ==================================================

    public static class ParseLocContext implements ErrorContext {
        private final int lineNbr;
        private final String keyword;
        private final String sym;

        public ParseLocContext(final int lineNbr, final String keyword,
            final String sym)
        {
            this.lineNbr = lineNbr;
            this.keyword = keyword;
            this.sym = sym;
        }

        @Override
        public String append(final String msg) {
            return msg + String.format(
                "\n    .mm file $t parse error loc info:"
                    + " $t comment line nbr = %d curr def keyword = %s curr def symbol = %s",
                lineNbr, keyword, sym);
        }
    }

    public static final ErrorCode ERRMSG_PREMATURE_END_OF_DEF = of(
        "E-GM-0601 $t parser: premature end of definition");

    public static final ErrorCode ERRMSG_MISSING_DOLLAR_T_TOKEN = of(
        "E-GM-0602 $t parser: missing $t token at start of"
            + " Metamath typsetting comment ($t comment). Expecting it"
            + " to be the first non-whitespace token after the $(");

    public static final ErrorCode ERRMSG_NESTED_COMMENTS = of(
        "E-GM-0603 $t parser: found C-style comment inside C-style"
            + " comment. Cause may be missing end of first comment.");

    public static final ErrorCode ERRMSG_MISSING_END_COMMENT = of(
        "E-GM-0604 $t parser: did not find end of C-style comment.");

    public static final ErrorCode ERRMSG_INVALID_KEYWORD_CHAR = of(
        "E-GM-0605 $t parser: invalid char in definition type"
            + " keyword. Found %s following start of keyword = %s");

    public static final ErrorCode ERRMSG_KEYWORD_EMPTY_STRING = of(
        "E-GM-0606 $t parser: expecting/looking for definition"
            + " type keyword. Found nothing there?!");

    public static final ErrorCode ERRMSG_INVALID_SYM = of(
        "E-GM-0607 $t parser: invalid definition sym. Must"
            + " have length > 0 and contain no whitespace.");

    public static final ErrorCode ERRMSG_AS_LITERAL_MISSING = of(
        "E-GM-0608 $t parser: did not find ' as ' literal"
            + " in sym definition");

    public static final ErrorCode ERRMSG_MISSING_SEMICOLON = of(
        "E-GM-0609 $t parser: did not find the requisite"
            + " semicolon needed to terminate the current" + " definition.");

    public static final ErrorCode ERRMSG_NOT_A_QUOTED_STRING = of(
        "E-GM-0610 $t parser: did not find the requisite"
            + " single-quote or double-quote needed to begin"
            + " a typesetting definition value. Found char = %s");

    public static final ErrorCode ERRMSG_MISSING_END_DELIM = of(
        "E-GM-0611 $t parser: did not find the requisite"
            + " closing quote, single or double, needed to enclose"
            + " a typesetting definition value.");

    public static final ErrorCode ERRMSG_DUP_SYM_TYPESET_DEF = of(
        "I-GM-0612 $t parser: duplicate typesetting definition"
            + " found for sym.");

    // ==================================================
    // Messages for GMFFExportParms
    // ==================================================

    public static final ErrorCode ERRMSG_EXPORT_TYPE_BAD_MISSING = of(
        "E-GM-0701 ExportParms error. Export Type is"
            + " required and must contain no whitespace. Found = %s");

    public static final ErrorCode ERRMSG_ON_OFF_BAD_MISSING = of(
        "E-GM-0702 ExportParms error for Export Type = %s:"
            + " ON/OFF is required and must equal 'ON' or 'OFF'. Found = %s");

    public static final ErrorCode ERRMSG_TYPESET_DEF_KEYWORD_BAD_MISSING = of(
        "E-GM-0703 ExportParms error for Export Type = %s:"
            + " Typesetting Definition Keyword is required and"
            + " must contain no whitespace. Found = %s");

    public static final ErrorCode ERRMSG_EXPORT_DIRECTORY_BAD_MISSING = of(
        "E-GM-0704 ExportParms error for Export Type = %s:"
            + " Export Directory is required and"
            + " must contain no whitespace. Found = %s");

    public static final ErrorCode ERRMSG_EXPORT_DIRECTORY_BAD2 = of(
        "E-GM-0705 ExportParms error for Export Type = %s:"
            + " Export Directory error. Detailed message follows: %s");

    public static final ErrorCode ERRMSG_EXPORT_FILE_TYPE_BAD_MISSING = of(
        "E-GM-0706 ExportParms error for Export Type = %s:"
            + " Export File Type is required, must contain no whitespace"
            + " and must begin with '.'. Found = %s");

    public static final ErrorCode ERRMSG_MODELS_DIRECTORY_BAD_MISSING = of(
        "E-GM-0707 ExportParms error for Export Type = %s:"
            + " Models Directory is required and"
            + " must contain no whitespace. Found = %s");

    public static final ErrorCode ERRMSG_MODELS_DIRECTORY_BAD2 = of(
        "E-GM-0708 ExportParms error for Export Type = %s:"
            + " Models Directory error. Detailed message follows: %s");

    public static final ErrorCode ERRMSG_MODEL_ID_BAD_MISSING = of(
        "E-GM-0709 ExportParms error for Export Type = %s:"
            + " Model Id is required and must contain no whitespace."
            + " Model Id 'A' is the only defined Model Id at this time."
            + " Found = %s");

    public static final ErrorCode ERRMSG_CHARSET_ENCODING_BAD_MISSING = of(
        "E-GM-0710 ExportParms error for Export Type = %s:"
            + " Charset Encoding is required and must contain no whitespace."
            + " Valid Charset Encodings include US-ASCII, ISO-8859-1,"
            + " UTF-8, UTF-16BE, UTF-16LE, and UTF-16");

    public static final ErrorCode ERRMSG_CHARSET_ENCODING_INVALID = of(
        "E-GM-0711 ExportParms error for Export Type = %s."
            + " Input Charset Encoding = %s is not a valid Charset name."
            + " Detailed message from system follows: %s");

    public static final ErrorCode ERRMSG_CHARSET_ENCODING_UNSUPPORTED = of(
        "E-GM-0712 ExportParms error for Export Type = %s."
            + " Input Charset Encoding = %s is a valid Charset name but is not"
            + " supported by your Java system environment.");

    public static final ErrorCode ERRMSG_OUTPUT_FILE_NAME_ERROR = of(
        "E-GM-0713 OutputFileName invalid for Export Type = %s."
            + " Must contain no whitespace characters or"
            + " '/' or '\' or ':'. Found = %s");

    // ==================================================
    // Messages for GMFFUserTextEscapes
    // ==================================================

    public static final ErrorCode ERRMSG_ESCAPE_EXPORT_TYPE_BAD_MISSING = of(
        "E-GM-0801 User Text Escapes Parm error. Export Type"
            + " is required, must contain no whitespace, and must"
            + " match one of the GMFFExportParms export types."
            + " Found = %s");

    public static final ErrorCode ERRMSG_ESCAPE_PAIR_BAD2 = of(
        "E-GM-0802 ExportParms error for Export Type = %s:"
            + " User Text Escape Pair error. Detailed message follows: %s");

    // ==================================================
    // Messages for EscapePair
    // ==================================================

    public static final ErrorCode ERRMSG_ESCAPE_PAIR_NUM_BAD = of(
        "E-GM-0901 User Text Escapes Parm error. Export Type = %s"
            + " Escape Pair number must be less than 256 and greater"
            + " than or equal to 0. Found = %d");

    public static final ErrorCode ERRMSG_ESCAPE_PAIR_REPLACEMENT_MISSING = of(
        "E-GM-0902 User Text Escapes Parm error. Export Type = %s"
            + " Escape Pair replacement string is required.");

    // ==================================================
    // Messages for GMFFUserExportChoice
    // ==================================================

    public static final ErrorCode ERRMSG_USER_EXPORT_CHOICE_BAD_MISSING = of(
        "E-GM-1001 User Export Choice Parm error. Choice is"
            + " required (if parm entered), and must contain no"
            + " whitespace. Choice may be any of the Export Types"
            + " or 'ALL'. Found = %s");

    // ==================================================
    // Messages for GMFFExporterTypesetDefs
    // ==================================================

    public static final ErrorCode ERRMSG_TYPESET_DEFS_AUDIT = of(
        "I-GM-1101 Metamath Typesetting Definitions Loaded"
            + " for typeset def keyword = %s\n%s");

    public static final String ERRMSG_TYPESET_DEFS_AUDIT_2 = "  Sym: %s Repl: %s\n";

    // ==================================================
    // Messages for GMFFBoss
    // ==================================================

    public static final ErrorCode ERRMSG_GMFF_INITIALIZE_PARM_0_ERR = of(
        "A-GM-1201 "
            + "Invalid parameter:  expecting blank or '%s'. Found = %s");

    public static final ErrorCode ERRMSG_GMFF_PARSE_RUNPARM_PARM_4_ERR = of(
        "A-GM-1203 " + "Invalid parameter: expecting blank or '%s'. Found %s");

}
