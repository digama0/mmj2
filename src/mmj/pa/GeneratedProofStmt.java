//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

// =======================================================
// ===                   Class                         ===
// ===                                                 ===
// ===   G e n e r a t e d   P r o o f   S t m t       ===
// ===                                                 ===
// =======================================================

/**
 *  GeneratedProofStmt.java  0.07 08/01/2008
 *  <code>
 *  Version 0.04:
 *      - Un-nested inner class
 *      - replace ProofWorkStmt.status
 *
 *  Nov-01-2007 Version 0.05
 *  - add abstract method computeFieldIdCol(int fieldId)
 *    for use in ProofAsstGUI (just in time) cursor
 *    positioning logic.
 *
 *  Feb-01-2008 Version 0.06
 *  - add tmffReformat().
 *
 *  Aug-01-2008 Version 0.07
 *  - remove stmtHasError() method.
 *
 *  </code>
 *  GeneratedProofStatement is added automatically after
 *  successful unification.
 *  <p>
 */

package mmj.pa;

import mmj.lang.Stmt;

public class GeneratedProofStmt extends ProofWorkStmt {
    // boolean dummyField?

    /**
     *  Default Constructor.
     */
    public GeneratedProofStmt(final ProofWorksheet w) {
        super(w);
    }

    /**
     *  Standard Constructor for GeneratedProofStmt.
     *
     *  @param rpnProof Proof Stmt Array in RPN format
     */
    public GeneratedProofStmt(final ProofWorksheet w, final Stmt[] rpnProof) {
        super(w);

        lineCnt = 1;

        stmtText = new StringBuffer(rpnProof.length * 5); // 5=guess
        final int left = w.proofAsstPreferences.getRPNProofLeftCol();
        final int right = w.proofAsstPreferences.getRPNProofRightCol();
        final StringBuffer indentLeft = new StringBuffer(left - 1);
        for (int i = 1; i < left; i++)
            indentLeft.append(' ');

        stmtText.append(PaConstants.GENERATED_PROOF_STMT_TOKEN);
        stmtText.append(' ');
        int col = 4;
        for (; col < left; col++)
            stmtText.append(' ');

        String x;
        int ps = 0;
        while (true) {
            if (ps < rpnProof.length)
                x = rpnProof[ps].getLabel();
            else if (ps > rpnProof.length) {
                stmtText.append(PaConstants.PROOF_WORKSHEET_NEW_LINE);
                ++lineCnt;
                break;
            }
            else
                x = PaConstants.END_PROOF_STMT_TOKEN;
            ++ps;
            col += x.length();
            if (col == right) {
                stmtText.append(x);
                stmtText.append(PaConstants.PROOF_WORKSHEET_NEW_LINE);
                ++lineCnt;
                stmtText.append(indentLeft);
                col = left;
            }
            else {
                if (col > right) {
                    stmtText.append(PaConstants.PROOF_WORKSHEET_NEW_LINE);
                    ++lineCnt;
                    stmtText.append(indentLeft);
                    col = left;
                }
                stmtText.append(x);
                stmtText.append(' ');
                ++col;
            }
        }
    }

    @Override
    public boolean stmtIsIncomplete() {
        return false;
    }

    /**
     *  Function used for cursor positioning.
     *  <p>
     *
     *  @param fieldId value identify ProofWorkStmt field
     *         for cursor positioning, as defined in
     *         PaConstants.FIELD_ID_*.
     *
     *  @return column of input fieldId or default value
     *         of 1 if there is an error.
     */
    @Override
    public int computeFieldIdCol(final int fieldId) {
        return 1;
    }

    /**
     *  Reformats Derivation Step using TMFF.
     */
    @Override
    public void tmffReformat() {}

}
