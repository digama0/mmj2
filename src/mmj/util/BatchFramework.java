//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * BatchFramework.java  0.09 11/01/2011
 *
 * Sep-25-2005:
 *     -->Start counting RunParmFile lines at 1. Duh.
 * Dec-03-2005:
 *     -->Add ProofAsst stuff
 *     -->Added exception/error message and stack trace
 *        print directly to System.err in runIt() because
 *        NullPointException was just dumping with the
 *        helpful message "null".
 * Sep-03-2006:
 *     -->Add TMFF stuff
 * Jun-01-2007 - Version 0.06
 *     -->OutputVerbosity RunParm stuff.
 * Sep-01-2007 - Version 0.07
 *     -->Add WorkVarBoss.
 * Aug-01-2008 - Version 0.08
 *     -->Add SvcBoss.
 *     -->Make sure LogicalSystemBoss is at the end of the
 *        Boss list. This gives other bosses a chance to
 *        see the LoadFile command and re-initialize
 *        themselves (rare situation: only if multiple
 *        sets of commands in a single RunParm file.)
 *     -->Add TheoremLoaderBoss
 * Nov-01-2011 - Version 0.09
 *     -->Add GMFFBoss.
 *     -->Add GMFFException.
 *     -->Modified for mmj2 Paths Enhancement
 *     -->Added code for MMJ2FailPopupWindow
 */

package mmj.util;

import java.io.IOException;
import java.util.*;

import mmj.gmff.GMFFException;
import mmj.mmio.MMIOException;
import mmj.pa.MMJException;
import mmj.tl.TheoremLoaderException;
import mmj.verify.VerifyException;

/**
 * BatchFramework is a quick hack to run mmj2 without the JUnit training wheels.
 * <p>
 * An example of using this "framework" is BatchMMJ2 which sub-classes
 * BatchFramework and is invoked via "main(String[] args)", passing those
 * parameters in turn, to BatchFramework.
 * <p>
 * A RunParmFile is used to provide flexibility for many, many parameters in the
 * future, and to allow for files using different code sets.
 * <p>
 * This code is experimental and goofy looking :) No warranty provided... The
 * theme is that the order of input RunParmFile commands is unknown and
 * therefore, each function must not make any assumptions about what has been
 * done previously.
 * <p>
 * "Boss" classes are used to manage "state" information, and "get" commands
 * build objects as needed, invoking functions under the control of other Bosses
 * to obtain needed objects (which are *not* to be retained given the dynamic
 * flux of state information.) This all seems very inefficient but the overhead
 * is very small compared to the amount of work performed by each RunParmFile
 * command.
 * <p>
 * A list of Bosses in use during a given run is maintained and each input
 * RunParm line is sent to each Boss, in turn, which may or may not do anything
 * with the command. Upon exit, a Boss returns "consumed" to indicate that the
 * RunParm need not be sent to any other Bosses, that the job is done. Adding a
 * new function, such as "Soundness checking" should be simple.
 * <p>
 * An alternate way to use BatchFramework is to instantiate one but instead of
 * executing "runIt", invoke initializeBatchFramework() and then directly call
 * routines in the various Boss classes, passing them hand-coded
 * RunParmArrayEntry objects built using the UtilConstants RunParm name and
 * value literals.. In other words, don't use a RunParmFile. This approach
 * provides a short-cut for invoking mmj2 functions, to which new functions
 * could be easily applied.
 */
public abstract class BatchFramework {

    protected boolean batchFrameworkInitialized = false;

    protected CommandLineArguments commandLineArguments;

    public Paths paths;

    public MMJ2FailPopupWindow mmj2FailPopupWindow;

    public boolean displayMMJ2FailPopupWindow = UtilConstants.DISPLAY_MMJ2_FAIL_POPUP_WINDOW_DEFAULT;

    protected RunParmFile runParmFile;
    public int runParmCnt;

    public BatchCommand currentRunParmCommand;

    public List<Boss> bossList;

    public StoreBoss storeBoss;

    public OutputBoss outputBoss;

    public LogicalSystemBoss logicalSystemBoss;

    public VerifyProofBoss verifyProofBoss;

    public GrammarBoss grammarBoss;

    public ProofAsstBoss proofAsstBoss;

    public TMFFBoss tmffBoss;

    public WorkVarBoss workVarBoss;

    public SvcBoss svcBoss;

    public TheoremLoaderBoss theoremLoaderBoss;

    public GMFFBoss gmffBoss;

    public MacroBoss macroBoss;

    /**
     * Initialize BatchFramework with Boss list and any captions that may have
     * been overridden.
     * <p>
     * The purpose of doing this here is to allow a BatchFramework to be
     * constructed without executing any runparms from a file: every "doRunParm"
     * function is public and can be called from a program (assuming the program
     * can create a valid RunParmArrayEntry to provide RunParm option values.)
     * This provides a shortcut to invoking complicated mmj2 functions that
     * would otherwise require lots of setup and parameters.
     */
    public void initializeBatchFramework() {
        batchFrameworkInitialized = true;
        bossList = new ArrayList<>();

        storeBoss = new StoreBoss(this);
        outputBoss = new OutputBoss(this);
        verifyProofBoss = new VerifyProofBoss(this);
        grammarBoss = new GrammarBoss(this);
        proofAsstBoss = new ProofAsstBoss(this);
        tmffBoss = new TMFFBoss(this);
        workVarBoss = new WorkVarBoss(this);
        svcBoss = new SvcBoss(this);
        theoremLoaderBoss = new TheoremLoaderBoss(this);
        gmffBoss = new GMFFBoss(this);
        macroBoss = new MacroBoss(this);

        // NOTE: LogicalSystemBoss should be at the end
        // because the "LoadFile" RunParm is a signal
        // to many other the other bosses that they
        // need to re-initialize their state (for
        // example, Grammar requires reinitialization
        // for new input .mm files.)
        logicalSystemBoss = new LogicalSystemBoss(this);

        mmj2FailPopupWindow = new MMJ2FailPopupWindow(this,
            displayMMJ2FailPopupWindow);
    }

    /**
     * Uses command line run parms to build {@code RunParmFile} and
     * {@code Paths} objects, performs other initialization and processes each
     * RunParmFile line.
     * <p>
     * The {@code MMJ2FailPopupWindow} object is initialized in startupMode and
     * gathers/displays error messages during startup logic. See
     * {@code OutputBoss.printAndClearMessages()} which does the gathering and
     * displaying of startup errors. Abnormal termination -- "Fail" -- error
     * messages are displayed here by calling the MMJ2 Fail Popup Window
     * directly.
     *
     * @param args command line parms for RunParmFile constructor. (See
     *            CommandLineArguments.java for detailed doc
     * @return return code 0 if BatchFramework was successful (however many
     *         mmj/Metamath errors were found), or 16, if BatchFramework failed
     *         to complete (probably due to a RunParmFile error.)
     */
    public int runIt(final String[] args) {

        int retCd = 0;
        String failMessage = null;

        if (!batchFrameworkInitialized)
            initializeBatchFramework();

        try {
            commandLineArguments = new CommandLineArguments(args);

            paths = commandLineArguments.getPaths();

            runParmFile = commandLineArguments.getRunParmFile();

            displayMMJ2FailPopupWindow = commandLineArguments
                .getDisplayMMJ2FailPopupWindow();

            mmj2FailPopupWindow.setEnabled(displayMMJ2FailPopupWindow);

            mmj2FailPopupWindow.initiateStartupMode();

        } catch (final Exception e) {
            failMessage = UtilConstants.ERRMSG_RUNPARM_FILE_BOGUS_1
                + e.getMessage();
            System.err.println(failMessage);
            retCd = 16;
        }

        if (retCd == 0)
            try {
                final LinkedList<RunParmArrayEntry> list = new LinkedList<>();
                runParmFile.forEachRemaining(list::add);
                runParmFile.close();
                for (final RunParmArrayEntry runParm : list)
                    executeRunParmCommand(runParm);
            } catch (final Exception e) {
                failMessage = e.getMessage();
                System.err.println(failMessage);
                e.printStackTrace(System.err);
                retCd = 16;
                try {
                    outputBoss.sysErrPrintln(failMessage);
                } catch (final IOException f) {
                    failMessage += f.getMessage();
                    System.err.println(failMessage);
                }
            } catch (final Error e) {
                failMessage = e.getMessage();
                retCd = 16;
                System.err.println(failMessage);
                e.printStackTrace(System.err);
            }

        if (failMessage != null)
            mmj2FailPopupWindow.displayFailMessage(failMessage);

        outputBoss.close();

        return retCd;
    }

    /**
     * Processes a single RunParmFile line.
     *
     * @param runParm RunParmFileLine parsed into a RunParmArrayEntry object.
     * @throws IOException if an error occurred in the RunParm
     * @throws GMFFException if an error occurred in the RunParm
     * @throws MMIOException if an error occurred in the RunParm
     * @throws TheoremLoaderException if an error occurred in the RunParm
     * @throws VerifyException if an error occurred in the RunParm
     * @throws IllegalArgumentException if an error occurred in the RunParm
     */
    public void executeRunParmCommand(final RunParmArrayEntry runParm)
        throws IOException, IllegalArgumentException, VerifyException,
        TheoremLoaderException, MMIOException, GMFFException
    {
        runParmCnt++;

        // capture this for use by MMJ2FailPopupWindow
        currentRunParmCommand = runParm.cmd;

        if (runParm.commentLine != null)
            printCommentRunParmLine(runParmCnt, runParm);
        else {
            printExecutableRunParmLine(runParmCnt, runParm);

            if (runParm.name.equalsIgnoreCase(
                UtilConstants.RUNPARM_JAVA_GARBAGE_COLLECTION.name()))
                System.gc();
            else {
                boolean consumed = false;
                for (final Boss b : bossList)
                    if (consumed = b.doRunParmCommand(runParm))
                        break;
                if (!consumed && !runParm.name
                    .equalsIgnoreCase(UtilConstants.RUNPARM_CLEAR.name()))
                    throw new IllegalArgumentException(new MMJException(
                        UtilConstants.ERRMSG_RUNPARM_NAME_INVALID,
                        runParm.name));
            }
        }
    }

    /**
     * Override this to change or eliminate the printout of each executable
     * RunParmFile line.
     *
     * @param cnt RunParmFile line number
     * @param runParm RunParmFile line parsed into object RunParmArrayEntry.
     * @throws IOException if an error occurred in the RunParm
     */
    public void printExecutableRunParmLine(final int cnt,
        final RunParmArrayEntry runParm) throws IOException
    {
        outputBoss.printException(
            new MMJException(UtilConstants.ERRMSG_RUNPARM_EXECUTABLE_CAPTION,
                cnt, runParm),
            UtilConstants.RUNPARM_LINE_DUMP_VERBOSITY);
    }

    /**
     * Override this to change or eliminate the printout of each Comment
     * RunParmFile line.
     *
     * @param cnt RunParmFile line number
     * @param runParm RunParmFile line parsed into object RunParmArrayEntry.
     * @throws IOException if an error occurred in the RunParm
     */
    public void printCommentRunParmLine(final int cnt,
        final RunParmArrayEntry runParm) throws IOException
    {
        outputBoss.printException(
            new MMJException(UtilConstants.ERRMSG_RUNPARM_COMMENT_CAPTION, cnt,
                runParm),
            UtilConstants.RUNPARM_LINE_DUMP_VERBOSITY);
    }

    /**
     * Returns the canonical path name of the RunParmFile.
     *
     * @return canonical path of RunParmFile or empty string if RunParmFile is
     *         null.
     */
    public String getRunParmFileAbsolutePath() {
        String s = "";
        if (runParmFile != null)
            s = runParmFile.getAbsolutePath();
        return s;
    }
}
