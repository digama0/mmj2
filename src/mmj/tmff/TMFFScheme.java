//********************************************************************/
//* Copyright (C) 2006                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * TMFFScheme.java  0.01 11/01/2006
 *
 * Aug-31-2006: - new, holds TMFF Scheme :)
 */

package mmj.tmff;

import mmj.pa.ErrorCode;

/**
 * TMFFScheme holds an instantiated TMFFMethod and a name assigned by a user to
 * the Scheme.
 */
public class TMFFScheme {

    private TMFFMethod tmffMethod;
    private String schemeName;

    /**
     * Default constructor for TMFFScheme.
     */
    public TMFFScheme() {}

    /**
     * Constructor for TMFFScheme used by TMFFBoss and BatchMMJ2.
     *
     * @param param String parameter array corresponding to the BatchMMJ2
     *            RunParm command TMFFDefineScheme.
     */
    public TMFFScheme(final String[] param) {

        if (param.length < 1 || param[0] == null || param[0].length() == 0)
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_SCHEME_NAME_REQUIRED);
        schemeName = param[0];

        if (param.length < 2 || param[1] == null || param[1].length() == 0)
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_SCHEME_METHOD_MISSING);

        tmffMethod = TMFFMethod.constructMethodWithUserParams(param);
    }

    /**
     * Standard constructor for TMFFScheme.
     *
     * @param schemeName name assigned to the scheme by the user.
     * @param method TMFFMethod to be assigned to the scheme.
     */
    public TMFFScheme(final String schemeName, final TMFFMethod method) {
        if (schemeName == null || schemeName.length() == 0)
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_SCHEME_NAME_REQUIRED);
        this.schemeName = schemeName;

        if (method == null)
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_SCHEME_METHOD_MISSING);
        tmffMethod = method;

    }

    /**
     * Get the TMFFMethod instance assigned to this TMFFScheme.
     *
     * @return tmffMethod instance.
     */
    public TMFFMethod getTMFFMethod() {
        return tmffMethod;
    }

    /**
     * Set TMFFMethod assigned to this TMFFScheme.
     *
     * @param tmffMethod pre-instantiated TMFFMethod.
     */
    public void setTMFFMethod(final TMFFMethod tmffMethod) {
        if (tmffMethod == null)
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_SCHEME_METHOD_MISSING);
        if (TMFFConstants.TMFF_UNFORMATTED_SCHEME_NAME
            .equalsIgnoreCase(getSchemeName()))
            throw new IllegalArgumentException(ErrorCode.format(
                TMFFConstants.ERRMSG_SCHEME_CANNOT_BE_UPDATED,
                TMFFConstants.TMFF_UNFORMATTED_SCHEME_NAME));

        this.tmffMethod = tmffMethod;
    }

    /**
     * Get the name assigned to this TMFFScheme.
     *
     * @return schemeName string.
     */
    public String getSchemeName() {
        return schemeName;
    }

    /**
     * Set Name assigned to this TMFFScheme.
     * <p>
     * Must not be null or zero length! And it will need to be unique, though
     * that is validated elsewhere.
     * <p>
     * Scheme Name "Unformatted" is RESERVED and cannot be assigned.
     *
     * @param schemeName non-null, non-empty String.
     */
    public void setSchemeName(final String schemeName) {
        if (schemeName == null || schemeName.length() == 0)
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_SCHEME_NAME_REQUIRED);

        if (TMFFConstants.TMFF_UNFORMATTED_SCHEME_NAME
            .equalsIgnoreCase(schemeName))
            throw new IllegalArgumentException(ErrorCode.format(
                TMFFConstants.ERRMSG_SCHEME_NM_CANT_BE_ASSIGNED,
                TMFFConstants.TMFF_UNFORMATTED_SCHEME_NAME));

        this.schemeName = schemeName;
    }
}
