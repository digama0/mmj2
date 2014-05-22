//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * GMFFBoss.java  0.01 11/01/2011
 *
 * Version 0.01:
 * Nov-01-2011: new.
 */

package mmj.util;

import java.util.ArrayList;
import java.util.List;

import mmj.gmff.EscapePair;
import mmj.gmff.GMFFConstants;
import mmj.gmff.GMFFException;
import mmj.gmff.GMFFExportParms;
import mmj.gmff.GMFFManager;
import mmj.gmff.GMFFUserExportChoice;
import mmj.gmff.GMFFUserTextEscapes;
import mmj.pa.ProofAsst;

public class GMFFBoss extends Boss {

    protected GMFFManager gmffManager;

    /**
     * Constructor with BatchFramework for access to environment.
     * 
     * @param batchFramework for access to environment.
     */
    public GMFFBoss(final BatchFramework batchFramework) {
        super(batchFramework);
    }

    /**
     * Executes a single command from the RunParmFile.
     * 
     * @param runParm the RunParmFile line to execute.
     */
    @Override
    public boolean doRunParmCommand(final RunParmArrayEntry runParm)
        throws IllegalArgumentException, GMFFException
    {

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_CLEAR.name()) == 0)
        {
            gmffManager = null;
            return false; // not "consumed"
        }

        if (runParm.name.compareToIgnoreCase(UtilConstants.RUNPARM_LOAD_FILE
            .name()) == 0)
        {
            // don't need to do anything here because
            // LogicalSystemBoss executes
            // gmffManager.forceReinitialization() --
            // we will null out the cached gmffManager
            // just to be safe though.
            gmffManager = null;

            return false; // not "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_GMFF_INITIALIZE.name()) == 0)
        {
            doGMFFInitialize(runParm);
            return true; // "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_GMFF_EXPORT_PARMS.name()) == 0)
        {
            doRunParmGMFFExportParms(runParm);
            return true; // "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_GMFF_USER_EXPORT_CHOICE
                .name()) == 0)
        {
            doRunParmGMFFUserExportChoice(runParm);
            return true; // "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_GMFF_USER_TEXT_ESCAPES
                .name()) == 0)
        {
            doRunParmGMFFUserTextEscapes(runParm);
            return true; // "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_GMFF_PARSE_METAMATH_TYPESET_COMMENT
                .name()) == 0)
        {
            doGMFFParseMetamathTypesetComment(runParm);
            return true; // "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_GMFF_EXPORT_FROM_FOLDER
                .name()) == 0)
        {
            doGMFFExportFromFolder(runParm);
            return true; // "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_GMFF_EXPORT_THEOREM
                .name()) == 0)
        {
            doGMFFExportTheorem(runParm);
            return true; // "consumed"
        }

        return false;
    }

    /**
     * Fetch a gmffManagerobject, building it if necessary from previously input
     * RunParms.
     * <p>
     * NOTE: The returned gmffManageris "ready to go" but may not have been
     * "initialized", which means gmffManagervalidation, etc. The reason that
     * gmffManageris not initialized here is that a previous attempt to
     * "initialize" may have failed due to gmffManagererrors, so to re-do it
     * here would result in doubled-up error messages. The Initialize
     * gmffManagerRunParm Command should be used prior to PrintSyntaxDetails if
     * a "load and print syntax" is desired.
     * 
     * @return gmffManagerobject, ready to go.
     */
    public GMFFManager getGMFFManager() {

        if (gmffManager == null)
            gmffManager = batchFramework.logicalSystemBoss.getLogicalSystem()
                .getGMFFManager();
        return gmffManager;
    }

    /**
     * Executes GMFFManager initializeGMFF function and prints any messages,
     * etc.
     * 
     * @param runParm RunParmFile line.
     * @throws GMFFException on invalid parameter
     */
    public void doGMFFInitialize(final RunParmArrayEntry runParm)
        throws GMFFException
    {

        boolean printTypesettingDefinitionsReport = false;
        String parmString;
        if (runParm.values.length > 0) {
            parmString = runParm.values[0].trim();
            if (parmString.length() > 0)
                if (parmString
                    .compareToIgnoreCase(GMFFConstants.PRINT_TYPESETTING_DEFINITIONS) == 0)
                    printTypesettingDefinitionsReport = true;
                else
                    throw new GMFFException(
                        GMFFConstants.ERRMSG_GMFF_INITIALIZE_PARM_0_ERR_1
                            + runParm.values[0]);
        }

        try {
            getGMFFManager().gmffInitialize(printTypesettingDefinitionsReport);
        } catch (final GMFFException e) {
            batchFramework.outputBoss.getMessages().accumErrorMessage(
                e.getMessage());
        }

        batchFramework.outputBoss.printAndClearMessages();

        if (!getGMFFManager().isGMFFInitialized())
            throw new GMFFException(
                UtilConstants.ERRMSG_GMFF_INITIALIZATION_ERROR_1);

        return;
    }

    public void doRunParmGMFFExportParms(final RunParmArrayEntry runParm)
        throws GMFFException
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_GMFF_EXPORT_PARMS.name(), 8);

        final String exportType = runParm.values[0].trim();
        final String onoff = runParm.values[1].trim();
        final String typesetDefKeyword = runParm.values[2].trim();
        final String exportDirectory = runParm.values[3].trim();
        final String exportFileType = runParm.values[4].trim();
        final String modelsDirectory = runParm.values[5].trim();
        final String modelId = runParm.values[6].trim();
        final String charsetEncoding = runParm.values[7].trim();

        String outputFileName = null;
        if (runParm.values.length > 8) {
            outputFileName = runParm.values[8].trim();
            if (outputFileName.length() == 0)
                outputFileName = null;
        }

        final GMFFExportParms gmffExportParms = new GMFFExportParms(exportType,
            onoff, typesetDefKeyword, exportDirectory, exportFileType,
            modelsDirectory, modelId, charsetEncoding, outputFileName);

        getGMFFManager().accumInputGMFFExportParms(gmffExportParms);

        batchFramework.outputBoss.printAndClearMessages();
        return;
    }

    public void doRunParmGMFFUserTextEscapes(final RunParmArrayEntry runParm)
        throws GMFFException
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_GMFF_USER_TEXT_ESCAPES.name(), 3);

        final String exportType = runParm.values[0].trim();

        final List<EscapePair> list = new ArrayList<EscapePair>(
            GMFFConstants.DEFAULT_USER_TEXT_ESCAPES.length);

        int num;
        String repl;
        int i = 1;
        do {
            num = editRunParmValueInteger(runParm.values[i++].trim(),
                GMFFConstants.ERRMSG_CAPTION_ESCAPE_PAIR_NUM);

            if (i < runParm.values.length)
                repl = runParm.values[i++].trim();
            else
                repl = null;

            final EscapePair p = new EscapePair(num, repl);
            list.add(p);

        } while (i < runParm.values.length);

        final GMFFUserTextEscapes gmffUserTextEscapes = new GMFFUserTextEscapes(
            exportType, list);

        getGMFFManager().accumInputGMFFUserTextEscapesList(gmffUserTextEscapes);

        batchFramework.outputBoss.printAndClearMessages();
        return;
    }

    public void doRunParmGMFFUserExportChoice(final RunParmArrayEntry runParm)
        throws GMFFException
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_GMFF_USER_EXPORT_CHOICE.name(), 1);

        final String exportTypeOrAll = runParm.values[0].trim();

        final GMFFUserExportChoice gmffUserExportChoice = new GMFFUserExportChoice(
            exportTypeOrAll);

        getGMFFManager().setInputGMFFUserExportChoice(gmffUserExportChoice);

        batchFramework.outputBoss.printAndClearMessages();
        return;
    }

    public void doGMFFParseMetamathTypesetComment(
        final RunParmArrayEntry runParm) throws IllegalArgumentException
    {

        boolean runParmPrintOption = false;

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_GMFF_PARSE_METAMATH_TYPESET_COMMENT.name(), 3);
        try {

            if (runParm.values.length > 3) {
                final String parmString = runParm.values[3].trim();
                if (parmString.length() > 0)
                    if (parmString
                        .compareToIgnoreCase(GMFFConstants.RUNPARM_PRINT_OPTION) == 0)
                        runParmPrintOption = true;
                    else
                        throw new GMFFException(
                            GMFFConstants.ERRMSG_GMFF_PARSE_RUNPARM_PARM_4_ERR_1
                                + runParm.values[3]);
            }

            final String typesetDefKeyword = runParm.values[0].trim();
            final String myDirectory = runParm.values[1].trim();
            final String myMetamathTypesetCommentFileName = runParm.values[2]
                .trim();

            getGMFFManager().parseMetamathTypesetComment(typesetDefKeyword,
                myDirectory, myMetamathTypesetCommentFileName,
                runParmPrintOption);
        } catch (final GMFFException e) {
            batchFramework.outputBoss.getMessages().accumErrorMessage(
                e.getMessage());
        }

        batchFramework.outputBoss.printAndClearMessages();
        return;
    }

    public void doGMFFExportFromFolder(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_GMFF_EXPORT_FROM_FOLDER.name(), 4);
        try {

            final String inputDirectory = runParm.values[0].trim();
            final String theoremLabelOrAsterisk = runParm.values[1].trim();
            final String inputFileType = runParm.values[2].trim();

            final String maxNumberToExport = runParm.values[3].trim();

            String appendFileName = null;
            if (runParm.values.length > 4) {
                appendFileName = runParm.values[4].trim();
                if (appendFileName.length() == 0)
                    appendFileName = null;
            }

            getGMFFManager().exportFromFolder(inputDirectory,
                theoremLabelOrAsterisk, inputFileType, maxNumberToExport,
                appendFileName);
        } catch (final GMFFException e) {
            batchFramework.outputBoss.getMessages().accumErrorMessage(
                e.getMessage());
        }

        batchFramework.outputBoss.printAndClearMessages();
        return;
    }

    public void doGMFFExportTheorem(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_GMFF_EXPORT_THEOREM.name(), 2);
        try {

            final String theoremLabelOrAsterisk = runParm.values[0].trim();

            final String maxNumberToExport = runParm.values[1].trim();

            String appendFileName = null;
            if (runParm.values.length > 2) {
                appendFileName = runParm.values[2].trim();
                if (appendFileName.length() == 0)
                    appendFileName = null;
            }

            final ProofAsst proofAsst = batchFramework.proofAsstBoss
                .getProofAsst();
            if (proofAsst == null)
                throw new GMFFException(
                    UtilConstants.ERRMSG_GMFF_PROOF_ASST_MISSING_1
                        + UtilConstants.RUNPARM_GMFF_EXPORT_THEOREM
                        + UtilConstants.ERRMSG_GMFF_PROOF_ASST_MISSING_2);

            getGMFFManager().exportTheorem(theoremLabelOrAsterisk,
                maxNumberToExport, appendFileName, proofAsst);
        } catch (final Exception e) {
            batchFramework.outputBoss.getMessages().accumErrorMessage(
                e.getMessage());
            batchFramework.outputBoss.getMessages().accumErrorMessage(
                UtilConstants.ERRMSG_GMFF_RUNPARM_ERROR_1
                    + UtilConstants.RUNPARM_GMFF_EXPORT_THEOREM
                    + UtilConstants.ERRMSG_GMFF_RUNPARM_ERROR_2);
        }

        batchFramework.outputBoss.printAndClearMessages();
        return;
    }
}
