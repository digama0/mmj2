//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * OutputBoss.java  0.08 11/01/2011
 *
 * 22-Jan-2006 --> added sysOutPrint and sysErrPrint
 *                 for use by Proof Assistant.
 *
 * Version 0.04 Sep-03-2006:
 *     -->Add TMFF stuff
 *
 * Version 0.05 06/01/2007:
 *     -->Add OutputVerbosity RunParm
 *     -->Add StartInstrumentationTimer and
 *        StopInstrumentationTimer.
 *
 * Version 0.06 11/01/2007:
 *     -->Fix bug: MaxErrorMessages and MaxInfoMessages
 *        parms not taking effect correctly!
 *
 * Version 0.07 08/01/2008:
 *     -->Add new Print commands for mmj.lang.BookManager:
 *            - PrintBookManagerChapters
 *            - PrintBookManagerSections
 *            - PrintBookManagerSectionDetails
 *
 * Version 0.08 - Nov-01-2011:
 * ==> mmj2 Paths Enhancement changes
 *     -->Added code for MMJ2FailPopupWindow
 *     -->change editSysErrFile() and
                  editSysOutFile() to pass mmj2Path as filePath
 *       argument to Boss.editPrintWriterRunParm().
 *     -->add setDefaults() routine.
 */

package mmj.util;

import java.io.*;

import mmj.lang.*;
import mmj.mmio.MMIOException;
import mmj.tmff.TMFFPreferences;
import mmj.verify.Grammar;

/**
 * Responsible for managing and using Messages, Dump and writing to
 * sysOut/sysErr.
 * <p>
 * OutputBoss' main responsibility is directing output to the user-designated
 * destination, so it provides its own "print-and-clear-messages" function for
 * the other Boss classes to use.
 * <p>
 * A key point to note is that in BatchMMJ2, Messages are printed and cleared
 * immediately after being generated, they are not accumulated for some later
 * purpose. Therefore, OutputBoss uses messages.reallocateInfoMessages and
 * messages.reallocateErrorMessages when a MaxErrorMessages or MaxInfoMessages
 * runparm is changed. It also uses LangConstants.MAX_ERROR_MESSAGES_DEFAULT and
 * LangConstants.MAX_INFO_MESSAGES_DEFAULT if the relevant runParms are *not*
 * input.
 */
public class OutputBoss extends Boss {

    protected PrintWriter sysOut;
    protected PrintWriter sysErr;

    protected int maxErrorMessagesParm;
    protected int maxInfoMessagesParm;
    protected Messages messages;

    protected int maxStatementPrintCountParm;
    protected String captionParm;
    protected Dump dump;

    protected int outputVerbosityParm;

    /**
     * Constructor with BatchFramework for access to environment.
     * 
     * @param batchFramework for access to environment.
     */
    public OutputBoss(final BatchFramework batchFramework) {
        super(batchFramework);
        setDefaults();
    }

    /**
     * Executes a single command from the RunParmFile.
     * 
     * @param runParm the RunParmFile line to execute.
     */
    @Override
    public boolean doRunParmCommand(final RunParmArrayEntry runParm)
        throws IllegalArgumentException, MMIOException, FileNotFoundException,
        IOException, VerifyException
    {

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_CLEAR.name()) == 0)
        {
            setDefaults();
            return false;
        }

        if (runParm.name.compareToIgnoreCase(UtilConstants.RUNPARM_SYSOUT_FILE
            .name()) == 0)
        {
            editSysOutFile(runParm);
            return true;
        }

        if (runParm.name.compareToIgnoreCase(UtilConstants.RUNPARM_SYSERR_FILE
            .name()) == 0)
        {
            editSysErrFile(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_MAX_ERROR_MESSAGES
                .name()) == 0)
        {
            editMaxErrorMessages(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_MAX_INFO_MESSAGES.name()) == 0)
        {
            editMaxInfoMessages(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_MAX_STATEMENT_PRINT_COUNT
                .name()) == 0)
        {
            editMaxStatementPrintCount(runParm);
            return true;
        }

        if (runParm.name.compareToIgnoreCase(UtilConstants.RUNPARM_CAPTION
            .name()) == 0)
        {
            editCaption(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PRINT_SYNTAX_DETAILS
                .name()) == 0)
        {
            doPrintSyntaxDetails(runParm);
            return true; // "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PRINT_STATEMENT_DETAILS
                .name()) == 0)
        {
            doPrintStatementDetails(runParm);
            return true; // "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_OUTPUT_VERBOSITY.name()) == 0)
        {
            editOutputVerbosity(runParm);
            return true; // "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_START_INSTRUMENTATION_TIMER
                .name()) == 0)
        {
            editStartInstrumentationTimer(runParm);
            return true; // "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_STOP_INSTRUMENTATION_TIMER
                .name()) == 0)
        {
            editStopInstrumentationTimer(runParm);
            return true; // "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PRINT_BOOK_MANAGER_CHAPTERS
                .name()) == 0)
        {
            doPrintBookManagerChapters(runParm);
            return true; // "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PRINT_BOOK_MANAGER_SECTIONS
                .name()) == 0)
        {
            doPrintBookManagerSections(runParm);
            return true; // "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PRINT_BOOK_MANAGER_SECTION_DETAILS
                .name()) == 0)
        {
            doPrintBookManagerSectionDetails(runParm);
            return true; // "consumed"
        }

        return false;
    }

    /**
     * Print all error/info messages, then clear the messages from the Messages
     * repository.
     * <p>
     * Note: startup errors are gathered and displayed here via calls to methods
     * in {@code MMJ2FailPopupWindow}. The gathering and displaying are separate
     * because when the popup window is shown the error messages should already
     * be displayed on the Command Prompt window -- but that process clears the
     * messages from the {@code Messages} object. So the messages are
     * accumulated by {@code MMJ2FailPopupWindow} before being displayed on the
     * Command Prompt window...
     */
    public void printAndClearMessages() {

        batchFramework.mmj2FailPopupWindow.accumStartupErrors();

        if (sysOut == null)
            getMessages().printAndClearMessages();
        else
            getMessages().writeAndClearMessages(sysOut);

        batchFramework.mmj2FailPopupWindow.displayStartupErrors();
    }

    /**
     * Get a Messages object.
     * 
     * @return Messages object, ready to go.
     */
    public Messages getMessages() {
        if (messages == null)
            initializeMessages();
        return messages;
    }

    /**
     * Common routine for printing a line to SysOut if the input verbosity
     * number is less than or equal to the OutputVerbosity RunParm
     * 
     * @param s line to print.
     * @param v verbosity of line to print.
     * @throws IOException if an error occurred
     */
    public void sysOutPrintln(final String s, final int v) throws IOException {
        if (v > outputVerbosityParm) {}
        else
            sysOutPrintln(s);

    }

    /**
     * Common routine for printing a line to SysOut.
     * 
     * @param s line to print.
     * @throws IOException if an error occurred
     */
    public void sysOutPrintln(final String s) throws IOException {
        if (sysOut == null)
            System.out.println(s);
        else {
            sysOut.println(s);
            checkSysOutError();
        }
    }

    /**
     * Common routine for printing to SysOut. if the input verbosity number is
     * less than or equal to the OutputVerbosity RunParm
     * 
     * @param s string to print.
     * @param v verbosity of string to print.
     * @throws IOException if an error occurred
     */
    public void sysOutPrint(final String s, final int v) throws IOException {

        if (v > outputVerbosityParm) {}
        else
            sysOutPrint(s);
    }

    /**
     * Common routine for printing to SysOut.
     * 
     * @param s string to print.
     * @throws IOException if an error occurred
     */
    public void sysOutPrint(final String s) throws IOException {
        if (sysOut == null)
            System.out.print(s);
        else {
            sysOut.print(s);
            checkSysOutError();
        }
    }

    /**
     * Common routine for printing a line to SysErr.
     * 
     * @param s line to print.
     * @throws IOException if an error occurred
     */
    public void sysErrPrintln(final String s) throws IOException {
        if (sysErr == null)
            System.err.println(s);
        else {
            sysErr.println(s);
            checkSysErrError();
        }
    }

    /**
     * Common routine for printing to SysErr.
     * 
     * @param s String to print.
     * @throws IOException if an error occurred
     */
    public void sysErrPrint(final String s) throws IOException {
        if (sysErr == null)
            System.err.print(s);
        else {
            sysErr.print(s);
            checkSysErrError();
        }
    }

    /**
     * Close SysOut and SysErr.
     */
    public void close() {
        closeSysOut();
        closeSysErr();
    }

    /**
     * Executes the PrintSyntaxDetails command, prints any messages, etc.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     */
    public void doPrintSyntaxDetails(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        final Grammar grammar = batchFramework.grammarBoss.getGrammar();

        final Dump d = getDump();
        d.printSyntaxDetails(getCaption(), logicalSystem, grammar);

    }

    /**
     * Executes the PrintStatementDetails command, prints any messages, etc.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     */
    public void doPrintStatementDetails(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        final Dump d = getDump();

        final String optionValue = runParm.values[0].trim();
        if (optionValue.compareTo(UtilConstants.RUNPARM_OPTION_VALUE_ALL) == 0)
        {
            int n = maxStatementPrintCountParm;
            if (n <= 0)
                n = UtilConstants.MAX_STATEMENT_PRINT_COUNT_DEFAULT;
            d.printStatementDetails(getCaption(), logicalSystem.getStmtTbl(), n);
        }
        else {
            final Stmt stmt = editRunParmValueStmt(optionValue,
                UtilConstants.RUNPARM_PRINT_STATEMENT_DETAILS.name(),
                logicalSystem);
            d.printOneStatementDetails(stmt);
        }
        printAndClearMessages();
    }

    /**
     * Executes the PrintBookManagerChapters command.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     */
    public void doPrintBookManagerChapters(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        final BookManager bookManager = checkBookManagerReady(runParm,
            UtilConstants.RUNPARM_PRINT_BOOK_MANAGER_CHAPTERS.name(),
            logicalSystem);

        final Dump d = getDump();

        d.printBookManagerChapters(
            UtilConstants.RUNPARM_PRINT_BOOK_MANAGER_CHAPTERS.name(),
            bookManager);

        printAndClearMessages();
    }

    /**
     * Executes the PrintBookManagerSections command.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     */
    public void doPrintBookManagerSections(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        final BookManager bookManager = checkBookManagerReady(runParm,
            UtilConstants.RUNPARM_PRINT_BOOK_MANAGER_SECTIONS.name(),
            logicalSystem);

        final Dump d = getDump();

        d.printBookManagerSections(
            UtilConstants.RUNPARM_PRINT_BOOK_MANAGER_SECTIONS.name(),
            bookManager);

        printAndClearMessages();
    }

    /**
     * Executes the PrintBookManagerSectionDetails command
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     */
    public void doPrintBookManagerSectionDetails(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        final BookManager bookManager = checkBookManagerReady(runParm,
            UtilConstants.RUNPARM_PRINT_BOOK_MANAGER_SECTION_DETAILS.name(),
            logicalSystem);

        final Dump d = getDump();

        Section section = null;
        final String optionValue = runParm.values[0].trim();
        if (optionValue.compareTo(UtilConstants.RUNPARM_OPTION_VALUE_ALL) != 0)
            section = editBookManagerSectionNbr(
                runParm,
                UtilConstants.RUNPARM_PRINT_BOOK_MANAGER_SECTION_DETAILS.name(),
                1, bookManager);

        d.printBookManagerSectionDetails(runParm, logicalSystem, bookManager,
            section);

        printAndClearMessages();
    }

    /**
     * Get a Dump object.
     * 
     * @return Dump object, ready to go.
     */
    public Dump getDump() {
        if (dump == null)
            dump = new Dump(sysOut);
        else
            dump.setSysOut(sysOut);

        final TMFFPreferences tmffPreferences = batchFramework.tmffBoss
            .getTMFFPreferences();

        dump.setTMFFPreferences(tmffPreferences);

        return dump;
    }

    /**
     * Validate System Output File Runparm.
     * 
     * @param runParm RunParmFile line parsed into RunParmArrayEntry.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editSysOutFile(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        closeSysOut();
        sysOut = null;
        sysOut = editPrintWriterRunParm(batchFramework.paths.getMMJ2Path(),
            runParm, UtilConstants.RUNPARM_SYSOUT_FILE.name());

    }

    /**
     * Validate System Error File Runparm.
     * 
     * @param runParm RunParmFile line parsed into RunParmArrayEntry.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editSysErrFile(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        closeSysErr();
        sysErr = null;
        sysErr = editPrintWriterRunParm(batchFramework.paths.getMMJ2Path(),
            runParm, UtilConstants.RUNPARM_SYSERR_FILE.name());

    }

    /**
     * Validate Max Error Messages Runparm.
     * 
     * @param runParm RunParmFile line parsed into RunParmArrayEntry.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editMaxErrorMessages(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        maxErrorMessagesParm = editRunParmValueReqPosInt(runParm,
            UtilConstants.RUNPARM_MAX_ERROR_MESSAGES.name(), 1);
        if (messages != null) {
            printAndClearMessages();
            messages.reallocateErrorMessages(maxErrorMessagesParm);
        }
    }

    /**
     * Validate Max Info Messages Runparm.
     * 
     * @param runParm RunParmFile line parsed into RunParmArrayEntry.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editMaxInfoMessages(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        maxInfoMessagesParm = editRunParmValueReqPosInt(runParm,
            UtilConstants.RUNPARM_MAX_INFO_MESSAGES.name(), 1);
        if (messages != null) {
            printAndClearMessages();
            messages.reallocateInfoMessages(maxInfoMessagesParm);
        }
    }

    /**
     * Validate Caption Runparm.
     * 
     * @param runParm RunParmFile line parsed into RunParmArrayEntry.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editCaption(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        editRunParmValuesLength(runParm, UtilConstants.RUNPARM_CAPTION.name(),
            1);
        captionParm = runParm.values[0].trim();
        if (captionParm.length() == 0)
            captionParm = " ";
    }

    /**
     * Get Caption Parm Option.
     * 
     * @return Caption string.
     */
    protected String getCaption() {
        if (captionParm == null)
            return " ";
        else
            return captionParm;
    }

    /**
     * Validate Max Statement Print Count RunParm.
     * 
     * @param runParm RunParmFile line parsed into RunParmArrayEntry.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editMaxStatementPrintCount(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        maxStatementPrintCountParm = editRunParmValueReqPosInt(runParm,
            UtilConstants.RUNPARM_MAX_STATEMENT_PRINT_COUNT.name(), 1);
    }

    /**
     * Validate OutputVerbosity Runparm.
     * 
     * @param runParm RunParmFile line parsed into RunParmArrayEntry.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editOutputVerbosity(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        outputVerbosityParm = editRunParmValueReqInt(runParm,
            UtilConstants.RUNPARM_OUTPUT_VERBOSITY.name(), 1);
    }

    /**
     * Validate StartInstrumentationTimer Runparm.
     * 
     * @param runParm RunParmFile line parsed into RunParmArrayEntry.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editStartInstrumentationTimer(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_START_INSTRUMENTATION_TIMER.name(), 1);

        getMessages().startInstrumentationTimer(runParm.values[0]);
    }

    /**
     * Validate StopInstrumentationTimer Runparm.
     * 
     * @param runParm RunParmFile line parsed into RunParmArrayEntry.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editStopInstrumentationTimer(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_STOP_INSTRUMENTATION_TIMER.name(), 1);

        getMessages().stopInstrumentationTimer(runParm.values[0]);
        printAndClearMessages();
    }

    /**
     * Checks to see if BookManager is initialized and enabled.
     * <p>
     * Caution: throws IllegalArgumentException if BookManager is not enabled!
     * Ouch.
     * 
     * @param runParm RunParmFile line.
     * @param valueCaption String identifying RunParm value
     * @param logicalSystem the LogicalSystem in use.
     * @return BookManager object in enabled status.
     */
    protected BookManager checkBookManagerReady(
        final RunParmArrayEntry runParm, final String valueCaption,
        final LogicalSystem logicalSystem)
    {

        final BookManager bookManager = logicalSystem.getBookManager();
        if (!bookManager.isEnabled())
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_BOOK_MANAGER_NOT_ENABLED_1 + valueCaption
                    + UtilConstants.ERRMSG_BOOK_MANAGER_NOT_ENABLED_2);
        return bookManager;
    }

    /**
     * Checks to see if BookManager is initialized and enabled.
     * <p>
     * Caution: throws IllegalArgumentException if Section Number is not a
     * positive interer, or if it is not found within the BookManager! Ouch.
     * 
     * @param runParm RunParmFile line.
     * @param valueCaption String identifying RunParm value
     * @param valueFieldNbr number of value field within RunParm.
     * @param bookManager the BookManager to check
     * @return Section BookManager section.
     */
    protected Section editBookManagerSectionNbr(
        final RunParmArrayEntry runParm, final String valueCaption,
        final int valueFieldNbr, final BookManager bookManager)
    {

        final int sectionNbr = editRunParmValueReqPosInt(runParm, valueCaption,
            valueFieldNbr);

        final Section section = bookManager.getSection(sectionNbr);
        if (section == null)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_BOOK_MANAGER_SECTION_NBR_NOT_FOUND_1
                    + sectionNbr
                    + UtilConstants.ERRMSG_BOOK_MANAGER_SECTION_NBR_NOT_FOUND_2
                    + valueCaption);
        return section;
    }

    /**
     * Initialize Messages Object.
     */
    protected void initializeMessages() {

        int e = maxErrorMessagesParm;
        if (e <= 0)
            e = LangConstants.MAX_ERROR_MESSAGES_DEFAULT;

        int i = maxInfoMessagesParm;
        if (i <= 0)
            i = LangConstants.MAX_INFO_MESSAGES_DEFAULT;

        messages = new Messages(e, i);
    }

    /**
     * Check SysErr to see if I/O Error has occurred.
     * 
     * @throws IOException if an error occurred
     */
    protected void checkSysErrError() throws IOException {

        if (sysErr.checkError()) {
            sysErr = null;
            throw new IOException(
                UtilConstants.ERRMSG_SYSERR_PRINT_WRITER_IO_ERROR_1);
        }
    }

    /**
     * Check SysOut to see if I/O Error has occurred.
     * 
     * @throws IOException if an error occurred
     */
    protected void checkSysOutError() throws IOException {

        if (sysOut.checkError()) {
            sysOut = null;
            throw new IOException(
                UtilConstants.ERRMSG_SYSOUT_PRINT_WRITER_IO_ERROR_1);
        }
    }

    /**
     * Close SysOut.
     */
    protected void closeSysOut() {
        if (sysOut != null)
            sysOut.close();
    }

    /**
     * Close SysErr.
     */
    protected void closeSysErr() {
        if (sysErr != null)
            sysErr.close();
    }

    private void setDefaults() {
        maxErrorMessagesParm = LangConstants.MAX_ERROR_MESSAGES_DEFAULT;

        maxInfoMessagesParm = LangConstants.MAX_INFO_MESSAGES_DEFAULT;

        messages = null;

        maxStatementPrintCountParm = UtilConstants.MAX_STATEMENT_PRINT_COUNT_DEFAULT;

        dump = null;

        outputVerbosityParm = UtilConstants.OUTPUT_VERBOSITY_DEFAULT;

    }
}
