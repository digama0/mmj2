//********************************************************************/
//* Copyright (C) 2008  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * TheoremLoaderException.java  0.01 08/01/2008
 */

package mmj.tl;

import mmj.pa.ErrorCode;
import mmj.pa.MMJException;

/**
 * Thrown by package mmj.tl methods when a theorem load error is detected.
 */
public class TheoremLoaderException extends MMJException {
    public static final String NS = "TL";

    /**
     * Constructor, {@code TheoremLoaderException} with error message.
     *
     * @param code error message.
     * @param args formatting arguments.
     */
    public TheoremLoaderException(final ErrorCode code, final Object... args) {
        super(code, args);
        checkNS(NS);
    }

    /**
     * Constructor, {@code TheoremLoaderException} with error message and cause.
     *
     * @param cause The source exception, for stack tracing
     * @param code error message.
     * @param args formatting arguments.
     */
    public TheoremLoaderException(final Throwable cause, final ErrorCode code,
        final Object... args)
    {
        super(cause, code, args);
        checkNS(NS);
    }
}
