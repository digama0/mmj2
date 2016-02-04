//********************************************************************/
//* Copyright (C) 2011                                               */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * GMFFExportFile.java  0.01 11/01/2011
 *
 * Version 0.01:
 * Nov-01-2011: new.
 */

package mmj.gmff;

import java.io.*;

/**
 * GMFFExportFile is a little helper class for GMFF to handle the ugly IO
 * details.
 */
public class GMFFExportFile {

    private final String exportFileName;
    private final String exportType;
    private final String charsetEncoding;
    private final boolean append;

    private File exportFile;

    /**
     * Standard constructor.
     *
     * @param exportFolder where output files written
     * @param exportFileName Output File Name.
     * @param charsetEncoding Charset Encoding Name (see doc).
     * @param exportType Export Type (e.g. "html" or "althtml")
     * @param append true only if open in append mode
     * @throws GMFFException if Export File is null or empty, or not a writeable
     *             file, or if there is a file security error, or if
     *             charsetEncoding name is null or empty.
     */
    public GMFFExportFile(final GMFFFolder exportFolder,
        final String exportFileName, final String charsetEncoding,
        final String exportType, final boolean append) throws GMFFException
    {

        if (exportFileName == null || exportFileName.trim().length() == 0)
            throw new GMFFException(
                GMFFConstants.ERRMSG_GMFF_EXPORT_FILE_NAME_BLANK, exportType);

        if (charsetEncoding == null || charsetEncoding.trim().length() == 0)
            throw new GMFFException(
                GMFFConstants.ERRMSG_GMFF_EXPORT_FILE_CHARSET_BLANK,
                exportFileName);

        this.exportFileName = exportFileName.trim();
        this.exportType = exportType;
        this.charsetEncoding = charsetEncoding.trim();
        this.append = append;

        try {
            exportFile = new File(exportFolder.getFolderFile(), exportFileName);

            if (!exportFile.exists()) {
                // OK, great, a new file.
            }
            else if (!exportFile.isFile())
                throw new GMFFException(
                    GMFFConstants.ERRMSG_EXPORT_FILE_EXISTS_NOT_A_FILE,
                    exportFileName, exportType, getAbsolutePath());
            else if (!exportFile.canWrite())
                throw new GMFFException(
                    GMFFConstants.ERRMSG_EXPORT_FILE_EXISTS_CANNOT_UPDATE,
                    exportFileName, exportType, getAbsolutePath());
        } catch (final SecurityException e) {
            throw new GMFFException(e,
                GMFFConstants.ERRMSG_EXPORT_FILE_MISC_ERROR, exportFileName,
                exportType, getAbsolutePath(), e.getMessage());
        }
    }

    /**
     * Writes the export buffer and closes the file.
     *
     * @param exportBuffer text to be written out.
     * @throws GMFFException if I/O exception, security exception or if the
     *             charsetEncoding name is not supported.
     */
    public void writeFileContents(final StringBuilder exportBuffer)
        throws GMFFException
    {

        try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(exportFile, append), charsetEncoding)))
        {
            w.write(exportBuffer.toString());
        } catch (final UnsupportedEncodingException e) {
            throw new GMFFException(e,
                GMFFConstants.ERRMSG_EXPORT_FILE_CHARSET_ERROR, exportFileName,
                exportType, charsetEncoding, e.getMessage());
        } catch (final IOException e) {
            throw new GMFFException(e,
                GMFFConstants.ERRMSG_EXPORT_FILE_IO_ERROR, exportFileName,
                exportType, getAbsolutePath(), e.getMessage());
        } catch (final SecurityException e) {
            throw new GMFFException(e,
                GMFFConstants.ERRMSG_EXPORT_FILE_MISC_ERROR, exportFileName,
                exportType, getAbsolutePath(), e.getMessage());
        }
    }

    /**
     * Returns the absolute pathname of the GMFFExportFile
     *
     * @return Absolute pathname of the GMFFExportFile or null if the underlying
     *         File is null.
     */
    public String getAbsolutePath() {
        if (exportFile == null)
            return null;
        return exportFile.getAbsolutePath();
    }
}
