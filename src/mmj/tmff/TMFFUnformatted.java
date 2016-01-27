//********************************************************************/
//* Copyright (C) 2006                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * TMFFUnformatted.java  0.02 06/01/2007
 *
 * Version 0.01 Sep-02-2006:
 *              - new. "Unformatted" overrides basic TMFFMethod
 *                renderFormula method to provide the old,
 *                unformatted output used previously in mmj2.
 *
 * Version 0.02 Jun-01-2007:
 *              - tweak to allow renderFormula to output a
 *                null formula (weird...but...)
 */

package mmj.tmff;

import org.json.JSONArray;

import mmj.lang.*;

/**
 * TMFFUnformatted overrides basic the TMFFMethod renderFormula method to
 * provide the old, unformatted output used previously in mmj2.
 * <p>
 */
public class TMFFUnformatted extends TMFFMethod {

    /**
     * Standard constructor for TMFFUnformatted.
     * <p>
     * Sets maxDepth to Integer.MAX_VALUE so that no depth breaks are triggered.
     */
    public TMFFUnformatted() {
        super(Integer.MAX_VALUE);
    }

    @Override
    public JSONArray asArray() {
        return new JSONArray()
            .put(TMFFConstants.TMFF_METHOD_USER_NAME_UNFORMATTED);
    }

    /**
     * Outputs a formula and outputs it to a StringBuilder without doing the
     * special TMFF formatting.
     * <p>
     * This method overrides the TMFFMethod renderFormula() method! It provides
     * a fallback for cases when the TMFF algorithm fails (e.g. excessive
     * indentation for line length.)
     *
     * @param tmffSP TMFFStateParams initialized, ready for use.
     * @param parseTree ParseTree for the formula to be formatted. NOT USED in
     *            this override method!
     * @param formula formula to be formatted.
     * @return number of lines rendered or -1 if an error was encountered and
     *         the formula could not be formatted.
     */
    @Override
    public int renderFormula(final TMFFStateParams tmffSP,
        final ParseTree parseTree, final Formula formula)
    {

        tmffSP.currLineNbr = 0;

        tmffSP.padSBToGivenPosition(tmffSP.leftmostColNbr - 1);

        if (formula == null)
            tmffSP.currLineNbr += 1;
        else
            tmffSP.currLineNbr += formula.toProofWorksheetStringBuilder(
                tmffSP.sb, tmffSP.prevColNbr + 1, tmffSP.rightmostColNbr);
        return tmffSP.currLineNbr;
    }

    @Override
    protected int renderSubExprWithBreaks(final TMFFStateParams tmffSP,
        final ParseNode currNode, final int leftmostColNbr)
    {
        throw new IllegalArgumentException(
            TMFFConstants.ERRMSG_UNFORMATTED_BAD_CALL_UNF_1);
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
