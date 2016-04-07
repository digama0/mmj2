//********************************************************************/
//* Copyright (C) 2006                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * TMFFMethod.java  0.01 11/01/2006
 *
 * Aug-28-2006: - new, base class for formula rendering in
 *                text mode in the new mmj2 feature "TMFF".
 */

package mmj.tmff;

import org.json.JSONArray;

import mmj.lang.*;
import mmj.pa.ErrorCode;

/**
 * TMFFMethod is the base class for text mode formula formatting (TMFF) methods.
 * It is designed to be fully reentrant and suitable for use with global
 * (default) formatting using a single Method, or at the Syntax Axiom / VarHyp
 * level of an individual ParseNode.
 */
public abstract class TMFFMethod {

    protected int maxDepth;

    protected abstract int renderSubExprWithBreaks(TMFFStateParams tmffSP,
        ParseNode currNode, int leftmostColNbr);

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
    public abstract boolean updateMaxDepth(int maxDepth);

    /**
     * Output the array form of this method's parameters.
     *
     * @return A JSON array
     */
    public abstract JSONArray asArray();

    /**
     * Validates the maxDepth parameter.
     *
     * @param maxDepth parameter.
     * @return maxDepth parameter.
     */
    public static int validateMaxDepth(final int maxDepth) {
        if (maxDepth < 1)
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_BAD_MAX_DEPTH_1);
        return maxDepth;
    }

    /**
     * Validates the maxDepth parameter.
     *
     * @param maxDepthString parameter.
     * @return maxDepth parameter.
     */
    public static int validateMaxDepth(final String maxDepthString) {
        try {
            return TMFFMethod
                .validateMaxDepth(Integer.parseInt(maxDepthString.trim()));
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_BAD_MAX_DEPTH_1);
        }
    }

    /**
     * Default constructor.
     */
    public TMFFMethod() {}

    /**
     * Constructor for TMFFMethod using user parameters.
     *
     * @param maxDepthString maximum sub-tree depth for a sub-expression that
     *            will not trigger a line-break, not counting leaf nodes, and
     *            non-Notation Syntax Axioms such as Type Conversions.
     */
    public TMFFMethod(final String maxDepthString) {

        maxDepth = TMFFMethod.validateMaxDepth(maxDepthString);
    }

    /**
     * Standard constructor for TMFFMethod.
     *
     * @param maxDepth maximum sub-tree depth for a sub-expression that will not
     *            trigger a line-break, not counting leaf nodes, and
     *            non-Notation Syntax Axioms such as Type Conversions.
     */
    public TMFFMethod(final int maxDepth) {

        this.maxDepth = TMFFMethod.validateMaxDepth(maxDepth);
    }

    private static <T> T opt(final T[] param, final int n) {
        return n < param.length ? param[n] : null;
    }

    /**
     * A crude TMFFMethod factory used to construct a TMFFMethod using BatchMMJ2
     * RunParm values from the TMFFDefineScheme command.
     *
     * @param param String parameter array corresponding to the BatchMMJ2
     *            RunParm command TMFFDefineScheme.
     * @return TMFFMethod constructed according to the user parameters.
     */
    public static TMFFMethod constructMethodWithUserParams(
        final String[] param)
    {

        final String methodName = opt(param, 1);
        if (methodName == null)
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_MISSING_USER_METHOD_NAME);

        if (methodName
            .equalsIgnoreCase(TMFFConstants.TMFF_METHOD_USER_NAME_ALIGN_COLUMN))
            return new TMFFAlignColumn(opt(param, 2), opt(param, 3),
                opt(param, 4), opt(param, 5));

        if (methodName.equalsIgnoreCase(
            TMFFConstants.TMFF_METHOD_USER_NAME_TWO_COLUMN_ALIGNMENT))
            return new TMFFTwoColumnAlignment(opt(param, 2));

        if (methodName
            .equalsIgnoreCase(TMFFConstants.TMFF_METHOD_USER_NAME_FLAT))
            return new TMFFFlat();

        if (methodName
            .equalsIgnoreCase(TMFFConstants.TMFF_METHOD_USER_NAME_UNFORMATTED))
            return new TMFFUnformatted();

        throw new IllegalArgumentException(ErrorCode
            .format(TMFFConstants.ERRMSG_BAD_USER_METHOD_NAME, methodName));
    }

    /**
     * Formats a formula and outputs it to a StringBuilder using the given
     * ParseTree root node and initial Constant of the formula.
     * <p>
     * This is the main method *in* TMFFMethod for formatting a formula. I
     * <p>
     * Note that if the returned number of lines = -1 the formula has not been
     * formatted, BUT the input StringBuilder may have been updated. To deal
     * with this, it is possible to restore the StringBuilder because output is
     * appended to the original input. See TMFFPreverences.renderFormula() for
     * an example.
     * <p>
     * NOTE: TMFFUnformatted AND TMFFFlat override this method!
     *
     * @param tmffSP TMFFStateParams initialized, ready for use.
     * @param parseTree for the formula to be formatted. If left null, -1 is
     *            returned.
     * @param formula formula to be formatted.
     * @return number of lines rendered or -1 if an error was encountered and
     *         the formula could not be formatted.
     */
    public int renderFormula(final TMFFStateParams tmffSP,
        final ParseTree parseTree, final Formula formula)
    {

        tmffSP.currLineNbr = 1;
        if (parseTree != null) {
            if (tmffSP.appendTokenAtGivenPosition(formula.getTyp().getId(),
                tmffSP.leftmostColNbr) < 0)
                tmffSP.currLineNbr = -1;
            else if (renderSubExpr(tmffSP, parseTree.getRoot(),
                tmffSP.prevColNbr + 2) < 0)
                tmffSP.currLineNbr = -1;
        }
        else
            tmffSP.currLineNbr = -1;

        return tmffSP.currLineNbr;

    }

    /**
     * Renders a sub-expression by first attempting to output on the current
     * line within the maxDepth restriction; if that is not possible then it
     * makes a polymorphic call to render the sub-expression using line breaks.
     * This is a fairly important bit of code :)
     * <p>
     * Note that if later we assign TMFFSchemes at the level of individual
     * Syntax Axioms, we could modify this routine to invoke the method instance
     * stored in an array (4 elements) inside mmj.lang.Axiom. In preparation,
     * Format Nbr is stored inside TMFFStateParams so that the array lookup can
     * be done -- the array index would be set at the start of formula rendering
     * and each method would invoke the 'i-th' TMFFMethod in the current node's
     * stmt object (except for VarHyps). Bit of work but not too hard.
     *
     * @param tmffSP the TMFF state data
     * @param currNode the current node of the tree
     * @param leftmostColNbr the indent amount
     * @return -1 if error else 0.
     */
    protected int renderSubExpr(final TMFFStateParams tmffSP,
        final ParseNode currNode, final int leftmostColNbr)
    {
        final int subExprOutputLength = currNode.renderParsedSubExpr(tmffSP.sb,
            maxDepth, tmffSP.getAvailLengthOnCurrLine());

        if (subExprOutputLength < 0) {
            if (renderSubExprWithBreaks(tmffSP, currNode, leftmostColNbr) < 0)
                return -1;
        }
        else
            tmffSP.prevColNbr += subExprOutputLength;
        return 0;
    }
}
