//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/**
 *  VarHypFormula.java  0.01 08/07/2005
 */

package mmj.lang;

import java.util.*;

/**
 *  VarHypFormula is a convenience class for VarHyp.
 *
 *  @see <a href="../../MetamathERNotes.html">
 *       Nomenclature and Entity-Relationship Notes</a>
 */
public class VarHypFormula extends Formula {

    /**
     *  Construct using count and Sym array.
     *
     *  @param workCnt length of formula.
     *  @param workFormula Formula Sym array.
     */
    public VarHypFormula(int     workCnt,
                         Sym[]   workFormula) {
        super(workCnt,
              workFormula);
    }

    /**
     *  Construct using Type Code String and Var id String.
     *  <p>
     *  The length of a VarHyp Formula is always 2.
     *
     *  @param symTbl Symbol Table (map).
     *  @param typS   Type Code String.
     *  @param varS   Var id String.
     */
    public VarHypFormula(Map       symTbl,
                         String    typS,
                         String    varS)
                                       throws LangException {
        super(symTbl,
              2,        // formula size
              typS);

        cnt = 2;
        setVarHypVar(symTbl,
                     varS);
    }

    /**
     *  Return the VarHypFormula's Var (sym[1]).
     *
     *  @return the VarHypFormula's Var (sym[1]).
     */
    public Var getVarHypVar() {
        return (Var)sym[1];
    }

    /**
     *  Set the VarHypFormula's Var (sym[1]).
     *
     *  @param var  the VarHypFormula's Var (sym[1]).
     */
    public void setVarHypVar(Var var) {
        sym[1] = var;
    }

    /**
     *  Set the VarHypFormula's Var (sym[1]).
     *
     *  @param symTbl Symbol Table (Map).
     *  @param varS   the VarHypFormula's Var id String.
     */
    public Var setVarHypVar(Map     symTbl,
                            String  varS)
                                        throws LangException {

        sym[1] = Var.verifyVarDefAndActive(symTbl,
                                           varS);
        return (Var)sym[1];
    }
}
