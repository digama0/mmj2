//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  SubstMapEntry.java  0.02 08/26/2005
 */

package mmj.verify;

import mmj.lang.*;

/**
 *  SubstMapEntry is a data structure used in an array in
 *  VerifyProofs for making variable substitutions.
 *  <p>
 *  Warning: the nomenclature is odd -- "substFrom" refers
 *  to the value that will be changed *from* one thing
 *  to "substTo". For example, change the value of all
 *  "x"s from "x" to "y".
 *  <p>
 *  Perhaps this *should be* an inner class of VerifyProofs,
 *  but...
 *  <p>
 *  Example: SubstMapEntry[] subst -->
 *  <p>
 *  <code>
 *      Sym substFrom: variable from proof step mandatory<br>
 *                     hypotheses<br><br>
 *      Sym[] substTo: expression/variable to substitute<br>
 *                     FOR each occurrence of substFrom<br>
 *                     in the proof step's mandatory<br>
 *                     hypotheses and assertion.<br>
 *  </code>
 *
 */
public class SubstMapEntry {

    /**
     * substFrom -- a Var to be replaced by an expression
     *              or variable.
     */
    public    Sym       substFrom;

    /**
     *  substTo -- an expression or variable to replace substFrom.
     */
    public    Sym[]     substTo;

    /**
     *  Default Constructor.
     */
    public SubstMapEntry() {
    }

    /**
     *  Construct using substFrom and substTo.
     *
     *  @param substFrom variable to be replaced.
     *
     *  @param substTo   expression or variable to replace substFrom.
     */
    public SubstMapEntry(Sym   substFrom,
                         Sym[] substTo) {
        this.substFrom = substFrom;
        this.substTo   = substTo;
    }
}
