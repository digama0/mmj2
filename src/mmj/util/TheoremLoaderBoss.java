//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * TheoremLoaderBoss.java  0.02 11/01/2011
 *
 * Version 0.01:
 *     - New.
 *
 * Nov-01-2011 - Version 0.02
 *     -->Modified for mmj2 Paths Enhancement
 *        added mmj2Path to setMMTFolder() call
 *        in editTheoremLoaderMMTFolder()
 */

package mmj.util;

import java.io.*;

import mmj.lang.*;
import mmj.mmio.MMIOException;
import mmj.pa.ProofAsst;
import mmj.tl.*;

/**
 * Responsible for building and referencing TheoremLoader.
 */
public class TheoremLoaderBoss extends Boss {

    private TheoremLoader theoremLoader;

    private TlPreferences tlPreferences;

    /**
     * Constructor with BatchFramework for access to environment.
     * 
     * @param batchFramework for access to environment.
     */
    public TheoremLoaderBoss(final BatchFramework batchFramework) {
        super(batchFramework);
    }

    /**
     * Executes a single command from the RunParmFile.
     * 
     * @param runParm the RunParmFile line to execute.
     * @return boolean "consumed" indicating that the input runParm should not
     *         be processed again.
     */
    @Override
    public boolean doRunParmCommand(final RunParmArrayEntry runParm)
        throws IllegalArgumentException, MMIOException, FileNotFoundException,
        IOException, VerifyException, TheoremLoaderException
    {

        if (runParm.name.compareToIgnoreCase(UtilConstants.RUNPARM_CLEAR) == 0)
        {
            theoremLoader = null;
            tlPreferences = null;
            return false; // not "consumed"
        }

        if (runParm.name.compareToIgnoreCase(UtilConstants.RUNPARM_LOAD_FILE) == 0)
        {
            theoremLoader = null;
            tlPreferences = null;
            return false; // not "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_THEOREM_LOADER_DJ_VARS_OPTION) == 0)
        {
            editTheoremLoaderDjVarsOption(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_THEOREM_LOADER_AUDIT_MESSAGES) == 0)
        {
            editTheoremLoaderAuditMessages(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_THEOREM_LOADER_MMT_FOLDER) == 0)
        {
            editTheoremLoaderMMTFolder(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_LOAD_THEOREMS_FROM_MMT_FOLDER) == 0)
        {
            editLoadTheoremsFromMMTFolder(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_EXTRACT_THEOREM_TO_MMT_FOLDER) == 0)
        {
            editExtractTheoremToMMTFolder(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_THEOREM_LOADER_STORE_FORMULAS_ASIS) == 0)
        {
            editTheoremLoaderStoreFormulasAsIs(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_THEOREM_LOADER_STORE_MM_INDENT_AMT) == 0)
        {
            editTheoremLoaderStoreMMIndentAmt(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_THEOREM_LOADER_STORE_MM_RIGHT_COL) == 0)
        {
            editTheoremLoaderStoreMMRightCol(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_UNIFY_PLUS_STORE_IN_LOG_SYS_AND_MMT_FOLDER) == 0)
        {
            editUnifyPlusStoreInLogSysAndMMTFolder(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_UNIFY_PLUS_STORE_IN_MMT_FOLDER) == 0)
        {
            editUnifyPlusStoreInMMTFolder(runParm);
            return true;
        }

        return false;
    }

    /**
     * Fetches a reference to the TlPreferences, first initializing it if
     * necessary.
     * <p>
     * Note: must re-initialize the TMFFPreferences reference in TlPreferences
     * because TMFFBoss controls which instance of TMFFPreferences is active!!!
     * 
     * @return TlPreferences object ready to go.
     */
    public TlPreferences getTlPreferences() {

        if (tlPreferences == null) {

            final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
                .getLogicalSystem();

            tlPreferences = new TlPreferences(logicalSystem);
        }

        return tlPreferences;
    }

    /**
     * Fetch a TheoremLoader object.
     * 
     * @return TheoremLoader object, ready to go, or null;.
     * @throws VerifyException if an error occurred
     */
    public TheoremLoader getTheoremLoader() throws VerifyException {

        if (theoremLoader != null)
            return theoremLoader;

        final TlPreferences tlPreferences = getTlPreferences();

        theoremLoader = new TheoremLoader(tlPreferences);

        batchFramework.outputBoss.printAndClearMessages();

        return theoremLoader;
    }

    /**
     * edit TheoremLoaderDjVarsOption RunParm.
     * 
     * @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editTheoremLoaderDjVarsOption(final RunParmArrayEntry runParm)
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_THEOREM_LOADER_DJ_VARS_OPTION, 1);
        if (getTlPreferences().setDjVarsOption(runParm.values[0].trim()))
            return; // ok, valid!

        final String errorMessage = TlConstants.ERRMSG_INVALID_DJ_VARS_OPTION_1
            + runParm.values[0].trim()
            + TlConstants.ERRMSG_INVALID_DJ_VARS_OPTION_2;

        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_1
                + UtilConstants.RUNPARM_THEOREM_LOADER_DJ_VARS_OPTION
                + UtilConstants.ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_2
                + errorMessage);
    }

    /**
     * edit TheoremLoaderAuditMessages RunParm.
     * 
     * @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editTheoremLoaderAuditMessages(
        final RunParmArrayEntry runParm)
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_THEOREM_LOADER_AUDIT_MESSAGES, 1);
        if (getTlPreferences().setAuditMessages(runParm.values[0].trim()))
            return; // ok, valid!

        final String errorMessage = TlConstants.ERRMSG_INVALID_AUDIT_MESSAGES_1
            + runParm.values[0].trim()
            + TlConstants.ERRMSG_INVALID_AUDIT_MESSAGES_2;

        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_1
                + UtilConstants.RUNPARM_THEOREM_LOADER_AUDIT_MESSAGES
                + UtilConstants.ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_2
                + errorMessage);

    }

    /**
     * Validate Theorem Loader MMT Folder Runparm.
     * 
     * @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editTheoremLoaderMMTFolder(final RunParmArrayEntry runParm) {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_THEOREM_LOADER_MMT_FOLDER, 1);

        final String errorMessage = getTlPreferences().setMMTFolder(
            batchFramework.paths.getMMJ2Path(), runParm.values[0].trim());

        if (errorMessage != null)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_1
                    + UtilConstants.RUNPARM_THEOREM_LOADER_MMT_FOLDER
                    + UtilConstants.ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_2
                    + errorMessage);
    }

    /**
     * edit TheoremLoaderStoreFormulasAsIs RunParm.
     * 
     * @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editTheoremLoaderStoreFormulasAsIs(
        final RunParmArrayEntry runParm)
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_THEOREM_LOADER_STORE_FORMULAS_ASIS, 1);
        if (getTlPreferences().setStoreFormulasAsIs(runParm.values[0].trim()))
            return; // ok, valid!

        final String errorMessage = TlConstants.ERRMSG_INVALID_STORE_FORMULAS_ASIS_1
            + runParm.values[0].trim()
            + TlConstants.ERRMSG_INVALID_STORE_FORMULAS_ASIS_2;

        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_1
                + UtilConstants.RUNPARM_THEOREM_LOADER_STORE_FORMULAS_ASIS
                + UtilConstants.ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_2
                + errorMessage);

    }

    /**
     * edit TheoremLoaderStoreMMIndentAmt RunParm.
     * 
     * @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editTheoremLoaderStoreMMIndentAmt(
        final RunParmArrayEntry runParm)
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_THEOREM_LOADER_STORE_MM_INDENT_AMT, 1);
        if (getTlPreferences().setStoreMMIndentAmt(runParm.values[0].trim()))
            return; // ok, valid!

        final String errorMessage = TlConstants.ERRMSG_INVALID_STORE_MM_INDENT_AMT_1
            + runParm.values[0].trim()
            + TlConstants.ERRMSG_INVALID_STORE_MM_INDENT_AMT_2;

        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_1
                + UtilConstants.RUNPARM_THEOREM_LOADER_STORE_MM_INDENT_AMT
                + UtilConstants.ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_2
                + errorMessage);

    }

    /**
     * edit TheoremLoaderStoreMMRightCol RunParm.
     * 
     * @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editTheoremLoaderStoreMMRightCol(
        final RunParmArrayEntry runParm)
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_THEOREM_LOADER_STORE_MM_RIGHT_COL, 1);
        if (getTlPreferences().setStoreMMRightCol(runParm.values[0].trim()))
            return; // ok, valid!

        final String errorMessage = TlConstants.ERRMSG_INVALID_STORE_MM_RIGHT_COL_1
            + runParm.values[0].trim()
            + TlConstants.ERRMSG_INVALID_STORE_MM_RIGHT_COL_2;

        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_1
                + UtilConstants.RUNPARM_THEOREM_LOADER_STORE_MM_RIGHT_COL
                + UtilConstants.ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_2
                + errorMessage);

    }

    protected void editLoadTheoremsFromMMTFolder(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_LOAD_THEOREMS_FROM_MMT_FOLDER, 1);

        final String label = runParm.values[0].trim();

        String errorMessage = null;
        try {
            final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
                .getLogicalSystem();

            final Messages messages = batchFramework.outputBoss.getMessages();

            final TheoremLoader theoremLoader = getTheoremLoader();

            if (label.equals(UtilConstants.RUNPARM_OPTION_VALUE_ALL))
                theoremLoader
                    .loadTheoremsFromMMTFolder(logicalSystem, messages);
            else
                theoremLoader.loadTheoremsFromMMTFolder(label, logicalSystem,
                    messages);
            batchFramework.outputBoss.printAndClearMessages();
            return;
        } catch (final TheoremLoaderException e) {
            errorMessage = e.getMessage();
        } catch (final LangException e) {
            errorMessage = e.getMessage();
        }

        batchFramework.outputBoss.printAndClearMessages();

        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_1
                + UtilConstants.RUNPARM_LOAD_THEOREMS_FROM_MMT_FOLDER
                + UtilConstants.ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_2
                + errorMessage);
    }

    protected void editExtractTheoremToMMTFolder(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_EXTRACT_THEOREM_TO_MMT_FOLDER, 1);

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        final Theorem theorem = getSelectorTheoremRunParmOption(runParm,
            UtilConstants.RUNPARM_EXTRACT_THEOREM_TO_MMT_FOLDER, 1,
            logicalSystem.getStmtTbl());

        String errorMessage = null;
        try {
            final Messages messages = batchFramework.outputBoss.getMessages();

            final TheoremLoader theoremLoader = getTheoremLoader();

            theoremLoader.extractTheoremToMMTFolder(theorem, logicalSystem,
                messages);

            batchFramework.outputBoss.printAndClearMessages();
            return;
        } catch (final TheoremLoaderException e) {
            errorMessage = e.getMessage();
        } catch (final LangException e) {
            errorMessage = e.getMessage();
        }

        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_1
                + UtilConstants.RUNPARM_EXTRACT_THEOREM_TO_MMT_FOLDER
                + UtilConstants.ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_2
                + errorMessage);
    }

    protected void editUnifyPlusStoreInLogSysAndMMTFolder(
        final RunParmArrayEntry runParm) throws IllegalArgumentException
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_UNIFY_PLUS_STORE_IN_LOG_SYS_AND_MMT_FOLDER, 1);

        Reader proofWorksheetReader = null;
        String errorMessage = null;
        try {
            final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
                .getLogicalSystem();

            final Messages messages = batchFramework.outputBoss.getMessages();

            final ProofAsst proofAsst = batchFramework.proofAsstBoss
                .getProofAsst();

            if (!proofAsst.getInitializedOK())
                proofAsst.initializeLookupTables(messages);

            final TheoremLoader theoremLoader = getTheoremLoader();

            proofWorksheetReader = batchFramework.proofAsstBoss
                .editProofAsstImportFileRunParm(
                    runParm,
                    UtilConstants.RUNPARM_UNIFY_PLUS_STORE_IN_LOG_SYS_AND_MMT_FOLDER,
                    1);

            final String proofWorksheetText = getProofWorksheetText(
                proofWorksheetReader, runParm,
                UtilConstants.RUNPARM_UNIFY_PLUS_STORE_IN_MMT_FOLDER, 1);

            theoremLoader.unifyPlusStoreInLogSysAndMMTFolder(
                proofWorksheetText, logicalSystem, messages, proofAsst,
                runParm.values[0].trim());

            batchFramework.outputBoss.printAndClearMessages();
            closeReader(proofWorksheetReader);
            return;
        } catch (final TheoremLoaderException e) {
            errorMessage = e.getMessage();
        } catch (final LangException e) {
            errorMessage = e.getMessage();
        }

        batchFramework.outputBoss.printAndClearMessages();
        closeReader(proofWorksheetReader);

        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_1
                + UtilConstants.RUNPARM_UNIFY_PLUS_STORE_IN_LOG_SYS_AND_MMT_FOLDER
                + UtilConstants.ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_2
                + errorMessage);
    }

    protected void editUnifyPlusStoreInMMTFolder(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_UNIFY_PLUS_STORE_IN_MMT_FOLDER, 1);

        Reader proofWorksheetReader = null;
        String errorMessage = null;
        try {
            final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
                .getLogicalSystem();

            final Messages messages = batchFramework.outputBoss.getMessages();

            final ProofAsst proofAsst = batchFramework.proofAsstBoss
                .getProofAsst();

            if (!proofAsst.getInitializedOK())
                proofAsst.initializeLookupTables(messages);

            proofWorksheetReader = batchFramework.proofAsstBoss
                .editProofAsstImportFileRunParm(runParm,
                    UtilConstants.RUNPARM_UNIFY_PLUS_STORE_IN_MMT_FOLDER, 1);

            final String proofWorksheetText = getProofWorksheetText(
                proofWorksheetReader, runParm,
                UtilConstants.RUNPARM_UNIFY_PLUS_STORE_IN_MMT_FOLDER, 1);

            final TheoremLoader theoremLoader = getTheoremLoader();

            theoremLoader.unifyPlusStoreInMMTFolder(proofWorksheetText,
                logicalSystem, messages, proofAsst, runParm.values[0].trim());

            batchFramework.outputBoss.printAndClearMessages();
            closeReader(proofWorksheetReader);
            return;
        } catch (final TheoremLoaderException e) {
            errorMessage = e.getMessage();
        } catch (final LangException e) {
            errorMessage = e.getMessage();
        }

        batchFramework.outputBoss.printAndClearMessages();
        closeReader(proofWorksheetReader);

        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_1
                + UtilConstants.RUNPARM_UNIFY_PLUS_STORE_IN_MMT_FOLDER
                + UtilConstants.ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_2
                + errorMessage);
    }

    private String getProofWorksheetText(final Reader proofWorksheetReader,
        final RunParmArrayEntry runParm, final String runParmName,
        final int optionNbr)
    {

        final StringBuilder sb = new StringBuilder(
            UtilConstants.THEOREM_LOADER_BOSS_FILE_BUFFER_SIZE);

        int c;
        try {
            while ((c = proofWorksheetReader.read()) != -1)
                sb.append((char)c);
            return sb.toString();

        } catch (final IOException e) {
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_THEOREM_LOADER_READER_ERROR_1
                    + runParm.values[optionNbr - 1].trim()
                    + UtilConstants.ERRMSG_THEOREM_LOADER_READER_ERROR_2
                    + runParmName
                    + UtilConstants.ERRMSG_THEOREM_LOADER_READER_ERROR_3
                    + e.getMessage());
        }
    }

    private void closeReader(final Reader r) {
        try {
            if (r != null)
                r.close();
        } catch (final IOException e) {}
    }

}
