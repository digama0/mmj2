//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/**
 *  DistinctVariablesStmt.java  0.07 08/01/2008
 *  <code>
 *  Version 0.04:
 *      - Un-nested inner class
 *
 *  Nov-01-2007 Version 0.05
 *  - add abstract method computeFieldIdCol(int fieldId)
 *    for use in ProofAsstGUI (just in time) cursor
 *    positioning logic.
 *
 *  Feb-01-2008 Version 0.06
 *  - add tmffReformat().
 *
 *  Aug-01-2008 Version 0.07
 *  - add validation that $d variables are defined
 *    in the theorem's extended "combo" frame.
 *  </code>
 *
 */

package mmj.pa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mmj.lang.Sym;
import mmj.lang.Var;
import mmj.mmio.MMIOError;

/**
 *  DistinctVariablesStmt represents a single Metamath
 *  $d statement in a ProofWorksheet.
 *  <p>
 *  A proof may contain 0 -> n DistinctVariablesStmt's.
 *  They are used during Unification as *additions* to
 *  whatever $d statements are contained in the database
 *  for the theorem being proved.
 */
public class DistinctVariablesStmt extends ProofWorkStmt {
    private Var[] dv;

    /**
     *  Default Constructor.
     *  <p>
     *  @param w ProofWorksheet object
     */
    public DistinctVariablesStmt(final ProofWorksheet w) {
        super(w);
    }

    /**
     *  Constructor with a group of distinct variables
     *  as input.
     *  <p>
     *  @param w ProofWorksheet object
     *  @param dvGroup List of Var which are distinct.
     */
    public DistinctVariablesStmt(final ProofWorksheet w, final List<Var> dvGroup)
    {
        super(w);

        stmtText = new StringBuilder(dvGroup.size() * 4); // guess

        stmtText.append(PaConstants.DISTINCT_VARIABLES_STMT_TOKEN);
        stmtText.append(' ');

        dv = new Var[dvGroup.size()];
        int dvCnt = 0;
        for (final Var v : dvGroup) {
            dv[dvCnt++] = v;
            stmtText.append(v.toString());
            stmtText.append(' ');
        }

        stmtText.append(PaConstants.PROOF_WORKSHEET_NEW_LINE);
    }

    /**
     *  Returns a dvGroup ArrayList of elements of the input
     *  dvGroup1 ArrayList which are not already specified
     *  by the contents of the input array of DistinctVariableStmt.
     *  <p>
     *  For example, if the input dvGroup has an element <x,y,z>
     *  and the input dvStmtArray has an element <z,y,z,t> then
     *  the dvGroup element is not written to the output dvGroups
     *  ArrayList.
     *  <p>
     *  @param dvStmtArray array of DistinctVariablesStmt
     *  @param dvGroupsIn List of List of Var
     *  @return non-redundant dvGroups List.
     */
    public static List<List<Var>> eliminateDvGroupsAlreadyPresent(
        final DistinctVariablesStmt[] dvStmtArray,
        final List<List<Var>> dvGroupsIn)
    {
        final List<List<Var>> out = new ArrayList<List<Var>>(dvStmtArray.length
            + dvGroupsIn.size());

        loopX: for (final List<Var> dvGroup : dvGroupsIn) {
            loopI: for (int i = 0; i < dvStmtArray.length; i++) {

                final Var[] dvI = dvStmtArray[i].dv;

                if (dvGroup.size() > dvI.length)
                    // match impossible
                    continue loopI;

                Var varY;
                loopY: for (int y = 0; y < dvGroup.size(); y++) {

                    varY = dvGroup.get(y);

                    for (int j = 0; j < dvI.length; j++)
                        if (varY == dvI[j])
                            continue loopY; // found one!
                    // didn't find one!
                    continue loopI;
                }
                // found all Vars in dvGroup in one dvStmt!
                continue loopX;
            }
            // didn't find each of dvGroup in any dvStmt!
            out.add(dvGroup);
        }
        return out;
    }

    /**
     *  Returns false, DistinctVariablesStmt never "incomplete"
     *  in ProofWorksheet terms.
     */
    @Override
    public boolean stmtIsIncomplete() {
        return false;
    }

    /**
     *  Function used for cursor positioning.
     *  <p>
     *
     *  @param fieldId value identify ProofWorkStmt field
     *         for cursor positioning, as defined in
     *         PaConstants.FIELD_ID_*.
     *
     *  @return column of input fieldId or default value
     *         of 1 if there is an error.
     */
    @Override
    public int computeFieldIdCol(final int fieldId) {
        return 1;
    }

    /**
     *  Reformats Derivation Step using TMFF.
     *  <p>
     *  Does nothing as there is no formula to reformat.
     */
    @Override
    public void tmffReformat() {}

    /**
     *  Gets dv, the array of distinct variables in the
     *  DistinctVariablesStmt.
     */
    public Var[] getDv() {
        return dv;
    }

    /**
     *   Load Distinct Variable Statement.
     *   <p>
     *   Distinct Variable group of variables must
     *   satisfy these edits:
     *   <p>
     *   <ul>
     *     <li> two or more variables
     *     <li> each variable seq nbr < maxSeq
     *     <li> no duplicate variables
     *   </ul>
     *   <p>
     *   Output/Updates
     *   <p>
     *   <ul>
     *     <li> accum tokens and whitespace into stmtText
     *     <li> load Distinct Variable Group: "var[] dv"
     *          in the DistinctVariableStmt
     *     <li> return nextToken after trailing whitespace,
     *          the start of the next statement.
     *   </ul>
     *
     *  @param firstToken first token of the statement
     *
     *  @return      first token of the next statement.
     */
    @Override
    public String load(final String firstToken) throws IOException, MMIOError,
        ProofAsstException
    {
        final int currLineNbr = (int)w.proofTextTokenizer.getCurrentLineNbr();

        stmtText = new StringBuilder();

        final String firstDv = loadStmtTextGetRequiredToken(firstToken);

        final List<Var> dvList = new ArrayList<Var>();

        validateDvAndAccumInList(firstDv, dvList);

        String nextT = loadStmtTextGetRequiredToken(firstDv);

        while (true) {
            validateDvAndAccumInList(nextT, dvList);
            nextT = loadStmtTextGetOptionalToken(nextT);

            if (nextT.length() == 0
                || nextT.length() == w.proofTextTokenizer.getCurrentColumnNbr())
                break;
        }

        dv = dvList.toArray(new Var[dvList.size()]);

        updateLineCntUsingTokenizer(currLineNbr, nextT);
        return nextT;
    }

    private void validateDvAndAccumInList(final String nextT,
        final List<Var> dvList) throws ProofAsstException
    {
        final Sym sym = w.logicalSystem.getSymTbl().get(nextT);
        if (sym == null)
            w.triggerLoadStructureException(PaConstants.ERRMSG_DV_SYM_ERR_1
                + w.getErrorLabelIfPossible() + PaConstants.ERRMSG_DV_SYM_ERR_2
                + nextT + PaConstants.ERRMSG_DV_SYM_ERR_3);

        if (sym.getSeq() >= w.getMaxSeq())
            w.triggerLoadStructureException(PaConstants.ERRMSG_DV_SYM_MAXSEQ_1
                + w.getErrorLabelIfPossible()
                + PaConstants.ERRMSG_DV_SYM_MAXSEQ_2 + nextT
                + PaConstants.ERRMSG_DV_SYM_MAXSEQ_3);

        if (!sym.isVar())
            w.triggerLoadStructureException(PaConstants.ERRMSG_DV_SYM_CNST_1
                + w.getErrorLabelIfPossible()
                + PaConstants.ERRMSG_DV_SYM_CNST_2 + nextT
                + PaConstants.ERRMSG_DV_SYM_CNST_3);
        final Var v = (Var)sym;

        if (w.getVarHypFromComboFrame(v) == null)
            w.triggerLoadStructureException(PaConstants.ERRMSG_DV_VAR_SCOPE_ERR_1
                + w.getErrorLabelIfPossible()
                + PaConstants.ERRMSG_DV_VAR_SCOPE_ERR_2
                + nextT
                + PaConstants.ERRMSG_DV_VAR_SCOPE_ERR_3);

        final int found = dvList.indexOf(v);
        if (found == -1)
            dvList.add(v);
        else
            w.triggerLoadStructureException(PaConstants.ERRMSG_DV_VAR_DUP_1
                + w.getErrorLabelIfPossible() + PaConstants.ERRMSG_DV_VAR_DUP_2
                + nextT + PaConstants.ERRMSG_DV_VAR_DUP_3);
    }
}
