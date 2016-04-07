//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * ProofWorkStmt.java  0.07 08/01/2008
 *
 * Version 0.04:
 *     - Un-nested inner class
 *     - replace ProofWorkStmt.status
 *
 * Nov-01-2007 Version 0.05
 * - add abstract method computeFieldIdCol(int fieldId)
 *   for use in ProofAsstGUI (just in time) cursor
 *   positioning logic.
 * - add hasMatchingRefLabel()
 *
 * Feb-01-2008 Version 0.06
 * - add tmffReformat().
 * - add getStmtDiagnosticInfo()
 *
 * Aug-01-2008 Version 0.07
 * - add getStmtText()
 * - remove stmtHasError() method.
 */

package mmj.pa;

import java.io.IOException;

import mmj.mmio.MMIOException;

/**
 * General object representing an item on a ProofWorksheet.
 */
public abstract class ProofWorkStmt {

    /**
     * Reference to parent Proof Worksheet added when these inner classes were
     * de-nested.
     */
    ProofWorksheet w;

    /**
     * Output format content of statement
     */
    StringBuilder stmtText;

    /**
     * Get count of number of lines used by this ProofWorkStmt.
     */
    int lineCnt;

    /**
     * Default ProofWorkStmt constructor.
     * <p>
     * Every ProofWorkStmt starts out with "valid" status and lineCnt = 1!
     *
     * @param w the owning ProofWorksheet
     */
    public ProofWorkStmt(final ProofWorksheet w) {
        this.w = w;
        lineCnt = 1;
    }

    /**
     * Is statement incomplete?
     * <p>
     * -- used primarily for cursor positioning:
     * <p>
     * -- a virtual method that checks the statement for the state of
     * "incompleteness" of data as indicated by state variables in the specific
     * ProofWorkStmt types.
     *
     * @return true if ProofWorkStmt is "incomplete" in ProofWorksheet
     *         terminology.
     */
    public abstract boolean stmtIsIncomplete();

    /**
     * Function used for cursor positioning.
     *
     * @param fieldId value identify ProofWorkStmt field for cursor positioning,
     *            as defined in PaConstants.FIELD_ID_*.
     * @return column of input fieldId or default value of 1 if there is an
     *         error.
     */
    public abstract int computeFieldIdCol(int fieldId);

    /**
     * Reformats Derivation Step using TMFF.
     */
    public abstract void tmffReformat();

    /**
     * Base class function to determine whether the ProofWorkStmt step number
     * matches the input step number (always false in base class.)
     *
     * @param newStepNbr to compare to ProofWorkStmt step number.
     * @return false because a generic ProofWorkStmt does not have a step
     *         number.
     */
    public boolean hasMatchingStepNbr(final String newStepNbr) {
        return false;
    }

    /**
     * Base class function to determine whether the ProofWorkStmt Ref label
     * matches the input Ref label (always false in base class.)
     *
     * @param newRefLabel to compare to ProofWorkStmt Ref label.
     * @return false because a generic ProofWorkStmt does not have a Ref label.
     */
    public boolean hasMatchingRefLabel(final String newRefLabel) {
        return false;
    }

    /**
     * Default load method for ProofWorkStmt that does not validate the input.
     * <p>
     * This is used for GeneratedProofStmt and CommentStmt -- and any other
     * possible future statement where you, theoretically, want to be able to
     * parse and load the input statement but don't care about the contents.
     * <p>
     * <b>Output/Updates</b>
     * <ul>
     * <li>accum tokens and whitespace into stmtText, but don't validate the
     * contents, just look for the start of the next statement.
     * <li>return nextToken after trailing whitespace, the start of the next
     * statement.
     *
     * @param firstToken the first token
     * @return the nextToken
     * @throws IOException if IO error
     * @throws MMIOException if an error occurred
     * @throws ProofAsstException if an error occurred
     */
    public String load(final String firstToken)
        throws IOException, MMIOException, ProofAsstException
    {
        final int currLineNbr = (int)w.proofTextTokenizer.getCurrentLineNbr();

        stmtText = new StringBuilder();

        final String nextT = loadAllStmtTextGetNextStmt(firstToken);

        updateLineCntUsingTokenizer(currLineNbr, nextT);
        return nextT;
    }

    /**
     * Appends the contents of the input StringBuilder to the ProofWorkStmt
     * formula area.
     *
     * @param sb StringBuilder to append to stmtText.
     */
    public void appendToProofText(final StringBuilder sb) {
        sb.append(stmtText);
    }

    /**
     * Get count of number of lines used by this ProofWorkStmt.
     *
     * @return number of lines used by this ProofWorkStmt.
     */
    public int getLineCnt() {
        return lineCnt;
    }

    public ProofWorksheet getProofWorksheet() {
        return w;
    }

    /**
     * Returns diagnostic data for this ProofWorkStmt, which in this case is the
     * Class name.
     *
     * @return ProofWorkStmt class name.
     */
    public String getStmtDiagnosticInfo() {
        return this.getClass().getName();
    }

    /**
     * Get the ProofWorkStmt stmtText area.
     *
     * @return ProofWorkStmt stmtText area.
     */
    public StringBuilder getStmtText() {
        return stmtText;
    }

    // only set caret if not already set
    protected void setStmtCursorToCurrLineColumn() {

        // compute nbr lines before this one
        final int total = w.computeProofWorkStmtLineNbr(this);

        final String[] split = stmtText.toString().split("\\s+", 2);
        final int sub = split.length == 2 ? split[1].length() : 0;

        // sets cursor pos only if not already set.
        w.proofCursor.setCursorAtCaret(-1, // charNbr not set
            total, stmtText.length() - sub);
    }

    protected int updateLineCntUsingTokenizer(final int prevLineNbr,
        final String nextT)
    {
        final int currLineNbr = (int)w.proofTextTokenizer.getCurrentLineNbr();
        lineCnt += currLineNbr - prevLineNbr;
        if (nextT.length() > 0)
            lineCnt--; // reduce because we're at the

        return currLineNbr;
    }

    /**
     * Loads input token into ProofWorkStmt's stmtText then loads following
     * whitespace and returns the <i>next</i> token, but without loading it into
     * stmtText.
     * <p>
     * Blows up if there is no next token or whitespace because they are
     * required at the current parse position within the ProofWorkStmt.
     *
     * @param prevToken the first token of the Stmt
     * @return the nextToken
     * @throws IOException if an error occurred
     * @throws MMIOException if an error occurred
     * @throws ProofAsstException if an error occurred
     */
    protected String loadStmtTextGetRequiredToken(final String prevToken)
        throws IOException, MMIOException, ProofAsstException
    {
        String outToken = null;
        stmtText.append(prevToken);

        final int lenW = w.proofTextTokenizer.getWhiteSpace(stmtText,
            stmtText.length());
        if (lenW >= 0) {
            final StringBuilder outSB = new StringBuilder();
            final int lenT = w.proofTextTokenizer.getToken(outSB, 0);
            if (lenT > 0) {
                outToken = outSB.toString();
                if (w.proofTextTokenizer.getCurrentColumnNbr() == lenT)
                    // oops, token begins in col 1, new stmt!
                    w.triggerLoadStructureException(
                        PaConstants.ERRMSG_STMT_NOT_DONE,
                        w.getErrorLabelIfPossible(), outToken);
            }
            else
                w.triggerLoadStructureException(
                    PaConstants.ERRMSG_PREMATURE_END,
                    w.getErrorLabelIfPossible());
        }
        else
            w.triggerLoadStructureException(PaConstants.ERRMSG_PREMATURE_END2,
                w.getErrorLabelIfPossible());
        return outToken;
    }

    /**
     * Loads input token into ProofWorkStmt's stmtText then loads following
     * whitespace and returns the *next* token, but without loading it into
     * stmtText.
     * <p>
     * Returns empty String (length = 0) if end of file reached.
     *
     * @param prevToken the first token
     * @return the next token
     * @throws IOException if an error occurred
     * @throws MMIOException if an error occurred
     * @throws ProofAsstException if an error occurred
     */
    protected String loadStmtTextGetOptionalToken(final String prevToken)
        throws IOException, MMIOException, ProofAsstException
    {
        stmtText.append(prevToken);
        final int lenW = w.proofTextTokenizer.getWhiteSpace(stmtText,
            stmtText.length());
        if (lenW >= 0) {
            final StringBuilder outSB = new StringBuilder();
            final int lenT = w.proofTextTokenizer.getToken(outSB, 0);
            if (lenT > 0)
                return outSB.toString();
        }
        return ""; // eof!
    }

    /**
     * Loads input token into ProofWorkStmt's stmtText then loads following
     * whitespace and returns the <i>next</i> token, but without loading it into
     * stmtText.
     * <p>
     * Blows up if the next token does not begin in column 1, which indicates
     * the start of the <i>next</i> statement.
     * <p>
     * Returns empty String (length = 0) if end of file reached.
     *
     * @param prevToken the first token
     * @return the next token
     * @throws IOException if an error occurred
     * @throws MMIOException if an error occurred
     * @throws ProofAsstException if an error occurred
     */
    protected String loadStmtTextGetNextStmt(final String prevToken)
        throws IOException, MMIOException, ProofAsstException
    {
        stmtText.append(prevToken);
        final int lenW = w.proofTextTokenizer.getWhiteSpace(stmtText,
            stmtText.length());
        if (lenW >= 0) {
            final StringBuilder outSB = new StringBuilder();
            final int lenT = w.proofTextTokenizer.getToken(outSB, 0);
            if (lenT > 0) {
                final String outToken = outSB.toString();
                if (w.proofTextTokenizer.getCurrentColumnNbr() == lenT)
                    // token starting in col 1, new Stmt!
                    return outToken;
                // oops, token does not begin in col 1.
                w.triggerLoadStructureException(PaConstants.ERRMSG_EXTRA_TOKEN,
                    w.getErrorLabelIfPossible(), outToken);

            }
        }
        return ""; // eof!
    }

    /**
     * Loads input token into ProofWorkStmt's stmtText then loads ALL following
     * whitespace AND tokens until the start of the next statement, returning
     * the <i>next</i> token without loading it into
     * <p>
     * Returns empty String (length = 0) if end of file reached.
     *
     * @param prevToken the token from the last call
     * @return the first token of the next stmt
     * @throws IOException if an error occurred
     * @throws MMIOException if an error occurred
     * @throws ProofAsstException if an error occurred
     */
    protected String loadAllStmtTextGetNextStmt(final String prevToken)
        throws IOException, MMIOException, ProofAsstException
    {
        stmtText.append(prevToken);

        int len;
        StringBuilder outSB;

        while (true) {
            if ((len = w.proofTextTokenizer.getWhiteSpace(stmtText,
                stmtText.length())) < 0)
                break;
            outSB = new StringBuilder();
            if ((len = w.proofTextTokenizer.getToken(outSB, 0)) <= 0)
                break;
            if (w.proofTextTokenizer.getCurrentColumnNbr() == len)
                // token starting in col 1, new Stmt!
                return outSB.toString();
            stmtText.append(outSB);
        }
        return ""; // eof!
    }
}
