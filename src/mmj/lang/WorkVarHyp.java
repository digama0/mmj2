//********************************************************************/
//* Copyright (C) 2007                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * WorkVarHyp.java  0.01 08/01/2007
 *
 * Aug-1-2007:
 *     --> new
 */

package mmj.lang;

public class WorkVarHyp extends VarHyp {

    /**
     * Index into WorkVarManager index of Work Variables for a given Type Code.
     * Now it is never used
     */
    int workVarIndex;

    /**
     * Construct WorkVarHyp using precomputed values and doing no validation.
     *
     * @param workVarHypSeq MObj.seq
     * @param workVarHypLabel Stmt.label
     * @param workVarHypFormula Stmt.formula
     * @param workVarIndex Index into WorkVarManager
     */
    public WorkVarHyp(final int workVarHypSeq, final String workVarHypLabel,
        final Formula workVarHypFormula, final int workVarIndex)
    {
        super(workVarHypSeq, workVarHypLabel, workVarHypFormula);
        this.workVarIndex = workVarIndex;
    }

    public WorkVar getWorkVar() {
        return (WorkVar)getVar();
    }
}
