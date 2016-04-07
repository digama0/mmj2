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

import static mmj.util.UtilConstants.*;

import java.io.IOException;
import java.io.PrintWriter;

import mmj.lang.*;
import mmj.pa.MMJException;
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

        putCommand(RUNPARM_CLEAR, this::setDefaults);

        putCommand(RUNPARM_SYSOUT_FILE, this::editSysOutFile);

        putCommand(RUNPARM_SYSERR_FILE, this::editSysErrFile);

        putCommand(RUNPARM_MAX_ERROR_MESSAGES, this::editMaxErrorMessages);

        putCommand(RUNPARM_MAX_INFO_MESSAGES, this::editMaxInfoMessages);

        putCommand(RUNPARM_MAX_STATEMENT_PRINT_COUNT,
            this::editMaxStatementPrintCount);

        putCommand(RUNPARM_CAPTION, this::editCaption);

        putCommand(RUNPARM_PRINT_SYNTAX_DETAILS, this::doPrintSyntaxDetails);

        putCommand(RUNPARM_PRINT_STATEMENT_DETAILS,
            this::doPrintStatementDetails);

        putCommand(RUNPARM_OUTPUT_VERBOSITY, this::editOutputVerbosity);

        putCommand(RUNPARM_START_INSTRUMENTATION_TIMER,
            this::editStartInstrumentationTimer);

        putCommand(RUNPARM_STOP_INSTRUMENTATION_TIMER,
            this::editStopInstrumentationTimer);

        putCommand(RUNPARM_PRINT_BOOK_MANAGER_CHAPTERS,
            this::doPrintBookManagerChapters);

        putCommand(RUNPARM_PRINT_BOOK_MANAGER_SECTIONS,
            this::doPrintBookManagerSections);

        putCommand(RUNPARM_PRINT_BOOK_MANAGER_SECTION_DETAILS,
            this::doPrintBookManagerSectionDetails);
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
        if (v <= outputVerbosityParm)
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

        if (v <= outputVerbosityParm)
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
     * Print an exception to the console. Will extract an MMJException cause if
     * one exists, otherwise it will print a full stack trace. Prints nothing if
     * the verbosity is too low or the error code has been disabled.
     *
     * @param t line to print.
     * @throws IOException if an error occurred
     */
    public void printException(final Throwable t) throws IOException {
        printException(t, 0);
    }

    /**
     * Print an exception to the console. Will extract an MMJException cause if
     * one exists, otherwise it will print a full stack trace. Prints nothing if
     * the verbosity is too low or the error code has been disabled.
     *
     * @param t line to print.
     * @param v verbosity of line to print.
     * @throws IOException if an error occurred
     */
    public void printException(final Throwable t, final int v)
        throws IOException
    {
        if (t == null || v > outputVerbosityParm)
            return;
        final MMJException e = MMJException.extract(t);
        if (e == null)
            t.printStackTrace();
        else if (e.code.use())
            if (e.code.level.error)
                sysErrPrintln(e.getMessage());
            else
                sysOutPrintln(e.getMessage());
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
     */
    public void doPrintSyntaxDetails() {

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        final Grammar grammar = batchFramework.grammarBoss.getGrammar();

        final Dump d = getDump();
        d.printSyntaxDetails(getCaption(), logicalSystem, grammar);

    }

    /**
     * Executes the PrintStatementDetails command, prints any messages, etc.
     */
    public void doPrintStatementDetails() {

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        final Dump d = getDump();

        if (get(1).equalsIgnoreCase(RUNPARM_OPTION_VALUE_ALL)) {
            int n = maxStatementPrintCountParm;
            if (n <= 0)
                n = MAX_STATEMENT_PRINT_COUNT_DEFAULT;
            d.printStatementDetails(getCaption(), logicalSystem.getStmtTbl(),
                n);
        }
        else
            d.printOneStatementDetails(getStmt(1, logicalSystem));
        printAndClearMessages();
    }

    /**
     * Executes the PrintBookManagerChapters command.
     */
    public void doPrintBookManagerChapters() {

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        final BookManager bookManager = checkBookManagerReady(logicalSystem);

        final Dump d = getDump();

        d.printBookManagerChapters(RUNPARM_PRINT_BOOK_MANAGER_CHAPTERS.name(),
            bookManager);

        printAndClearMessages();
    }

    /**
     * Executes the PrintBookManagerSections command.
     */
    public void doPrintBookManagerSections() {

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        final BookManager bookManager = checkBookManagerReady(logicalSystem);

        final Dump d = getDump();

        d.printBookManagerSections(RUNPARM_PRINT_BOOK_MANAGER_SECTIONS.name(),
            bookManager);

        printAndClearMessages();
    }

    /**
     * Executes the PrintBookManagerSectionDetails command
     */
    public void doPrintBookManagerSectionDetails() {

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        final BookManager bookManager = checkBookManagerReady(logicalSystem);

        final Dump d = getDump();

        Section section = null;
        final String optionValue = runParm.values[0].trim();
        if (optionValue.compareTo(RUNPARM_OPTION_VALUE_ALL) != 0)
            section = editBookManagerSectionNbr(1, bookManager);

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
     */
    protected void editSysOutFile() {
        closeSysOut();
        sysOut = getPrintWriter(batchFramework.paths.getMMJ2Path());

    }

    /**
     * Validate System Error File Runparm.
     */
    protected void editSysErrFile() {
        closeSysErr();
        sysErr = getPrintWriter(batchFramework.paths.getMMJ2Path());
    }

    /**
     * Validate Max Error Messages Runparm.
     */
    protected void editMaxErrorMessages() {

        maxErrorMessagesParm = getPosInt(1);
        if (messages != null) {
            printAndClearMessages();
            messages.reallocateErrorMessages(maxErrorMessagesParm);
        }
    }

    /**
     * Validate Max Info Messages Runparm.
     */
    protected void editMaxInfoMessages() {

        maxInfoMessagesParm = getPosInt(1);
        if (messages != null) {
            printAndClearMessages();
            messages.reallocateInfoMessages(maxInfoMessagesParm);
        }
    }

    /**
     * Validate Caption Runparm.
     */
    protected void editCaption() {
        captionParm = get(1);
        if (captionParm.isEmpty())
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
     */
    protected void editMaxStatementPrintCount() {
        maxStatementPrintCountParm = getPosInt(1);
    }

    /**
     * Validate OutputVerbosity Runparm.
     */
    protected void editOutputVerbosity() {
        outputVerbosityParm = getInt(1);
    }

    /**
     * Validate StartInstrumentationTimer Runparm.
     */
    protected void editStartInstrumentationTimer() {
        getMessages().startInstrumentationTimer(get(1));
    }

    /**
     * Validate StopInstrumentationTimer Runparm.
     */
    protected void editStopInstrumentationTimer() {
        getMessages().stopInstrumentationTimer(get(1));
        printAndClearMessages();
    }

    /**
     * Checks to see if BookManager is initialized and enabled.
     * <p>
     * Caution: throws IllegalArgumentException if BookManager is not enabled!
     * Ouch.
     *
     * @param logicalSystem the LogicalSystem in use.
     * @return BookManager object in enabled status.
     */
    protected BookManager checkBookManagerReady(
        final LogicalSystem logicalSystem)
    {

        final BookManager bookManager = logicalSystem.bookManager;
        if (!bookManager.isEnabled())
            throw error(ERRMSG_BOOK_MANAGER_NOT_ENABLED,
                RUNPARM_BOOK_MANAGER_ENABLED, RUNPARM_LOAD_FILE);
        return bookManager;
    }

    /**
     * Checks to see if BookManager is initialized and enabled.
     * <p>
     * Caution: throws IllegalArgumentException if Section Number is not a
     * positive integer, or if it is not found within the BookManager! Ouch.
     *
     * @param valueFieldNbr number of value field within RunParm.
     * @param bookManager the BookManager to check
     * @return Section BookManager section.
     */
    protected Section editBookManagerSectionNbr(final int valueFieldNbr,
        final BookManager bookManager)
    {

        final int sectionNbr = getPosInt(valueFieldNbr);

        final Section section = bookManager.getSection(sectionNbr);
        if (section == null)
            throw error(ERRMSG_BOOK_MANAGER_SECTION_NBR_NOT_FOUND, sectionNbr);
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
                new MMJException(ERRMSG_SYSERR_PRINT_WRITER_IO_ERROR));
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
                new MMJException(ERRMSG_SYSOUT_PRINT_WRITER_IO_ERROR));
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

    private boolean setDefaults() {
        maxErrorMessagesParm = LangConstants.MAX_ERROR_MESSAGES_DEFAULT;

        maxInfoMessagesParm = LangConstants.MAX_INFO_MESSAGES_DEFAULT;

        messages = null;

        maxStatementPrintCountParm = MAX_STATEMENT_PRINT_COUNT_DEFAULT;

        dump = null;

        outputVerbosityParm = OUTPUT_VERBOSITY_DEFAULT;

        return false;
    }
}
