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
// ===          H e a d e r   S t m t            ===
// ===                                           ===
// =================================================

/**
 *  HeaderStmt.java  0.07 08/01/2008
 *  <code>
 *  Version 0.04:
 *      - Un-nested inner class
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
 *  </code>
 *  HeaderStmt represents the first line of the ProofWorksheet.
 */

package mmj.pa;

import java.io.IOException;

import mmj.lang.Stmt;
import mmj.lang.Theorem;
import mmj.mmio.MMIOError;
import mmj.mmio.Statementizer;

public class HeaderStmt extends ProofWorkStmt {
    String theoremLabel;
    String locAfterLabel;

    boolean headerIncomplete;

    boolean headerInvalid;

    /**
     *  Default Constructor.
     */
    public HeaderStmt(final ProofWorksheet w) {
        super(w);
    }

    /**
     *  Constructor used for new proof.
     *
     *  @param theoremLabel proof theorem label
     *  @param locAfterLabel LOC_AFTER statement label
     */
    public HeaderStmt(final ProofWorksheet w, final String theoremLabel,
        final String locAfterLabel)
    {
        super(w);
        this.theoremLabel = theoremLabel;
        this.locAfterLabel = locAfterLabel;

        stmtText = new StringBuilder();

        stmtText.append(PaConstants.PROOF_TEXT_HEADER_1);

        if (!validateTheoremLabel())
            setStmtCursorToCurrLineColumn();

        stmtText.append(theoremLabel);
        stmtText.append(PaConstants.PROOF_TEXT_HEADER_2);

        if (w.theorem == null && !theoremLabel.equals("")
            && !theoremLabel.equals(PaConstants.DEFAULT_STMT_LABEL))
            if (!validateLocAfterLabel())
                setStmtCursorToCurrLineColumn();

        stmtText.append(locAfterLabel);
        stmtText.append(PaConstants.PROOF_WORKSHEET_NEW_LINE);

        stmtText.append(PaConstants.PROOF_WORKSHEET_NEW_LINE);
        ++lineCnt;
    }

    @Override
    public boolean stmtIsIncomplete() {
        return headerIncomplete;
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

    /**
     *   load Header statement with Tokenizer input
     *   <p>
     *   <code>
     *   Output/Updates
     *   - accum tokens and whitespace into stmtText,
     *     checking for extra tokens or premature EOF
     *   - throw exception if structural error found.
     *   - set status to 0 if no errors
     *   - return nextToken after trailing whitespace.
     *     the start of the next statement.
     *   - keep track of lineCnt, number of lines in
     *     the statement.
     *   - position cursor to field in error, as needed.
     *   - Load Header statement fields.
     *   </code>
     *
     *   @param firstToken first token of statement
     *   @return      first token of following statement.
     *   @throws ProofAsstException if validation error.
     */
    @Override
    public String load(final String firstToken) throws IOException, MMIOError,
        ProofAsstException
    {
        final int currLineNbr = (int)w.proofTextTokenizer.getCurrentLineNbr();

        stmtText = new StringBuilder();

        String nextT = loadStmtTextGetRequiredToken(firstToken);
        if (!nextT.equals(PaConstants.HEADER_MM_TOKEN))
            w.triggerLoadStructureException(PaConstants.ERRMSG_BAD_HDR_TOKEN_1
                + w.getErrorLabelIfPossible()
                + PaConstants.ERRMSG_BAD_HDR_TOKEN_2 + nextT, nextT.length());

        nextT = loadStmtTextGetRequiredToken(nextT);
        if (!nextT.equals(PaConstants.HEADER_PROOF_ASST_TOKEN))
            w.triggerLoadStructureException(PaConstants.ERRMSG_BAD_HDR_TOKEN2_1
                + w.getErrorLabelIfPossible()
                + PaConstants.ERRMSG_BAD_HDR_TOKEN2_2 + nextT, nextT.length());

        nextT = loadStmtTextGetRequiredToken(nextT);
        if (!nextT.startsWith(PaConstants.HEADER_THEOREM_EQUAL_PREFIX))
            w.triggerLoadStructureException(PaConstants.ERRMSG_BAD_HDR_TOKEN3_1
                + w.getErrorLabelIfPossible()
                + PaConstants.ERRMSG_BAD_HDR_TOKEN3_2 + nextT, nextT.length());
        theoremLabel = nextT.substring(PaConstants.HEADER_THEOREM_EQUAL_PREFIX
            .length());
        if (!validateTheoremLabel())
            w.triggerLoadStructureException(PaConstants.ERRMSG_BAD_THRM_VAL_1
                + w.getErrorLabelIfPossible()
                + PaConstants.ERRMSG_BAD_THRM_VAL_2 + theoremLabel
                + PaConstants.ERRMSG_BAD_THRM_VAL_3, theoremLabel.length());

        nextT = loadStmtTextGetRequiredToken(nextT);
        if (!nextT.startsWith(PaConstants.HEADER_LOC_AFTER_EQUAL_PREFIX))
            w.triggerLoadStructureException(PaConstants.ERRMSG_BAD_LOC_TOKEN_1
                + w.getErrorLabelIfPossible()
                + PaConstants.ERRMSG_BAD_LOC_TOKEN_2 + nextT);
        locAfterLabel = nextT
            .substring(PaConstants.HEADER_LOC_AFTER_EQUAL_PREFIX.length());
        if (w.theorem == null && !locAfterLabel.equals("")
            && !locAfterLabel.equals(PaConstants.DEFAULT_STMT_LABEL))
            if (!validateLocAfterLabel())
                w.triggerLoadStructureException(
                    PaConstants.ERRMSG_BAD_LOC_VAL_1
                        + w.getErrorLabelIfPossible()
                        + PaConstants.ERRMSG_BAD_LOC_VAL_2 + locAfterLabel
                        + PaConstants.ERRMSG_BAD_LOC_VAL_3,
                    locAfterLabel.length());

        w.loadComboFrameAndVarMap();

        nextT = loadStmtTextGetNextStmt(nextT);

        updateLineCntUsingTokenizer(currLineNbr, nextT);

        return nextT;
    }

    // Must be a valid theorem, or not found.
    // If a valid theorem, compute maxSeq.
    private boolean validateTheoremLabel() {

        w.theorem = null;

        if (theoremLabel == null || theoremLabel.equals("")
            || theoremLabel.equals(PaConstants.DEFAULT_STMT_LABEL))
        {
            theoremLabel = PaConstants.DEFAULT_STMT_LABEL;
            headerIncomplete = true;
            return false;
        }

        final Stmt stmt = w.logicalSystem.getStmtTbl().get(theoremLabel);
        if (stmt == null) {
            if (!Statementizer.areLabelCharsValid(theoremLabel)) {
                w.messages
                    .accumErrorMessage(PaConstants.ERRMSG_BAD_LABEL_CHAR_1
                        + theoremLabel + PaConstants.ERRMSG_BAD_LABEL_CHAR_2);
                headerInvalid = true;
                return false;
            }
            if (Statementizer.isLabelOnProhibitedList(theoremLabel)) {
                w.messages.accumErrorMessage(PaConstants.ERRMSG_PROHIB_LABEL_1
                    + theoremLabel + PaConstants.ERRMSG_PROHIB_LABEL_2);
                headerInvalid = true;
                return false;
            }
            if (w.logicalSystem.getSymTbl().containsKey(theoremLabel)) {
                w.messages
                    .accumErrorMessage(PaConstants.ERRMSG_STMT_LABEL_DUP_OF_SYM_ID_1_1
                        + theoremLabel
                        + PaConstants.ERRMSG_STMT_LABEL_DUP_OF_SYM_ID_1_2);
                headerInvalid = true;
                return false;
            }
            return true;
        }

        if (stmt instanceof Theorem)
            if (stmt.getTyp() != w.getProvableLogicStmtTyp()) {
                w.messages.accumErrorMessage(PaConstants.ERRMSG_BAD_TYP_CD_1
                    + theoremLabel + PaConstants.ERRMSG_BAD_TYP_CD_2
                    + stmt.getTyp() + PaConstants.ERRMSG_BAD_TYP_CD_3
                    + w.getProvableLogicStmtTyp());
                headerInvalid = true;
                return false;
            }
            else {
                w.theorem = (Theorem)stmt;
                w.setMaxSeq(stmt.getSeq());
                w.setNewTheorem(false); // worksheet method
                return true;
            }

        w.messages.accumErrorMessage(PaConstants.ERRMSG_NOT_A_THRM_1
            + theoremLabel + PaConstants.ERRMSG_NOT_A_THRM_2);
        headerInvalid = true;
        return false;
    }

    // if entered, must be valid stmt.
    // compute maxSeq if valid.
    private boolean validateLocAfterLabel() {
        w.locAfter = null;

        if (locAfterLabel == null || locAfterLabel.equals("")
            || locAfterLabel.equals(PaConstants.DEFAULT_STMT_LABEL))
        {
            locAfterLabel = PaConstants.DEFAULT_STMT_LABEL;
            return true;
        }

        final Stmt stmt = w.logicalSystem.getStmtTbl().get(locAfterLabel);
        if (stmt == null) {
            w.messages.accumErrorMessage(PaConstants.ERRMSG_LOC_NOTFND_1
                + w.getErrorLabelIfPossible() + PaConstants.ERRMSG_LOC_NOTFND_2
                + locAfterLabel + PaConstants.ERRMSG_LOC_NOTFND_3);
            headerInvalid = true;
            return false;
        }
        w.setMaxSeq(stmt.getSeq() + 1);
        w.locAfter = stmt;
        return true;

    }
}
