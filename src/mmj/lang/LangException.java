//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * LangException.java  0.02 08/23/2005
 */

package mmj.lang;

import mmj.pa.ErrorCode;
import mmj.verify.VerifyException;

/**
 * Thrown when Metamath source file has a non-fatal error such as a syntax
 * error.
 */
public class LangException extends VerifyException {
    public static final String NS = "LA";

    /**
     * Constructor, {@code LangException} with error message.
     *
     * @param code error message.
     * @param args formatting arguments.
     */
    public LangException(final ErrorCode code, final Object... args) {
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
    public LangException(final Throwable cause, final ErrorCode code,
        final Object... args)
    {
        super(cause, code, args);
        checkNS(NS);
    }
}
