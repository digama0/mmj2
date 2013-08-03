//********************************************************************/
//* Copyright (C) 2005, 2006, 2007                                   */
//*   MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  VarHyp.java  0.06 08/01/2007
 *
 *  Sep-30-2005: change getMandHypArray() to
 *               getMandHypArrayLength().
 *
 *  Version 0.05:
 *
 *  Aug-27-2006: - added renderParsedSubExpr() for TMFF project
 *
 *  Oct-12-2006: - added StmtTbl to constructors and modified to
 *                 conform to Metamath.pdf spec change of 6-24-2006
 *                 prohibiting Stmt label and Sym id namespace
 *                 collisions.
 *
 *  Version 0.06 : 08/01/2007
 *               - Misc Work Var Enhancements:
 *                 -- added paSubst
 *                 -- added isWorkVarHyp
 *                 -- removed dummy var hyp stuff
 *                 -- added containedInVarListBySeq()
 *                 -- added accumVarHypListBySeq()
 */

package mmj.lang;

import java.util.*;

/**
 *  VarHyp -- Variable Hypothesis -- corresponds to
 *  the Metamath "$f" statement, or "floating hypothesis"
 *  statement.
 *  <p>
 *  The VarHyp is truly a hypothesis (Hyp) in Metamath,
 *  and it is also a statement (Stmt), complete with its
 *  own label and Formula.
 *  <p>
 *  The Formula of a VarHyp consists of a Cnst followed by
 *  a Var. For example, "wph $f wff ph $." is the famous
 *  Metamath declaration of phi, stating that variable
 *  "ph" <b>is a</b> wff.
 *  <p>
 *  <b>On a Tangent Concerning Type Codes and Conversions</b>
 *  <p>
 *  By the way, Metamath's base language is strongly typed.
 *  Every statement must have a Type Code (every Formula is
 *  at least one Sym long and the first Sym must be a Cnst.)
 *  In addition, subsitution of an expression for a variable
 *  is permitted in Proof Verification (or parsing) only
 *  if the two statements have the same Type Code.
 *  <p>
 *  Apparently  a less strict regime was overthrown after
 *  an inconsistency was "proved". The people of Metamathland
 *  quickly discovered the lie and revised their system
 *  to require their laws and terminology to be well-defined
 *  and impartially judged by incorruptible automatons.
 *  <p>
 *  Fortunately for the expressiveness of Metamath, Type
 *  Conversions can be defined. The most famous example
 *  is Syntax Axiom "cv" in set.mm:
 *  <p>
 *  <code></b>
 *  $( All sets are classes (but not vice-versa!). $)<br>
 *  cv $a class x $.
 *  </b></code>
 *
 *  @see <a href="../../MetamathERNotes.html">
 *       Nomenclature and Entity-Relationship Notes</a>
 */
public class VarHyp extends Hyp {

    /**
     *  paSubst is used in Proof Assistant unification and
     *  holds the root node of a subtree which is the
     *  assigned substitution value for the VarHyp in a
     *  single ProofStep.
     *  <p>
     *  Yes, this is a hokey scratchpad. In theory an
     *  array of paSubst could be devised, with one
     *  entry for each thread :-) Or not.
     *
     */
    public ParseNode paSubst;

    private   boolean isWorkVarHypInd;

    /**
     *  Construct VarHyp using sequence number plus label,
     *  Type Code and Var Strings.
     *
     *  @param seq     MObj.seq number
     *  @param symTbl  Symbol Table (Map)
     *  @param stmtTbl Statement Table (Map)
     *  @param varS    Var id String.
     *  @param labelS  Statement label String.
     *  @param typS    Type Code id String.
     *
     *  @throws LangException if duplicate, etc.
     */
    public VarHyp(int     seq,
                  Map     symTbl,
                  Map     stmtTbl,
                  String  varS,
                  String  labelS,
                  String  typS)
                            throws LangException {
        super(seq,
              symTbl,
              stmtTbl,
              labelS,
              true);   //true = "active"

        formula = new VarHypFormula(symTbl,
                                    typS,
                                    varS);

        if (getVar().getActiveVarHyp() != null) {
            throw new LangException(
                LangConstants.ERRMSG_MULT_ACTIVE_HYP_FOR_VAR +
                    labelS);
        }

    }

    /**
     *  Construct VarHyp using precomputed values and
     *  doing no validation.
     *
     *  @param tempVarHypSeq       MObj.seq
     *  @param tempVarHypLabel     Stmt.label
     *  @param tempVarHypFormula   Stmt.formula
     */
    protected VarHyp(int       tempVarHypSeq,
                     String    tempVarHypLabel,
                     Formula   tempVarHypFormula) {

        super(tempVarHypSeq,
              tempVarHypLabel,
              tempVarHypFormula,
              null,     // null tempVarHypParseTree for a moment...
              true);    // true = "active"

        getVar().setActiveVarHyp(this);

        setExprParseTree(
            new ParseTree(
                new ParseNode(
                    this)));

    }

    /**
     *  Sets VarHyp "active", either true or false.
     *  <p>
     *  Sets "active" to true or false. Also, if the
     *  new setting is "inactive", then the associated
     *  var.activeVarHyp is set to null (which happens
     *  at the end of a scope definition -- see LogicalSystem.)
     *
     *  @param active true or false.
     */
    public void setActive(boolean active) {
        this.active = active;
        if (!active) {
            getVar().setActiveVarHyp(null);
        }
    }


    /**
     *  Gets the Var for this VarHyp.
     *  <p>
     *  "De-embeds" the var from the VarHyp's VarHypFormula.
     *  <p>
     *  A "var" occurrence was stored redundantly here in
     *  VarHyp at one time, but it was not much used and
     *  was later eliminated as being "a waste of code".
     *
     *  @return Var for this VarHyp.
     */
    public Var getVar() {
        return ((VarHypFormula)formula).getVarHypVar();
    }

    /**
     *  Sets the Var for this VarHyp.
     *  <p>
     *  A "var" occurrence was stored redundantly here in
     *  VarHyp at one time, but it was not much used and
     *  was later eliminated as being "a waste of code".
     *
     *  @param var the new Var for the VarHypFormula.
     */
    public void setVar(Var var) {
        ((VarHypFormula)formula).setVarHypVar(var);
    }

    /**
     *  Is the VarHyp a VarHyp.
     *  <p>
     *  Hmmm...
     *
     *  @return true
     */
    public boolean isVarHyp() {
        return true;
    }

    /**
     *  Is the VarHyp a WorkVarHyp.
     *
     *  @return true or false
     */
    public boolean isWorkVarHyp() {
        return isWorkVarHypInd;
    }

    /**
     *  Set the isWorkVarHyp indicator.
     *
     *  @param isWorkVarHypInd set to true is WorkVarHyp
     */
    protected void setIsWorkVarHypInd(boolean isWorkVarHypInd) {
        this.isWorkVarHypInd      = isWorkVarHypInd;
    }

    /**
     *  Is the VarHyp a LogHyp.
     *  <p>
     *  Hmmm...I don't think so.
     *
     *  @return false.
     */
    public boolean isLogHyp() {
        return false;
    }

    /**
     *  Return the mandatory VarHyp array for this VarHyp.
     *  <p>
     *  I'd rather not explain this one, but at the time
     *  I coded it, I must have been thinking something...
     *  It appears useless, but ...
     *
     *  @return Mandatory VarHyp Array for this VarHyp.
     */
    public VarHyp[] getMandVarHypArray() {
        VarHyp[] vH = new VarHyp[0];
        return vH;
    }


    /**
     *  Return the mandatory Hyp array length for this VarHyp.
     *  <p>
     *
     *  @return Mandatory VarHyp Array Length for this VarHyp.
     */
    public int getMandHypArrayLength() {
        return 0;
    }

    /**
     *  Converts a parse sub-tree into a sub-expression
     *  which is output into a String Buffer.
     *  <p>
     *  Note: this will not work for a proof node! The
     *        ParseNode's stmt must be a VarHyp or
     *        a Syntax Axiom.
     *  <p>
     *  The output sub-expression is generated into
     *  text not to exceed the given maxLength. If the
     *  number of output characters exceeds maxLength
     *  output terminates, possibly leaving a dirty
     *  StringBuffer.
     *  <p>
     *  The depth of the sub-tree is checked against
     *  the input maxDepth parameter, and if the depth
     *  exceeds this number, output terminates,, possibly
     *  leaving a dirty StringBuffer.
     *  <p>
     *  Depth is computed as 1 for each Notation Syntax
     *  Axiom Node. VarHyp nodes and Nulls Permitted,
     *  Type Conversion and NamedTypedConstant Syntax
     *  Axiom nodes are assigned depth = 0 for purposes of
     *  depth checking.
     *  <p>
     *  @param sb            StringBuffer already initialized
     *                       for appending characters.
     *
     *  @param maxDepth      maximum depth of Notation Syntax
     *                       axioms in sub-tree to be printed.
     *                       Set to Integer.MAX_VALUE to turn
     *                       off depth checking.
     *
     *  @param maxLength     maximum length of output
     *                       sub-expression.
     *                       Set to Integer.MAX_VALUE to turn
     *                       off depth checking.
     *
     *  @param child         array of ParseNode, corresponding
     *                       to VarHyp nodes to be substituted
     *                       into the Stmt.
     *
     *  @return length of sub-expression characters
     *          appended to the input StringBuffer --
     *          or -1 if maxDepth or maxLength exceeded.
     */
    public int renderParsedSubExpr(StringBuffer sb,
                                   int          maxDepth,
                                   int          maxLength,
                                   ParseNode[]  child) {

        String s                  = getVar().getId();
        int    sLen               = s.length() + 1; //include ' '

        if (sLen > maxLength) {
            return -1;
        }

        sb.append(' ');
        sb.append(s);
        return sLen;
    }

    /**
     *  Accumulate VarHyp(no duplicates), storing
     *  it in an array list in order of appearance
     *  in the database.
     *  <p>
     *  Because varHypList is maintained in database statement
     *  sequence order, varHypList should either be empty (new)
     *  before the call, or already be in that order.
     *
     *  @param varHypList  ArrayList of Var Hyps, updated here.
     *
     */
    public void accumVarHypListBySeq(ArrayList varHypList) {

        int i           = 0;
        int iEnd        = varHypList.size();
        int newSeq      = this.seq;
        int existingSeq;

        while (true) {
            if (i < iEnd) {
                existingSeq       = ((VarHyp)varHypList.get(i)).seq;
                if (newSeq < existingSeq) {
                    //insert here, at "i"
                    break;
                }
                if (newSeq == existingSeq) {
                    //don't add, already here.
                    return;
                }
            }
            else {
                //insert at end, which happens to be here at "i"
                break;
            }
            ++i;
        }
        varHypList.add(i, this);
        return;
    }

    /**
     *  Searches for this Var Hyp in an ArrayList maintained
     *  in database input sequence.
     *
     *  @param varHypList ArrayList of Var Hyps
     *  @return true if found, else false.
     */
    public boolean containedInVarHypListBySeq(ArrayList varHypList) {
        VarHyp vH;
        for (int i = 0; i < varHypList.size(); i++) {
            vH                    = (VarHyp)varHypList.get(i);
            if (this.seq < vH.seq) {
                break;
            }
            if (vH == this) {
                return true;
            }
        }
        return false;
    }
}
