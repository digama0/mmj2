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

import java.util.IllegalFormatException;

/**
 * Thrown when Metamath source file has a non-fatal error such as a syntax
 * error.
 */
public class LangException extends Exception {

    /**
     * Default Constructor, {@code LangException}.
     */
    public LangException() {
        super();
    }

    /**
     * Contructor, {@code LangException} with error message.
     * 
     * @param errorMessage error message.
     * @param args formatting arguments.
     */
    public LangException(final String errorMessage, final Object... args) {
        super(format(errorMessage, args));
    }

    public static String format(final String format, final Object... args) {
        if (args == null || args.length == 0)
            return format;
        if (format == null)
            return args.toString();
        try {
            return String.format(format, args);
        } catch (final IllegalFormatException e) {
            return format;
        }
    }
}
