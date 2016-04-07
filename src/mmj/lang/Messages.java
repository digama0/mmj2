//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * Messages.java  0.03 06/01/2007
 *
 * Version 0.02 -- 08/23/2005
 *
 * Version 0.03 -- 06/01/2007
 *     --> added startInstrumentationTimer() and
 *               stopInstrumentationTimer().
 */

package mmj.lang;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Hashtable;

import mmj.pa.ErrorCode;
import mmj.pa.MMJException;

/**
 * Repository of error and informational messages during mmj processing.
 * <p>
 * The number of reported errors can be limited, thus providing a "governor" on
 * processing (to a point).
 * <p>
 * Keeps track of the number of error and informational messages so that
 * processing can be terminated if desired when no further output will be
 * accumulated, particularly with error messages... The rationale of which is
 * that if the Metamath .mm has basic Metamath language errors, such as invalid
 * keywords, etc., there is no point in attempting Proof Verification or Syntax
 * Verification. Also, processes that follow the System Load require a "clean"
 * system, and the code assumes the integrity of LogicalSystem's data.
 * <p>
 * Various "print" functions in Messages accept a java.io.PrintWriter object so
 * that output can be directed somewhere besides <b>System.out</b>. This also
 * allows for the possibility of writing in other, non-default character sets
 * such as UTF-8.
 */
public class Messages {

    /**
     * Count of error messages stored in Messages object.
     */
    protected int errorMessageCnt;

    /**
     * Count of info messages stored in Messages object.
     */
    protected int infoMessageCnt;

    /**
     * String array of error messages in Messages object.
     */
    protected String[] errorMessageArray;

    /**
     * String array of info messages in Messages object.
     */
    protected String[] infoMessageArray;

    protected Hashtable<String, InstrumentationTimer> instrumentationTable;

    /**
     * Default constructor using LangConstants.MAX_ERROR_MESSAGES_DEFAULT and
     * LangConstants.MAX_INFO_MESSAGES_DEFAULT.
     */
    public Messages() {
        this(LangConstants.MAX_ERROR_MESSAGES_DEFAULT,
            LangConstants.MAX_INFO_MESSAGES_DEFAULT);
    }

    /**
     * Constructor using max error/info message params.
     *
     * @param maxErrorMessages max error messages to be stored.
     * @param maxInfoMessages max info messages to be stored.
     * @throws IllegalArgumentException if "max" params < 1.
     */
    public Messages(final int maxErrorMessages, final int maxInfoMessages) {

        if (maxErrorMessages < 1)
            throw new IllegalArgumentException(
                new LangException(LangConstants.ERRMSG_MAX_ERROR_MSG_LT_1));
        if (maxInfoMessages < 1)
            throw new IllegalArgumentException(
                new LangException(LangConstants.ERRMSG_MAX_INFO_MSG_LT_1));

        errorMessageCnt = 0;
        infoMessageCnt = 0;

        errorMessageArray = new String[maxErrorMessages];
        infoMessageArray = new String[maxInfoMessages];
    }

    /**
     * Reallocate error message array with new size.
     *
     * @param maxErrorMessages max error messages to be stored.
     * @throws IllegalArgumentException if "max" param < 1.
     */
    public void reallocateErrorMessages(final int maxErrorMessages) {
        if (maxErrorMessages < 1)
            throw new IllegalArgumentException(
                new LangException(LangConstants.ERRMSG_MAX_ERROR_MSG_LT_1));
        errorMessageCnt = 0;
        errorMessageArray = new String[maxErrorMessages];
    }

    /**
     * Reallocate info message array with new size.
     *
     * @param maxInfoMessages max info messages to be stored.
     * @throws IllegalArgumentException if "max" param < 1.
     */
    public void reallocateInfoMessages(final int maxInfoMessages) {
        if (maxInfoMessages < 1)
            throw new IllegalArgumentException(
                new LangException(LangConstants.ERRMSG_MAX_INFO_MSG_LT_1));
        infoMessageCnt = 0;
        infoMessageArray = new String[maxInfoMessages];
    }

    /**
     * Accum an {@link MMJException} in Messages repository.
     * <p>
     * Stores the new message if there is room in the array.
     *
     * @param e exception
     * @return true if message stored, false if no room left.
     */
    public boolean accumException(final MMJException e) {
        if (e == null || !e.code.use())
            return true;
        return e.code.level.error ? accumErrorMessage(e.getMessage())
            : accumInfoMessage(e.getMessage());
    }

    /**
     * Accum info/error message in Messages repository.
     * <p>
     * Stores the new message if there is room in the array.
     *
     * @param code error message.
     * @param args formatting arguments.
     * @return true if message stored, false if no room left.
     */
    public boolean accumMessage(final ErrorCode code, final Object... args) {
        return accumException(new MMJException(code, args));
    }

    /**
     * Accum error message in Messages repository.
     * <p>
     * Stores the new message if there is room in the array.
     *
     * @param errorMessage error message.
     * @param args formatting arguments.
     * @return true if message stored, false if no room left.
     */
    public boolean accumErrorMessage(final String errorMessage,
        final Object... args)
    {
        if (errorMessageCnt < errorMessageArray.length) {
            errorMessageArray[errorMessageCnt++] = ErrorCode
                .format(errorMessage, args);
            return true;
        }
        return false;
    }

    /**
     * Accum info message in Messages repository.
     * <p>
     * Stores the new message if there is room in the array.
     *
     * @param infoMessage info message.
     * @param args formatting arguments.
     * @return true if message stored, false if no room left.
     */
    public boolean accumInfoMessage(final String infoMessage,
        final Object... args)
    {
        if (infoMessageCnt < infoMessageArray.length) {
            infoMessageArray[infoMessageCnt++] = ErrorCode.format(infoMessage,
                args);
            return true;
        }
        return false;
    }

    /**
     * Return count of error messages stored in Messages object.
     *
     * @return error message count.
     */
    public int getErrorMessageCnt() {
        return errorMessageCnt;
    }

    /**
     * Check max error messages (table full).
     *
     * @return true if no room for more error messages, otherwise false.
     */
    public boolean maxErrorMessagesReached() {
        if (errorMessageCnt < errorMessageArray.length)
            return false;
        return true;
    }

    /**
     * Return count of info messages stored in Messages object.
     *
     * @return info message count.
     */
    public int getInfoMessageCnt() {
        return infoMessageCnt;
    }

    /**
     * Return error message array.
     *
     * @return error message array.
     */
    public String[] getErrorMessageArray() {
        return errorMessageArray;
    }

    /**
     * Return info message array.
     *
     * @return info message array.
     */
    public String[] getInfoMessageArray() {
        return infoMessageArray;
    }

    /**
     * Print all messages to System.out and clear message arrays.
     */
    public void printAndClearMessages() {
        printMessages();
        clearMessages();
    }

    /**
     * Print all messages to System.out.
     */
    public void printMessages() {
        printInfoMessages();
        printErrorMessages();
    }

    /**
     * Print all messages to printStream and clear message arrays.
     *
     * @param printStream the PrintStream
     */
    public void printAndClearMessages(final PrintStream printStream) {
        printMessages(printStream);
        clearMessages();
    }

    /**
     * Print all messages to printStream.
     *
     * @param printStream the PrintStream
     */
    public void printMessages(final PrintStream printStream) {
        printInfoMessages(printStream);
        printErrorMessages(printStream);
    }

    /**
     * Write all messages to printWriter and clear message arrays.
     *
     * @param printWriter the PrintWriter
     */
    public void writeAndClearMessages(final PrintWriter printWriter) {
        writeMessages(printWriter);
        clearMessages();
    }

    /**
     * Write all messages to printWriter.
     *
     * @param printWriter the PrintWriter
     */
    public void writeMessages(final PrintWriter printWriter) {
        writeInfoMessages(printWriter);
        writeErrorMessages(printWriter);
    }

    /**
     * Print error messages to System.out.
     */
    public void printErrorMessages() {
        printErrorMessages(System.out);
    }

    /**
     * Print info messages to System.out.
     */
    public void printInfoMessages() {
        printInfoMessages(System.out);
    }

    /**
     * Print error messages to printStream.
     *
     * @param printStream the PrintStream
     */
    public void printErrorMessages(final PrintStream printStream) {
        for (int i = 0; i < errorMessageCnt; i++)
            printStream.println(errorMessageArray[i]);
    }

    /**
     * Write error messages to printWriter.
     *
     * @param printWriter the PrintWriter
     */
    public void writeErrorMessages(final PrintWriter printWriter) {
        for (int i = 0; i < errorMessageCnt; i++)
            printWriter.println(errorMessageArray[i]);
    }

    /**
     * Print info messages to printStream.
     *
     * @param printStream the PrintStream
     */
    public void printInfoMessages(final PrintStream printStream) {
        for (int i = 0; i < infoMessageCnt; i++)
            printStream.println(infoMessageArray[i]);
    }

    /**
     * Write info messages to printWriter.
     *
     * @param printWriter the PrintWriter
     */
    public void writeInfoMessages(final PrintWriter printWriter) {
        for (int i = 0; i < infoMessageCnt; i++)
            printWriter.println(infoMessageArray[i]);
    }

    /**
     * Empty message arrays and reset counters to zero.
     */
    public void clearMessages() {
        errorMessageCnt = 0;
        infoMessageCnt = 0;
    }

    public void startInstrumentationTimer(final String timerID) {

        if (instrumentationTable == null)
            instrumentationTable = new Hashtable<>();

        instrumentationTable.put(timerID.trim(), new InstrumentationTimer());
    }

    public void stopInstrumentationTimer(final String inTimerID) {

        final InstrumentationTimer tNow = new InstrumentationTimer();

        final String timerID = inTimerID.trim();

        if (instrumentationTable == null)
            instrumentationTable = new Hashtable<>();

        final InstrumentationTimer tThen = instrumentationTable.get(timerID);

        if (tThen == null)
            throw new IllegalArgumentException(new LangException(
                LangConstants.ERRMSG_TIMER_ID_NOTFND, timerID));

        accumMessage(LangConstants.ERRMSG_TIMER_ID, timerID,
            tNow.millisTime - tThen.millisTime, tNow.totalMemory,
            tNow.totalMemory - tThen.totalMemory, tNow.maxMemory,
            tNow.maxMemory - tThen.maxMemory, tNow.freeMemory,
            tNow.freeMemory - tThen.freeMemory);
    }

    public class InstrumentationTimer {
        public long millisTime;
        public long freeMemory;
        public long totalMemory;
        public long maxMemory;

        public InstrumentationTimer() {
            millisTime = System.currentTimeMillis();
            final Runtime r = Runtime.getRuntime();
            freeMemory = r.freeMemory();
            totalMemory = r.totalMemory();
            maxMemory = r.maxMemory();
        }
    }
}
