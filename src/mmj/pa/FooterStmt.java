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
// ===          F o o t e r   S t m t                  ===
// ===                                                 ===
// =======================================================

/*
 * FooterStmt.java  0.07 08/01/2008
 *
 * Version 0.04:
 *     - Un-nested inner class
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
 * - remove stmtHasError() method
 */

package mmj.pa;

import java.io.IOException;

import mmj.mmio.MMIOException;

/**
 * FooterStmt is simply the "$)" in column 1 of the last line of a proof.
 * <p>
 * Note: the purpose of FooterStmt is to provide a terminator so that multiple
 * proofs can be stored in a file (and this is done for batch volume testing.)
 */
public class FooterStmt extends ProofWorkStmt {

    /**
     * Default Constructor.
     *
     * @param w the owning ProofWorksheet
     */
    public FooterStmt(final ProofWorksheet w) {
        super(w);
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

    /**
     * Loads FooterStmt with standard contents of a FooterStmt.
     */
    public void loadDefault() {
        stmtText = new StringBuilder(
            PaConstants.PROOF_TEXT_FOOTER.length() + 2);

        stmtText.append(PaConstants.PROOF_TEXT_FOOTER);
        stmtText.append("\n");

        w.doubleSpaceQedStep();
    }

    /**
     * Load Footer statement with Tokenizer input
     * <p>
     * Output/Updates
     * <ul>
     * <li>accum tokens and whitespace into stmtText, checking for extra tokens
     * or premature EOF
     * <li>throw exception if structural error found.
     * <li>set status to 0 if no errors
     * <li>return nextToken after trailing whitespace. the start of the next
     * statement.
     * <li>keep track of lineCnt, number of lines in the statement.
     * <li>position cursor to field in error, as needed.
     * <li>Load Footer statement fields.
     * </ul>
     *
     * @param firstToken first token of the statement
     * @return first token of the next statement.
     */
    @Override
    public String load(final String firstToken)
        throws IOException, MMIOException, ProofAsstException
    {
        final int currLineNbr = (int)w.proofTextTokenizer.getCurrentLineNbr();

        stmtText = new StringBuilder();

        final String nextT = loadStmtTextGetNextStmt(firstToken);

        updateLineCntUsingTokenizer(currLineNbr, nextT);
        return nextT;
    }
}
