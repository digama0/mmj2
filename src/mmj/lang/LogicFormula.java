//********************************************************************/
//* Copyright (C) 2005, 2006                                         */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/


/*
 * @(#)LogicFormula.java  0.02 02/01/2006
 *
 *  -->Moved accumHypInList from LogicFormula to Formula
 *
 */

package mmj.lang;

import java.util.*;

/**
 *  LogicFormula is a convenience class for LogHyp.
 *
 *  @see <a href="../../MetamathERNotes.html">
 *       Nomenclature and Entity-Relationship Notes</a>
 */
public class LogicFormula extends Formula {

    /**
     *  Construct using count and Sym array.
     *
     *  @param workCnt length of formula.
     *  @param workFormula Formula Sym array.
     */
    public LogicFormula(int     workCnt,
                        Sym[]   workFormula) {
        super(workCnt,
              workFormula);
    }

    /**
     *  Construct using String Type Code and Sym List.
     *  <p>
     *  Verifies that each Sym id in Expression is active
     *  and accumulates the matching VarHyp's in the input
     *  exprHypList param.
     *
     *  @param symTbl      Symbol Table (map).
     *  @param typS        Formula Type Code String.
     *  @param symList     Formula Expression as List of Sym id
     *                     Strings
     *  @param exprHypList ArrayList of Hyps, updated in function.
     */
    public LogicFormula(Map       symTbl,
                        String    typS,
                        ArrayList symList,
                        ArrayList exprHypList)
                                      throws LangException {
        super(symTbl,
              symList.size() + 1,
              typS);
        verifyExprSymsDefAndActive(symTbl,
                                   symList,
                                   exprHypList);
    }


    /**
     *  Verifies that each Sym id in Expression is active
     *  and accumulates the matching VarHyp's in the input
     *  exprHypList param.
     *  <p>
     *  <ol>
     *    <li> verify that each symbol string in an expression is
     *         defined and active, and that each referenced
     *         variable is defined by an active variable
     *         hypothesis (in scope). NOTE: this routine
     *         is not used for disjoint variables because
     *         they do not have "expressions" as such, and
     *         also, they may not have active variable
     *         hypotheses.
     *    <li> build a Sym[] as the symbol strings are scanned,
     *         thus converting the source strings to unique object
     *         references.
     *    <li> while building Sym[], build the set of variable
     *         hypotheses for the variables in the expression,
     *         and store them in the input hypList.
     *
     * @param symTbl   Map containing Cnst and Var definitions.
     *
     * @param symList  expression's symbol character
     *        strings
     *
     * @param hypList  ArrayList of Hyp's. Is updated with
     *        unique variable hypotheses in the expression.
     *        Because the list is maintained in database statement
     *        sequence order, hypList should either be empty (new)
     *        before the call, or already be in that order
     *        (see <code>accumHypInList</code>.
     *
     * @throws  LangException if duplicate symbol, etc.
     *          (see <code>mmj.lang.LangConstants.java</code>)
     */
    public void verifyExprSymsDefAndActive(Map       symTbl,
                                           ArrayList symList,
                                           ArrayList hypList)
                                               throws LangException {
        String       symS;
        VarHyp       varHyp;

        ListIterator x      = symList.listIterator();
        while (x.hasNext()) {
            symS = (String)x.next();
            sym[cnt] = (Sym)symTbl.get(symS);
            if (sym[cnt] == null) {
                throw new LangException(
                    LangConstants.ERRMSG_EXPR_SYM_NOT_DEF +
                        symS);
            }
            if (!(sym[cnt].isActive())) {
                throw new LangException(
                    LangConstants.ERRMSG_EXPR_SYM_NOT_ACTIVE +
                        symS);
            }
            if (sym[cnt].isVar()) {
                if ((varHyp = ((Var)sym[cnt]).getActiveVarHyp())
                    == null) {
                    throw new LangException(
                        LangConstants.ERRMSG_EXPR_VAR_W_O_ACTIVE_VAR_HYP +
                            symS);
                }
                //add varHyp to mandatory hypotheses in hypList
                Formula.accumHypInList(hypList, varHyp);
            }
            ++cnt;
        }
    }

}
