//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * Assrt.java  0.08 08/01/2008
 *
 *  Sep-30-2005: change getMandHypArray() to
 *               getMandHypArrayLength().
 *
 *  Dec-14-2005: - add new (redundant) logHypArray
 *                 for the convenience of ProofAsst.
 *               - made loadVarHypArray, loadHypArray,
 *                 loadDjVarsArray and accumHypInList
 *                 static for use in ProofAsst.
 *               - added isLogHyp()
 *               - added isAxiom()
 *               - added getLogHypsMaxDepth (see
 *                 associated code in Stmt.java)
 *
 *  Version 0.04 Oct-12-2006:
 *               - added SymTbl to constructor and modified to
 *                 conform to Metamath.pdf spec change of 6-24-2006
 *                 prohibiting Stmt label and Sym id namespace
 *                 collisions.
 *
 *  Version 0.05 Jun-01-2007
 *               - move loadDjVarsArray to DjVars class.
 *
 *  Version 0.06 - 08/01/2007
 *               - Misc Work Var Enhancements.
 *
 *  Version 0.07 - 03/01/2008
 *               - Additions for mmj.pa.StepSelectorSearch.java:
 *                     Assrt.sortListIntoArray() and
 *                     Assrt.NBR_LOG_HYP_SEQ
 *               - Added sortedLogHypArray,
 *                       loadSortedLogHypArray()
 *                   and hypVarsInCommonWithAssrt().
 *                  Plus getSortedLogHypArray(),
 *                   and modified loadLogHypArray() to call
 *                       loadSortedLogHypArray() during
 *                       object construction.
 *
 *  Version 0.08 - 08/01/2008
 */

package mmj.lang;

import java.util.*;

/**
 *  Assrt is an "Assertion", of which there are two
 *  main kinds, Axiom and Theorem -- known in
 *  Metamath as "Axiomatic Assertions" and "Provable
 *  Assertions". What do Assertions have in common that
 *  Hypothotheses do not? "Mandatory" Frames.
 *
 *  @see <a href="../../MetamathERNotes.html">
 *       Nomenclature and Entity-Relationship Notes</a>
 */
public abstract class Assrt extends Stmt {

    /**
     *  varHypArray contains *exactly* the Assrt's Formula's
     *  VarHyp's, in database sequence.
     *  <p>
     *  varHypArray is an array of length zero if the Assrt's
     *  Formula contains no variables.
     *  <p>
     *  Note: varHypArray is somewhat redundant, but
     *  note that MandFrame contains not only VarHyp's used
     *  in an Expression's Formula, but the LogHyp's that
     *  are in scope, <b>and the VarHyp's used by those
     *  LogHyp's!</b>.
     *  <p>
     */
    protected VarHyp[]  varHypArray;

    /**
     *  logHypArray contains *exactly* the Assrt's Formula's
     *  LogHyp's, in database sequence.
     *  <p>
     *  logHypArray is an array of length zero if the Assrt
     *  has no LogHyp's.
     *  <p>
     *  logHypArray is redundant with the data in MandFrame,
     *  but ProofAsst needs quick access to the LogHyp's as
     *  well as the actual number of LogHyp's.
     *  <p>
     */
    protected LogHyp[]  logHypArray;

    /**
     *  sortedLogHypArray contains the contents of logHypArray
     *  as sorted by loadSortedLogHypArray().
     *  <p>
     *  The sorted LogHyps are for the benefit of ProofAssistant.
     */
    protected LogHyp[]  sortedLogHypArray;

    /**
     *  The assertion's Mandatory Frame.
     */
    protected MandFrame mandFrame;

    /**
     *  Construct using a boatload of parameters.
     *
     *  @param seq           MObj.seq number
     *  @param scopeDefList  the Scope list
     *  @param symTbl        Symbol Table (Map)
     *  @param stmtTbl       Statement Table (Map)
     *  @param labelS        Label String identifying the Assrt
     *  @param typS          Type Code String (first sym of formula)
     *  @param symList       Formula's Expression (2nd thru nth syms)
     *
     *  @throws LangException variety of errors :)
     */
    public Assrt(int       seq,
                 ArrayList scopeDefList,
                 Map       symTbl,
                 Map       stmtTbl,
                 String    labelS,
                 String    typS,
                 ArrayList symList)
                                throws LangException {
        super (seq,
               symTbl,
               stmtTbl,
               labelS);

        ArrayList exprHypList = new ArrayList();
        formula               = new LogicFormula(symTbl,
                                                 typS,
                                                 symList,
                                                 exprHypList);

        varHypArray           = Assrt.loadVarHypArray(exprHypList);

        mandFrame             = buildMandFrame(scopeDefList,
                                               exprHypList);

        loadLogHypArray();
    }

    /**
     *  Return the *mandatory* varHypArray.
     *  <p>
     *  Note: the "varHypArray" contains only *mandatory*
     *        VarHyp's, hence the name of this function,
     *        which is intended to highlight the point.
     *        These are the VarHyp's use in the Assrt's
     *        Formula.
     *
     *  @return varHypArray which contains only mandatory
     *        VarHyp's.
     */
    public VarHyp[] getMandVarHypArray() {
        return varHypArray;
    }

    /**
     *  Return the logHypArray.
     *  <p>
     *  @return logHypArray for the Assrt.
     */
    public LogHyp[] getLogHypArray() {
        return logHypArray;
    }

    /**
     *  Return the logHypArray length
     *  <p>
     *  @return Assrt's logHypArray length.
     */
    public int getLogHypArrayLength() {
        return logHypArray.length;
    }

    /**
     *  Set the logHypArray.
     *  <p>
     *  @param logHypArray for the Assrt.
     */
    public void setLogHypArray(LogHyp[] logHypArray) {
        this.logHypArray = logHypArray;
    }


    /**
     *  Return the *mandatory* Hyp Array.Length
     *
     *  @return hypArray length from the Assrt's MandFrame.
     */
    public int getMandHypArrayLength() {
        return mandFrame.hypArray.length;
    }


    /**
     *  Set the *mandatory* VarHyp Array.
     *
     *  @param varHypArray VarHyp's used by the Assrt.
     */
    public void setMandVarHypArray(VarHyp[] varHypArray) {
        this.varHypArray    = varHypArray;
    }

    /**
     *  Get the Assrt's MandFrame.
     *
     *  @return Assrt's MandFrame.
     */
    public MandFrame getMandFrame() {
        return mandFrame;
    }

    /**
     *  Set the Assrt's MandFrame.
     *
     *  @param mandFrame  Assrt's MandFrame.
     */
    public void setMandFrame(MandFrame mandFrame) {
        this.mandFrame = mandFrame;
    }

    /**
     *  Is the Assrt "active".
     *  <p>
     *  Yep. Always. An assertion is always "active"
     *  even if it is defined inside a scope level.
     *  (Recall that, in practice, "active" simply means
     *  that a statement can be referred to by a
     *  subsequent statement in a proof or parse RPN.)
     *  <p>
     *  That is because Metmath "scope" is designed
     *  primarily to limit the visibility of hypotheses
     *  so that variables can be reused with different
     *  Types and so that logical hypotheses can be
     *  applied to just a single assertion. In a sense,
     *  Metamath scopes are merely notational shorthand,
     *  and as Metamath.pdf explains, every assertion
     *  has an "Extended Frame" (look it up for more
     *  info.)
     *
     *  @return true (assertions are always "active")
     */
    public boolean isActive() {
        // assertions are always active at global scope.
        return true;
    }

    /**
     *  Is the Stmt an Assrt?
     *  <p>
     *  Someone asked the question, so it is answered...
     *
     *  @return true (an Assrt is an Assrt :)
     */
    public boolean isAssrt() {
        return true;
    }

    /**
     *  Is the Assrt an Axiom?
     *  <p>
     *  Someone asked the question, so it is answered...
     *
     *  @return true (an Assrt is an Assrt :)
     */
    public abstract boolean isAxiom();


    /**
     *  Is the Stmt an Hyp?
     *  <p>
     *  Someone asked the question, so it is answered...
     *
     *  @return false (an Assrt is not an Hyp :)
     */
    public boolean isHyp() {
        return false;
    }

    /**
     *  Is the Stmt a VarHyp?
     *  <p>
     *  Someone asked the question, so it is answered...
     *
     *  @return false (an Assrt is not a Hyp, ergo it is not
     *                a VarHyp :)
     */
    public boolean isVarHyp() {
        return false;
    }

    /**
     *  Is the Stmt a WorkVarHyp?
     *  <p>
     *  Someone asked the question, so it is answered...
     *
     *  @return false (an Assrt is not a Hyp, ergo it is not
     *                a WorkVarHyp :)
     */
    public boolean isWorkVarHyp() {
        return false;
    }


    /**
     *  Is the Stmt a LogHyp?
     *  <p>
     *  Someone asked the question, so it is answered...
     *
     *  @return false (an Assrt is not a Hyp, ergo it is not
     *                a LogHyp :)
     */
    public boolean isLogHyp() {
        return false;
    }


    /**
     *  Loads the logHypArray using mandFrame.
     *  <p>
     *  This is pretty redundant, but is an add-on
     *  for ProofAsst and is designed to be as
     *  bombproof as possible -- the output logHypArray
     *  is guaranteed to be in database sequence
     *  because it is created from MandFrame.hypArray.
     */
    public void loadLogHypArray() {

        Hyp[]  hyp                = mandFrame.hypArray;

        int    logHypCount        = 0;
        for (int i = 0; i < hyp.length; i++) {
            if (hyp[i].isLogHyp()) {
                ++logHypCount;
            }
        }
        logHypArray               = new LogHyp[logHypCount];

        logHypCount               = 0;
        for (int i = 0; i < hyp.length; i++) {
            if (hyp[i].isLogHyp()) {
                logHypArray[logHypCount++]
                                  = (LogHyp)hyp[i];
            }
        }

        loadSortedLogHypArray();
    }




    /**
     *  Load the Assrt's MandFrame.
     *  <p>
     *  <ol>
     *     <li>add the variable hypotheses referenced in the logical
     *         hypotheses to the variable hypotheses list in the
     *         hypotheses list (input contains var hyps used in
     *         assertion's expression.)
     *     <li>add the logical hypotheses that are "in scope" to
     *         the the hypotheses list
     *     <li>add the DjVars that match variable hypotheses (pairs)
     *         in the hypotheses list.
     *
     *     <li>convert lists to arrays
     *     <li>return with the goodies.
     *  </ol>
     *  <p>
     *  NOTE 3: "ddeeq1" in set.mm requires disjoint variables on
     *          w and x, but w is not referenced directly in
     *          ddeeq1; rather, it employs w in the proof.
     *          DjVars that do not match variable hypotheses are
     *          therefore added to the OptFrame for use in proof
     *          verification!
     *          @see mmj.lang.VerifyProofs#checkSubstToVars()
     *
     *  @param scopeDefList  Scope List as of this Stmt's definition.
     *  @param hypList       Already partly filled in with variable
     *                       hypotheses from the assertion's
     *                       expression.
     *
     * @return MandFrame     Metamath ("mandatory") Frame for the
     *                       Assrt.
     *
     */
    private MandFrame buildMandFrame(ArrayList scopeDefList,
                                     ArrayList hypList) {


        ListIterator    scopeIterator;
        ScopeDef        scopeDef;

        LogHyp          logHyp;
        VarHyp[]        varHypArray;

        scopeIterator = scopeDefList.listIterator();
        while (scopeIterator.hasNext()) {
            scopeDef = (ScopeDef)scopeIterator.next();

            for (int i = 0; i < scopeDef.scopeLogHyp.size(); i++) {
                logHyp = (LogHyp)scopeDef.scopeLogHyp.get(i);
                varHypArray = logHyp.getMandVarHypArray();

                for (int j = 0; j < varHypArray.length; j++) {
                    Assrt.accumHypInList(hypList,
                                         varHypArray[j]);
                }

                Assrt.accumHypInList(hypList,
                                     logHyp);
            }
        }

        ListIterator    djVarsIterator;
        ArrayList       djVarsList = new ArrayList();
        DjVars          djVars;

        scopeIterator = scopeDefList.listIterator();
        while (scopeIterator.hasNext()) {
            scopeDef = (ScopeDef)scopeIterator.next();

            djVarsIterator = scopeDef.scopeDjVars.listIterator();
            while (djVarsIterator.hasNext()) {

                djVars = (DjVars)djVarsIterator.next();
                if (
                    (areBothDjVarsInHypList(hypList,
                                            djVars))
                    &&
                    (!djVarsList.contains(djVars))
                   ) {
                    djVarsList.add(djVars);
                }
            }
        }

        MandFrame mF        = new MandFrame();

        mF.hypArray         = Assrt.loadHypArray(hypList);
        mF.djVarsArray      = DjVars.loadDjVarsArray(djVarsList);

        return mF;
    }


    /**
     *  Checks to see whether or not both variables in a DjVars
     *  pair are referenced in a list of hypotheses.
     *  <p>
     *  Note: checks only the VarHyp's.
     *  <p>
     * @param hypList  -- ArrayList containing hypotheses
     *
     * @param djVars -- DjVars object containing 2 variables to
     *                  be checked against the variables referenced
     *                  in hypList.
     *
     * @return boolean -- true if both DjVars variables are present
     *                    in hypList, otherwise false.
     */
    private boolean areBothDjVarsInHypList(ArrayList hypList,
                                           DjVars    djVars) {
        boolean loFound = false;
        boolean hiFound = false;
        Hyp     hyp;
        for (int i = 0; i < hypList.size(); i++) {
            hyp = (Hyp)hypList.get(i);
            if (hyp.isVarHyp()) {
                if (((VarHyp)hyp).getVar() == djVars.varLo) {
                    loFound = true;
                }
                if (((VarHyp)hyp).getVar() == djVars.varHi) {
                    hiFound = true;
                }
                if (loFound && hiFound) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     *  Accumulate unique hypotheses (no duplicates), storing
     *  them in an array list in order of their appearance
     *  in the database.
     *  <p>
     *  The input "hypList" is updated with unique variable
     *  hypotheses in the expression.
     *  <p>
     *  Because hypList is maintained in database statement
     *  sequence order, hypList should either be empty (new)
     *  before the call, or already be in that order.
     *
     *  @param hypList  ArrayList of Hyp's, updated here.
     *
     *  @param hypNew  Candidate Hyp to be added to hypList if
     *                 not already there.
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
     *  Copies hypList ArrayList to a Hyp Array.
     *  <p>
     *  This was codes because i couldn't get
     *  ArrayList.toArray() to compile. Doh!
     *
     *  @param hypList  ArrayList of hypotheses to copy.
     *
     *  @return Array of Hyp's copied from hyplist.
     */
    public static Hyp[] loadHypArray(ArrayList hypList) {
        Hyp[] hypArray = new Hyp[hypList.size()];
        for (int i=0; i < hypArray.length; i++) {
            hypArray[i] = (Hyp)hypList.get(i);
        }
        return hypArray;
    }

    /**
     *  Copies VarHyp ArrayList to a VarHyp Array.
     *  <p>
     *  This was codes because i couldn't get
     *  ArrayList.toArray() to compile. Doh!
     *
     *  @param  hypList  ArrayList of VarHyp to copy
     *
     *  @return Array of VarHyp copied from hyplist.
     */
    public static VarHyp[] loadVarHypArray(ArrayList hypList) {
        VarHyp[] varHypArray = new VarHyp[hypList.size()];
        for (int i=0; i < varHypArray.length; i++) {
            varHypArray[i] = (VarHyp)hypList.get(i);
        }
        return varHypArray;
    }

    /**
     *  Dynamically computes, if needed, the larges maximum
     *  depth of parse trees of logHypArray, and caches the
     *  value for later use.
     *
     *  @return greatest maxDepth of parse trees for logHypArray.
     */
    public int getLogHypsMaxDepth() {
        if (logHypsMaxDepth == -1) {
            int hypMaxDepth = 0;
            int hypDepth;
            for (int i = 0; i < logHypArray.length; i++) {
                if ((hypDepth =
                        logHypArray[i].getExprParseTree(
                            ).getMaxDepth())
                    > hypMaxDepth) {
                    hypMaxDepth = hypDepth;
                }
            }
            setLogHypsMaxDepth(hypMaxDepth);
        }

        return logHypsMaxDepth;
    }

    /**
     *  Dynamically computes, if needed, the Hi and Lo keys
     *  of Level 1 (root) of parse trees of logHypArray, and
     *  caches the value for later use.
     *
     *  @return Level 1 HiLoKey of parse trees for logHypArray.
     */
    public String getLogHypsL1HiLoKey() {
        if (logHypsL1HiLoKey == null) {
            if (logHypArray.length > 0) {
                Stmt   hStmt;
                int    n;
                String low        = null;
                String high       = null;
                int    lowNbr     = Integer.MAX_VALUE;
                int    highNbr    = Integer.MIN_VALUE;
                for (int i = 0; i < logHypArray.length; i++) {
                    hStmt         =
                        logHypArray[i].getExprParseTree(
                            ).getRoot(
                                ).getStmt();
                    if (hStmt.isVarHyp()) {
                        setLogHypsL1HiLoKey("");
                        return logHypsL1HiLoKey;
                    }
                    n = hStmt.getSeq();
                    if (n < lowNbr) {
                        lowNbr    = n;
                        low       = hStmt.getLabel();
                    }
                    if (n > highNbr) {
                        highNbr   = n;
                        high      = hStmt.getLabel();
                    }
                }
                setLogHypsL1HiLoKey(
                    new String(high + " " + low));
            }
            else {
                setLogHypsL1HiLoKey("");
            }
        }
        return logHypsL1HiLoKey;
    }

//not needed in StepSelectorSearch anymore, so comment out for now
//  /**
//   *  Sorts a list of Assrt into an array.
//   *
//   *  @param assrtList List of Assrt to be sorted.
//   *  @param comparator Comparator to be used for the sort.
//   *  @return Array of Assrt with size equal to the number
//   *                of elements in the input list.
//   */
//  public static Assrt[] sortListIntoArray(
//                              List       assrtList,
//                              Comparator comparator) {
//
//      Assrt[]  assrtArray       = new Assrt[assrtList.size()];
//      Iterator i                = assrtList.iterator();
//      int      cnt              = 0;
//      while (i.hasNext()) {
//          assrtArray[cnt++]     = (Assrt)i.next();
//      }
//
//      Arrays.sort(assrtArray,
//                  comparator);
//
//      return assrtArray;
//  }
//

    /**
     *  NBR_LOG_HYP_SEQ sequences by Stmt.seq
     */
    static public final Comparator NBR_LOG_HYP_SEQ
            = new Comparator() {
        public int compare(Object o1, Object o2) {
            int n                 =
                ((Assrt)o1).logHypArray.length -
                ((Assrt)o2).logHypArray.length;
            if (n == 0) {
                n                 =
                ((Assrt)o1).seq -
                ((Assrt)o2).seq;
            }
            return n;
        }
    };

    /**
     *  Loads sortedLogHypArray from logHypArray.
     *  <p>
     *  The purpose of this is to finalize the state
     *  of memory concerning the loaded database and
     *  to get this sorting process completed for
     *  Proof Assistanting.
     *  <p>
     *  Sort in descending order of formula length and
     *  if two hyps have the same length and the new
     *  one has variables in common with its assertion's
     *  formula's variables, put it first in the output
     *  (variables in common are less likely to lead
     *  to a "false" set of unifications -- the
     *  inconsistencies are caught quicker, in other words.)
     */
    private void loadSortedLogHypArray() {

        LogHyp[]  outArray        =
                            new LogHyp[logHypArray.length];

        int       outEnd;
        int       outIndex;
        int       iFormulaLength;
        int       diff;
        LogHyp    holdLogHyp1;

        iLoop: for (int i = 0; i < logHypArray.length; i++) {
            outEnd                = i;
            holdLogHyp1           = logHypArray[i];
            iFormulaLength        =
                holdLogHyp1.getFormula().getCnt();
            outIndex              = 0;
            outLoop: while (true) {
                if (outIndex >= outEnd) {
                    outArray[outEnd]
                                  = holdLogHyp1;
                    continue iLoop;
                }
                diff              =
                    outArray[outIndex].getFormula().getCnt()
                    - iFormulaLength;
                if (diff > 0) {
                    ++outIndex;
                    continue outLoop; //look for shorter formula
                }
                if (diff < 0  ||
                    hypVarsInCommonWithAssrt(holdLogHyp1)) {
                    break outLoop; //insert here at outIndex
                }
                equalLoop: while (true) {
                    ++outIndex; //find formula with diff length
                    if (outIndex < outEnd) {
                        if (outArray[outIndex].getFormula().getCnt()
                            ==
                            iFormulaLength) {
                            continue equalLoop;
                        }
                    }
                    break outLoop; //insert here at outIndex
                }
            }
            /*
             * end of outLoop: insert here at outIndex, which means
             * shifting whatever is here downwards by one.
             */
            for (int k = outEnd; k > outIndex; k--) {
                outArray[k]       = outArray[k - 1];
            }
            outArray[outIndex]
                                  = holdLogHyp1;
        }

        sortedLogHypArray         = outArray; //whew!
    }

    /**
     *  See if the LogHyp has any variables in common with
     *  the assertion.
     *
     *  Note: both of the VarHyp arrays are sorted in database
     *        order (*.getSeq());
     */
    private boolean hypVarsInCommonWithAssrt(LogHyp holdLogHyp1) {
        VarHyp[] h                =
                    holdLogHyp1.getMandVarHypArray();

        if (varHypArray.length == 0 ||
            h.length           == 0) {
            return false;
        }

        int iA                    = 0;
        int iH                    = 0;

        while (true) {
            if (h[iH] == varHypArray[iA]) {
                return true;
            }
            if (h[iH].getSeq() < varHypArray[iA].getSeq()) {
                if (++iH < h.length) {
                    continue;
                }
                break;
            }
            if (++iA < varHypArray.length) {
                continue;
            }
            break;
        }

        return false;
    }

    /**
     *  Return the sortedLogHypArray.
     *  <p>
     *  @return sortedLogHypArray for the Assrt.
     */
    public LogHyp[] getSortedLogHypArray() {
        return sortedLogHypArray;
    }
}
