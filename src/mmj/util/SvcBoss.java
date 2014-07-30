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

import java.io.File;
import java.util.*;

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
    private List<String> svcArgKeyList;
    private List<String> svcArgValueList;
    private Map<String, String> svcArgs;

    /**
     * Constructor with BatchFramework for access to environment.
     * 
     * @param batchFramework for access to environment.
     */
    public SvcBoss(final BatchFramework batchFramework) {
        super(batchFramework);
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
     * Executes a single command from the RunParmFile.
     * 
     * @param runParm the RunParmFile line to execute.
     * @return boolean "consumed" indicating that the input runParm should not
     *         be processed again.
     */
    @Override
    public boolean doRunParmCommand(final RunParmArrayEntry runParm)
        throws IllegalArgumentException, VerifyException
    {

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_CLEAR.name()) == 0)
        {

            // erase input parameters but NOT SvcCallback
            // (unless derived from svcCallbackClass). SvcCallback
            // can be passed via call from BatchMMJ2
            // as well as via RunParm.
            if (svcCallbackClass != null) {
                svcCallbackClass = null;
                svcCallback = null;
            }
            svcArgKeyList = null;
            svcArgValueList = null;
            svcArgs = null;
            return false; // not "consumed"
        }

        if (runParm.name.compareToIgnoreCase(UtilConstants.RUNPARM_SVC_FOLDER
            .name()) == 0)
        {
            editSvcFolder(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_SVC_CALLBACK_CLASS
                .name()) == 0)
        {
            editSvcCallbackClass(runParm);
            return true;
        }

        if (runParm.name.compareToIgnoreCase(UtilConstants.RUNPARM_SVC_ARG
            .name()) == 0)
        {
            editSvcArg(runParm);
            return true;
        }

        if (runParm.name.compareToIgnoreCase(UtilConstants.RUNPARM_SVC_CALL
            .name()) == 0)
        {
            editSvcCall(runParm);
            return true;
        }

        return false;
    }

    /**
     * Validate SvcFolder.
     * 
     * @param runParm run parm parsed into RunParmArrayEntry object
     * @throws VerifyException if an error occurred
     */
    protected void editSvcFolder(final RunParmArrayEntry runParm)
        throws VerifyException
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_SVC_FOLDER.name(), 1);

        svcFolder = editExistingFolderRunParm(
            batchFramework.paths.getSvcPath(), runParm,
            UtilConstants.RUNPARM_SVC_FOLDER.name(), 1); // field nbr of folder
    }

    /**
     * Validate SvcCallbackClass name and instantiate one of them for use later.
     * 
     * @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editSvcCallbackClass(final RunParmArrayEntry runParm) {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_SVC_CALLBACK_CLASS.name(), 1);

        svcCallbackClass = runParm.values[0].trim();
        Object o;
        try {
            o = Class.forName(svcCallbackClass).newInstance();
        } catch (final Throwable t) {
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_SVC_CALLBACK_CLASS_INIT_ERROR_1
                    + svcCallbackClass
                    + UtilConstants.ERRMSG_SVC_CALLBACK_CLASS_INIT_ERROR_2
                    + t.getMessage());
        }
        try {
            svcCallback = (SvcCallback)o;
        } catch (final Throwable t) {
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_SVC_CALLBACK_CLASS_CAST_ERROR_1
                    + svcCallbackClass
                    + UtilConstants.ERRMSG_SVC_CALLBACK_CLASS_CAST_ERROR_2
                    + t.getMessage());
        }

    }

    /**
     * Validate arguments for SvcCallback.
     * 
     * @param runParm run parm parsed into RunParmArrayEntry object
     * @throws VerifyException if an error occurred
     */
    protected void editSvcArg(final RunParmArrayEntry runParm)
        throws VerifyException
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_SVC_FOLDER.name(), 2);

        if (svcArgKeyList == null) {
            svcArgKeyList = new ArrayList<String>();
            svcArgValueList = new ArrayList<String>();
        }

        if (runParm.values[0].length() == 0
            || runParm.values[0].trim().length() == 0
            || svcArgKeyList.contains(runParm.values[0]))
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_SVC_ARG_ERROR_1 + runParm.values[0]
                    + UtilConstants.ERRMSG_SVC_ARG_ERROR_2 + runParm.values[1]);

        svcArgKeyList.add(runParm.values[0]);
        svcArgValueList.add(runParm.values[1]);
    }

    /**
     * Call the designated SvcCallback object.
     * 
     * @param runParm run parm parsed into RunParmArrayEntry object
     * @throws VerifyException if an error occurred
     */
    protected void editSvcCall(final RunParmArrayEntry runParm)
        throws VerifyException
    {

        final Messages messages = batchFramework.outputBoss.getMessages();

        final OutputBoss outputBoss = batchFramework.outputBoss;

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        final VerifyProofs verifyProofs = batchFramework.verifyProofBoss
            .getVerifyProofs();

        final Grammar grammar = batchFramework.grammarBoss.getGrammar();

        final ProofAsst proofAsst = batchFramework.proofAsstBoss.getProofAsst();
        if (proofAsst == null)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_SVC_CALL_PROOF_ASST_MISSING_1);

        if (!proofAsst.getInitializedOK())
            proofAsst.initializeLookupTables(messages);

        final WorkVarManager workVarManager = batchFramework.workVarBoss
            .getWorkVarManager();

        final ProofAsstPreferences proofAsstPreferences = batchFramework.proofAsstBoss
            .getProofAsstPreferences();

        final TheoremLoader theoremLoader = batchFramework.theoremLoaderBoss
            .getTheoremLoader();
        if (theoremLoader == null)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_SVC_CALL_THEOREM_LOADER_MISSING_1);

        final TlPreferences tlPreferences = batchFramework.theoremLoaderBoss
            .getTlPreferences();

        if (svcArgKeyList == null) {
            svcArgKeyList = new ArrayList<String>();
            svcArgValueList = new ArrayList<String>();
        }
        int n = svcArgKeyList.size();
        if (n < 12)
            n = 12;
        n = 4 * n / 3; // 75% load capacity
        svcArgs = new HashMap<String, String>(n);
        for (int i = 0; i < svcArgKeyList.size(); i++)
            svcArgs.put(svcArgKeyList.get(i), svcArgValueList.get(i));

        svcCallback.go(messages, outputBoss, logicalSystem, verifyProofs,
            grammar, workVarManager, proofAsstPreferences, proofAsst,
            tlPreferences, theoremLoader, svcFolder, svcArgs);
    }
}
