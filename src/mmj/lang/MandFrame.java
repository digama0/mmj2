//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  MandFrame.java  0.06 08/01/2008
 *
 *  31-Dec-2005
 *  -->added convenience routines for ProofAsst.
 *
 *  Version 0.04 -- 07-Sep-2006
 *  -->removed unnecessary LangException declarations
 *     on constructors (doh?)
 *  --> move Assrt.loadDjVarsArray to DjVars class.
 *
 *  Version 0.05 -- 01-Jun-2007
 *  --> Added consolidateDvGroups(), etc.
 *
 *  Version 0.06 -- 01-Aug-2008
 *  --> Added buildConsolidatedDvArray()
 */

package mmj.lang;

import  java.util.ArrayList;
import  java.util.Iterator;
import  java.util.HashMap;

import  mmj.pa.DistinctVariablesStmt;

/**
 *  Mandatory "Frame" of an Assrt (assertion).
 *  <p>
 *  Add MandFrame to OptFrame and you have a Metamath
 *  "Extended Frame".
 *
 *  @see <a href="../../MetamathERNotes.html">
 *       Nomenclature and Entity-Relationship Notes</a>
 */
public class MandFrame {

    /**
     *  Default Constructor.
     */
    public MandFrame() {
    }

    /**
     *  These are the "mandatories" that are referenced in an
     *  assertion, in order of appearance in the database.
     *  <p>
     *  For a Theorem these include not only the variable
     *  hypotheses for variables referenced in the assertion's
     *  Formula, but the logical hypotheses in scope of the
     *  Theorem plus the variable hypotheses for variables
     *  referenced by *those* logical hypotheses.
     */
    public Hyp[]  hypArray;

    /**
     *  These are the disjoint variable (pair)restrictions, if any,
     *  that apply to the Assrt and any of its proof steps.
     */
    public DjVars[]  djVarsArray;


    /**
     *  Checks to see if a certain pair of variables is
     *  mentioned in a MandFrame's DjVars array.
     *  <p>
     *  Note: vLo and vHi are automatically switched
     *        if vLo is not low.
     *  <p>
     *  Note: this function is the reason that DjVars vars
     *        are stored as "lo" and "hi". Low and High
     *        are irrelevant to the mathematics of this
     *        situation. But if the vars were stored
     *        randomly or arbitrarily, then twice as
     *        many comparisons would be needed here.
     *
     *  @param frame Mandatory Frame to inspect.
     *  @param vLo   the "low" variable in the pair.
     *  @param vHi   the "high" variable in the pair.
     *
     *  @return true if param var pair in MandFrame DjVars array.
     */
    public static boolean isVarPairInDjArray(MandFrame frame,
                                             Var vLo,
                                             Var vHi) {
        Var vSwap;
        if ((vLo.compareTo(vHi)) > 0) {
            vSwap = vHi;
            vHi = vLo;
            vLo = vSwap;
        }
        for (int i = 0; i < frame.djVarsArray.length; i++) {
            if (frame.djVarsArray[i].varLo == vLo &&
                frame.djVarsArray[i].varHi == vHi) {
                return true;
            }
        }
        return false;
    }

    /**
     *  Converts an array of ProofWorksheet DistinctVariablesStmt
     *  objects into an array of DjVars objects.
     *  <p>
     *  @param distinctVariablesStmtArray array of
     *         DistinctVariablesStmt object.
     *  @return array of DjVars objects.
     */
    public static DjVars[] buildConsolidatedDvArray(
               DistinctVariablesStmt[] distinctVariablesStmtArray) {

        ArrayList dvArrayList     = new ArrayList();

        Var[][]   dvGroupArray    =
            new Var[distinctVariablesStmtArray.length][];
        for (int i = 0; i < distinctVariablesStmtArray.length; i++) {
            dvGroupArray[i]       =
                distinctVariablesStmtArray[i].getDv();
        }

        try {
            MandFrame.loadDvGroupsIntoList(dvArrayList,
                                           dvGroupArray);
        }
        catch (LangException e) {
            //this should never happen because we're
            //checking for dups and bypassing them,
            //so blow up on this occurrence!
            throw new IllegalArgumentException(
                LangConstants.
                    ERRMSG_DUP_DJ_VARS_AFTER_CONSOLIDATION_ERR_1
                + e.getMessage());
        }

        return DjVars.loadDjVarsArray(dvArrayList);
    }

    /**
     *  Accumulates new Distinct Variables into an
     *  ArrayList of DjVars pairs.
     *
     *  The dvGroupArray is Var[][], array of Var arrays.
     *
     *  Each Var array is of the form { x, y, z, ... }
     *  which generates DjVars for (x,y), (x,z), (y,z),
     *  etc. There is one Var array for each $d statement,
     *  in other words.
     *
     *  Duplicate DjVars pairs are dropped without
     *  notice, as are pairs such as (x, x).
     *
     *  Convenience method for ProofAsst's use with
     *  an existing theorem.
     *
     *  @param arrayList list of DjVars objects
     *  @param dvGroupArray array of Var[] arrays.
     */
    public static void loadDvGroupsIntoList(ArrayList arrayList,
                                            Var[][] dvGroupArray)
                                       throws LangException {
        DjVars djVars;
        int    found;

        if (dvGroupArray != null) {
            Var[] dvGroup;
            for (int i = 0; i < dvGroupArray.length; i++) {
                dvGroup           = dvGroupArray[i];
                for (int j = 0; j < dvGroup.length - 1; j++) {
                    for (int k = j + 1; k < dvGroup.length; k++) {
                        if (!dvGroup[j].equals(dvGroup[k])) {
                            djVars
                                  = new DjVars(dvGroup[j],
                                               dvGroup[k]);
                            found = arrayList.indexOf(djVars);
                            if (found == -1) {
                                arrayList.add(djVars);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *  Attempts to do a good job consolidating pairs of
     *  disjoint variables into groups of three or more.
     *  <p>
     *  The input dvArray is assumed to be in ascending order
     *  according to DjVars.compareTo. We assume AND
     *  require that it be free from duplicates. Here is an
     *  example of dvArray -- sorted and with no duplicates:
     *  <p>
     *
     *  <code>
     *  [(a,b),(a,c),(a,d),(b,c),(b,d),(b,e),(c,d),(c,f)]
     *  </code>
     *
     *  The output ArrayList contains an ordered set of
     *  ArrayLists of disjoint variables, sorted by the
     *  first Var character string in the list. Here is
     *  sample output for the example above:
     *  <p>
     *  <code>
     *  [ [a,b,c,d], [b,e], [c,f] ]
     *  </code>
     *  <p>
     *  The algorith should be capable of consolidating
     *  $d's into </code> [ [a,x,y,z], [b,x,y,z] ]</code>,
     *  as well as </code> [ [x,y,z,a], [x,y,z,b] ]</code>.
     *  <p>
     *  Note that within a DjVars object. varLo and varHi
     *  are loaded by Var.Id. Thus, for disjoint vars "a" and
     *  "b", varHo = a, and varHi = b. Also, varLo is never
     *  equal to varHi.
     *  <p>
     */
    public static ArrayList consolidateDvGroups(DjVars[] dvArray) {

        ArrayList     dvGroupList = new ArrayList(); //the output

        // "done" is a parallel array for dvArray. done[i] set to
        // true when dvArray[i] has been stored in the output.
        boolean[]  done           = new boolean[dvArray.length];

        // "checked" is an ArrayList of Integers used to hold
        // the indexes of dvArray elements that match the
        // searched-for varLo and varHi(s), signifying that they
        // are disjoint. checked is needed so that when
        // a candidate Var is added to a dvGroup, all
        // corresponding dvArray elements can be marked "done" --
        // and if the candidate is rejected, the checked contents
        // are discarded (this saves an extra scan of dvArray).
        ArrayList  checked        = new ArrayList();

        // choices are to insert a new Var just *before* the end
        // of a dvGroup's last Var or *at* the end. See below...
        boolean    insertInDvGroupBeforeEnd;
        int        insertIndex;

        // currVarLoFirst and Last define a "chunk" of the
        // input wherein each DjVars object has a matching
        // varLo.
        int        currVarLoFirst = 0;
        int        currVarLoLast;
        Var currVarLo;

        // pass through the dvArray "chunks", one at a time.
        while (currVarLoFirst < dvArray.length) {

            currVarLo             = dvArray[currVarLoFirst].varLo;

            // here we find the index of the last dvArray element
            // in the "chunk" with a matching varLo
            currVarLoLast         = currVarLoFirst + 1;
            while (true) {
                if (currVarLoLast < dvArray.length &&
                    dvArray[currVarLoLast].varLo == currVarLo) {
                    ++currVarLoLast;
                }
                else {
                    --currVarLoLast;
                    break;
                }
            }

            // for each dvArray element within the varLo
            // chunk (subset) of dvArray, create a new dvGroup --
            // unless the dvArray element is already marked "done":
            for (int i = currVarLoFirst; i <= currVarLoLast; i++) {

                if (done[i]) {
                    continue;
                }

                // the minimum length of a dvGroup is 2
                ArrayList dvGroup = new ArrayList();
                dvGroup.add(dvArray[i].varLo);
                dvGroup.add(dvArray[i].varHi);
                done[i]           = true;

                // Now, for each dvArray element in
                // the current chunk (has matching varLo),
                // see if its varHi is disjoint with
                // every Var in the new dvGroup. If so, insert!
                // Insert before end of DvGroup if j < i --
                // otherwise at end.
                insertInDvGroupBeforeEnd
                                  = true;
                for (int j  = currVarLoFirst;
                         j <= currVarLoLast;
                         j++) {
                    if (j == i) {
                        insertInDvGroupBeforeEnd
                                  = false;
                        continue;
                    }
                    if (MandFrame.areAllDisjoint(dvArray[j].varHi,
                                                 dvGroup,
                                                 dvArray,
                                                 currVarLoLast,
                                                 checked)) {

                        //success! consolidated another one!
                        done[j]   = true;
                        if (insertInDvGroupBeforeEnd) {
                            insertIndex
                                  = dvGroup.size() - 1;
                            dvGroup.add(   //shift last Var right
                                dvGroup.get(insertIndex));
                            dvGroup.set(   // add new prior to last
                                insertIndex,
                                dvArray[j].varHi) ;
                        }
                        else {
                            dvGroup.add(dvArray[j].varHi);
                        }

                        // use "checked" to mark as "done" every
                        // dvArray element that participated --
                        // their disjoint variable restriction info
                        // is now reflected in the dvGroup's info!
                        for (int k = 0; k < checked.size(); k++) {
                            done[(Integer)checked.get(k)]
                                  = true;
                        }
                    }
                }

                // add the new dvGroup to the output -- this will
                // ultimately generate *one* $d statement with
                // two or more variables:
                dvGroupList.add(dvGroup);
            }

            currVarLoFirst        = currVarLoLast + 1; //next chunk!
        }

        return dvGroupList;
    }

    /**
     *  Builds a Frame using a ScopeDef and an array of
     *  Distinct Variables, limited by an input maximum
     *  sequence number.
     *
     *  Convenience method for ProofAsst's use on a new
     *  theorem. ProofAsst allows new theorems, assuming
     *  that all variables and hypotheses used are in
     *  global scope, but it also provides a "LocAfter"
     *  statement specification, so maxSeq is input to
     *  restrict the contents of the new Frame.
     *
     *  LogHyp's in the scopeDef are not loaded into
     *  the Frame -- not expected at global scope level,
     *  and global LogHyp's are not used in ProofAsst
     *  (haven't seen one yet, just saying).
     *
     *  @param scopeDef ScopeDef, intended to be global scope.
     *  @param maxSeq MObj.seq must be < maxSeq
     */
    public MandFrame(ScopeDef scopeDef,
                     int      maxSeq) {

        ArrayList arrayList;
        Iterator  iterator;
        Hyp       hyp;
        DjVars    djVars;

        if (maxSeq < Integer.MAX_VALUE) {
            arrayList                    =
                new ArrayList(scopeDef.scopeVarHyp.size());
            iterator              =
                scopeDef.scopeVarHyp.iterator();
            while (iterator.hasNext()) {
                hyp               = (Hyp)iterator.next();
                if (hyp.getSeq() < maxSeq) {
                    arrayList.add(hyp);
                }
            }
        }
        else {
            arrayList = scopeDef.scopeVarHyp;
        }
        hypArray                  =
            Assrt.loadHypArray(arrayList);


        arrayList                         =
            new ArrayList(scopeDef.scopeDjVars.size());
        iterator                  =
                scopeDef.scopeDjVars.iterator();
        while (iterator.hasNext()) {
            djVars                = (DjVars)iterator.next();
            if (djVars.getVarLo().getSeq() < maxSeq &&
                djVars.getVarHi().getSeq() < maxSeq) {
                arrayList.add(djVars);
            }
        }

        djVarsArray               =
            DjVars.loadDjVarsArray(arrayList);
    }

    /**
     *  Builds a composite ("combo") Frame using a
     *  MandFrame plus an OptFrame, augmented by
     *  additional $d specifications.
     *
     *  Convenience method for ProofAsst's use with
     *  an existing theorem.
     *
     *  @param mandFrame a Theorem's MandFrame
     *  @param optFrame  a Theorem's OptFrame
     */
    public MandFrame(MandFrame mandFrame,
                     OptFrame  optFrame) {

        ArrayList arrayList;
        Hyp       hyp;

        arrayList                 =
                new ArrayList(mandFrame.hypArray.length
                              +
                              optFrame.optHypArray.length);

        for (int i = 0; i < mandFrame.hypArray.length; i++) {
            Assrt.accumHypInList(arrayList,
                                 mandFrame.hypArray[i]);
        }
        for (int i = 0; i < optFrame.optHypArray.length; i++) {
            Assrt.accumHypInList(arrayList,
                                 optFrame.optHypArray[i]);
        }
        hypArray                  =
            Assrt.loadHypArray(arrayList);

        djVarsArray               =
            new DjVars[mandFrame.djVarsArray.length
                          +
                       optFrame.optDjVarsArray.length];

        int djCnt                 = 0;
        for (int i = 0; i < mandFrame.djVarsArray.length; i++) {
            djVarsArray[djCnt++]  = mandFrame.djVarsArray[i];
        }
        for (int i = 0; i < optFrame.optDjVarsArray.length; i++) {
            djVarsArray[djCnt++]  = optFrame.optDjVarsArray[i];
        }

    }

    /**
     *  Checks to see whether or not both variables in a DjVars
     *  pair are referenced in the MandFrame's array of hypotheses.
     *  <p>
     *  Note: checks only the VarHyp's.
     *  <p>
     * @param djVars -- DjVars object containing 2 variables to
     *                  be checked against the variables referenced
     *                  in the MandFrame hypArray.
     *
     * @return boolean -- true if both DjVars variables are present
     *                    in hypArray, otherwise false.
     */
    public boolean areBothDjVarsInHypArray(DjVars djVars) {
        boolean loFound = false;
        boolean hiFound = false;
        Hyp     hyp;
        VarHyp  varHyp;
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
     *  Accumulates an array of DvGroups into the djVarsArray.
     *  <p>
     *  FYI, a DvGroup is an array of at least 2 variables
     *  which are by definition supposed to be distinct from
     *  each other. So [ph th ch] is a DvGroup which could
     *  be obtained from a $d statement like "$d ph th ch $."
     *  <p>
     *  @param dvGroupArray array of array of disjoint variables.
     *  @throws LangException if a two variables in a DvGroup
     *          are identical (e.g. [ph ph ch]).
     */
    public void addDjVarGroups(Var[][] dvGroupArray) {
        ArrayList arrayList       =
            new ArrayList(djVarsArray.length +
                          dvGroupArray.length);
        for (int i = 0; i < djVarsArray.length; i++) {
            arrayList.add(djVarsArray[i]);
        }

        try {
            MandFrame.loadDvGroupsIntoList(arrayList,
                                           dvGroupArray);
        }
        catch (LangException e) {
            //this should never happen because we're
            //checking for dups and bypassing them,
            //so blow up on this occurrence!
            throw new IllegalArgumentException(
                LangConstants.
                    ERRMSG_DUP_DJ_VARS_AFTER_CONSOLIDATION_ERR_2
                + e.getMessage());
        }

        djVarsArray               =
            DjVars.loadDjVarsArray(arrayList);

    }

    /**
     *  Builds and returns Var HashMap derived from the
     *  MandFrame hypArray.
     *
     *  The returned map of Vars contains the vars returned
     *  from the MandFrame's VarHyps via getVar(), and does not
     *  seek out vars used in the formulas of LogHyps mentioned
     *  in the hypArray. Reason? The VarHyps for any LogHyps
     *  should *already* be in the MandFrame hypArray, by
     *  definition (of MandFrame).
     *
     *  Convenience method for ProofAsst's use.
     *
     *  @return HashMap of Vars for VarHyps in MandFrame.hypArray.
     */
    public HashMap getVarMap() {
        HashMap varMap            =
            new HashMap(((hypArray.length * 3) / 2)
                         + 1);
        Var    v;
        for (int i = 0; i < hypArray.length; i++) {
            if (hypArray[i].isVarHyp()) {
                v                 =
                    ((VarHyp)hypArray[i]).getVar();
                varMap.put(v.getId(),
                           v);
            }
        }
        return varMap;
    }

    /*
     *  Here we make a single scan of dvArray
     *  looking to match DjVars elements for the dvArray
     *  varHi element and each of the Var elements already
     *  in the dvGroup. This is a multiple search because
     *  we are looking for multiple "hits".
     *
     *  The indexes of the matching dvArray entries are
     *  stored in "checked", for use later in marking them
     *  "done".
     *
     *  If there are *any* NOTFND's, whatsoever, return
     *  false -- complete and total success in the search
     *  is required (signifying that the candidate varHi
     *  variable is disjoint with every Var in dvGroup!)
     *
     *  Note that the Vars in dvGroup are stored in
     *  ascending order by Sym.Id. This enables us to
     *  make a single pass through dvArray and perform
     *  all of the matches (dvArray is *also* sorted
     *  in ascending order.)
     */
    private static boolean areAllDisjoint(
                                    Var       dvArrayVar,
                                    ArrayList dvGroup,
                                    DjVars[]  dvArray,
                                    int       searchIndex,
                                    ArrayList checked) {
        checked.clear();


        DjVars search             = new DjVars();
        int    compare;
        Loop1: for (int i = 1; i < dvGroup.size(); i++) {

            search.varLo          = (Var)dvGroup.get(i);
            if (search.varLo.compareTo(dvArrayVar) > 0) {
                search.varHi      = search.varLo;
                search.varLo      = dvArrayVar;
            }
            else {
                search.varHi    = dvArrayVar;
            }

            Loop2: while (true) {
                if (++searchIndex >= dvArray.length) {
                    return false; //not found
                }
                if ((compare =
                     dvArray[searchIndex].compareTo(search))
                    > 0) {
                    return false; //not found
                }
                if (compare == 0) {
                    //found!
                    checked.add(new Integer(searchIndex));
                    continue Loop1;
                }
                continue Loop2;
            }
        }
        return true;
    }
}
