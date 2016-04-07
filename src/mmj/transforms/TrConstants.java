//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import static mmj.pa.ErrorCode.of;

import mmj.gmff.GMFFConstants;
import mmj.mmio.MMIOConstants;
import mmj.pa.ErrorCode;
import mmj.pa.PaConstants;
import mmj.util.UtilConstants;
import mmj.verify.GrammarConstants;
import mmj.verify.ProofConstants;

/**
 * (Most) Constants used in mmj.transforms classes
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
public class TrConstants {
    // This class should have no objects.
    private TrConstants() {}

    // ----------------------------------------------------------
    // Messages
    // ----------------------------------------------------------
    public static final ErrorCode ERRMSG_UNEXPECTED_EXCEPTION = of(
        "A-TR-0001 Unexpected exception: %s");

    public static final ErrorCode ERRMSG_ILLEGAL_STATE_IN_CREATE_TRANSFORMATION = of(
        "A-TR-0003 Error in createTransformation() algorithm");

    public static final ErrorCode ERRMSG_CANONICAL_FORM = of(
        "D-TR-0004 Step %s has canonical form: %s");

    public static final ErrorCode ERRMSG_CANONICAL_CORRESPONDENCE = of(
        "D-TR-0005 found canonical forms correspondance: %s and %s");

    public static final ErrorCode ERRMSG_MORE_THEN_ONE_EQUALITY_OPERATOR = of(
        "E-TR-0101 More then one operator (%s and %s) defines equality between two objects with type %s");

    public static final ErrorCode ERRMSG_MISSING_IMPL_TRIV_RULE = of(
        "E-TR-0102 The input library has no trivial "
            + "implication rule for %s implication operator (main assertion %s).");

    public static final ErrorCode ERRMSG_MISSING_IMPL_DISTR_RULE = of(
        "E-TR-0103 The input library has no "
            + "implication distributive rule for %s implication operator (main assertion %s).");

    public static final ErrorCode ERRMSG_MISSING_EQUAL_COMMUT_DEDUCT_RULE = of(
        "E-TR-0104 The library has no deduction commutative "
            + "assertion for implication operator %s and equivalence operator %s");

    public static final ErrorCode ERRMSG_MISSING_EQUAL_TRANSIT_DEDUCT_RULE = of(
        "E-TR-0105 The library has no deduction transitive "
            + "assertion for implication operator %s and equivalence operator %s");

    public static final ErrorCode ERRMSG_LOOP_IN_TRANSFORMATIONS = of(
        "W-TR-0201 "
            + "One or more loops has been detected during generation of\n"
            + "simplification rules. This can be addressed by adding the rules to\n"
            + "badAssrtList in macros/transformations.js, skipping the preferred\n"
            + "simplification rule if desired. Offending $p and $a rules:\n%s");

    public static final ErrorCode ERRMSG_ASSOC_REPLACE_FAIL = of(
        "D-TR-0301 found associative assrts "
            + "but it has problems with replace: %s: %s");

    public static final ErrorCode ERRMSG_ASSOC_ASSRTS = of(
        "D-TR-0302 associative assrts: %d. %s: %s");

    public static final ErrorCode ERRMSG_COMM_ASSRTS = of(
        "D-TR-0401 commutative assrts: %s: %s");

    public static final ErrorCode ERRMSG_IMPL_GATHER_ASSRTS = of(
        "D-TR-0501 implication gathering assrt %s : %s");

    public static final ErrorCode ERRMSG_GATHER_ASSRTS = of(
        "D-TR-0502 gathering assrt %s : %s");

    public static final ErrorCode ERRMSG_GATHER_PART = of(
        "D-TR-0503 part rule #%d for gathering assrt %s : %s");

    public static final ErrorCode ERRMSG_NOT_AND_OP = of(
        "D-TR-0504 statement %s is not and operation");

    public static final ErrorCode ERRMSG_EQUIV_COMM_ASSRTS = of(
        "D-TR-0601 Equivalence commutative assrt: %s: %s");

    public static final ErrorCode ERRMSG_EQUIV_COMM_DED_ASSRTS = of(
        "D-TR-0602 Equivalence commutative deduction assrt: %s: %s");

    public static final ErrorCode ERRMSG_EQUIV_TRANS_ASSRTS = of(
        "D-TR-0603 Equivalence transitive assrt: %s: %s");

    public static final ErrorCode ERRMSG_EQUIV_TRANS_DED_ASSRTS = of(
        "D-TR-0604 Equivalence transitive deduction assrt: %s: %s");

    public static final ErrorCode ERRMSG_EQUIV_RULES = of(
        "D-TR-0605 Equivalence rules: %s: %s and %s");

    public static final ErrorCode ERRMSG_TYPE_EQUIV = of(
        "D-TR-0606 Type equivalence map: %s: %s");

    public static final ErrorCode ERRMSG_IMPL_DISTR_ASSRTS = of(
        "D-TR-0701 distributive rule for implication: %s: %s");

    public static final ErrorCode ERRMSG_IMPL_TRANS_ASSRTS = of(
        "D-TR-0702 implication transitive rule: %s: %s");

    public static final ErrorCode ERRMSG_MP_BACKWARDS = of(
        "D-TR-0703 the current implementation doesn't support A->B & A"
            + " hypotheses order, assert %s");

    public static final ErrorCode ERRMSG_IMPL_ASSRTS = of(
        "D-TR-0704 implication assrt: %s: %s");

    public static final ErrorCode ERRMSG_IMPL_EQ_ASSRTS = of(
        "D-TR-0705 implication equal assrt: %s: %s");

    public static final ErrorCode ERRMSG_IMPL_TRIV_ASSRTS = of(
        "D-TR-0706 implication trivial rule: %s: %s");

    public static final ErrorCode ERRMSG_CLOSURE_TRANS = of(
        "D-TR-0801 transitive property: %s");

    public static final ErrorCode ERRMSG_TRANS_TO_RESULT = of(
        "D-TR-0802 transitive to result properties(%b): %s: %s");

    public static final ErrorCode ERRMSG_TRANS_TO_CONST = of(
        "D-TR-0803 transitive rule for constant %s: %s: %s");

    public static final ErrorCode ERRMSG_REPL_UNIQUE_COLLECTION = of(
        "D-TR-0901 Replace assrts: unique %s assert collection for %s");

    public static final ErrorCode ERRMSG_REPL_UNIQUE_ASSRT = of(
        "D-TR-0902 Replace assrts: unique %s assert for %s in position %d");

    public static final ErrorCode ERRMSG_REPL_ASSRTS = of(
        "D-TR-0903 Replace assrts for %s [%d]: %s: %s");

    public static final ErrorCode ERRMSG_EMITTED_STEP = of(
        "D-TR-1001 Emmited step: %s");

    public static final ErrorCode ERRMSG_FINISHED_STEP = of(
        "D-TR-1002 Finished step: %s");
}
