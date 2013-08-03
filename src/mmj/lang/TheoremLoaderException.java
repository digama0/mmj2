//********************************************************************/
//* Copyright (C) 2008  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * TheoremLoaderException.java  0.01 08/01/2008
 */

package mmj.lang;

/**
 *  Thrown by package mmj.tl methods when a theorem load
 *  error is detected.
 */
public class TheoremLoaderException extends LangException {

    /**
     * Default Constructor, <code>TheoremLoaderException</code>.
     */
    public TheoremLoaderException() {
        super();
    }

    /**
     *  Contructor, <code>TheoremLoaderException</code> with
     *  error message.
     *
     *  @param   errorMessage  error message.
     */
    public TheoremLoaderException(String errorMessage) {
        super(errorMessage);
    }
}
