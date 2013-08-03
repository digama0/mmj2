//********************************************************************/
//* Copyright (C) 2006                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/


/*
 *  TMFFAlignColumn.java  0.02 08/01/2007
 *
 *  Aug-28-2006: - new, "AlignColumn" Method for formula rendering in
 *                 text mode in the new mmj2 feature "TMFF".
 *
 *  Aug-01-2007  - renderSubExprWithBreaks() fixed to avoid
 *                 ArrayIndexOutOfBoundsException when
 *                 invoked with a VarHyp parse node (problem
 *                 is that a VarHyp ParseNode.child array
 *                 has length = 0.
 */

package mmj.tmff;
import mmj.lang.Assrt;
import mmj.lang.Axiom;
import mmj.lang.Formula;
import mmj.lang.ParseNode;
import mmj.lang.Stmt;
import mmj.lang.Sym;
import mmj.lang.VarHyp;

/**
 *  TMFFAlignColumn aligns portions of a sub-expression into
 *  a single column when splitting the sub-expression across
 *  multiple lines.
 *  <p>
 *  TMFFAlignColumn renders a parsed sub-expression
 *  and if the expression exceeds the input length
 *  or tree depth parameters, the sub-expression is
 *  broken up across multiple lines. Either Variables,
 *  Constants or just plain Symbols can be aligned into
 *  a single column.
 *  <p>
 *  The idea here is to enable use of multiple instances
 *  of TMFFAlignColumn -- and others -- customized for
 *  specific formatting schemes. TMFFAlignColumn instances
 *  vary according to 3 parameters which designate the
 *  type of symbol objects to be aligned and the starting
 *  point within a syntax axiom formula for alignment.
 *  <p>
 *  <code>
 *  Example:
 *
 *      alignAtNbr = 3
 *      alignAtValue = Sym
 *      alignByValue = Var
 *
 *         render "( a -> b )" as follows:
 *
 *                "( a ->
 *                     b )"
 *
 *             where "a" and "b" are metavariables that
 *             may be replaced by sub-expressions of
 *             arbitrary length and depth.
 *
 *  Example:
 *
 *      alignAtNbr = 1
 *      alignAtValue = Sym
 *      alignByValue = Cnst
 *
 *         render "( a -> b )" as follows:
 *
 *                "( a
 *                 -> b
 *                 )"
 *
 *  <p>
 */
public class TMFFAlignColumn extends TMFFMethod {

    protected     int alignAtNbr;
    protected     int alignAtValue;
    protected     int alignByValue;

    /**
     *  Helper to calculate the arbitrary code number
     *  signifying Cnst or Var within TMFF.
     *
     *  @param sym to interrogate.
     *
     *  @return TMFFConstants.ALIGN_CNST if the input Sym
     *  is a Cnst, else, TMFFConstants.ALIGN_VAR.
     */
    public static int getAlignTypeValue(Sym sym) {
        if (sym.isCnst()) {
            return TMFFConstants.ALIGN_CNST;
        }
        else {
            return TMFFConstants.ALIGN_VAR;
        }
    }

    /**
     *  Validates an alignment type string and converts it
     *  into the numeric equivalent used internally by
     *  the program (sym = 1, etc.)
     *
     *  @param byValue string: sym, var, cnst, etc.
     *
     *  @return numeric equivalent to byValue string
     *          (see TMFFConstants.ALIGN_*).
     */
    public static int validateByValue(String byValue) {
        if (byValue != null) {

            for (int i = 0;
                 i < TMFFConstants.ALIGN_TYPE.length;
                 i++) {

                if (TMFFConstants.ALIGN_TYPE[i].
                        compareToIgnoreCase(
                            byValue)
                    == 0) {
                    return ++i;
                }
            }

            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_BAD_BY_VALUE_1
                + byValue
                + TMFFConstants.ERRMSG_BAD_BY_VALUE_2);
        }
        throw new IllegalArgumentException(
            TMFFConstants.ERRMSG_MISSING_BY_VALUE_1);
    }

    /**
     *  Validates an alignment type string and converts it
     *  into the numeric equivalent used internally by
     *  the program (sym = 1, etc.)
     *
     *  @param atValue string: sym, var, cnst, etc.
     *
     *  @return numeric equivalent to byValue string
     *          (see TMFFConstants.ALIGN_*).
     */
    public static int validateAtValue(String atValue) {
        if (atValue != null) {

            for (int i = 0;
                 i < TMFFConstants.ALIGN_TYPE.length;
                 i++) {

                if (TMFFConstants.ALIGN_TYPE[i].
                        compareToIgnoreCase(
                            atValue)
                    == 0) {
                    return ++i;
                }
            }

            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_BAD_AT_VALUE_1
                + atValue
                + TMFFConstants.ERRMSG_BAD_AT_VALUE_2);
        }
        throw new IllegalArgumentException(
            TMFFConstants.ERRMSG_MISSING_AT_VALUE_1);
    }

    /**
     *  Validates an alignment number.
     *
     *  @param atNbrString ought to be 1, 2, or 3 (any higher
     *                     than 3 has no apparent use.)
     *
     *  @return numeric equivalent to byValue string
     *          (see TMFFConstants.ALIGN_*).
     */
    public static int validateAtNbr(String atNbrString) {

        try {
            return TMFFAlignColumn.validateAtNbr(
                Integer.parseInt(atNbrString.trim()));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_BAD_AT_NBR_1
                + atNbrString
                + TMFFConstants.ERRMSG_BAD_AT_NBR_2);
        }
    }

    /**
     *  Validates an alignment number.
     *
     *  @param atNbr ought to be 1, 2, or 3 (any higher
     *               than 3 has no apparent use.)
     *
     *  @return numeric equivalent to byValue string
     *          (see TMFFConstants.ALIGN_*).
     */
    public static int validateAtNbr(int atNbr) {
        if (atNbr < TMFFConstants.MIN_ALIGN_AT_NBR ||
            atNbr > TMFFConstants.MAX_ALIGN_AT_NBR) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_BAD_AT_NBR_1
                + atNbr
                + TMFFConstants.ERRMSG_BAD_AT_NBR_2);
        }
        return atNbr;
    }

    /**
     *  Default constructor.
     */
    public TMFFAlignColumn() {
        super();
    }

    /**
     *  Standard constructor for TMFFAlignColumn.
     *
     *  @param maxDepth maximum sub-tree depth for a sub-expression
     *                  that will not trigger a line-break, not
     *                  counting leaf nodes, and non-Notation
     *                  Syntax Axioms such as Type Conversions.
     *
     *  @param byValue  Text indicating the alignment type
     *                  when a Syntax Axiom is split across
     *                  multiple lines: 'Var', 'Cnst' or 'Sym'.
     *
     *  @param atNbr    The nth occurrence of the atValue type
     *                  is where an alignment column is positioned.
     *                  Should be 1, 2, or 3.
     *
     *  @param atValue Text indicating the type the atNbr
     *                  parm refers to for start of alignment:
     *                  'Var', 'Cnst' or 'Sym'.
     *
     */
    public TMFFAlignColumn(int    maxDepth,
                           String byValue,
                           int    atNbr,
                           String atValue) {
        super(maxDepth);

        alignByValue              =
            TMFFAlignColumn.validateByValue(byValue);

        alignAtNbr                =
            TMFFAlignColumn.validateAtNbr(atNbr);

        alignAtValue              =
            TMFFAlignColumn.validateAtValue(atValue);
    }


    /**
     *  Constructor for TMFFAlignColumn from user parameters.
     *
     *  @param maxDepthString maximum sub-tree depth for a sub-expression
     *                  that will not trigger a line-break, not
     *                  counting leaf nodes, and non-Notation
     *                  Syntax Axioms such as Type Conversions.
     *
     *  @param byValueString  Text indicating the alignment type
     *                  when a Syntax Axiom is split across
     *                  multiple lines: 'Var', 'Cnst' or 'Sym'.
     *
     *  @param atNbrString    The nth occurrence of the atValue type
     *                  is where an alignment column is positioned.
     *                  Should be 1, 2, or 3.
     *
     *  @param atValueString Text indicating the type the atNbr
     *                  parm refers to for start of alignment:
     *                  'Var', 'Cnst' or 'Sym'.
     *
     */
    public TMFFAlignColumn(String maxDepthString,
                           String byValueString,
                           String atNbrString,
                           String atValueString) {
        super(maxDepthString);

        alignByValue              =
            TMFFAlignColumn.validateByValue(byValueString);

        alignAtNbr                =
            TMFFAlignColumn.validateAtNbr(atNbrString);

        alignAtValue              =
            TMFFAlignColumn.validateAtValue(atValueString);
    }

    /**
     *  Updates maxDepth for a TMFFMethod if the Method
     *  allows updates.
     *
     *  @param maxDepth parameter.
     *
     *  @return boolean - true only if update performed.
     */
    public boolean updateMaxDepth(int maxDepth) {

        this.maxDepth             =
            TMFFMethod.validateMaxDepth(maxDepth);

        return true;
    }

    // return -1 if error else 0
    protected int renderSubExprWithBreaks(
                                TMFFStateParams tmffSP,
                                ParseNode       currNode,
                                int             leftmostColNbr) {

        int       symAlignType    = 0;
        int       alignTypeCnt    = 0;

        boolean   align           = false;
        int       alignPosition   = -1;
        String    token;
        int       pos;


        ParseNode subNode;

        int[]     reseq           = null;

        Axiom     axiom           = null;

        Stmt      stmt            = currNode.getStmt();

        Sym[]     formulaSymArray =
                        stmt.getFormula().getSym();

        VarHyp[]  mandVarHypArray =
                        stmt.getMandVarHypArray();

        if (stmt.isVarHyp()) {
            // ok, valid and no hyp resequencing
        }
        else {
            if (stmt.isAssrt()          &&
                ((Assrt)stmt).isAxiom()) {
                axiom             = (Axiom)stmt;
                if (axiom.getIsSyntaxAxiom()) {
                    reseq         =
                        axiom.getSyntaxAxiomVarHypReseq();
                }
                else {
                    throw new IllegalArgumentException(
                        TMFFConstants.ERRMSG_BAD_SUB_EXPR_NODE_1);
                }
            }
        }

        int       symI            = 0;  //start at 2nd formula sym
        int       varI            = -1; //start at 0 = 1st var index
        while (true) {

            align                 = false;

            if (++symI >= formulaSymArray.length) {
                return 0;
            }

            symAlignType          =
                TMFFAlignColumn.getAlignTypeValue(
                    formulaSymArray[symI]);

            pos                   = tmffSP.prevColNbr + 2; //default
            if (alignPosition == -1) {
                if (symAlignType == alignAtValue
                    ||
                    (alignAtValue ==
                     TMFFConstants.ALIGN_SYM)) {

                    ++alignTypeCnt;

                    if (alignTypeCnt >= alignAtNbr) {
                        align     = true;
                        alignPosition
                                  = pos;
                    }
                }
            }
            else {
                if (symAlignType == alignByValue
                    ||
                    (alignByValue ==
                     TMFFConstants.ALIGN_SYM)) {
                    align         = true;
                    pos           = alignPosition;
                }
            }

            if (symAlignType == TMFFConstants.ALIGN_CNST) {

                token             = formulaSymArray[symI].getId();

                if ((!align)
                       &&
                    (pos + token.length() >
                     tmffSP.rightmostColNbr)) {

                    tmffSP.newlineSB();
                    pos  = alignPosition + 4;
                }

                if (tmffSP.appendTokenAtGivenPosition(token,
                                                      pos)
                    < 0) {
                    return -1;
                }

                continue; //loop to next sym
            }

            if (pos > tmffSP.rightmostColNbr) {
                return -1;
            }

            //fix for problem 20070705.2
            if (currNode.getChild().length == 0) {
                return -1;
            }

            ++varI;
            if (reseq == null) {
                subNode           =
                        (currNode.getChild())[varI];
            }
            else {
                subNode           =
                        (currNode.getChild())[reseq[varI]];
            }

            // finagle: we want to pad to position pos - 2 because
            //          the output tokens will be prefixed by " ".
            int padPos            = pos - 2;

            tmffSP.padSBToGivenPosition(padPos);

            if (renderSubExpr(tmffSP,
                              subNode,
                              pos)        // new leftmostColNbr
                < 0) {
                return -1;
            }
        }
    }
}
