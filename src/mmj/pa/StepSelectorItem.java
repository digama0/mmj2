//********************************************************************/
//* Copyright (C) 2008                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * StepSelectorItem.java  0.01 03/01/2008
 *
 * Version 0.01:
 * ==> New.
 */

package mmj.pa;

import mmj.lang.Assrt;

/**
 * StepSelectorItem contains a single result obtained from the StepSelector
 * search.
 */
public class StepSelectorItem {

    /* friendly */Assrt assrt;
    /* friendly */String[] selection;

    /**
     * Constructor for StepSelectorItem
     * 
     * @param assrt Assrt matched by the StepSelector search and may be null.
     * @param selection formula or message String to be displayed by
     *            StepSelectorDialog for a single assertion.
     */
    public StepSelectorItem(final Assrt assrt, final String[] selection) {
        this.assrt = assrt;
        this.selection = selection;
    }
}
