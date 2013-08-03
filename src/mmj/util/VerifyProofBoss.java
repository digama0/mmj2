//********************************************************************/
//* Copyright (C) 2005, 2006, 2008                                   */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * VerifyProofBoss.java  0.04 08/01/2008
 *
 * Dec-03-2005
 * --> Added getVerifyProofs() for Proof Assistant's usage.
 *
 * Version 0.04 -- 08/01/2008
 * --> Modify to load ProofVerifier into LogicalSystem
 *     after VerifyProof has been performed -- then
 *     new theorem adds, via TheoremLoader, will know
 *     to do VerifyProof.
 */

package mmj.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import mmj.lang.*;
import mmj.mmio.MMIOException;
import mmj.verify.VerifyProofs;

/**
 * Responsible for building, loading, maintaining and fetching ProofVerifier,
 * and for executing RunParms involving it.
 * <ul>
 * <li>If non-executable parm, validate, store and "consume"
 * <li>If Verify Proof or VerifyParse parm, validate, run, get error status,
 * print-and-clear messages, and "consume". Remember that Messages,
 * LogicalSystem and other objects may have changed. Don't worry about whether
 * or not file is loaded, the LogicalSystemBoss will throw an exception if
 * attempt is made to retrieve LogicalSystem if it is not loaded and error free.
 * <li>If clear, set ProofVerifier to null.
 * </ul>
 */
public class VerifyProofBoss extends Boss {

    protected VerifyProofs verifyProofs;

    protected boolean allProofsVerifiedSuccessfully;

    protected boolean allStatementsParsedSuccessfully;

    /**
     * Constructor with BatchFramework for access to environment.
     * 
     * @param batchFramework for access to environment.
     */
    public VerifyProofBoss(final BatchFramework batchFramework) {
        super(batchFramework);
    }

    /**
     * Returns true if all proofs verified successfully.
     * 
     * @return true if all proofs verified successfully.
     */
    public boolean getAllProofsVerifiedSuccessfully() {
        return allProofsVerifiedSuccessfully;
    }

    /**
     * Returns true if all statements parsed successfully.
     * 
     * @return true if all statements parsed successfully.
     */
    public boolean getAllStatementsParsedSuccessfully() {
        return allStatementsParsedSuccessfully;
    }

    /**
     * Return initialized VerifyProofs object
     * 
     * @return VerifyProofs object
     */
    public VerifyProofs getVerifyProofs() {
        initializeVerifyProofsIfNeeded();
        return verifyProofs;
    }

    /**
     * Executes a single command from the RunParmFile.
     * 
     * @param runParm the RunParmFile line to execute.
     */
    public boolean doRunParmCommand(final RunParmArrayEntry runParm)

    throws IllegalArgumentException, MMIOException, FileNotFoundException,
        IOException, VerifyException
    {

        if (runParm.name.compareToIgnoreCase(UtilConstants.RUNPARM_CLEAR) == 0)
        {
            verifyProofs = null;
            allProofsVerifiedSuccessfully = false;
            allStatementsParsedSuccessfully = false;
            return false; // not "consumed"
        }
        if (runParm.name.compareToIgnoreCase(UtilConstants.RUNPARM_LOAD_FILE) == 0)
        {
            allProofsVerifiedSuccessfully = false;
            allStatementsParsedSuccessfully = false;
            return false; // not "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_VERIFY_PROOF) == 0)
        {
            doVerifyProof(runParm);
            return true; // not "consumed"
        }
        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_VERIFY_PARSE) == 0)
        {
            doVerifyParse(runParm);
            return true; // not "consumed"
        }

        return false;

    }

    /**
     * Executes the VerifyProof command, prints any messages, etc.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     * @throws IOException if an error occurred
     * @throws VerifyException if an error occurred
     */
    public void doVerifyProof(final RunParmArrayEntry runParm)
        throws IllegalArgumentException, IOException, VerifyException
    {

        editRunParmValuesLength(runParm, UtilConstants.RUNPARM_VERIFY_PROOF, 1);

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        initializeVerifyProofsIfNeeded();

        final Messages messages = batchFramework.outputBoss.getMessages();

        if (!batchFramework.logicalSystemBoss.getLoadProofs())
            messages
                .accumInfoMessage(UtilConstants.ERRMSG_IGNORING_VERIFY_PROOF_RUNPARM);
        else {
            final String optionValue = runParm.values[0].trim();
            if (optionValue.compareTo(UtilConstants.RUNPARM_OPTION_VALUE_ALL) == 0)
            {
                verifyProofs.verifyAllProofs(messages,
                    logicalSystem.getStmtTbl());
                if (messages.getErrorMessageCnt() == 0)
                    allProofsVerifiedSuccessfully = true;
                else
                    allProofsVerifiedSuccessfully = true;
            }
            else {
                final Theorem theorem = editRunParmValueTheorem(optionValue,
                    UtilConstants.RUNPARM_VERIFY_PROOF, logicalSystem);
                final String errmsg = verifyProofs.verifyOneProof(theorem);
                if (errmsg != null) {
                    messages.accumErrorMessage(errmsg);
                    allProofsVerifiedSuccessfully = false;
                }
            }
        }

        logicalSystem.setProofVerifier(verifyProofs);

        batchFramework.outputBoss.printAndClearMessages();

    }

    /**
     * Executes the VerifyParse command, prints any messages, etc.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     * @throws IOException if an error occurred
     * @throws VerifyException if an error occurred
     */
    public void doVerifyParse(final RunParmArrayEntry runParm)
        throws IllegalArgumentException, IOException, VerifyException
    {

        editRunParmValuesLength(runParm, UtilConstants.RUNPARM_VERIFY_PARSE, 1);

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        initializeVerifyProofsIfNeeded();

        final Messages messages = batchFramework.outputBoss.getMessages();

        final String optionValue = runParm.values[0].trim();
        if (optionValue.compareTo(UtilConstants.RUNPARM_OPTION_VALUE_ALL) == 0)
        {
            verifyProofs.verifyAllExprRPNAsProofs(messages,
                logicalSystem.getStmtTbl());
            if (messages.getErrorMessageCnt() == 0)
                allStatementsParsedSuccessfully = true;
            else
                allStatementsParsedSuccessfully = false;

        }
        else {
            final Stmt stmt = editRunParmValueStmt(optionValue,
                UtilConstants.RUNPARM_VERIFY_PARSE, logicalSystem);
            final String errmsg = verifyProofs.verifyExprRPNAsProof(stmt);
            if (errmsg != null) {
                messages.accumErrorMessage(errmsg);
                allStatementsParsedSuccessfully = false;
            }
        }

        logicalSystem.setProofVerifier(verifyProofs);

        batchFramework.outputBoss.printAndClearMessages();

    }

    protected void initializeVerifyProofsIfNeeded() {
        if (verifyProofs == null) {
            verifyProofs = new VerifyProofs();
            allProofsVerifiedSuccessfully = false;
            allStatementsParsedSuccessfully = false;
        }
    }
}
