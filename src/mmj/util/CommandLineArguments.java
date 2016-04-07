//********************************************************************/
//* Copyright (C) 2011  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * CommandLineArguments.java.java  0.01 11/01/2011
 *
 * Nov-01-2011 - Version 0.01
 *         --> new
 */

package mmj.util;

import java.io.IOException;

import mmj.pa.MMJException;
import mmj.pa.PaConstants;

/**
 * CommandLineArguments loads the arguments on the java command line into the
 * {@code RunParmFile} and {@code Paths} objects.
 * <p>
 * This class is not general purpose. It is specific to mmj2's needs and is
 * intended to localize everything related to the Command Line Arguments for
 * easy use by {@code BatchFramework}.
 * <p>
 * Coincidentally (ha), the "String[] args" parameter for the constructor is the
 * same as the BatchMMJ2 command line parms.
 */
public class CommandLineArguments {

    private String runParmFileNameArgument = null;
    private String displayMMJ2FailPopupWindowArgument = null;
    private String mmj2PathArgument = null;
    private String metamathPathArgument = null;
    private String svcPathArgument = null;
    private final Paths paths;

    private final RunParmFile runParmFile;

    private boolean displayMMJ2FailPopupWindow = UtilConstants.DISPLAY_MMJ2_FAIL_POPUP_WINDOW_DEFAULT;

    /**
     * Construct using "String[] args" parameters then load {@code Paths},
     * {@code RunParmFile}, displayMMJ2FailPopupWindow and testOption.
     * <p>
     * <b>args:</b>
     * <ol>
     * <li>args[0] = RunParmFileName. Required. May be relative or absolute path
     * name.
     * <ul>
     * <li>If relative path then mmj2Path applied as parent path if specified;
     * otherwise Current Path used (by system).
     * <li>If absolute then mmj2Path is ignored.
     * </ul>
     * <li>args[1] = displayMMJ2FailPopupWindow. Optional. May be "y" or "n"
     * (case-insensitive), or " ". Default setting is "y". Triggers popup window
     * displaying mmj2 error message if mmj2 startup errors or mmj2 "fatal"
     * errors. If any CommandLineArguments are in error the default setting is
     * used regardless of this argument.
     * <li>args[2] = mmj2Path. Optional. May be blank or empty string. If no
     * path specified the default path for mmj2 files is Current Path (unless
     * otherwise indicated in the documentation -- e.g. ProofAsstProofFolder).
     * <li>args[3] = metamathPath. Optional. May be blank or empty string. If no
     * path specified the default path for Metamath files is Current Path
     * (unless otherwise indicated in the documentation -- e.g.
     * GMFFParseMetamathTypesetComment).
     * <li>args[4] = svcPath. Optional. May be blank or empty string. If no path
     * specified the default path for the mmj2 Service Feature is Current Path.
     * </ol>
     *
     * @param args Array of String. runParmFileName, displayMMJ2FailPopupWindow,
     *            mmj2Path, metamathPath, svcPath, testOption.
     * @throws IOException if an error occurred
     * @throws MMJException if errors found.
     */
    public CommandLineArguments(final String[] args)
        throws IOException, MMJException
    {

        runParmFileNameArgument = getArg(args,
            UtilConstants.RUNPARM_FILE_NAME_ARGUMENT_INDEX);

        displayMMJ2FailPopupWindowArgument = getArg(args,
            UtilConstants.DISPLAY_MMJ2_FAIL_POPUP_WINDOW_ARGUMENT_INDEX);

        mmj2PathArgument = getArg(args, UtilConstants.MMJ2_PATH_ARGUMENT_INDEX);

        metamathPathArgument = getArg(args,
            UtilConstants.METAMATH_PATH_ARGUMENT_INDEX);

        svcPathArgument = getArg(args, UtilConstants.SVC_PATH_ARGUMENT_INDEX);

        displayArgumentOptionReportPart1(args);

        paths = new Paths(mmj2PathArgument, metamathPathArgument,
            svcPathArgument);

        displayArgumentOptionReportPart3(args);

        displayMMJ2FailPopupWindow = getDisplayMMJ2FailPopupWindowVar(
            displayMMJ2FailPopupWindowArgument);

        displayArgumentOptionReportPart4(args);

        runParmFile = new RunParmFile(paths, runParmFileNameArgument);

    }

    /**
     * Returns the {@code Paths} object.
     *
     * @return The {@code Paths} object.
     */
    public Paths getPaths() {
        return paths;
    }

    /**
     * Returns the {@code RunParmFile} object.
     *
     * @return The {@code RunParmFile} object.
     */
    public RunParmFile getRunParmFile() {
        return runParmFile;
    }

    /**
     * Returns the {@code displayMMJ2FailPopupWindow} value;
     *
     * @return The {@code displayMMJ2FailPopupWindow} value;
     */
    public boolean getDisplayMMJ2FailPopupWindow() {
        return displayMMJ2FailPopupWindow;
    }

    /**
     * Returns Y or N, or if no value entered, the default setting for the
     * {@code displayMMJ2FailPopupWindow} variable.
     * <p>
     * Note that blanks and empty string values have been previously converted
     * to {@code null}.
     *
     * @param arg must equal Y, N or null.
     * @return Y or N.
     * @throws MMJException if input not equal to Y, N or null.
     */
    private boolean getDisplayMMJ2FailPopupWindowVar(final String arg)
        throws MMJException
    {
        if (arg == null)
            return UtilConstants.DISPLAY_MMJ2_FAIL_POPUP_WINDOW_DEFAULT;
        if (arg.equalsIgnoreCase(UtilConstants.YES_ARGUMENT))
            return true;
        if (arg.equalsIgnoreCase(UtilConstants.NO_ARGUMENT))
            return false;
        throw new MMJException(UtilConstants.ERRMSG_FAIL_POPUP_WINDOW_ARGUMENT,
            UtilConstants.DISPLAY_MMJ2_FAIL_POPUP_WINDOW_ARGUMENT_LITERAL, arg)
                .addContext(UtilConstants.ERRMSG_COMMAND_LINE_ARGUMENTS_FORMAT);
    }

    /**
     * The java command line argument array element specified by
     * {@code argIndex} is returned after converting it to {@code null} if it is
     * blank or an empty string.
     *
     * @param args Java command line arguments array
     * @param argIndex index into args indicating the arg to retrieve
     * @return The arg element specified by argIndex is returned after
     *         converting it to null if it is blank or an empty string.
     */
    private String getArg(final String[] args, final int argIndex) {
        String s = argIndex < args.length ? args[argIndex] : null;
        return s == null || (s = s.trim()).isEmpty() ? null : s;
    }

    private void displayArgumentOptionReportPart1(final String[] args)
        throws IOException
    {

        System.out.println(UtilConstants.ARGUMENTS_OPTION_REPORT_LINE_1);

        System.out.println(PaConstants.PROOF_ASST_GUI_STARTUP_MSG);
        System.out.println("");

        System.out.println(UtilConstants.ARGUMENTS_OPTION_REPORT_LINE_2);
        for (int i = 0; i < args.length; i++)
            System.out
                .println(UtilConstants.ARGUMENTS_OPTION_REPORT_LINE_3A + (i + 1)
                    + UtilConstants.ARGUMENTS_OPTION_REPORT_LINE_3B + args[i]);
    }

    private void displayArgumentOptionReportPart3(final String[] args)
        throws IOException
    {

        RunParmFile.displayArgumentOptionReport(paths, runParmFileNameArgument);

        System.out.print(UtilConstants.ARGUMENTS_OPTION_REPORT_LINE_4);
    }

    private void displayArgumentOptionReportPart4(final String[] args) {

        System.out.print(displayMMJ2FailPopupWindow + "\n");

        System.out.println(UtilConstants.ARGUMENTS_OPTION_REPORT_LINE_5);
    }
}
