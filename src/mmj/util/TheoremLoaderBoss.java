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

import static mmj.util.UtilConstants.*;

import java.io.IOException;
import java.io.Reader;
import java.util.function.BooleanSupplier;

import mmj.lang.*;
import mmj.pa.MMJException;
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

    {
        final BooleanSupplier clear = () -> {
            theoremLoader = null;
            tlPreferences = null;
            return false; // not "consumed"
        };
        putCommand(RUNPARM_CLEAR, clear);
        putCommand(RUNPARM_LOAD_FILE, clear);

        putCommand(RUNPARM_THEOREM_LOADER_DJ_VARS_OPTION,
            this::editTheoremLoaderDjVarsOption);

        putCommand(RUNPARM_THEOREM_LOADER_AUDIT_MESSAGES,
            this::editTheoremLoaderAuditMessages);

        putCommand(RUNPARM_THEOREM_LOADER_MMT_FOLDER,
            this::editTheoremLoaderMMTFolder);

        putCommand(RUNPARM_LOAD_THEOREMS_FROM_MMT_FOLDER,
            this::editLoadTheoremsFromMMTFolder);

        putCommand(RUNPARM_EXTRACT_THEOREM_TO_MMT_FOLDER,
            this::editExtractTheoremToMMTFolder);

        putCommand(RUNPARM_THEOREM_LOADER_STORE_FORMULAS_ASIS,
            this::editTheoremLoaderStoreFormulasAsIs);

        putCommand(RUNPARM_THEOREM_LOADER_STORE_MM_INDENT_AMT,
            this::editTheoremLoaderStoreMMIndentAmt);

        putCommand(RUNPARM_THEOREM_LOADER_STORE_MM_RIGHT_COL,
            this::editTheoremLoaderStoreMMRightCol);

        putCommand(RUNPARM_UNIFY_PLUS_STORE_IN_LOG_SYS_AND_MMT_FOLDER,
            this::editUnifyPlusStoreInLogSysAndMMTFolder);

        putCommand(RUNPARM_UNIFY_PLUS_STORE_IN_MMT_FOLDER,
            this::editUnifyPlusStoreInMMTFolder);
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

        if (tlPreferences == null)
            tlPreferences = new TlPreferences(
                batchFramework.logicalSystemBoss.getLogicalSystem(),
                batchFramework.storeBoss.getStore());

        return tlPreferences;
    }

    /**
     * Fetch a TheoremLoader object.
     *
     * @return TheoremLoader object, ready to go, or null;.
     */
    public TheoremLoader getTheoremLoader() {

        if (theoremLoader != null)
            return theoremLoader;

        final TlPreferences tlPreferences = getTlPreferences();

        theoremLoader = new TheoremLoader(tlPreferences);

        batchFramework.outputBoss.printAndClearMessages();

        return theoremLoader;
    }

    /**
     * edit TheoremLoaderDjVarsOption RunParm.
     */
    protected void editTheoremLoaderDjVarsOption() {
        try {
            getTlPreferences().djVarsOption.setString(get(1));
        } catch (final IllegalArgumentException e) {
            throw error(TlConstants.ERRMSG_INVALID_DJ_VARS_OPTION, get(1));
        }
    }

    /**
     * edit TheoremLoaderAuditMessages RunParm.
     */
    protected void editTheoremLoaderAuditMessages() {
        try {
            if (getTlPreferences().auditMessages.setString(get(1)))
                return; // ok, valid!
        } catch (final IllegalArgumentException e) {}
        throw error(TlConstants.ERRMSG_INVALID_BOOLEAN, get(1));

    }

    /**
     * Validate Theorem Loader MMT Folder Runparm.
     */
    protected void editTheoremLoaderMMTFolder() {
        try {
            getTlPreferences().mmtFolder
                .set(new MMTFolder(batchFramework.paths.getMMJ2Path(), get(1)));
        } catch (final TheoremLoaderException e) {
            throw error(e);
        }
    }

    /**
     * edit TheoremLoaderStoreFormulasAsIs RunParm.
     */
    protected void editTheoremLoaderStoreFormulasAsIs() {
        try {
            if (getTlPreferences().storeFormulasAsIs.setString(get(1)))
                return; // ok, valid!
        } catch (final IllegalArgumentException e) {}
        throw error(TlConstants.ERRMSG_INVALID_BOOLEAN, get(1));

    }

    /**
     * edit TheoremLoaderStoreMMIndentAmt RunParm.
     */
    protected void editTheoremLoaderStoreMMIndentAmt() {
        getTlPreferences().storeMMIndentAmt.set(getInt(1));
    }

    /**
     * edit TheoremLoaderStoreMMRightCol RunParm.
     */
    protected void editTheoremLoaderStoreMMRightCol() {
        getTlPreferences().storeMMRightCol.set(getInt(1));
    }

    protected void editLoadTheoremsFromMMTFolder() {
        final String label = get(1);

        try {
            final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
                .getLogicalSystem();

            final Messages messages = batchFramework.outputBoss.getMessages();

            final TheoremLoader theoremLoader = getTheoremLoader();

            if (label.equals(UtilConstants.RUNPARM_OPTION_VALUE_ALL))
                theoremLoader.loadTheoremsFromMMTFolder(logicalSystem,
                    messages);
            else
                theoremLoader.loadTheoremsFromMMTFolder(label, logicalSystem,
                    messages);
            batchFramework.outputBoss.printAndClearMessages();
        } catch (final TheoremLoaderException e) {
            throw error(e);
        }
    }

    protected void editExtractTheoremToMMTFolder()
        throws IllegalArgumentException
    {
        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        final Theorem theorem = getSelectorTheorem(1,
            logicalSystem.getStmtTbl());

        try {
            final Messages messages = batchFramework.outputBoss.getMessages();

            final TheoremLoader theoremLoader = getTheoremLoader();

            theoremLoader.extractTheoremToMMTFolder(theorem, logicalSystem,
                messages);

            batchFramework.outputBoss.printAndClearMessages();
        } catch (final MMJException e) {
            throw error(e);
        }
    }

    protected void editUnifyPlusStoreInLogSysAndMMTFolder()
        throws IllegalArgumentException
    {
        require(1);
        try {
            final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
                .getLogicalSystem();

            final Messages messages = batchFramework.outputBoss.getMessages();

            final ProofAsst proofAsst = batchFramework.proofAsstBoss
                .getProofAsst();

            if (!proofAsst.getInitializedOK())
                proofAsst.initializeLookupTables(messages);

            final TheoremLoader theoremLoader = getTheoremLoader();

            final String proofWorksheetText = getProofWorksheetText(1, 1);

            theoremLoader.unifyPlusStoreInLogSysAndMMTFolder(proofWorksheetText,
                logicalSystem, messages, proofAsst, runParm.values[0].trim());

            batchFramework.outputBoss.printAndClearMessages();
        } catch (final MMJException e) {
            throw error(e);
        }
    }

    protected void editUnifyPlusStoreInMMTFolder()
        throws IllegalArgumentException
    {

        require(1);

        try {
            final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
                .getLogicalSystem();

            final Messages messages = batchFramework.outputBoss.getMessages();

            final ProofAsst proofAsst = batchFramework.proofAsstBoss
                .getProofAsst();

            if (!proofAsst.getInitializedOK())
                proofAsst.initializeLookupTables(messages);

            final String proofWorksheetText = getProofWorksheetText(1, 1);

            final TheoremLoader theoremLoader = getTheoremLoader();

            theoremLoader.unifyPlusStoreInMMTFolder(proofWorksheetText,
                logicalSystem, messages, proofAsst, get(1));

            batchFramework.outputBoss.printAndClearMessages();
        } catch (final MMJException e) {
            throw error(e);
        }
    }

    private String getProofWorksheetText(final int valueFieldNbr,
        final int optionNbr)
    {
        try (Reader proofWorksheetReader = batchFramework.proofAsstBoss
            .getImportFile(valueFieldNbr))
        {

            final StringBuilder sb = new StringBuilder(
                UtilConstants.THEOREM_LOADER_BOSS_FILE_BUFFER_SIZE);

            int c;
            while ((c = proofWorksheetReader.read()) != -1)
                sb.append((char)c);
            return sb.toString();
        } catch (final IOException e) {
            throw error(e, UtilConstants.ERRMSG_THEOREM_LOADER_READER_ERROR,
                get(optionNbr), e.getMessage());
        }
    }
}
