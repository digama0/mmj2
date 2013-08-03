//********************************************************************/
//* Copyright (C) 2005, 2006                                         */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * MMIOException.java  0.03 02/01/2006
 *
 * Dec-22-2005:
 * - add charNbr, store line, column and charNbr
 */

package mmj.mmio;

/**
 * Thrown when Metamath source file has a non-fatal error such as a syntax
 * error.
 */
public class MMIOException extends Exception {

    public long lineNbr = -1;
    public long columnNbr = -1;
    public long charNbr = -1;

    /**
     * Default Constructor, {@code MMIOException}.
     */
    public MMIOException() {
        super();
    }

    /**
     * Contructor, {@code MMIOException} with error message.
     * 
     * @param errorMessage error message.
     */
    public MMIOException(final String errorMessage) {
        super(errorMessage);
    }

    /**
     * Contructor, {@code MMIOException} with file name, line number, column
     * number and error message.
     * 
     * @param sourceId String identifying source of error
     * @param lineNbr line number assigned to the error
     * @param columnNbr column number assigned to the error
     * @param charNbr character number of the error
     * @param errorMessage error message.
     */
    public MMIOException(final String sourceId, final long lineNbr,
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
