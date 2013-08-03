//********************************************************************/
//* Copyright (C) 2008                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * StepRequest.java  0.01 03/01/2008
 *
 * Version 0.01:
 * ==> New.
 */

package mmj.pa;

/**
 * StepRequest contains the StepSelector search results and is passed to the
 * ProofAsstGUI for use in generating the StepSelectorDialog.
 */
public class StepRequest {

    /* friendly */int request;
    /* friendly */String step;
    /* friendly */Object param1;

    /**
     * Constructor for StepRequest
     * 
     * @param request code
     * @param step for which the Step Selector was run.
     * @param param1 parameter depending on code.
     */
    public StepRequest(final int request, final String step, final Object param1)
    {

        this.request = request;
        this.step = step;
        this.param1 = param1;
    }
    /**
     * Constructor for StepRequest
     * 
     * @param request code
     */
    public StepRequest(final int request) {

        this(request, null, null);
    }

}
