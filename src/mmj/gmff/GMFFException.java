//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * GMFFException.java  0.01 11/01/2011
 *
 * Version 0.01:
 * Nov-01-2011: new.
 */

package mmj.gmff;

/**
 * Custom exception for GMFF.
 */
public class GMFFException extends RuntimeException {

    /**
     * Default Constructor.
     */
    public GMFFException() {
        super();
    }

    /**
     * Contructor with error message.
     *
     * @param errorMessage error message.
     */
    public GMFFException(final String errorMessage) {
        super(errorMessage);
    }
}
