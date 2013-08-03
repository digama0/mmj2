//********************************************************************/
//* Copyright (C) 2006                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/


/*
 *  TMFFFlat.java  0.01 11/01/2006
 *
 *  Aug-28-2006: - new, "Flat" Method for formula rendering in
 *                 text mode in the new mmj2 feature "TMFF".
 */

package mmj.tmff;
import mmj.lang.Formula;
import mmj.lang.ParseNode;
import mmj.lang.ParseTree;

/**
 *  TMFFFlat renders a formula into a single line of text.
 *  <p>
 *  This rendering method was requested by Norm to provide
 *  a concise way to see an entire proof.
 *  <p>
 *  Note that with LineWrap on, the text will wrap around
 *  and occupy multiple screen lines
 *
 *  TMFFFlat overrides basic TMFFMethod renderFormula() to
 *  override the rightmostColNbr parameter temporarily,
 *  without altering the user setting for future invocations
 *  of TMFF.
 */
public class TMFFFlat extends TMFFMethod {

    /**
     *  Default constructor.
     */
    public TMFFFlat() {
        super();
    }

    /**
     *  Constructor for TMFFFlat using user parameters.
     *  <p>
     *  Sets maxDepth to Integer.MAX_VALUE so that no depth
     *  breaks are triggered.
     *  <p>
     *  @param maxDepthString maximum sub-tree depth for a
     *                  sub-expression
     *                  that will not trigger a line-break, not
     *                  counting leaf nodes, and non-Notation
     *                  Syntax Axioms such as Type Conversions.
     *                  Doesn't apply to TMFFFlat but provided
     *                  as common element to TMFFMethod class...
     */
    public TMFFFlat(String maxDepthString) {
        super(Integer.MAX_VALUE);
    }

    /**
     *  Standard constructor for TMFFFlat.
     *  <p>
     *  Sets maxDepth to Integer.MAX_VALUE so that no depth
     *  breaks are triggered.
     *  <p>
     *  @param maxDepth maximum sub-tree depth for a sub-expression
     *                  that will not trigger a line-break, not
     *                  counting leaf nodes, and non-Notation
     *                  Syntax Axioms such as Type Conversions.
     *                  Doesn't apply to TMFFFlat but provided
     *                  as common element to TMFFMethod class...
     */
    public TMFFFlat(int maxDepth) {
        super(Integer.MAX_VALUE);
    }


    /**
     *  Formats a formula and outputs it to a StringBuffer
     *  using the given ParseTree root node and initial
     *  Constant of the formula.
     *  <p>
     *  This method overrides the TMFFMethod renderFormula()
     *  method!
     *  <p>
     *  Sets rightmostColNbr to Integer.MAX_VALUE so that no
     *  line width breaks are triggered -- and this is done
     *  regardless of the user setting for the rightmost
     *  column number (overrides that setting.)
     *  <p>
     *  @param tmffSP TMFFStateParams initialized, ready for use.
     *
     *  @param parseTree ParseTree for the formula to be formatted.
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

        if (parseTree != null) {
            int savedRightmostColNbr
                                  = tmffSP.rightmostColNbr;
            tmffSP.rightmostColNbr
                                  = Integer.MAX_VALUE;
            tmffSP.currLineNbr    = 1;

            if (tmffSP.appendTokenAtGivenPosition(
                    formula.getTyp().getId(),
                    tmffSP.leftmostColNbr)
                < 0) {
                tmffSP.currLineNbr
                              = -1;
                tmffSP.rightmostColNbr
                              = savedRightmostColNbr; //restore
            }
            else {
                if (renderSubExpr(tmffSP,
                                  parseTree.getRoot(),
                                  tmffSP.prevColNbr + 2)
                    < 0) {

                    tmffSP.currLineNbr
                              = -1;
                    tmffSP.rightmostColNbr
                              = savedRightmostColNbr; //restore
                }
            }
        }
        else {
            tmffSP.currLineNbr    = -1;
        }

        return tmffSP.currLineNbr;

    }

    // return -1 if error else 0
    protected int renderSubExprWithBreaks(
                                TMFFStateParams tmffSP,
                                ParseNode       currNode,
                                int             leftmostColNbr) {

        throw new IllegalArgumentException(
            TMFFConstants.ERRMSG_UNFORMATTED_BAD_CALL_FLAT_1);

    }

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
    public boolean updateMaxDepth(int maxDepth) {

        return false;
    }

}
