//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/**
 *  Formula.java  0.07 08/01/2008
 *
 *  Dec 31, 2005
 *  -->Added toProofWorksheetString() for ProofAsst
 *  -->Added constructor using ArrayList of Syms
 *  -->Moved accumHypInList from LogicFormula to Formula
 *  -->Cloned getParseNodeHolderExpr() to accept Hyp[]
 *     instead of VarHyp[] for use in ProofWorksheet.java.
 *
 *  Version 0.04 - Release 11/01/2006:
 *
 *  Sep 2, 2006:
 *  --> For TMFF project, cloned toProofWorksheetString()
 *      to make toProofWorksheetStringBuffer() so that
 *      the number of output lines can be returned, as
 *      desired by TMFF.
 *  --> Added Formula.constructTempDummyFormula() to
 *      temporarily hold something like "|- ?".
 *
 *  Oct 6, 2006:
 *  --> Added computeWidthOfWidestExprCnst() for TMFF
 *
 *  Version 0.05: 08/01/2007
 *      - Work Var Enhancement misc. changes.
 *
 *  Version 0.06: 02/01/2008
 *  --> Add exprToString()
 *
 *  Version 0.07: 08/01/2008
 *  --> Add srcStmtEquals()
 *  --> remove unused toProofWorksheetString() variant
 *  --> Added toStringBufferLineList() for MMTTheoremExportFormatter.
 */

package mmj.lang;

import java.util.*;
import mmj.mmio.SrcStmt;

/**
 *  Formula is, basically just an array of Sym, with a counter.
 *  <p>
 *  Sub-Classes of Formula exist: VarHypFormula and LogicFormula
 *  but their function is basically just to simplify certain
 *  coding in VarHyp, LogHyp, etc. In theory, the distinction
 *  would be useful for creating default exprRPN's.
 *  <p>
 *  Formula: contains Cnst in sym[0], followed by the formula's
 *            "expression", which consists of zero or more
 *            Cnst's and Var's in sym[1]...sym[cnt - 1].
 *  <p>
 *  Formula Factoids:
 *  <ul>
 *      <li>A formula must have length >= 1.
 *      <li>sym[0], the first Sym in a Formula must be a Cnst.
 *      <li>sym[0] is the Type Code of the Formula, and by
 *          extension, the Stmt's Type Code.
 *      <li>sym[1] through sym[sym.length - 1] are referred
 *          to in mmj as the Formula's "Expression" -- which
 *          is simply the Formula minus its Type Code.
 *      <li>Every Stmt has a Formula, but a Formula can be
 *          created without a corresponding Stmt (which is
 *          the reason Formula was made into a class.)
 *
 *  @see <a href="../../MetamathERNotes.html">
 *       Nomenclature and Entity-Relationship Notes</a>
 *
 */
public class Formula {

    /**
     *  Count of number of symbols in the formula.
     *  <p>
     *  In a valid Formula, <code>cnt == sym.length</code>.
     */
    int     cnt;

    /**
     *  Formula is just an array of Sym with a Count.
     */
    Sym[]   sym;

    /**
     *  Construct a temporary dummy Formula for transient
     *  use.
     *
     *  This is useful in ProofWorksheet.
     *
     *  @param typ is the Formula's TypCd Cnst.
     *  @param dummySym a string of characters that should
     *                  not have non-printable characters
     *                  or whitespace (else renderFormula
     *                  may come out wrong!)
     */
    public static Formula constructTempDummyFormula(
                              Cnst   typ,
                              String dummySym) {

        Cnst dummyCnst            =
            new Cnst(Integer.MAX_VALUE,
                     dummySym);
        dummyCnst.setIsTempObject(true);

        Sym[] tempSym             = new Sym[2];
        tempSym[0]                = typ;
        tempSym[1]                = dummyCnst;

        return new Formula(tempSym.length,
                           tempSym);
    }

    /**
     *  Construct using cnt and Sym array.
     *
     *  @param workCnt the correct length of the formula.
     *  @param workFormula the formula's Sym array.
     */
    public Formula(int     workCnt,
                   Sym[]   workFormula) {
        cnt = workCnt;
        sym = new Sym[cnt];

      //arraycopy was tried and it turned out to be slower!
      //System.arraycopy(workFormula,
      //                 0,
      //                 sym,
      //                 0,
      //                 cnt);
        for (int i = 0; i < cnt; i++) {
            sym[i] = workFormula[i];
        }
    }

    /**
     *  Construct using Sym ArrayList.
     *
     *  Enforces rule that first symbol must be a Cnst,
     *  just in case.
     *
     *  @param symList ArrayList containing formula symbols
     */
    public Formula(ArrayList symList) {

        cnt                       = symList.size();
        sym                       = new Sym[cnt];
        setTyp((Cnst)symList.get(0));
        for (int i = 1; i < cnt; i++) {
            sym[i]                = (Sym)symList.get(i);
        }
    }

    /**
     *  Construct Formula of given size and Type.
     *
     *  @param symTbl  Symbol Table (Map)
     *  @param sz      Size of the formula
     *  @param typS    Formula Type code
     */
    protected Formula(Map     symTbl,
                      int     sz,
                      String  typS)
                          throws LangException {
        sym = new Sym[sz];
        cnt = 1;
        setTyp(symTbl,
               typS);
    }

    /**
     *  Return Formula Type Code.
     *
     *  @return Formula Type Code (which is sym[0]).
     */
    public Cnst getTyp() {
        return (Cnst)sym[0];
    }

    /**
     *  Set Formula Type Code.
     *
     *  @param typ Formula Type Code (sym[0]).
     */
    public void setTyp(Cnst typ) {
        sym[0] = typ;
    }

    /**
     *  Return Formula's Expression (sym[1]...sym[cnt - 1]).
     *
     *  @return Formula's Expression (sym[1]...sym[cnt - 1]).
     */
    public Sym[] getExpr() {
        Sym[] expr = new Sym[cnt - 1];

        for (int f = 1, e = 0; f < cnt; f++, e++) {
            expr[e] = sym[f];
        }
        return expr;
    }

    /**
     *  Return Formula's length.
     *
     *  @return Formula's length.
     */
    public int getCnt() {
        return cnt;
    }

    /**
     *  Return Formula's symbol array.
     *
     *  @return Formula's symbol array.
     */
    public Sym[] getSym() {
        return sym;
    }

    /**
     *  Set Formula Type Code.
     *
     *  @param symTbl Symbol Table (Map).
     *  @param typS   Type Code String identifying a Cnst.
     *
     *  @return Type Code symbol.
     *
     *  @throws LangException if Type Code is undefined or
     *          not defined as a Cnst.
     */
    public Sym setTyp(Map       symTbl,
                      String    typS)
                                     throws LangException {

        Sym typC = (Sym)symTbl.get(typS);
        if (typC == null) {
            throw new LangException(
                LangConstants.ERRMSG_STMT_TYP_UNDEF +
                    typS);
        }
        if (!typC.isCnst()) {
            throw new LangException(
                LangConstants.ERRMSG_STMT_TYP_NOT_DEF_AS_CNST +
                    typS);
        }
        sym[0] = typC;
        return   typC;
    }


    /**
     *  Builds a "custom" version of an Expression in which
     *  an array of ParseNodeHolders is output for use in
     *  generating a ParseTree.
     *  <p>
     *  The key fact about the
     *  output ParseNodeHolders is that a Cnst in the Formula
     *  Expression just goes into the holder's "mObj" -- it
     *  will not be part of the ParseTree and the ParseNode
     *  element is null.
     *  <p>
     *  On the other hand, Variables in the
     *  Formula's Expression are converted into ParseNodes
     *  with Stmt = the VarHyp; the ParseNodeHolder's "mObj"
     *  element is set to the VarHyp Stmt reference -- and
     *  this ParseNode will be part of the ultimate Parse
     *  Tree. In effect, we're "parsing" VarHyps and creating
     *  their output ParseNodes at this time.)
     *
     *  @param varHypArray Array of VarHyp for Formula.
     *
     *  @return ParseNodeHolder array.
     *
     *  @throws IllegalArgumentException if unable to find
     *          a VarHyp for one of the Formula's Var's.
     */
    public ParseNodeHolder[] getParseNodeHolderExpr(
                                            VarHyp[] varHypArray) {
        ParseNodeHolder[] parseNodeHolderExpr
                            = new ParseNodeHolder[cnt - 1];
        int               dest = 0;
        VarHyp            vH;

        //start at i = 1 to bypass the Cnst at Formula.sym[0]
        for (int i = 1; i < cnt; i++) {
            if (sym[i].isVar()) {
                vH = ((Var)sym[i]).getVarHyp(varHypArray);
                if (vH == null) {
                    if (((Var)sym[i]).getIsWorkVar()) {
                        vH        =
                            ((Var)sym[i]).getActiveVarHyp();
                    }
                    if (vH == null) {
                        throw new IllegalArgumentException(
                            LangConstants.
                                ERRMSG_FORMULA_VAR_HYP_NOTFND_1 +
                                 sym[i]                         +
                            LangConstants.
                                ERRMSG_FORMULA_CAPTION          +
                                 toString());
                    }
                }
                parseNodeHolderExpr[dest] =
                    new ParseNodeHolder(vH);
            }
            else { //is Cnst, so no ParseNode, must an MObj to hold...
                parseNodeHolderExpr[dest] =
                    new ParseNodeHolder((Cnst)sym[i]);
            }
            ++dest;
        }
        return parseNodeHolderExpr;
    }

    /**
     *  Builds a "custom" version of an Expression in which
     *  an array of ParseNodeHolders is output for use in
     *  generating a ParseTree.
     *  <p>
     *
     *  @param hypArray Array of Hyp for Formula.
     *
     *  @return ParseNodeHolder array.
     *
     *  @throws IllegalArgumentException if unable to find
     *          a VarHyp for one of the Formula's Var's.
     */
    public ParseNodeHolder[] getParseNodeHolderExpr(
                                            Hyp[] hypArray) {
        ParseNodeHolder[] parseNodeHolderExpr
                            = new ParseNodeHolder[cnt - 1];
        int               dest = 0;
        VarHyp            vH;

        //start at i = 1 to bypass the Cnst at Formula.sym[0]
        for (int i = 1; i < cnt; i++) {
            if (sym[i].isVar()) {
                vH = ((Var)sym[i]).getVarHyp(hypArray);
                if (vH == null) {
                    if (((Var)sym[i]).getIsWorkVar()) {
                        vH        =
                            ((Var)sym[i]).getActiveVarHyp();
                    }
                    if (vH == null) {
                        throw new IllegalArgumentException(
                            LangConstants.
                                ERRMSG_FORMULA_VAR_HYP_NOTFND_1 +
                                 sym[i]                         +
                            LangConstants.
                                ERRMSG_FORMULA_CAPTION          +
                                 toString());
                    }
                }
                parseNodeHolderExpr[dest] =
                    new ParseNodeHolder(vH);
            }
            else { //is Cnst, so no ParseNode, must an MObj to hold...
                parseNodeHolderExpr[dest] =
                    new ParseNodeHolder((Cnst)sym[i]);
            }
            ++dest;
        }
        return parseNodeHolderExpr;
    }


    /**
     *  Builds a "rule format" version of the Formula's Expression.
     *
     *  Each Cnst in the Expression is output unchanged, while the
     *  Type Code (a Cnst) of each Var is output instead of the
     *  var -- this requires looking up the Var's VarHyp in the
     *  input varHypArray. For example, Expression "( ph -> ps )"
     *  is output as "( wff -> wff )".
     *
     *  @param varHypArray Array of VarHyp for Formula.
     *
     *  @return ruleFormatExpr in an array of Cnst.
     *
     *  @throws IllegalArgumentException if unable to find
     *          a VarHyp for one of the Formula's Var's.
     */
    public Cnst[] buildRuleFormatExpr(VarHyp[] varHypArray) {
        Cnst[]  ruleFormatExpr  = new Cnst[cnt - 1];
        int     dest            = 0;
        VarHyp  vH;

        //start at i = 1 to bypass the Cnst at Formula.sym[0]
        for (int i = 1; i < cnt; i++) {
            if (sym[i].isVar()) {
                vH = ((Var)sym[i]).getVarHyp(varHypArray);
                if (vH == null) {
                    throw new IllegalArgumentException(
                        LangConstants.ERRMSG_FORMULA_VAR_HYP_NOTFND_1 +
                             sym[i]                                   +
                        LangConstants.ERRMSG_FORMULA_CAPTION          +
                             toString());
                }
                ruleFormatExpr[dest] = vH.getTyp();
            }
            else { // not Var, must be Cnst!
                ruleFormatExpr[dest] = (Cnst)sym[i];
            }
            ++dest;
        }
        return ruleFormatExpr;
    }

    /**
     *  Uses an array of Hyps to build an array of VarHyps
     *  containing only the VarHyps needed for the variables
     *  actually used in the Formula.
     *  <p>
     *  Note: if the input array of Hyps does not contain
     *  all of the necessary VarHyps, an IllegalArgumentException
     *  is thrown! No mercy.
     *
     *  @param tempHypArray array of Hyp.
     *
     *  @return ruleFormatExpr in an array of Cnst.
     *
     *  @throws IllegalArgumentException if unable to find
     *          a VarHyp for one of the Formula's Var's.
     */
    public VarHyp[] buildMandVarHypArray(Hyp[] tempHypArray) {
        ArrayList hypList = new ArrayList();
        VarHyp    vH;
        //start at i = 1 to bypass the Cnst at Formula.sym[0]
        for (int i = 1; i < cnt; i++) {
            if (!sym[i].isVar()) {
                continue;
            }
            vH = ((Var)sym[i]).getVarHyp(tempHypArray);
            if (vH != null) {
                Formula.accumHypInList(hypList,
                                       vH);
                continue;
            }
            throw new IllegalArgumentException(
                LangConstants.ERRMSG_FORMULA_VAR_HYP_NOTFND_1 +
                     sym[i]                                   +
                LangConstants.ERRMSG_FORMULA_CAPTION          +
                     toString());
        }
        VarHyp[] out              = new VarHyp[hypList.size()];
        Iterator iterator         = hypList.iterator();
        int outIndex              = 0;
        while (iterator.hasNext()) {
            out[outIndex++]       = (VarHyp)iterator.next();
        }
        return out;
    }

    /**
     * Computes hashcode for this Formula.
     *
     * @return hashcode for the Formula
     */
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     *  Compare Expression for equality with another Formula's
     *  expr (don't compare Type Codes, in other words).
     *  <p>
     *  Equal if and only if the Sym strings are equal.
     *  and the obj to be compared to this object is not null
     *  and is a Formula as well.
     *
     *  @param obj Formula whose Expression will be compared
     *             to this Formula's Expression.
     *
     *  @return returns true if equal, otherwise false.
     */
    public boolean exprEquals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Formula)) {
            return false;
        }

        if (cnt != ((Formula)obj).cnt) {
            return false;
        }
        for (int i = 1; i < cnt; i++) {
            if (sym[i] != ((Formula)obj).sym[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     *  Compare SrcStmt typ and symList to Formula.
     *
     *  @param srcStmt from mmj.mmio.Statementizer.java.
     *
     *  @return returns true if label and symList are
     *                  identical to the Formula otherwise
     *                  false.
     */
    public boolean srcStmtEquals(SrcStmt srcStmt) {

        if (cnt != (srcStmt.symList.size() + 1)        ||
            sym[0].getId().compareTo(srcStmt.typ) != 0) {
            return false;
        }
        for (int i = 1; i < cnt; i++) {
            if (sym[i].getId().compareTo(
                (String)srcStmt.symList.get(i - 1)) != 0) {
                return false;
            }
        }
        return true;
    }


    /**
     *  Compare for equality with another Formula.
     *  <p>
     *  Equal if and only if the Sym strings are equal.
     *  and the obj to be compared to this object is not null
     *  and is a Formula as well.
     *
     *  @param obj Formula that will be compared to this Formula.
     *
     *  @return returns true if equal, otherwise false.
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Formula)) {
            return false;
        }

        if (cnt != ((Formula)obj).cnt) {
            return false;
        }
        for (int i = 0; i < cnt; i++) {
            if (sym[i] != ((Formula)obj).sym[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     *  Computes a character string version of Formula
     *  for printing.
     *  <p>
     *  Note: LogicalSystem does not
     *  validate for "printable" characters in Sym,
     *  and output of certain non-printable characters
     *  can cause abrupt termination of DOS windows
     *  (though mmj.mmio does extensive validation,
     *  LogicalSystem itself is unconcerned about the
     *  contents of Sym id strings.)
     *
     *  @return String for the Formula
     */
    public String toString() {
        StringBuffer sb = new StringBuffer(sym.length * 3);
        sb.append(sym[0].getId());
        for (int i=1; i < sym.length; i++) {
            sb.append(' ');
            sb.append(sym[i].getId());
        }
        return sb.toString();
    }

    /**
     *  Computes a character string version of the expression
     *  portion of the formula.
     *  <p>
     *  Note: the "expression" here is the 2nd thru nth
     *        symbols of the formula -- that is, the
     *        type code at the start is discarded.
     *
     *  @return String for the expression portion of Formula
     */
    public String exprToString() {
        StringBuffer sb           = new StringBuffer(sym.length * 3);
        String ws                 = "";
        for (int i=1; i < sym.length; i++) {
            sb.append(ws);
            sb.append(sym[i].getId());
            ws                    = " ";
        }
        return sb.toString();
    }


    /**
     *
     *  Formats formula symbol strings into a column of
     *  characters within a text area.
     *
     *  Weird because it does not indent on the first
     *  line, but the output can be simply "append"ed
     *  to the text area, which presumably already has
     *  data to the left of "leftColContinuation" on
     *  line 1.
     *
     *  the first line of text -- to
     *  @param  sb                  StringBuffer to append to.
     *
     *  @param  leftColContinuation the leftmost column in
     *                              the text area for use by
     *                              formulas.
     *  @param  marginRight         the rightmost column for
     *                              use by formulas.
     *  @return nbrLines used by the output formula.
     */
    public int toProofWorksheetStringBuffer(
                                StringBuffer sb,
                                int          leftColContinuation,
                                int          marginRight) {

        int          nbrLines     = 1;

        String       s;

        int          currCol  = leftColContinuation - 1;

        for (int i = 0; i < sym.length; i++) {
            s                     = sym[i].toString();
            currCol              += s.length();
            if (currCol > marginRight) {
                sb.append('\n');
                ++nbrLines;
                currCol           = leftColContinuation
                                    + sym[0].getId().length();
                for (int j = 0; j < currCol; j++) {
                    sb.append(' ');
                }
            }
            sb.append(s);
            if (currCol < marginRight) {
                sb.append(' ');
                ++currCol;
            }
        }
        return nbrLines;
    }

    /**
     *
     *  Formats formula into StringBuffer lines in a List.
     *  @param  list                list of StringBuffer lines.
     *
     *  @param  sb                  StringBuffer to append to.
     *
     *  @param  leftColContinuation the leftmost column in
     *                              the text area for use by
     *                              formulas.
     *  @param  marginRight         the rightmost column for
     *                              use by formulas.
     *  @param  endToken            string such as "$." or "$="
     *  @return final StringBuffer line in use.
     */
    public StringBuffer toStringBufferLineList(
                                LinkedList   list,
                                StringBuffer sb,
                                int          leftColContinuation,
                                int          marginRight,
                                String       endToken) {

        String       s;

        int          currCol  = leftColContinuation - 1;

        for (int i = 0; i < sym.length; i++) {
            s                     = sym[i].toString();
            currCol              += s.length();
            if (currCol > marginRight) {
                list.add(sb);
                sb                = new StringBuffer(marginRight);
                currCol           = leftColContinuation
                                    + sym[0].getId().length();
                for (int j = 0; j < currCol; j++) {
                    sb.append(' ');
                }
            }
            sb.append(s);
            if (currCol < marginRight) {
                sb.append(' ');
                ++currCol;
            }
        }
        if (endToken != null) {
            if (currCol + endToken.length() > marginRight) {
                list.add(sb);
                sb                = new StringBuffer(marginRight);
            }
            sb.append(endToken);
        }
        return sb;
    }

    /**
     * Accumulate unique hypotheses (no duplicates), storing them
     * in an array list in order of their appearance in the database.
     *
     * @param hypList  -- ArrayList of Hyp's. Is updated with
     *        unique variable hypotheses in the expression.
     *        Because the list is maintained in database statement
     *        sequence order, hypList should either be empty (new)
     *        before the call, or already be in that order
     *        (see <code>accumHypInList</code>.
     *
     * @param hypNew candidate Hyp to be added to hypList if
     *               not already there.
     */
    public static void accumHypInList(ArrayList hypList,
                                      Hyp       hypNew) {
        int i           = 0;
        int iEnd        = hypList.size();
        int newSeq      = hypNew.seq;
        int existingSeq;

        while (true) {
            if (i < iEnd) {
                existingSeq = ((Hyp)hypList.get(i)).seq;
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
        hypList.add(i, hypNew);
        return;
    }

    /**
     *  Computes the width in characters of the widest Cnst
     *  in the Formula's Expression.
     *
     *  @return width in characters of the widest Cnst
     *          in the Formula's Expression -- or
     *          -1 if the Expression is null or if it
     *          contains no constants.
     */
    public int computeWidthOfWidestExprCnst() {
        int max                   = -1;
        int len;
        for (int i = 1; i < cnt; i++) {
            if (sym[i].isCnst()) {
                len               =
                    sym[i].getId().length();
                if (len > max) {
                    max           = len;
                }
            }
        }
        return max;
    }
}
