//********************************************************************/
//* Copyright (C) 2006                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * TMFFAlignColumn.java  0.02 08/01/2007
 *
 * Aug-28-2006: - new, "AlignColumn" Method for formula rendering in
 *                text mode in the new mmj2 feature "TMFF".
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
import mmj.pa.ErrorCode;
import mmj.tmff.TMFFConstants.AlignType;

/**
 * TMFFAlignColumn aligns portions of a sub-expression into a single column when
 * splitting the sub-expression across multiple lines.
 * <p>
 * TMFFAlignColumn renders a parsed sub-expression and if the expression exceeds
 * the input length or tree depth parameters, the sub-expression is broken up
 * across multiple lines. Either Variables, Constants or just plain Symbols can
 * be aligned into a single column.
 * <p>
 * The idea here is to enable use of multiple instances of TMFFAlignColumn --
 * and others -- customized for specific formatting schemes. TMFFAlignColumn
 * instances vary according to 3 parameters which designate the type of symbol
 * objects to be aligned and the starting point within a syntax axiom formula
 * for alignment.
 * <p>
 *
 * <pre>
 * Example:
 *
 *     alignAtNbr = 3
 *     alignAtValue = Sym
 *     alignByValue = Var
 *
 *        render "( a -> b )" as follows:
 *
 *               "( a ->
 *                    b )"
 *
 *            where "a" and "b" are metavariables that
 *            may be replaced by sub-expressions of
 *            arbitrary length and depth.
 *
 * Example:
 *
 *     alignAtNbr = 1
 *     alignAtValue = Sym
 *     alignByValue = Cnst
 *
 *        render "( a -> b )" as follows:
 *
 *               "( a
 *                -> b
 *                )"
 * </pre>
 */
public class TMFFAlignColumn extends TMFFMethod {

    protected int alignAtNbr;
    protected AlignType alignAtValue;
    protected AlignType alignByValue;

    /**
     * Helper to calculate the arbitrary code number signifying Cnst or Var
     * within TMFF.
     *
     * @param sym to interrogate.
     * @return TMFFConstants.ALIGN_CNST if the input Sym is a Cnst, else,
     *         TMFFConstants.ALIGN_VAR.
     */
    public static AlignType getAlignTypeValue(final Sym sym) {
        return sym instanceof Cnst ? AlignType.Cnst : AlignType.Var;
    }

    private static AlignType validateAlignType(final String value,
        final String var)
    {
        if (value != null)
            try {
                return AlignType.valueOf(value);
            } catch (final IllegalArgumentException e) {
                throw new IllegalArgumentException(ErrorCode
                    .format(TMFFConstants.ERRMSG_BAD_BY_VALUE, var, value));
            }
        throw new IllegalArgumentException(
            ErrorCode.format(TMFFConstants.ERRMSG_MISSING_BY_VALUE, var));
    }

    /**
     * Validates an alignment type string and converts it into the enum
     * equivalent used internally by the program
     *
     * @param byValue string: sym, var, cnst, etc.
     * @return numeric equivalent to byValue string (see TMFFConstants.ALIGN_*).
     */
    public static AlignType validateByValue(final String byValue) {
        return validateAlignType(byValue, TMFFConstants.TMFF_BY_VALUE);
    }

    /**
     * Validates an alignment type string and converts it into the numeric
     * equivalent used internally by the program (sym = 1, etc.)
     *
     * @param atValue string: sym, var, cnst, etc.
     * @return numeric equivalent to byValue string (see TMFFConstants.ALIGN_*).
     */
    public static AlignType validateAtValue(final String atValue) {
        return validateAlignType(atValue, TMFFConstants.TMFF_AT_VALUE);
    }

    /**
     * Validates an alignment number.
     *
     * @param atNbrString ought to be 1, 2, or 3 (any higher than 3 has no
     *            apparent use.)
     * @return numeric equivalent to byValue string (see TMFFConstants.ALIGN_*).
     */
    public static int validateAtNbr(final String atNbrString) {

        try {
            return TMFFAlignColumn
                .validateAtNbr(Integer.parseInt(atNbrString.trim()));
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException(TMFFConstants.ERRMSG_BAD_AT_NBR_1
                + atNbrString + TMFFConstants.ERRMSG_BAD_AT_NBR_2);
        }
    }

    /**
     * Validates an alignment number.
     *
     * @param atNbr ought to be 1, 2, or 3 (any higher than 3 has no apparent
     *            use.)
     * @return numeric equivalent to byValue string (see TMFFConstants.ALIGN_*).
     */
    public static int validateAtNbr(final int atNbr) {
        if (atNbr < TMFFConstants.MIN_ALIGN_AT_NBR
            || atNbr > TMFFConstants.MAX_ALIGN_AT_NBR)
            throw new IllegalArgumentException(TMFFConstants.ERRMSG_BAD_AT_NBR_1
                + atNbr + TMFFConstants.ERRMSG_BAD_AT_NBR_2);
        return atNbr;
    }

    /**
     * Default constructor.
     */
    public TMFFAlignColumn() {
        super();
    }

    /**
     * Standard constructor for TMFFAlignColumn.
     *
     * @param maxDepth maximum sub-tree depth for a sub-expression that will not
     *            trigger a line-break, not counting leaf nodes, and
     *            non-Notation Syntax Axioms such as Type Conversions.
     * @param byValue Text indicating the alignment type when a Syntax Axiom is
     *            split across multiple lines: 'Var', 'Cnst' or 'Sym'.
     * @param atNbr The nth occurrence of the atValue type is where an alignment
     *            column is positioned. Should be 1, 2, or 3.
     * @param atValue Text indicating the type the atNbr parm refers to for
     *            start of alignment: 'Var', 'Cnst' or 'Sym'.
     */
    public TMFFAlignColumn(final int maxDepth, final String byValue,
        final int atNbr, final String atValue)
    {
        super(maxDepth);

        alignByValue = TMFFAlignColumn.validateByValue(byValue);

        alignAtNbr = TMFFAlignColumn.validateAtNbr(atNbr);

        alignAtValue = TMFFAlignColumn.validateAtValue(atValue);
    }

    /**
     * Constructor for TMFFAlignColumn from user parameters.
     *
     * @param maxDepthString maximum sub-tree depth for a sub-expression that
     *            will not trigger a line-break, not counting leaf nodes, and
     *            non-Notation Syntax Axioms such as Type Conversions.
     * @param byValueString Text indicating the alignment type when a Syntax
     *            Axiom is split across multiple lines: 'Var', 'Cnst' or 'Sym'.
     * @param atNbrString The nth occurrence of the atValue type is where an
     *            alignment column is positioned. Should be 1, 2, or 3.
     * @param atValueString Text indicating the type the atNbr parm refers to
     *            for start of alignment: 'Var', 'Cnst' or 'Sym'.
     */
    public TMFFAlignColumn(final String maxDepthString,
        final String byValueString, final String atNbrString,
        final String atValueString)
    {
        super(maxDepthString);

        alignByValue = validateByValue(byValueString);

        alignAtNbr = validateAtNbr(atNbrString);

        alignAtValue = validateAtValue(atValueString);
    }

    @Override
    public JSONArray asArray() {
        return new JSONArray(TMFFConstants.TMFF_METHOD_USER_NAME_ALIGN_COLUMN,
            maxDepth, alignByValue.toString(), alignAtNbr,
            alignAtValue.toString());
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

        AlignType symAlignType = null;
        int alignTypeCnt = 0;

        boolean align = false;
        int alignPosition = -1;
        String token;
        int pos;

        ParseNode subNode;

        int[] reseq = null;

        Axiom axiom = null;

        final Stmt stmt = currNode.stmt;

        final Sym[] formulaSymArray = stmt.getFormula().getSym();

        if (stmt instanceof Axiom) {
            axiom = (Axiom)stmt;
            if (axiom.getIsSyntaxAxiom())
                reseq = axiom.getSyntaxAxiomVarHypReseq();
            else
                throw new IllegalArgumentException(
                    TMFFConstants.ERRMSG_BAD_SUB_EXPR_NODE_1);
        }

        int symI = 0; // start at 2nd formula sym
        int varI = -1; // start at 0 = 1st var index
        while (true) {

            align = false;

            if (++symI >= formulaSymArray.length)
                return 0;

            symAlignType = TMFFAlignColumn
                .getAlignTypeValue(formulaSymArray[symI]);

            pos = tmffSP.prevColNbr + 2; // default
            if (alignPosition == -1) {
                if (symAlignType == alignAtValue
                    || alignAtValue == AlignType.Sym)
                {

                    alignTypeCnt++;

                    if (alignTypeCnt >= alignAtNbr) {
                        align = true;
                        alignPosition = pos;
                    }
                }
            }
            else if (symAlignType == alignByValue
                || alignByValue == AlignType.Sym)
            {
                align = true;
                pos = alignPosition;
            }

            if (symAlignType == AlignType.Cnst) {

                token = formulaSymArray[symI].getId();

                if (!align && pos + token.length() > tmffSP.rightmostColNbr) {

                    tmffSP.newlineSB();
                    pos = alignPosition + 4;
                }

                if (tmffSP.appendTokenAtGivenPosition(token, pos) < 0)
                    return -1;

                continue; // loop to next sym
            }

            if (pos > tmffSP.rightmostColNbr)
                return -1;

            // fix for problem 20070705.2
            if (currNode.child.length == 0)
                return -1;

            varI++;
            if (reseq == null)
                subNode = currNode.child[varI];
            else
                subNode = currNode.child[reseq[varI]];

            // finagle: we want to pad to position pos - 2 because
            // the output tokens will be prefixed by " ".
            final int padPos = pos - 2;

            tmffSP.padSBToGivenPosition(padPos);

            if (renderSubExpr(tmffSP, subNode, pos) // new leftmostColNbr
            < 0)
                return -1;
        }
    }
}
