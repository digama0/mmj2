//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * VarHypSubst.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.lang;

public class VarHypSubst {
    public static final VarHypSubst END_OF_LIST = new VarHypSubst(null, null);

    public VarHyp targetVarHyp;
    public ParseNode sourceNode;

    public VarHypSubst(final VarHyp target, final ParseNode source) {
        targetVarHyp = target;
        sourceNode = source;
    }
}
