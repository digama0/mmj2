//********************************************************************/
//* Copyright (C) 2008                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  MMTTheoremFile.java  0.01 08/01/2008
 *
 *  Version 0.01:
 *      --> new.
 */

package mmj.tl;
import java.io.*;
import java.util.*;
import mmj.lang.*;
import mmj.mmio.*;

/**
 *  MMTTheoremFile is a little helper class for MMT Theorem files.
 */
public class MMTTheoremFile {

    private File          theoremFile;
    private String        label;

    /**
     *  Constructor using an input File object.
     *  <p>
     *  @throws TheoremLoaderException if the filename doesn't
     *          have filetype ".mmt".
     */
    public MMTTheoremFile(File theoremFile)
                                    throws TheoremLoaderException {

        this.theoremFile          = theoremFile;

        String fileName           = theoremFile.getName();
        if (fileName.length() > 4) {
            label                 =
                fileName.
                    substring(0,
                              fileName.length()
                              - TlConstants.FILE_SUFFIX_MMT.length());
            String suffix         =
                fileName.
                    substring(
                        label.length());
            if (TlConstants.
                    FILE_SUFFIX_MMT.
                        compareToIgnoreCase(
                            suffix) == 0) {
                return;
            }
        }
        throw new TheoremLoaderException(
                TlConstants.ERRMSG_MMT_THEOREM_FILE_TYPE_BOGUS_1
                + theoremFile.getAbsolutePath());
    }

    /**
     *  Constructor using a MMTFolder and a theoremLabel designating
     *  a MMT Theorem File in the MMT Folder.
     *  <p>
     *  @param mmtFolder the MMTFolder to look in.
     *  @param theoremLabel the label of the theorem in the MMT Theorem
     *         file.
     *  @param inputFile boolean set to true if the MMTTheoremFile
     *         is supposed to be an inputFile (and therefore must
     *         exist), otherwise false.
     *  @throws TheoremLoaderException if the theoremLabel is blank
     *          or null, or if the theorem is not found in the MMT
     *          Folder and the inputFile parameter is true, or if
     *          there is a SecurityException.
     */
    public MMTTheoremFile(MMTFolder mmtFolder,
                          String    theoremLabel,
                          boolean   inputFile)
                                throws TheoremLoaderException {

        if (theoremLabel                  == null) {
            throw new TheoremLoaderException(
                TlConstants.ERRMSG_MMT_THEOREM_LABEL_BLANK_1);
        }

        label                     = theoremLabel.trim();
        if (label.length() == 0) {
            throw new TheoremLoaderException(
                TlConstants.ERRMSG_MMT_THEOREM_LABEL_BLANK_1);
        }

        try {
            theoremFile           =
                new File(mmtFolder.getFolderFile(),
                         label
                         + TlConstants.FILE_SUFFIX_MMT);

            if (theoremFile.exists()) {
                if (theoremFile.isFile()) {
                    // okey dokey!
                }
                else {
                    throw new TheoremLoaderException(
                        TlConstants.ERRMSG_MMT_THEOREM_NOT_A_FILE_1
                        + theoremFile.getAbsolutePath());
                }
            }
            else {
                if (inputFile) {
                    throw new TheoremLoaderException(
                        TlConstants.ERRMSG_MMT_THEOREM_NOTFND_1
                        + theoremFile.getAbsolutePath());
                }
            }

        }
        catch(SecurityException e) {
            throw new TheoremLoaderException(
                TlConstants.ERRMSG_MMT_THEOREM_FILE_MISC_ERROR_1
                + theoremFile.getAbsolutePath()
                + TlConstants.ERRMSG_MMT_THEOREM_FILE_MISC_ERROR_2
                + e.getMessage());
        }
    }

    /**
     *  Builds an mmj2 Statementizer object for use in parsing an
     *  input MMT Theorem File.
     *  <p>
     *  @return mmj2 Statementizer object.
     *  @throws TheoremLoaderException if the file doesn't actually
     *          exist or if there is an I/O error.
     */
    public Statementizer constructStatementizer()
                            throws TheoremLoaderException {

        String fileName           = theoremFile.getAbsolutePath();

        Reader readerIn;
        try {
            readerIn              =
                new BufferedReader(
                    new InputStreamReader(
                        new FileInputStream(
                            theoremFile)
                            ),
                        MMIOConstants.READER_BUFFER_SIZE
                        );
        }
        catch (FileNotFoundException e) {
            throw new TheoremLoaderException(
                TlConstants.ERRMSG_MMT_THEOREM_FILE_NOTFND_1
                + fileName
                + TlConstants.ERRMSG_MMT_THEOREM_FILE_NOTFND_2);
        }

        Tokenizer tokenizer;
        try {
            tokenizer             = new Tokenizer(readerIn,
                                                  fileName);
        }
        catch (IOException e) {
            close(readerIn);
            throw new TheoremLoaderException(
                TlConstants.ERRMSG_MMT_THEOREM_FILE_IO_ERROR_1ST_READ_1
                + fileName
                + TlConstants.ERRMSG_MMT_THEOREM_FILE_IO_ERROR_1ST_READ_2
                + e.getMessage());
        }

        return new Statementizer(tokenizer);
    }

    /**
     *  Writes Theorem to the MMT Folder using an input List of
     *  StringBuffer lines.
     *  <p>
     *  Note: the input lines do not contain newline characters,
     *        which are written here in platform neutral code
     *        using writeLine().
     *  <p>
     *  @param mmtTheoremLines List of StringBuffer lines.
     *  @throws TheoremLoaderException if there is an I/O error
     *         while writing the MMTTheoremFile lines.
     */
    public void writeTheoremToMMTFolder(List mmtTheoremLines)
                                throws TheoremLoaderException {

        BufferedWriter w          = null;

        try {

            w                     =
                new BufferedWriter(
                    new FileWriter(theoremFile),
                    TlConstants.FILE_WRITER_BUFFER_SIZE);

            Iterator i            = mmtTheoremLines.iterator();
            while (i.hasNext()) {
                StringBuffer sb   = (StringBuffer)i.next();
                w.write(sb.toString());
                w.newLine();
            }
            w.newLine(); //extra line containing just end-of-line
        }
        catch (IOException e) {
            throw new TheoremLoaderException(
                TlConstants.ERRMSG_MMT_THEOREM_WRITE_IO_ERROR_1
                + theoremFile.getAbsolutePath()
                + TlConstants.ERRMSG_MMT_THEOREM_WRITE_IO_ERROR_2
                + e.getMessage());
        }

        close(w);
    }

    /**
     *  Closes the Writer used for the MMTTheoremFile.
     *  <p>
     *  Does nothing if input Writer is null.
     *  <p>
     *  @param w Writer object or null.
     *  @throws TheoremLoaderException if there is an I/O error
     *         during the close operation.
     */
    public void close(Writer w) throws TheoremLoaderException {
        try {
            if (w != null) {
                w.close();
            }
        }
        catch (IOException e) {
            throw new TheoremLoaderException(
                TlConstants.ERRMSG_MMT_THEOREM_CLOSE_IO_ERROR_1
                + theoremFile.getAbsolutePath()
                + TlConstants.ERRMSG_MMT_THEOREM_CLOSE_IO_ERROR_2
                + e.getMessage());
        }
    }

    /**
     *  Closes the Reader used for the MMTTheoremFile.
     *  <p>
     *  Does nothing if input Writer is null.
     *  <p>
     *  @param readerIn Reader object or null.
     */
    public void close(Reader readerIn) {
        try {
            if (readerIn != null) {
                readerIn.close();
            }
        }
        catch (Exception e) {
        }
    }

    /**
     *  Returns the MMTTheoremFile filename.
     */
    public String getFileName() {
        return theoremFile.getName();
    }

    /**
     *  Returns the MMTTheoremFile theorem label.
     */
    public String getLabel() {
        return label;
    }

    /**
     *  Returns the MMTTheoremFile absolute pathname.
     */
    public String getSourceFileName() {
        return theoremFile.getAbsolutePath();
    }
}
