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

import static mmj.pa.ErrorCode.of;

import java.util.*;

import mmj.gmff.GMFFConstants;
import mmj.pa.ErrorCode;
import mmj.pa.MMJException.ErrorContext;
import mmj.pa.MMJException.FormatContext;
import mmj.pa.PaConstants;
import mmj.transforms.TrConstants;
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
 * <li>{@code ` ~ ! @ # $ % ^ & * ( ) - _ = + }
 * <li><code>[ ] { } ; : ' " , . < > / ? \ | </code>
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
 * <li>{@code TR} = mmj.transforms package (proof assistant) (see
 * {@link TrConstants})
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

    /**
     * Printable character. Contains all ASCII printable characters (character
     * codes 32-126), except for {@link #WHITE_SPACE} characters.
     */
    public static final byte PRINTABLE = 1;

    /**
     * Whitespace character. Contains the space, tab, form feed, carriage
     * return, and new line characters.
     */
    public static final byte WHITE_SPACE = 2;
    /**
     * Label character. Contains alphanumeric characters, and the characters
     * {@code '-', '_', '.'}.
     */
    public static final byte LABEL = 4;

    /**
     * Math symbol character, contains all {@link #PRINTABLE} characters except
     * {@code '$'}.
     */
    public static final byte MATH_SYMBOL = 8;

    /** File name character, same as {@link #MATH_SYMBOL}. */
    public static final byte FILE_NAME = 16;

    public static final byte[] VALID_CHAR_ARRAY = new byte[256];

    static {
        for (final byte labelChar : new byte[]{'0', '1', '2', '3', '4', '5',
                '6', '7', '8', '9',

                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',

                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',

                '-', '_', '.'})
            VALID_CHAR_ARRAY[labelChar] |= PRINTABLE | MATH_SYMBOL | FILE_NAME
                | LABEL;
        for (final byte otherMathSymbolChar : new byte[]{'`', '~', '!', '@',
                '#', '%', '^', '&', '*', '(', ')', '=', '+',

                '[', ']', '{', '}', ';', ':', '\'', '\"', ',', '<', '>', '/',
                '?', '\\', '|'})
            VALID_CHAR_ARRAY[otherMathSymbolChar] |= PRINTABLE | MATH_SYMBOL
                | FILE_NAME;
        VALID_CHAR_ARRAY['$'] |= PRINTABLE;
        for (final byte whiteSpace : new byte[]{'\r', '\n', '\f', '\t', ' '})
            VALID_CHAR_ARRAY[whiteSpace] |= WHITE_SPACE;
    }

    /* avoid conflicts with OS File name restrictions */
    public static final Set<String> PROHIBITED_LABELS = new HashSet<>(
        Arrays.asList("nul", "con", "prn", "aux", "com1", "com2", "com3",
            "com4", "com5", "com6", "com7", "com8", "com9", "lpt1", "lpt2",
            "lpt3", "lpt4", "lpt5", "lpt6", "lpt7", "lpt8", "lpt9"));

    public static final char MM_KEYWORD_1ST_CHAR = '$';

    public static final String MM_CNST_KEYWORD = "$c";
    public static final String MM_VAR_KEYWORD = "$v";
    public static final String MM_DJ_VAR_KEYWORD = "$d";
    public static final String MM_VAR_HYP_KEYWORD = "$f";
    public static final String MM_LOG_HYP_KEYWORD = "$e";
    public static final String MM_AXIOMATIC_ASSRT_KEYWORD = "$a";
    public static final String MM_PROVABLE_ASSRT_KEYWORD = "$p";

    public static final String MM_BEGIN_SCOPE_KEYWORD = "${";
    public static final String MM_END_SCOPE_KEYWORD = "$}";

    public static final String MM_BEGIN_COMMENT_KEYWORD = "$(";
    public static final String MM_END_COMMENT_KEYWORD = "$)";

    public static final char MM_BEGIN_COMPRESSED_PROOF_LIST_CHAR = '(';
    public static final char MM_END_COMPRESSED_PROOF_LIST_CHAR = ')';

    public static final char MM_END_STMT_KEYWORD_CHAR = '.';

    public static final String MM_BEGIN_FILE_KEYWORD = "$[";
    public static final String MM_END_FILE_KEYWORD = "$]";

    public static final String MM_END_STMT_KEYWORD = "$.";
    public static final String MM_START_PROOF_KEYWORD = "$=";

    // "~" precedes labels used within Metamath .mm comment statements
    public static final String MM_LABEL_IN_COMMENT_ESCAPE_STRING = "~";

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

    public static class FileContext extends FormatContext {
        public final String sourceId;

        public FileContext(final String sourceId) {
            super("Source Id: %s", sourceId);
            this.sourceId = sourceId;
        }
    }

    public static class LineColumnContext extends FormatContext {
        public final long lineNbr;
        public final long columnNbr;
        public final long charNbr;

        public LineColumnContext(final long lineNbr, final long columnNbr,
            final long charNbr)
        {
            super("Line: %d Column: %d", lineNbr, columnNbr);
            this.lineNbr = lineNbr;
            this.columnNbr = columnNbr;
            this.charNbr = charNbr;
        }
    }

    // from Tokenizer.java

    public static final ErrorCode ERRMSG_SKIP_AHEAD_FAILED = of(
        "A-IO-0002 Unable to position reader at char nbr %d.");

    // from Statementizer.java

    public static final String ERRMSG_TXT_LABEL = " Label = ";
    public static final String ERRMSG_TXT_KEYWORD = " Keyword = ";

    public static final ErrorCode ERRMSG_INV_KEYWORD = of(
        "E-IO-0003 Invalid keyword read = %s");

    public static final ErrorCode ERRMSG_EMPTY_CNST_STMT = of(
        "E-IO-0004 Constant statement ($c) with no constants read %s");

    public static final ErrorCode ERRMSG_INV_CHAR_IN_MATH_SYM = of(
        "E-IO-0005 Invalid character in Math Symbol. Symbol read = %s");

    public static final ErrorCode ERRMSG_EMPTY_VAR_STMT = of(
        "E-IO-0006 Variable statement ($v) with no variables read %s");

    public static final ErrorCode ERRMSG_LESS_THAN_2_DJVARS = of(
        "E-IO-0007 Fewer than 2 disjoint variables in $d statement.%s");

    public static final ErrorCode ERRMSG_MISSING_LABEL = of(
        "E-IO-0008 Label missing for keyword = %s");

    public static final ErrorCode ERRMSG_MISSING_START_COMMENT = of(
        "E-IO-0009 End Comment keyword \"$)\" without"
            + " matching Start Comment \"$(\". %s");

    public static final ErrorCode ERRMSG_INV_LABEL = of(
        "E-IO-0010 Invalid Label name = %s");

    public static final ErrorCode ERRMSG_EOF_AFTER_LABEL = of(
        "E-IO-0012 Premature End of File following Label = %s");

    public static final ErrorCode ERRMSG_MISSING_KEYWORD_AFTER_LABEL = of(
        "E-IO-0013 Token after label not a keyword ($*), label = %s");

    public static final ErrorCode ERRMSG_MISLABELLED_KEYWORD = of(
        "E-IO-0014 Label = %s input for keyword %s that should not"
            + " have one (only $e, $f, $a, $p have labels).");

    public static final ErrorCode ERRMSG_INV_COMMENT_CHAR_STR = of(
        "E-IO-0015 Comment contains embedded $( or $) character"
            + " string, token = %s");

    public static final ErrorCode ERRMSG_INV_INCLUDE_FILE_NAME = of(
        "E-IO-0016 Include File Name contains $ or other invalid"
            + " characters: %s");

    public static final ErrorCode ERRMSG_PREMATURE_INCLUDE_STMT_EOF = of(
        "E-IO-0017 Include statement ($[ x.mm $]) incomplete,"
            + " premature end of file!");

//  obsolete -- erroneous too.
//  public static final String ERRMSG_LT_2_LOG_HYP_TOKENS =
//      "E-IO-0018 Logical Hypothesis ($e) statement body"
//      + " has less than 2 tokens";

    public static final ErrorCode ERRMSG_STMT_HAS_DUP_TOKENS = of(
        "E-IO-0019 Statement has duplicate tokens." + " Statment keyword = %s");

    public static final ErrorCode ERRMSG_STMT_PREMATURE_EOF = of(
        "E-IO-0020 Statement incomplete, end of file reached"
            + " while reading tokens. Statment keyword = %s");

    public static final ErrorCode ERRMSG_STMT_MISSING_TYPE = of(
        "E-IO-0021 Statement token list must begin with"
            + " a constant symbol (type). Statment keyword = %s");

    public static final ErrorCode ERRMSG_VAR_HYP_NE_2_TOKENS = of(
        "E-IO-0022 A \"$f\" statement requires exactly" + " two math symbols.");

    public static final ErrorCode ERRMSG_PROOF_MISSING = of(
        "E-IO-0023 A \"$p\" statement requires \"$=\" followed"
            + " by proof steps.");

    public static final ErrorCode ERRMSG_INV_CHAR_IN_PROOF_STEP = of(
        "E-IO-0024 Invalid character in proof step. Token read = %s");

    public static final ErrorCode ERRMSG_PROOF_IS_EMPTY = of(
        "E-IO-0025 Proof must have at least one step (which"
            + " may be a \"?\" symbol).");

    public static final ErrorCode ERRMSG_SET_STMT_NBR_LT_0 = of(
        "A-IO-0026 setStmtNbr() negative Stmt Nbr input = %s");

    public static final ErrorCode ERRMSG_SET_TOKENIZER_NULL = of(
        "A-IO-0027 setTokenizer() input is null.");

    public static final ErrorCode ERRMSG_PREMATURE_COMMENT_EOF = of(
        "E-IO-0028 Comment \"$( ... $)\" incomplete,"
            + " premature end of file!");

    public static final ErrorCode ERRMSG_COMPRESSED_PROOF_IS_EMPTY = of(
        "E-IO-0029 Compressed proof must have at least one"
            + " block of compressed data!");

    // from Systemizer.java

    public static final ErrorCode ERRMSG_INPUT_FILE_EMPTY = of("I-IO-0101",
        "Input File Empty!");

    public static final ErrorCode ERRMSG_INCL_FILE_NOTFND = of("E-IO-0102",
        "Include File Not Found = %s. Check $[ ??.mm $] name and directory.");

    public static final ErrorCode ERRMSG_INCL_FILE_DUP = of(
        "E-IO-0103 Include File statement for file that was"
            + " already loaded. File name = %s");

    public static final ErrorCode ERRMSG_LOAD_REQ_FILE_DUP = of(
        "E-IO-0104 Load() request for file that was already"
            + " loaded. File name = %s");

    public static final ErrorCode ERRMSG_LOAD_REQ_FILE_NOTFND = of(
        "A-IO-0105 Load() file not found. File name = %s");

    public static final ErrorCode ERRMSG_LOAD_MISC_IO = of(
        "E-IO-0106 I/O error occurred during load, File name = %s."
            + " Detailed error message: %s");

    public static final ErrorContext EOF_ERRMSG = new FormatContext(
        "End of file")
    {};

    public static final ErrorCode ERRMSG_INCLUDE_FILE_LIST_ERR = of(
        "A-IO-0107 Include File List's file not found in"
            + " termIncludeFile()");

    public static final ErrorCode ERRMSG_LOAD_LIMIT_STMT_NBR_REACHED = of(
        "I-IO-0108 Load Endpoint Statement Number Reached,"
            + " Metamath file(s) load to be halted at input statement"
            + " number = %d");

    public static final ErrorCode ERRMSG_LOAD_LIMIT_STMT_LABEL_REACHED = of(
        "I-IO-0109 Load Endpoint Statement Label Reached,"
            + " Metamath file(s) load to be halted at input statement"
            + " label = %d");

    // from IncludeFile.java

    public static final ErrorCode ERRMSG_INCLUDE_FILE_ARRAY_EMPTY = of(
        "A-IO-0201 Ooops! IncludeFile array is empty: code bug!");
}
