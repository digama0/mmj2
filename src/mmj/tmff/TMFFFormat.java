//********************************************************************/
//* Copyright (C) 2006                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  TMFFFormat.java  0.01 11/01/2006
 *
 *  Aug-31-2006: - new, holds TMFF Format :)
 */

package mmj.tmff;

/**
 *  TMFFFormat holds an instantiated TMFFScheme and a
 *  format number assigned by the user.
 *  <p>
 *  In the future additional schemes might be assigned
 *  for Syntax Axiom level processing -- which is why
 *  TMFFFormat was created, though it seems redundant
 *  now.
 *
 */
public class TMFFFormat {

    private TMFFScheme formatScheme;
    private int        formatNbr;

    /**
     *  Default constructor.
     */
    public TMFFFormat() {
    }

    public static int validateFormatNbr(String formatNbrString) {
        try {
            return TMFFFormat.validateFormatNbr(
                       Integer.parseInt(
                           formatNbrString.trim()));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_BAD_NEW_FORMAT_NBR_1
                + formatNbrString
                + TMFFConstants.ERRMSG_BAD_NEW_FORMAT_NBR_2
                + TMFFConstants.TMFF_MAX_FORMAT_NBR);
        }

    }

    public static int validateFormatNbr(int formatNbr) {

         if (formatNbr < 0
             ||
             formatNbr >
            TMFFConstants.TMFF_MAX_FORMAT_NBR) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_BAD_NEW_FORMAT_NBR_1
                + formatNbr
                + TMFFConstants.ERRMSG_BAD_NEW_FORMAT_NBR_2
                + TMFFConstants.TMFF_MAX_FORMAT_NBR);
        }

        return formatNbr;
    }

    /**
     *  Constructor for TMFFFormat using user parameters.
     *  <p>
     *  @param param String array of RunParm values.
     *  @param tmffPreferences TMFFPreferences object.
     */
    public TMFFFormat(String[]   param,
                      TMFFPreferences tmffPreferences) {

        if (param.length < 1   ||
            param[0] == null   ||
            param[0].length() == 0) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_FORMAT_NBR_MISSING_1);
        }
        this.formatNbr            =
            TMFFFormat.validateFormatNbr(param[0]);

        if (param.length < 2   ||
            param[1] == null   ||
            param[1].length() == 0) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_SCHEME_NAME_MISSING_1);
        }

        this.formatScheme         =
            tmffPreferences.getDefinedScheme(param[1]);
        if (this.formatScheme == null) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_FORMAT_SCHEME_NAME_NOTFND2_1
                + param[1]);
        }

    }


    /**
     *  Standard constructor for TMFFFormat.
     *
     *  @param formatNbrString number 1, 2, 3, etc., up to a
     *                  predefined
     *                  maximum value (presently 3). This number is
     *                  used elsewhere to identify Formats.
     *
     *  @param formatScheme the instantiated TMFFScheme assigned
     *                  to this format.
     *
     */
    public TMFFFormat(String     formatNbrString,
                      TMFFScheme formatScheme) {

        this.formatNbr            =
            TMFFFormat.validateFormatNbr(formatNbrString);

        if (formatScheme == null) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_FORMAT_SCHEME_MISSING_1);
        }

        this.formatScheme         = formatScheme;
    }


    /**
     *  Standard constructor for TMFFFormat.
     *
     *  @param formatNbr number 1, 2, 3, etc., up to a predefined
     *                  maximum value (presently 3). This number is
     *                  used elsewhere to identify Formats.
     *
     *  @param formatScheme the instantiated TMFFScheme assigned
     *                  to this format.
     *
     */
    public TMFFFormat(int        formatNbr,
                      TMFFScheme formatScheme) {

        this.formatNbr            =
            TMFFFormat.validateFormatNbr(formatNbr);

        if (formatScheme == null) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_FORMAT_SCHEME_MISSING_1);
        }

        this.formatScheme        = formatScheme;
    }

    /**
     *  Get the TMFFScheme instance assigned to this TMFFFormat.
     *
     *  @return formatScheme instance.
     */
    public TMFFScheme getFormatScheme() {
        return formatScheme;
    }

    /**
     *  Set TMFFScheme assigned to this TMFFFormat.
     *  <p>
     *  Note: the Scheme for format number 0 cannot
     *        be updated, it is 'RESERVED' for 'Unformatted'.
     *
     *  @param formatScheme pre-instantiated TMFFScheme.
     */
    public void setFormatScheme(TMFFScheme formatScheme) {
        if (formatScheme == null) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_FORMAT_SCHEME_MISSING_1);
        }
        if (getFormatNbr() == 0) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_CANNOT_UPD_FORMAT_0_1
                + TMFFConstants.TMFF_UNFORMATTED_FORMAT_NBR_0
                + TMFFConstants.ERRMSG_CANNOT_UPD_FORMAT_0_2);
        }

        this.formatScheme           = formatScheme;
    }

    /**
     *  Get the number assigned to this TMFFFormat.
     *
     *  @return formatNbr string.
     */
    public int getFormatNbr() {
        return formatNbr;
    }

    /**
     *  Set Number assigned to this TMFFFormat.
     *  <p>
     *  @param formatNbr non-null, non-empty String.
     */
    public void setFormatNbr(int formatNbr) {
         if (formatNbr < 1
             ||
             formatNbr >
            TMFFConstants.TMFF_MAX_FORMAT_NBR) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_BAD_UPD_FORMAT_NBR_1
                + formatNbr
                + TMFFConstants.ERRMSG_BAD_UPD_FORMAT_NBR_2
                + TMFFConstants.TMFF_MAX_FORMAT_NBR);
        }
        this.formatNbr           = formatNbr;
    }
}