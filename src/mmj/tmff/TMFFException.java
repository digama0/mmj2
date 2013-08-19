//********************************************************************/
//* Copyright (C) 2006  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * TMFFException.java  0.01 11/01/2006
 *
 * Aug-28-2006: - new Exception for TMFF internal processing.
 */

package mmj.tmff;

/**
 * Used internally in the TMFF classes of mmj.lang.
 */
public class TMFFException extends Exception {

    /**
     * Default Constructor, {@code TMFFException}.
     */
    public TMFFException() {
        super();
    }

    /**
     * Contructor, {@code TMFFException} with error message.
     * 
     * @param errorMessage error message.
     */
    public TMFFException(final String errorMessage) {
        super(errorMessage);
    }
}
