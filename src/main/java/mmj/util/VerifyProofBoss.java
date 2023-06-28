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

import static mmj.util.UtilConstants.*;

import mmj.lang.*;
import mmj.verify.VerifyException;
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

        putCommand(RUNPARM_CLEAR, () -> {
            verifyProofs = null;
            allProofsVerifiedSuccessfully = false;
            allStatementsParsedSuccessfully = false;
            return false; // not "consumed"
        });
        putCommand(RUNPARM_LOAD_FILE, () -> {
            allProofsVerifiedSuccessfully = false;
            allStatementsParsedSuccessfully = false;
            return false; // not "consumed"
        });

        putCommand(RUNPARM_VERIFY_PROOF, this::doVerifyProof);
        putCommand(RUNPARM_VERIFY_PARSE, this::doVerifyParse);

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
     * Executes the VerifyProof command, prints any messages, etc.
     */
    public void doVerifyProof() {

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        initializeVerifyProofsIfNeeded();

        final Messages messages = batchFramework.outputBoss.getMessages();

        if (!batchFramework.logicalSystemBoss.getLoadProofs())
            messages.accumMessage(ERRMSG_IGNORING_VERIFY_PROOF_RUNPARM);
        else if (get(1).equals(RUNPARM_OPTION_VALUE_ALL)) {
            verifyProofs.verifyAllProofs(messages, logicalSystem.getStmtTbl());
            allProofsVerifiedSuccessfully = messages.getErrorMessageCnt() == 0;
        }
        else {
            final Theorem theorem = getTheorem(1, logicalSystem);
            final VerifyException errmsg = verifyProofs.verifyOneProof(theorem);
            if (errmsg != null) {
                messages.accumException(errmsg);
                allProofsVerifiedSuccessfully = false;
            }
        }

        logicalSystem.setProofVerifier(verifyProofs);

        batchFramework.outputBoss.printAndClearMessages();

    }

    /**
     * Executes the VerifyParse command, prints any messages, etc.
     */
    public void doVerifyParse() {

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        initializeVerifyProofsIfNeeded();

        final Messages messages = batchFramework.outputBoss.getMessages();

        if (get(1).equals(RUNPARM_OPTION_VALUE_ALL)) {
            verifyProofs.verifyAllExprRPNAsProofs(messages,
                logicalSystem.getStmtTbl());
            allStatementsParsedSuccessfully = messages
                .getErrorMessageCnt() == 0;
        }
        else {
            final Stmt stmt = getStmt(1, logicalSystem);
            final VerifyException errmsg = verifyProofs
                .verifyExprRPNAsProof(stmt);
            if (errmsg != null) {
                messages.accumException(errmsg);
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
