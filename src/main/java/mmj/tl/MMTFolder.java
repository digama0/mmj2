//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * MMTFolder.java  0.03 11/01/2011
 *
 * Version 0.01:
 *     --> new.
 * Version 0.02 - Jan-01-2012:
 *     --> Added ERRMSG_MMT_FOLDER_READ_ERROR_2 to error message
 *         (it had been left off originally).
 * Version 0.03 - Nov-01-2011
 *     --> Mods for mmj2 Paths Enhancement: Jan-01-2012:
 *         add filePath argument to MMTFolder constructor.
 */

package mmj.tl;

import java.io.File;
import java.util.List;

import mmj.lang.LogicalSystem;
import mmj.lang.Messages;
import mmj.pa.Serializer;
import mmj.pa.SessionStore;

/**
 * MMTFolder is a helper class for the Theorem Loader.
 */
public class MMTFolder {

    public static Serializer<MMTFolder> serializer(final SessionStore store) {
        final Serializer<File> fSer = Serializer
            .getFileSerializer(store::getMMJ2Path);
        return Serializer.of(o -> {
            try {
                return new MMTFolder(fSer.deserialize(o));
            } catch (final Exception e) {
                throw new IllegalArgumentException(e);
            }
        } , v -> fSer.serialize(v.folderFile));
    }

    private File folderFile;

    /**
     * Default constructor for MMTFolder.
     */
    public MMTFolder() {
        folderFile = null;
    }

    /**
     * Constructor for MMTFolder using pathname String.
     *
     * @param filePath path for mmtFolderName. May be null or absolute or
     *            relative path name.
     * @param mmtFolderName String containing absolute or relative pathname.
     * @throws TheoremLoaderException if mmtFolderName is null, is blank,
     *             doesn't exist, is not a directory, or if there is a
     *             SecurityException.
     */
    public MMTFolder(final File filePath, final String mmtFolderName)
        throws TheoremLoaderException
    {

        if (mmtFolderName == null || mmtFolderName.trim().length() == 0)
            throw new TheoremLoaderException(
                TlConstants.ERRMSG_MMT_FOLDER_NAME_BLANK);

        try {
            folderFile = new File(mmtFolderName);
            if (filePath == null || folderFile.isAbsolute()) {}
            else
                folderFile = new File(filePath, mmtFolderName);
            if (folderFile.exists()) {
                if (folderFile.isDirectory()) {
                    // okey dokey!
                }
                else
                    throw new TheoremLoaderException(
                        TlConstants.ERRMSG_NOT_A_MMT_FOLDER,
                        folderFile.getAbsolutePath());
            }
            else
                throw new TheoremLoaderException(
                    TlConstants.ERRMSG_MMT_FOLDER_NOTFND,
                    folderFile.getAbsolutePath());

        } catch (final SecurityException e) {
            throw new TheoremLoaderException(e,
                TlConstants.ERRMSG_MMT_FOLDER_MISC_ERROR,
                folderFile.getAbsolutePath(), e.getMessage());
        }
    }

    /**
     * Constructor for MMTFolder using File object.
     *
     * @param file File object.
     * @throws TheoremLoaderException if input file is null, doesn't exist, is
     *             not a directory, or if there is a SecurityException.
     */
    public MMTFolder(final File file) throws TheoremLoaderException {
        if (file == null)
            throw new TheoremLoaderException(
                TlConstants.ERRMSG_MMT_FOLDER_FILE_NULL);

        try {
            folderFile = file;
            if (folderFile.exists()) {
                if (folderFile.isDirectory()) {
                    // okey dokey!
                }
                else
                    throw new TheoremLoaderException(
                        TlConstants.ERRMSG_NOT_A_MMT_FOLDER,
                        folderFile.getAbsolutePath());
            }
            else
                throw new TheoremLoaderException(
                    TlConstants.ERRMSG_MMT_FOLDER_NOTFND,
                    folderFile.getAbsolutePath());

        } catch (final SecurityException e) {
            throw new TheoremLoaderException(e,
                TlConstants.ERRMSG_MMT_FOLDER_MISC_ERROR,
                folderFile.getAbsolutePath(), e.getMessage());
        }
    }

    /**
     * Returns the File object for the MMTFolder.
     *
     * @return File object for the MMTFolder.
     */
    public File getFolderFile() {
        return folderFile;
    }

    /**
     * Builds the MMTTheoremSet object for an MMTFolder using all the files in
     * the folder with file type ".mmt".
     *
     * @param logicalSystem LogicalSystem object.
     * @param messages Messages object.
     * @param tlPreferences TlPreferences object.
     * @return MMTTheoremSet the MMTTheoremSet object loaded with the
     *         MMTFolder's data.
     * @throws TheoremLoaderException if the MMTFolder File object is null or if
     *             there is an I/O error reading the directory.
     */
    public MMTTheoremSet constructMMTTheoremSet(
        final LogicalSystem logicalSystem, final Messages messages,
        final TlPreferences tlPreferences) throws TheoremLoaderException
    {

        if (folderFile == null)
            throw new TheoremLoaderException(
                TlConstants.ERRMSG_MMT_FOLDER_UNSPECIFIED);

        final File[] fileArray = folderFile.listFiles(new MMTFileFilter());

        if (fileArray == null)
            throw new TheoremLoaderException(
                TlConstants.ERRMSG_MMT_FOLDER_READ_ERROR,
                folderFile.getAbsolutePath());

        return new MMTTheoremSet(fileArray, logicalSystem, messages,
            tlPreferences);

    }

    /**
     * Builds an MMTTheoremSet object for a single theorem in a file in the
     * MMTFolder.
     *
     * @param theoremLabel Metamath label of the theorem to load into the
     *            MMTTheoremSet.
     * @param logicalSystem LogicalSystem object.
     * @param messages Messages object.
     * @param tlPreferences TlPreferences object.
     * @return MMTTheoremSet an MMTTheoremSet object loaded with the MMTFolder's
     *         data for the requested theorem.
     * @throws TheoremLoaderException if the MMTFolder File object is null or if
     *             the theorem label is null or an empty string, or if the given
     *             theorem is not a valid MMT Theorem file in the directory
     *             (i.e. not found), or if there is a security exception.
     */
    public MMTTheoremSet constructMMTTheoremSet(final String theoremLabel,
        final LogicalSystem logicalSystem, final Messages messages,
        final TlPreferences tlPreferences) throws TheoremLoaderException
    {

        if (folderFile == null)
            throw new TheoremLoaderException(
                TlConstants.ERRMSG_MMT_FOLDER_UNSPECIFIED);

        final MMTTheoremFile mmtTheoremFile = new MMTTheoremFile(this,
            theoremLabel, true); // true = input file

        return new MMTTheoremSet(mmtTheoremFile, logicalSystem, messages,
            tlPreferences);
    }

    /**
     * Stores a theorem in the MMTFolder as a MMT Theorem file.
     * <p>
     * Note: the input mmtTheoremLines List does not contain newline characters.
     * Those are created by the program in a platform neutral manner.
     *
     * @param theoremLabel Metamath label of the theorem to store into the
     *            MMTTheoremSet.
     * @param mmtTheoremLines List of StringBuilder objects with one line (no
     *            newline!) per StringBuilder object, already formatted into
     *            Metamath .mm format.
     * @return MMTTheoremFile the output MMTTheoremFile object as created during
     *         the store operation.
     * @throws TheoremLoaderException if the MMTFolder File object is null or if
     *             the theorem label is null or an empty string, or if there is
     *             an I/O error during the attempt to create an MMTTheoremFile.
     */
    public MMTTheoremFile storeMMTTheoremFile(final String theoremLabel,
        final List<StringBuilder> mmtTheoremLines)
            throws TheoremLoaderException
    {

        if (folderFile == null)
            throw new TheoremLoaderException(
                TlConstants.ERRMSG_MMT_FOLDER_UNSPECIFIED);

        final MMTTheoremFile mmtTheoremFile = new MMTTheoremFile(this,
            theoremLabel, false); // false = not input file

        mmtTheoremFile.writeTheoremToMMTFolder(mmtTheoremLines);

        return mmtTheoremFile;
    }

    /**
     * Returns the absolute pathname of the MMTFolder.
     *
     * @return Absolute pathname of the MMTFolder or null if the underlying File
     *         is null.TheoremFile.
     */
    public String getAbsolutePath() {
        if (folderFile == null)
            return null;
        return folderFile.getAbsolutePath();
    }

}
