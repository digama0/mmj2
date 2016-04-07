//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * OptFrame.java  0.02 08/23/2005
 */

package mmj.lang;

import java.util.*;

import mmj.pa.DistinctVariablesStmt;

/**
 * A combined class representing {@code mandFrame}, the Mandatory "Frame" of an
 * Assrt (assertion), as well as {@code optFrame}, the Optional "Frame" of a
 * Theorem (OptFrame not present in other Assrt's).
 * <p>
 * Add {@code mandFrame} to {@code optFrame} and you have a Metamath
 * "Extended Frame".
 *
 * @see <a href="../../MetamathERNotes.html"> Nomenclature and
 *      Entity-Relationship Notes</a>
 */
public class ScopeFrame {

    /**
     * These are the "mandatories" that are referenced in an assertion, in order
     * of appearance in the database.
     * <p>
     * For a Theorem these include not only the variable hypotheses for
     * variables referenced in the assertion's Formula, but the logical
     * hypotheses in scope of the Theorem plus the variable hypotheses for
     * variables referenced by *those* logical hypotheses.
     * <p>
     * For an {@code optFrame}, these include all Hyps in scope not present in
     * the mandatory {@code hypArray}.
     */
    public Hyp[] hypArray;

    /**
     * These are the disjoint variable (pair)restrictions, if any, that apply to
     * the Assrt and any of its proof steps.
     * <p>
     * For an {@code optFrame}, these include all DjVars (pairs) in scope not
     * present in the mandatory {@code djVarsArray}.
     */
    public DjVars[] djVarsArray;

    /**
     * Default Constructor.
     */
    public ScopeFrame() {}

    /**
     * Checks to see if a certain pair of variables is mentioned in a OptFrame's
     * DjVars array.
     * <p>
     * Note: vLo and vHi are automatically switched if vLo is not low.
     * <p>
     * Note: this function is the reason that DjVars vars are stored as "lo" and
     * "hi". Low and High are irrelevant to the mathematics of this situation.
     * But if the vars were stored randomly or arbitrarily, then twice as many
     * comparisons would be needed here.
     *
     * @param frame Scope Frame to inspect.
     * @param vLo the "low" variable in the pair.
     * @param vHi the "high" variable in the pair.
     * @return true if param var pair in OptFrame DjVars array.
     */
    public static boolean isVarPairInDjArray(final ScopeFrame frame, Var vLo,
        Var vHi)
    {
        Var vSwap;
        if (DjVars.DV_ORDER.compare(vLo, vHi) > 0) {
            vSwap = vHi;
            vHi = vLo;
            vLo = vSwap;
        }
        for (final DjVars element : frame.djVarsArray)
            if (element.getVarLo() == vLo && element.getVarHi() == vHi)
                return true;
        return false;
    }

    /**
     * Converts an array of ProofWorksheet DistinctVariablesStmt objects into an
     * array of DjVars objects.
     *
     * @param distinctVariablesStmtArray array of DistinctVariablesStmt object.
     * @return array of DjVars objects.
     */
    public static DjVars[] buildConsolidatedDvArray(
        final DistinctVariablesStmt[] distinctVariablesStmtArray)
    {

        final List<DjVars> dvList = new ArrayList<>();

        final Var[][] dvGroupArray = new Var[distinctVariablesStmtArray.length][];
        for (int i = 0; i < distinctVariablesStmtArray.length; i++)
            dvGroupArray[i] = distinctVariablesStmtArray[i].getDv();

        loadDvGroupsIntoList(dvList, dvGroupArray);

        return dvList.toArray(new DjVars[dvList.size()]);
    }

    /**
     * Accumulates new Distinct Variables into an List of DjVars pairs. The
     * dvGroupArray is Var[][], array of Var arrays. Each Var array is of the
     * form { x, y, z, ... } which generates DjVars for (x,y), (x,z), (y,z),
     * etc. There is one Var array for each $d statement, in other words.
     * Duplicate DjVars pairs are dropped without notice, as are pairs such as
     * (x, x). Convenience method for ProofAsst's use with an existing theorem.
     *
     * @param arrayList list of DjVars objects
     * @param dvGroupArray array of Var[] arrays.
     */
    public static void loadDvGroupsIntoList(final List<DjVars> arrayList,
        final Var[][] dvGroupArray)
    {
        DjVars djVars;
        int found;

        if (dvGroupArray != null) {
            Var[] dvGroup;
            for (final Var[] element : dvGroupArray) {
                dvGroup = element;
                for (int j = 0; j < dvGroup.length - 1; j++)
                    for (int k = j + 1; k < dvGroup.length; k++)
                        if (!dvGroup[j].equals(dvGroup[k]))
                            try {
                                djVars = new DjVars(dvGroup[j], dvGroup[k]);
                                found = arrayList.indexOf(djVars);
                                if (found == -1)
                                    arrayList.add(djVars);
                            } catch (final LangException e) {
                                // this should never happen because we're
                                // checking for dups and bypassing them,
                                // so blow up on this occurrence!
                                throw new IllegalArgumentException(
                                    new LangException(e,
                                        LangConstants.ERRMSG_DUP_DJ_VARS_AFTER_CONSOLIDATION_ERR,
                                        e.getMessage()));
                            }
            }
        }
    }

    /**
     * Attempts to do a good job consolidating pairs of disjoint variables into
     * groups of three or more.
     * <p>
     * The input dvArray is assumed to be in ascending order according to
     * DjVars.compareTo. We assume AND require that it be free from duplicates.
     * Here is an example of dvArray -- sorted and with no duplicates:
     * <p>
     *
     * <pre>
     * [(a,b),(a,c),(a,d),(b,c),(b,d),(b,e),(c,d),(c,f)]
     * </pre>
     *
     * The output List contains an ordered set of Lists of disjoint variables,
     * sorted by the first Var character string in the list. Here is sample
     * output for the example above:
     *
     * <pre>
     * [ [a,b,c,d], [b,e], [c,f] ]
     * </pre>
     *
     * The algorithm should be capable of consolidating $d's into </code>[
     * [a,x,y,z], [b,x,y,z] ]</code>, as well as </code> [ [x,y,z,a], [x,y,z,b]
     * ]</code>.
     * <p>
     * Note that within a DjVars object. varLo and varHi are loaded by Var.Id.
     * Thus, for disjoint vars "a" and "b", varLo = a, and varHi = b. Also,
     * varLo is never equal to varHi.
     *
     * @param dvArray the input list of DjVars
     * @return the list of distinct variable groups
     */
    public static List<List<Var>> consolidateDvGroups(final DjVars[] dvArray) {

        final List<List<Var>> dvGroupList = new ArrayList<>();

        // "done" is a parallel array for dvArray. done[i] set to
        // true when dvArray[i] has been stored in the output.
        final boolean[] done = new boolean[dvArray.length];

        // "checked" is an List of Integers used to hold
        // the indexes of dvArray elements that match the
        // searched-for varLo and varHi(s), signifying that they
        // are disjoint. checked is needed so that when
        // a candidate Var is added to a dvGroup, all
        // corresponding dvArray elements can be marked "done" --
        // and if the candidate is rejected, the checked contents
        // are discarded (this saves an extra scan of dvArray).
        final List<Integer> checked = new ArrayList<>();

        // choices are to insert a new Var just *before* the end
        // of a dvGroup's last Var or *at* the end. See below...
        boolean insertInDvGroupBeforeEnd;
        int insertIndex;

        // currVarLoFirst and Last define a "chunk" of the
        // input wherein each DjVars object has a matching
        // varLo.
        int currVarLoFirst = 0;
        int currVarLoLast;
        Var currVarLo;

        // pass through the dvArray "chunks", one at a time.
        while (currVarLoFirst < dvArray.length) {

            currVarLo = dvArray[currVarLoFirst].getVarLo();

            // here we find the index of the last dvArray element
            // in the "chunk" with a matching varLo
            currVarLoLast = currVarLoFirst + 1;
            while (true)
                if (currVarLoLast < dvArray.length
                    && dvArray[currVarLoLast].getVarLo() == currVarLo)
                    currVarLoLast++;
                else {
                    currVarLoLast--;
                    break;
                }

            // for each dvArray element within the varLo
            // chunk (subset) of dvArray, create a new dvGroup --
            // unless the dvArray element is already marked "done":
            for (int i = currVarLoFirst; i <= currVarLoLast; i++) {

                if (done[i])
                    continue;

                // the minimum length of a dvGroup is 2
                final List<Var> dvGroup = new ArrayList<>();
                dvGroup.add(dvArray[i].getVarLo());
                dvGroup.add(dvArray[i].getVarHi());
                done[i] = true;

                // Now, for each dvArray element in
                // the current chunk (has matching varLo),
                // see if its varHi is disjoint with
                // every Var in the new dvGroup. If so, insert!
                // Insert before end of DvGroup if j < i --
                // otherwise at end.
                insertInDvGroupBeforeEnd = true;
                for (int j = currVarLoFirst; j <= currVarLoLast; j++) {
                    if (j == i) {
                        insertInDvGroupBeforeEnd = false;
                        continue;
                    }
                    if (areAllDisjoint(dvArray[j].getVarHi(), dvGroup, dvArray,
                        currVarLoLast, checked))
                    {

                        // success! consolidated another one!
                        done[j] = true;
                        if (insertInDvGroupBeforeEnd) {
                            insertIndex = dvGroup.size() - 1;
                            dvGroup.add( // shift last Var right
                                dvGroup.get(insertIndex));
                            dvGroup.set( // add new prior to last
                                insertIndex, dvArray[j].getVarHi());
                        }
                        else
                            dvGroup.add(dvArray[j].getVarHi());

                        // use "checked" to mark as "done" every
                        // dvArray element that participated --
                        // their disjoint variable restriction info
                        // is now reflected in the dvGroup's info!
                        for (int k = 0; k < checked.size(); k++)
                            done[checked.get(k)] = true;
                    }
                }

                // add the new dvGroup to the output -- this will
                // ultimately generate *one* $d statement with
                // two or more variables:
                dvGroupList.add(dvGroup);
            }

            currVarLoFirst = currVarLoLast + 1; // next chunk!
        }

        return dvGroupList;
    }

    /**
     * Builds a Frame using a ScopeDef and an array of Distinct Variables,
     * limited by an input maximum sequence number. Convenience method for
     * ProofAsst's use on a new theorem. ProofAsst allows new theorems, assuming
     * that all variables and hypotheses used are in global scope, but it also
     * provides a "LocAfter" statement specification, so maxSeq is input to
     * restrict the contents of the new Frame. LogHyp's in the scopeDef are not
     * loaded into the Frame -- not expected at global scope level, and global
     * LogHyp's are not used in ProofAsst (haven't seen one yet, just saying).
     *
     * @param scopeDef ScopeDef, intended to be global scope.
     * @param maxSeq MObj.seq must be < maxSeq
     */
    public ScopeFrame(final ScopeDef scopeDef, final int maxSeq) {

        if (maxSeq < Integer.MAX_VALUE)
            hypArray = scopeDef.scopeVarHyp.stream()
                .filter(hyp -> hyp.getSeq() < maxSeq).toArray(Hyp[]::new);
        else
            hypArray = scopeDef.scopeVarHyp
                .toArray(new Hyp[scopeDef.scopeVarHyp.size()]);

        final List<DjVars> arrayList = new ArrayList<>(
            scopeDef.scopeDjVars.size());
        for (final DjVars djVars : scopeDef.scopeDjVars)
            if (djVars.getVarLo().getSeq() < maxSeq
                && djVars.getVarHi().getSeq() < maxSeq)
                arrayList.add(djVars);

        djVarsArray = arrayList.toArray(new DjVars[arrayList.size()]);
    }

    /**
     * Builds a composite ("combo") Frame using a MandFrame plus an OptFrame,
     * augmented by additional $d specifications. Convenience method for
     * ProofAsst's use with an existing theorem.
     *
     * @param mandFrame a Theorem's MandFrame
     * @param optFrame a Theorem's OptFrame
     */
    public ScopeFrame(final ScopeFrame mandFrame, final ScopeFrame optFrame) {

        List<Hyp> hyps;

        hyps = new ArrayList<>(
            mandFrame.hypArray.length + optFrame.hypArray.length);

        for (final Hyp hyp : mandFrame.hypArray)
            Assrt.accumHypInList(hyps, hyp);
        for (final Hyp hyp : optFrame.hypArray)
            Assrt.accumHypInList(hyps, hyp);
        hypArray = hyps.toArray(new Hyp[hyps.size()]);

        djVarsArray = new DjVars[mandFrame.djVarsArray.length
            + optFrame.djVarsArray.length];

        int djCnt = 0;
        for (int i = 0; i < mandFrame.djVarsArray.length; i++)
            djVarsArray[djCnt++] = mandFrame.djVarsArray[i];
        for (final DjVars dj : optFrame.djVarsArray)
            djVarsArray[djCnt++] = dj;

    }

    /**
     * Checks to see whether or not both variables in a DjVars pair are
     * referenced in the MandFrame's array of hypotheses.
     * <p>
     * Note: checks only the VarHyp's.
     *
     * @param djVars -- DjVars object containing 2 variables to be checked
     *            against the variables referenced in the MandFrame hypArray.
     * @return boolean -- true if both DjVars variables are present in hypArray,
     *         otherwise false.
     */
    public boolean areBothDjVarsInHypArray(final DjVars djVars) {
        boolean loFound = false;
        boolean hiFound = false;
        VarHyp varHyp;
        for (int i = 0; i < hypArray.length; i++) {
            if (!(hypArray[i] instanceof VarHyp))
                continue;

            varHyp = (VarHyp)hypArray[i];
            if (varHyp.getVar() == djVars.getVarLo())
                loFound = true;
            if (varHyp.getVar() == djVars.getVarHi())
                hiFound = true;
            if (loFound && hiFound)
                return true;
        }
        return false;
    }

    /**
     * Accumulates an array of DvGroups into the djVarsArray.
     * <p>
     * FYI, a DvGroup is an array of at least 2 variables which are by
     * definition supposed to be distinct from each other. So [ph th ch] is a
     * DvGroup which could be obtained from a $d statement like "$d ph th ch $."
     *
     * @param dvGroupArray array of array of disjoint variables.
     */
    public void addDjVarGroups(final Var[][] dvGroupArray) {
        final List<DjVars> arrayList = new ArrayList<>(
            Arrays.asList(djVarsArray));

        loadDvGroupsIntoList(arrayList, dvGroupArray);

        djVarsArray = arrayList.toArray(new DjVars[arrayList.size()]);

    }

    /**
     * Builds and returns Var HashMap derived from the MandFrame hypArray. The
     * returned map of Vars contains the vars returned from the MandFrame's
     * VarHyps via getVar(), and does not seek out vars used in the formulas of
     * LogHyps mentioned in the hypArray. Reason? The VarHyps for any LogHyps
     * should *already* be in the MandFrame hypArray, by definition (of
     * MandFrame). Convenience method for ProofAsst's use.
     *
     * @return Map of Vars for VarHyps in ScopeFrame.hypArray.
     */
    public Map<String, Var> getVarMap() {
        final Map<String, Var> varMap = new HashMap<>(
            hypArray.length * 3 / 2 + 1);
        for (final Hyp hyp : hypArray)
            if (hyp instanceof VarHyp) {
                final Var v = ((VarHyp)hyp).getVar();
                varMap.put(v.getId(), v);
            }
        return varMap;
    }

    /**
     * Here we make a single scan of dvArray looking to match DjVars elements
     * for the dvArray varHi element and each of the Var elements already in the
     * dvGroup. This is a multiple search because we are looking for multiple
     * "hits".
     * <p>
     * The indexes of the matching dvArray entries are stored in "checked", for
     * use later in marking them "done".
     * <p>
     * If there are *any* NOTFND's, whatsoever, return false -- complete and
     * total success in the search is required (signifying that the candidate
     * varHi variable is disjoint with every Var in dvGroup!)
     * <p>
     * Note that the Vars in dvGroup are stored in ascending order by Sym.Id.
     * This enables us to make a single pass through dvArray and perform all of
     * the matches (dvArray is *also* sorted in ascending order.)
     *
     * @param dvArrayVar the other Var to pair with each entry in dvGroup
     * @param dvGroup the array of Vars to search for
     * @param dvArray the array of DjVars to match against
     *            {@code [dvArrayVar, dvGroup]}
     * @param searchIndex the start index into dvArray to search for matches
     * @param checked the output array of matching dvArray entries
     * @return if all DjVars are disjoint
     */
    private static boolean areAllDisjoint(final Var dvArrayVar,
        final List<Var> dvGroup, final DjVars[] dvArray, int searchIndex,
        final List<Integer> checked)
    {
        checked.clear();

        DjVars search = null;
        int compare;
        // why start at 1 not 0?
        Loop1: for (int i = 1; i < dvGroup.size(); i++) {
            try {
                if (search == null)
                    search = new DjVars(dvGroup.get(i), dvArrayVar);
                else
                    search.set(dvGroup.get(i), dvArrayVar);
            } catch (final LangException e) {
                // Shouldn't happen
            }

            while (true) {
                if (++searchIndex >= dvArray.length)
                    return false; // not found
                if ((compare = dvArray[searchIndex].compareTo(search)) > 0)
                    return false; // not found
                if (compare == 0) {
                    // found!
                    checked.add(Integer.valueOf(searchIndex));
                    continue Loop1;
                }
                continue;
            }
        }
        return true;
    }
}
