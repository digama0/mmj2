//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import mmj.gmff.GMFFConstants;
import mmj.mmio.MMIOConstants;
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
    public static final String ERRMSG_UNEXPECTED_EXCEPTION = "A-TR-0001 Unexpected exception: ";
    public static final String ERRMSG_ILLEGAL_STATE_IN_CREATE_TRANSFORMATION = "A-TR-0003 Error in createTransformation() algorithm";

    public static final String ERRMSG_MORE_THEN_ONE_EQUALITY_OPERATOR = "E-TR-0101 More then one operator (%s and %s) defines equality between two objects with type %s";
    public static final String ERRMSG_MISSING_IMPL_TRIV_RULE = "E-TR-0102 The input library has no trivial "
        + "implication rule for %s implication operator (main assertion %s).";
    public static final String ERRMSG_MISSING_IMPL_DISRT_RULE = "E-TR-0103 The input library has no "
        + "implication distributive rule for %s implication operator (main assertion %s).";
    public static final String ERRMSG_MISSING_EQUAL_COMMUT_DEDUCT_RULE = "E-TR-0104 The library has no deduction commutative "
        + "assertion for implication operator %s and equivalence operator %s";
    public static final String ERRMSG_MISSING_EQUAL_TRANSIT_DEDUCT_RULE = "E-TR-0105 The library has no deduction transitive "
        + "assertion for implication operator %s and equivalence operator %s";

    public static final String ERRMSG_LOOP_IN_TRANSFORMATIONS = "W-TR-0201 "
        + "One or more loops has been detected during generation of\n"
        + "simplification rules. This can be addressed by adding the rules to\n"
        + "badAssrtList in macros/transformations.js, skipping the preferred\n"
        + "simplification rule if desired. Offending $p and $a rules:\n";
}
