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
 *  Version 0.02 08/23/2005
 *
 *  Version 0.03 06/01/2007
 *      --> Add sortAndCombineDvArrays() for use by
 *          mmj.pa.ProofWorksheet.java
 *      --> move Assrt.loadDjVarsArray to DjVars class.
 *      --> fix compareTo -- make it operate on varLo
 *          and varHi instead of the toString() of
 *          djVars.
 *
 *  Version 0.04 08/01/2007
 *      --> added areBothDjVarsInExtendedFrame() for TheoremLoader.
 *
 *  Version 0.05 08/02/2008
 *      --> added buildMetamathDjVarsStatementList() and
 *                convertDvGroupsListToMetamathList().
 */

package mmj.lang;

import java.util.*;
import mmj.mmio.MMIOConstants;
import mmj.pa.DistinctVariablesStmt;

/**
 *  DjVars is a simple structure that holds a pair of
 *  variables specified in a Metamath $d statement, the
 *  "Disjoint Variable Restriction" statement.
 *
 *  "Disjoint" here means different. See Pg. 96 of Metamath.pdf.
 *  Refer also to mmj.verify.VerifyProofs.java, which is where
 *  DjVars really come into play. The narrative version is
 *  tricky and tedious, and is not repeated here...
 *
 *  Another minor complication is that Metamath's "$d"
 *  statement employs a shorthand notation allowing several
 *  variable to be specified on a single $d statement. This
 *  results in multiple DjVars objects since each pair
 *  combination in the $d must be treated as a separate
 *  restriction (the combinatorial explosion is exciting
 *  when there are many variables listed!) Also, multiple
 *  $d statements are allowed to have overlapping
 *  pairs, perhaps resulting from the combinatorial explosions.
 *  This all makes it easy for the person writing the Metamath
 *  .mm file, but there is some work storing the results.
 *
 *  During loading of LogicalSystem, DjVars are maintained
 *  in mmj.lang.ScopeDef.java -- actually, a list of ScopeDef's.
 *  And DjVars are stored in mmj.lang.MandFrame.java and
 *  mmj.lang.OptFrame.java as part of a successful system
 *  load operation.
 *
 *  @see <a href="../../MetamathERNotes.html">
 *       Nomenclature and Entity-Relationship Notes</a>
 */
public class DjVars implements Comparable {

    /**
     *  varLo and varHi refer to the pair of DjVars after
     *  being switched from input, as necessary, so that
     *  for example, "a" is varLo and "b" is varHi (sorting
     *  based on Sym.id, not MObj.seq in other words.)
     */
    Var varLo;

    /**
     *  varLo and varHi refer to the pair of DjVars after
     *  being switched from input, as necessary, so that
     *  for example, "a" is varLo and "b" is varHi (sorting
     *  based on Sym.id, not MObj.seq in other words).
     */
    Var varHi;

    /**
     *  Copies djVarsList List to an array of DjVars.
     *  <p>
     *  This was coded because i couldn't get
     *  ArrayList.toArray() to compile. Doh!
     *
     *  @param  djVarsList List of DjVars to copy
     *
     *  @return Array of DjVars copied from hyplist.
     */
    public static DjVars[] loadDjVarsArray(List djVarsList) {

        DjVars[] djVarsArray      = new DjVars[djVarsList.size()];

        Iterator iterator         = djVarsList.iterator();
        int      n                = 0;
        while (iterator.hasNext()) {
            djVarsArray[n++]      = (DjVars)iterator.next();
        }
        return djVarsArray;
    }

    /**
     *  Helper routine for Theorem Loader to confirm that
     *  both DjVars variables are in the Extended Frame
     *  of a theorem.
     *  <p>
     *
     *  @param  djVars DjVars object to check.
     *  @param  mandFrame Mandatory Frame from a Theorem.
     *  @param  optFrame Optional Frame from a Theorem.
     *
     *  @return true if both DjVars variables are used in
     *          VarHyps in the Extended Frame, otherwise false.
     */
    public static boolean areBothDjVarsInExtendedFrame(
                                            DjVars    djVars,
                                            MandFrame mandFrame,
                                            OptFrame  optFrame) {

        boolean loFound = false;
        boolean hiFound = false;
        Hyp     hyp;
        VarHyp  varHyp;

        Hyp[]   hypArray          = mandFrame.hypArray;
        for (int i = 0; i < hypArray.length; i++) {
            if (!hypArray[i].isVarHyp()) {
                continue;
            }

            varHyp                = (VarHyp)hypArray[i];
            if (varHyp.getVar() == djVars.varLo) {
                loFound = true;
            }
            if (varHyp.getVar() == djVars.varHi) {
                hiFound = true;
            }
            if (loFound && hiFound) {
                return true;
            }
        }

        hypArray                  = optFrame.optHypArray;
        for (int i = 0; i < hypArray.length; i++) {
            if (!hypArray[i].isVarHyp()) {
                continue;
            }

            varHyp                = (VarHyp)hypArray[i];
            if (varHyp.getVar() == djVars.varLo) {
                loFound = true;
            }
            if (varHyp.getVar() == djVars.varHi) {
                hiFound = true;
            }
            if (loFound && hiFound) {
                return true;
            }
        }

        return false;
    }

    /**
     *  Helper routine for Proof Assistant to confirm that
     *  a DjVars variable is defined in the Theorem's Extended Frame.
     *  <p>
     *
     *  @param  djVarsVar variable object to check.
     *  @param  mandFrame Mandatory Frame from a Theorem.
     *  @param  optFrame Optional Frame from a Theorem.
     *
     *  @return true if both DjVars variables are used in
     *          VarHyps in the Extended Frame, otherwise false.
     */
    public static boolean isDjVarsVarInExtendedFrame(
                                            Var       djVarsVar,
                                            MandFrame mandFrame,
                                            OptFrame  optFrame) {

        Hyp     hyp;
        VarHyp  varHyp;

        Hyp[]   hypArray          = mandFrame.hypArray;
        for (int i = 0; i < hypArray.length; i++) {
            if (!hypArray[i].isVarHyp()) {
                continue;
            }

            varHyp                = (VarHyp)hypArray[i];
            if (varHyp.getVar() == djVarsVar) {
                return true;
            }
        }

        hypArray                  = optFrame.optHypArray;
        for (int i = 0; i < hypArray.length; i++) {
            if (!hypArray[i].isVarHyp()) {
                continue;
            }

            varHyp                = (VarHyp)hypArray[i];
            if (varHyp.getVar() == djVarsVar) {
                return true;
            }
        }
        return false;
    }

    /**
     *  Builds a LinkedList of StringBuffers containing the
     *  Metamath formatted text $d statements for the theorem.
     *  <p>
     *  The input DistinctVariablesStmt array objects are
     *  consolidated to remove duplicates, sorted and then
     *  merged to create a concise set of $d statements.
     *  <p>
     *  @param distinctVariablesStmtArray array of Proof Worksheet
     *              DistinctVariablesStmt objects.
     *  @return LinkedList of StringBuffers containing $d statements
     *                        (not containing any newlines).
     */
    public static LinkedList buildMetamathDjVarsStatementList(
                DistinctVariablesStmt[] distinctVariablesStmtArray) {

        DjVars[] dvArray          =
            MandFrame.
                buildConsolidatedDvArray(
                    distinctVariablesStmtArray);

        DjVars[] sortedDvArray    =
            DjVars.sortAndCombineDvArrays(
                dvArray,
                null);

        ArrayList dvGroups        =
            MandFrame.
                consolidateDvGroups(
                    sortedDvArray);

        return  DjVars.
                    convertDvGroupsListToMetamathList(
                        dvGroups);
    }

    /**
     *  Builds a LinkedList of StringBuffers containing the
     *  Metamath format text $d statements for the theorem.
     *  <p>
     *  @param theorem the theorem for which $d statements are needed.
     *  @return LinkedList of StringBuffers containing $d statements
     *                        (not containing any newlines).
     */
    public static LinkedList buildMetamathDjVarsStatementList(
                                    Theorem theorem) {

        DjVars[] comboDvArray     =
            DjVars.sortAndCombineDvArrays(
                theorem.getMandFrame().djVarsArray,
                theorem.getOptFrame().optDjVarsArray);

        ArrayList comboDvGroups   =
            MandFrame.consolidateDvGroups(
                comboDvArray);

        return  DjVars.
                    convertDvGroupsListToMetamathList(
                        comboDvGroups);
    }


    /**
     *  Converts a ArrayList of ArrayLists containing distinct
     *  variables into a LinkedList of StringBuffers containing
     *  Metamath format text $d statements.
     *  <p>
     *  @param comboDvGroups ArrayList of ArrayLists containing
     *                      distinct variables.
     *  @return LinkedList of StringBuffers containing $d statements
     *                      not containing any newlines.
     */
    public static LinkedList convertDvGroupsListToMetamathList(
                                    ArrayList comboDvGroups) {

        LinkedList list           = new LinkedList();
        StringBuffer sb           = new StringBuffer();

        Iterator i                = comboDvGroups.iterator();
        while (i.hasNext()) {

            sb.append(MMIOConstants.MM_KEYWORD_1ST_CHAR);
            sb.append(MMIOConstants.MM_DJ_VAR_KEYWORD_CHAR);

            Iterator j            = ((ArrayList)i.next()).iterator();
            while (j.hasNext()) {
                sb.append(' ');
                sb.append(((Var)j.next()).toString());
            }

            sb.append(' ');
            sb.append(MMIOConstants.MM_END_STMT_KEYWORD);

            list.add(sb);
            sb                    = new StringBuffer();
        }
        return list;
    }


    /**
     *  Merges two arrays of DjVars returning a single
     *  array in ascending compareTo order with duplicates
     *  eliminated.
     *  <p>
     *  @param array1 1st array of DjVars objects, may be null.
     *  @param array2 2nd array of DjVars objects, may be null.
     *  @return consolidated, sorted array of DjVars objects.
     */
    public static DjVars[] sortAndCombineDvArrays(
                                        DjVars[] array1,
                                        DjVars[] array2) {

        LinkedList mergedList     = new LinkedList();

        if (array1 != null) {
            for (int i = 0; i < array1.length; i++) {
                DjVars.sortAndCombineDvPair(mergedList,
                                            array1[i]);
            }
        }
        if (array2 != null) {
            for (int i = 0; i < array2.length; i++) {
                DjVars.sortAndCombineDvPair(mergedList,
                                            array2[i]);
            }
        }

        DjVars[] dvArray          =
            new DjVars[mergedList.size()];
        Iterator i                = mergedList.iterator();
        int      c                = 0;
        while (i.hasNext()) {
            dvArray[c++]          = (DjVars)i.next();
        }

        return dvArray;
    }

    /**
     *  Merges lists of lists of DjVars returning a single
     *  array in ascending compareTo order with duplicates
     *  eliminated.
     *  <p>
     *  @param list1 List containing List elements of DjVars objects.
     *  @return consolidated, sorted array of DjVars objects.
     */
    public static DjVars[] sortAndCombineDvListOfLists(List list1) {

        LinkedList mergedList     = new LinkedList();

        Iterator i;
        Iterator j;
        Object   o;
        DjVars   djVars;
        if (list1 != null) {
            i                     = list1.iterator();
            while (i.hasNext()) {
                o                 = i.next();
                if (o != null) {
                    j             = ((List)o).iterator();
                    while (j.hasNext()) {
                        djVars    = (DjVars)j.next();
                        DjVars.sortAndCombineDvPair(
                            mergedList,
                            djVars);
                    }
                }
            }
        }

        DjVars[] dvArray          =
            new DjVars[mergedList.size()];
        i                         = mergedList.iterator();
        int      c                = 0;
        while (i.hasNext()) {
            dvArray[c++]          = (DjVars)i.next();
        }

        return dvArray;
    }


    /**
     *  Consolidates a DjVars object into an existing LinkedList
     *  maintaining the list in compareTo order.
     */
    private static void sortAndCombineDvPair(
                                        LinkedList mergedList,
                                        DjVars     dvPair) {
        ListIterator iterator;

        DjVars       d;

        int          compValue;

        iterator                  = mergedList.listIterator(0);

        while (iterator.hasNext()) {

            d                 = (DjVars)iterator.next();

            if ((compValue = d.compareTo(dvPair)) < 0) {
                continue;            //keep scanning list forward
            }

            if (compValue == 0) {
                return;              //dup found, don't add...
            }

            iterator.previous();
            iterator.add(dvPair);
            return;
        }

        mergedList.add(dvPair);     //add at end and continue
    }

    /**
     *  Default constructor.
     */
    public DjVars() {
    }

    /**
     *  Construct using two Var id Strings.
     *
     *  The input variables are validated and switched
     *  if necessary into varLo and varHi based on
     *  Var.id.
     *
     *  Note: it is required that the Var's specified
     *        be "active", but not that there be
     *        active VarHyp's associated with them.
     *        The $d vars on an Assrt may be specified
     *        for use in a proof step, and it is
     *        guaranteed that that proof step will have
     *        its own VarHyp's (or else there would be
     *        a different error.)
     *
     *  @param symTbl  Symbol Table (Map)
     *  @param loS     Var1 id String.
     *  @param hiS     Var2 id String.
     *
     *  @throws LangException if the two Var id's are identical,
     *          or are not defined and active vars.
     */
    public DjVars(Map     symTbl,
                  String  loS,
                  String  hiS)
                                throws LangException {

        Var lo = Var.verifyVarDefAndActive(symTbl,
                                           loS);
        Var hi = Var.verifyVarDefAndActive(symTbl,
                                           hiS);
        int compare = (loS.compareTo(hiS));
        if (compare == 0) {
            throw new LangException(
                LangConstants.ERRMSG_DJ_VARS_ARE_DUPS +
                loS);
        }
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
     *  Construct using two Var's.
     *
     *  The input variables are compared and switched
     *  if necessary into varLo and varHi based on
     *  Var.id.
     *
     *  @param lo      Var 1.
     *  @param hi      Var 2.
     *
     *  @throws LangException if the two Var id's are identical.
     */
    public DjVars(Var lo, Var hi)
                            throws LangException {
        int compare = (lo.compareTo(hi));
        if (compare == 0) {
            throw new LangException(
                LangConstants.ERRMSG_DJ_VARS_ARE_DUPS +
                lo.getId());
        }
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
     *  Return the "low" Dj Var, varLo.
     *
     *  @return varLo the "low" Var based on Var.id.
     */
    public Var getVarLo() {
        return varLo;
    }

    /**
     *  Set the "low" Dj Var, varLo.
     *
     *  @param varLo the "low" Var based on Var.id.
     */
    public void setVarLo(Var varLo) {
        this.varLo = varLo;
    }

    /**
     *  Return the "high" Dj Var, varHi.
     *
     *  @return varHi the "high" Var based on Var.id.
     */
    public Var getVarHi() {
        return varHi;
    }

    /**
     *  Set the "high" Dj Var, varHi.
     *
     *  @param varHi the "high" Var based on Var.id.
     */
    public void setVarHi(Var varHi) {
        this.varHi = varHi;
    }

    /**
     *  Converts DjVars to String.
     *
     *  @return returns DjVars string;
     *
     */
    public String toString() {
        return (LangConstants.DJVARS_LEFT_BRACKET
                + varLo.getId()
                + LangConstants.DJVARS_SEPARATOR
                + varHi.getId()
                + LangConstants.DJVARS_RIGHT_BRACKET);
    }

    /*
     * Computes hashcode for this DjVars.
     *
     * @return hashcode for the Order.
     */
    public int hashCode() {
        return (this.toString()).hashCode();
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
    public boolean equals(Object obj) {
        return (this == obj) ? true
                : !(obj instanceof DjVars) ? false
                        : ((varLo == ((DjVars)obj).varLo) &&
                           (varHi == ((DjVars)obj).varHi));
    }

    /**
     * Compares DjVars object based on the variables.
     *
     * @param obj Order object to compare to this Order
     *
     * @return returns negative, zero, or a positive int
     * if this Order object is less than, equal to
     * or greater than the input parameter obj.
     *
     */
    public int compareTo(Object obj) {
        int compare               =
            varLo.compareTo(((DjVars)obj).varLo);
        if (compare == 0) {
            compare               =
            varHi.compareTo(((DjVars)obj).varHi);
        }
        return compare;

//      return  ((this.toString()).compareTo(
//               ((DjVars)obj).toString()));
    }
}
