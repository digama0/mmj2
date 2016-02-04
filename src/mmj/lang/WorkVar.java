//********************************************************************/
//* Copyright (C) 2007                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * WorkVar.java  0.01 08/01/2007
 *
 * Aug-1-2007:
 *     --> new
 */

package mmj.lang;

public class WorkVar extends Var {

    /**
     * Index into WorkVarManager index of Work Variables for a given Type Code.
     */
    int workVarIndex;

    /**
     * Construct using sequence number and id string.
     *
     * @param seq MObj.seq number
     * @param id Sym id string
     * @param workVarIndex WorkVarManager Work Var Index value
     * @throws LangException if id is empty
     */
    public WorkVar(final int seq, final String id, final int workVarIndex)
        throws LangException
    {
        super(seq, id, true); // true = "active"
        this.workVarIndex = workVarIndex;
    }
}
