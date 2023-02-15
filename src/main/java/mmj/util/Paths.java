//********************************************************************/
//* Copyright (C) 2011  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * Paths.java.java  0.01 11/01/2011
 *
 * Nov-01-2011 - Version 0.01
 *         --> new
 */

package mmj.util;

import java.io.File;
import java.io.IOException;

import mmj.pa.MMJException;

/**
 * {@code Paths} holds the path-related command line arguments and provides
 * several path-related services.
 */
public class Paths {

    private final File mmj2Path;
    private final File metamathPath;
    private final File svcPath;

    /**
     * Standard constructor.
     *
     * @param mmj2PathArgument null or existing mmj2 directory.
     * @param metamathPathArgument null or existing Metamath directory.
     * @param svcPathArgument null or existing Svc directory.
     * @throws MMJException if errors found.
     * @throws IOException if IO error
     */
    public Paths(final String mmj2PathArgument,
        final String metamathPathArgument, final String svcPathArgument)
            throws MMJException, IOException
    {

        String s;

        // mmj2 path
        if (mmj2PathArgument == null)
            s = null;
        else
            s = new File(mmj2PathArgument).getAbsolutePath();

        System.out.print(UtilConstants.MMJ2_PATH_REPORT_LINE_1 + s);

        mmj2Path = loadPathArgument(mmj2PathArgument,
            UtilConstants.MMJ2_PATH_ARGUMENT_LITERAL).getCanonicalFile();

        System.out.print(UtilConstants.PATH_REPORT_E_G_CAPTION_1
            + buildMMJ2FilePath(UtilConstants.PATH_REPORT_EXAMPLE_FILE_NAME)
                .getCanonicalPath()
            + UtilConstants.PATH_REPORT_E_G_CAPTION_2 + "\n");

        // metamath path
        if (metamathPathArgument == null)
            s = null;
        else
            s = new File(metamathPathArgument).getAbsolutePath();

        System.out.print(UtilConstants.METAMATH_PATH_REPORT_LINE_2 + s);

        metamathPath = loadPathArgument(metamathPathArgument,
            UtilConstants.METAMATH_PATH_ARGUMENT_LITERAL);

        System.out.print(UtilConstants.PATH_REPORT_E_G_CAPTION_1
            + buildMetamathFilePath(UtilConstants.PATH_REPORT_EXAMPLE_FILE_NAME)
                .getCanonicalPath()
            + UtilConstants.PATH_REPORT_E_G_CAPTION_2 + "\n");

        // svc path
        if (svcPathArgument == null)
            s = null;
        else
            s = new File(svcPathArgument).getAbsolutePath();

        System.out.print(UtilConstants.SVC_PATH_REPORT_LINE_3 + s);

        svcPath = loadPathArgument(svcPathArgument,
            UtilConstants.SVC_PATH_ARGUMENT_LITERAL);
        System.out.print(UtilConstants.PATH_REPORT_E_G_CAPTION_1
            + buildSvcFilePath(UtilConstants.PATH_REPORT_EXAMPLE_FILE_NAME)
                .getCanonicalPath()
            + UtilConstants.PATH_REPORT_E_G_CAPTION_2 + "\n");
    }

    /**
     * Builds a {@code File} object relative to the {@code mmj2Path}.
     * <p>
     * If the input fileName designates an absolute path then the output
     * {@code File} is not relative to {@code mmj2Path} or the Current Path.
     * <p>
     * Otherwise, if {@code mmj2Path} is null then the output {@code File} is
     * relative to the Current Path.
     *
     * @param pathName name of file or directory.
     * @return File object relative to mmj2Path unless input pathName is
     *         absolute or mmj2Path is null, in which case the output File is
     *         relative to the current path.
     */
    public File buildMMJ2FilePath(final String pathName) {

        File file = new File(pathName);

        if (getMMJ2Path() != null && !file.isAbsolute())
            file = new File(getMMJ2Path(), pathName);

        return file;
    }

    /**
     * Builds a {@code File} object relative to the {@code metamathPath}.
     * <p>
     * If the input fileName designates an absolute path then the output
     * {@code File} is not relative to {@code metamathPath} or the Current Path.
     * <p>
     * Otherwise, if {@code metamathPath} is null then the output {@code File}
     * is relative to the Current Path.
     *
     * @param pathName name of file or directory.
     * @return File object relative to metamathPath unless input pathName is
     *         absolute or metamathPath is null, in which case the output File
     *         is relative to the current path.
     */
    public File buildMetamathFilePath(final String pathName) {

        File file = new File(pathName);

        if (getMetamathPath() != null && !file.isAbsolute())
            file = new File(getMetamathPath(), pathName);

        return file;
    }

    /**
     * Builds a {@code File} object relative to the {@code svcPath}.
     * <p>
     * If the input fileName designates an absolute path then the output
     * {@code File} is not relative to {@code svcPath} or the Current Path.
     * <p>
     * Otherwise, if {@code svcPath} is null then the output {@code File} is
     * relative to the Current Path.
     *
     * @param pathName name of file or directory.
     * @return File object relative to svcPath unless input pathName is absolute
     *         or svcPath is null, in which case the output File is relative to
     *         the current path.
     */
    public File buildSvcFilePath(final String pathName) {

        File file = new File(pathName);

        if (getSvcPath() != null && !file.isAbsolute())
            file = new File(getSvcPath(), pathName);

        return file;
    }

    /**
     * Gets the mmj2Path.
     *
     * @return The mmj2Path File object.
     */
    public File getMMJ2Path() {
        return mmj2Path;
    }

    /**
     * Gets the metamathPath.
     *
     * @return The mmj2Path File object.
     */
    public File getMetamathPath() {
        return metamathPath;
    }

    /**
     * Gets the svcPath.
     *
     * @return The mmj2Path File object.
     */
    public File getSvcPath() {
        return svcPath;
    }

    private File loadPathArgument(final String pathArgument,
        final String pathArgumentLiteral) throws MMJException
    {

        if (pathArgument == null)
            return new File(".");

        final File path = new File(pathArgument);

        try {
            if (path.exists() && !path.isFile())
                return path;
            else
                throw new MMJException(UtilConstants.ERRMSG_PATH_INVALID,
                    pathArgumentLiteral, path.getAbsolutePath()).addContext(
                        UtilConstants.ERRMSG_COMMAND_LINE_ARGUMENTS_FORMAT);
        } catch (final SecurityException e) {
            throw new MMJException(UtilConstants.ERRMSG_PATH_SECURITY_ERROR,
                pathArgumentLiteral, path.getAbsolutePath()).addContext(
                    UtilConstants.ERRMSG_COMMAND_LINE_ARGUMENTS_FORMAT);
        }
    }
}
