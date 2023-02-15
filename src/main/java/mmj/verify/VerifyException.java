//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * VerifyException.java  0.02 08/23/2005
 */

package mmj.verify;

import mmj.pa.ErrorCode;
import mmj.pa.MMJException;

/**
 * Thrown by package mmj.verify methods when a verification error is detected in
 * LogicalSystem.
 */
public class VerifyException extends MMJException {
    /**
     * Constructor, {@code VerifyException} with error message.
     *
     * @param code error message.
     * @param args formatting arguments.
     */
    public VerifyException(final ErrorCode code, final Object... args) {
        super(code, args);
    }

    /**
     * Constructor, {@code VerifyException} with error message and cause.
     *
     * @param cause The source exception, for stack tracing
     * @param code error message.
     * @param args formatting arguments.
     */
    public VerifyException(final Throwable cause, final ErrorCode code,
        final Object... args)
    {
        super(cause, code, args);
    }
}
