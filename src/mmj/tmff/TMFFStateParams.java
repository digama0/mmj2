//********************************************************************/
//* Copyright (C) 2006                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * TMFFStateParams.java  0.02 11/01/2007
 *
 * Aug-28-2006: - new; simple data structure for TMFF "state" to
 *                enable full reentrancy.
 *
 * Nov-01-2007 Version 0.02
 *     - fix typo in comments.
 *     - add new method to implement indentation by changing
 *       the TMFFSP.leftmostColNbr just prior to each call
 *       to TMFFPreferences.renderFormula():
 *
 *           setLeftmostColNbr(int formulaLeftCol,
 *                             int useIndent,
 *                             int proofLevel);
 */

package mmj.tmff;

/**
 * TMFFStateParams holds the state data used throughout processing in the TMFF
 * rendering code.
 * <p>
 */
public class TMFFStateParams {
    private final TMFFPreferences prefs;
    public int leftmostColNbr;
    public int rightmostColNbr;
    public StringBuilder sb;
    public int prevColNbr;

    /**
     * currLineNbr is used internally and need not be set by programs calling
     * TMFF. It refers to the line number within a formula (1, 2, etc.)
     */
    public int currLineNbr;

    /**
     * Standard constructor for TMFFStateParams.
     * <p>
     * You are responsible for setting reasonable values. No validation is
     * performed. See previous constructor for additional information.
     *
     * @param inSb is the input StringBuilder. All output is appended to this
     *            StringBuilder. It must be pre-initialized.
     * @param inPrevColNbr is the column number on the current print line of the
     *            most recently output character. Set it to 0 if nothing has
     *            been written to the current line, or to the previous
     *            character's position (1, 2, etc.)
     * @param tmffPreferences - current TMFFPreferences.
     */
    public TMFFStateParams(final StringBuilder inSb, final int inPrevColNbr,
        final TMFFPreferences tmffPreferences)
    {
        prefs = tmffPreferences;
        sb = inSb;
        prevColNbr = inPrevColNbr;
        leftmostColNbr = tmffPreferences.formulaLeftCol.get();
        rightmostColNbr = tmffPreferences.formulaRightCol.get();
        currLineNbr = 1;
    }

    /**
     * Puts token to string buffer with at least one space following previous
     * output character.
     *
     * @param token the token to append
     * @param pos the screen column at which to draw the token
     * @return -1 if error, else 0.
     */
    public int appendTokenAtGivenPosition(final String token, final int pos) {

        padSBToGivenPosition(pos - 1);

        sb.append(token);

        prevColNbr += token.length();

        if (prevColNbr > rightmostColNbr)
            return -1;
        return 0;
    }

    /**
     * Outputs spaces to prepare for output of a token at a given column. If
     * necessary performs a line break using either NewLine, or extra spaces
     * when in LineWrapOn mode.
     * <p>
     * Pads *thru* the given position! I.E. if you say pad to column 7, then
     * column 7 ends up containing a space.
     *
     * @param pos the screen column at which to draw the token
     */
    public void padSBToGivenPosition(final int pos) {

        int padLength = pos - prevColNbr;

        if (padLength == 0) {}
        else {
            if (padLength < 0) {
                newlineSB();
                padLength = pos;
            }

            padSB(' ', padLength);
        }

    }

    public void setSB(final StringBuilder sb) {
        this.sb = sb;
    }
    public void setPrevColNbr(final int prevColNbr) {
        this.prevColNbr = prevColNbr;
    }

    /**
     * Updates TMFFSP leftmost column number using the TMFFPrefences
     * formulaLeftCol and indent amount parameters plus the level number of the
     * formula within the proof.
     * <p>
     * If indentation is used, based on variances between different proof steps,
     * then this routine needs to be invoked every time, just before calling
     * tmffPreferences.renderFormula(). That is because the
     * TMFFPreferences.formulaLeftCol is not updated and must be used to reset
     * the value of TMFFSP.leftmostColNbr after each use.
     * <p>
     *
     * <pre>
     * setLeftmostColNbr(formulaLeftCol + (useIndent * proofLevel));
     * </pre>
     *
     * NOTE: If the resulting leftmost column number is not less than the
     * rightmost column number, the indentation is turned off:
     *
     * <pre>
     * setLeftmostColNbr(formulaLeftCol);
     * </pre>
     *
     * @param formulaLeftCol from TMFFPreferences or other.
     * @param useIndent indent column amount per proof step level
     * @param proofLevel step formula level number within proof
     */
    public void setLeftmostColNbr(final int formulaLeftCol, final int useIndent,
        final int proofLevel)
    {
        leftmostColNbr = formulaLeftCol + useIndent * proofLevel;

        if (leftmostColNbr >= rightmostColNbr)
            leftmostColNbr = formulaLeftCol;
    }

    public void newlineSB() {
        if (!prefs.lineWrap.get())
            sb.append('\n');
        else {
            final int padLength = prefs.textColumns.get() - prevColNbr;
            padSB(' ', padLength);
        }
        currLineNbr++;
        prevColNbr = 0;
    }

    public void padSB(final char padChar, int padLength) {
        prevColNbr += padLength;
        while (padLength-- > 0)
            sb.append(padChar);
    }

    public int getAvailLengthOnCurrLine() {
        return rightmostColNbr - prevColNbr;
    }

}
