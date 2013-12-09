//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * MMIOConstants.java  0.07 11/01/2011
 *
 * Sep-25-2005:
 *     ->Error message id's changed from "E-" TO "A-" because
 *       their severity halts the run immediately (...abort):
 *         "E-IO-0001 Invalid input character, decimal value = ";
 *         "E-IO-0002 Unable to position reader at char nbr ";
 *         "E-IO-0032 Load() file not found. File name = ";
 *     ->Added " " to message text:
 *         E-IO-0014  Label input for keyword that should not have
 *                    one
 *     ->Removed message, never used:
 *         E-IO-0018
 *
 * Dec-10-2005:
 *     ->Add info messages for new LoadLimit feature that
 *       halts file loading after a user-input statement
 *       number or label is reached.
 *
 * Version 0.04:
 *     ->Add MM_BEGIN_COMPRESSED_PROOF_LIST_CHAR
 *       and MM_END_COMPRESSED_PROOF_LIST_CHAR
 *
 * Sep-02-2006 -- Version 0.05 --
 *     -> comment update for TMFF project messages.
 *     -> add LOAD_COMMENTS_DEFAULT = true
 * Oct-27-2006
 *     -> add LOAD_PROOFS_DEFAULT   = true
 *
 * Version 0.06 AUG-01-2008 --
 *     -- Added new static constants identifying Metmath
 *        comment statements which contain Chapter and
 *        Section titles (called Section and Sub-Section
 *        by Norm.)
 *
 *        MMIOConstants.CHAPTER_ID_C1     = '#';
 *        MMIOConstants.CHAPTER_ID_C2     = '*';
 *        MMIOConstants.SECTION_ID_C1     = '=';
 *        MMIOConstants.SECTION_ID_C2     = '-';
 *     -- Add "~" Metamath .mm comment label escape char
 *        and "\\s", the Java Regex whitespace pattern
 *        and "\\n", the Java Regex newline pattern
 *
 * Version 0.07 - Nov-01-2011:  comment update.
 *     -- Add MMIOConstants.TYPESETTING_COMMENT_ID_STRING
 *        for Systemizer.
 */

package mmj.mmio;

import mmj.gmff.GMFFConstants;
import mmj.pa.PaConstants;
import mmj.util.UtilConstants;
import mmj.verify.GrammarConstants;
import mmj.verify.ProofConstants;

/**
 * Constants used to parse MetaMath source statements.
 * <p>
 * Contains gnasty 7-bit ASCII code-set dependencies! Gasp... I can't even begin
 * to explain the ugliness of this bit-manipulating, symbol-tweaking horror
 * show. Old school all the way :) Needs to be fixed to move into the 21st
 * Century with Unicode -- that will be where we have 3D holo-IO infinite
 * virtual blackboards and virtual reality gloves and voice recognition units
 * that handle math symbols...in Phase IV of the project :)
 * <p>
 * From Metamath.pdf, 4.1:
 * <p>
 * The only characters that are allowed to appear in a Metamath source file are
 * the 94 printable characters on standard ascii keyboards, which are digits,
 * upper and lower case letters, and the following 32 special characters (plus
 * the following non-printable (white space) characters: space, tab, carriage
 * return, line feed, and form feed.:
 * <ul>
 * <li> {@code ` ~ ! @ # $ % ^ & * ( ) - _ = + }
 * <li> <code>[ ] { } ; : ' " , . < > / ? \ | </code>
 * <p>
 * There are two primary types of constants: parameters that are "hardcoded"
 * which affect/control processing, and error/info messages.
 * <p>
 * Each mmj message begins with a code, such as this:
 * <p>
 * <code>E-LA-0007</code>
 * <p>
 * where the format of the code is {@code X-YY-9999}<br>
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
public class MMIOConstants {

    /*
     * -------------------------------------------------------------------
     *
     * D E F A U L T   P A R A M E T E R S   F O R   "S Y S T E M I Z E R"
     *
     * -------------------------------------------------------------------
     */

    public static final int READER_BUFFER_SIZE = 32768;

    public static final byte PRINTABLE = 1;
    public static final byte WHITE_SPACE = 2;
    public static final byte LABEL = 4;
    public static final byte MATH_SYMBOL = 8;
    public static final byte FILE_NAME = 16;
    public static final byte PROOF_STEP = 32;

    public static final byte[] VALID_CHAR_ARRAY = new byte[256];

    public static final byte[] LABEL_CHARS = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9',

            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',

            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',

            '-', '_', '.'};

    public static final byte[] OTHER_MATH_SYMBOL_CHARS = {'`', '~', '!', '@',
            '#', '%', '^', '&', '*', '(', ')', '=', '+',

            '[', ']', '{', '}', ';', ':', '\'', '\"', ',', '<', '>', '/', '\\',
            '|'

    };

    public static final byte[] OTHER_PROOF_STEP_CHARS = {'?'};

    public static final byte[] OTHER_PRINTABLE_CHARS = {'$'};

    public static final byte[] WHITE_SPACE_CHARS =

    {'\r', // carriage return
            '\n', // new line
            '\f', // form feed
            '\t', // tab (horizontal tab)
            ' ' // space
    };

    static {
        for (int i = 0; i < LABEL_CHARS.length; i++)
            VALID_CHAR_ARRAY[LABEL_CHARS[i]] |= PRINTABLE | MATH_SYMBOL
                | FILE_NAME | PROOF_STEP | LABEL;
        for (int i = 0; i < OTHER_PROOF_STEP_CHARS.length; i++)
            VALID_CHAR_ARRAY[OTHER_PROOF_STEP_CHARS[i]] |= PRINTABLE
                | MATH_SYMBOL | FILE_NAME | PROOF_STEP;
        for (int i = 0; i < OTHER_MATH_SYMBOL_CHARS.length; i++)
            VALID_CHAR_ARRAY[OTHER_MATH_SYMBOL_CHARS[i]] |= PRINTABLE
                | MATH_SYMBOL | FILE_NAME;
        for (int i = 0; i < OTHER_PRINTABLE_CHARS.length; i++)
            VALID_CHAR_ARRAY[OTHER_PRINTABLE_CHARS[i]] |= PRINTABLE;
        for (int i = 0; i < WHITE_SPACE_CHARS.length; i++)
            VALID_CHAR_ARRAY[WHITE_SPACE_CHARS[i]] |= WHITE_SPACE;
    }

    /* avoid conflicts with OS File name restrictions */
    public static final String[] PROHIBITED_LABELS = {
            "NUL",
            "CON",
            "PRN",
            "AUX", // "CLOCK$",
            "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8",
            "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7",
            "LPT8", "LPT9", "nul", "con",
            "prn",
            "aux", // "clock$",
            "com1", "com2", "com3", "com4", "com5", "com6", "com7", "com8",
            "com9", "lpt1", "lpt2", "lpt3", "lpt4", "lpt5", "lpt6", "lpt7",
            "lpt8", "lpt9"};

    public static final int MM_KEYWORD_LEN = 2;
    public static final char MM_KEYWORD_1ST_CHAR = '$';

    public static final char MM_CNST_KEYWORD_CHAR = 'c';
    public static final char MM_VAR_KEYWORD_CHAR = 'v';
    public static final char MM_DJ_VAR_KEYWORD_CHAR = 'd';
    public static final char MM_VAR_HYP_KEYWORD_CHAR = 'f';
    public static final char MM_LOG_HYP_KEYWORD_CHAR = 'e';
    public static final char MM_AXIOMATIC_ASSRT_KEYWORD_CHAR = 'a';
    public static final char MM_PROVABLE_ASSRT_KEYWORD_CHAR = 'p';

    public static final char MM_BEGIN_SCOPE_KEYWORD_CHAR = '{';
    public static final char MM_END_SCOPE_KEYWORD_CHAR = '}';

    public static final char MM_BEGIN_COMMENT_KEYWORD_CHAR = '(';
    public static final char MM_END_COMMENT_KEYWORD_CHAR = ')';

    public static final char MM_BEGIN_COMPRESSED_PROOF_LIST_CHAR = '(';
    public static final char MM_END_COMPRESSED_PROOF_LIST_CHAR = ')';

    public static final char MM_END_STMT_KEYWORD_CHAR = '.';

    public static final char MM_BEGIN_FILE_KEYWORD_CHAR = '[';
    public static final char MM_END_FILE_KEYWORD_CHAR = ']';

    public static final String MM_END_STMT_KEYWORD = "$.";
    public static final String MM_START_PROOF_KEYWORD = "$=";

    public static final String MM_END_COMMENT_KEYWORD = "$)";
    public static final String MM_START_COMMENT_KEYWORD = "$(";
    public static final String MM_END_FILE_KEYWORD = "$]";

    // "~" precedes labels used withing Metamath .mm comment statements
    public static final String MM_LABEL_IN_COMMENT_ESCAPE_STRING = "~";

    public static final String MM_JAVA_REGEX_WHITESPACE = "\\s";

    public static final String MM_JAVA_REGEX_NEWLINE = "\\n";

    // from Systemizer.java (AND mmj.gmff.TypesetDefCommentParser.java)

    public static final String CHAPTER_ID_STRING = "#*#*";
    public static final String SECTION_ID_STRING = "=-=-";

    public static final String TYPESETTING_COMMENT_ID_STRING = "$t";
    public static final String TYPESETTING_C_COMMENT_START = "/*";
    public static final String TYPESETTING_C_COMMENT_END = "*/";
    public static final String TYPESETTING_AS_LITERAL = "as";

    public static final char TYPESETTING_SINGLE_QUOTE_CHAR = '\'';
    public static final char TYPESETTING_DOUBLE_QUOTE_CHAR = '\"';
    public static final char TYPESETTING_PLUS_CHAR = '+';
    public static final char TYPESETTING_SLASH_CHAR = '/';
    public static final char TYPESETTING_ASTERISK_CHAR = '*';
    public static final char TYPESETTING_SEMICOLON_CHAR = ';';

    // from Statementizer.java

    public static final String DEFAULT_TITLE = "Default Title";
    public static final char NEW_LINE_CHAR = '\n';
    public static final char CARRIAGE_RETURN_CHAR = '\r';

    /**
     * Load Comments Default equal true.
     * <p>
     * If set to true then Metamath comments, at least on theorems, will be
     * loaded into LogicalSystem.
     * <p>
     * If a Metamath comment statement immediately precedes a $p (Theorem)
     * statement then the comment is stored on the mmj.lang.MObj in the
     * "description" field. Later, perhaps, descriptions will be obtained for
     * all MObj's, but for now we just need them for Proof Assistant.
     */
    public static final boolean LOAD_COMMENTS_DEFAULT = true;

    /**
     * Missing Proof Step label.
     * <p>
     * Equal "?". Every proof must have at least one step according to
     * Metamath.pdf.
     */
    public static final String MISSING_PROOF_STEP = "?";

    /**
     * Load Proofs Default equal true.
     * <p>
     * If set to true then Metamath proofs will be loaded as input. Otherwise, a
     * single step = "?" will be passed to the SystemLoader when adding a
     * Theorem.
     * <p>
     */
    public static final boolean LOAD_PROOFS_DEFAULT = true;

    // from MMIOError.java, MMIOException.java

    public static final String ERRMSG_TXT_SOURCE_ID = " Source Id: ";
    public static final String ERRMSG_TXT_LINE = " Line: ";
    public static final String ERRMSG_TXT_COLUMN = " Column: ";

    // from Tokenizer.java

    public static final String ERRMSG_INV_INPUT_CHAR = "A-IO-0001 Invalid input character, decimal value = ";

    public static final String ERRMSG_SKIP_AHEAD_FAILED = "A-IO-0002 Unable to position reader at char nbr ";

    // from Statementizer.java

    public static final String ERRMSG_TXT_LABEL = " Label = ";
    public static final String ERRMSG_TXT_KEYWORD = " Keyword = ";

    public static final String ERRMSG_INV_KEYWORD = "E-IO-0003 Invalid keyword read = ";

    public static final String ERRMSG_EMPTY_CNST_STMT = "E-IO-0004 Constant statement ($c) with no constants read ";

    public static final String ERRMSG_INV_CHAR_IN_MATH_SYM = "E-IO-0005 Invalid character in Math Symbol. Symbol read = ";

    public static final String ERRMSG_EMPTY_VAR_STMT = "E-IO-0006 Variable statement ($v) with no variables read ";

    public static final String ERRMSG_LESS_THAN_2_DJVARS = "E-IO-0007 Fewer than 2 disjoint variables in $d statement.";

    public static final String ERRMSG_MISSING_LABEL = "E-IO-0008 Label missing for keyword = ";

    public static final String ERRMSG_MISSING_START_COMMENT = "E-IO-0009 End Comment keyword \"$)\" without"
        + " matching Start Comment \"$(\". ";

    public static final String ERRMSG_INV_CHAR_IN_LABEL = "E-IO-0010 Invalid character in Label, token read = ";

    public static final String ERRMSG_PROHIBITED_LABEL = "E-IO-0011 Prohibited Label (no device name such as"
        + " NUL, LPT1, CON, etc.) = ";

    public static final String ERRMSG_EOF_AFTER_LABEL = "E-IO-0012 Premature End of File following Label = ";

    public static final String ERRMSG_MISSING_KEYWORD_AFTER_LABEL = "E-IO-0013 Token after label not a keyword ($*), label = ";

    public static final String ERRMSG_MISLABELLED_KEYWORD = "E-IO-0014 Label input for keyword that should not"
        + " have one (only $e, $f, $a, $p have labels).";

    public static final String ERRMSG_INV_COMMENT_CHAR_STR = "E-IO-0015 Comment contains embedded $( or $) character"
        + " string, token = ";

    public static final String ERRMSG_INV_INCLUDE_FILE_NAME = "E-IO-0016 Include File Name contains $ or other invalid"
        + " characters: ";

    public static final String ERRMSG_PREMATURE_INCLUDE_STMT_EOF = "E-IO-0017 Include statement ($[ x.mm $]) incomplete,"
        + " premature end of file!";

//  obsolete -- erroneous too.
//  public static final String ERRMSG_LT_2_LOG_HYP_TOKENS =
//      "E-IO-0018 Logical Hypothesis ($e) statement body"
//      + " has less than 2 tokens";

    public static final String ERRMSG_STMT_HAS_DUP_TOKENS = "E-IO-0019 Statement has duplicate tokens."
        + " Statment keyword = ";

    public static final String ERRMSG_STMT_PREMATURE_EOF = "E-IO-0020 Statement incomplete, end of file reached"
        + " while reading tokens. Statment keyword = ";

    public static final String ERRMSG_STMT_MISSING_TYPE = "E-IO-0021 Statement token list must begin with"
        + " a constant symbol (type). Statment keyword = ";

    public static final String ERRMSG_VAR_HYP_NE_2_TOKENS = "E-IO-0022 A \"$f\" statement requires exactly"
        + " two math symbols.";

    public static final String ERRMSG_PROOF_MISSING = "E-IO-0023 A \"$p\" statement requires \"$=\" followed"
        + " by proof steps.";

    public static final String ERRMSG_INV_CHAR_IN_PROOF_STEP = "E-IO-0024 Invalid character in proof step. Token read = ";

    public static final String ERRMSG_PROOF_IS_EMPTY = "E-IO-0025 Proof must have at least one step (which"
        + " may be a \"?\" symbol).";

    public static final String ERRMSG_SET_STMT_NBR_LT_0 = "A-IO-0026 setStmtNbr() negative Stmt Nbr input = ";

    public static final String ERRMSG_SET_TOKENIZER_NULL = "A-IO-0027 setTokenizer() input is null.";

    public static final String ERRMSG_PREMATURE_COMMENT_EOF = "E-IO-0028 Comment \"$( ... $)\" incomplete,"
        + " premature end of file!";

    public static final String ERRMSG_COMPRESSED_PROOF_IS_EMPTY = "E-IO-0201 Compressed proof must have at least one"
        + " block of compressed data!";

    // from Systemizer.java

    public static final String ERRMSG_INPUT_FILE_EMPTY = "E-IO-0028 Input File Empty! ";

    public static final String ERRMSG_INCL_FILE_NOTFND_1 = "E-IO-0029 Include File Not Found = ";
    public static final String ERRMSG_INCL_FILE_NOTFND_2 = ". Check $[ ??.mm $] name and directory.";

    public static final String ERRMSG_INCL_FILE_DUP = "E-IO-0030 Include File statement for file that was"
        + " already loaded. File name = ";

    public static final String ERRMSG_LOAD_REQ_FILE_DUP = "E-IO-0031 Load() request for file that was already"
        + " loaded. File name = ";

    public static final String ERRMSG_LOAD_REQ_FILE_NOTFND = "A-IO-0032 Load() file not found. File name = ";

    public static final String EOF_ERRMSG = "E-IO-0033 End of file error message: ";

    public static final String ERRMSG_INCLUDE_FILE_LIST_ERR = "A-IO-0034 Include File List's file not found in"
        + " termIncludeFile()";

    public static final String ERRMSG_LOAD_LIMIT_STMT_NBR_REACHED = "I-IO-0101 Load Endpoint Statement Number Reached,"
        + " Metamath file(s) load to be halted at input statement"
        + " number = ";

    public static final String ERRMSG_LOAD_LIMIT_STMT_LABEL_REACHED = "I-IO-0102 Load Endpoint Statement Label Reached,"
        + " Metamath file(s) load to be halted at input statement"
        + " label = ";

    // from IncludeFile.java

    public static final String ERRMSG_INCLUDE_FILE_ARRAY_EMPTY = "A-IO-0035 Ooops! IncludeFile array is empty: code bug!";
}
