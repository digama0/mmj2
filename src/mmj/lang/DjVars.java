//********************************************************************/
//* Copyright (C) 2005, 2007, 2008                                   */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * @(#)DjVars.java  0.05 08/01/2008
 *
 * Version 0.02 08/23/2005
 *
 * Version 0.03 06/01/2007
 *     --> Add sortAndCombineDvArrays() for use by
 *         mmj.pa.ProofWorksheet.java
 *     --> move Assrt.loadDjVarsArray to DjVars class.
 *     --> fix compareTo -- make it operate on varLo
 *         and varHi instead of the toString() of
 *         djVars.
 *
 * Version 0.04 08/01/2007
 *     --> added areBothDjVarsInExtendedFrame() for TheoremLoader.
 *
 * Version 0.05 08/02/2008
 *     --> added buildMetamathDjVarsStatementList() and
 *               convertDvGroupsListToMetamathList().
 */

package mmj.lang;

import java.util.*;

import mmj.mmio.MMIOConstants;
import mmj.pa.DistinctVariablesStmt;

/**
 * DjVars is a simple structure that holds a pair of variables specified in a
 * Metamath $d statement, the "Disjoint Variable Restriction" statement.
 * "Disjoint" here means different. See Pg. 96 of Metamath.pdf. Refer also to
 * mmj.verify.VerifyProofs.java, which is where DjVars really come into play.
 * The narrative version is tricky and tedious, and is not repeated here...
 * Another minor complication is that Metamath's "$d" statement employs a
 * shorthand notation allowing several variable to be specified on a single $d
 * statement. This results in multiple DjVars objects since each pair
 * combination in the $d must be treated as a separate restriction (the
 * combinatorial explosion is exciting when there are many variables listed!)
 * Also, multiple $d statements are allowed to have overlapping pairs, perhaps
 * resulting from the combinatorial explosions. This all makes it easy for the
 * person writing the Metamath .mm file, but there is some work storing the
 * results. During loading of LogicalSystem, DjVars are maintained in
 * mmj.lang.ScopeDef.java -- actually, a list of ScopeDef's. And DjVars are
 * stored in mmj.lang.ScopeFrame.java and mmj.lang.OptFrame.java as part of a
 * successful system load operation.
 *
 * @see <a href="../../MetamathERNotes.html"> Nomenclature and
 *      Entity-Relationship Notes</a>
 */
public class DjVars implements Comparable<DjVars> {

    /**
     * varLo and varHi refer to the pair of DjVars after being switched from
     * input, as necessary, so that for example, "a" is varLo and "b" is varHi
     * (sorting based on Sym.id, not MObj.seq in other words.)
     */
    private Var varLo;

    /**
     * varLo and varHi refer to the pair of DjVars after being switched from
     * input, as necessary, so that for example, "a" is varLo and "b" is varHi
     * (sorting based on Sym.id, not MObj.seq in other words).
     */
    private Var varHi;

    /**
     * Helper routine for Theorem Loader to confirm that both DjVars variables
     * are in the Extended Frame of a theorem.
     *
     * @param djVars DjVars object to check.
     * @param mandFrame Mandatory Frame from a Theorem.
     * @param optFrame Optional Frame from a Theorem.
     * @return true if both DjVars variables are used in VarHyps in the Extended
     *         Frame, otherwise false.
     */
    public static boolean areBothDjVarsInExtendedFrame(final DjVars djVars,
        final ScopeFrame mandFrame, final ScopeFrame optFrame)
    {

        boolean loFound = false;
        boolean hiFound = false;
        VarHyp varHyp;

        Hyp[] hypArray = mandFrame.hypArray;
        for (int i = 0; i < hypArray.length; i++) {
            if (!(hypArray[i] instanceof VarHyp))
                continue;

            varHyp = (VarHyp)hypArray[i];
            if (varHyp.getVar() == djVars.varLo)
                loFound = true;
            if (varHyp.getVar() == djVars.varHi)
                hiFound = true;
            if (loFound && hiFound)
                return true;
        }

        hypArray = optFrame.hypArray;
        for (int i = 0; i < hypArray.length; i++) {
            if (!(hypArray[i] instanceof VarHyp))
                continue;

            varHyp = (VarHyp)hypArray[i];
            if (varHyp.getVar() == djVars.varLo)
                loFound = true;
            if (varHyp.getVar() == djVars.varHi)
                hiFound = true;
            if (loFound && hiFound)
                return true;
        }

        return false;
    }

    /**
     * Helper routine for Proof Assistant to confirm that a DjVars variable is
     * defined in the Theorem's Extended Frame.
     *
     * @param djVarsVar variable object to check.
     * @param mandFrame Mandatory Frame from a Theorem.
     * @param optFrame Optional Frame from a Theorem.
     * @return true if both DjVars variables are used in VarHyps in the Extended
     *         Frame, otherwise false.
     */
    public static boolean isDjVarsVarInExtendedFrame(final Var djVarsVar,
        final ScopeFrame mandFrame, final ScopeFrame optFrame)
    {
        VarHyp varHyp;

        Hyp[] hypArray = mandFrame.hypArray;
        for (int i = 0; i < hypArray.length; i++) {
            if (!(hypArray[i] instanceof VarHyp))
                continue;

            varHyp = (VarHyp)hypArray[i];
            if (varHyp.getVar() == djVarsVar)
                return true;
        }

        hypArray = optFrame.hypArray;
        for (int i = 0; i < hypArray.length; i++) {
            if (!(hypArray[i] instanceof VarHyp))
                continue;

            varHyp = (VarHyp)hypArray[i];
            if (varHyp.getVar() == djVarsVar)
                return true;
        }
        return false;
    }

    /**
     * Builds a LinkedList of StringBuilders containing the Metamath formatted
     * text $d statements for the theorem.
     * <p>
     * The input DistinctVariablesStmt array objects are consolidated to remove
     * duplicates, sorted and then merged to create a concise set of $d
     * statements.
     *
     * @param distinctVariablesStmtArray array of Proof Worksheet
     *            DistinctVariablesStmt objects.
     * @return List of StringBuilders containing $d statements (not containing
     *         any newlines).
     */
    public static List<StringBuilder> buildMetamathDjVarsStatementList(
        final DistinctVariablesStmt[] distinctVariablesStmtArray)
    {

        final DjVars[] dvArray = ScopeFrame
            .buildConsolidatedDvArray(distinctVariablesStmtArray);

        final DjVars[] sortedDvArray = DjVars.sortAndCombineDvArrays(dvArray,
            null);

        final List<List<Var>> dvGroups = ScopeFrame
            .consolidateDvGroups(sortedDvArray);

        return DjVars.convertDvGroupsListToMetamathList(dvGroups);
    }

    /**
     * Builds a LinkedList of StringBuilders containing the Metamath format text
     * $d statements for the theorem.
     *
     * @param theorem the theorem for which $d statements are needed.
     * @return List of StringBuilders containing $d statements (not containing
     *         any newlines).
     */
    public static List<StringBuilder> buildMetamathDjVarsStatementList(
        final Theorem theorem)
    {

        final DjVars[] comboDvArray = DjVars.sortAndCombineDvArrays(
            theorem.getMandFrame().djVarsArray,
            theorem.getOptFrame().djVarsArray);

        final List<List<Var>> comboDvGroups = ScopeFrame
            .consolidateDvGroups(comboDvArray);

        return DjVars.convertDvGroupsListToMetamathList(comboDvGroups);
    }

    /**
     * Converts a ArrayList of Lists containing distinct variables into a
     * LinkedList of StringBuilders containing Metamath format text $d
     * statements.
     *
     * @param comboDvGroups List of Lists containing distinct variables.
     * @return List of StringBuilders containing $d statements not containing
     *         any newlines.
     */
    public static List<StringBuilder> convertDvGroupsListToMetamathList(
        final List<List<Var>> comboDvGroups)
    {

        final List<StringBuilder> list = new LinkedList<>();

        for (final List<Var> i : comboDvGroups) {
            final StringBuilder sb = new StringBuilder();

            sb.append(MMIOConstants.MM_DJ_VAR_KEYWORD);

            for (final Var j : i) {
                sb.append(' ');
                sb.append(j.toString());
            }

            sb.append(' ');
            sb.append(MMIOConstants.MM_END_STMT_KEYWORD);

            list.add(sb);
        }
        return list;
    }

    /**
     * Merges two arrays of DjVars returning a single array in ascending
     * compareTo order with duplicates eliminated.
     *
     * @param array1 1st array of DjVars objects, may be null.
     * @param array2 2nd array of DjVars objects, may be null.
     * @return consolidated, sorted array of DjVars objects.
     */
    public static DjVars[] sortAndCombineDvArrays(final DjVars[] array1,
        final DjVars[] array2)
    {

        final List<DjVars> mergedList = new LinkedList<>();

        if (array1 != null)
            for (final DjVars element : array1)
                DjVars.sortAndCombineDvPair(mergedList, element);
        if (array2 != null)
            for (final DjVars element : array2)
                DjVars.sortAndCombineDvPair(mergedList, element);

        return mergedList.toArray(new DjVars[mergedList.size()]);
    }

    /**
     * Merges lists of lists of DjVars returning a single array in ascending
     * compareTo order with duplicates eliminated.
     *
     * @param list1 List containing List elements of DjVars objects.
     * @return consolidated, sorted array of DjVars objects.
     */
    public static DjVars[] sortAndCombineDvListOfLists(
        final List<List<DjVars>> list1)
    {

        final List<DjVars> mergedList = new LinkedList<>();

        if (list1 != null)
            for (final List<DjVars> i : list1)
                if (i != null)
                    for (final DjVars djVars : i)
                        DjVars.sortAndCombineDvPair(mergedList, djVars);

        return mergedList.toArray(new DjVars[mergedList.size()]);
    }

    /**
     * Consolidates a DjVars object into an existing LinkedList maintaining the
     * list in compareTo order.
     *
     * @param mergedList the list to merge into
     * @param dvPair the DjVars to merge
     */
    private static void sortAndCombineDvPair(final List<DjVars> mergedList,
        final DjVars dvPair)
    {
        final ListIterator<DjVars> iterator = mergedList.listIterator();

        while (iterator.hasNext()) {

            final DjVars d = iterator.next();

            final int compValue = d.compareTo(dvPair);
            if (compValue < 0)
                continue; // keep scanning list forward

            if (compValue == 0)
                return; // dup found, don't add...

            iterator.previous();
            iterator.add(dvPair);
            return;
        }

        mergedList.add(dvPair); // add at end and continue
    }

    /**
     * Construct using two Var id Strings. The input variables are validated and
     * switched if necessary into varLo and varHi based on Var.id. Note: it is
     * required that the Var's specified be "active", but not that there be
     * active VarHyp's associated with them. The $d vars on an Assrt may be
     * specified for use in a proof step, and it is guaranteed that that proof
     * step will have its own VarHyp's (or else there would be a different
     * error.)
     *
     * @param symTbl Symbol Table (Map)
     * @param loS Var1 id String.
     * @param hiS Var2 id String.
     * @throws LangException if the two Var id's are identical, or are not
     *             defined and active vars.
     */
    public DjVars(final Map<String, Sym> symTbl, final String loS,
        final String hiS) throws LangException
    {
        final Var lo = Var.verifyVarDefAndActive(symTbl, loS);
        final Var hi = Var.verifyVarDefAndActive(symTbl, hiS);
        set(lo, hi);
    }

    /**
     * Construct using two Var's. The input variables are compared and switched
     * if necessary into varLo and varHi based on Var.id.
     *
     * @param lo Var 1.
     * @param hi Var 2.
     * @throws LangException if the two Var id's are identical.
     */
    public DjVars(final Var lo, final Var hi) throws LangException {
        set(lo, hi);
    }

    /**
     * Set the contents of this DjVars object. The input variables are compared
     * and switched if necessary into varLo and varHi based on Var.id.
     *
     * @param lo Var 1.
     * @param hi Var 2.
     * @throws LangException if the two Var id's are identical.
     */
    public void set(final Var lo, final Var hi) throws LangException {
        final int compare = DV_ORDER.compare(lo, hi);
        if (compare == 0)
            throw new LangException(LangConstants.ERRMSG_DJ_VARS_ARE_DUPS,
                lo.getId());
        if (compare > 0) {
            varLo = hi;
            varHi = lo;
        }
        else {
            varLo = lo;
            varHi = hi;
        }
    }

    /**
     * Return the "low" Dj Var, varLo.
     *
     * @return varLo the "low" Var based on Var.id.
     */
    public Var getVarLo() {
        return varLo;
    }

    /**
     * Return the "high" Dj Var, varHi.
     *
     * @return varHi the "high" Var based on Var.id.
     */
    public Var getVarHi() {
        return varHi;
    }

    /**
     * Converts DjVars to String.
     *
     * @return returns DjVars string;
     */
    @Override
    public String toString() {
        return LangConstants.DJVARS_LEFT_BRACKET + varLo.getId()
            + LangConstants.DJVARS_SEPARATOR + varHi.getId()
            + LangConstants.DJVARS_RIGHT_BRACKET;
    }

    /*
     * Computes hashcode for this DjVars.
     *
     * @return hashcode for the Order.
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /*
     * Compare for equality with another DjVars.
     * <p>
     * Equal if and only if the DjVars variables are equal.
     * and the obj to be compared to this object is not null
     * and is a DjVars as well.
     *
     * @return returns true if equal, otherwise false.
     */
    @Override
    public boolean equals(final Object obj) {
        return this == obj || obj instanceof DjVars
            && varLo == ((DjVars)obj).varLo && varHi == ((DjVars)obj).varHi;
    }

    /**
     * Compares DjVars object based on the variables.
     *
     * @param obj Order object to compare to this Order
     * @return returns negative, zero, or a positive int if this Order object is
     *         less than, equal to or greater than the input parameter obj.
     */
    public int compareTo(final DjVars obj) {
        int compare = DV_ORDER.compare(varLo, obj.varLo);
        if (compare == 0)
            compare = DV_ORDER.compare(varHi, obj.varHi);
        return compare;

//      return  ((this.toString()).compareTo(
//               ((DjVars)obj).toString()));
    }

    /**
     * This is the ordering that will be used to determine which variable in a
     * DjVars is "hi" and which is "lo", and is visible to the user as the
     * ordering of variables in missing $d statmements generated by the program.
     * This comparator sorts by the standard ASCII string comparison on the
     * labels, but sorts set variables before other things. If the .mm file that
     * is loaded does not have a type code called "set", this is just a plain
     * ASCII sort.
     */
    public static Comparator<? super Var> DV_ORDER = new Comparator<Var>() {
        public int compare(final Var o1, final Var o2) {
            if (o1.getActiveVarHyp() == null && o2.getActiveVarHyp() == null)
                return o1.getId().compareTo(o2.getId());

            if (o1.getActiveVarHyp() == null || o2.getActiveVarHyp() == null)
                return o1.getActiveVarHyp() != null ? -1 : 1;

            final boolean s1 = o1.getActiveVarHyp().getTyp().isSetTyp();
            final boolean s2 = o2.getActiveVarHyp().getTyp().isSetTyp();
            if (s1 == s2)
                return o1.getId().compareTo(o2.getId());
            return s1 ? -1 : 1;
        }
    };
}
