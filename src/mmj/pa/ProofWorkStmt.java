//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/**
 *  ProofWorkStmt.java  0.07 08/01/2008
 *  <code>
 *  Version 0.04:
 *      - Un-nested inner class
 *      - replace ProofWorkStmt.status
 *
 *  Nov-01-2007 Version 0.05
 *  - add abstract method computeFieldIdCol(int fieldId)
 *    for use in ProofAsstGUI (just in time) cursor
 *    positioning logic.
 *  - add hasMatchingRefLabel()
 *
 *  Feb-01-2008 Version 0.06
 *  - add tmffReformat().
 *  - add getStmtDiagnosticInfo()
 *
 *  Aug-01-2008 Version 0.07
 *  - add getStmtText()
 *  - remove stmtHasError() method.
 *
 *  </code>
 *
 */

package mmj.pa;

import  java.io.IOException;
import  mmj.mmio.*;

/**
 *  General object representing an item on a ProofWorksheet.
 */
public abstract class ProofWorkStmt {

    /**
     *  Reference to parent Proof Worksheet added when
     *  these inner classes were de-nested.
     */
    ProofWorksheet    w;


    /**
     *  Output format content of statement
     */
    StringBuffer      stmtText;

    /**
     *  Get count of number of lines used by this ProofWorkStmt.
     */
    int               lineCnt;


    /**
     *  Default ProofWorkStmt constructor.
     *  <p>
     *  Every ProofWorkStmt starts out with "valid"
     *  status and lineCnt = 1!
     */
    public ProofWorkStmt(ProofWorksheet w) {
        this.w                    = w;
        lineCnt                   = 1;
    }

    /**
     *  Is statement incomplete?
     *  <p>
     *  -- used primarily for cursor positioning:
     *  <p>
     *  -- a virtual method that checks the statement
     *     for the state of "incompleteness" of data
     *     as indicated by state variables in the
     *     specific ProofWorkStmt types.
     *  <p>
     *  @return true if ProofWorkStmt is "incomplete" in
     *          ProofWorksheet terminology.
     */
    public abstract boolean stmtIsIncomplete();

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
    public abstract int computeFieldIdCol(int fieldId);

    /**
     *  Reformats Derivation Step using TMFF.
     */
    public abstract void tmffReformat();


    /**
     *  Is this a proof step?
     *  <p>
     *  @return false by default, unless overridden.
     */
    public boolean isProofStep() {
        return false;
    }

    /**
     *  Is this a hypothesis step?
     *  <p>
     *  @return false by default, unless overridden.
     */
    public boolean isHypothesisStep() {
        return false;
    }

    /**
     *  Is this a proof derivation step?
     *  <p>
     *  @return false by default, unless overridden.
     */
    public boolean isDerivationStep() {
        return false;
    }

    /*
     *  Base class function to determine whether the
     *  ProofWorkStmt step number matches the input
     *  step number (always false in base class.)
     *  <p>
     *  @param newStepNbr to compare to ProofWorkStmt
     *         step number.
     *  @return false because a generic ProofWorkStmt
     *                does not have a step number.
     */
    public boolean hasMatchingStepNbr(String newStepNbr) {
        return false;
    }

    /*
     *  Base class function to determine whether the
     *  ProofWorkStmt Ref label matches the input
     *  Ref label (always false in base class.)
     *  <p>
     *  @param newRefLabel to compare to ProofWorkStmt
     *         Ref label.
     *
     *  @return false because a generic ProofWorkStmt
     *                does not have a Ref label.
     */
    public boolean hasMatchingRefLabel(String newRefLabel) {
        return false;
    }

    /**
     *   Default load method for ProofWorkStmt that
     *   does not validate the input.
     *
     *   This is used for GeneratedProofStmt and
     *   CommentStmt -- and any other possible future
     *   statement where you, theoretically, want to
     *   be able to parse and load the input statement
     *   but don't care about the contents.
     *
     *   Output/Updates
     *   - accum tokens and whitespace into stmtText,
     *     but don't validate the contents, just look
     *     for the start of the next statement.
     *   - return nextToken after trailing whitespace,
     *     the start of the next statement.
     */
    public String load(String firstToken)
                            throws IOException,
                                   MMIOError,
                                   ProofAsstException {
        int currLineNbr       =
            (int)w.proofTextTokenizer.getCurrentLineNbr();

        stmtText              = new StringBuffer();

        String nextT          =
            loadAllStmtTextGetNextStmt(firstToken);

        updateLineCntUsingTokenizer(currLineNbr,
                                    nextT);
        return nextT;
    }

    /**
     *  Appends the contents of the input StringBuffer
     *  to the ProofWorkStmt formula area.
     *
     *  @param sb StringBuffer to append to stmtText.
     */
    public void appendToProofText(StringBuffer sb) {
        sb.append(stmtText);
    }

    /**
     *  Get count of number of lines used by this ProofWorkStmt.
     *  <p>
     *  @return number of lines used by this ProofWorkStmt.
     */
    public int getLineCnt() {
        return lineCnt;
    }

    /**
     *  Returns diagnostic data for this ProofWorkStmt, which
     *  in this case is the Class name.
     *  <p>
     *  @return ProofWorkStmt class name.
     */
    public String getStmtDiagnosticInfo() {
        return this.getClass().getName();
    }

    /**
     *  Get the ProofWorkStmt stmtText area.
     *  <p>
     *  @return ProofWorkStmt stmtText area.
     */
    public StringBuffer getStmtText() {
        return stmtText;
    }

    // only set caret if not already set
    protected void setStmtCursorToCurrLineColumn() {

        // compute nbr lines before this one
        int total             =
            w.computeProofWorkStmtLineNbr(null);

        // sets cursor pos only if not already set.
        w.proofCursor.setCursorAtCaret(
            -1,                     // charNbr not set
            total + 1,
            stmtText.length() + 1);
    }

    protected int updateLineCntUsingTokenizer(
                                int    prevLineNbr,
                                String nextT) {
        int currLineNbr       =
            (int)w.proofTextTokenizer.getCurrentLineNbr();
        lineCnt               += (currLineNbr
                                  - prevLineNbr);
        if (nextT.length() > 0) {
            --lineCnt; //reduce because we're at the
        }              // next stmt now

        return currLineNbr;
    }

    /**
     *  Loads input token into ProofWorkStmt's stmtText
     *  then loads following whitespace and returns
     *  the *next* token, but without loading it into
     *  stmtText.
     *
     *  Blows up if there is no next token or whitespace
     *  because they are required at the current parse
     *  position within the ProofWorkStmt.
     *
     */
    protected String loadStmtTextGetRequiredToken(
                        String prevToken)
                            throws IOException,
                                   MMIOError,
                                   ProofAsstException {
        String outToken       = null;
        stmtText.append(prevToken);

        int lenW              =
            w.proofTextTokenizer.getWhiteSpace(
                                    stmtText,
                                    stmtText.length());
        if (lenW >= 0) {
            StringBuffer outSB
                              = new StringBuffer();
            int lenT = w.proofTextTokenizer.getToken(outSB,
                                                   0);
            if (lenT > 0) {
                outToken      = outSB.toString();
                if (w.proofTextTokenizer.getCurrentColumnNbr()
                    ==
                    lenT) {
                    //oops, token begins in col 1, new stmt!
                    w.triggerLoadStructureException(
                        PaConstants.ERRMSG_STMT_NOT_DONE_1
                        + w.getErrorLabelIfPossible()
                        + PaConstants.
                            ERRMSG_STMT_NOT_DONE_2
                        + outToken);
                }
            }
            else {
                w.triggerLoadStructureException(
                    PaConstants.ERRMSG_PREMATURE_END_1
                    + w.getErrorLabelIfPossible()
                    + PaConstants.
                        ERRMSG_PREMATURE_END_2);
            }
        }
        else {
            w.triggerLoadStructureException(
                PaConstants.ERRMSG_PREMATURE_END2_1
                + w.getErrorLabelIfPossible()
                + PaConstants.
                    ERRMSG_PREMATURE_END2_2);
        }
        return outToken;
    }


    /**
     *  Loads input token into ProofWorkStmt's stmtText
     *  then loads following whitespace and returns
     *  the *next* token, but without loading it into
     *  stmtText.
     *
     *  Returns empty String (length = 0) if end of
     *  file reached.
     *
     */
    protected String loadStmtTextGetOptionalToken(
                        String prevToken)
                            throws IOException,
                                   MMIOError,
                                   ProofAsstException {
        stmtText.append(prevToken);
        int lenW              =
            w.proofTextTokenizer.getWhiteSpace(
                                    stmtText,
                                    stmtText.length());
        if (lenW >= 0) {
            StringBuffer outSB
                              = new StringBuffer();
            int lenT = w.proofTextTokenizer.getToken(outSB,
                                                   0);
            if (lenT > 0) {
                return outSB.toString();
            }
        }
        return new String(""); //eof!
    }

    /**
     *  Loads input token into ProofWorkStmt's stmtText
     *  then loads following whitespace and returns
     *  the *next* token, but without loading it into
     *  stmtText.
     *
     *  Blows up if the next token does not begin in
     *  column 1, which indicates the start of the
     *  *next* statement.
     *
     *  Returns empty String (length = 0) if end of
     *  file reached.
     *
     */
    protected String loadStmtTextGetNextStmt(
                        String prevToken)
                            throws IOException,
                                   MMIOError,
                                   ProofAsstException {
        stmtText.append(prevToken);
        int lenW              =
            w.proofTextTokenizer.getWhiteSpace(
                                    stmtText,
                                    stmtText.length());
        if (lenW >= 0) {
            StringBuffer outSB
                              = new StringBuffer();
            int lenT = w.proofTextTokenizer.getToken(outSB,
                                                   0);
            if (lenT > 0) {
                String outToken
                              = outSB.toString();
                if (w.proofTextTokenizer.getCurrentColumnNbr()
                    ==
                    lenT) {
                    // token starting in col 1, new Stmt!
                    return outToken;
                }
                //oops, token does not begin in col 1.
                w.triggerLoadStructureException(
                    PaConstants.ERRMSG_EXTRA_TOKEN_1
                    + w.getErrorLabelIfPossible()
                    + PaConstants.
                        ERRMSG_EXTRA_TOKEN_2
                    + outToken);

            }
        }
        return new String(""); //eof!
    }

    /**
     *  Loads input token into ProofWorkStmt's stmtText
     *  then loads ALL following whitespace AND tokens
     *  until the start of the next statement, returning
     *  the *next* token without loading it into
     *
     *  Returns empty String (length = 0) if end of
     *  file reached.
     *
     */
    protected String loadAllStmtTextGetNextStmt(
                                         String prevToken)
                            throws IOException,
                                   MMIOError,
                                   ProofAsstException {
        stmtText.append(prevToken);

        int          len;
        StringBuffer outSB;

        while (true) {
            len               =
            w.proofTextTokenizer.getWhiteSpace(
                                    stmtText,
                                    stmtText.length());
            if (len >= 0) {
                outSB         = new StringBuffer();
                len           =
                    w.proofTextTokenizer.getToken(outSB,
                                                0);
                if (len > 0) {
                    if (w.proofTextTokenizer.getCurrentColumnNbr()
                        ==
                        len) {
                        // token starting in col 1, new Stmt!
                        return outSB.toString();
                    }
                    stmtText.append(outSB);
                }
                else {
                    break;
                }
            }
            else {
                break;
            }
        }
        return new String(""); //eof!
    }
}
