//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/


/**
 *  LangConstants.java  0.10 11/01/2011
 *
 *  Aug-30-2005: misc. message typos fixed.
 *
 *  Sep-25-2005:
 *      -->misc. message typos fixed.
 *      -->noted, a few of the "E-LA-" messages are
 *         overridden by "E-IO-" messages because
 *         mmj.lang.LogicalSystem.java is double-checking.
 *      -->Error message id's changed from "E-" TO "A-" because
 *         their severity halts the run immediately (...abort):
 *             "E-LA-0030 ParseTree conversion to RPN failed."
 *             "E-LA-0033 Statement Label string is empty.";
 *             "E-LA-0034 Sym Id string is empty.";
 *
 *  Version 0.03:
 *      --> Added new messages for ParseNode.java
 *              ERRMSG_ASSRT_SUBST_HYP_NOTFND_1
 *              ERRMSG_UNIFY_SUBST_HYP_NOTFND_1
 *      --> Added stuff for ProofCompression.java to use :)
 *
 *  Version 0.04:
 *  Aug-27-2006: Added error message
 *               A-LA-0201 ERRMSG_BAD_PARSE_STMT_1
 *               for TMFF project.
 *
 *  Sep-02-2006: comment update for TMFF project messages.
 *  Oct-12-2006: New error messages supporting
 *               Metamath.pdf spec change of 6-24-2006
 *               prohibiting Stmt label and Sym id namespace
 *               collisions.
 *  Oct-15-2006: Set max error messages and max info messages
 *               to 15,000 to match the standard RunParms.txt file.
 *
 *  Version 0.05 -- 06/01/2007
 *      --> Added messages for use in stopInstrumentationTimer()
 *
 *  Version 0.06 -- 08/01/2007
 *      --> Misc. Work Var Enhancements.
 *
 *  Version 0.07 -- 02/01/2008
 *      --> Modified E-LA-0028 and E-LA-0029 messages to
 *          spell out "SymbolTable" and "StatementTable".
 *      --> Add ERRMSG_SUBTREE_CONV_TO_RPN_FAILURE for
 *          use by ParseNode.converToRPN()
 *      --> Increase SYM_TBL_INITIAL_SIZE_DEFAULT and
 *                  STMT_TBL_INITIAL_SIZE_DEFAULT.
 *
 *  Version 0.08 -- 08/01/2008
 *      --> Add constants for BookManager.java
 *      --> Add constants for SeqAssigner.java
 *      --> Add stuff for Theorem Loader.
 *      --> Add ERRMSG_DUP_SYM_MAP_PUT_ATTEMPT (A-LA-0041)
 *          and ERRMSG_DUP_STMT_MAP_PUT_ATTEMPT (A-LA-0042)
 *          for duplicate checking by LogicalSystem.
 *      --> Add ERRMSG_DUP_DJ_VARS_AFTER_CONSOLIDATION_ERR_1
 *          and ERRMSG_DUP_DJ_VARS_AFTER_CONSOLIDATION_ERR_2
 *
 *  Version 0.09 -- 07/01/2011
 *      --> Update SYM_TBL_INITIAL_SIZE_DEFAULT to 1500 and
 *                 STMT_TBL_INITIAL_SIZE_DEFAULT to 45000.
 *
 *  Version 0.10 - Nov-01-2011:  comment update.
 *      --> Add stmt label to ERRMSG_BAD_PARSE_STMT_1
 */

package mmj.lang;


/**
 *  Constants used in mmj.lang package.
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
public class LangConstants {

    /**
     *  for LogicalSystem.java
     */

    /**
     *  Default initial size of Symbol Table, large
     *  enough to handle set.mm times 1.5.
     *
     *  The system will grow the table, as needed.
     */
    public static final int      SYM_TBL_INITIAL_SIZE_DEFAULT
                                  = 1500;

    /**
     *  Minimum size of Symbol Table.
     *
     *  An arbitrary small number greater than zero :)
     */
    public static final int      SYM_TBL_INITIAL_SIZE_MINIMUM
                                  = 10;


    /**
     *  Default initial size of Statement Table, large
     *  enough to handle set.mm times 1.5.
     *
     *  An arbitrary small number greater than zero :)
     */
    public static final int      STMT_TBL_INITIAL_SIZE_DEFAULT
                                  = 45000;


    /**
     *  Minimum size of Statement Table.
     *
     *  An arbitrary small number greater than zero :)
     */
    public static final int      STMT_TBL_INITIAL_SIZE_MINIMUM
                                  = 100;

//  DEPRECATED/DELETED AS OF 08/01/2008 RELEASE
//  SEE LangConstants.SEQ_ASSIGNER_INTERVAL_SIZE_DEFAULT
//  /**
//   *  First .mm MObj (Sym/Stmt/etc) .seq = 10, second, 20, etc.
//   *
//   *  MObj.seq numbers are assigned sequentially in
//   *  input/database sequence, but a gapping interval
//   *  is applied via multiples of this number.
//   */
//  public static final int      MOBJ_SEQ_NBR_INCREMENT
//                                = 10;

    /**
     *  Default ProofVerifier is null, meaning no proof
     *  verification is done automatically.
     *
     *  The ProofVerifier can be added after the .mm file
     *  is loaded.
     */
    public static final ProofVerifier PROOF_VERIFIER_DEFAULT
                                  = null;

    /*
     *  Default SyntaxVerifier is null, meaning no grammar
     *  or syntax parsing/verification is done automatically.
     *
     *  The SyntaxVerifier can be added after the .mm file
     *  is loaded.
     */
    public static final SyntaxVerifier SYNTAX_VERIFIER_DEFAULT
                                  = null;

    /**
     *  Default maximum number of error messages in
     *  mmj.lang.Messages.java, the mmj message repository
     *  during processing.
     *
     *  The number of error messages is important, and
     *  is used to halt processing when the maximum is
     *  reached. Also, Proof Verification and/or Syntax
     *  Verification will not take place unless the
     *  .mm file was loaded with zero errors.
     *
     *  Set this number to 9999 if desired, it is not
     *  a problem unless you are short on memory.
     *
     */
    public static final int      MAX_ERROR_MESSAGES_DEFAULT
                                  = 15000;

    /**
     *  Default maximum number of "info" messages in
     *  mmj.lang.Messages.java, the mmj message repository
     *  during processing.
     *
     *  There are not many info messages in mmj at this
     *  time. Basically they fall into the category of
     *  "warnings", but in the future might include
     *  audit or statistical data ("instrumentation").
     */
    public static final int      MAX_INFO_MESSAGES_DEFAULT
                                  = 15000;

    // ====================================================

    /**
     *  for SeqAssigner.java
     */

    /**
     *  Sequence Number minimum interval size.
     *  <p>
     *  SEQ_ASSIGNER_MIN_INTERVAL_SIZE = 1.
     */
    public static final int SEQ_ASSIGNER_MIN_INTERVAL_SIZE = 1;

    /**
     *  Sequence Number maximum interval size.
     *  <p>
     *  SEQ_ASSIGNER_MAX_INTERVAL_SIZE = 10000.
     */
    public static final int SEQ_ASSIGNER_MAX_INTERVAL_SIZE = 10000;

    /**
     *  Sequence Number default interval size.
     *  <p>
     *  SEQ_ASSIGNER_INTERVAL_SIZE_DEFAULT = 1000.
     */
    public static final int
                     SEQ_ASSIGNER_INTERVAL_SIZE_DEFAULT    = 1000;


    /**
     *  Sequence Number Interval Table HashMap minimum initial size.
     *  <p>
     *  SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE_MIN = 10.
     */
    public static final int
                     SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE_MIN
                                                           = 10;

    /**
     *  Sequence Number Interval Table HashMap maximum initial size.
     *  <p>
     *  SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE_MAX = 10000.
     */
    public static final int
                     SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE_MAX
                                                           = 10000;

    /**
     *  Sequence Number Interval Table HashMap initial size.
     *  <p>
     *  SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE_DEFAULT = 100.
     */
    public static final int
                     SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE_DEFAULT
                                                           = 100;

    // ====================================================

    /**
     *  for ProofCompression.java
     */

    public static final int      COMPRESS_LOW_BASE
                                  = 20;

    public static final int      COMPRESS_HIGH_BASE
                                  = 5;

    public static final byte[] COMPRESS_LOW_DIGIT_CHARS
                                  =
        { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
          'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T'
        };

    public static final byte[] COMPRESS_HIGH_DIGIT_CHARS
                                  =
        { 'U', 'V', 'W', 'X', 'Y'
        };

    public static final byte COMPRESS_UNKNOWN_CHAR
                                  = '?';

    public static final byte COMPRESS_REPEAT_CHAR
                                  = 'Z';

    public static final byte COMPRESS_UNKNOWN_CHAR_VALUE
                                  = -3;

    public static final byte COMPRESS_REPEAT_CHAR_VALUE
                                  = -2;

    public static final byte COMPRESS_ERROR_CHAR_VALUE
                                  = -1;


    public static final byte[] COMPRESS_VALID_CHARS
                                  = new byte[256];

    static {
        for (int i = 0; i < COMPRESS_VALID_CHARS.length; i++) {
            COMPRESS_VALID_CHARS[i]
                                  = COMPRESS_ERROR_CHAR_VALUE;
        }
        for (int i = 0; i < COMPRESS_LOW_DIGIT_CHARS.length; i++) {
            COMPRESS_VALID_CHARS[ COMPRESS_LOW_DIGIT_CHARS[i] ]
                                  = (byte)i;
        }
        for (int i = 0; i < COMPRESS_HIGH_DIGIT_CHARS.length; i++) {
            COMPRESS_VALID_CHARS[ COMPRESS_HIGH_DIGIT_CHARS[i] ]
                                  = (byte)((i + 1)
                                            *
                                            COMPRESS_LOW_BASE);
        }

        COMPRESS_VALID_CHARS[COMPRESS_UNKNOWN_CHAR]
                                  = COMPRESS_UNKNOWN_CHAR_VALUE;

        COMPRESS_VALID_CHARS[COMPRESS_REPEAT_CHAR]
                                  = COMPRESS_REPEAT_CHAR_VALUE;
    }

    public static final int      COMPRESS_OTHER_HYP_INIT_LEN
                                  = 50;

    public static final int      COMPRESS_OTHER_ASSRT_INIT_LEN
                                  = 500;

    public static final int      COMPRESS_REPEATED_SUBPROOF_INIT_LEN
                                  = 500;

    public static final int      COMPRESS_STEP_INIT_LEN
                                  = 20000;

    // ====================================================

    /**
     *  for WorkVarManager.java
     */

    public static final int      NBR_WORK_VARS_FOR_TYPE_MIN
                                  = 10;

    public static final int      NBR_WORK_VARS_FOR_TYPE_MAX
                                  = 999;

    public static final int      STARTING_WORK_VAR_SEQ_NBR_FOR_MOBJ
                                  = Integer.MIN_VALUE;

    public static final String   WORK_VAR_DEFAULT_PREFIX_PREFIX
                                  = "&";

    public static final int      WORK_VAR_DEFAULT_NBR_FOR_TYP_CD
                                  = 200;

    // ====================================================

    /**
     *  for ParseNode.java to commo to mmj.pa.StepUnifier...
     *  (see ParseNode.checkWorkVarHasOccursIn())
     */

    public static final int      WV_OCCURS_IN_RENAME_LOOP
                                  = -1;

    public static final int      WV_OCCURS_IN_NOT_AT_ALL
                                  = 0;

    public static final int      WV_OCCURS_IN_ERROR
                                  = 1;

    // ====================================================

    /**
     *  for BookManager.java
     */

    /**
     *  The BookManager is enabled by default even though
     *  it is possibly of little use to the average mmj2 user.
     */
    public final static boolean BOOK_MANAGER_ENABLED_DEFAULT
                                  = true;

    /**
     *  This sets the initial size of the ArrayList used to
     *  store Chapters in the BookManager.
     *  <p>
     *  If the number of input Chapters exceeds this value
     *  the ArrayList is automatically resized. As of August 2008
     *  there were 33 Chapters in set.mm
     */
    public final static int ALLOC_NBR_BOOK_CHAPTERS_INITIAL
                                  = 50;

    /**
     *  This sets the initial size of the ArrayList used to
     *  store Sections in the BookManager.
     *  <p>
     *  If the number of input Sections exceeds this value
     *  the ArrayList is automatically resized. As of August 2008
     *  there were about 1250 Sections in set.mm
     */
    public final static int ALLOC_NBR_BOOK_SECTIONS_INITIAL
                                  = 1500;

    public final static int SECTION_NBR_CATEGORIES
                                  = 4;

    public final static int SECTION_SYM_CD
                                  = 1;
    public final static int SECTION_VAR_HYP_CD
                                  = 2;
    public final static int SECTION_SYNTAX_CD
                                  = 3;
    public final static int SECTION_LOGIC_CD
                                  = 4;

    public final static String[]  SECTION_DISPLAY_CAPTION
                                  = { " N/A    ",
                                      " Symbols ",
                                      " VarHyps ",
                                      " Syntax  ",
                                      " Logic   "};

    // ====================================================

    /**
     *  for Chapter.java
     */

    public final static String CHAPTER_TOSTRING_LITERAL_1
                                  = "Chapter ";

    public final static String CHAPTER_TOSTRING_LITERAL_2
                                  = " -- ";

    public final static String CHAPTER_TOSTRING_LITERAL_3
                                  = " -- ";

    public final static String CHAPTER_TOSTRING_LITERAL_4
                                  = " Sections ";

    public final static String CHAPTER_TOSTRING_LITERAL_5
                                  = " thru ";

    // ====================================================

    /**
     *  for Section.java
     */

    public final static String SECTION_TOSTRING_LITERAL_1
                                  = "Chapter ";

    public final static String SECTION_TOSTRING_LITERAL_2
                                  = ", Section ";

    public final static String SECTION_TOSTRING_LITERAL_3
                                  = " -- ";

    public final static String SECTION_TOSTRING_LITERAL_4
                                  = " -- ";

    public final static String SECTION_TOSTRING_LITERAL_5
                                  = " Last MObj Nbr = ";

    // ==================================================


    public static final String ERRMSG_MUST_DEF_CNST_AT_GLOBAL_LVL =
        "E-LA-0001 Constants must be defined in outer/global"
        + " scope level.";

    public static final String ERRMSG_CANNOT_END_GLOBAL_SCOPE =
        "E-LA-0002 Too many End Scope statements? Cannot end"
        + " global level!";

    public static final String ERRMSG_MISSING_END_SCOPE_AT_EOF =
        "E-LA-0003 Missing End Scope? EOF reached following"
        + " Begin Scope!";

    // Note: "E-IO-0019 Statement has duplicate tokens.
    //       Statment keyword = " in mmj.mmio.Statementizer
    //       takes precedence here, but it is ok that
    //       LogicalSystem doublechecks this.
    public static final String ERRMSG_DJ_VARS_ARE_DUPS =
        "E-LA-0004 Disjoint Variable statement has duplicate"
        + " variables! Sym = ";


    public static final String ERRMSG_STMT_TYP_UNDEF =
        "E-LA-0005 Constant on statement not previously declared."
        + " Type = ";

    public static final String ERRMSG_STMT_TYP_NOT_DEF_AS_CNST =
        "E-LA-0006 Constant symbol already declared but not as a"
        + " constant. Type = ";


    public static final String ERRMSG_EXPR_SYM_NOT_DEF =
        "E-LA-0007 Undefined symbol used in statement expression."
        + " Symbol = ";

    public static final String ERRMSG_EXPR_SYM_NOT_ACTIVE =
        "E-LA-0008 Inactive (in scope) symbol used in statement"
        + " expression. Symbol = ";

    public static final String ERRMSG_EXPR_VAR_W_O_ACTIVE_VAR_HYP =
        "E-LA-0009 Variable in expression has no active VarHyp."
        + " Symbol = ";


    public static final String ERRMSG_DUP_STMT_LABEL =
        "E-LA-0010 Label on statement already used -- a duplicate."
        + " Label = ";

    public static final String ERRMSG_FORMULA_VAR_HYP_NOTFND_1 =
        "A-LA-0011 Assertion expression variable hypothesis not"
        + " found for variable = ";
    public static final String ERRMSG_FORMULA_CAPTION =
        " . Formula = ";


    public static final String ERRMSG_DUP_VAR_OR_CNST_SYM =
        "E-LA-0012 Declared symbol is duplicate of other variable"
        + " or constant, sym = ";


    public static final String MISSING_PROOF_STEP = "?";

    // Note: "E-IO-0025 Proof must have at least one step
    //        (which may be a "?" symbol)." from
    //       mmj.mmio.Statementizer.java appears to take
    //       precedence over this, but it is ok that
    //       LogicalSystem doublechecks this.
    public static final String ERRMSG_PROOF_HAS_NO_STEPS =
        "E-LA-0013 Proof must have at least one step. A"
        + " single \"?\" will suffice.";

    public static final String ERRMSG_PROOF_STEP_LABEL_NOTFND =
        "E-LA-0014 Proof step label not found in Statement table."
        + " Label = ";

    //note: E-LA-0014 in mmj.lang.theorem takes precedence over
    //      E-LA-0015 if the Theorem is being added in file
    //      order. So this double-check may be useful in the
    //      future but doesn't have any effect.
    public static final String ERRMSG_FORWARD_PROOF_STEP_LABEL =
        "E-LA-0015 Proof step sequence in database >= this"
        + " statement! Label = ";
    public static final String ERRMSG_FORWARD_PROOF_STEP_LABEL_2 =
        ". Theorem = ";

    public static final String ERRMSG_PROOF_STEP_HYP_INACTIVE =
        "E-LA-0016 Proof step refers to inactive (out of scope)"
        + " hypothesis. Symbol = ";


    public static final String ERRMSG_VAR_IS_DUP_OF_CNST_SYM =
        "E-LA-0017 Variable symbol duplicates a Constant ($c)"
        + "sym = ";

    public static final String ERRMSG_VAR_IS_ALREADY_ACTIVE =
        "E-LA-0018 Variable symbol is already active in scope."
        + " Sym = ";

    public static final String ERRMSG_STMT_VAR_UNDEF =
        "E-LA-0019 Variable in statement not previously declared."
        + " Var = ";

    public static final String ERRMSG_STMT_VAR_NOT_DEF_AS_VAR =
        "E-LA-0020 Variable symbol already declared, but not as"
        + " a variable. Var = ";

    public static final String ERRMSG_STMT_VAR_NOT_ACTIVE =
        "E-LA-0021 Variable in statement not active in scope."
        + " Var = ";


    public static final String ERRMSG_MULT_ACTIVE_HYP_FOR_VAR =
        "E-LA-0022 Variable Hyp. already active for var in new"
        + " VarHyp, Label = ";


    public static final String ERRMSG_PARSED_RPN_INCOMPLETE =
        "E-LA-0023 Input RPN for ParseTree, either an Expression"
        + " RPN or a proof, contains a missing/null/? step at"
        + " step number ";

    public static final String ERRMSG_PARSED_RPN_EMPTY_STACK =
        "E-LA-0024 Input RPN for ParseTree is invalid:"
        + " incorrect number of variable expressions for a"
        + " statement, resulting in a premature Empty Stack"
        + " condition at step number ";

    public static final String ERRMSG_PARSED_RPN_NOT_EMPTY_AT_END =
        "E-LA-0025 Input RPN for Parse Tree is invalid:"
        + " mismatch of variable expressions and statements"
        + " resulting in a non-empty stack (leftovers) at end."
        + " Number of leftovers = ";
    public static final String ERRMSG_PARSED_RPN_TOP_STACK_STMT =
        " Top stack entry statement = ";

    public static final String ERRMSG_MAX_ERROR_MSG_LT_1 =
        "E-LA-0026 Max error message param less than 1.";

    public static final String ERRMSG_MAX_INFO_MSG_LT_1 =
        "E-LA-0027 Max error message param less than 1.";

    public static final String ERRMSG_SYM_TBL_TOO_SMALL =
        "E-LA-0028 SymbolTableInitialSize must be at least ";

    public static final String ERRMSG_STMT_TBL_TOO_SMALL =
        "E-LA-0029 StatementTableInitialSize must be at least ";

    public static final String ERRMSG_TREE_CONV_TO_RPN_FAILURE =
        "A-LA-0030 ParseTree conversion to RPN failed."
        + " Not enough Stmts loaded to RPN. Count is off by ";

    public static final String ERRMSG_RPN_CONV_TO_TREE_FAILURE =
        "E-LA-0031 RPN conversion to ParseTree failed."
        + " Leftover RPN Stmts after completing the ParseTree!";

    public static final String ERRMSG_RPN_INVALID_NOT_ENOUGH_STMTS =
        "E-LA-0032 RPN invalid. Ran out of RPN Stmts before"
        + " completing the ParseTree!";

    public static final String ERRMSG_STMT_LABEL_STRING_EMPTY =
        "A-LA-0033 Statement Label string is empty.";

    public static final String DJVARS_LEFT_BRACKET  = "<";
    public static final String DJVARS_RIGHT_BRACKET = ">";
    public static final String DJVARS_SEPARATOR     = ",";

    public static final String ERRMSG_SYM_ID_STRING_EMPTY =
        "A-LA-0034 Sym Id string is empty.";

    public static final String ERRMSG_TYP_CONV_DUP_1 =
        "A-LA-0035 Oops! Programmer Error. Attempt made to"
        + " add a duplicate TypeConversionRule to"
        + " Cnst.convFRomTypGRArray. Label = ";
    public static final String ERRMSG_TYP_CONV_DUP_2 =
        ", Type Code (Cnst) = ";

    public static final String ERRMSG_ASSRT_SUBST_HYP_NOTFND_1 =
        "A-LA-0036 Oops! Programmer Error. Unable to find"
        + " matching Hyp in Assrt HypArray while performing"
        + " deepCloneApplyingAssrtSubst() routine. Hyp = ";

    public static final String ERRMSG_UNIFY_SUBST_HYP_NOTFND_1 =
        "A-LA-0037 Oops! Programmer Error. Unify substitution"
        + " VarHyp ";
    public static final String ERRMSG_UNIFY_SUBST_HYP_NOTFND_2 =
        " not found in input VarHypArray = ";

    public static final String ERRMSG_SYM_ID_DUP_OF_STMT_LABEL_1 =
        "E-LA-0038 Symbol duplicates a statement label. This"
        + " is prohibited according to the Metamath.pdf spec"
        + " change of 24-June-2006."
        + " Sym = ";

    public static final String ERRMSG_STMT_LABEL_DUP_OF_SYM_ID_1 =
        "E-LA-0039 Statement label duplicates a symbol id. This"
        + " is prohibited according to the Metamath.pdf spec"
        + " change of 24-June-2006."
        + " Stmt label = ";

    public static final String ERRMSG_SUBTREE_CONV_TO_RPN_FAILURE =
        "A-LA-0040 ParseNode subtree conversion to RPN failed."
        + " Not enough Stmts loaded to RPN. Count is off by ";

    public static final String ERRMSG_DUP_SYM_MAP_PUT_ATTEMPT =
        "A-LA-0041 Duplicate Sym MObj map.put() (add) attempted by "
        + " LogicalSystem. The duplicate id = ";

    public static final String ERRMSG_DUP_STMT_MAP_PUT_ATTEMPT =
        "A-LA-0042 Duplicate Stmt MObj map.put() (add) attempted by "
        + " LogicalSystem. The duplicate label = ";

    public static final String
                            ERRMSG_THEOREM_LOADER_ROLLBACK_FAILED_1 =
        "A-LA-0043 theoremLoaderRollback() failed."
        + " This is an unrecoverable error, probably a bug!"
        + " Manual restart of mmj2 required."
        + " Explanation message identifying original error follows:";
    public static final String
                            ERRMSG_THEOREM_LOADER_ROLLBACK_FAILED_2 =
        " Explanation message identifying the rollback error"
        + " follows:";

    public static final String
                            ERRMSG_THEOREM_LOADER_COMMIT_FAILED =
        "A-LA-0044 theoremLoaderCommit() failed."
        + " This is an unrecoverable error, probably a bug!"
        + " Manual restart of mmj2 required."
        + " Explanation message identifying failure follows:";

    // =======================================================

    /**
     *  Messages for ProofCompression.java
     */

    public static final String ERRMSG_COMPRESS_OTHER_NOTFND_1 =
        "E-LA-0101 Theorem ";
    public static final String ERRMSG_COMPRESS_OTHER_NOTFND_2 =
        ": compressed proof contains invalid statement label,"
        + " not found in Statement Table. Label position within"
        + " the compressed proof's parentheses = ";
    public static final String ERRMSG_COMPRESS_OTHER_NOTFND_3 =
        ". Statement label = ";

    public static final String ERRMSG_COMPRESS_OTHER_BOGUS_1 =
        "E-LA-0102 Theorem ";
    public static final String ERRMSG_COMPRESS_OTHER_BOGUS_2 =
        ": compressed proof contains invalid statement label"
        + " in parenthesized portion of proof. The referenced"
        + " statement is neither a VarHyp nor an Assrt type"
        + " statement! Label position within"
        + " the compressed proof's parentheses = ";
    public static final String ERRMSG_COMPRESS_OTHER_BOGUS_3 =
        ". Statement label = ";


    public static final String ERRMSG_COMPRESS_OTHER_VARHYP_POS_1 =
        "E-LA-0103 Theorem: ";
    public static final String ERRMSG_COMPRESS_OTHER_VARHYP_POS_2 =
        ": compressed proof contains invalid statement label"
        + " in parenthesized portion of proof. The referenced"
        + " statement is a VarHyp that occurs *after* one or"
        + " more Assrt labels within the parentheses."
        + " Label position within"
        + " the compressed proof's parentheses = ";
    public static final String ERRMSG_COMPRESS_OTHER_VARHYP_POS_3 =
        ". Statement label = ";


    public static final String ERRMSG_COMPRESS_NO_PROOF_BLOCKS_1 =
        "E-LA-0104 Theorem ";
    public static final String ERRMSG_COMPRESS_NO_PROOF_BLOCKS_2 =
        ": compressed proof contains no compressed proof blocks"
        + " following the parentheses! Proof is empty?!";

    public static final String ERRMSG_COMPRESS_PREMATURE_END_1 =
        "E-LA-0105 Theorem ";
    public static final String ERRMSG_COMPRESS_PREMATURE_END_2 =
        ": Premature end of proof. Final compressed proof block"
        + " reached prior to end of compressed step number";

    public static final String ERRMSG_COMPRESS_NOT_ASCII_1 =
        "E-LA-0106 Theorem ";
    public static final String ERRMSG_COMPRESS_NOT_ASCII_2 =
        ": Compressed proof block ";
    public static final String ERRMSG_COMPRESS_NOT_ASCII_3 =
        ", character position ";
    public static final String ERRMSG_COMPRESS_NOT_ASCII_4 =
        " contains a character code > 255 = ";

    public static final String ERRMSG_COMPRESS_BAD_CHAR_1 =
        "E-LA-0107 Theorem ";
    public static final String ERRMSG_COMPRESS_BAD_CHAR_2 =
        ": Compressed proof block ";
    public static final String ERRMSG_COMPRESS_BAD_CHAR_3 =
        ", character position ";
    public static final String ERRMSG_COMPRESS_BAD_CHAR_4 =
        " contains an invalid character. Compressed proof"
        + " block may contain only 'A', 'B'...'Z' and '?'.";

    public static final String ERRMSG_COMPRESS_BAD_UNK_1 =
        "E-LA-0108 Theorem ";
    public static final String ERRMSG_COMPRESS_BAD_UNK_2 =
        ": Compressed proof block ";
    public static final String ERRMSG_COMPRESS_BAD_UNK_3 =
        ", character position ";
    public static final String ERRMSG_COMPRESS_BAD_UNK_4 =
        " contains a '?' inside a compressed proof number"
        + " (for example: 'U?' or 'U?U').";

    public static final String ERRMSG_COMPRESS_BAD_RPT_1 =
        "E-LA-0109 Theorem ";
    public static final String ERRMSG_COMPRESS_BAD_RPT_2 =
        ": Compressed proof block ";
    public static final String ERRMSG_COMPRESS_BAD_RPT_3 =
        ", character position ";
    public static final String ERRMSG_COMPRESS_BAD_RPT_4 =
        " contains a 'Z' (Repeated Subproof symbol)"
        + " inside a compressed proof number"
        + " (for example: 'UZ' or 'UZU').";

    public static final String ERRMSG_COMPRESS_BAD_RPT2_1 =
        "E-LA-0110 Theorem ";
    public static final String ERRMSG_COMPRESS_BAD_RPT2_2 =
        ": Compressed proof block ";
    public static final String ERRMSG_COMPRESS_BAD_RPT2_3 =
        ", character position ";
    public static final String ERRMSG_COMPRESS_BAD_RPT2_4 =
        " contains a 'Z' (Repeated Subproof symbol)"
        + " at an invalid location, such as after another 'Z'"
        + " or following a '?'";

    public static final String ERRMSG_COMPRESS_BAD_RPT3_1 =
        "E-LA-0111 Theorem ";
    public static final String ERRMSG_COMPRESS_BAD_RPT3_2 =
        ": Compressed proof block ";
    public static final String ERRMSG_COMPRESS_BAD_RPT3_3 =
        ", character position ";
    public static final String ERRMSG_COMPRESS_BAD_RPT3_4 =
        " is invalid: comressed number that points beyond"
        + " the end of the Repeated Subproof array.";

    public static final String ERRMSG_COMPRESS_CORRUPT_1 =
        "E-LA-0112 Theorem ";
    public static final String ERRMSG_COMPRESS_CORRUPT_2 =
        ": Compressed proof block ";
    public static final String ERRMSG_COMPRESS_CORRUPT_3 =
        ", character position ";
    public static final String ERRMSG_COMPRESS_CORRUPT_4 =
        " is invalid: It appears that the comressed proof is"
        + " corrupted -- subproof length points outside the"
        + " range of proof steps! The problem may be the result"
        + " of a handcoded compressed proof, or a bug in the"
        + " proof compression logic that occurred at some"
        + " earlier time. At any rate, this proof is bogus"
        + " and cannot be processed further!";

    // =======================================================

    /**
     *  Messages for ProofCompression.java
     */

    public static final String ERRMSG_BAD_PARSE_STMT_1 =
        "A-LA-0201 Stmt Label ";
    public static final String ERRMSG_BAD_PARSE_STMT_2 =
        ": Invalid ParseNode passed for Sub-Expression"
        + " output. Node must be either a VarHyp node"
        + " or a SyntaxAxiom Assrt node -- i.e. a"
        + " parse ParseNode, not a proof ParseNode";

    // =======================================================

    /**
     *  Messages for Messages.java
     */

    public static final String ERRMSG_TIMER_ID_NOTFND_1 =
        "E-LA-0301 TimerID ";
    public static final String ERRMSG_TIMER_ID_NOTFND_2 =
        ": not found in Instrumentation Timer Table. The "
        + " Timer ID's on the startInstrumentationTimer and"
        + " stopInstrumentationTimer RunParms/calls must "
        + " match.";

    public static final String ERRMSG_TIMER_ID_1 =
        "I-LA-0302 TimerID ";
    public static final String ERRMSG_TIMER_ID_2 =
        ": Elapsed Millis=";
    public static final String ERRMSG_TIMER_ID_3 =
        " Total Memory=";
    public static final String ERRMSG_TIMER_ID_4 =
        " (delta=";
    public static final String ERRMSG_TIMER_ID_5 =
        ") Max Memory=";
    public static final String ERRMSG_TIMER_ID_6 =
        " (delta=";
    public static final String ERRMSG_TIMER_ID_7 =
        ") FreeMemory=";
    public static final String ERRMSG_TIMER_ID_8 =
        " (delta=";
    public static final String ERRMSG_TIMER_ID_9 =
        ")";


    // =======================================================

    /**
     *  Messages for WorkVarManager.java
     */

    public static final String ERRMSG_DEFINE_WORK_VAR_TYPE_BAD_1 =
        "A-LA-0401 DefineWorkVarType error on RunParm or"
        + " call parameter. Value of Type Code input = ";
    public static final String ERRMSG_DEFINE_WORK_VAR_TYPE_BAD_2 =
        ". Must be a valid Cnst Type Code used on a $f"
        + " (VarHyp) statement in the input Metamath file.";

    public static final String ERRMSG_DEFINE_WORK_VAR_PREFIX_BAD_1 =
        "A-LA-0402 DefineWorkVarType error on RunParm or"
        + " call parameter. Value of Work Variable Prefix input = ";
    public static final String ERRMSG_DEFINE_WORK_VAR_PREFIX_BAD_2 =
        ". Must be a string containing only characters defined"
        + " in Metamath.pdf as valid math symbols, with no"
        + " embedded blanks.";

    public static final String ERRMSG_DEFINE_WORK_VAR_NBR_BAD_1 =
        "A-LA-0403 DefineWorkVarType error on RunParm or"
        + " call parameter. Value of Work Variable Number input = ";
    public static final String ERRMSG_DEFINE_WORK_VAR_NBR_BAD_2 =
        ". This number defines how many Work Variables will be"
        + " created for the Type Code. The number must not be"
        + " less than ";
    public static final String ERRMSG_DEFINE_WORK_VAR_NBR_BAD_3 =
        ", nor greater than ";

    public static final String ERRMSG_DEFINE_WORK_VAR_PFX_DUP_1 =
        "A-LA-0404 DefineWorkVarType error on RunParm or"
        + " call parameter. Value of Work Variable Prefix = ";
    public static final String ERRMSG_DEFINE_WORK_VAR_PFX_DUP_2 =
        " is the same on two different Work Var Types: ";
    public static final String ERRMSG_DEFINE_WORK_VAR_PFX_DUP_3 =
        " and ";

    public static final String ERRMSG_DEFINE_WORK_VAR_DUP_1 =
        "A-LA-0405 DefineWorkVarType error on RunParm or"
        + " call parameter. Work Variable duplicates a"
        + " regular variable or statement label!"
        + " Work Variable = ";

    public static final String ERRMSG_BOGUS_WORK_VAR_IN_ALLOC_1 =
        "A-LA-0406 Null or empty string WorkVar token input."
        + " This indicates a programming error (bug)!";

    public static final String ERRMSG_TOO_FEW_WORK_VAR_FOR_TYP_1 =
        "A-LA-0407 Ooops, not enough Work Variables available!"
        + " For Type Code ";
    public static final String ERRMSG_TOO_FEW_WORK_VAR_FOR_TYP_2 =
        " a maximum of ";
    public static final String ERRMSG_TOO_FEW_WORK_VAR_FOR_TYP_3 =
        " Work Variables can be in use at one time according"
        + " to the default and RunParm defined settings."
        + " Assuming that there is not a bug, a new setting"
        + " must be input via RunParm 'DefineWorkVarType', and"
        + " mmj2 will need to be restarted. Sorry..."
        + " For more information, see:"
        + " ..\\mmj2\\mmj2jar\\AnnotatedRunParms.txt.";


    // =======================================================

    /**
     *  Messages for ParseNode.java
     */

    public static final String ERRMSG_NULL_TARGET_VAR_HYP_PA_SUBST_1 =
        "A-LA-0501"
        + " Severe bug encountered! cloneTargetToSourceVars"
        + " tried to clone a target VarHyp with a"
        + " null 'paSubst' value -- the target Var Hyps"
        + " should already have been assigned to"
        + " Work Var Hyps!";

    // =======================================================

    /**
     *  Messages for SeqAssigner.java
     */

    public static final String ERRMSG_INTERVAL_SIZE_RANGE_ERR_1 =
        "A-LA-0601"
        + " SeqAssigner Interval Size is in error. Input = ";
    public static final String ERRMSG_INTERVAL_SIZE_RANGE_ERR_2 =
        ". Must be >= ";
    public static final String ERRMSG_INTERVAL_SIZE_RANGE_ERR_3 =
        " and <= ";

    public static final String ERRMSG_INTERVAL_TBL_SIZE_RANGE_ERR_1 =
        "A-LA-0602"
        + " SeqAssigner Interval Table Initial"
        + " Size is in error. Input = ";
    public static final String ERRMSG_INTERVAL_TBL_SIZE_RANGE_ERR_2 =
        ". Must be >= ";
    public static final String ERRMSG_INTERVAL_TBL_SIZE_RANGE_ERR_3 =
        " and <= ";

    public static final String ERRMSG_SEQ_ASSIGNER_OUT_OF_NUMBERS_1 =
        "A-LA-0603"
        + " SeqAssigner has exhausted the available numbers"
        + " available for use. The theoretical maximum is"
        + " limited by mmj2's use of a Java 'int' number, whose"
        + " limit is 2**31 - 1. We are at or near that number"
        + " now. Either modify mmj2 to use 'long' instead of 'int'"
        + " seq numbers, or use RunParm SeqAssignerIntervalSize"
        + " with a smaller interval. The next sequence number is ";
    public static final String ERRMSG_SEQ_ASSIGNER_OUT_OF_NUMBERS_2 =
        " The total count so far, including the next, of MObj's is ";

    public static final String ERRMSG_SEQ_ASSIGNER_ROLLBACK_STATE_1 =
        "A-LA-0604"
        + " SeqAssigner.rollback() invoked but checkpointing"
        + " was not 'on'. This indicates a severe programming error!";

    public static final String ERRMSG_SEQ_ASSIGNER_COMMIT_STATE_1 =
        "A-LA-0605"
        + " SeqAssigner.commit() invoked but checkpointing"
        + " was not 'on'. This indicates a severe programming error!";

    public static final String ERRMSG_SEQ_ASSIGNER_ROLLBACK_AUDIT_1 =
        "I-LA-0606"
        + " SeqAssigner.rollback() unassigning MObj.seq nbr ";
    public static final String ERRMSG_SEQ_ASSIGNER_ROLLBACK_AUDIT_2 =
        " for ";
    public static final String ERRMSG_SEQ_ASSIGNER_ROLLBACK_AUDIT_3 =
        " (";
    public static final String ERRMSG_SEQ_ASSIGNER_ROLLBACK_AUDIT_4 =
        ")";
    public static final String ERRMSG_THEOREM_CAPTION  = "Theorem ";
    public static final String ERRMSG_LOGHYP_CAPTION   = "LogHyp ";
    public static final String ERRMSG_INSERTED_CAPTION = "Inserted";
    public static final String ERRMSG_APPENDED_CAPTION = "Appended";

    public static final String ERRMSG_SEQ_ASSIGNER_CHECKPOINT_STATE_1
                                  =
        "A-LA-0607"
        + " turnOnCheckpointing() invoked but checkpointing"
        + " was already 'on'."
        + " This indicates a severe programming error!";


    // =======================================================

    /**
     *  Messages for Theorem.java
     */

    public static final String
                ERRMSG_DJ_VARS_VARS_NOT_DEF_IN_EXT_FRAME =
        "A-LA-0701"
        + " One or both of the variables in a Distinct Variable"
        + " statement is not present in the theorem's extended frame"
        + " (scope) of Variable Hypotheses. Dj Vars = ";

    // =======================================================

    /**
     *  Messages for BookManager.java
     */

    public static final String
                ERRMSG_BM_UPDATE_W_MMT_SECTION_NOTFND_1 =
        "E-LA-0801"
        + "Bit of a problem here, skipper! Oy!!!"
        + " We was asked to assign a Section Number = ";
    public static final String
                ERRMSG_BM_UPDATE_W_MMT_SECTION_NOTFND_2 =
        " to a Theorem, label = ";
    public static final String
                ERRMSG_BM_UPDATE_W_MMT_SECTION_NOTFND_3 =
        ",  but the given Section is not in the BookManager!";

    // =======================================================

    /**
     *  Messages for MandFrame.java
     */

    public static final String
                ERRMSG_DUP_DJ_VARS_AFTER_CONSOLIDATION_ERR_1 =
        "A-LA-0901"
        + "Severe program bug found. Duplicate disjoint"
        + " variables found even though they were previously"
        + " consolidated to eliminate duplicates!"
        + " Original error message follows: ";

    public static final String
                ERRMSG_DUP_DJ_VARS_AFTER_CONSOLIDATION_ERR_2 =
        "A-LA-0902"
        + "Severe program bug found. Duplicate disjoint"
        + " variables found even though they were previously"
        + " consolidated to eliminate duplicates!"
        + " Original error message follows: ";


}

