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

/**
 *  Thrown when Metamath source file has a non-fatal error such
 *  as a syntax error.
 */
public class LangException extends Exception {

    /**
     * Default Constructor, <code>LangException</code>.
     */
    public LangException() {
        super();
    }

    /**
     *  Contructor, <code>LangException</code> with
     *  error message.
     *
     *  @param   errorMessage  error message.
     */
    public LangException(String errorMessage) {
        super(errorMessage);
    }
}
