//********************************************************************/
//* Copyright (C) 2006                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * TMFFTwoColumnAlignment.java  0.02 08/01/2007
 *
 * Oct-06-2006: - new, "TwoColumnAlignment" Method for formula
 *                rendering in text mode in the new mmj2 feature
 *                "TMFF".
 *
 * Aug-01-2007  - renderSubExprWithBreaks() fixed to avoid
 *                ArrayIndexOutOfBoundsException when
 *                invoked with a VarHyp parse node (problem
 *                is that a VarHyp ParseNode.child array
 *                has length = 0.
 */

package mmj.tmff;

import org.json.JSONArray;

import mmj.lang.*;

/**
 * TMFFTwoColumnAlignment aligns portions of a sub-expression into a two columns
 * when splitting the sub-expression across multiple lines: the left column is
 * used for constants and the right column is used for variables. However, if
 * the expression contains only constants or only variables then only one column
 * is used.
 * <p>
 * TMFFTwoColumnAlignment renders a parsed sub-expression and if the expression
 * exceeds the input length or tree depth parameters, the sub-expression is
 * broken up across multiple lines.
 * <p>
 * <p>
 *
 * <pre>
 * Example:
 *
 *     maxDepth = 1
 *
 *        render "( a -> b )" as follows:
 *
 *               "(  a
 *                -> b )"
 *
 *            where "a" and "b" are metavariables that
 *            may be replaced by sub-expressions of
 *            arbitrary length and depth.
 *
 *            Note that the trailing constant is appended
 *            to the current line (or if no more room exists
 *            then it is indented 4 columns from the right
 *            column's position.
 *
 * Example:
 *     maxDepth = 1
 *
 *        render "a (_ b" as follows:
 *
 *               "   a
 *                (_ b"
 *
 *            where "a" and "b" are metavariables that
 *            may be replaced by sub-expressions of
 *            arbitrary length and depth.
 * </pre>
 */
public class TMFFTwoColumnAlignment extends TMFFMethod {

    /**
     * Default constructor.
     */
    public TMFFTwoColumnAlignment() {
        super();
    }

    /**
     * Standard constructor for TMFFTwoColumnAlignment.
     *
     * @param maxDepth maximum sub-tree depth for a sub-expression that will not
     *            trigger a line-break, not counting leaf nodes, and
     *            non-Notation Syntax Axioms such as Type Conversions.
     */
    public TMFFTwoColumnAlignment(final int maxDepth) {
        super(maxDepth);
    }

    /**
     * Constructor for TMFFTwoColumnAlignment from user parameters.
     *
     * @param maxDepthString maximum sub-tree depth for a sub-expression that
     *            will not trigger a line-break, not counting leaf nodes, and
     *            non-Notation Syntax Axioms such as Type Conversions.
     */
    public TMFFTwoColumnAlignment(final String maxDepthString) {
        super(maxDepthString);
    }

    @Override
    public JSONArray asArray() {
        return new JSONArray(
            TMFFConstants.TMFF_METHOD_USER_NAME_TWO_COLUMN_ALIGNMENT, maxDepth);
    }

    /**
     * Updates maxDepth for a TMFFMethod if the Method allows updates.
     *
     * @param maxDepth parameter.
     * @return boolean - true only if update performed.
     */
    @Override
    public boolean updateMaxDepth(final int maxDepth) {

        this.maxDepth = TMFFMethod.validateMaxDepth(maxDepth);

        return true;
    }

    // return -1 if error else 0
    @Override
    protected int renderSubExprWithBreaks(final TMFFStateParams tmffSP,
        final ParseNode currNode, final int leftmostColNbr)
    {

        final Stmt stmt = currNode.stmt;
        final Sym[] formulaSymArray = stmt.getFormula().getSym();

        Axiom axiom = null;
        int[] reseq = null;
        final int leftColPos = tmffSP.prevColNbr + 2;
        int rightColPos = leftColPos; // default
        if (stmt instanceof VarHyp)
            // ok, valid and no hyp resequencing
            // but VarHyp's have no child nodes...
            return -1;
        else if (stmt instanceof Axiom) {
            axiom = (Axiom)stmt;
            if (axiom.getIsSyntaxAxiom()) {
                reseq = axiom.getSyntaxAxiomVarHypReseq();
                // getWidthOfWidestExprCnst returns -1 if
                // no constants or null, so adding 1 regardless
                // works -- makes right=leftColPos...
                rightColPos += 1 + axiom.getWidthOfWidestExprCnst();
            }
            else
                throw new IllegalArgumentException(
                    TMFFConstants.ERRMSG_BAD_SUB_EXPR_NODE_1);
        }

        if (rightColPos > tmffSP.rightmostColNbr
            || leftColPos > tmffSP.rightmostColNbr)
            return -1;

        ParseNode subNode;
        String token;
        int pos;
        int symI = 0; // start at 2nd formula sym
        int varI = -1; // start at 0 = 1st var index
        while (true) {
            if (++symI >= formulaSymArray.length)
                return 0;
            if (formulaSymArray[symI] instanceof Cnst) {
                token = formulaSymArray[symI].getId();
                if (symI == formulaSymArray.length - 1) {
                    pos = tmffSP.prevColNbr + 2;
                    if (pos > tmffSP.rightmostColNbr) {
                        tmffSP.newlineSB();
                        pos = leftColPos;
                    }
                }
                else
                    pos = leftColPos;
                if (tmffSP.appendTokenAtGivenPosition(token, pos) < 0)
                    return -1;
                continue;
            }

            varI++;

            if (reseq == null)
                subNode = currNode.child[varI];
            else
                subNode = currNode.child[reseq[varI]];

            // finagle: we want to pad to position pos - 2 because
            // the output tokens will be prefixed by " ".
            tmffSP.padSBToGivenPosition(rightColPos - 2);

            if (renderSubExpr(tmffSP, subNode, rightColPos) // new
                                                            // leftmostColNbr,
            < 0)
                return -1;
        }
    }
}
