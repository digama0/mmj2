//********************************************************************/
//* Copyright (C) 2005, 2006                                         */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/


/*
 *  Hyp.java  0.05 08/01/2007
 *
 *  15-Jan-2006: added new constructor (protected) for
 *               use in Proof Assistant. Creates "temp"
 *               LogHyp object (which creates temp Hyp,
 *               etc.)
 *
 *  Version 0.03
 *      ==> minor comment change
 *
 *  Version 0.04:
 *
 *  Oct-12-2006: - added SymTbl to constructor and modified to
 *                 conform to Metamath.pdf spec change of 6-24-2006
 *                 prohibiting Stmt label and Sym id namespace
 *                 collisions.
 *
 *  Version 0.05 - 08/01/2007
 *               - Misc Work Var Enhancements.
 */

package mmj.lang;

import java.util.*;

/**
 *  Hyp unifies VarHyp (Variable Hypothesis) and LogHyp
 *  (Logical Hypothesis).
 *
 *  The unification of VarHyp and LogHyp is one of the
 *  things that makes Metamath elegant. Both are statements,
 *  both have labels, and both have formulas -- which means
 *  that both have Type Codes. Clean and simple (simple now
 *  that Megill invented it, that is :)
 *
 *  @see <a href="../../MetamathERNotes.html">
 *       Nomenclature and Entity-Relationship Notes</a>
 */
public abstract class Hyp extends Stmt {

    /**
     *  "active", true or false?
     *
     *  Only an "active" hypothesis can be referred to by
     *  another statement.
     */
    protected boolean   active;

    /**
     *  Construct using sequence number and label string.
     *
     *  @param seq     MObj.seq number
     *  @param symTbl  Symbol Table (Map)
     *  @param stmtTbl Statement Table (Map)
     *  @param labelS  Label String.
     *
     *  @throws LangException if duplicate, etc.
     */
    public Hyp(int     seq,
               Map     symTbl,
               Map     stmtTbl,
               String  labelS,
               boolean active)
                            throws LangException {
        super (seq,
               symTbl,
               stmtTbl,
               labelS);
        setActive(active);
    }

    /**
     *  Construct temp Hyp using precomputed values and
     *  doing no validation.
     *
     *  @param tempSeq       MObj.seq
     *  @param tempLabel     Stmt.label
     *  @param tempFormula   Stmt.formula
     *  @param tempParseTree Stmt.exprParseTree
     *  @param tempActive    Hyp.active
     */
    protected Hyp(int       tempSeq,
                  String    tempLabel,
                  Formula   tempFormula,
                  ParseTree tempParseTree,
                  boolean   tempActive) {
        super(tempSeq,
              tempLabel,
              tempFormula,
              tempParseTree);
        setActive(tempActive);
    }

    /**
     *  Is the Hyp a VarHyp?
     *  <p>
     *  Enquiring minds want to know!
     *
     *  @return true or false.
     */
    public abstract boolean  isVarHyp();

    /**
     *  Is the Hyp a WorkVarHyp?
     *  <p>
     *  Enquiring minds want to know!
     *
     *  @return true or false.
     */
    public abstract boolean  isWorkVarHyp();

    /**
     *  Is the Hyp a LogHyp?
     *  <p>
     *  Enquiring minds want to know!
     *
     *  @return true or false.
     */
    public abstract boolean  isLogHyp();

    /**
     *  Set Hyp.active, true or false.
     *
     *  @param active  true or false.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     *  Return Hyp.active, true or false.
     *
     *  @return Hyp.active, true or false.
     */
    public boolean isActive() {
        return active;
    }

    /**
     *  Is the Hyp an Assrt?
     *  <p>
     *  Nope. By definition.
     *
     *  @return false (a Hyp cannot be an Assrt.)
     */
    public boolean isAssrt() {
        return false;
    }

    /**
     *  Is the Hyp a Hyp?
     *  <p>
     *  I know the answer to that one.
     *
     *  @return true -- by definition :)
     */
    public boolean isHyp() {
        return true;
    }
}
