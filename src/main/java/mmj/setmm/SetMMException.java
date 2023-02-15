//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * SetMMException.java  0.02 30/07/2016
 */

package mmj.setmm;

import mmj.pa.ErrorCode;
import mmj.verify.VerifyException;

/**
 * Thrown when Metamath source file has a non-fatal error such as a syntax
 * error.
 */
public class SetMMException extends VerifyException {
    public static final String NS = "SM";

    /**
     * Constructor, {@code SetMMException} with error message.
     *
     * @param code error message.
     * @param args formatting arguments.
     */
    public SetMMException(final ErrorCode code, final Object... args) {
        super(code, args);
        checkNS(NS);
    }

    /**
     * Constructor, {@code LangException} with error message and cause.
     *
     * @param cause The source exception, for stack tracing
     * @param code error message.
     * @param args formatting arguments.
     */
    public SetMMException(final Throwable cause, final ErrorCode code,
        final Object... args)
    {
        super(cause, code, args);
        checkNS(NS);
    }
}
