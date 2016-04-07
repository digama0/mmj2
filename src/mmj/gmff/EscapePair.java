//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * GMFFBoss.java  0.01 11/01/2011
 *
 * Version 0.01:
 * Nov-01-2011: new.
 */

package mmj.gmff;

/**
 * GMFFUserTextEscapes stopped here
 */
/**
 * EscapesPair is a companion class to {@code GMFFUserTextEscapes} which holds a
 * single escape char number and its replacement text.
 * <p>
 * Metamath allows only 7-bit ASCII characters but {@code EscapesPair}
 * accomodates 8 bits just to avoid having to answer questions :-)
 * <p>
 * It is basically just a data structure with some attached utility functions on
 * the data elements.
 * <p>
 * The reason for creating this class is that GMFF parameter type RunParms are
 * not validated and processed until GMFF is initialized, typically when the
 * user requests an export. So the RunParms are cached until initialization
 * time.
 * <p>
 */

public class EscapePair {
    public int num;
    public String replacement;

    /**
     * Standard constructor.
     * <p>
     * No validation is done at this time. Just load the data structure.
     *
     * @param num the escaped character's numeric value
     * @param replacement the text which replaces the escaped character.
     */
    public EscapePair(final int num, final String replacement) {
        this.num = num;
        this.replacement = replacement;
    }

    /**
     * Validates the Num and Replacement fields individually.
     *
     * @param exportType the export type, for error reporting
     * @throws GMFFException if error found.
     */
    public void validateEscapePair(final String exportType)
        throws GMFFException
    {
        validateNum(exportType);
        validateReplacement(exportType);
    }

    /**
     * Validates the Num field.
     * <p>
     * Num must be > -1 and < 256.
     *
     * @param exportType the export type, for error reporting
     * @throws GMFFException if error found.
     */
    public void validateNum(final String exportType) throws GMFFException {

        if (num < GMFFConstants.ESCAPE_PAIR_NUM_MIN
            || num > GMFFConstants.ESCAPE_PAIR_NUM_MAX)
            throw new GMFFException(GMFFConstants.ERRMSG_ESCAPE_PAIR_NUM_BAD,
                exportType, num);
    }

    /**
     * Validates the Replacement field
     * <p>
     * Replacement must not be null and not an empty String.
     *
     * @param exportType the export type, for error reporting
     * @throws GMFFException if error found.
     */
    public void validateReplacement(final String exportType)
        throws GMFFException
    {

        if (replacement == null || replacement.length() == 0)
            throw new GMFFException(
                GMFFConstants.ERRMSG_ESCAPE_PAIR_REPLACEMENT_MISSING,
                exportType);
    }
}
