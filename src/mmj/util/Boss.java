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

import static mmj.util.UtilConstants.*;

import java.awt.Color;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

import mmj.lang.*;
import mmj.pa.*;
import mmj.util.UtilConstants.RunParmContext;

/**
 * Boss is the superclass of GrammarBoss, LogicalSystemBoss, etc, which are used
 * by BatchFramework to "exercise" mmj2 in batch mode.
 * <p>
 * Boss consists of the abstract "doRunParmCommand" and some common parameter
 * validation functions used in sub-classes of Boss.
 */
public abstract class Boss {

    protected BatchFramework batchFramework;

    private final Map<BatchCommand, BooleanSupplier> ops = new HashMap<>();

    protected RunParmArrayEntry runParm;

    /**
     * Constructor with BatchFramework for access to environment.
     *
     * @param batchFramework for access to environment.
     */
    public Boss(final BatchFramework batchFramework) {
        this.batchFramework = batchFramework;
        batchFramework.bossList.add(this);
    }

    protected void putCommand(final BatchCommand cmd,
        final BooleanSupplier op)
    {
        ops.put(cmd, op);
    }

    protected void putCommand(final BatchCommand cmd, final Runnable op) {
        putCommand(cmd, () -> {
            if (op != null)
                op.run();
            return true;
        });
    }

    /**
     * Executes a single command from the RunParmFile.
     *
     * @param curRunParm the RunParmFile line to execute.
     * @return {@code true} if the RunParm was "consumed"
     */
    public boolean doRunParmCommand(final RunParmArrayEntry curRunParm) {
        runParm = curRunParm;
        final BooleanSupplier op = ops.get(curRunParm.cmd);
        return op != null && op.getAsBoolean();
    }

    // =======================================================
    // === bazillions of subroutines used by Boss subclasses
    // =======================================================

    /**
     * Validate Required Number of RunParm fields.
     *
     * @param n required number of fields in the RunParm line.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void require(final int n) {
        if (runParm.values.length < n)
            throw error(ERRMSG_RUNPARM_NOT_ENOUGH_FIELDS, runParm.cmd, n);
    }

    /**
     * Get a RunParm field.
     *
     * @param valueFieldNbr the index of the value field, starting at 1
     * @return The n-th element of the value field
     */
    public String get(final int valueFieldNbr) {
        require(valueFieldNbr);
        return runParm.values[valueFieldNbr - 1].trim();
    }

    /**
     * Get a non-blank RunParm field.
     *
     * @param valueFieldNbr the index of the value field, starting at 1
     * @param code The error message to give if the value is blank
     * @return The n-th element of the value field
     */
    public String getNonBlank(final int valueFieldNbr, final ErrorCode code) {
        final String s = get(valueFieldNbr);
        if (s.isEmpty())
            throw error(code, valueFieldNbr);
        return s;
    }

    /**
     * Get a RunParm field.
     *
     * @param valueFieldNbr the index of the value field, starting at 1
     * @return The n-th element of the value field
     */
    public String opt(final int valueFieldNbr) {
        if (runParm.values.length > valueFieldNbr)
            return null;
        final String s = get(valueFieldNbr);
        return s.isEmpty() ? null : s;
    }

    /**
     * Validate existing folder RunParm (must exist!)
     *
     * @param filePath path used to resolve file name. May be null or absolute
     *            or relative path.
     * @param valueFieldNbr number of field in RunParm line.
     * @return File object for folder
     */
    protected File getExistingFolder(final File filePath,
        final int valueFieldNbr)
    {
        return getExistingFolder(filePath,
            getNonBlank(valueFieldNbr, ERRMSG_FOLDER_NAME_BLANK));
    }

    /**
     * Validate existing folder RunParm (must exist!)
     *
     * @param filePath path used to resolve file name. May be null or absolute
     *            or relative path.
     * @param folderNameParm The folder name
     * @return File object for folder
     */
    protected File getExistingFolder(final File filePath,
        final String folderNameParm)
    {
        File folder = new File(folderNameParm);
        try {
            if (filePath == null || folder.isAbsolute()) {}
            else
                folder = new File(filePath, folderNameParm);
            if (!folder.exists())
                throw error(ERRMSG_FOLDER_NOTFND, folder.getAbsolutePath());
            if (!folder.isDirectory())
                throw error(ERRMSG_NOT_A_FOLDER, folder.getAbsolutePath());

        } catch (final Exception e) {
            throw error(ERRMSG_FOLDER_MISC_ERROR, folder.getAbsolutePath(),
                e.getMessage());
        }
        return folder;
    }

    /**
     * Validate existing folder RunParm (must exist!)
     *
     * @param filePath path name for building files. May be null, relative or
     *            absolute.
     * @param valueFieldNbr number of field in RunParm line.
     * @return File object for file.
     */
    protected File getExistingFile(final File filePath,
        final int valueFieldNbr)
    {
        return getExistingFile(filePath, getFileName(valueFieldNbr));
    }

    /**
     * Validate Proof Worksheet File Name Suffix
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @return String validated file name suffix
     */
    protected String getFileNameSuffix(final int valueFieldNbr) {
        final String fileNameSuffixParm = get(valueFieldNbr);
        if (fileNameSuffixParm
            .equalsIgnoreCase(PaConstants.PA_GUI_FILE_CHOOSER_FILE_SUFFIX_TXT)
            || fileNameSuffixParm.equalsIgnoreCase(
                PaConstants.PA_GUI_FILE_CHOOSER_FILE_SUFFIX_MMP))
            return fileNameSuffixParm;

        throw error(ERRMSG_BAD_FILE_NAME_SUFFIX, valueFieldNbr);
    }

    /**
     * Build a File object for an existing File Name
     *
     * @param filePath path name for building files. May be null, relative or
     *            absolute.
     * @param fileNameParm name of file
     * @return File File object for file.
     * @throws IllegalArgumentException if an error occurred
     */
    protected File getExistingFile(final File filePath,
        final String fileNameParm)
    {

        File file = new File(fileNameParm);
        try {
            if (filePath != null && !file.isAbsolute())
                file = new File(filePath, fileNameParm);
            if (!file.exists())
                throw error(ERRMSG_FILE_NOTFND, file.getAbsolutePath());
            else if (file.isDirectory())
                throw error(ERRMSG_NOT_A_FILE, file.getAbsolutePath());
        } catch (final Exception e) {
            throw error(ERRMSG_FILE_MISC_ERROR, e, file.getAbsolutePath(),
                e.getMessage());
        }
        return file;
    }

    /**
     * Validate PrintWriter RunParm and its options.
     *
     * @param filePath path for building files. May be null, absolute, or
     *            relative.
     * @return PrintWriter object.
     * @throws IllegalArgumentException if an error occurred
     */
    protected PrintWriter getPrintWriter(final File filePath) {

        final String fileNameParm = getFileName(1);

        final String fileUsageParm = getFileUsage(2);

        final String fileCharsetParm = getFileCharset(3);

        return buildPrintWriter(filePath, fileNameParm, fileUsageParm,
            fileCharsetParm);
    }

    /**
     * Construct a PrintWriter using RunParm options.
     *
     * @param filePath path for building files. May be null, absolute or
     *            relative.
     * @param fileNameParm RunParmFile line parsed into RunParmArrayEntry.
     * @param fileUsageParm "new" or "update"
     * @param fileCharsetParm optional, "UTF-8", etc.
     * @return PrintWriter object.
     * @throws IllegalArgumentException if an error occurred
     */
    protected PrintWriter buildPrintWriter(final File filePath,
        final String fileNameParm, final String fileUsageParm,
        final String fileCharsetParm)
    {

        PrintWriter printWriter = null;
        File file = new File(fileNameParm);
        try {
            if (filePath == null || file.isAbsolute()) {}
            else
                file = new File(filePath, fileNameParm);
            if (file.exists())
                if (fileUsageParm.compareTo(RUNPARM_OPTION_FILE_OUT_NEW) == 0)
                    throw error(ERRMSG_FILE_USAGE_ERR_EXISTS,
                        file.getAbsolutePath(), RUNPARM_OPTION_FILE_OUT_NEW);
                else if (!file.isFile() || !file.canWrite())
                    throw error(ERRMSG_FILE_UPDATE_NOT_ALLOWED,
                        file.getAbsolutePath(), RUNPARM_OPTION_FILE_OUT_UPDATE);

            if (fileCharsetParm.isEmpty())
                printWriter = new PrintWriter(
                    new BufferedWriter(new FileWriter(file)));
            else
                printWriter = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(file), fileCharsetParm)));
        } catch (final Exception e) {
            throw error(ERRMSG_FILE_MISC_ERROR, e, file.getAbsolutePath(),
                e.getMessage());
        }
        return printWriter;
    }

    /**
     * Construct a Buffered File Reader using RunParm options plus an optional
     * parent directory File object.
     *
     * @param fileNameParm RunParmFile line parsed into RunParmArrayEntry.
     * @param parentDirectory the root directory
     * @return BufferedReader file object.
     * @throws IllegalArgumentException if an error occurred
     */
    protected Reader buildBufferedFileReader(final String fileNameParm,
        final File parentDirectory)
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
                throw error(ERRMSG_FILE_NOTFND, file.getAbsolutePath());
            if (!file.isFile() || !file.canRead())
                throw error(ERRMSG_FILE_READ_NOT_ALLOWED,
                    file.getAbsolutePath());
            bufferedFileReader = new BufferedReader(new FileReader(file));
        } catch (final Exception e) {
            throw error(ERRMSG_FILE_MISC_ERROR, e, file.getAbsolutePath(),
                e.getMessage());
        }
        return bufferedFileReader;
    }

    /**
     * Construct a Buffered File Writer using RunParm options plus an optional
     * parent directory File object.
     *
     * @param fileNameParm RunParmFile line parsed into RunParmArrayEntry.
     * @param fileUsageParm "new" or "update"
     * @param parentDirectory the root directory
     * @return BufferedWriter file object.
     * @throws IllegalArgumentException if an error occurred
     */
    protected BufferedWriter buildBufferedFileWriter(final String fileNameParm,
        final String fileUsageParm, final File parentDirectory)
    {

        BufferedWriter bufferedFileWriter = null;
        File file = new File(fileNameParm);
        if (parentDirectory != null && !file.isAbsolute())
            // fileName relative to parentDirectory.
            file = new File(parentDirectory, fileNameParm);
        try {
            if (file.exists())
                if (fileUsageParm.equals(RUNPARM_OPTION_FILE_OUT_NEW))
                    throw error(ERRMSG_FILE_USAGE_ERR_EXISTS,
                        file.getAbsolutePath(), RUNPARM_OPTION_FILE_OUT_NEW);
                else if (!file.isFile() || !file.canWrite())
                    throw error(ERRMSG_FILE_UPDATE_NOT_ALLOWED,
                        file.getAbsolutePath(), RUNPARM_OPTION_FILE_OUT_UPDATE);

            bufferedFileWriter = new BufferedWriter(new FileWriter(file));
        } catch (final Exception e) {
            throw error(ERRMSG_FILE_MISC_ERROR, e, file.getAbsolutePath(),
                e.getMessage());
        }
        return bufferedFileWriter;
    }

    /**
     * Validate File Name.
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @return String validated file name.
     * @throws IllegalArgumentException if an error occurred
     */
    protected String getFileName(final int valueFieldNbr) {
        return getNonBlank(valueFieldNbr, ERRMSG_FILE_NAME_BLANK);
    }

    /**
     * Validate File Usage Parm ("new" or "update").
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @return String validated file usage parm.
     * @throws IllegalArgumentException if an error occurred
     */
    protected String getFileUsage(final int valueFieldNbr) {

        if (runParm.values.length < valueFieldNbr)
            return OPTION_FILE_OUT_USAGE_DEFAULT;

        final String fileUsageParm = get(valueFieldNbr);
        if (fileUsageParm.length() == 0)
            return OPTION_FILE_OUT_USAGE_DEFAULT;

        if (fileUsageParm.equalsIgnoreCase(RUNPARM_OPTION_FILE_OUT_NEW))
            return RUNPARM_OPTION_FILE_OUT_NEW;

        if (fileUsageParm.equalsIgnoreCase(RUNPARM_OPTION_FILE_OUT_UPDATE))
            return RUNPARM_OPTION_FILE_OUT_UPDATE;

        throw error(ERRMSG_FILE_USAGE_PARM_UNRECOG, valueFieldNbr,
            RUNPARM_OPTION_FILE_OUT_NEW, RUNPARM_OPTION_FILE_OUT_UPDATE,
            fileUsageParm);
    }

    /**
     * Validate File Charset Parm ("" or "UTF-8", etc).
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @return String validated file usage parm.
     * @throws IllegalArgumentException if an error occurred
     */
    protected String getFileCharset(final int valueFieldNbr) {

        if (runParm.values.length < valueFieldNbr)
            return "";

        final String fileCharsetParm = get(valueFieldNbr);
        if (fileCharsetParm.isEmpty())
            return "";

        boolean isSupported;
        try {
            isSupported = Charset.isSupported(fileCharsetParm);
        } catch (final IllegalCharsetNameException e) {
            throw error(ERRMSG_FILE_CHARSET_INVALID, e, valueFieldNbr,
                fileCharsetParm, e.getMessage());
        }

        if (!isSupported)
            throw error(ERRMSG_FILE_CHARSET_UNSUPPORTED, valueFieldNbr,
                fileCharsetParm);
        return fileCharsetParm;
    }

    /**
     * Get SelectorCount RunParm Option.
     * <p>
     * If "*" input returns {@link Integer#MAX_VALUE}, if positive integer input
     * returns integer value, otherwise, if negative or zero integer, throws an
     * exception. If none of the above returns 0.
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @return 0 for non-integer values, the number if integer
     */
    protected int getSelectorCount(final int valueFieldNbr) {
        final String s = get(valueFieldNbr);
        if (s.equals(RUNPARM_OPTION_VALUE_ALL))
            return Integer.MAX_VALUE;
        try {
            // NumberFormatException if not integer
            Integer.parseInt(s);
            // IllegalArgumentException if not > 0
            return getPosInt(valueFieldNbr);
        } catch (final NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Get SelectorTheorem RunParm Option if present or null.
     * <p>
     * If present, and not a valid Theorem label, an IllegalArgumentException is
     * thrown.
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @param stmtTbl stmtTbl map from LogicalSystem.
     * @return Theorem or null
     * @throws IllegalArgumentException if an error occurred
     */
    protected Theorem getSelectorTheorem(final int valueFieldNbr,
        final Map<String, Stmt> stmtTbl)
    {

        final String label = get(valueFieldNbr);
        final Stmt mapValue = stmtTbl.get(label);
        if (mapValue == null)
            throw error(ERRMSG_SELECTOR_NOT_A_STMT, valueFieldNbr, label);
        if (mapValue instanceof Theorem)
            return (Theorem)mapValue;
        throw error(ERRMSG_SELECTOR_NOT_A_THEOREM, valueFieldNbr, label);
    }

    /**
     * Validate Required Yes/No Parm.
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @return boolean true(yes) or false(no)
     * @throws IllegalArgumentException if an error occurred
     */
    protected boolean getYesNo(final int valueFieldNbr) {
        final String yesNoParm = get(valueFieldNbr).toLowerCase();
        if (yesNoParm.equals(RUNPARM_OPTION_YES)
            || yesNoParm.equals(RUNPARM_OPTION_YES_ABBREVIATED))
            return true;
        else if (yesNoParm.equals(RUNPARM_OPTION_NO)
            || yesNoParm.equals(RUNPARM_OPTION_NO_ABBREVIATED))
            return false;
        else
            throw error(ERRMSG_BAD_YES_NO_PARM, yesNoParm);
    }

    /**
     * Validate Required On/Off Parm.
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @return boolean true(yes) or false(no)
     * @throws IllegalArgumentException if an error occurred
     */
    protected boolean getOnOff(final int valueFieldNbr) {

        final String onOffParm = get(valueFieldNbr).toLowerCase();
        if (onOffParm.equals(RUNPARM_OPTION_ON))
            return true;
        else if (onOffParm.equals(RUNPARM_OPTION_OFF))
            return false;
        else
            throw error(ERRMSG_BAD_ON_OFF_PARM, onOffParm);
    }

    /**
     * Validate a boolean value named by the {@code yes} string, with the
     * {@code no} string being {@code No} prepended to the {@code yes} string.
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @param blank Value to return if the field is blank
     * @param yes Value to return true
     * @return boolean The read value
     */
    protected boolean getBoolean(final int valueFieldNbr, final boolean blank,
        final String yes)
    {
        return getBoolean(valueFieldNbr, blank, yes, "No" + yes);
    }

    /**
     * Validate a boolean value named by the {@code yes} and {@code no} strings.
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @param blank Value to return if the field is blank
     * @param yes Value to return true
     * @param no Value to return false
     * @return boolean The read value
     */
    protected boolean getBoolean(final int valueFieldNbr, final boolean blank,
        final String yes, final String no)
    {

        final String printParm = opt(valueFieldNbr);

        if (printParm == null)
            return blank;

        if (printParm.equalsIgnoreCase(yes))
            return true;

        if (printParm.equalsIgnoreCase(no))
            return false;

        throw error(ERRMSG_BOOLEAN_UNRECOG, valueFieldNbr, yes, no, printParm);
    }

    /**
     * Validate Enum Parm.
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @param blank Value to return if blank
     * @param e String to return if the input did not match any values
     * @param <E> The enum type
     * @return The interpreted enum value
     */
    protected <E extends Enum<E>> E getEnum(final int valueFieldNbr,
        final E blank, final MMJException e)
    {
        final String s = opt(valueFieldNbr);

        if (s == null)
            return blank;

        for (final E val : blank.getDeclaringClass().getEnumConstants())
            if (s.equalsIgnoreCase(val.toString()))
                return val;

        throw error(e);
    }

    /**
     * Validate Required, RGB Color Parms
     *
     * @return Color value
     * @throws IllegalArgumentException if an error occurred
     */
    protected Color getColor() {
        return new Color(getInt(1), getInt(2), getInt(3));
    }

    /**
     * Validate Required Integer Parm.
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @return int integer.
     * @throws IllegalArgumentException if an error occurred
     */
    protected int getInt(final int valueFieldNbr) {
        return parseInt(get(valueFieldNbr));
    }

    /**
     * Validate Integer Parm.
     *
     * @param value a string to convert
     * @return int an integer.
     * @throws IllegalArgumentException if an error occurred
     */
    protected int parseInt(final String value) {
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            throw error(ERRMSG_RUNPARM_NBR_FORMAT_ERROR, e.getMessage());
        }
    }

    /**
     * Validate Positive Integer Parm.
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @return int a positive integer.
     * @throws IllegalArgumentException if an error occurred
     */
    protected int getPosInt(final int valueFieldNbr) {
        final int n = getInt(valueFieldNbr);
        if (n <= 0)
            throw error(ERRMSG_RUNPARM_NBR_LE_ZERO, n);
        return n;
    }

    /**
     * Validate Non-Negative Integer Parm.
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @return int a positive integer.
     * @throws IllegalArgumentException if an error occurred
     */
    protected int getNonnegInt(final int valueFieldNbr) {
        final int n = getInt(valueFieldNbr);
        if (n < 0)
            throw error(ERRMSG_RUNPARM_NBR_LT_ZERO, n);
        return n;
    }

    /**
     * Validate RunParm Theorem Label String.
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @param logicalSystem Uh-oh, Mr. Big. Heavy validation using
     *            LogicalSystem.stmtTbl.
     * @return Theorem if stmtLabel is valid.
     * @throws IllegalArgumentException if an error occurred
     */
    protected Theorem getTheorem(final int valueFieldNbr,
        final LogicalSystem logicalSystem)
    {
        final Stmt stmt = getStmt(valueFieldNbr, logicalSystem);
        if (stmt instanceof Theorem)
            return (Theorem)stmt;
        throw error(ERRMSG_RUNPARM_STMT_NOT_THEOREM, stmt);
    }

    /**
     * Validate RunParm Statement Label String.
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @param logicalSystem Uh-oh, Mr. Big. Heavy validation using
     *            LogicalSystem.stmtTbl.
     * @return Stmt if stmtLabel is valid.
     * @throws IllegalArgumentException if an error occurred
     */
    protected Stmt getStmt(final int valueFieldNbr,
        final LogicalSystem logicalSystem)
    {
        final String stmtLabel = getNonBlank(valueFieldNbr,
            ERRMSG_RUNPARM_STMT_LABEL_BLANK);
        final Stmt stmt = logicalSystem.getStmtTbl().get(stmtLabel);
        if (stmt == null)
            throw error(ERRMSG_RUNPARM_STMT_LABEL_NOTFND, stmtLabel);
        return stmt;
    }

    protected IllegalArgumentException error(final ErrorCode code,
        final Object... args)
    {
        return error(null, code, args);
    }

    protected IllegalArgumentException error(final Exception e,
        final ErrorCode code, final Object... args)
    {
        return error(new MMJException(e, code, args));
    }

    protected MMJException addContext(final MMJException e) {
        return RunParmContext
            .addRunParmContext(runParm == null ? null : runParm.cmd, e);
    }

    protected IllegalArgumentException error(final MMJException e) {
        return new IllegalArgumentException(addContext(e));
    }

    protected void accumException(final MMJException e) {
        batchFramework.outputBoss.getMessages().accumException(addContext(e));
    }

    /**
     * Validate RunParm String with length greater than zero and no embedded
     * blanks or unprintable characters.
     *
     * @param valueFieldNbr required number of fields in the RunParm line.
     * @return String if valid.
     * @throws IllegalArgumentException if an error occurred
     */
    protected String getPrintableNoBlanksString(final int valueFieldNbr) {
        final String s = get(valueFieldNbr);

        if (s.isEmpty() || s.chars().anyMatch(c -> c > 127
            || Character.isWhitespace(c) || Character.isISOControl(c)))
            throw error(ERRMSG_RUNPARM_NONBLANK_PRINT_STR_BAD);
        return s;
    }
}
