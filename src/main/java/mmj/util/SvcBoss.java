//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SvcBoss.java  0.02 11/01/2011
 *
 * Version 0.01: 08/01/2008
 *     - New.
 * Version 0.02: Nov-01-2011 -
 *     -->Modified for mmj2 Paths Enhancement:
 *        added svcPath argument to editExistingFolderRunParm()
 *        call in editSvcFolder().
 */

package mmj.util;

import static mmj.util.UtilConstants.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import mmj.lang.*;
import mmj.pa.ProofAsst;
import mmj.pa.ProofAsstPreferences;
import mmj.svc.SvcCallback;
import mmj.tl.TheoremLoader;
import mmj.tl.TlPreferences;
import mmj.verify.Grammar;
import mmj.verify.VerifyProofs;

/**
 * Manages access to the facilities which allow use of mmj2 as a service.
 */
public class SvcBoss extends Boss {

    private SvcCallback svcCallback;
    private String svcCallbackClass;
    private File svcFolder;
    private Map<String, String> svcArgs = new HashMap<>();

    /**
     * Constructor with BatchFramework for access to environment.
     *
     * @param batchFramework for access to environment.
     */
    public SvcBoss(final BatchFramework batchFramework) {
        super(batchFramework);

        putCommand(RUNPARM_CLEAR, () -> {
            // erase input parameters but NOT SvcCallback
            // (unless derived from svcCallbackClass). SvcCallback
            // can be passed via call from BatchMMJ2
            // as well as via RunParm.
            if (svcCallbackClass != null) {
                svcCallbackClass = null;
                svcCallback = null;
            }
            svcArgs = new HashMap<>();
            return false; // not "consumed"
        });

        putCommand(RUNPARM_SVC_FOLDER, this::editSvcFolder);

        putCommand(RUNPARM_SVC_CALLBACK_CLASS, this::editSvcCallbackClass);

        putCommand(RUNPARM_SVC_ARG, this::editSvcArg);

        putCommand(RUNPARM_SVC_CALL, this::editSvcCall);
    }

    /**
     * Validate SvcFolder.
     */
    protected void editSvcFolder() {
        svcFolder = getExistingFolder(batchFramework.paths.getSvcPath(), 1);
    }

    /**
     * Validate SvcCallbackClass name and instantiate one of them for use later.
     */
    protected void editSvcCallbackClass() {
        svcCallbackClass = get(1);
        try {
            svcCallback = (SvcCallback)Class.forName(svcCallbackClass)
                .newInstance();
        } catch (final ClassCastException e) {
            throw error(ERRMSG_SVC_CALLBACK_CLASS_CAST_ERROR, e,
                svcCallbackClass, e.getMessage());
        } catch (final Exception e) {
            throw error(ERRMSG_SVC_CALLBACK_CLASS_INIT_ERROR, e,
                svcCallbackClass, e.getMessage());
        }

    }

    /**
     * Sets the SvcCallback object which allows designated user code to use mmj2
     * as a service.
     *
     * @param svcCallback entry point to user code.
     */
    public void setSvcCallback(final SvcCallback svcCallback) {
        this.svcCallback = svcCallback;
    }

    /**
     * Gets the SvcCallback object which allows designated user code to use mmj2
     * as a service.
     *
     * @return svcCallback entry point to user code.
     */
    public SvcCallback getSvcCallback() {
        return svcCallback;
    }

    /**
     * Validate arguments for SvcCallback.
     */
    protected void editSvcArg() {
        require(2);
        if (svcArgs.put(get(1), get(2)) != null)
            throw error(ERRMSG_SVC_ARG_ERROR, get(1), get(2));
    }

    /**
     * Call the designated SvcCallback object.
     */
    protected void editSvcCall() {

        final Messages messages = batchFramework.outputBoss.getMessages();

        final OutputBoss outputBoss = batchFramework.outputBoss;

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        final VerifyProofs verifyProofs = batchFramework.verifyProofBoss
            .getVerifyProofs();

        final Grammar grammar = batchFramework.grammarBoss.getGrammar();

        final ProofAsst proofAsst = batchFramework.proofAsstBoss.getProofAsst();
        if (proofAsst == null)
            throw error(ERRMSG_SVC_CALL_PROOF_ASST_MISSING);

        if (!proofAsst.getInitializedOK())
            proofAsst.initializeLookupTables(messages);

        final WorkVarManager workVarManager = batchFramework.workVarBoss
            .getWorkVarManager();

        final ProofAsstPreferences proofAsstPreferences = batchFramework.proofAsstBoss
            .getProofAsstPreferences();

        final TheoremLoader theoremLoader = batchFramework.theoremLoaderBoss
            .getTheoremLoader();
        if (theoremLoader == null)
            throw error(ERRMSG_SVC_CALL_THEOREM_LOADER_MISSING);

        final TlPreferences tlPreferences = batchFramework.theoremLoaderBoss
            .getTlPreferences();

        svcCallback.go(messages, outputBoss, logicalSystem, verifyProofs,
            grammar, workVarManager, proofAsstPreferences, proofAsst,
            tlPreferences, theoremLoader, svcFolder, svcArgs);
    }
}
