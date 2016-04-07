//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * ProofConstants.java  0.07 11/01/2011
 *
 * Sep-25-2005:
 *     -->misc. message typos fixed.
 *     -->noted, a few of the "E-PR-" messages are
 *        overridden by "E-IO-" messages because
 *        mmj.lang.LogicalSystem.java is double-checking.
 * Jan-16-2006:
 *     --> added QED_STEP_NBR literal for use in
 *         creating ProofDerivationStepEntry objects
 *         in VerifyProofs.java.
 *     --> added failsafe PROOF_ABSOLUTE_MAX_RETRIES (sigh)
 *         and ERRMSG_PROOF_ABS_MAX_RETRY_EXCEEDED
 *
 * Version 0.05:
 *     --> Added message for VerifyProofs.convertRPNToFormula()
 *         used when RPN -> Formula conversion fails (abend!)
 *
 * Sep-06-2006:
 *    -->  comment update for TMFF project messages.
 *    -->  convert VerifyProofs hardcoded messages to
 *         ProofConstants.java. Oops.
 *    -->  Fixed bug: a proof of "wph wps ax-mp" resulted in
 *         ArrayIndexOutOfBoundsException in FindUniqueSubstMapping()
 *         so message ERRMSG_STACK_MISMATCH_STEP, E-PR-0021, was
 *         added to detect this underflow condition and generate
 *         a cryptic but helpful message :)
 *
 * Version 0.07 - Nov-01-2011:  comment update.
 */

package mmj.verify;

import static mmj.pa.ErrorCode.of;

import mmj.gmff.GMFFConstants;
import mmj.mmio.MMIOConstants;
import mmj.pa.ErrorCode;
import mmj.pa.PaConstants;
import mmj.transforms.TrConstants;
import mmj.util.UtilConstants;

/**
 * Constants used in mmj.verify.VerifyProofs class.
 * <p>
 * VerifyProofs uses fixed size arrays that are reused from one proof to the
 * next. This is much faster than reallocating everything for each proof, but it
 * means that each instance of VerifyProofs requires massive storage and can
 * only work on one proof at a time. It also means that Metamath databases like
 * set.mm can overflow the arrays (set.mm is unbelievable!) Therefore,
 * VerifyProofs dynamically reallocates its array after an overflow, up to a
 * point; it throws an exception if a predetermined maximum size is exceeded,
 * based on the idea that a bug in the code is more likely than a work formula
 * having 32,000 symbols. The numbers can be changed here, in ProofConstants, up
 * or down (have fun :).
 * <hr>
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
public class ProofConstants {

    // from VerifyProofs.java

    /**
     * Absolute Maximum Retries!
     */
    public static final int PROOF_ABSOLUTE_MAX_RETRIES = 7;

    /**
     * Proof stack initial size.
     */
    public static final int PROOF_PSTACK_INIT_LEN = 400;
    /**
     * Proof stack "hard failure" maximum size.
     */
    public static final int PROOF_PSTACK_HARD_FAILURE_LEN = 6400;
    /**
     * Work expression (array) initial size.
     */
    public static final int PROOF_WEXPR_INIT_LEN = 4000;
    /**
     * Work expression (array) "hard failure" maximum size.
     */
    public static final int PROOF_WEXPR_HARD_FAILURE_LEN = 32000;
    /**
     * Substitution map array initial size.
     */
    public static final int PROOF_SUBST_INIT_LEN = 400;
    /**
     * Substitution map array "hard failure" maximum size.
     */
    public static final int PROOF_SUBST_HARD_FAILURE_LEN = 6400;

    /**
     * QED ProofDerivationStepEntry last step number
     */
    public static final String QED_STEP_NBR = "qed";

    public static final ErrorCode ERRMSG_PSTACK_ARRAY_OVERFLOW = of(
        "A-PR-0001 VerifyProofs pStack array overflow. Bug?"
            + " Increase PROOF_WORK_HARD_FAILURE_LEN"
            + " and recompile? Max at %d");

    public static final ErrorCode ERRMSG_WEXPR_ARRAY_OVERFLOW = of(
        "A-PR-0002 VerifyProofs wExpr array overflow. Bug?"
            + " Increase PROOF_WEXPR_HARD_FAILURE_LEN"
            + " and recompile? Max at %d");

    public static final ErrorCode ERRMSG_SUBST_ARRAY_OVERFLOW = of(
        "A-PR-0003 VerifyProofs subst array overflow. Bug?"
            + " Increase PROOF_SUBST_HARD_FAILURE_LEN"
            + " and recompile? Max at %d");

    public static final ErrorCode ERRMSG_PROOF_STEP_INCOMPLETE = of(
        "E-PR-0004 VerifyProof: proof incomplete. ");

    // ==>E-PR-0005 is overridden when a file is input
    // by
    // E-IO-0025 Proof must have at least one step...
    // so this double-check doesn't hurt but...
    public static final ErrorCode ERRMSG_PROOF_HAS_ZERO_STEPS = of(
        "E-PR-0005 VerifyProof: proof has zero steps.");

    public static final ErrorCode ERRMSG_PROOF_STACK_GT_1_AT_END = of(
        "E-PR-0006 VerifyProof: stack has more than one"
            + " entry at end of proof!");

    public static final ErrorCode ERRMSG_FINAL_STACK_ENTRY_UNEQUAL = of(
        "E-PR-0007 VerifyProof: assertion to be proved not"
            + " = final stack entry: %s");

    public static final ErrorCode ERRMSG_HYP_TYP_MISMATCH_STACK_TYP = of(
        "E-PR-0008 Label %s: VerifyProof: proof stack item type"
            + " not = hypothesis type: %s Stack item type = %s");

    public static final ErrorCode ERRMSG_SUBST_TO_VARS_MATCH = of("E-PR-0009"
        + " Label %s: VerifyProof: DjVars restriction violated! The step lists"
        + " <%s %s> as a DjVars restriction, but the substitutions share"
        + " variable %s. ");

    public static final ErrorCode ERRMSG_SUBST_TO_VARS_NOT_DJ = of("E-PR-0010"
        + " Label %s: VerifyProof: The step lists <%s %s> as a DjVars restriction,"
        + " but the induced restriction <%s %s> after substitution is"
        + " not listed in the theorem. ");

    public static final ErrorCode ERRMSG_STEP_LOG_HYP_SUBST_UNEQUAL = of(
        "E-PR-0011", "Label %s: Verify Proof: invalid substitution, stack"
            + " and subst-hypothesis not equal!  STACK = %s SUBST-HYP = %s");

    public static final ErrorCode ERRMSG_RPN_VERIFY_AS_PROOF_FAILURE = of(
        "E-PR-0012",
        "The Statement's exprRPN (from"
            + " grammatical parsing) failed verification"
            + " in the VerifyProofs engine as follows: %s");

    public static final ErrorCode ERRMSG_PROOF_STACK_UNDERFLOW = of(
        "E-PR-0013" + " VerifyProof: stack 'underflow'! Bug?");

    public static final ErrorCode ERRMSG_PROOF_ABS_MAX_RETRY_EXCEEDED = of(
        "E-PR-0014 Loop in VerifyProofs!?! Absolute maximum"
            + " number of retries performed, unsuccessfully."
            + " This is a bug!");

    public static final ErrorCode ERRMSG_DERIV_STEP_PROOF_FAILURE = of(
        "E-PR-0015 The derivation proof step failed verification"
            + " in the VerifyProofs engine as follows: %s");

    public static final ErrorCode ERRMSG_RPN_TO_FORMULA_CONV_FAILURE = of(
        "A-PR-0016 Programmer Error! Ooops. convertRPNToFormula()"
            + " failed with this message: ");

    public static final ErrorCode ERRMSG_BOGUS_PROOF_LOGHYP_STMT = of(
        "E-PR-0017 Failure to generate proof derivation step:"
            + " Theorem %s LogHyp %s specified in proof that is not found in the"
            + " theorem's LogHyp array.");

    public static final ErrorCode ERRMSG_LOGHYP_STACK_DEFICIENT = of(
        "E-PR-0018 Label %s: Proof Worksheet generation halted"
            + " because the undischargedStack does not have enough"
            + " entries to satisfy the step's logical hypotheses.");

    public static final ErrorCode ERRMSG_FINAL_STACK_ENTRY_UNEQUAL2 = of(
        "E-PR-0019 Proof Worksheet generation halted because"
            + " VerifyProof found the assertion to be proved not"
            + " = final stack entry: %s");

    public static final ErrorCode ERRMSG_NO_DERIV_STEPS_CREATED = of(
        "E-PR-0020",
        "Proof Worksheet generation halted because theorem"
            + " has invalid proof? No proof steps created"
            + " for Proof Worksheet!");

    public static final ErrorCode ERRMSG_STACK_SIZE_MISMATCH_FOR_STEP_HYPS = of(
        "E-PR-0021 Label %s: VerifyProof: proof stack does not contain"
            + " enough items to satisfy the current step's hypotheses");

    public static final ErrorCode ERRMSG_PROOF_STEP_RANGE = of("E-PR-0022"
        + " VerifyProof: Proof backreference points outside the range of"
        + " valid references. This is probably an error in the compressed"
        + " proof.");

    public static final ErrorCode ERRMSG_PROOF_NULL = of("E-PR-0023",
        "Proof is null for Stmt = %s");

    public static final ErrorCode ERRMSG_PROOF_SQUISH_FAIL = of("E-PR-0024",
        "RPN invalid during proof compression");

}
