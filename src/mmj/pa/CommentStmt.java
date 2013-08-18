//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

// =================================================
// ===                   Class                   ===
// ===                                           ===
// ===        C o m m e n t   S t m t            ===
// ===                                           ===
// =================================================

/*
 * CommentStmt.java  0.07 08/01/2008
 * {@code 
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
 * - remove stmtHasError().
 * }

 * CommentStmt represents a comment in a proof.
 * <p>
 * Comments begin with "*" in column 1, and do
 * nothing except be ignored.
 * <p>
 */

package mmj.pa;

import java.io.*;

public class CommentStmt extends ProofWorkStmt {

    /**
     * Default Constructor.
     * 
     * @param w the owner ProofWorksheet
     */
    public CommentStmt(final ProofWorksheet w) {
        super(w);
    }

    /**
     * Creates a CommentStmt using an input String.
     * <p>
     * The input String is parsed into lines so that lineCnt can be incremented,
     * and newline characters are re-inserted into the text.
     * <p>
     * The comment text is prefixed by "* " on the first output line and "  " on
     * subsequent lines.
     * <p>
     * If doublespace requested, then an extra newline is inserted after the end
     * of the comment text, and lineCnt is incremented.
     * 
     * @param w the owner ProofWorksheet
     * @param s String to be inserted into the Comment text. Should not include
     *            the "*" that denotes a ProofWorksheet CommentStmt.
     * @param doubleSpace set to true if extra newline should be added after the
     *            comment text.
     */
    public CommentStmt(final ProofWorksheet w, final String s,
        final boolean doubleSpace)
    {
        super(w);

        stmtText = new StringBuilder(s.length() + 2);

        String linePrefix = PaConstants.COMMENT_STMT_TOKEN_PREFIX + " ";

        final String secondLinePrefix = "  ";

        try {
            final LineNumberReader r = new LineNumberReader(new StringReader(s));

            String line;
            while ((line = r.readLine()) != null) {
                stmtText.append(linePrefix).append(line.trim()).append("\n");
                lineCnt++;
                linePrefix = secondLinePrefix;
            }
        } catch (final IOException e) {
            stmtText.append(PaConstants.PROOF_WORKSHEET_COMMENT_STMT_IO_ERROR);
            stmtText.append("\n");
        }

        if (lineCnt == 1) {
            stmtText.append("\n");
            lineCnt++;
        }
        if (doubleSpace)
            stmtText.append("\n");
        else
            lineCnt--;
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
