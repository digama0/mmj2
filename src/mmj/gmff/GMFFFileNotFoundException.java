//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/


/**
 *  GMFFFileNotFoundException.java  0.01 11/01/2011
 *
 *  Version 0.01:
 *  Nov-01-2011: new.
 */

package mmj.gmff;

/**
 *  Custom exception for GMFF.
 */
public class GMFFFileNotFoundException extends GMFFException {

    /**
     * Default Constructor.
     */
    public GMFFFileNotFoundException() {
        super();
    }

    /**
     * Contructor with error message.
     *
     * @param   errorMessage  error message.
     */
    public GMFFFileNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
