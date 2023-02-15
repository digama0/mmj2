//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * GrammarConstants.java  0.06 11/01/2011
 *
 * Aug-30-2005: misc. message typos fixed.
 * Sep-25-2005: - add comment, I-GR-0017 is not triggered with
 *                the present configuration of the code.
 *              - Change E-GR-0044 to reflect fact that the
 *                duplicate grammar rule is *derived*, and to
 *                show the actual rules involved!
 *              - add comment, I-GR-0051 will never be triggered
 *                unless
 *                GrammarConstants.PARSE_TREE_MAX_FOR_AMBIG_EDIT
 *                is set to > 2 (Earley Parse will return 'n'
 *                trees for an Ambiguous Grammar but in normal
 *                use here we merely need to see that there are
 *                2 parse trees for an expression to know that
 *                there is an ambiguity.)
 * Sep-02-2006:  comment update for TMFF project messages.
 * Nov-01-2011:  comment update.
 */

package mmj.verify;

import static mmj.pa.ErrorCode.of;

import mmj.gmff.GMFFConstants;
import mmj.mmio.MMIOConstants;
import mmj.pa.ErrorCode;
import mmj.pa.MMJException.FormatContext;
import mmj.pa.PaConstants;
import mmj.transforms.TrConstants;
import mmj.util.UtilConstants;

/**
 * Constants used in Grammar-related classes.
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
public class GrammarConstants {

    /**
     * Maximum number of parse trees to return for an ambiguous statement (with
     * more than one grammatical parse tree.)
     * <p>
     * Normally set at 2, but can be altered for testing to as many as desired
     * (which will generate huge messages...)
     */
    public static final int PARSE_TREE_MAX_FOR_AMBIG_EDIT
//  /*
//                                = 128; // experimental
    = 2; // normal
//  /*

    /**
     * Default list, for Grammar.java constructor, of Type Codes used on
     * Theorems involving Logical Statements, thus identifying all other Type
     * Codes as Syntax/Grammatical Type Codes.
     * <p>
     * At this time only one Provable Logic Statement Type Code can be used for
     * a Metamath system.
     */
    public static final String[] DEFAULT_PROVABLE_LOGIC_STMT_TYP_CODES = {"|-"};

    /**
     * Default list, for Grammar.java constructor, of Type Codes used on
     * statements of logic ("wff" but not "set", etc).
     * <p>
     * In the grammatical parse process a "Start Type Code" is identified for
     * the expression being parsed, and this is the expected Type Code given the
     * formula's Type Code. Generally speaking, if the Formula's Type is the
     * Provable Logic Statement Type Code then the Start Type used is the
     * corresponding Logical Statement Type Code; thus, the grammatical parse
     * Start Type Code for a Formula with Type Code "|-" is "wff" -- and that
     * means that the parse tree root Stmt's Type Code had better be "wff"!
     * <p>
     * At this time only one Logical Statement Type Code can be used for a
     * Metamath system. There is a "loophole" however -- it is not prohibited
     * for another Type Code to convert *to* a Logical Statement Type Code, so
     * "wffa" could convert to "wff" (but "|-" can never be the Type of a
     * variable and cannot convert to "wff").
     */
    public static final String[] DEFAULT_LOGIC_STMT_TYP_CODES = {"wff"};

    /**
     * Grammar Ambiguity Edits Level, complete or "basic".
     * <ul>
     * <li>Complete: runs through all sorts of tedious scenarios looking for
     * ambiguities :)
     * <li>Not-Complete (aka "Basic"): reports ambiguities that hit us in the
     * face, like duplicate Syntax Axioms, parseable GrammarRules, etc.
     * </ul>
     */
    public static final boolean DEFAULT_COMPLETE_GRAMMAR_AMBIG_EDITS = false;

    /**
     * Statement Ambiguity Edits Level, complete or "basic".
     * <ul>
     * <li>Complete: Grammatical Parse attempts to find two parse trees.
     * <li>Not-Complete (aka "Basic"): Grammatical Parse returns the first parse
     * tree found. This is a little or a lot faster, depending on which parser
     * is in use.
     * </ul>
     */
    public static final boolean DEFAULT_COMPLETE_STATEMENT_AMBIG_EDITS = false;

    /**
     * Size limit for PriorityQueue used to store derived GrammarRules
     * "in flight", during processing of a single new GrammarRule.
     * <p>
     * Thus, this is not the maximum number of GrammarRules but a limit to the
     * number waiting to be processed after being derived from one GrammarRule
     * (a GrammarRule such as a TypeConversionRule or NullsPermittedRule can
     * "trigger" other new GrammarRules). For this limit to be breached,
     * something very unorthodox would need to be used, such as putting a key
     * TypeConversion Syntax Axiom at the end of a huge list of other Syntax
     * Axioms (set.mm has only about 500 GrammarRules in total).
     */
    public static final int MAX_DERIVED_RULE_QUEUE_SIZE = 1000;
    /**
     * Maximum times the grammatical parser will "retry" the expression parse
     * after exceeding the size of the pre-allocated arrays (used for
     * efficiency).
     * <p>
     * A retry involves re-allocating the arrays with a larger size and then
     * repeating the parse from the start. BUT if the maximum number of retries
     * is exceeded than there may be a bug in the code -- this is a fail-safe
     * feature, in other words.
     */
    public static final int MAX_PARSE_RETRIES = 20;

    /**
     * Minimum initial allocation size for Earley Parser Itemset Array.
     * <p>
     * Used only if larger than the number of NotationRules. Established because
     * of certain test files with very small numbers of NotationRules which
     * ended up overflowing in the retry routine.
     */
    public static final int EARLEY_PARSE_MIN_ITEMSET_MAXIMUM = 50;

    /**
     * Ratio of initial Earley Itemset Array allocation to the Completed Itemset
     * Array allocation.
     * <p>
     * --> i.e. there are expected to be at most 1/2 as many Completed Items as
     * there are Active/Predicted Items.
     */
    public static final int EARLEY_PARSE_CITEMSET_ITEMSET_RATIO = 2;

    /**
     * Maximum number of loops through the BottomUpParser algorithm is set at
     * BOTTOM_UP_GOVERNOR_LIMIT times the length of the expression being parsed.
     * <p>
     * NOTE: BottomUpParser is not normally used but is retained in mmj for
     * experimental purposes.
     * <p>
     * If BOTTOM_UP_GOVERNOR_LIMIT does not get the job done then the grammar is
     * probably unsuitable for the BottomUp parse algorithm (for example,
     * parsing "supeu" in set.mm with BottomUpParser has never been achieved --
     * it requires millions of loops, or thereabouts.)
     */
    public static final int BOTTOM_UP_GOVERNOR_LIMIT = 4;

    /**
     * Used to limit BottomUpParser when the user has requested "complete"
     * statement ambiguity checking", which means that they want more than one
     * parse tree returned for a single expression, if multiple trees exist.
     * <p>
     * Thus, the Ultra-Maximum number of loops through the BottomUpParser.java
     * algorithm is then set = <code>
     *     BOTTOM_UP_GOVERNOR_LIMIT<br>
     *     times the length of the expression being parsed<br>
     *     times BOTTOM_UP_GOVERNOR_LIMIT_MAX;<br>
     * </code>
     * </p>
     * Generally speaking, this would only be used when attempting to find
     * ambiguities in the grammar rules themselves (they are fewer in number and
     * tend to be "doable" with the BottomUp algorithm.)
     */
    public static final int BOTTOM_UP_GOVERNOR_LIMIT_MAX = 1024;

    /**
     * Absolute maximum stack size used by BottomUpParser.
     * <p>
     * Since this equates to the maximum parse tree depth, it is a ridiculously
     * high number for the BottomUpParser, especially since we are using a
     * governor limit on the number of times through the main loop; in other
     * words, this is a fail-safe, belt *and* suspenders and velcro.
     */
    public static final int BOTTOM_UP_STACK_HARD_FAILURE_MAX = 1000;

    public static final ErrorCode ERRMSG_MAX_RETRIES_EXCEEDED = of("A-GR-0001",
        "Maximum parse retries = %d exceeded. A loop? Bug? Check size of arrays"
            + " which are not adjusted by reInitArrays()");

    public static final ErrorCode ERRMSG_RETRY_TO_BE_INITIATED = of("I-GR-0002",
        "Retry with larger array sizes will"
            + " be initiated after ArrayIndexOutOfBoundsException: %s");

    public static final ErrorCode ERRMSG_BU_GOVERNOR_LIMIT_EXCEEDED = of(
        "E-GR-0003",
        "Bottom-Up parse algorithm Governor limit of %d exceeded. This grammar"
            + " may be unsuitable for the Bottom-Up parsing algorithm."
            + " Skipping this expression's parse.");

    public static final ErrorCode ERRMSG_BU_PARSE_STACK_OVERFLOW = of(
        "A-GR-0004 Parse Stack Overflow. Max stack size set to %d. Recompile"
            + " w/larger maximum or debug :). Ooops!");

    public static final ErrorCode ERRMSG_EXPR_USES_TYP_AS_CNST = of(
        "E-GR-0005 Parsed Formula's expression contains Constant %s that is"
            + " used elsewhere as a Grammatical Type"
            + " (VarHyp Type, Syntax Axiom Type, Logic Stmt"
            + " Type or Provable Logic Stmt Type).");

    public static final ErrorCode ERRMSG_START_RULE_TYPE_UNDEF = of(
        "A-GR-0006 Unable to derive Start Rule Type based"
            + " on Provable Logic Statement Type: %s -- no corresponding"
            + " Logical Statement Type Code"
            + " found in Grammar. This situation indicates that"
            + " there is a bug in the code, it should not ever happen.");

    public static final ErrorCode ERRMSG_EARLEY_ITEMSET_OVERFLOW = of(
        "A-GR-0007 Earley Parse ItemSet Overflow. Max set to %d. Recompile"
            + " w/larger maximum and/or debug. Ooops! :)");

    public static final ErrorCode ERRMSG_EARLEY_C_ITEMSET_OVERFLOW = of(
        "A-GR-0008 Earley Parse Completed ItemSet Overflow."
            + " Max set to %d. Recompile w/larger maximum and/or debug. Ooops! :)");

    public static final ErrorCode ERRMSG_FATAL_ARRAY_INDEX_ERROR = of(
        "A-GR-0009 Program has detected a severe programmer"
            + " mistake! Found ArrayIndexOutOfBoundsException"
            + " that cannot be retried!"
            + " Heat the tar and get the feathers ready...");

    public static final ErrorCode ERRMSG_EARLEY_HYP_PARAMS_NOTFND = of(
        "A-GR-0010 Ooops! Severe program bug somewhere in here!"
            + " Function EarleyParser.buildTreeForTyp()"
            + " cannot produce the hypothesis params for a"
            + " Completed ItemSet item where exprThru = %d, exprFrom = %d,"
            + " and the Completed EarleyItem = %s");

    public static final ErrorCode ERRMSG_EARLEY_HYPMAP_PARAMS_NOTFND = of(
        "A-GR-0011 Ooops! Severe program bug somewhere in here!"
            + "Function EarleyParser.finishLoadingHypMapEntries()"
            + " could not produce the hypothesis params for"
            + " hypothesis map item, HypMap[%d] where exprFrom = %d,"
            + " exprThru = %d, and the Completed EarleyItem = %s");

    public static final ErrorCode ERRMSG_RIGHT_TWEENER_ERROR = of(
        "A-GR-0012 Ooops! Grammar.ruleFormatExpr right side"
            + " constants do not match parse expr right side."
            + " This indicates a parser program error."
            + " hypMap[lastMapIndex].exprThru = %d"
            + " hypMap[lastMapIndex].hypPos = %d ruleFormatExpr = %s");

    public static final ErrorCode ERRMSG_LEFT_TWEENER_ERROR = of(
        "A-GR-0013 Ooops! Grammar.ruleFormatExpr left side"
            + " constants do not match parse expr left side."
            + " This indicates a parser program error."
            + " exprFromR - 1 = %d hypMap[0].hypPos = %d ruleFormatExpr = %s");

    public static final ErrorCode ERRMSG_PARSE_FAILED_AT_POS = of(
        "E-GR-0014 No valid grammatical Parse Tree found for"
            + " expression. Label = %s. Parse terminated in formula at symbol"
            + " position %d");

    public static final ErrorCode ERRMSG_PARSE_FAILED = of(
        "E-GR-0015 No valid grammatical Parse Tree found for"
            + " expression. Label = %s");

    public static final ErrorCode ERRMSG_2_PARSE_TREES = of(
        "I-GR-0016 Two Grammatical Parse trees found for"
            + " expression (the first found will be used). Label = %s"
            + " ParseTree[0] = %s ParseTree[1] = %s");

    public static class LabelContext extends FormatContext {
        public LabelContext(final String label) {
            super("Label = %s", label);
        }
    }

    // Sep-18-2005: I-GR-0017 is not triggered with
    // the present configuration of the code because
    // only one parse of grammar rules is attempted,
    // (see mmj.java.verify.GrammarAmbiguity.java).
    // I-GR-0018 is output instead.
    public static final ErrorCode ERRMSG_GRAMMAR_RULE_PARSEABLE = of(
        "I-GR-0017 Syntax of %s (or grammar rule derived from it indirectly)"
            + " is parseable; thus it is a duplicate or"
            + " is a composite function. 1st ParseTree = %s");

    public static final ErrorCode ERRMSG_GRAMMAR_RULE_2_PARSEABLE = of(
        "I-GR-0018 Syntax of %s (or grammar rule derived from it indirectly)"
            + " is parseable; thus it is a duplicate or"
            + " is a composite function. 1st ParseTree = %s 2nd ParseTree = %s");

    public static final ErrorCode ERRMSG_PROVABLE_TYP_PARAM_INVALID = of(
        "A-GR-0020 Grammar constructor param"
            + " 'provableLogicStmtTypCodes' array is null, has no "
            + " elements, or has more than one element. Use no-parameter"
            + " Constructor to accept default value '|-'");

    public static final ErrorCode ERRMSG_LOGIC_TYP_PARAM_INVALID = of(
        "A-GR-0021 Grammar constructor param"
            + " 'logicStmtTypCodes' array is null, has no "
            + " elements, or has more than one element. Use no-parameter"
            + " Constructor to accept default value 'wff'");

    public static final ErrorCode ERRMSG_PROVABLE_TYP_CD_BOGUS = of(
        "A-GR-0022 Grammar constructor param"
            + " 'provableLogicStmtTypCodes' array member %d"
            + " has length zero or contains a space.");

    public static final ErrorCode ERRMSG_PROVABLE_TYP_DUPS = of(
        "A-GR-0023 Grammar constructor param"
            + " 'provableLogicStmtTypCodes' array members %d and %d are duplicates.");

    public static final ErrorCode ERRMSG_PROVABLE_DUP_OF_LOGICAL = of(
        "A-GR-0024 Grammar constructor param,"
            + " 'provableLogicStmtTypCodes' array member %d"
            + " is a duplicate of Grammar constructor param"
            + " 'logicStmtTypCodes' array member %d");

    public static final ErrorCode ERRMSG_LOGIC_TYP_CD_BOGUS = of(
        "A-GR-0025 Grammar constructor param"
            + " 'logicStmtTypCodes' array member %d has length zero or contains a space.");

    public static final ErrorCode ERRMSG_LOGIC_TYP_DUPS = of(
        "A-GR-0026 Grammar constructor param"
            + " 'logicStmtTypCodes' array members %d and %d are duplicates.");

    public static final ErrorCode ERRMSG_PROVABLE_TYP_NOT_A_CNST = of(
        "A-GR-0027 Grammar constructor param"
            + " 'provableLogicStmtTypCodes' array member %d = %s does not"
            + " specify a valid Cnst Symbol.");

    public static final ErrorCode ERRMSG_LOGIC_TYP_NOT_A_CNST = of(
        "A-GR-0028 Grammar constructor param"
            + " 'logicStmtTypCodes' array member %d = %s"
            + " does not specify a valid Cnst Symbol.%s");

    public static final ErrorCode ERRMSG_VARHYP_TYP_PROVABLE = of(
        "E-GR-0029 Variable Hypothesis, Label = %s, has Type Code that is"
            + " defined as a Provable Logic Statement Type."
            + " This Grammar program is presently unable to deal"
            + " with meta-metalogical statements of that kind.");

    public static final ErrorCode ERRMSG_DJ_VARS_ON_SYNTAX = of(
        "E-GR-0030 Syntax Axiom, Label = %s, has Disjoint Variable Restrictions.");

    public static final ErrorCode ERRMSG_LOGHYP_FOR_SYNTAX = of(
        "E-GR-0031 Syntax Axiom, Label = %s, has Logical Hypothesis ($e) in scope.");

    public static final ErrorCode ERRMSG_SYNTAX_VARHYP_MISMATCH = of(
        "E-GR-0032 Notation Syntax Axiom formula variables do"
            + " not match the VarHyp's for the axiom. Note: a"
            + " variable may appear only once in a Notation Syntax"
            + " Axiom. Stmt Label = %s Formula = %s");

    public static final ErrorCode ERRMSG_SYNTAX_VAR_GT_1_OCC = of(
        "E-GR-0033 Notation Syntax Axiom, label = %s, has Variable %s"
            + " that occurs more than once in its formula = %s.");

    public static final ErrorCode ERRMSG_SYNTAX_USES_TYP_AS_CNST = of(
        "E-GR-0034 Syntax Axiom, label = %s contains Cnst = %s"
            + " that is used elsewhere as a Grammatical Type"
            + " (VarHyp Type, Syntax Axiom Type, Logic Stmt"
            + " Type or Provable Logic Stmt Type).");

    public static final ErrorCode ERRMSG_GRFOREST_EXPR_LENGTH_ZERO = of(
        "A-GR-0035 Input expression has zero length, cannot add.");

    public static final ErrorCode ERRMSG_GRFOREST_RULE_NULL = of(
        "A-GR-0036 Input notationRule is null, cannot add.");

    public static final ErrorCode ERRMSG_GRFOREST_RULE_DUP = of(
        "A-GR-0037 Input notationRule already in GRForest," + " cannot add.");

    public static final ErrorCode ERRMSG_GRFOREST_NODE_LOST = of(
        "A-GR-0038 Oops! Just added a new GRForest node"
            + " but cannot find it now! Oy!");

    public static final ErrorCode ERRMSG_TYPCONV_VARHYP_NOTFND = of(
        "A-GR-0039 Oops! Creating a TypeConversionRule from"
            + " existing NotationRule with baseSyntaxAxiom Label = %s,"
            + " but cannot locate the new VarHyp ParseNode!");

    public static final ErrorCode ERRMSG_TYPCONV_2_VARHYP_NOTFND = of(
        "A-GR-0040 Oops! Creating a TypeConversionRule from"
            + " existing TypeConversionRule with baseSyntaxAxiom"
            + " Label = %s, but cannot locate the new VarHyp ParseNode!");

    public static final ErrorCode ERRMSG_TYPCONV_AXIOM_LOOP = of(
        "E-GR-0041 Syntax Axiom, label = %s is a Type Conversion Rule"
            + " that creates a Type Conversion loop from Type = %s, to Type = %s");

    public static final ErrorCode ERRMSG_TYPCONV_NBRHYP_NE_1 = of(
        "A-GR-0042 Oops! This TypeConversionRule has"
            + " nbrHypParamsUsed = %d. Should be equal to 1"
            + " baseSyntaxAxiom label = %s. This grammar rule number = %d");

    public static final String ERRMSG_AT_CAPTION = " at:";
    public static final String ERRMSG_DOT_CAPTION = " dot:";
    public static final String ERRMSG_AFTER_DOT_CAPTION = " afterDot:";
    public static final String ERRMSG_COLON_CAPTION = ":";
    public static final String ERRMSG_RULE_EXPR_CAPTION = " ruleExpr:";

    public static final ErrorCode ERRMSG_BASE_RULE_IS_DUP = of(
        "E-GR-0043 Syntax Axiom, %s, has base Grammar Rule that is a duplicate"
            + " of another Grammar Rule derived from Syntax Axiom, %s");

    public static final ErrorCode ERRMSG_DUP_RULE_DIFF_TYP = of("E-GR-0044",
        "Derived GrammarRule is a duplicate, and"
            + " has a different Type Code than the rule it"
            + " duplicates. Derived Rule Type Code = %s."
            + " Derived 'Rule Format' expression = %s."
            + " Duplicated Rule's Type Code = %s."
            + " Derived Rule's Base Syntax Axiom Label = %s."
            + " Duplicated Rule's Base Syntax Axiom Label = %s.");

    public static final ErrorCode ERRMSG_BOGUS_PARAM_VARHYP_NODE = of(
        "A-GR-0045 Ooops. Found paramVarHypNode[%d], Label = %s"
            + " that is not a VarHyp! Uh oh, a very ugly situation...");

    public static final ErrorCode ERRMSG_UNDEF_NON_TERMINAL = of(
        "I-GR-0046 Info Message: The size of the Syntax"
            + " Axiom Type Code set = %d is not equal to the size of the Variable Hypothesis"
            + " Type Code set = %d. This indicates that one of the Variable Types has no"
            + " Syntax Axioms, a condition termed 'Undefined non-Terminal',"
            + " and, in theory, may be removed from the grammar.");

    public static final ErrorCode ERRMSG_GRAMMAR_UNAMBIGUOUS = of(
        "I-GR-0047 Info Message: This grammar is deemed"
            + " Unambiguous because every Notation Grammar Rule"
            + " is a 'Gimme Match'. and no other grammar errors"
            + " were found.");

    public static final ErrorCode ERRMSG_NOTATION_VARHYP_NOTFND = of(
        "A-GR-0048 Oops! Creating a NotationRule from"
            + " existing NotationRule with baseSyntaxAxiom %s,"
            + " paramVarHypNode nbr %d, new paramTransformationTree"
            + " = %s. Cannot locate new VarHyp ParseNode!");

    public static final ErrorCode ERRMSG_NOTATION_GRFOREST_DUP = of(
        "A-GR-0050 Oops! Failed add of new NotationRule"
            + " derived from SyntaxAxiom label = %s."
            + " New rule duplicates another in the GRForest.");

    // Note: Only if GrammarConstants.PARSE_TREE_MAX_FOR_AMBIG_EDIT
    // is set to > 2 will I-GR-0051 ever be triggered.
    public static final ErrorCode ERRMSG_N_PARSE_TREES = of(
        "I-GR-0051 Multiple Grammatical Parse trees found for"
            + " expression (the first found will be used). Label = %s%s");
    public static final String ERRMSG_N_PARSE_TREES_2 = " ParseTree[%d] = %s";

    public static final ErrorCode ERRMSG_REDUCE_REDUCE = of(
        "E-GR-0052 Reduce/reduce conflict in grammar: %s => \"%s\" / \"%s\"");

    public static final Class<? extends GrammaticalParser> DEFAULT_PARSER_PROTOTYPE = EarleyParser.class;
}
