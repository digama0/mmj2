//********************************************************************/
//* Copyright (C) 2011                                               */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/**
 *  GMFFInputFile.java  0.01 11/01/2011
 *
 *  Version 0.01:
 *  Nov-01-2011: new.
 */

package mmj.gmff;

import java.io.*;

/**
 *  GMFFInputFile is a little helper class for reading
 *  files.
 */
public class GMFFInputFile {

    private final String inputFileName;
    private final String exportType;
    private final String errorMessageDescriptor;
    private final int bufferSize;

    private final GMFFFolder inputFolder;
    private File inputFile;

    /**
     *  Returns the contents of a file as a <code>String</code>.
     *  <p>
     *  @param inputFolder a GMFFFolder holding the input file
     *  @param inputFileName file name of file to be read.
     *  @param exportType the Export Type associated with this
     *           I/O operation -- used for error messages.
     *  @param errorMessageDescriptor name or description of
     *           what is being read (e.g. "Proof Worksheet").
     *  @param bufferSize estimated size of the file, or at least
     *           how large the buffer should be for reading it.
     *  @return Contents of the file as a single String.
     *  @throws GMFFFileNotFoundException if file not found.
     *  @throws GMFFException if other error, such as IO exception.
     */
    public static String getFileContents(final GMFFFolder inputFolder,
        final String inputFileName, final String exportType,
        final String errorMessageDescriptor, final int bufferSize)
        throws GMFFException, GMFFFileNotFoundException
    {

        final GMFFInputFile f = new GMFFInputFile(inputFolder, inputFileName,
            exportType, errorMessageDescriptor, bufferSize);

        return f.loadContentsToString();
    }

    /**
     *  Returns the contents of a file as a <code>String</code>.
     *  <p>
     *  @param inputFolder a GMFFFolder holding the input file
     *  @param file  File object for the file name to be read.
     *  @param exportType the Export Type associated with this
     *           I/O operation -- used for error messages.
     *  @param errorMessageDescriptor name or description of
     *           what is being read (e.g. "Proof Worksheet").
     *  @param bufferSize estimated size of the file, or at least
     *           how large the buffer should be for reading it.
     *  @return Contents of the file as a single String.
     *  @throws GMFFFileNotFoundException if file not found.
     *  @throws GMFFException if other error, such as IO exception.
     */
    public static String getFileContents(final GMFFFolder inputFolder,
        final File file, final String exportType,
        final String errorMessageDescriptor, final int bufferSize)
        throws GMFFException, GMFFFileNotFoundException
    {

        final GMFFInputFile f = new GMFFInputFile(inputFolder, file,
            exportType, errorMessageDescriptor, bufferSize);

        return f.loadContentsToString();
    }

    /**
     *  Standard constructor.
     *  <p>
     *  @param inputFolder a GMFFFolder holding the input file
     *  @param inputFileName file name of file to be read.
     *  @param exportType the Export Type associated with this
     *           I/O operation -- used for error messages.
     *  @param errorMessageDescriptor name or description of
     *           what is being read (e.g. "Proof Worksheet").
     *  @param bufferSize estimated size of the file, or at least
     *           how large the buffer should be for reading it.
     *  @throws GMFFFileNotFoundException if file not found.
     *  @throws GMFFException if other error, such as IO exception.
     */
    public GMFFInputFile(final GMFFFolder inputFolder,
        final String inputFileName, final String exportType,
        final String errorMessageDescriptor, final int bufferSize)
        throws GMFFException, GMFFFileNotFoundException
    {

        this.inputFolder = inputFolder;
        this.inputFileName = inputFileName;
        this.exportType = exportType;
        this.errorMessageDescriptor = errorMessageDescriptor;
        this.bufferSize = bufferSize;

        if (inputFileName == null || inputFileName.trim().length() == 0)
            throw new GMFFException(
                GMFFConstants.ERRMSG_GMFF_INPUT_FILE_NAME_BLANK_1
                    + errorMessageDescriptor
                    + GMFFConstants.ERRMSG_GMFF_INPUT_FILE_NAME_BLANK_1B
                    + exportType);

        try {
            inputFile = new File(inputFolder.getFolderFile(), inputFileName);

            if (inputFile.exists()) {
                if (inputFile.isFile()) {
                    // okey dokey!
                }
                else
                    throw new GMFFException(
                        GMFFConstants.ERRMSG_INPUT_FILE_EXISTS_NOT_A_FILE_1
                            + errorMessageDescriptor
                            + GMFFConstants.ERRMSG_INPUT_FILE_EXISTS_NOT_A_FILE_1B
                            + inputFileName
                            + GMFFConstants.ERRMSG_INPUT_FILE_EXISTS_NOT_A_FILE_2
                            + exportType
                            + GMFFConstants.ERRMSG_INPUT_FILE_EXISTS_NOT_A_FILE_3
                            + getAbsolutePath());
            }
            else
                throw new GMFFFileNotFoundException(
                    GMFFConstants.ERRMSG_INPUT_FILE_NOT_FOUND_1
                        + errorMessageDescriptor
                        + GMFFConstants.ERRMSG_INPUT_FILE_NOT_FOUND_1B
                        + inputFileName
                        + GMFFConstants.ERRMSG_INPUT_FILE_NOT_FOUND_2
                        + exportType
                        + GMFFConstants.ERRMSG_INPUT_FILE_NOT_FOUND_3
                        + getAbsolutePath());
        } catch (final SecurityException e) {
            throw new GMFFException(
                GMFFConstants.ERRMSG_INPUT_FILE_MISC_ERROR_1
                    + errorMessageDescriptor
                    + GMFFConstants.ERRMSG_INPUT_FILE_MISC_ERROR_1B
                    + inputFileName
                    + GMFFConstants.ERRMSG_INPUT_FILE_MISC_ERROR_2 + exportType
                    + GMFFConstants.ERRMSG_INPUT_FILE_MISC_ERROR_3
                    + getAbsolutePath()
                    + GMFFConstants.ERRMSG_INPUT_FILE_MISC_ERROR_4
                    + e.getMessage());

        }
    }

    /**
     *  Standard constructor.
     *  <p>
     *  @param inputFolder a GMFFFolder holding the input file
     *  @param file  File object for the file name to be read.
     *  @param exportType the Export Type associated with this
     *           I/O operation -- used for error messages.
     *  @param errorMessageDescriptor name or description of
     *           what is being read (e.g. "Proof Worksheet").
     *  @param bufferSize estimated size of the file, or at least
     *           how large the buffer should be for reading it.
     *  @throws GMFFException if error, such as IO exception.
     */
    public GMFFInputFile(final GMFFFolder inputFolder, final File file,
        final String exportType, final String errorMessageDescriptor,
        final int bufferSize)
    {

        this.inputFolder = inputFolder;
        this.exportType = exportType;
        this.errorMessageDescriptor = errorMessageDescriptor;
        this.bufferSize = bufferSize;

        inputFile = file;

        inputFileName = file.getName();

    }

    /**
     *  Returns string containing the entire file contents.
     *  <p>
     *  A <code>BufferedReader</code> is created using the
     *  specified buffer size and a <code>StringBuffer</code>
     *  is loaded with repeated reads until end of file
     *  reached. Then the StringBuffer is converted to a
     *  String and returned to the called.
     *  <p>
     *  Obviously, this function has a problem if asked to
     *  read a humongous file. (We should test that :-)
     *  <p>
     *  @return String containing the entire file contents.
     *  @throws GMFFFileNotFoundException if file not found
     *  @throws GMFFException if other error, such as
     *            IOException or SecurityException.
     */
    public String loadContentsToString() throws GMFFException,
        GMFFFileNotFoundException
    {

        Reader readerIn;
        try {
            readerIn = new BufferedReader(new InputStreamReader(
                new FileInputStream(inputFile)), bufferSize);
        } catch (final FileNotFoundException e) {
            throw new GMFFFileNotFoundException();
        } catch (final SecurityException e) {
            throw new GMFFException(
                GMFFConstants.ERRMSG_INPUT_FILE_MISC_ERROR_1
                    + errorMessageDescriptor
                    + GMFFConstants.ERRMSG_INPUT_FILE_MISC_ERROR_1B
                    + inputFileName
                    + GMFFConstants.ERRMSG_INPUT_FILE_MISC_ERROR_2 + exportType
                    + GMFFConstants.ERRMSG_INPUT_FILE_MISC_ERROR_3
                    + getAbsolutePath()
                    + GMFFConstants.ERRMSG_INPUT_FILE_MISC_ERROR_4
                    + e.getMessage());
        }

        final StringBuffer outputBuffer = new StringBuffer(bufferSize);

        final char[] cBuf = new char[bufferSize];

        int nbrCharsRead = 0;

        try {
            while (true) {
                nbrCharsRead = readerIn.read(cBuf, 0, cBuf.length);
                if (nbrCharsRead == -1)
                    break;
                outputBuffer.append(cBuf, 0, nbrCharsRead);
            }
            close(readerIn);
        } catch (final IOException e) {
            close(readerIn);
            throw new GMFFException(
                GMFFConstants.ERRMSG_INPUT_FILE_READ_IO_ERROR_1
                    + errorMessageDescriptor
                    + GMFFConstants.ERRMSG_INPUT_FILE_READ_IO_ERROR_1B
                    + inputFileName
                    + GMFFConstants.ERRMSG_INPUT_FILE_READ_IO_ERROR_2
                    + exportType
                    + GMFFConstants.ERRMSG_INPUT_FILE_READ_IO_ERROR_3
                    + getAbsolutePath()
                    + GMFFConstants.ERRMSG_INPUT_FILE_READ_IO_ERROR_4
                    + e.getMessage());
        }

        return outputBuffer.toString();
    }

    /**
     *  Closes the Reader used for the GMFFInputFile.
     *  <p>
     *  Does nothing if input Reader is null.
     *  <p>
     *  @param readerIn Reader object or null.
     */
    public void close(final Reader readerIn) {
        try {
            if (readerIn != null)
                readerIn.close();
        } catch (final Exception e) {}
    }

    /**
     *  Returns the absolute pathname of the GMFFInputFile
     *  <p>
     *  @return Absolute pathname of the GMFFFolder or null if the
     *              underlying File is null.
     */
    public String getAbsolutePath() {
        if (inputFile == null)
            return null;
        return inputFile.getAbsolutePath();
    }
}
