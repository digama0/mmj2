//********************************************************************/
//* Copyright (C) 2008                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * MMTTheoremFile.java  0.01 08/01/2008
 *
 * Version 0.01:
 *     --> new.
 */

package mmj.tl;

import java.io.*;
import java.util.List;

import mmj.mmio.*;

/**
 * MMTTheoremFile is a little helper class for MMT Theorem files.
 */
public class MMTTheoremFile {

    private File theoremFile;
    private String label;

    /**
     * Constructor using an input File object.
     *
     * @param theoremFile the File from which to initialize this object
     * @throws TheoremLoaderException if the filename doesn't have filetype
     *             ".mmt".
     */
    public MMTTheoremFile(final File theoremFile)
        throws TheoremLoaderException
    {

        this.theoremFile = theoremFile;

        final String fileName = theoremFile.getName();
        if (fileName.length() > 4) {
            label = fileName.substring(0,
                fileName.length() - TlConstants.FILE_SUFFIX_MMT.length());
            final String suffix = fileName.substring(label.length());
            if (TlConstants.FILE_SUFFIX_MMT.equalsIgnoreCase(suffix))
                return;
        }
        throw new TheoremLoaderException(
            TlConstants.ERRMSG_MMT_THEOREM_FILE_TYPE_BOGUS,
            theoremFile.getAbsolutePath());
    }

    /**
     * Constructor using a MMTFolder and a theoremLabel designating a MMT
     * Theorem File in the MMT Folder.
     *
     * @param mmtFolder the MMTFolder to look in.
     * @param theoremLabel the label of the theorem in the MMT Theorem file.
     * @param inputFile boolean set to true if the MMTTheoremFile is supposed to
     *            be an inputFile (and therefore must exist), otherwise false.
     * @throws TheoremLoaderException if the theoremLabel is blank or null, or
     *             if the theorem is not found in the MMT Folder and the
     *             inputFile parameter is true, or if there is a
     *             SecurityException.
     */
    public MMTTheoremFile(final MMTFolder mmtFolder, final String theoremLabel,
        final boolean inputFile) throws TheoremLoaderException
    {

        if (theoremLabel == null)
            throw new TheoremLoaderException(
                TlConstants.ERRMSG_MMT_THEOREM_LABEL_BLANK);

        label = theoremLabel.trim();
        if (label.length() == 0)
            throw new TheoremLoaderException(
                TlConstants.ERRMSG_MMT_THEOREM_LABEL_BLANK);

        try {
            theoremFile = new File(mmtFolder.getFolderFile(),
                label + TlConstants.FILE_SUFFIX_MMT);

            if (theoremFile.exists()) {
                if (theoremFile.isFile()) {
                    // okey dokey!
                }
                else
                    throw new TheoremLoaderException(
                        TlConstants.ERRMSG_MMT_THEOREM_NOT_A_FILE,
                        theoremFile.getAbsolutePath());
            }
            else if (inputFile)
                throw new TheoremLoaderException(
                    TlConstants.ERRMSG_MMT_THEOREM_NOTFND,
                    theoremFile.getAbsolutePath());

        } catch (final SecurityException e) {
            throw new TheoremLoaderException(e,
                TlConstants.ERRMSG_MMT_THEOREM_FILE_MISC_ERROR,
                theoremFile.getAbsolutePath(), e.getMessage());
        }
    }

    /**
     * Builds an mmj2 Statementizer object for use in parsing an input MMT
     * Theorem File.
     *
     * @return mmj2 Statementizer object.
     * @throws IOException if there is an I/O error.
     * @throws TheoremLoaderException if the file doesn't actually exist
     */
    public Statementizer constructStatementizer()
        throws IOException, TheoremLoaderException
    {

        final String fileName = theoremFile.getAbsolutePath();

        Reader readerIn;
        try {
            readerIn = new BufferedReader(
                new InputStreamReader(new FileInputStream(theoremFile)),
                MMIOConstants.READER_BUFFER_SIZE);
        } catch (final FileNotFoundException e) {
            throw new TheoremLoaderException(
                TlConstants.ERRMSG_MMT_THEOREM_FILE_NOTFND, fileName);
        }

        try {
            return new Statementizer(new Tokenizer(readerIn, fileName));
        } catch (final IOException e) {
            try {
                readerIn.close();
            } catch (final IOException e1) {
                e.addSuppressed(e1);
            }
            throw e;
        }
    }

    /**
     * Writes Theorem to the MMT Folder using an input List of StringBuilder
     * lines.
     * <p>
     * Note: the input lines do not contain newline characters, which are
     * written here in platform neutral code using writeLine().
     *
     * @param mmtTheoremLines List of StringBuilder lines.
     * @throws TheoremLoaderException if there is an I/O error while writing the
     *             MMTTheoremFile lines.
     */
    public void writeTheoremToMMTFolder(
        final List<StringBuilder> mmtTheoremLines)
            throws TheoremLoaderException
    {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(theoremFile),
            TlConstants.FILE_WRITER_BUFFER_SIZE))
        {

            for (final StringBuilder sb : mmtTheoremLines) {
                w.write(sb.toString());
                w.newLine();
            }
            w.newLine(); // extra line containing just end-of-line
        } catch (final IOException e) {
            throw new TheoremLoaderException(e,
                TlConstants.ERRMSG_MMT_THEOREM_WRITE_IO_ERROR,
                theoremFile.getAbsolutePath(), e.getMessage());
        }
    }

    /**
     * Closes the Reader used for the MMTTheoremFile.
     * <p>
     * Does nothing if input Writer is null.
     *
     * @param readerIn Reader object or null.
     */
    public void close(final Reader readerIn) {
        try {
            if (readerIn != null)
                readerIn.close();
        } catch (final Exception e) {}
    }

    /**
     * @return the MMTTheoremFile filename.
     */
    public String getFileName() {
        return theoremFile.getName();
    }

    /**
     * @return the MMTTheoremFile theorem label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the MMTTheoremFile absolute pathname.
     */
    public String getSourceFileName() {
        return theoremFile.getAbsolutePath();
    }
}
