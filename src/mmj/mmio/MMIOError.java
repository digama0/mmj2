//********************************************************************/
//* Copyright (C) 2005, 2006                                         */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  MMIOError.java  0.03 02/01/2006
 *
 *  Dec-22-2005:
 *  - add charNbr, store line, column and charNbr
 */

package mmj.mmio;

/**
 * Thrown when a parsing error is found in a MetaMath source stream.
 */
public class MMIOError extends Error {
    public long lineNbr = -1;
    public long columnNbr = -1;
    public long charNbr = -1;

    /**
     * Default Constructor, <code>MMIOError</code>.
     */
    public MMIOError() {
        super();
    }

    /**
     * Contructor, <code>MMIOError</code> with
     * error message.
     *
     * @param   errorMessage  error message.
     */
    public MMIOError(final String errorMessage) {
        super(errorMessage);
    }

    /**
     * Contructor, <code>MMIOError</code> with
     * file name, line number, column number and error message.
     *
     * @param   sourceId      String identifying location of error
     * @param   lineNbr       line number assigned to the error
     * @param   columnNbr     column number of the error
     * @param   charNbr       character number of the error
     * @param   errorMessage  error message.
     */
    public MMIOError(final String sourceId, final long lineNbr,
        final long columnNbr, final long charNbr, final String errorMessage)
    {
        super(errorMessage + MMIOConstants.ERRMSG_TXT_SOURCE_ID + sourceId
            + MMIOConstants.ERRMSG_TXT_LINE + lineNbr
            + MMIOConstants.ERRMSG_TXT_COLUMN + columnNbr);
        this.lineNbr = lineNbr;
        this.columnNbr = columnNbr;
        this.charNbr = charNbr;
    }
}
