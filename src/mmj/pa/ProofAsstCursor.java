//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  ProofAsstCursor.java  0.05 11/01/2011
 *
 *  Sep-15-2006 -- Version 0.01:
 *     ---> New. Simple data structure to hold caret/scroll params.
 *
 *  Version 0.02 -- 06/01/2007
 *     ---> Un-nesting of Proof Worksheet inner classes.
 *
 *  Nov-01-2007 -- Version 0.03
 *     ---> Add fieldId to cursor (see PaConstant.FIELD_ID_*).
 *
 *  Feb-01-2008 -- Version 0.04
 *     ---> Add outputCursorInstrumentation() for use in
 *          regression testing.
 *
 *  Version 0.05 - Nov-01-2011:  comment update.
 *  ==> Add dontScroll for use with incompleteStepCursorAsIs
 *      ProofAsstCursor positioning.

 */

package mmj.pa;

/**
 *  Simple data structure to hold caret/scroll params for
 *  the Proof Asst GUI.
 */
public class ProofAsstCursor {

    /* friendly */boolean cursorIsSet;

    /* friendly */ProofWorkStmt proofWorkStmt;
    /* friendly */int fieldId;

    /* friendly */int caretCharNbr;
    /* friendly */int caretLine;
    /* friendly */int caretCol;
    /* friendly */int scrollToLine;
    /* friendly */int scrollToCol;

    private boolean dontScroll;

    public static ProofAsstCursor makeProofStartCursor() {
        final ProofAsstCursor cursor = new ProofAsstCursor();

        // Note: could not get swing to scroll to line 1
        // if caret was set to line 2 and the window
        // was scrolled to page 2 of a proof before
        // displaying the new proof; the first line
        // displayed was line 2!#$% So...switched
        // to scroll line = caret line...doh.
        cursor.setCursor(-1, // caret char
            1, // caret line
            PaConstants.PROOF_TEXT_HEADER_1.length() + 1, // caret col
            1, // scroll to line
            PaConstants.PROOF_TEXT_HEADER_1.length() + 1); // scroll to col
        return cursor;
    }

    /**
     *  Default constructor.
     */
    public ProofAsstCursor() {
        cursorIsSet = false;
        proofWorkStmt = null;
        fieldId = PaConstants.FIELD_ID_NONE;
        caretCharNbr = -1;
        caretLine = -1;
        caretCol = -1;
        scrollToLine = -1;
        scrollToCol = -1;
    }

    public ProofAsstCursor(final int caretCharNbr, final int caretLine,
        final int caretCol)
    {

        super();
        this.caretCharNbr = caretCharNbr;
        this.caretLine = caretLine;
        this.caretCol = caretCol;

        if (caretCharNbr >= 0 || caretLine >= 0 || caretCol >= 0)
            cursorIsSet = true;
    }

    public ProofAsstCursor(final ProofWorkStmt proofWorkStmt) {
        super();

        setCursorAtProofWorkStmt(proofWorkStmt, PaConstants.FIELD_ID_NONE);
    }

    public ProofAsstCursor(final ProofWorkStmt proofWorkStmt, final int fieldId)
    {
        super();

        setCursorAtProofWorkStmt(proofWorkStmt, fieldId);
    }

    public void setCursorAtProofWorkStmt(final ProofWorkStmt proofWorkStmt) {
        setCursorAtProofWorkStmt(proofWorkStmt, PaConstants.FIELD_ID_NONE);
    }

    public void setCursorAtProofWorkStmt(final ProofWorkStmt proofWorkStmt,
        final int fieldId)
    {
        if (!cursorIsSet) {
            this.proofWorkStmt = proofWorkStmt;
            this.fieldId = fieldId;
            cursorIsSet = true;
        }
    }

    public void setCursorAtCaret(final int caretCharNbr, final int caretLine,
        final int caretCol)
    {

        if (!cursorIsSet) {
            this.caretCharNbr = caretCharNbr;
            this.caretLine = caretLine;
            this.caretCol = caretCol;
            cursorIsSet = true;
        }
    }

    public void setCursor(final int caretCharNbr, final int caretLine,
        final int caretCol, final int scrollToLine, final int scrollToCol)
    {

        if (!cursorIsSet) {
            this.caretCharNbr = caretCharNbr;
            this.caretLine = caretLine;
            this.caretCol = caretCol;
            this.scrollToLine = scrollToLine;
            this.scrollToCol = scrollToCol;
            cursorIsSet = true;
        }
    }

    public String outputCursorInstrumentation(final String theoremLabel) {
        String stmtDiagnosticInfo;
        if (proofWorkStmt == null)
            stmtDiagnosticInfo = " ";
        else
            stmtDiagnosticInfo = proofWorkStmt.getStmtDiagnosticInfo();
        return PaConstants.ERRMSG_PA_CURSOR_INSTRUMENTATION_1 + theoremLabel
            + PaConstants.ERRMSG_PA_CURSOR_INSTRUMENTATION_2
            + stmtDiagnosticInfo
            + PaConstants.ERRMSG_PA_CURSOR_INSTRUMENTATION_3 + fieldId
            + PaConstants.ERRMSG_PA_CURSOR_INSTRUMENTATION_4 + caretCharNbr
            + PaConstants.ERRMSG_PA_CURSOR_INSTRUMENTATION_5 + caretLine
            + PaConstants.ERRMSG_PA_CURSOR_INSTRUMENTATION_6 + caretCol;
    }

    public void setDontScroll(final boolean dontScroll) {
        this.dontScroll = dontScroll;
    }

    public boolean getDontScroll() {
        return dontScroll;
    }

}
