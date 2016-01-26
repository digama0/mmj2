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
 * ProofAsstGUI for use in generating the StepSelectorDialog. It is modeled
 * after an enum, although it is not because one of the possible values has
 * extra parameters.
 */
public class StepRequest {

    public static final StepRequest SelectorSearch = new StepRequest(true);

    public static final StepRequest StepSearch = new StepRequest(true);

    public static final StepRequest GeneralSearch = new StepRequest(false);

    public static final StepRequest SearchOptions = new StepRequest(true);

    public static final StepRequest StepSearchChoice = new StepRequest(false);

    public boolean simple;
    public String step;
    public Object param1;

    public static class SelectorChoice extends StepRequest {

        /**
         * Constructor for StepRequest
         *
         * @param step for which the Step Selector was run.
         * @param param1 parameter depending on code.
         */
        public SelectorChoice(final String step, final Object param1) {
            super(false);
            this.step = step;
            this.param1 = param1;
        }
    }

    private StepRequest(final boolean simple) {
        this.simple = simple;
    }
}
