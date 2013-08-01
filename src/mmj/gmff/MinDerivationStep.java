//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/**
 *  MinDerivationStep.java  0.01 11/01/2011
 *
 *  Version 0.01:
 *  Nov-01-2011: new.
 */

package mmj.gmff;

/**
 *  Derivation step on a <code>MinProofWorksheet</code>.
 *  <p>
 *  Note: at this time GMFF does not need to know whether
 *        a proof step is an hypothesis or a derivation
 *        step, but <code>MinHypothesisStep</code> and
 *        <code>MinDerivationStep</code> are provided for
 *        completeness and future possibilities :-)
 */
public class MinDerivationStep extends MinProofStepStmt {

    /**
     *  Standard MinDerivationStep constructor.
     *  <p>
     *  @param w the <code>MinProofworksheet</code> object which
     *				contains the proof step.
     *  @param slc Array of proof step lines each comprised
     *				of an Array of <code>String</code>s
     *              called "chunks", which may be either Metamath
     *              whitespace or Metamath tokens. Hence the
     *              acronym "slc" refers to Statement Line Chunks.
     */
    public MinDerivationStep(final MinProofWorksheet w, final String[][] slc) {

        super(w, slc);
    }
}
