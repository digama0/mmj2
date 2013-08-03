//********************************************************************/
//* Copyright (C) 2006                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/


/*
 *  TMFFStateParams.java  0.02 11/01/2007
 *
 *  Aug-28-2006: - new; simple data structure for TMFF "state" to
 *                 enable full reentrancy.
 *
 *  Nov-01-2007 Version 0.02
 *      - fix typo in comments.
 *      - add new method to implement indentation by changing
 *        the TMFFSP.leftmostColNbr just prior to each call
 *        to TMFFPreferences.renderFormula():
 *
 *            setLeftmostColNbr(int formulaLeftCol,
 *                              int useIndent,
 *                              int proofLevel);
 */

package mmj.tmff;

/**
 *  TMFFStateParams holds the state data used throughout
 *  processing in the TMFF rendering code.
 *  <p>
 *
 */
public class TMFFStateParams {

    /* friendly */ int          leftmostColNbr;
    /* friendly */ int          rightmostColNbr;
    /* friendly */ boolean      lineWrapOn;
    /* friendly */ StringBuffer sb;
    /* friendly */ int          prevColNbr;
    /* friendly */ int          textColumns;
    /* friendly */ int          currFormatNbr; //prev char's output pos or 0

    /**
     *  currLineNbr is used internally and need not be
     *  set by programs calling TMFF. It refers to the
     *  line number within a formula (1, 2, etc.)
     */
    public    int          currLineNbr;


    /**
     *  Simple constructor for TMFFStateParams.
     *  <p>
     *  You are responsible for setting reasonable values.
     *  No validation is performed!
     *  <p>
     *  Note that output of the first/next token will be at position
     *  rightmostColNbr + 1 because a space character is
     *  inserted PRIOR to each output token. That means that
     *  if <code>inPrevColNbr</code> is set to <code>4</code>
     *  then there should be a line containing 3 characters
     *  already loaded at the end of the input StringBuffer
     *  <code>inSb</code>!
     *  <p>
     *  <code>
     *      Sample call w/empty StringBuffer:
     *
     *      TMFFStateParams tmffSP =
     *          new TMFFStateParams(new StringBuffer(80),
     *                              0,          //inPrevColNbr,
     *                              20,         //inLeftmostColNbr,
     *                              79,         //inRightmostColNbr,
     *                              80,         //inTextColumns,
     *                              false       //inLineWrapOn,
     *                              0,          //not used now);
     *  </code>
     *  <p>
     *
     *  @param inSb is the input StringBuffer. All output is
     *         appended to this StringBuffer. It must be
     *         pre-initialized.
     *
     *  @param inPrevColNbr is the column number on the current
     *         print line of the most recently output character.
     *         Set it to 0 if nothing has been written to the
     *         current line, or to the previous character's
     *         position (1, 2, etc.)
     *
     *  @param inLeftmostColNbr is the logical left margin
     *         for formulas.
     *
     *  @param inRightmostColNbr is the logical right margin
     *         for output formulas, regardless of whether
     *         <code>inLineWrapOn</code> is true. For example,
     *         to format formulas through column 79 of the
     *         screen/page, set <code>inRightmostColNbr</code>
     *         to <code>79</code>. Note: this number does not
     *         include linefeed/carriage return, but only the
     *         characters displayed!
     *
     *  @param inLineWrapOn is a flag to turn On/Off
     *         line wrapping. If set to On then Newline
     *         characters (linefeed/carriage return sequences)
     *         are not used inside a formula that spans
     *         multiple lines. Instead, space characters are
     *         used to pad out line endings thru the specified
     *         <code>inRightmostColNbr</code> column. Note that
     *         a newline will follow the end of a formula,
     *         for example on the mmj2 Proof Assistant screen.
     *         The purpose of this feature is to facilitate
     *         copy/pasting, especially into text editors that
     *         burp when a newline is encountered (Metamath and
     *         mmj2 generally treat Newlines as whitespace
     *         except between formulas on the Proof Assistant
     *         GUI screen.)
     *
     *  @param inTextColumns - number of columns on a line.
     *
     *  @param inCurrFormatNbr is the format number in use in
     *         TMFFPreferences. Not used right now, but stored
     *         here to simplify a later enhancement (possible)
     *         to use TMFFSchemes assigned at the Syntax Axiom
     *         level.
     *
     */
    public TMFFStateParams(StringBuffer inSb,
                           int          inPrevColNbr,
                           int          inLeftmostColNbr,
                           int          inRightmostColNbr,
                           int          inTextColumns,
                           boolean      inLineWrapOn,
                           int          inCurrFormatNbr) {

        sb                        = inSb;
        prevColNbr                = inPrevColNbr;
        leftmostColNbr            = inLeftmostColNbr;
        rightmostColNbr           = inRightmostColNbr;
        textColumns               = inTextColumns;
        lineWrapOn                = inLineWrapOn;
        currFormatNbr             = inCurrFormatNbr;
        currLineNbr               = 1;
    }

    /**
     *  Standard constructor for TMFFStateParams.
     *  <p>
     *  You are responsible for setting reasonable values.
     *  No validation is performed. See previous constructor
     *  for additional information.
     *  <p>
     *
     *  @param inSb is the input StringBuffer. All output is
     *         appended to this StringBuffer. It must be
     *         pre-initialized.
     *
     *  @param inPrevColNbr is the column number on the current
     *         print line of the most recently output character.
     *         Set it to 0 if nothing has been written to the
     *         current line, or to the previous character's
     *         position (1, 2, etc.)
     *
     *  @param tmffPreferences - current TMFFPreferences.
     *
     */
    public TMFFStateParams(StringBuffer    inSb,
                           int             inPrevColNbr,
                           TMFFPreferences tmffPreferences) {
        this(inSb,
             inPrevColNbr,
             tmffPreferences.getFormulaLeftCol(),
             tmffPreferences.getFormulaRightCol(),
             tmffPreferences.getTextColumns(),
             tmffPreferences.getLineWrap(),
             tmffPreferences.getCurrFormatNbr());
    }


    /**
     *  Puts token to string buffer with at least one space
     *  following previous output character.
     * @return -1 if error, else 0.
     */
    public int appendTokenAtGivenPosition(String token,
                                           int    pos) {

        padSBToGivenPosition(pos - 1);

        sb.append(token);

        prevColNbr               += token.length();

        if (prevColNbr > rightmostColNbr) {
            return -1;
        }
        return 0;
    }

    /**
     *  Outputs spaces to prepare for output of a token
     *  at a given column. If necessary performs a line
     *  break using either NewLine, or extra spaces when
     *  in LineWrapOn mode.
     *
     *  Pads *thru* the given position! I.E. if you say
     *  pad to column 7, then column 7 ends up containing
     *  a space.
     */
    public void padSBToGivenPosition(int pos) {

        int padLength             = pos - prevColNbr;

        if (padLength == 0) {
        }
        else {
            if (padLength < 0) {
                newlineSB();
                padLength         = pos;
            }

            padSB(' ',
                  padLength);
        }

    }

    public void setSB(StringBuffer sb) {
        this.sb                   = sb;
    }
    public void setPrevColNbr(int prevColNbr) {
        this.prevColNbr           = prevColNbr;
    }

    /**
     *  Updates TMFFSP leftmost column number using the
     *  TMFFPrefences formulaLeftCol and indent amount
     *  parameters plus the level number of the formula
     *  within the proof.
     *  <p>
     *  If indentation is used, based on variances between
     *  different proof steps, then this routine needs to
     *  be invoked every time, just before calling
     *  tmffPreferences.renderFormula(). That is because
     *  the TMFFPreferences.formulaLeftCol is not updated
     *  and must be used to reset the value of
     *  TMFFSP.leftmostColNbr after each use.
     *  <p>
     *  <code>
     *        setLeftmostColNbr(formulaLeftCol
     *                    + (useIndent *
     *                       proofLevel));
     *
     *  NOTE: If the resulting leftmost column number is
     *        not less than the rightmost column number,
     *        the indentation is turned off:
     *
     *        setLeftmostColNbr(formulaLeftCol);
     *  </code>
     *
     *  @param formulaLeftCol from TMFFPreferences or other.
     *  @param useIndent indent column amount per proof step level
     *  @param proofLevel step formula level number within proof
     */
    public void setLeftmostColNbr(int formulaLeftCol,
                                  int useIndent,
                                  int proofLevel) {
        setLeftmostColNbr(formulaLeftCol
                          + (useIndent
                             * proofLevel));

        if (leftmostColNbr >= rightmostColNbr) {
            setLeftmostColNbr(formulaLeftCol);
        }
    }

    public void setLeftmostColNbr(int leftmostColNbr) {
        this.leftmostColNbr       = leftmostColNbr;
    }
    public int getLeftmostColNbr() {
        return leftmostColNbr;
    }

    public void newlineSB() {
        if (!lineWrapOn) {
            sb.append('\n');
        }
        else {
            int padLength         = textColumns
                                    - prevColNbr;
            padSB(' ',
                  padLength);
        }
        ++currLineNbr;
        prevColNbr                = 0;
    }

    public void padSB(char padChar,
                         int  padLength) {
        prevColNbr               += padLength;
        while (padLength-- > 0) {
            sb.append(padChar);
        }
    }

    public int getAvailLengthOnCurrLine() {
        return rightmostColNbr - prevColNbr;
    }

}
