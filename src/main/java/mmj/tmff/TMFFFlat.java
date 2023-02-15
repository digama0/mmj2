//********************************************************************/
//* Copyright (C) 2006                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * TMFFFlat.java  0.01 11/01/2006
 *
 * Aug-28-2006: - new, "Flat" Method for formula rendering in
 *                text mode in the new mmj2 feature "TMFF".
 */

package mmj.tmff;

import org.json.JSONArray;

import mmj.lang.*;

/**
 * TMFFFlat renders a formula into a single line of text.
 * <p>
 * This rendering method was requested by Norm to provide a concise way to see
 * an entire proof.
 * <p>
 * Note that with LineWrap on, the text will wrap around and occupy multiple
 * screen lines
 * <p>
 * TMFFFlat overrides basic TMFFMethod renderFormula() to override the
 * rightmostColNbr parameter temporarily, without altering the user setting for
 * future invocations of TMFF.
 */
public class TMFFFlat extends TMFFMethod {

    /**
     * Standard constructor for TMFFFlat.
     * <p>
     * Sets maxDepth to Integer.MAX_VALUE so that no depth breaks are triggered.
     */
    public TMFFFlat() {
        super(Integer.MAX_VALUE);
    }

    @Override
    public JSONArray asArray() {
        return new JSONArray().put(TMFFConstants.TMFF_METHOD_USER_NAME_FLAT);
    }

    /**
     * Formats a formula and outputs it to a StringBuilder using the given
     * ParseTree root node and initial Constant of the formula.
     * <p>
     * This method overrides the TMFFMethod renderFormula() method!
     * <p>
     * Sets rightmostColNbr to Integer.MAX_VALUE so that no line width breaks
     * are triggered -- and this is done regardless of the user setting for the
     * rightmost column number (overrides that setting.)
     *
     * @param tmffSP TMFFStateParams initialized, ready for use.
     * @param parseTree ParseTree for the formula to be formatted.
     * @param formula formula to be formatted.
     * @return number of lines rendered or -1 if an error was encountered and
     *         the formula could not be formatted.
     */
    @Override
    public int renderFormula(final TMFFStateParams tmffSP,
        final ParseTree parseTree, final Formula formula)
    {

        if (parseTree != null) {
            final int savedRightmostColNbr = tmffSP.rightmostColNbr;
            tmffSP.rightmostColNbr = Integer.MAX_VALUE;
            tmffSP.currLineNbr = 1;

            if (tmffSP.appendTokenAtGivenPosition(formula.getTyp().getId(),
                tmffSP.leftmostColNbr) < 0)
            {
                tmffSP.currLineNbr = -1;
                tmffSP.rightmostColNbr = savedRightmostColNbr; // restore
            }
            else if (renderSubExpr(tmffSP, parseTree.getRoot(),
                tmffSP.prevColNbr + 2) < 0)
            {

                tmffSP.currLineNbr = -1;
                tmffSP.rightmostColNbr = savedRightmostColNbr; // restore
            }
        }
        else
            tmffSP.currLineNbr = -1;

        return tmffSP.currLineNbr;

    }

    // return -1 if error else 0
    @Override
    protected int renderSubExprWithBreaks(final TMFFStateParams tmffSP,
        final ParseNode currNode, final int leftmostColNbr)
    {

        throw new IllegalArgumentException(
            TMFFConstants.ERRMSG_UNFORMATTED_BAD_CALL_FLAT_1);

    }

    /**
     * Updates maxDepth for a TMFFMethod if the Method allows updates.
     * <p>
     * As of the initial release, only TMFFAlignColumn uses maxDepth. The
     * methods TMFFFlat and TMFFUnformatted have maxDepth = Integer.MAX_VALUE
     * which results in no maxDepth line breaks from happening -- therefore,
     * they do not allow updates after initial construction of the method.
     *
     * @param maxDepth parameter.
     * @return boolean - true only if update performed.
     */
    @Override
    public boolean updateMaxDepth(final int maxDepth) {

        return false;
    }

}
