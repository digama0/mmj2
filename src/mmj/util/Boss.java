//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * Boss.java  0.10 11/01/2011
 *
 * Dec-10-2005
 *     -->Added folder-related routines for use by ProofAsst:
 *            editExistingFolderRunParm(
 *            editExistingFolderNameParm(
 *            buildFileObjectForExistingFolder(
 *        These are called by mmj.util.ProofAsstBoss.java
 * Jan-16-2006
 *     -->Added editYesNoRunParm
 *     -->Added buffering in doConstructPrintWriter.
 *     -->Added doConstructBufferedFileWriter for exporting proofs
 *        from Proof Assistant.
 *     -->changed name of method editPrintWriterFileNameParm() to
 *         editFileNameParm().
 *
 * Version 0.04:
 *     -->Added editRunParmPrintableNoBlanksString
 *        for ProofAsstBoss
 *
 * Sep-09-2006 - Version 0.05 - TMFF Enhancement
 *     -->Added editOnOffRunParm()
 *
 * Jun-01-2007 - Version 0.06
 *     -->err msg bug fix, see "PATCH 2007-10-01"
 *
 * Nov-01-2007 - Version 0.07
 *     -->Misc.
 *
 * Feb-01-2008 - Version 0.08
 *     -->Remove old, commented-out code (patch note.)
 *
 * Aug-01-2008 - Version 0.09
 *     -->Add TheoremLoaderException.
 *     -->editRunParmNonNegativeInteger() and
 *        editRunParmValueReqNonNegativeInt().
 *
 * Nov-01-2011 - Version 0.10
 *     -->Add GMFFException
 *     -->Modified for mmj2 Paths Enhancement:
 *        --> added filePath argument to
 *            editExistingFolderRunParm() and
 *            buildFileObjectForExistingFolder() and
 *            doConstructPrintWriter() and
 *            editPrintWriterRunParm()
 */

package mmj.util;

import java.awt.Color;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Map;

import mmj.gmff.GMFFException;
import mmj.lang.*;
import mmj.mmio.MMIOException;
import mmj.pa.PaConstants;

/**
 * Boss is the superclass of GrammarBoss, LogicalSystemBoss, etc, which are used
 * by BatchFramework to "exercise" mmj2 in batch mode.
 * <p>
 * Boss consists of the abstract "doRunParmCommand" and some common parameter
 * validation functions used in sub-classes of Boss.
 */
public abstract class Boss {

    protected BatchFramework batchFramework;

    /**
     * Constructor with BatchFramework for access to environment.
     * 
     * @param batchFramework for access to environment.
     */
    public Boss(final BatchFramework batchFramework) {
        this.batchFramework = batchFramework;
    }

    /**
     * Executes a single command from the RunParmFile.
     * 
     * @param runParm the RunParmFile line to execute.
     * @return {@code true} if the RunParm was "consumed"
     * @throws IllegalArgumentException if an error occurred in the RunParm
     * @throws MMIOException if an error occurred in the RunParm
     * @throws FileNotFoundException if an error occurred in the RunParm
     * @throws IOException if an error occurred in the RunParm
     * @throws VerifyException if an error occurred in the RunParm
     * @throws TheoremLoaderException if an error occurred in the RunParm
     * @throws GMFFException if an error occurred in the RunParm
     */
    public abstract boolean doRunParmCommand(RunParmArrayEntry runParm)
        throws IllegalArgumentException, MMIOException, FileNotFoundException,
        IOException, VerifyException, TheoremLoaderException, GMFFException;

    // =======================================================
    // === bazillions of subroutines used by Boss subclasses
    // =======================================================

    /**
     * Validate existing folder RunParm (must exist!)
     * 
     * @param filePath path used to resolve file name. May be null or absolute
     *            or relative path.
     * @param runParm RunParmFile line parsed into RunParmArrayEntry.
     * @param valueCaption name of RunParm, for error message output.
     * @param valueFieldNbr number of field in RunParm line.
     * @return File object for folder
     * @throws IllegalArgumentException if an error occurred
     */
    protected File editExistingFolderRunParm(final File filePath,
        final RunParmArrayEntry runParm, final String valueCaption,
        final int valueFieldNbr) throws IllegalArgumentException
    {

        final String folderNameParm = editExistingFolderNameParm(runParm,
            valueCaption, valueFieldNbr);

        return buildFileObjectForExistingFolder(filePath, valueCaption,
            folderNameParm);
    }

    /**
     * Validate existing folder RunParm (must exist!)
     * 
     * @param filePath path name for building files. May be null, relative or
     *            absolute.
     * @param runParm RunParmFile line parsed into RunParmArrayEntry.
     * @param valueCaption name of RunParm, for error message output.
     * @param valueFieldNbr number of field in RunParm line.
     * @return File object for file.
     * @throws IllegalArgumentException if an error occurred
     */
    protected File editExistingFileRunParm(final File filePath,
        final RunParmArrayEntry runParm, final String valueCaption,
        final int valueFieldNbr) throws IllegalArgumentException
    {

        final String fileNameParm = editFileNameParm(runParm, valueCaption,
            valueFieldNbr);

        return buildFileObjectForExistingFile(filePath, valueCaption,
            fileNameParm);
    }

    /**
     * Validate Proof Worksheet File Name Suffix
     * 
     * @param runParm RunParmFile line.
     * @param valueCaption name of RunParm, for error message output.
     * @param valueFieldNbr number of field in RunParm line.
     * @return String validated file name suffix
     * @throws IllegalArgumentException if an error occurred
     */
    protected String editProofWorksheetFileNameSuffix(
        final RunParmArrayEntry runParm, final String valueCaption,
        final int valueFieldNbr) throws IllegalArgumentException
    {
        editRunParmValuesLength(runParm, valueCaption, 1);
        final String fileNameSuffixParm = runParm.values[valueFieldNbr - 1]
            .trim();
        if (fileNameSuffixParm
            .compareTo(PaConstants.PA_GUI_FILE_CHOOSER_FILE_SUFFIX_TXT) == 0
            || fileNameSuffixParm
                .compareTo(PaConstants.PA_GUI_FILE_CHOOSER_FILE_SUFFIX_TXT2) == 0
            || fileNameSuffixParm
                .compareTo(PaConstants.PA_GUI_FILE_CHOOSER_FILE_SUFFIX_MMP) == 0
            || fileNameSuffixParm
                .compareTo(PaConstants.PA_GUI_FILE_CHOOSER_FILE_SUFFIX_MMP2) == 0)
            return fileNameSuffixParm;

        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_BAD_FILE_NAME_SUFFIX_1 + valueCaption
                + UtilConstants.ERRMSG_BAD_FILE_NAME_SUFFIX_2 + valueFieldNbr
                + UtilConstants.ERRMSG_BAD_FILE_NAME_SUFFIX_3);
    }

    /**
     * Validate name of folder
     * 
     * @param runParm RunParmFile line.
     * @param valueCaption name of RunParm, for error message output.
     * @param valueFieldNbr number of field in RunParm line.
     * @return String validated folder name.
     * @throws IllegalArgumentException if an error occurred
     */
    protected String editExistingFolderNameParm(
        final RunParmArrayEntry runParm, final String valueCaption,
        final int valueFieldNbr) throws IllegalArgumentException
    {
        editRunParmValuesLength(runParm, valueCaption, 1);
        final String folderNameParm = runParm.values[valueFieldNbr - 1].trim();
        if (folderNameParm.length() == 0)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_FOLDER_NAME_BLANK_1 + valueCaption
                    + UtilConstants.ERRMSG_FOLDER_NAME_BLANK_2 + valueFieldNbr
                    + UtilConstants.ERRMSG_FOLDER_NAME_BLANK_3);
        return folderNameParm;
    }

    /**
     * Build a File object for a Folder Name
     * 
     * @param filePath path used to resolve file name. May be null or absolute
     *            or relative path.
     * @param valueCaption name of RunParm, for error message output.
     * @param folderNameParm name of folder
     * @return File File object for folder
     * @throws IllegalArgumentException if an error occurred
     */
    protected File buildFileObjectForExistingFolder(final File filePath,
        final String valueCaption, final String folderNameParm)
        throws IllegalArgumentException
    {

        File folder = new File(folderNameParm);
        try {
            if (filePath == null || folder.isAbsolute()) {}
            else
                folder = new File(filePath, folderNameParm);
            if (folder.exists()) {
                if (folder.isDirectory()) {
                    // okey dokey!
                }
                else
                    throw new IllegalArgumentException(
                        UtilConstants.ERRMSG_NOT_A_FOLDER_1 + valueCaption
                            + UtilConstants.ERRMSG_NOT_A_FOLDER_2
//                      + folderNameParm
                            + folder.getAbsolutePath()
                            + UtilConstants.ERRMSG_NOT_A_FOLDER_3);
            }
            else
                throw new IllegalArgumentException(
                    UtilConstants.ERRMSG_FOLDER_NOTFND_1 + valueCaption
                        + UtilConstants.ERRMSG_FOLDER_NOTFND_2
//                  + folderNameParm
                        + folder.getAbsolutePath()
                        + UtilConstants.ERRMSG_FOLDER_NOTFND_3);

        } catch (final Exception e) {
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_FOLDER_MISC_ERROR_1 + valueCaption
                    + UtilConstants.ERRMSG_FOLDER_MISC_ERROR_2
//              + folderNameParm
                    + folder.getAbsolutePath()
                    + UtilConstants.ERRMSG_FOLDER_MISC_ERROR_3 + e.getMessage());
        }
        return folder;
    }

    /**
     * Build a File object for an existing File Name
     * 
     * @param filePath path name for building files. May be null, relative or
     *            absolute.
     * @param valueCaption name of RunParm, for error message output.
     * @param fileNameParm name of file
     * @return File File object for file.
     * @throws IllegalArgumentException if an error occurred
     */
    protected File buildFileObjectForExistingFile(final File filePath,
        final String valueCaption, final String fileNameParm)
        throws IllegalArgumentException
    {

        File file = new File(fileNameParm);
        try {
            if (filePath == null || file.isAbsolute()) {}
            else
                file = new File(filePath, fileNameParm);
            if (file.exists()) {
                if (!file.isDirectory()) {
                    // okey dokey!
                }
                else
                    throw new IllegalArgumentException(
                        UtilConstants.ERRMSG_NOT_A_FILE_1 + valueCaption
                            + UtilConstants.ERRMSG_NOT_A_FILE_2
//                      + fileNameParm
                            + file.getAbsolutePath()
                            + UtilConstants.ERRMSG_NOT_A_FILE_3);
            }
            else
                throw new IllegalArgumentException(
                    UtilConstants.ERRMSG_FILE_NOTFND_1 + valueCaption
                        + UtilConstants.ERRMSG_FILE_NOTFND_2
//                  + fileNameParm
                        + file.getAbsolutePath()
                        + UtilConstants.ERRMSG_FILE_NOTFND_3);

        } catch (final Exception e) {
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_FILE_MISC_ERROR_1 + valueCaption
                    + UtilConstants.ERRMSG_FILE_MISC_ERROR_2
//              + fileNameParm
                    + file.getAbsolutePath()
                    + UtilConstants.ERRMSG_FILE_MISC_ERROR_3 + e.getMessage());
        }
        return file;
    }

    /**
     * Validate PrintWriter RunParm and its options.
     * 
     * @param filePath path for building files. May be null, absolute, or
     *            relative.
     * @param runParm RunParmFile line parsed into RunParmArrayEntry.
     * @param valueCaption name of RunParm, for error message output.
     * @return PrintWriter object.
     * @throws IllegalArgumentException if an error occurred
     */
    protected PrintWriter editPrintWriterRunParm(final File filePath,
        final RunParmArrayEntry runParm, final String valueCaption)
        throws IllegalArgumentException
    {

        final String fileNameParm = editFileNameParm(runParm, valueCaption, 1);

        final String fileUsageParm = editFileUsageParm(runParm, valueCaption, 2);

        final String fileCharsetParm = editFileCharsetParm(runParm,
            valueCaption, 3);

        return doConstructPrintWriter(filePath, valueCaption, fileNameParm,
            fileUsageParm, fileCharsetParm);
    }

    /**
     * Construct a PrintWriter using RunParm options.
     * 
     * @param filePath path for building files. May be null, absolute or
     *            relative.
     * @param valueCaption name of RunParm, for error message output.
     * @param fileNameParm RunParmFile line parsed into RunParmArrayEntry.
     * @param fileUsageParm "new" or "update"
     * @param fileCharsetParm optional, "UTF-8", etc.
     * @return PrintWriter object.
     * @throws IllegalArgumentException if an error occurred
     */
    protected PrintWriter doConstructPrintWriter(final File filePath,
        final String valueCaption, final String fileNameParm,
        final String fileUsageParm, final String fileCharsetParm)
        throws IllegalArgumentException
    {

        PrintWriter printWriter = null;
        File file = new File(fileNameParm);
        try {
            if (filePath == null || file.isAbsolute()) {}
            else
                file = new File(filePath, fileNameParm);
            if (file.exists())
                if (fileUsageParm
                    .compareTo(UtilConstants.RUNPARM_OPTION_FILE_OUT_NEW) == 0)
                    throw new IllegalArgumentException(
                        UtilConstants.ERRMSG_FILE_USAGE_ERR_EXISTS_1
                            + valueCaption
                            + UtilConstants.ERRMSG_FILE_USAGE_ERR_EXISTS_2
//                      + fileNameParm
                            + file.getAbsolutePath()
                            + UtilConstants.ERRMSG_FILE_USAGE_ERR_EXISTS_3
                            + UtilConstants.RUNPARM_OPTION_FILE_OUT_NEW
                            + UtilConstants.ERRMSG_FILE_USAGE_ERR_EXISTS_4);
                else if (file.isFile() && file.canWrite()
                    && !file.isDirectory())
                {
                    // okey dokey!
                }
                else
                    throw new IllegalArgumentException(
                        UtilConstants.ERRMSG_FILE_UPDATE_NOT_ALLOWED_1
                            + valueCaption
                            + UtilConstants.ERRMSG_FILE_UPDATE_NOT_ALLOWED_2
//                      + fileNameParm
                            + file.getAbsolutePath()
                            + UtilConstants.ERRMSG_FILE_UPDATE_NOT_ALLOWED_3
                            + UtilConstants.RUNPARM_OPTION_FILE_OUT_UPDATE
                            + UtilConstants.ERRMSG_FILE_UPDATE_NOT_ALLOWED_4);

            if (fileCharsetParm.length() == 0)
                printWriter = new PrintWriter(new BufferedWriter(
                    new FileWriter(file)));
            else
                printWriter = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file),
                        fileCharsetParm)));
        } catch (final Exception e) {
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_FILE_MISC_ERROR_1 + valueCaption
                    + UtilConstants.ERRMSG_FILE_MISC_ERROR_2
//              + fileNameParm
                    + file.getAbsolutePath()
                    + UtilConstants.ERRMSG_FILE_MISC_ERROR_3 + e.getMessage());
        }
        return printWriter;
    }

    /**
     * Construct a Buffered File Reader using RunParm options plus an optional
     * parent directory File object.
     * 
     * @param valueCaption name of RunParm, for error message output.
     * @param fileNameParm RunParmFile line parsed into RunParmArrayEntry.
     * @param parentDirectory the root directory
     * @return BufferedReader file object.
     * @throws IllegalArgumentException if an error occurred
     */
    protected Reader doConstructBufferedFileReader(final String valueCaption,
        final String fileNameParm, final File parentDirectory)
        throws IllegalArgumentException
    {

        Reader bufferedFileReader = null;

        File file = new File(fileNameParm);
        if (parentDirectory == null || file.isAbsolute()) {
            // ok, use as-is
        }
        else
            // fileName relative to parentDirectory.
            file = new File(parentDirectory, fileNameParm);

        try {
            if (!file.exists())
                throw new IllegalArgumentException(
                    UtilConstants.ERRMSG_FILE_NOTFND_1 + valueCaption
                        + UtilConstants.ERRMSG_FILE_NOTFND_2
//                  + fileNameParm
                        + file.getAbsolutePath()
                        + UtilConstants.ERRMSG_FILE_NOTFND_3);
            if (file.isFile() && file.canRead() && !file.isDirectory()) {
                // okey dokey!
            }
            else
                throw new IllegalArgumentException(
                    UtilConstants.ERRMSG_FILE_READ_NOT_ALLOWED_1 + valueCaption
                        + UtilConstants.ERRMSG_FILE_READ_NOT_ALLOWED_2
//                  + fileNameParm
                        + file.getAbsolutePath()
                        + UtilConstants.ERRMSG_FILE_READ_NOT_ALLOWED_3);

            bufferedFileReader = new BufferedReader(new FileReader(file));
        } catch (final Exception e) {
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_FILE_MISC_ERROR_1 + valueCaption
                    + UtilConstants.ERRMSG_FILE_MISC_ERROR_2
//              + fileNameParm
                    + file.getAbsolutePath()
                    + UtilConstants.ERRMSG_FILE_MISC_ERROR_3 + e.getMessage());
        }
        return bufferedFileReader;
    }

    /**
     * Construct a Buffered File Writer using RunParm options plus an optional
     * parent directory File object.
     * 
     * @param valueCaption name of RunParm, for error message output.
     * @param fileNameParm RunParmFile line parsed into RunParmArrayEntry.
     * @param fileUsageParm "new" or "update"
     * @param parentDirectory the root directory
     * @return BufferedWriter file object.
     * @throws IllegalArgumentException if an error occurred
     */
    protected BufferedWriter doConstructBufferedFileWriter(
        final String valueCaption, final String fileNameParm,
        final String fileUsageParm, final File parentDirectory)
        throws IllegalArgumentException
    {

        BufferedWriter bufferedFileWriter = null;
        File file = new File(fileNameParm);
        if (parentDirectory == null || file.isAbsolute()) {
            // ok, use as-is
        }
        else
            // fileName relative to parentDirectory.
            file = new File(parentDirectory, fileNameParm);

        try {
            if (file.exists())
                if (fileUsageParm
                    .compareTo(UtilConstants.RUNPARM_OPTION_FILE_OUT_NEW) == 0)
                    throw new IllegalArgumentException(
                        UtilConstants.ERRMSG_FILE_USAGE_ERR_EXISTS_1
                            + valueCaption
                            + UtilConstants.ERRMSG_FILE_USAGE_ERR_EXISTS_2
//                      + fileNameParm
                            + file.getAbsolutePath()
                            + UtilConstants.ERRMSG_FILE_USAGE_ERR_EXISTS_3
                            + UtilConstants.RUNPARM_OPTION_FILE_OUT_NEW
                            + UtilConstants.ERRMSG_FILE_USAGE_ERR_EXISTS_4);
                else if (file.isFile() && file.canWrite()
                    && !file.isDirectory())
                {
                    // okey dokey!
                }
                else
                    throw new IllegalArgumentException(
                        UtilConstants.ERRMSG_FILE_UPDATE_NOT_ALLOWED_1
                            + valueCaption
                            + UtilConstants.ERRMSG_FILE_UPDATE_NOT_ALLOWED_2
//                      + fileNameParm
                            + file.getAbsolutePath()
                            + UtilConstants.ERRMSG_FILE_UPDATE_NOT_ALLOWED_3
                            + UtilConstants.RUNPARM_OPTION_FILE_OUT_UPDATE
                            + UtilConstants.ERRMSG_FILE_UPDATE_NOT_ALLOWED_4);

            bufferedFileWriter = new BufferedWriter(new FileWriter(file));
        } catch (final Exception e) {
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_FILE_MISC_ERROR_1 + valueCaption
                    + UtilConstants.ERRMSG_FILE_MISC_ERROR_2
//              + fileNameParm
                    + file.getAbsolutePath()
                    + UtilConstants.ERRMSG_FILE_MISC_ERROR_3 + e.getMessage());
        }
        return bufferedFileWriter;
    }

    /**
     * Validate File Name.
     * 
     * @param runParm RunParmFile line.
     * @param valueCaption name of RunParm, for error message output.
     * @param valueFieldNbr number of field in RunParm line.
     * @return String validated file name.
     * @throws IllegalArgumentException if an error occurred
     */
    protected String editFileNameParm(final RunParmArrayEntry runParm,
        final String valueCaption, final int valueFieldNbr)
        throws IllegalArgumentException
    {
        editRunParmValuesLength(runParm, valueCaption, 1);
        final String fileNameParm = runParm.values[valueFieldNbr - 1].trim();
        if (fileNameParm.length() == 0)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_FILE_NAME_BLANK_1 + valueCaption
                    + UtilConstants.ERRMSG_FILE_NAME_BLANK_2 + valueFieldNbr
                    + UtilConstants.ERRMSG_FILE_NAME_BLANK_3);
        return fileNameParm;
    }

    /**
     * Validate File Usage Parm ("new" or "update").
     * 
     * @param runParm RunParmFile line.
     * @param valueCaption name of RunParm, for error message output.
     * @param valueFieldNbr number of field in RunParm line.
     * @return String validated file usage parm.
     * @throws IllegalArgumentException if an error occurred
     */
    protected String editFileUsageParm(final RunParmArrayEntry runParm,
        final String valueCaption, final int valueFieldNbr)
        throws IllegalArgumentException
    {

        if (runParm.values.length < valueFieldNbr)
            return UtilConstants.OPTION_FILE_OUT_USAGE_DEFAULT;

        final String fileUsageParm = runParm.values[valueFieldNbr - 1].trim();
        if (fileUsageParm.length() == 0)
            return UtilConstants.OPTION_FILE_OUT_USAGE_DEFAULT;

        if (fileUsageParm
            .compareToIgnoreCase(UtilConstants.RUNPARM_OPTION_FILE_OUT_NEW) == 0)
            return UtilConstants.RUNPARM_OPTION_FILE_OUT_NEW;

        if (fileUsageParm
            .compareToIgnoreCase(UtilConstants.RUNPARM_OPTION_FILE_OUT_UPDATE) == 0)
            return UtilConstants.RUNPARM_OPTION_FILE_OUT_UPDATE;

        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_FILE_USAGE_PARM_UNRECOG_1 + valueCaption
                + UtilConstants.ERRMSG_FILE_USAGE_PARM_UNRECOG_2
                + valueFieldNbr
                + UtilConstants.ERRMSG_FILE_USAGE_PARM_UNRECOG_3
                + UtilConstants.RUNPARM_OPTION_FILE_OUT_NEW
                + UtilConstants.ERRMSG_FILE_USAGE_PARM_UNRECOG_4
                + UtilConstants.RUNPARM_OPTION_FILE_OUT_UPDATE
                + UtilConstants.ERRMSG_FILE_USAGE_PARM_UNRECOG_5
                + fileUsageParm
                + UtilConstants.ERRMSG_FILE_USAGE_PARM_UNRECOG_6);
    }

    /**
     * Validate File Charset Parm ("" or "UTF-8", etc).
     * 
     * @param runParm RunParmFile line.
     * @param valueCaption name of RunParm, for error message output.
     * @param valueFieldNbr number of field in RunParm line.
     * @return String validated file usage parm.
     * @throws IllegalArgumentException if an error occurred
     */
    protected String editFileCharsetParm(final RunParmArrayEntry runParm,
        final String valueCaption, final int valueFieldNbr)
        throws IllegalArgumentException
    {

        if (runParm.values.length < valueFieldNbr)
            return "";

        final String fileCharsetParm = runParm.values[valueFieldNbr - 1].trim();
        if (fileCharsetParm.length() == 0)
            return "";

        boolean isSupported;
        try {
            isSupported = Charset.isSupported(fileCharsetParm);
        } catch (final IllegalCharsetNameException e) {
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_FILE_CHARSET_INVALID_1 + valueCaption
                    + UtilConstants.ERRMSG_FILE_CHARSET_INVALID_2
                    + valueFieldNbr
                    + UtilConstants.ERRMSG_FILE_CHARSET_INVALID_3
                    + fileCharsetParm
                    + UtilConstants.ERRMSG_FILE_CHARSET_INVALID_4
                    + e.getMessage());
        }

        if (!isSupported)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_FILE_CHARSET_UNSUPPORTED_1 + valueCaption
                    + UtilConstants.ERRMSG_FILE_CHARSET_UNSUPPORTED_2
                    + valueFieldNbr
                    + UtilConstants.ERRMSG_FILE_CHARSET_UNSUPPORTED_3
                    + fileCharsetParm
                    + UtilConstants.ERRMSG_FILE_CHARSET_UNSUPPORTED_4);
        return fileCharsetParm;
    }

    /**
     * Get SelectorAll RunParm Option if present or null.
     * <p>
     * If "*" input returns true Boolean value otherwise, null;
     * 
     * @param runParm RunParmFile line.
     * @param valueCaption name of RunParm, for error message output.
     * @param valueFieldNbr number of field in RunParm line.
     * @return boolean true(yes) or false(no)
     */
    protected Boolean getSelectorAllRunParmOption(
        final RunParmArrayEntry runParm, final String valueCaption,
        final int valueFieldNbr)
    {

        if (runParm.values.length >= valueFieldNbr
            && runParm.values[valueFieldNbr - 1].trim().equals(
                UtilConstants.RUNPARM_OPTION_VALUE_ALL))
            return Boolean.valueOf(true);
        else
            return null;
    }

    /**
     * Get SelectorCount RunParm Option if present or null.
     * <p>
     * If positive integer input returns Integer value, otherwise, if negative
     * or zero integer, throws an exception. If none of the above returns null;
     * 
     * @param runParm RunParmFile line.
     * @param valueCaption name of RunParm, for error message output.
     * @param valueFieldNbr number of field in RunParm line.
     * @return boolean true(yes) or false(no)
     * @throws IllegalArgumentException if an error occurred
     */
    protected Integer getSelectorCountRunParmOption(
        final RunParmArrayEntry runParm, final String valueCaption,
        final int valueFieldNbr) throws IllegalArgumentException
    {

        Integer count = null;
        if (runParm.values.length >= valueFieldNbr)
            try {
                // NumberFormatException if not integer
                count = Integer.valueOf(runParm.values[valueFieldNbr - 1]
                    .trim());
                // IllegalArgumentException if not > 0
                count = Integer.valueOf(editRunParmValueReqPosInt(runParm,
                    valueCaption, valueFieldNbr));
            } catch (final NumberFormatException e) {
                count = null;
            }
        return count;
    }

    /**
     * Get SelectorTheorem RunParm Option if present or null.
     * <p>
     * If present, and not a valid Theorem label, an IllegalArgumentException is
     * thrown.
     * 
     * @param runParm RunParmFile line.
     * @param valueCaption name of RunParm, for error message output.
     * @param valueFieldNbr number of field in RunParm line.
     * @param stmtTbl stmtTbl map from LogicalSystem.
     * @return Theorem or null
     * @throws IllegalArgumentException if an error occurred
     */
    protected Theorem getSelectorTheoremRunParmOption(
        final RunParmArrayEntry runParm, final String valueCaption,
        final int valueFieldNbr, final Map<String, Stmt> stmtTbl)
        throws IllegalArgumentException
    {

        Object mapValue;
        String label;
        if (runParm.values.length >= valueFieldNbr) {
            label = runParm.values[valueFieldNbr - 1].trim();
            mapValue = stmtTbl.get(label);
            if (mapValue == null)
                throw new IllegalArgumentException(
                    UtilConstants.ERRMSG_SELECTOR_NOT_A_STMT_1 + valueCaption
                        + UtilConstants.ERRMSG_SELECTOR_NOT_A_STMT_2
                        + valueFieldNbr
                        + UtilConstants.ERRMSG_SELECTOR_NOT_A_STMT_3 + label
                        + UtilConstants.ERRMSG_SELECTOR_NOT_A_STMT_4);
            if (mapValue instanceof Theorem)
                return (Theorem)mapValue;
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_SELECTOR_NOT_A_THEOREM_1 + valueCaption
                    + UtilConstants.ERRMSG_SELECTOR_NOT_A_THEOREM_2
                    + valueFieldNbr
                    + UtilConstants.ERRMSG_SELECTOR_NOT_A_THEOREM_3 + label
                    + UtilConstants.ERRMSG_SELECTOR_NOT_A_THEOREM_4);
        }
        return null;
    }

    /**
     * Validate Required Yes/No Parm.
     * 
     * @param runParm RunParmFile line.
     * @param valueCaption name of RunParm, for error message output.
     * @param valueFieldNbr number of field in RunParm line.
     * @return boolean true(yes) or false(no)
     * @throws IllegalArgumentException if an error occurred
     */
    protected boolean editYesNoRunParm(final RunParmArrayEntry runParm,
        final String valueCaption, final int valueFieldNbr)
        throws IllegalArgumentException
    {

        editRunParmValuesLength(runParm, valueCaption, valueFieldNbr);
        boolean yesNoBoolean;
        final String yesNoParm = runParm.values[valueFieldNbr - 1]
            .toLowerCase().trim();
        if (yesNoParm.equals(UtilConstants.RUNPARM_OPTION_YES)
            || yesNoParm.equals(UtilConstants.RUNPARM_OPTION_YES_ABBREVIATED))
            yesNoBoolean = true;
        else if (yesNoParm.equals(UtilConstants.RUNPARM_OPTION_NO)
            || yesNoParm.equals(UtilConstants.RUNPARM_OPTION_NO_ABBREVIATED))
            yesNoBoolean = false;
        else
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RECHECK_PA_1 + valueCaption
                    + UtilConstants.ERRMSG_RECHECK_PA_2);
        return yesNoBoolean;
    }

    /**
     * Validate Required On/Off Parm.
     * 
     * @param runParm RunParmFile line.
     * @param valueCaption name of RunParm, for error message output.
     * @param valueFieldNbr number of field in RunParm line.
     * @return boolean true(yes) or false(no)
     * @throws IllegalArgumentException if an error occurred
     */
    protected boolean editOnOffRunParm(final RunParmArrayEntry runParm,
        final String valueCaption, final int valueFieldNbr)
        throws IllegalArgumentException
    {

        editRunParmValuesLength(runParm, valueCaption, valueFieldNbr);
        boolean onOffBoolean;
        final String onOffParm = runParm.values[valueFieldNbr - 1]
            .toLowerCase().trim();
        if (onOffParm.equals(UtilConstants.RUNPARM_OPTION_ON))
            onOffBoolean = true;
        else if (onOffParm.equals(UtilConstants.RUNPARM_OPTION_OFF))
            onOffBoolean = false;
        else
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_BAD_ON_OFF_PARM_1 + valueCaption
                    + UtilConstants.ERRMSG_BAD_ON_OFF_PARM_2);
        return onOffBoolean;
    }

    /**
     * Validate Required, RGB Color Parms
     * 
     * @param runParm RunParmFile line.
     * @param valueCaption name of RunParm, for error message output.
     * @return int positive integer.
     * @throws IllegalArgumentException if an error occurred
     */
    protected Color editRunParmValueReqRGBColor(
        final RunParmArrayEntry runParm, final String valueCaption)
        throws IllegalArgumentException
    {

        editRunParmValuesLength(runParm, valueCaption,
            UtilConstants.RUNPARM_NBR_RGB_COLOR_VALUES);

        final int[] rgb = new int[UtilConstants.RUNPARM_NBR_RGB_COLOR_VALUES];

        for (int valueFieldNbr = 0; valueFieldNbr < UtilConstants.RUNPARM_NBR_RGB_COLOR_VALUES; valueFieldNbr++)
        {

            rgb[valueFieldNbr] = editRunParmValueInteger(
                runParm.values[valueFieldNbr], valueCaption);

            if (rgb[valueFieldNbr] < UtilConstants.RUNPARM_OPTION_MIN_RGB_COLOR
                || rgb[valueFieldNbr] > UtilConstants.RUNPARM_OPTION_MAX_RGB_COLOR)
                throw new IllegalArgumentException(
                    UtilConstants.ERRMSG_RUNPARM_RGB_RANGE_1 + valueCaption
                        + UtilConstants.ERRMSG_RUNPARM_RGB_RANGE_2
                        + UtilConstants.RUNPARM_OPTION_MIN_RGB_COLOR
                        + UtilConstants.ERRMSG_RUNPARM_RGB_RANGE_3
                        + UtilConstants.RUNPARM_OPTION_MAX_RGB_COLOR
                        + UtilConstants.ERRMSG_RUNPARM_RGB_RANGE_4
                        + Integer.toString(rgb[valueFieldNbr]));
        }

        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * Validate Required, Positive Integer Parm.
     * 
     * @param runParm RunParmFile line.
     * @param valueCaption name of RunParm, for error message output.
     * @param valueFieldNbr number of field in RunParm line.
     * @return int positive integer.
     * @throws IllegalArgumentException if an error occurred
     */
    protected int editRunParmValueReqPosInt(final RunParmArrayEntry runParm,
        final String valueCaption, final int valueFieldNbr)
        throws IllegalArgumentException
    {

        editRunParmValuesLength(runParm, valueCaption, valueFieldNbr);
        final Integer i = editRunParmValueInteger(
            runParm.values[valueFieldNbr - 1], valueCaption);
        return editRunParmPositiveInteger(i, valueCaption);
    }

    /**
     * Validate Required, Non-negative Integer Parm.
     * 
     * @param runParm RunParmFile line.
     * @param valueCaption name of RunParm, for error message output.
     * @param valueFieldNbr number of field in RunParm line.
     * @return int positive integer.
     * @throws IllegalArgumentException if an error occurred
     */
    protected int editRunParmValueReqNonNegativeInt(
        final RunParmArrayEntry runParm, final String valueCaption,
        final int valueFieldNbr) throws IllegalArgumentException
    {

        editRunParmValuesLength(runParm, valueCaption, valueFieldNbr);
        final Integer i = editRunParmValueInteger(
            runParm.values[valueFieldNbr - 1], valueCaption);
        return editRunParmNonNegativeInteger(i, valueCaption);
    }

    /**
     * Validate Required Integer Parm.
     * 
     * @param runParm RunParmFile line.
     * @param valueCaption name of RunParm, for error message output.
     * @param valueFieldNbr number of field in RunParm line.
     * @return int integer.
     * @throws IllegalArgumentException if an error occurred
     */
    protected int editRunParmValueReqInt(final RunParmArrayEntry runParm,
        final String valueCaption, final int valueFieldNbr)
        throws IllegalArgumentException
    {

        editRunParmValuesLength(runParm, valueCaption, valueFieldNbr);
        final Integer i = editRunParmValueInteger(
            runParm.values[valueFieldNbr - 1], valueCaption);
        return i;
    }

    /**
     * Validate Required Number of RunParm fields.
     * 
     * @param runParm RunParmFile line.
     * @param valueCaption name of RunParm, for error message output.
     * @param requiredNbrValueFields required number of fields in the RunParm
     *            line.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editRunParmValuesLength(final RunParmArrayEntry runParm,
        final String valueCaption, final int requiredNbrValueFields)
        throws IllegalArgumentException
    {

        if (runParm.values.length < requiredNbrValueFields)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_NOT_ENOUGH_FIELDS_1 + valueCaption
                    + UtilConstants.ERRMSG_RUNPARM_NOT_ENOUGH_FIELDS_2
                    + requiredNbrValueFields
                    + UtilConstants.ERRMSG_RUNPARM_NOT_ENOUGH_FIELDS_3);
    }

    /**
     * Validate Integer Parm.
     * 
     * @param integerString String supposedly containing a number.
     * @param valueCaption name of RunParm, for error message output.
     * @return int an integer.
     * @throws IllegalArgumentException if an error occurred
     */
    protected Integer editRunParmValueInteger(final String integerString,
        final String valueCaption) throws IllegalArgumentException
    {

        Integer i = null;
        try {
            i = Integer.valueOf(integerString.trim());
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_NBR_FORMAT_ERROR_1 + valueCaption
                    + UtilConstants.ERRMSG_RUNPARM_NBR_FORMAT_ERROR_2
                    + e.getMessage());
        }
        return i;
    }

    /**
     * Validate Positive Integer Parm.
     * 
     * @param i an integer, supposedly positive.
     * @param valueCaption name of RunParm, for error message output.
     * @return int a positive integer.
     * @throws IllegalArgumentException if an error occurred
     */
    protected int editRunParmPositiveInteger(final Integer i,
        final String valueCaption) throws IllegalArgumentException
    {
        final int n = i.intValue();
        if (n <= 0)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_NBR_LE_ZERO_1 + valueCaption
                    + UtilConstants.ERRMSG_RUNPARM_NBR_LE_ZERO_2 + i.toString());
        return n;
    }

    /**
     * Validate Non-Negative Integer Parm.
     * 
     * @param i an integer, supposedly greater than or equal to zero.
     * @param valueCaption name of RunParm, for error message output.
     * @return int a positive integer.
     * @throws IllegalArgumentException if an error occurred
     */
    protected int editRunParmNonNegativeInteger(final Integer i,
        final String valueCaption) throws IllegalArgumentException
    {
        final int n = i.intValue();
        if (n < 0)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_NBR_LT_ZERO_1 + valueCaption
                    + UtilConstants.ERRMSG_RUNPARM_NBR_LT_ZERO_2 + i.toString());
        return n;
    }

    /**
     * Validate RunParm Theorem Label String.
     * 
     * @param stmtLabel String, supposedly a Theorem label.
     * @param valueCaption name of RunParm, for error message output.
     * @param logicalSystem Uh-oh, Mr. Big. Heavy validation using
     *            LogicalSystem.stmtTbl.
     * @return Theorem if stmtLabel is valid.
     * @throws IllegalArgumentException if an error occurred
     */
    protected Theorem editRunParmValueTheorem(final String stmtLabel,
        final String valueCaption, final LogicalSystem logicalSystem)
        throws IllegalArgumentException
    {
        final Stmt stmt = editRunParmValueStmt(stmtLabel, valueCaption,
            logicalSystem);
        if (stmt instanceof Theorem)
            return (Theorem)stmt;
        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_RUNPARM_STMT_NOT_THEOREM_1 + valueCaption
                + UtilConstants.ERRMSG_RUNPARM_STMT_NOT_THEOREM_2 + stmtLabel
                + UtilConstants.ERRMSG_RUNPARM_STMT_NOT_THEOREM_3);
    }

    /**
     * Validate RunParm Statement Label String.
     * 
     * @param stmtLabel String, supposedly a Stmt label.
     * @param valueCaption name of RunParm, for error message output.
     * @param logicalSystem Uh-oh, Mr. Big. Heavy validation using
     *            LogicalSystem.stmtTbl.
     * @return Stmt if stmtLabel is valid.
     * @throws IllegalArgumentException if an error occurred
     */
    protected Stmt editRunParmValueStmt(final String stmtLabel,
        final String valueCaption, final LogicalSystem logicalSystem)
        throws IllegalArgumentException
    {
        if (stmtLabel.length() == 0)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_STMT_LABEL_BLANK_1 + valueCaption
                    + UtilConstants.ERRMSG_RUNPARM_STMT_LABEL_BLANK_2);

        final Stmt stmt = logicalSystem.getStmtTbl().get(stmtLabel);
        if (stmt == null)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_STMT_LABEL_NOTFND_1 + valueCaption
                    + UtilConstants.ERRMSG_RUNPARM_STMT_LABEL_NOTFND_2
                    + stmtLabel
                    + UtilConstants.ERRMSG_RUNPARM_STMT_LABEL_NOTFND_3);
        return stmt;
    }

    /**
     * Validate RunParm String with length greater than zero and no embedded
     * blanks or unprintable characters.
     * 
     * @param runParm RunParmFile line.
     * @param valueCaption name of RunParm, for error message output.
     * @param valueFieldNbr required number of fields in the RunParm line.
     * @return String if valid.
     * @throws IllegalArgumentException if an error occurred
     */
    protected String editRunParmPrintableNoBlanksString(
        final RunParmArrayEntry runParm, final String valueCaption,
        final int valueFieldNbr) throws IllegalArgumentException
    {

        editRunParmValuesLength(runParm, valueCaption, valueFieldNbr);

        final String printableNoBlanksString = runParm.values[valueFieldNbr - 1]
            .trim();

        boolean err = true;
        char c;
        if (printableNoBlanksString.length() > 0) {
            err = false;
            for (int i = 0; i < printableNoBlanksString.length(); i++) {

                c = printableNoBlanksString.charAt(i);
                if (c > 127 || Character.isWhitespace(c)
                    || Character.isISOControl(c))
                {
                    err = true;
                    break;
                }
            }
        }

        if (err)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_NONBLANK_PRINT_STR_BAD_1
                    + valueCaption
                    + UtilConstants.ERRMSG_RUNPARM_NONBLANK_PRINT_STR_BAD_2);
        return printableNoBlanksString;
    }
}
