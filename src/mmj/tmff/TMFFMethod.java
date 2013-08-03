//********************************************************************/
//* Copyright (C) 2006                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/


/*
 *  TMFFMethod.java  0.01 11/01/2006
 *
 *  Aug-28-2006: - new, base class for formula rendering in
 *                 text mode in the new mmj2 feature "TMFF".
 */

package mmj.tmff;
import mmj.lang.ParseNode;
import mmj.lang.ParseTree;
import mmj.lang.Formula;

/**
 *  TMFFMethod is the base class for text mode formula
 *  formatting (TMFF) methods. It is designed to be fully
 *  reentrant and suitable for use with global (default)
 *  formatting using a single Method, or at the Syntax
 *  Axiom / VarHyp level of an individual ParseNode.
 *
 */
public abstract class TMFFMethod {

    protected int maxDepth;

    protected abstract int renderSubExprWithBreaks(
                               TMFFStateParams tmffSP,
                               ParseNode       currNode,
                               int             leftmostColNbr);

    /**
     *  Updates maxDepth for a TMFFMethod if the Method
     *  allows updates.
     *  <p>
     *  As of the initial release, only TMFFAlignColumn
     *  uses maxDepth. The methods TMFFFlat and
     *  TMFFUnformatted have maxDepth = Integer.MAX_VALUE
     *  which results in no maxDepth line breaks from
     *  happening -- therefore, they do not allow updates
     *  after initial construction of the method.
     *
     *  @param maxDepth parameter.
     *
     *  @return boolean - true only if update performed.
     */
    public abstract boolean updateMaxDepth(int maxDepth);


    /**
     *  Validates the maxDepth parameter.
     *
     *  @param maxDepth parameter.
     *
     *  @return maxDepth parameter.
     */
    public static int validateMaxDepth(int maxDepth) {
        if (maxDepth < 1) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_BAD_MAX_DEPTH_1);
        }
        return maxDepth;
    }

    /**
     *  Validates the maxDepth parameter.
     *
     *  @param maxDepthString parameter.
     *
     *  @return maxDepth parameter.
     */
    public static int validateMaxDepth(String maxDepthString) {
        try {
            return TMFFMethod.validateMaxDepth(
                Integer.parseInt(maxDepthString.trim()));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_BAD_MAX_DEPTH_1);
        }
    }

    /**
     *  Default constructor.
     */
    public TMFFMethod() {
    }

    /**
     *  Constructor for TMFFMethod using user parameters.
     *
     *  @param maxDepthString maximum sub-tree depth for a
     *                  sub-expression
     *                  that will not trigger a line-break, not
     *                  counting leaf nodes, and non-Notation
     *                  Syntax Axioms such as Type Conversions.
     */
    public TMFFMethod(String maxDepthString) {

        this.maxDepth = TMFFMethod.validateMaxDepth(maxDepthString);
    }


    /**
     *  Standard constructor for TMFFMethod.
     *
     *  @param maxDepth maximum sub-tree depth for a sub-expression
     *                  that will not trigger a line-break, not
     *                  counting leaf nodes, and non-Notation
     *                  Syntax Axioms such as Type Conversions.
     */
    public TMFFMethod(int maxDepth) {

        this.maxDepth = TMFFMethod.validateMaxDepth(maxDepth);
    }

    /**
     *  A crude TMFFMethod factory used to construct a
     *  TMFFMethod using BatchMMJ2 RunParm values from
     *  the TMFFDefineScheme command.
     *
     *  @param param String parameter array corresponding to
     *               the BatchMMJ2 RunParm command
     *               TMFFDefineScheme.
     *
     *  @return TMFFMethod constructed according to the user
     *                  parameters.
     */
    public static TMFFMethod ConstructMethodWithUserParams(
                    String[] param) {

        String methodName         = null;
        String param3             = null;
        String param4             = null;
        String param5             = null;
        String param6             = null;
        int    pIndex             = 1;
        if (param.length > pIndex)                    {
            methodName            = param[pIndex++];
            if (param.length > pIndex)                {
                param3            = param[pIndex++];
                if (param.length > pIndex)            {
                    param4        = param[pIndex++];
                    if (param.length > pIndex)        {
                        param5    = param[pIndex++];
                        if (param.length > pIndex)    {
                            param6
                                  = param[pIndex++];
                        }
                    }
                }
            }
        }

        if (methodName == null) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_MISSING_USER_METHOD_NAME_1);
        }

        if (methodName.compareToIgnoreCase(
                TMFFConstants.TMFF_METHOD_USER_NAME_ALIGN_COLUMN)
            == 0) {
            return new TMFFAlignColumn(param3,
                                       param4,
                                       param5,
                                       param6);
        }

        if (methodName.compareToIgnoreCase(
                TMFFConstants.
                    TMFF_METHOD_USER_NAME_TWO_COLUMN_ALIGNMENT)
            == 0) {
            return new TMFFTwoColumnAlignment(param3);
        }


        if (methodName.compareToIgnoreCase(
                TMFFConstants.TMFF_METHOD_USER_NAME_FLAT)
            == 0) {
            return new TMFFFlat(param3);
        }

        if (methodName.compareToIgnoreCase(
                TMFFConstants.TMFF_METHOD_USER_NAME_UNFORMATTED)
            == 0) {
            return new TMFFUnformatted(param3);
        }

        throw new IllegalArgumentException(
            TMFFConstants.ERRMSG_BAD_USER_METHOD_NAME_1
            + methodName);
    }

    /**
     *  Formats a formula and outputs it to a StringBuffer
     *  using the given ParseTree root node and initial
     *  Constant of the formula.
     *  <p>
     *  This is the main method *in* TMFFMethod for formatting
     *  a formula. I
     *  <p>
     *  Note that if the returned number of lines = -1 the
     *  formula has not been formatted, BUT the input
     *  StringBuffer may have been updated. To deal with
     *  this, it is possible to restore the StringBuffer
     *  because output is appended to the original input.
     *  See TMFFPreverences.renderFormula() for an example.
     *  <p>
     *  NOTE: TMFFUnformatted AND TMFFFlat override this method!
     *  <p>
     *  @param tmffSP TMFFStateParams initialized, ready for use.
     *
     *  @param parseTree for the formula to be formatted.
     *                   If left null, -1 is returned.
     *
     *  @param formula formula to be formatted.
     *
     *  @return number of lines rendered or -1 if an error
     *                  was encountered and the formula could
     *                  not be formatted.
     */
    public int renderFormula(TMFFStateParams tmffSP,
                             ParseTree       parseTree,
                             Formula         formula) {

        tmffSP.currLineNbr        = 1;
        if (parseTree != null) {
            if (tmffSP.appendTokenAtGivenPosition(
                    formula.getTyp().getId(),
                    tmffSP.leftmostColNbr)
                < 0) {
                tmffSP.currLineNbr
                                  = -1;
            }
            else {
                if (renderSubExpr(tmffSP,
                                  parseTree.getRoot(),
                                  tmffSP.prevColNbr + 2)
                   < 0) {
                    tmffSP.currLineNbr
                                  = -1;
                }
            }
        }
        else {
            tmffSP.currLineNbr    = -1;
        }

        return tmffSP.currLineNbr;

    }

    /*
     *  Renders a sub-expression by first attempting
     *  to output on the current line within the maxDepth
     *  restriction; if that is not possible then it
     *  makes a polymorphic call to render the sub-expression
     *  using line breaks. This is a fairly important
     *  bit of code :)
     *
     *  Note that if later we assign TMFFSchemes at the
     *  level of individual Syntax Axioms, we could modify
     *  this routine to invoke the method instance stored
     *  in an array (4 elements) inside mmj.lang.Axiom.
     *  In preparation, Format Nbr is stored inside
     *  TMFFStateParams so that the array lookup
     *  can be done -- the array index would be set
     *  at the start of formula rendering and each method
     *  would invoke the 'ith' TMFFMethod in the current
     *  node's stmt object (except for VarHyps). Bit of
     *  work but not too hard.
     *
     *  @return -1 if error else 0.
     */
    protected int renderSubExpr(TMFFStateParams tmffSP,
                                ParseNode       currNode,
                                int             leftmostColNbr) {
        int subExprOutputLength =
            currNode.renderParsedSubExpr(
                               tmffSP.sb,
                               maxDepth,
                               tmffSP.getAvailLengthOnCurrLine());

        if (subExprOutputLength < 0) {
            if (renderSubExprWithBreaks(tmffSP,
                                        currNode,
                                        leftmostColNbr)
                < 0) {
                return -1;
            }
        }
        else {
            tmffSP.prevColNbr    += subExprOutputLength;
        }
        return 0;
    }
}