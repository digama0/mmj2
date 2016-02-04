//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * ProofVerifier.java  0.02 08/24/2005
 */

package mmj.lang;

import java.util.Map;

import mmj.verify.VerifyException;

/**
 * Interface to proof verification.
 * <p>
 * Refer to mmj.verify.ProofConstants.java for information about proof
 * parameters and error messages.
 */
public interface ProofVerifier {

    /**
     * Verify a single proof.
     *
     * @param theorem Theorem object reference.
     * @return String error message if error(s), or null.
     */
    VerifyException verifyOneProof(Theorem theorem);

    /**
     * Verify all proofs in Statement Table.
     *
     * @param messages Messages object for output error messages.
     * @param stmtTbl Statement Table (map).
     */
    void verifyAllProofs(Messages messages, Map<String, Stmt> stmtTbl);

    /**
     * Verify grammatical parse RPN as if it were a proof.
     * <p>
     * Note: even VarHyp and Syntax Axioms are assigned default RPN's, so this
     * should work -- unless there are errors in the Metamath file, or in the
     * grammar itself.
     *
     * @param exprRPNStmt Stmt with RPN to verify.
     * @return String error message if error(s), or null.
     */
    VerifyException verifyExprRPNAsProof(Stmt exprRPNStmt);

    /**
     * Verify all Statements' grammatical parse RPNs.
     * <p>
     * Note: even VarHyp and Syntax Axioms are assigned default RPN's, so this
     * should work -- unless there are errors in the Metamath file, or in the
     * grammar itself.
     *
     * @param messages Messages object for output error messages.
     * @param stmtTbl Statement Table (map).
     */
    void verifyAllExprRPNAsProofs(Messages messages, Map<String, Stmt> stmtTbl);

}
