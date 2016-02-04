//********************************************************************/
//* Copyright (C) 2005, 2006                                         */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * ProofAsstException.java  0.01 02/01/2006
 */

package mmj.pa;

/**
 * Custom exception for ProofAsst.
 */
public class ProofAsstException extends MMJException {
    public static final String NS = "PA";

    /**
     * Constructor, {@code ProofAsstException} with error message.
     *
     * @param code error message.
     * @param args formatting arguments.
     */
    public ProofAsstException(final ErrorCode code, final Object... args) {
        super(code, args);
        checkNS(NS);
    }

    /**
     * Constructor, {@code ProofAsstException} with error message and cause.
     *
     * @param cause The source exception, for stack tracing
     * @param code error message.
     * @param args formatting arguments.
     */
    public ProofAsstException(final Throwable cause, final ErrorCode code,
        final Object... args)
    {
        super(cause, code, args);
        checkNS(NS);
    }
}
