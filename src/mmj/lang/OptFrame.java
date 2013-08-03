//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  OptFrame.java  0.02 08/23/2005
 */

package mmj.lang;

/**
 *  Optional "Frame" of a Theorem (OptFrame not present
 *  in other Assrt's).
 *  <p>
 *  Add MandFrame to OptFrame and you have a Metamath
 *  "Extended Frame".
 *
 *  @see <a href="../../MetamathERNotes.html">
 *       Nomenclature and Entity-Relationship Notes</a>
 */
public class OptFrame {

    /**
     *  FIRST DRAFT :) -->
     *  "optHypArray" includes all of the variable hypotheses
     *  in scope of a theorem that are not present in "hypArray"
     *  (above).
     */
    public Hyp[]     optHypArray;

    /**
     *  FIRST DRAFT :) -->
     *  "optDjVarsArray" includes all of DjVars (pairs) in scope
     *  which are not present in djVarsArray (above).
     */
    public DjVars[]  optDjVarsArray;

    /**
     *  Checks to see if a certain pair of variables is
     *  mentioned in a OptFrame's DjVars array.
     *  <p>
     *  Note: vLo and vHi are automatically switched
     *        if vLo is not low.
     *  <p>
     *  Note: this function is the reason that DjVars vars
     *        are stored as "lo" and "hi". Low and High
     *        are irrelevant to the mathematics of this
     *        situation. But if the vars were stored
     *        randomly or arbitrarily, then twice as
     *        many comparisons would be needed here.
     *
     *  @param frame Optional Frame to inspect.
     *  @param vLo   the "low" variable in the pair.
     *  @param vHi   the "high" variable in the pair.
     *
     *  @return true if param var pair in OptFrame DjVars array.
     */
    public static boolean isVarPairInDjArray(OptFrame frame,
                                             Var vLo,
                                             Var vHi) {
        Var vSwap;
        if ((vLo.compareTo(vHi)) > 0) {
            vSwap = vHi;
            vHi = vLo;
            vLo = vSwap;
        }
        for (int i = 0; i < frame.optDjVarsArray.length; i++) {
            if (frame.optDjVarsArray[i].varLo  == vLo &&
                frame.optDjVarsArray[i].varHi == vHi) {
                return true;
            }
        }
        return false;
    }
}
