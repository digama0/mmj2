//********************************************************************/
//* Copyright (C) 2005, 2006                                         */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * MMIOException.java  0.03 02/01/2006
 *
 * Dec-22-2005:
 * - add charNbr, store line, column and charNbr
 */

package mmj.mmio;

import mmj.pa.ErrorCode;
import mmj.pa.MMJException;

/**
 * Thrown when Metamath source file has a non-fatal error such as a syntax
 * error.
 */
public class MMIOException extends MMJException {
    public static final String NS = "IO";

    /**
     * Constructor, {@code MMIOException} with error message.
     *
     * @param code error message.
     * @param args formatting arguments.
     */
    public MMIOException(final ErrorCode code, final Object... args) {
        super(code, args);
        checkNS(NS);
    }

    /**
     * Constructor, {@code MMIOException} with error message and cause.
     *
     * @param cause The source exception, for stack tracing
     * @param code error message.
     * @param args formatting arguments.
     */
    public MMIOException(final Throwable cause, final ErrorCode code,
        final Object... args)
    {
        super(cause, code, args);
        checkNS(NS);
    }
}
