//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * Assrt.java  0.08 08/01/2008
 *
 * Sep-30-2005: change getMandHypArray() to
 *              getMandHypArrayLength().
 *
 * Dec-14-2005: - add new (redundant) logHypArray
 *                for the convenience of ProofAsst.
 *              - made loadVarHypArray, loadHypArray,
 *                loadDjVarsArray and accumHypInList
 *                static for use in ProofAsst.
 *              - added isLogHyp()
 *              - added isAxiom()
 *              - added getLogHypsMaxDepth (see
 *                associated code in Stmt.java)
 *
 * Version 0.04 Oct-12-2006:
 *              - added SymTbl to constructor and modified to
 *                conform to Metamath.pdf spec change of 6-24-2006
 *                prohibiting Stmt label and Sym id namespace
 *                collisions.
 *
 * Version 0.05 Jun-01-2007
 *              - move loadDjVarsArray to DjVars class.
 *
 * Version 0.06 - 08/01/2007
 *              - Misc Work Var Enhancements.
 *
 * Version 0.07 - 03/01/2008
 *              - Additions for mmj.pa.StepSelectorSearch.java:
 *                    Assrt.sortListIntoArray() and
 *                    Assrt.NBR_LOG_HYP_SEQ
 *              - Added sortedLogHypArray,
 *                      loadSortedLogHypArray()
 *                  and hypVarsInCommonWithAssrt().
 *                 Plus getSortedLogHypArray(),
 *                  and modified loadLogHypArray() to call
 *                      loadSortedLogHypArray() during
 *                      object construction.
 *
 * Version 0.08 - 08/01/2008
 */

package mmj.lang;

import java.util.*;

import mmj.verify.VerifyProofs;

/**
 * Assrt is an "Assertion", of which there are two main kinds, Axiom and Theorem
 * -- known in Metamath as "Axiomatic Assertions" and "Provable Assertions".
 * What do Assertions have in common that Hypotheses do not? "Mandatory" Frames.
 *
 * @see <a href="../../MetamathERNotes.html"> Nomenclature and
 *      Entity-Relationship Notes</a>
 */
public abstract class Assrt extends Stmt {

    /**
     * varHypArray contains *exactly* the Assrt's Formula's VarHyp's, in
     * database sequence.
     * <p>
     * varHypArray is an array of length zero if the Assrt's Formula contains no
     * variables.
     * <p>
     * Note: varHypArray is somewhat redundant, but note that MandFrame contains
     * not only VarHyp's used in an Expression's Formula, but the LogHyp's that
     * are in scope, <b>and the VarHyp's used by those LogHyp's!</b>.
     * <p>
     */
    protected VarHyp[] varHypArray;

    /**
     * logHypArray contains *exactly* the Assrt's Formula's LogHyp's, in
     * database sequence.
     * <p>
     * logHypArray is an array of length zero if the Assrt has no LogHyp's.
     * <p>
     * logHypArray is redundant with the data in ScopeFrame, but ProofAsst needs
     * quick access to the LogHyp's as well as the actual number of LogHyp's.
     * <p>
     */
    protected LogHyp[] logHypArray;

    /**
     * sortedLogHypArray contains the contents of logHypArray as sorted by
     * loadSortedLogHypArray().
     * <p>
     * The sorted LogHyps are for the benefit of ProofAssistant.
     */
    private LogHyp[] sortedLogHypArray;

    /** The reverse permutation for sortedLogHypArray */
    private int[] reversePermutationForSortedHyp;

    /**
     * The assertion's Mandatory Frame.
     */
    protected ScopeFrame mandFrame;

    /**
     * Construct using a boatload of parameters.
     *
     * @param seq MObj.seq number
     * @param scopeDefList the Scope list
     * @param symTbl Symbol Table (Map)
     * @param stmtTbl Statement Table (Map)
     * @param labelS Label String identifying the Assrt
     * @param typS Type Code String (first sym of formula)
     * @param symList Formula's Expression (2nd thru nth syms)
     * @throws LangException variety of errors :)
     */
    public Assrt(final int seq, final List<ScopeDef> scopeDefList,
        final Map<String, Sym> symTbl, final Map<String, Stmt> stmtTbl,
        final String labelS, final String typS, final List<String> symList)
            throws LangException
    {
        super(seq, symTbl, stmtTbl, labelS);

        final List<Hyp> exprHypList = new ArrayList<>();
        formula = new Formula(symTbl, typS, symList, exprHypList);

        varHypArray = exprHypList.toArray(new VarHyp[exprHypList.size()]);

        mandFrame = buildMandFrame(scopeDefList, exprHypList);

        loadLogHypArray();
    }

    /**
     * Return the *mandatory* varHypArray.
     * <p>
     * Note: the "varHypArray" contains only *mandatory* VarHyp's, hence the
     * name of this function, which is intended to highlight the point. These
     * are the VarHyp's use in the Assrt's Formula.
     *
     * @return varHypArray which contains only mandatory VarHyp's.
     */
    @Override
    public VarHyp[] getMandVarHypArray() {
        return varHypArray;
    }

    /**
     * Return the logHypArray.
     *
     * @return logHypArray for the Assrt.
     */
    public LogHyp[] getLogHypArray() {
        return logHypArray;
    }

    /**
     * Return the logHypArray length
     *
     * @return Assrt's logHypArray length.
     */
    public int getLogHypArrayLength() {
        return logHypArray.length;
    }

    /**
     * Set the logHypArray.
     *
     * @param logHypArray for the Assrt.
     */
    public void setLogHypArray(final LogHyp[] logHypArray) {
        this.logHypArray = logHypArray;
    }

    /**
     * Return the *mandatory* Hyp Array.Length
     *
     * @return hypArray length from the Assrt's MandFrame.
     */
    @Override
    public int getMandHypArrayLength() {
        return mandFrame.hypArray.length;
    }

    /**
     * Set the *mandatory* VarHyp Array.
     *
     * @param varHypArray VarHyp's used by the Assrt.
     */
    public void setMandVarHypArray(final VarHyp[] varHypArray) {
        this.varHypArray = varHypArray;
    }

    /**
     * Get the Assrt's MandFrame.
     *
     * @return Assrt's MandFrame.
     */
    public ScopeFrame getMandFrame() {
        return mandFrame;
    }

    /**
     * Set the Assrt's MandFrame.
     *
     * @param mandFrame Assrt's MandFrame.
     */
    public void setMandFrame(final ScopeFrame mandFrame) {
        this.mandFrame = mandFrame;
    }

    /**
     * Is the Assrt "active".
     * <p>
     * Yep. Always. An assertion is always "active" even if it is defined inside
     * a scope level. (Recall that, in practice, "active" simply means that a
     * statement can be referred to by a subsequent statement in a proof or
     * parse RPN.)
     * <p>
     * That is because Metmath "scope" is designed primarily to limit the
     * visibility of hypotheses so that variables can be reused with different
     * Types and so that logical hypotheses can be applied to just a single
     * assertion. In a sense, Metamath scopes are merely notational shorthand,
     * and as Metamath.pdf explains, every assertion has an "Extended Frame"
     * (look it up for more info.)
     *
     * @return true (assertions are always "active")
     */
    @Override
    public boolean isActive() {
        // assertions are always active at global scope.
        return true;
    }

    /**
     * Loads the logHypArray using mandFrame.
     * <p>
     * This is pretty redundant, but is an add-on for ProofAsst and is designed
     * to be as bombproof as possible -- the output logHypArray is guaranteed to
     * be in database sequence because it is created from MandFrame.hypArray.
     */
    public void loadLogHypArray() {

        final Hyp[] hyp = mandFrame.hypArray;

        int logHypCount = 0;
        for (final Hyp element : hyp)
            if (element instanceof LogHyp)
                logHypCount++;
        logHypArray = new LogHyp[logHypCount];

        logHypCount = 0;
        for (final Hyp element : hyp)
            if (element instanceof LogHyp)
                logHypArray[logHypCount++] = (LogHyp)element;

        loadSortedLogHypArray();
    }

    /**
     * Load the Assrt's MandFrame.
     * <p>
     * <ol>
     * <li>add the variable hypotheses referenced in the logical hypotheses to
     * the variable hypotheses list in the hypotheses list (input contains var
     * hyps used in assertion's expression.)
     * <li>add the logical hypotheses that are "in scope" to the the hypotheses
     * list
     * <li>add the DjVars that match variable hypotheses (pairs) in the
     * hypotheses list.
     * <li>convert lists to arrays
     * <li>return with the goodies.
     * </ol>
     * <p>
     * NOTE 3: "ddeeq1" in set.mm requires disjoint variables on w and x, but w
     * is not referenced directly in ddeeq1; rather, it employs w in the proof.
     * DjVars that do not match variable hypotheses are therefore added to the
     * OptFrame for use in proof verification!
     *
     * @see VerifyProofs#checkSubstToVars(int,int)
     * @param scopeDefList Scope List as of this Stmt's definition.
     * @param hypList Already partly filled in with variable hypotheses from the
     *            assertion's expression.
     * @return ScopeFrame Metamath ("mandatory") Frame for the Assrt.
     */
    private ScopeFrame buildMandFrame(final List<ScopeDef> scopeDefList,
        final List<Hyp> hypList)
    {
        for (final ScopeDef scopeDef : scopeDefList)
            for (int i = 0; i < scopeDef.scopeLogHyp.size(); i++) {
                final LogHyp logHyp = scopeDef.scopeLogHyp.get(i);

                for (final VarHyp element : logHyp.getMandVarHypArray())
                    Assrt.accumHypInList(hypList, element);

                Assrt.accumHypInList(hypList, logHyp);
            }

        final List<DjVars> djVarsList = new ArrayList<>();
        for (final ScopeDef scopeDef : scopeDefList)
            for (final DjVars djVars : scopeDef.scopeDjVars)
                if (areBothDjVarsInHypList(hypList, djVars)
                    && !djVarsList.contains(djVars))
                    djVarsList.add(djVars);

        final ScopeFrame mF = new ScopeFrame();

        mF.hypArray = hypList.toArray(new Hyp[hypList.size()]);
        mF.djVarsArray = djVarsList.toArray(new DjVars[djVarsList.size()]);

        return mF;
    }

    /**
     * Checks to see whether or not both variables in a DjVars pair are
     * referenced in a list of hypotheses.
     * <p>
     * Note: checks only the VarHyp's.
     *
     * @param hypList -- List containing hypotheses
     * @param djVars -- DjVars object containing 2 variables to be checked
     *            against the variables referenced in hypList.
     * @return boolean -- true if both DjVars variables are present in hypList,
     *         otherwise false.
     */
    private boolean areBothDjVarsInHypList(final List<Hyp> hypList,
        final DjVars djVars)
    {
        boolean loFound = false;
        boolean hiFound = false;
        Hyp hyp;
        for (int i = 0; i < hypList.size(); i++) {
            hyp = hypList.get(i);
            if (hyp instanceof VarHyp) {
                if (((VarHyp)hyp).getVar() == djVars.getVarLo())
                    loFound = true;
                if (((VarHyp)hyp).getVar() == djVars.getVarHi())
                    hiFound = true;
                if (loFound && hiFound)
                    return true;
            }
        }
        return false;
    }

    /**
     * Accumulate unique hypotheses (no duplicates), storing them in an array
     * list in order of their appearance in the database.
     * <p>
     * The input "hypList" is updated with unique variable hypotheses in the
     * expression.
     * <p>
     * Because hypList is maintained in database statement sequence order,
     * hypList should either be empty (new) before the call, or already be in
     * that order.
     *
     * @param <T> the actual type of the list
     * @param hypList List of Hyp's, updated here.
     * @param hypNew Candidate Hyp to be added to hypList if not already there.
     */
    public static <T extends Hyp> void accumHypInList(final List<T> hypList,
        final T hypNew)
    {
        int i = 0;
        final int iEnd = hypList.size();
        final int newSeq = hypNew.seq;
        int existingSeq;

        while (true) {
            if (i < iEnd) {
                existingSeq = hypList.get(i).seq;
                if (newSeq < existingSeq)
                    // insert here, at "i"
                    break;
                if (newSeq == existingSeq)
                    // don't add, already here.
                    return;
            }
            else
                // insert at end, which happens to be here at "i"
                break;
            i++;
        }
        hypList.add(i, hypNew);
        return;
    }

    /**
     * Dynamically computes, if needed, the larges maximum depth of parse trees
     * of logHypArray, and caches the value for later use.
     *
     * @return greatest maxDepth of parse trees for logHypArray.
     */
    public int getLogHypsMaxDepth() {
        if (logHypsMaxDepth == -1) {
            int hypMaxDepth = 0;
            int hypDepth;
            for (final LogHyp element : logHypArray)
                if ((hypDepth = element.getExprParseTree()
                    .getMaxDepth()) > hypMaxDepth)
                    hypMaxDepth = hypDepth;
            setLogHypsMaxDepth(hypMaxDepth);
        }

        return logHypsMaxDepth;
    }

    /**
     * Dynamically computes, if needed, the Hi and Lo keys of Level 1 (root) of
     * parse trees of logHypArray, and caches the value for later use.
     *
     * @return Level 1 HiLoKey of parse trees for logHypArray.
     */
    public String getLogHypsL1HiLoKey() {
        if (logHypsL1HiLoKey == null)
            if (logHypArray.length > 0) {
                Stmt hStmt;
                int n;
                String low = null;
                String high = null;
                int lowNbr = Integer.MAX_VALUE;
                int highNbr = Integer.MIN_VALUE;
                for (final LogHyp element : logHypArray) {
                    hStmt = element.getExprParseTree().getRoot().stmt;
                    if (hStmt instanceof VarHyp) {
                        setLogHypsL1HiLoKey("");
                        return logHypsL1HiLoKey;
                    }
                    n = hStmt.getSeq();
                    if (n < lowNbr) {
                        lowNbr = n;
                        low = hStmt.getLabel();
                    }
                    if (n > highNbr) {
                        highNbr = n;
                        high = hStmt.getLabel();
                    }
                }
                setLogHypsL1HiLoKey(high + " " + low);
            }
            else
                setLogHypsL1HiLoKey("");
        return logHypsL1HiLoKey;
    }

//not needed in StepSelectorSearch anymore, so comment out for now
    /**
     * Sorts a list of Assrt into an array.
     *
     * @param assrtList List of Assrt to be sorted.
     * @param comparator Comparator to be used for the sort.
     * @return Array of Assrt with size equal to the number of elements in the
     *         input list.
     */
    public static Assrt[] sortListIntoArray(final List<Assrt> assrtList,
        final Comparator<Assrt> comparator)
    {
        final Assrt[] assrtArray = assrtList
            .toArray(new Assrt[assrtList.size()]);
        Arrays.sort(assrtArray, comparator);
        return assrtArray;
    }

    /**
     * NBR_LOG_HYP_SEQ sequences by Stmt.seq
     */
    public static final Comparator<Assrt> NBR_LOG_HYP_SEQ = Comparator
        .comparingInt((final Assrt a) -> a.logHypArray.length)
        .thenComparingInt(a -> a.seq);

    /**
     * Loads sortedLogHypArray from logHypArray.
     * <p>
     * The purpose of this is to finalize the state of memory concerning the
     * loaded database and to get this sorting process completed for Proof
     * Assistanting.
     * <p>
     * Sort in descending order of formula length and if two hyps have the same
     * length and the new one has variables in common with its assertion's
     * formula's variables, put it first in the output (variables in common are
     * less likely to lead to a "false" set of unifications -- the
     * inconsistencies are caught quicker, in other words.)
     */
    private void loadSortedLogHypArray() {

        final LogHyp[] outArray = new LogHyp[logHypArray.length];
        final int[] rearrangeArray = new int[logHypArray.length];

        int outEnd;
        int outIndex;
        int iFormulaLength;
        int diff;
        LogHyp holdLogHyp1;

        iLoop: for (int i = 0; i < logHypArray.length; i++) {
            outEnd = i;
            holdLogHyp1 = logHypArray[i];
            iFormulaLength = holdLogHyp1.getFormula().getCnt();
            outIndex = 0;
            outLoop: while (true) {
                if (outIndex >= outEnd) {
                    outArray[outEnd] = holdLogHyp1;
                    rearrangeArray[outEnd] = i;
                    continue iLoop;
                }
                diff = outArray[outIndex].getFormula().getCnt()
                    - iFormulaLength;
                if (diff > 0) {
                    outIndex++;
                    continue; // look for shorter formula
                }
                if (diff < 0 || hypVarsInCommonWithAssrt(holdLogHyp1))
                    break; // insert here at outIndex
                while (true) {
                    outIndex++; // find formula with diff length
                    if (outIndex < outEnd)
                        if (outArray[outIndex].getFormula()
                            .getCnt() == iFormulaLength)
                            continue;
                    break outLoop; // insert here at outIndex
                }
            }
            /*
             * end of outLoop: insert here at outIndex, which means
             * shifting whatever is here downwards by one.
             */
            for (int k = outEnd; k > outIndex; k--) {
                outArray[k] = outArray[k - 1];
                rearrangeArray[k] = rearrangeArray[k - 1];
            }
            outArray[outIndex] = holdLogHyp1;
            rearrangeArray[outIndex] = i;
        }

        sortedLogHypArray = outArray; // whew!

        final int[] reverseArray = new int[logHypArray.length];

        for (int i = 0; i < logHypArray.length; i++)
            reverseArray[rearrangeArray[i]] = i;
        reversePermutationForSortedHyp = reverseArray;
    }

    /**
     * See if the LogHyp has any variables in common with the assertion. Note:
     * both of the VarHyp arrays are sorted in database order (*.getSeq());
     *
     * @param holdLogHyp1 the LogHyp the query
     * @return true if there is a common variable
     */
    private boolean hypVarsInCommonWithAssrt(final LogHyp holdLogHyp1) {
        final VarHyp[] h = holdLogHyp1.getMandVarHypArray();

        if (varHypArray.length == 0 || h.length == 0)
            return false;

        int iA = 0;
        int iH = 0;

        while (true) {
            if (h[iH] == varHypArray[iA])
                return true;
            if (h[iH].getSeq() < varHypArray[iA].getSeq()) {
                if (++iH < h.length)
                    continue;
                break;
            }
            if (++iA < varHypArray.length)
                continue;
            break;
        }

        return false;
    }

    /**
     * Return the sortedLogHypArray.
     *
     * @return sortedLogHypArray for the Assrt.
     */
    public LogHyp[] getSortedLogHypArray() {
        return sortedLogHypArray;
    }

    /** @return the reverse permutation for sortedLogHypArray */
    public int[] getReversePermutationForSortedHyp() {
        return reversePermutationForSortedHyp;
    }
}
