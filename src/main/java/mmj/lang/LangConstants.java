//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * LangConstants.java  0.10 11/01/2011
 *
 * Aug-30-2005: misc. message typos fixed.
 *
 * Sep-25-2005:
 *     -->misc. message typos fixed.
 *     -->noted, a few of the "E-LA-" messages are
 *        overridden by "E-IO-" messages because
 *        mmj.lang.LogicalSystem.java is double-checking.
 *     -->Error message id's changed from "E-" TO "A-" because
 *        their severity halts the run immediately (...abort):
 *            "E-LA-0030 ParseTree conversion to RPN failed."
 *            "E-LA-0033 Statement Label string is empty.";
 *            "E-LA-0034 Sym Id string is empty.";
 *
 * Version 0.03:
 *     --> Added new messages for ParseNode.java
 *             ERRMSG_ASSRT_SUBST_HYP_NOTFND_1
 *             ERRMSG_UNIFY_SUBST_HYP_NOTFND_1
 *     --> Added stuff for ProofCompression.java to use :)
 *
 * Version 0.04:
 * Aug-27-2006: Added error message
 *              A-LA-0201 ERRMSG_BAD_PARSE_STMT_1
 *              for TMFF project.
 *
 * Sep-02-2006: comment update for TMFF project messages.
 * Oct-12-2006: New error messages supporting
 *              Metamath.pdf spec change of 6-24-2006
 *              prohibiting Stmt label and Sym id namespace
 *              collisions.
 * Oct-15-2006: Set max error messages and max info messages
 *              to 15,000 to match the standard RunParms.txt file.
 *
 * Version 0.05 -- 06/01/2007
 *     --> Added messages for use in stopInstrumentationTimer()
 *
 * Version 0.06 -- 08/01/2007
 *     --> Misc. Work Var Enhancements.
 *
 * Version 0.07 -- 02/01/2008
 *     --> Modified E-LA-0028 and E-LA-0029 messages to
 *         spell out "SymbolTable" and "StatementTable".
 *     --> Add ERRMSG_SUBTREE_CONV_TO_RPN_FAILURE for
 *         use by ParseNode.converToRPN()
 *     --> Increase SYM_TBL_INITIAL_SIZE_DEFAULT and
 *                 STMT_TBL_INITIAL_SIZE_DEFAULT.
 *
 * Version 0.08 -- 08/01/2008
 *     --> Add constants for BookManager.java
 *     --> Add constants for SeqAssigner.java
 *     --> Add stuff for Theorem Loader.
 *     --> Add ERRMSG_DUP_SYM_MAP_PUT_ATTEMPT (A-LA-0041)
 *         and ERRMSG_DUP_STMT_MAP_PUT_ATTEMPT (A-LA-0042)
 *         for duplicate checking by LogicalSystem.
 *     --> Add ERRMSG_DUP_DJ_VARS_AFTER_CONSOLIDATION_ERR_1
 *         and ERRMSG_DUP_DJ_VARS_AFTER_CONSOLIDATION_ERR_2
 *
 * Version 0.09 -- 07/01/2011
 *     --> Update SYM_TBL_INITIAL_SIZE_DEFAULT to 1500 and
 *                STMT_TBL_INITIAL_SIZE_DEFAULT to 45000.
 *
 * Version 0.10 - Nov-01-2011:  comment update.
 *     --> Add stmt label to ERRMSG_BAD_PARSE_STMT_1
 *
 * Version 0.11 - Sep-02-2013
 *     --> Add CNST_SET_TYPE
 */

package mmj.lang;

import static mmj.pa.ErrorCode.of;

import mmj.gmff.GMFFConstants;
import mmj.mmio.MMIOConstants;
import mmj.pa.ErrorCode;
import mmj.pa.PaConstants;
import mmj.transforms.TrConstants;
import mmj.util.UtilConstants;
import mmj.verify.GrammarConstants;
import mmj.verify.ProofConstants;

/**
 * Constants used in mmj.lang package.
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
public class LangConstants {

    /**
     * for LogicalSystem.java
     */

    /**
     * Default initial size of Symbol Table, large enough to handle set.mm times
     * 1.5.
     * <p>
     * The system will grow the table, as needed.
     */
    public static final int SYM_TBL_INITIAL_SIZE_DEFAULT = 1500;

    /**
     * Minimum size of Symbol Table.
     * <p>
     * An arbitrary small number greater than zero :)
     */
    public static final int SYM_TBL_INITIAL_SIZE_MINIMUM = 10;

    /**
     * Default initial size of Statement Table, large enough to handle set.mm
     * times 1.5.
     * <p>
     * An arbitrary small number greater than zero :)
     */
    public static final int STMT_TBL_INITIAL_SIZE_DEFAULT = 45000;

    /**
     * Minimum size of Statement Table.
     * <p>
     * An arbitrary small number greater than zero :)
     */
    public static final int STMT_TBL_INITIAL_SIZE_MINIMUM = 100;

//  DEPRECATED/DELETED AS OF 08/01/2008 RELEASE
//  SEE LangConstants.SEQ_ASSIGNER_INTERVAL_SIZE_DEFAULT
//  /**
//   * First .mm MObj (Sym/Stmt/etc) .seq = 10, second, 20, etc.
//   * <p>
//   * MObj.seq numbers are assigned sequentially in
//   * input/database sequence, but a gapping interval
//   * is applied via multiples of this number.
//   */
//  public static final int      MOBJ_SEQ_NBR_INCREMENT
//                                = 10;

    /**
     * Default ProofVerifier is null, meaning no proof verification is done
     * automatically.
     * <p>
     * The ProofVerifier can be added after the .mm file is loaded.
     */
    public static final ProofVerifier PROOF_VERIFIER_DEFAULT = null;

    /**
     * Default SyntaxVerifier is null, meaning no grammar or syntax
     * parsing/verification is done automatically.
     * <p>
     * The SyntaxVerifier can be added after the .mm file is loaded.
     */
    public static final SyntaxVerifier SYNTAX_VERIFIER_DEFAULT = null;

    /**
     * Default maximum number of error messages in mmj.lang.Messages.java, the
     * mmj message repository during processing.
     * <p>
     * The number of error messages is important, and is used to halt processing
     * when the maximum is reached. Also, Proof Verification and/or Syntax
     * Verification will not take place unless the .mm file was loaded with zero
     * errors.
     * <p>
     * Set this number to 9999 if desired, it is not a problem unless you are
     * short on memory.
     */
    public static final int MAX_ERROR_MESSAGES_DEFAULT = 15000;

    /**
     * Default maximum number of "info" messages in mmj.lang.Messages.java, the
     * mmj message repository during processing.
     * <p>
     * There are not many info messages in mmj at this time. Basically they fall
     * into the category of "warnings", but in the future might include audit or
     * statistical data ("instrumentation").
     */
    public static final int MAX_INFO_MESSAGES_DEFAULT = 15000;

    // ====================================================

    /**
     * for SeqAssigner.java
     */

    /**
     * Sequence Number minimum interval size.
     * <p>
     * SEQ_ASSIGNER_MIN_INTERVAL_SIZE = 1.
     */
    public static final int SEQ_ASSIGNER_MIN_INTERVAL_SIZE = 1;

    /**
     * Sequence Number maximum interval size.
     * <p>
     * SEQ_ASSIGNER_MAX_INTERVAL_SIZE = 10000.
     */
    public static final int SEQ_ASSIGNER_MAX_INTERVAL_SIZE = 10000;

    /**
     * Sequence Number default interval size.
     * <p>
     * SEQ_ASSIGNER_INTERVAL_SIZE_DEFAULT = 1000.
     */
    public static final int SEQ_ASSIGNER_INTERVAL_SIZE_DEFAULT = 1000;

    /**
     * Sequence Number Interval Table HashMap minimum initial size.
     * <p>
     * SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE_MIN = 10.
     */
    public static final int SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE_MIN = 10;

    /**
     * Sequence Number Interval Table HashMap maximum initial size.
     * <p>
     * SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE_MAX = 10000.
     */
    public static final int SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE_MAX = 10000;

    /**
     * Sequence Number Interval Table HashMap initial size.
     * <p>
     * SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE_DEFAULT = 100.
     */
    public static final int SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE_DEFAULT = 100;

    // ====================================================

    /**
     * for ProofCompression.java
     */

    public static final int COMPRESS_LOW_BASE = 20;

    public static final int COMPRESS_HIGH_BASE = 5;

    public static final byte[] COMPRESS_LOW_DIGIT_CHARS = {'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
            'R', 'S', 'T'};

    public static final byte[] COMPRESS_HIGH_DIGIT_CHARS = {'U', 'V', 'W', 'X',
            'Y'};

    public static final byte COMPRESS_UNKNOWN_CHAR = '?';

    public static final byte COMPRESS_REPEAT_CHAR = 'Z';

    public static final byte COMPRESS_UNKNOWN_CHAR_VALUE = -3;

    public static final byte COMPRESS_REPEAT_CHAR_VALUE = -2;

    public static final byte COMPRESS_ERROR_CHAR_VALUE = -1;

    public static final byte[] COMPRESS_VALID_CHARS = new byte[256];

    static {
        for (int i = 0; i < COMPRESS_VALID_CHARS.length; i++)
            COMPRESS_VALID_CHARS[i] = COMPRESS_ERROR_CHAR_VALUE;
        for (int i = 0; i < COMPRESS_LOW_DIGIT_CHARS.length; i++)
            COMPRESS_VALID_CHARS[COMPRESS_LOW_DIGIT_CHARS[i]] = (byte)i;
        for (int i = 0; i < COMPRESS_HIGH_DIGIT_CHARS.length; i++)
            COMPRESS_VALID_CHARS[COMPRESS_HIGH_DIGIT_CHARS[i]] = (byte)((i + 1)
                * COMPRESS_LOW_BASE);

        COMPRESS_VALID_CHARS[COMPRESS_UNKNOWN_CHAR] = COMPRESS_UNKNOWN_CHAR_VALUE;

        COMPRESS_VALID_CHARS[COMPRESS_REPEAT_CHAR] = COMPRESS_REPEAT_CHAR_VALUE;
    }

    public static final int COMPRESS_OTHER_STMT_INIT_LEN = 500;

    public static final int COMPRESS_REPEATED_SUBPROOF_INIT_LEN = 500;

    public static final int COMPRESS_STEP_INIT_LEN = 20000;

    // ====================================================

    /**
     * for WorkVarManager.java
     */

    public static final int NBR_WORK_VARS_FOR_TYPE_MIN = 10;

    public static final int NBR_WORK_VARS_FOR_TYPE_MAX = 999;

    public static final int STARTING_WORK_VAR_SEQ_NBR_FOR_MOBJ = Integer.MIN_VALUE;

    public static final String WORK_VAR_DEFAULT_PREFIX = "&";

    public static final int WORK_VAR_DEFAULT_NBR_FOR_TYP_CD = 200;

    // ====================================================

    /**
     * for ParseNode.java to commo to mmj.pa.StepUnifier... (see
     * ParseNode.checkWorkVarHasOccursIn())
     */

    public static final int WV_OCCURS_IN_RENAME_LOOP = -1;

    public static final int WV_OCCURS_IN_NOT_AT_ALL = 0;

    public static final int WV_OCCURS_IN_ERROR = 1;

    // ====================================================

    /**
     * for BookManager.java
     */

    /**
     * The BookManager is enabled by default even though it is possibly of
     * little use to the average mmj2 user.
     */
    public final static boolean BOOK_MANAGER_ENABLED_DEFAULT = true;

    /**
     * This sets the initial size of the ArrayList used to store Chapters in the
     * BookManager.
     * <p>
     * If the number of input Chapters exceeds this value the ArrayList is
     * automatically resized. As of August 2008 there were 33 Chapters in set.mm
     */
    public final static int ALLOC_NBR_BOOK_CHAPTERS_INITIAL = 50;

    /**
     * This sets the initial size of the ArrayList used to store Sections in the
     * BookManager.
     * <p>
     * If the number of input Sections exceeds this value the ArrayList is
     * automatically resized. As of August 2008 there were about 1250 Sections
     * in set.mm
     */
    public final static int ALLOC_NBR_BOOK_SECTIONS_INITIAL = 1500;

    public final static int SECTION_NBR_CATEGORIES = 4;

    public final static int SECTION_SYM_CD = 1;
    public final static int SECTION_VAR_HYP_CD = 2;
    public final static int SECTION_SYNTAX_CD = 3;
    public final static int SECTION_LOGIC_CD = 4;

    public final static String[] SECTION_DISPLAY_CAPTION = {" N/A    ",
            " Symbols ", " VarHyps ", " Syntax  ", " Logic   "};

    // ====================================================

    /**
     * for Cnst.java
     */

    public static final String CNST_SET_TYPE = "set";

    // ====================================================

    /**
     * for Chapter.java
     */

    public final static String CHAPTER_TOSTRING_LITERAL = "Chapter %d -- %s -- Sections %d thru %d";

    // ====================================================

    /**
     * for Section.java
     */

    public final static String SECTION_TOSTRING_LITERAL = "Chapter %d, Section %d %s -- %s -- Last MObj Nbr = %d";

    // ==================================================

    public static final ErrorCode ERRMSG_MUST_DEF_CNST_AT_GLOBAL_LVL = of(
        "E-LA-0001 Constants must be defined in outer/global"
            + " scope level.");

    public static final ErrorCode ERRMSG_CANNOT_END_GLOBAL_SCOPE = of(
        "E-LA-0002 Too many End Scope statements? Cannot end"
            + " global level!");

    public static final ErrorCode ERRMSG_MISSING_END_SCOPE_AT_EOF = of(
        "E-LA-0003 Missing End Scope? EOF reached following" + " Begin Scope!");

    // Note: "E-IO-0019 Statement has duplicate tokens.
    // Statment keyword = " in mmj.mmio.Statementizer
    // takes precedence here, but it is ok that
    // LogicalSystem doublechecks this.
    public static final ErrorCode ERRMSG_DJ_VARS_ARE_DUPS = of(
        "E-LA-0004 Disjoint Variable statement has duplicate"
            + " variables! Sym = %s");

    public static final ErrorCode ERRMSG_STMT_TYP_UNDEF = of(
        "E-LA-0005 Constant on statement not previously declared."
            + " Type = %s");

    public static final ErrorCode ERRMSG_STMT_TYP_NOT_DEF_AS_CNST = of(
        "E-LA-0006 Constant symbol already declared but not as a"
            + " constant. Type = %s");

    public static final ErrorCode ERRMSG_EXPR_SYM_NOT_DEF = of(
        "E-LA-0007 Undefined symbol used in statement expression."
            + " Symbol = %s");

    public static final ErrorCode ERRMSG_EXPR_SYM_NOT_ACTIVE = of(
        "E-LA-0008 Inactive (in scope) symbol used in statement"
            + " expression. Symbol = %s");

    public static final ErrorCode ERRMSG_EXPR_VAR_W_O_ACTIVE_VAR_HYP = of(
        "E-LA-0009 Variable in expression has no active VarHyp."
            + " Symbol = %s");

    public static final ErrorCode ERRMSG_DUP_STMT_LABEL = of(
        "E-LA-0010 Label on statement already used -- a duplicate."
            + " Label = %s");

    public static final ErrorCode ERRMSG_FORMULA_VAR_HYP_NOTFND = of(
        "A-LA-0011 Assertion expression variable hypothesis not"
            + " found for variable = %s. Formula = %s");

    public static final ErrorCode ERRMSG_DUP_VAR_OR_CNST_SYM = of(
        "E-LA-0012 Declared symbol is duplicate of other variable"
            + " or constant, sym = %s");

    public static final String MISSING_PROOF_STEP = "?";

    // Note: "E-IO-0025 Proof must have at least one step
    // (which may be a "?" symbol)." from
    // mmj.mmio.Statementizer.java appears to take
    // precedence over this, but it is ok that
    // LogicalSystem doublechecks this.
    public static final ErrorCode ERRMSG_PROOF_HAS_NO_STEPS = of(
        "E-LA-0013 Proof must have at least one step. A"
            + " single \"?\" will suffice.");

    public static final ErrorCode ERRMSG_PROOF_STEP_LABEL_NOTFND = of(
        "E-LA-0014 Proof step label not found in Statement table."
            + " Label = %s");

    // note: E-LA-0014 in mmj.lang.theorem takes precedence over
    // E-LA-0015 if the Theorem is being added in file
    // order. So this double-check may be useful in the
    // future but doesn't have any effect.
    public static final ErrorCode ERRMSG_FORWARD_PROOF_STEP_LABEL = of(
        "E-LA-0015 Proof step sequence in database >= this"
            + " statement! Label = %s. Theorem = %s");

    public static final ErrorCode ERRMSG_PROOF_STEP_HYP_INACTIVE = of(
        "E-LA-0016 Proof step refers to inactive (out of scope)"
            + " hypothesis. Symbol = %s");

    public static final ErrorCode ERRMSG_VAR_IS_DUP_OF_CNST_SYM = of(
        "E-LA-0017 Variable symbol = %s duplicates a Constant ($c).");

    public static final ErrorCode ERRMSG_VAR_IS_ALREADY_ACTIVE = of(
        "E-LA-0018 Variable symbol = %s is already active in scope.");

    public static final ErrorCode ERRMSG_STMT_VAR_UNDEF = of(
        "E-LA-0019 Variable = %s in statement not previously declared.");

    public static final ErrorCode ERRMSG_STMT_VAR_NOT_DEF_AS_VAR = of(
        "E-LA-0020 Variable = %s symbol already declared, but not as"
            + " a variable.");

    public static final ErrorCode ERRMSG_STMT_VAR_NOT_ACTIVE = of(
        "E-LA-0021 Variable = %s in statement not active in scope.");

    public static final ErrorCode ERRMSG_MULT_ACTIVE_HYP_FOR_VAR = of(
        "E-LA-0022 Variable Hyp. already active for var in new"
            + " VarHyp, Label = %s");

    public static final ErrorCode ERRMSG_PARSED_RPN_INCOMPLETE = of(
        "E-LA-0023 Input RPN for ParseTree, either an Expression"
            + " RPN or a proof, contains a missing/null/? step at"
            + " step number %d");

    public static final ErrorCode ERRMSG_PARSED_RPN_EMPTY_STACK = of(
        "E-LA-0024 Input RPN for ParseTree is invalid:"
            + " incorrect number of variable expressions for a"
            + " statement, resulting in a premature Empty Stack"
            + " condition at step number %d");

    public static final ErrorCode ERRMSG_PARSED_RPN_NOT_EMPTY_AT_END = of(
        "E-LA-0025 Input RPN for Parse Tree is invalid:"
            + " mismatch of variable expressions and statements"
            + " resulting in a non-empty stack (leftovers) at end."
            + " Number of leftovers = %d");

    public static final ErrorCode ERRMSG_MAX_ERROR_MSG_LT_1 = of(
        "E-LA-0026 Max error message param less than 1.");

    public static final ErrorCode ERRMSG_MAX_INFO_MSG_LT_1 = of(
        "E-LA-0027 Max error message param less than 1.");

    public static final ErrorCode ERRMSG_SYM_TBL_TOO_SMALL = of(
        "E-LA-0028 SymbolTableInitialSize must be at least %d");

    public static final ErrorCode ERRMSG_STMT_TBL_TOO_SMALL = of(
        "E-LA-0029 StatementTableInitialSize must be at least %d");

    public static final ErrorCode ERRMSG_TREE_CONV_TO_RPN_FAILURE = of(
        "A-LA-0030 ParseTree conversion to RPN failed."
            + " Not enough Stmts loaded to RPN. Count is off by %d");

    public static final ErrorCode ERRMSG_RPN_CONV_TO_TREE_FAILURE = of(
        "E-LA-0031 RPN conversion to ParseTree failed."
            + " Leftover RPN Stmts after completing the ParseTree!");

    public static final ErrorCode ERRMSG_RPN_INVALID_NOT_ENOUGH_STMTS = of(
        "E-LA-0032 RPN invalid. Ran out of RPN Stmts before"
            + " completing the ParseTree!");

    public static final ErrorCode ERRMSG_STMT_LABEL_STRING_EMPTY = of(
        "A-LA-0033 Statement Label string is empty.");

    public static final String DJVARS_LEFT_BRACKET = "<";
    public static final String DJVARS_RIGHT_BRACKET = ">";
    public static final String DJVARS_SEPARATOR = ",";

    public static final ErrorCode ERRMSG_SYM_ID_STRING_EMPTY = of(
        "A-LA-0034 Sym Id string is empty.");

    public static final ErrorCode ERRMSG_TYP_CONV_DUP = of(
        "A-LA-0035 Oops! Programmer Error. Attempt made to"
            + " add a duplicate TypeConversionRule to"
            + " Cnst.convFRomTypGRArray. Label = %s, Type Code (Cnst) = %s");

    public static final ErrorCode ERRMSG_ASSRT_SUBST_HYP_NOTFND = of(
        "A-LA-0036 Oops! Programmer Error. Unable to find"
            + " matching Hyp in Assrt HypArray while performing"
            + " deepCloneApplyingAssrtSubst() routine. Hyp = %s");

    public static final ErrorCode ERRMSG_UNIFY_SUBST_HYP_NOTFND = of(
        "A-LA-0037 Oops! Programmer Error. Unify substitution"
            + " VarHyp %s not found in input VarHypArray = %s");

    public static final ErrorCode ERRMSG_SYM_ID_DUP_OF_STMT_LABEL = of(
        "E-LA-0038 Symbol duplicates a statement label. This"
            + " is prohibited according to the Metamath.pdf spec"
            + " change of 24-June-2006." + " Sym = %s");

    public static final ErrorCode ERRMSG_STMT_LABEL_DUP_OF_SYM_ID = of(
        "E-LA-0039 Statement label duplicates a symbol id. This"
            + " is prohibited according to the Metamath.pdf spec"
            + " change of 24-June-2006." + " Stmt label = %s");

    public static final ErrorCode ERRMSG_SUBTREE_CONV_TO_RPN_FAILURE = of(
        "A-LA-0040 ParseNode subtree conversion to RPN failed."
            + " Not enough Stmts loaded to RPN. Count is off by %d");

    public static final ErrorCode ERRMSG_DUP_SYM_MAP_PUT_ATTEMPT = of(
        "A-LA-0041 Duplicate Sym MObj map.put() (add) attempted by "
            + " LogicalSystem. The duplicate id = %s");

    public static final ErrorCode ERRMSG_DUP_STMT_MAP_PUT_ATTEMPT = of(
        "A-LA-0042 Duplicate Stmt MObj map.put() (add) attempted by "
            + " LogicalSystem. The duplicate label = %s");

    public static final ErrorCode ERRMSG_THEOREM_LOADER_COMMIT_FAILED = of(
        "A-LA-0044 theoremLoaderCommit() failed."
            + " This is an unrecoverable error, probably a bug!"
            + " Manual restart of mmj2 required."
            + " Explanation message identifying failure follows:\n%s");

    // =======================================================

    /**
     * Messages for ProofCompression.java
     */

    public static final ErrorCode ERRMSG_COMPRESS_OTHER_NOTFND = of(
        "E-LA-0101 Theorem %s:"
            + " compressed proof contains invalid statement label,"
            + " not found in Statement Table. Label position within"
            + " the compressed proof's parentheses = %d. Statement label = %s");

    public static final ErrorCode ERRMSG_COMPRESS_OTHER_BOGUS = of(
        "E-LA-0102 Theorem %s:"
            + " compressed proof contains invalid statement label"
            + " in parenthesized portion of proof. The referenced"
            + " statement is neither a VarHyp nor an Assrt type"
            + " statement! Label position within"
            + " the compressed proof's parentheses = %d. Statement label = %s");

    public static final ErrorCode ERRMSG_COMPRESS_OTHER_VARHYP_POS = of(
        "E-LA-0103 Theorem %s:"
            + " compressed proof contains invalid statement label"
            + " in parenthesized portion of proof. The referenced"
            + " statement is a VarHyp that occurs *after* one or"
            + " more Assrt labels within the parentheses."
            + " Label position within"
            + " the compressed proof's parentheses = %d. Statement label = %s");

    public static final ErrorCode ERRMSG_COMPRESS_NO_PROOF_BLOCKS = of(
        "E-LA-0104 Theorem %s:"
            + " compressed proof contains no compressed proof blocks"
            + " following the parentheses! Proof is empty?!");

    public static final ErrorCode ERRMSG_COMPRESS_PREMATURE_END = of(
        "E-LA-0105 Theorem %s:"
            + " Premature end of proof. Final compressed proof block"
            + " reached prior to end of compressed step number");

    public static final ErrorCode ERRMSG_COMPRESS_NOT_ASCII = of(
        "E-LA-0106 Theorem %s:"
            + " Compressed proof character position %d contains a character code > 255 = %c");

    public static final ErrorCode ERRMSG_COMPRESS_BAD_CHAR = of(
        "E-LA-0107 Theorem %s:"
            + " Compressed proof character position %d contains an invalid character '%c'. Compressed proof"
            + " block may contain only 'A', 'B'...'Z' and '?'.");

    public static final ErrorCode ERRMSG_COMPRESS_BAD_UNK = of(
        "E-LA-0108 Theorem %s:"
            + " Compressed proof character position %d contains a '?' inside a compressed proof number"
            + " (for example: 'U?' or 'U?U').");

    public static final ErrorCode ERRMSG_COMPRESS_BAD_RPT = of(
        "E-LA-0109 Theorem %s:"
            + " Compressed proof character position %d contains a 'Z' (Repeated Subproof symbol)"
            + " inside a compressed proof number"
            + " (for example: 'UZ' or 'UZU').");

    public static final ErrorCode ERRMSG_COMPRESS_BAD_RPT2 = of(
        "E-LA-0110 Theorem %s:"
            + " Compressed proof character position %d contains a 'Z' (Repeated Subproof symbol)"
            + " at an invalid location, such as after another 'Z'"
            + " or following a '?'");

    public static final ErrorCode ERRMSG_COMPRESS_BAD_RPT3 = of(
        "E-LA-0111 Theorem %s:"
            + " Compressed proof character position %d is invalid: compressed number that points beyond"
            + " the end of the Repeated Subproof array.");

    public static final ErrorCode ERRMSG_COMPRESS_CORRUPT = of(
        "E-LA-0112 Theorem %s:"
            + " Compressed proof character position %d is invalid: It appears that the compressed proof is"
            + " corrupted -- subproof length points outside the"
            + " range of proof steps! The problem may be the result"
            + " of a handcoded compressed proof, or a bug in the"
            + " proof compression logic that occurred at some"
            + " earlier time. At any rate, this proof is bogus"
            + " and cannot be processed further!");

    public static final ErrorCode ERRMSG_COMPRESS_OTHER_MAND = of(
        "E-LA-0113 Theorem %s:"
            + " compressed proof contains required hypothesis within the parentheses."
            + " Label position within the compressed proof's parentheses = %d."
            + " Statement label = %s");

    // =======================================================

    /**
     * Messages for ProofCompression.java
     */

    public static final ErrorCode ERRMSG_BAD_PARSE_STMT = of(
        "A-LA-0201 Stmt Label %s:"
            + " Invalid ParseNode passed for Sub-Expression"
            + " output. Node must be either a VarHyp node"
            + " or a SyntaxAxiom Assrt node -- i.e. a"
            + " parse ParseNode, not a proof ParseNode");

    // =======================================================

    /**
     * Messages for Messages.java
     */

    public static final ErrorCode ERRMSG_TIMER_ID_NOTFND = of(
        "E-LA-0301 TimerID %s:"
            + " not found in Instrumentation Timer Table. The "
            + " Timer ID's on the startInstrumentationTimer and"
            + " stopInstrumentationTimer RunParms/calls must match.");

    public static final ErrorCode ERRMSG_TIMER_ID = of(
        "I-LA-0302 TimerID %s: Elapsed Millis=%d Total Memory=%d"
            + " (delta=%d) Max Memory=%d (delta=%d) FreeMemory=%d (delta=%d)");

    // =======================================================

    /**
     * Messages for WorkVarManager.java
     */

    public static final ErrorCode ERRMSG_DEFINE_WORK_VAR_TYPE_BAD = of(
        "A-LA-0401 DefineWorkVarType error on RunParm or"
            + " call parameter. Value of Type Code input = %s. Must be a valid Cnst Type Code used on a $f"
            + " (VarHyp) statement in the input Metamath file.");

    public static final ErrorCode ERRMSG_DEFINE_WORK_VAR_PREFIX_BAD = of(
        "A-LA-0402 DefineWorkVarType error on RunParm or"
            + " call parameter. Value of Work Variable Prefix input = %s. Must be a string containing only characters defined"
            + " in Metamath.pdf as valid math symbols, with no"
            + " embedded blanks.");

    public static final ErrorCode ERRMSG_DEFINE_WORK_VAR_NBR_BAD = of(
        "A-LA-0403 DefineWorkVarType error on RunParm or"
            + " call parameter. Value of Work Variable Number input = %d. This number defines how many Work Variables will be"
            + " created for the Type Code. The number must not be"
            + " less than %d, nor greater than %d.");

    public static final ErrorCode ERRMSG_DEFINE_WORK_VAR_PFX_DUP = of(
        "A-LA-0404 DefineWorkVarType error on RunParm or"
            + " call parameter. Value of Work Variable Prefix = %s"
            + " is the same on two different Work Var Types: %s and %s");

    public static final ErrorCode ERRMSG_DEFINE_WORK_VAR_DUP = of(
        "A-LA-0405 DefineWorkVarType error on RunParm or"
            + " call parameter. Work Variable duplicates a"
            + " regular variable or statement label! Work Variable = %s");

    public static final ErrorCode ERRMSG_BOGUS_WORK_VAR_IN_ALLOC = of(
        "A-LA-0406 Null or empty string WorkVar token input."
            + " This indicates a programming error (bug)!");

    public static final ErrorCode ERRMSG_TOO_FEW_WORK_VAR_FOR_TYP = of(
        "A-LA-0407 Ooops, not enough Work Variables available!"
            + " For Type Code %s a maximum of %d Work Variables can be in use at one time according"
            + " to the default and RunParm defined settings."
            + " Assuming that there is not a bug, a new setting"
            + " must be input via RunParm 'DefineWorkVarType', and"
            + " mmj2 will need to be restarted. Sorry..."
            + " For more information, see:"
            + " ..\\mmj2\\mmj2jar\\AnnotatedRunParms.txt.");

    public static final ErrorCode ERRMSG_VAR_IN_WORK_VAR_HYP = of(
        "A-LA-0408" + " Attempting to store a Var in a WorkVarHyp."
            + " This indicates a programming error (bug)!");

    // =======================================================

    /**
     * Messages for ParseNode.java
     */

    public static final ErrorCode ERRMSG_NULL_TARGET_VAR_HYP_PA_SUBST = of(
        "A-LA-0501" + " Severe bug encountered! cloneTargetToSourceVars"
            + " tried to clone a target VarHyp with a"
            + " null 'paSubst' value -- the target Var Hyps"
            + " should already have been assigned to Work Var Hyps!");

    // =======================================================

    /**
     * Messages for SeqAssigner.java
     */

    public static final ErrorCode ERRMSG_INTERVAL_SIZE_RANGE_ERR = of(
        "A-LA-0601"
            + " SeqAssigner Interval Size is in error. Input = %d. Must be >= %d and <= %d.");

    public static final ErrorCode ERRMSG_INTERVAL_TBL_SIZE_RANGE_ERR = of(
        "A-LA-0602"
            + " SeqAssigner Interval Table Initial Size is in error. Input = %d."
            + " Must be >= %d and <= %d.");

    public static final ErrorCode ERRMSG_SEQ_ASSIGNER_OUT_OF_NUMBERS = of(
        "A-LA-0603" + " SeqAssigner has exhausted the available numbers"
            + " available for use. The theoretical maximum is"
            + " limited by mmj2's use of a Java 'int' number, whose"
            + " limit is 2**31 - 1. We are at or near that number"
            + " now. Either modify mmj2 to use 'long' instead of 'int'"
            + " seq numbers, or use RunParm SeqAssignerIntervalSize"
            + " with a smaller interval. The next sequence number is %d."
            + " The total count so far, including the next, of MObj's is %d.");

    public static final ErrorCode ERRMSG_SEQ_ASSIGNER_ROLLBACK_STATE = of(
        "A-LA-0604" + " SeqAssigner.rollback() invoked but checkpointing"
            + " was not 'on'. This indicates a severe programming error!");

    public static final ErrorCode ERRMSG_SEQ_ASSIGNER_COMMIT_STATE = of(
        "A-LA-0605" + " SeqAssigner.commit() invoked but checkpointing"
            + " was not 'on'. This indicates a severe programming error!");

    public static final ErrorCode ERRMSG_SEQ_ASSIGNER_ROLLBACK_AUDIT = of(
        "I-LA-0606"
            + " SeqAssigner.rollback() unassigning MObj.seq nbr %d for %s %s (%s)");
    public static final String ERRMSG_THEOREM_CAPTION = "Theorem";
    public static final String ERRMSG_LOGHYP_CAPTION = "LogHyp";
    public static final String ERRMSG_INSERTED_CAPTION = "Inserted";
    public static final String ERRMSG_APPENDED_CAPTION = "Appended";

    public static final ErrorCode ERRMSG_SEQ_ASSIGNER_CHECKPOINT_STATE = of(
        "A-LA-0607" + " turnOnCheckpointing() invoked but checkpointing"
            + " was already 'on'. This indicates a severe programming error!");

    // =======================================================

    /**
     * Messages for Theorem.java
     */

    public static final ErrorCode ERRMSG_DJ_VARS_VARS_NOT_DEF_IN_EXT_FRAME = of(
        "A-LA-0701" + " One or both of the variables in a Distinct Variable"
            + " statement is not present in the theorem's extended frame"
            + " (scope) of Variable Hypotheses. Dj Vars = %s");

    // =======================================================

    /**
     * Messages for BookManager.java
     */

    public static final ErrorCode ERRMSG_BM_UPDATE_W_MMT_SECTION_NOTFND = of(
        "E-LA-0801",
        "Bit of a problem here, skipper! Oy!!!"
            + " We was asked to assign a Section Number = %d to a Theorem, label = %s,"
            + " but the given Section is not in the BookManager!");

    // =======================================================

    /**
     * Messages for ScopeFrame.java
     */

    public static final ErrorCode ERRMSG_DUP_DJ_VARS_AFTER_CONSOLIDATION_ERR = of(
        "A-LA-0901",
        "Severe program bug found. Duplicate disjoint"
            + " variables found even though they were previously"
            + " consolidated to eliminate duplicates!"
            + " Original error message follows: %s");
}
