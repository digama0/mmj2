//********************************************************************/
//* Copyright (C) 2008                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * StepSelectorResults.java  0.01 03/01/2008
 *
 * Version 0.01:
 * ==> New.
 */

package mmj.pa;

import mmj.lang.Assrt;

/**
 * StepSelectorResults contains the StepSelector search results and is passed to
 * the ProofAsstGUI for use in generating the StepSelectorDialog.
 */
public class StepSelectorResults {

    /* friendly */String step;
    /* friendly */Assrt[] refArray;
    /* friendly */String[] selectionArray;

    /**
     * Constructor for StepSelectorResults
     * 
     * @param step for which the Step Selector was run.
     * @param refArray parallel array of the candidates plus a null entry at the
     *            end of the array.
     * @param selectionArray preformatted strings for the dialog display
     *            including a final item containing either "***MORE***" or
     *            "***END***".
     */
    public StepSelectorResults(final String step, final Assrt[] refArray,
        final String[] selectionArray)
    {

        this.step = step;
        this.refArray = refArray;
        this.selectionArray = selectionArray;
    }
}
