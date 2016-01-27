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

/*
 * GeneratedProofStmt.java  0.07 08/01/2008
 *
 * Version 0.04:
 *     - Un-nested inner class
 *     - replace ProofWorkStmt.status
 *
 * Nov-01-2007 Version 0.05
 * - add abstract method computeFieldIdCol(int fieldId)
 *   for use in ProofAsstGUI (just in time) cursor
 *   positioning logic.
 *
 * Feb-01-2008 Version 0.06
 * - add tmffReformat().
 *
 * Aug-01-2008 Version 0.07
 * - remove stmtHasError() method.
 */

package mmj.pa;

import java.util.List;

import mmj.lang.ParseTree.RPNStep;
import mmj.lang.Stmt;

/**
 * GeneratedProofStatement is added automatically after successful unification.
 * <p>
 */
public class GeneratedProofStmt extends ProofWorkStmt {
    // boolean dummyField?

    /**
     * Default Constructor.
     *
     * @param w the owner ProofWorksheet
     */
    public GeneratedProofStmt(final ProofWorksheet w) {
        super(w);
    }

    /**
     * Standard Constructor for GeneratedProofStmt.
     *
     * @param w the owner ProofWorksheet
     * @param rpnProof Proof Stmt Array in RPN format
     */
    public GeneratedProofStmt(final ProofWorksheet w,
        final RPNStep[] rpnProof)
    {
        super(w);

        lineCnt = 1;

        stmtText = new StringBuilder(rpnProof.length * 5); // 5=guess
        final int left = w.getRPNProofLeftCol();
        final int right = w.proofAsstPreferences.rpnProofRightCol.get();
        final StringBuilder indentLeft = new StringBuilder(left - 1);
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
            if (ps < rpnProof.length) {
                if (rpnProof[ps].backRef == 0)
                    x = rpnProof[ps].stmt.getLabel();
                else if (rpnProof[ps].backRef < 0)
                    x = -rpnProof[ps].backRef + ":"
                        + rpnProof[ps].stmt.getLabel();
                else
                    x = rpnProof[ps].backRef + "";
            }
            else if (ps > rpnProof.length) {
                stmtText.append("\n");
                lineCnt++;
                break;
            }
            else
                x = PaConstants.END_PROOF_STMT_TOKEN;
            ps++;
            col += x.length();
            if (col == right) {
                stmtText.append(x);
                stmtText.append("\n");
                lineCnt++;
                stmtText.append(indentLeft);
                col = left;
            }
            else {
                if (col > right) {
                    stmtText.append("\n");
                    lineCnt++;
                    stmtText.append(indentLeft);
                    col = left;
                }
                stmtText.append(x);
                stmtText.append(' ');
                col++;
            }
        }
    }

    public GeneratedProofStmt(final ProofWorksheet w,
        final List<Stmt> parenList, final String letters)
    {
        super(w);

        lineCnt = 1;

        stmtText = new StringBuilder(parenList.size() * 5 + letters.length()); // 5=guess
        final int left = w.getRPNProofLeftCol();
        final int right = w.proofAsstPreferences.rpnProofRightCol.get();
        final StringBuilder indentLeft = new StringBuilder(left - 1);
        for (int i = 1; i < left; i++)
            indentLeft.append(' ');

        stmtText.append(PaConstants.GENERATED_PROOF_STMT_TOKEN);
        stmtText.append(' ');
        int col = 4;
        for (; col < left; col++)
            stmtText.append(' ');
        col++;
        stmtText.append('(');

        for (int i = 0; i <= parenList.size(); i++) {
            String label;
            if (i == parenList.size())
                label = ")";
            else
                label = parenList.get(i).getLabel();
            if (col + label.length() < right) {
                stmtText.append(' ');
                col++;
            }
            else {
                stmtText.append("\n");
                lineCnt++;
                stmtText.append(indentLeft);
                col = left;
            }
            stmtText.append(label);
            col += label.length();
        }
        if (col + 1 < right) {
            stmtText.append(' ');
            col++;
        }
        else {
            stmtText.append("\n");
            lineCnt++;
            stmtText.append(indentLeft);
            col = left;
        }
        int lIndex = 0;
        while (true) {
            final int avail = right - col;
            if (lIndex + avail >= letters.length()) {
                stmtText.append(letters.substring(lIndex));
                col += letters.length() - lIndex;
                break;
            }
            stmtText.append(letters.substring(lIndex, lIndex += avail));
            stmtText.append("\n");
            lineCnt++;
            stmtText.append(indentLeft);
            col = left;
        }
        if (col + 2 < right)
            stmtText.append(' ');
        else {
            stmtText.append("\n");
            lineCnt++;
            stmtText.append(indentLeft);
        }
        stmtText.append(PaConstants.END_PROOF_STMT_TOKEN);
        stmtText.append("\n");
    }

    @Override
    public boolean stmtIsIncomplete() {
        return false;
    }

    /**
     * Function used for cursor positioning.
     *
     * @param fieldId value identify ProofWorkStmt field for cursor positioning,
     *            as defined in PaConstants.FIELD_ID_*.
     * @return column of input fieldId or default value of 1 if there is an
     *         error.
     */
    @Override
    public int computeFieldIdCol(final int fieldId) {
        return 1;
    }

    /**
     * Reformats Derivation Step using TMFF.
     */
    @Override
    public void tmffReformat() {}

}
