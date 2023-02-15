//********************************************************************/
//* Copyright (C) 2007  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * UnifySubst.java  0.01 08/01/2007
 *
 * Version 0.01 08/01/2007
 *              - new.
 */

package mmj.pa;

import mmj.lang.ParseNode;
import mmj.lang.VarHyp;

/**
 * UnifySubst is a data structure for use in proof unification.
 * <p>
 */
public class UnifySubst {

    /* friendly */VarHyp fromHyp;

    /* friendly */ParseNode toNode;

    /* friendly */UnifySubst next;

    /* friendly */boolean generatedDuringAccum;

    public static UnifySubst IMPOSSIBLE = new UnifySubst();
    public static UnifySubst EMPTY_LIST = new UnifySubst();

    public UnifySubst() {}

    public UnifySubst(final VarHyp fromHyp, final ParseNode toNode) {
        this.fromHyp = fromHyp;
        this.toNode = toNode;
    }

    public UnifySubst(final VarHyp fromHyp, final ParseNode toNode,
        final boolean generatedDuringAccum)
    {
        this.fromHyp = fromHyp;
        this.toNode = toNode;
        this.generatedDuringAccum = generatedDuringAccum;
    }

    public UnifySubst insert(final UnifySubst last) {
        next = null;
        if (last != null)
            last.next = this;
        return this;
    }
}
