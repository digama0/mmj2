//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * Formula.java  0.07 08/01/2008
 *
 * Dec 31, 2005
 * -->Added toProofWorksheetString() for ProofAsst
 * -->Added constructor using ArrayList of Syms
 * -->Moved accumHypInList from LogicFormula to Formula
 * -->Cloned getParseNodeHolderExpr() to accept Hyp[]
 *    instead of VarHyp[] for use in ProofWorksheet.java.
 *
 * Version 0.04 - Release 11/01/2006:
 *
 * Sep 2, 2006:
 * --> For TMFF project, cloned toProofWorksheetString()
 *     to make toProofWorksheetStringBuilder() so that
 *     the number of output lines can be returned, as
 *     desired by TMFF.
 * --> Added Formula.constructTempDummyFormula() to
 *     temporarily hold something like "|- ?".
 *
 * Oct 6, 2006:
 * --> Added computeWidthOfWidestExprCnst() for TMFF
 *
 * Version 0.05: 08/01/2007
 *     - Work Var Enhancement misc. changes.
 *
 * Version 0.06: 02/01/2008
 * --> Add exprToString()
 *
 * Version 0.07: 08/01/2008
 * --> Add srcStmtEquals()
 * --> remove unused toProofWorksheetString() variant
 * --> Added toStringBuilderLineList() for MMTTheoremExportFormatter.
 */

package mmj.lang;

import java.util.*;

import mmj.mmio.SrcStmt;

/**
 * Formula is, basically just an array of Sym, with a counter.
 * <p>
 * Sub-Classes of Formula exist: VarHypFormula and LogicFormula but their
 * function is basically just to simplify certain coding in VarHyp, LogHyp, etc.
 * In theory, the distinction would be useful for creating default exprRPN's.
 * <p>
 * Formula: contains Cnst in sym[0], followed by the formula's "expression",
 * which consists of zero or more Cnst's and Var's in sym[1]...sym[cnt - 1].
 * <p>
 * Formula Factoids:
 * <ul>
 * <li>A formula must have length >= 1.
 * <li>sym[0], the first Sym in a Formula must be a Cnst.
 * <li>sym[0] is the Type Code of the Formula, and by extension, the Stmt's Type
 * Code.
 * <li>sym[1] through sym[sym.length - 1] are referred to in mmj as the
 * Formula's "Expression" -- which is simply the Formula minus its Type Code.
 * <li>Every Stmt has a Formula, but a Formula can be created without a
 * corresponding Stmt (which is the reason Formula was made into a class.)
 *
 * @see <a href="../../MetamathERNotes.html"> Nomenclature and
 *      Entity-Relationship Notes</a>
 */
public class Formula {

    /**
     * Count of number of symbols in the formula.
     * <p>
     * In a valid Formula, {@code cnt == sym.length}.
     */
    int cnt;

    /**
     * Formula is just an array of Sym with a Count.
     */
    final Sym[] sym;

    /**
     * The sorted list of all constants in this formula.
     */
    private Cnst[] constList = null;

    /**
     * The comparator for the sorted list of all constants in this formula.
     */
    private Comparator<Cnst> constComp = null;

    /**
     * Construct a temporary dummy Formula for transient use. This is useful in
     * ProofWorksheet.
     *
     * @param typ is the Formula's TypCd Cnst.
     * @param dummySym a string of characters that should not have non-printable
     *            characters or whitespace (else renderFormula may come out
     *            wrong!)
     * @return the dummy Formula
     * @throws LangException if dummySym is empty
     */
    public static Formula constructTempDummyFormula(final Cnst typ,
        final String dummySym) throws LangException
    {

        final Cnst dummyCnst = new Cnst(Integer.MAX_VALUE, dummySym);
        dummyCnst.setIsTempObject(true);

        final Sym[] tempSym = new Sym[2];
        tempSym[0] = typ;
        tempSym[1] = dummyCnst;

        return new Formula(tempSym.length, tempSym);
    }

    /**
     * Construct using cnt and Sym array.
     *
     * @param workCnt the correct length of the formula.
     * @param workFormula the formula's Sym array.
     */
    public Formula(final int workCnt, final Sym[] workFormula) {
        sym = new Sym[cnt = workCnt];

        // arraycopy was tried and it turned out to be slower!
        // System.arraycopy(workFormula, 0, sym, 0, cnt);
        for (int i = 0; i < cnt; i++)
            sym[i] = workFormula[i];
    }

    /**
     * Construct using Sym List. Enforces rule that first symbol must be a Cnst,
     * just in case.
     *
     * @param symList List containing formula symbols
     */
    public Formula(final Collection<Sym> symList) {
        sym = symList.toArray(new Sym[cnt = symList.size()]);
        assert sym[0] instanceof Cnst;
    }

    /**
     * Construct Formula of given size and Type.
     *
     * @param symTbl Symbol Table (Map)
     * @param sz Size of the formula
     * @param typS Formula Type code
     * @throws LangException if an error occurred
     */
    protected Formula(final Map<String, Sym> symTbl, final int sz,
        final String typS) throws LangException
    {
        sym = new Sym[sz];
        cnt = 1;
        setTyp(symTbl, typS);
    }

    /**
     * Construct using String Type Code and Sym List.
     * <p>
     * Verifies that each Sym id in Expression is active and accumulates the
     * matching VarHyp's in the input exprHypList param.
     * <p>
     * <ol>
     * <li>verify that each symbol string in an expression is defined and
     * active, and that each referenced variable is defined by an active
     * variable hypothesis (in scope). NOTE: this routine is not used for
     * disjoint variables because they do not have "expressions" as such, and
     * also, they may not have active variable hypotheses.
     * <li>build a Sym[] as the symbol strings are scanned, thus converting the
     * source strings to unique object references.
     * <li>while building Sym[], build the set of variable hypotheses for the
     * variables in the expression, and store them in the input hypList.
     * </ol>
     *
     * @param symTbl Map containing Cnst and Var definitions.
     * @param typS Formula Type Code String.
     * @param symList expression's symbol character strings
     * @param hypList List of Hyp's. Is updated with unique variable hypotheses
     *            in the expression. Because the list is maintained in database
     *            statement sequence order, hypList should either be empty (new)
     *            before the call, or already be in that order (see
     *            {@code accumHypInList}.
     * @throws LangException if duplicate symbol, etc. (see
     *             {@code mmj.lang.LangConstants.java})
     */
    public Formula(final Map<String, Sym> symTbl, final String typS,
        final List<String> symList, final List<Hyp> hypList)
            throws LangException
    {
        this(symTbl, symList.size() + 1, typS);

        for (final String symS : symList) {
            sym[cnt] = symTbl.get(symS);
            if (sym[cnt] == null)
                throw new LangException(LangConstants.ERRMSG_EXPR_SYM_NOT_DEF,
                    symS);
            if (!sym[cnt].isActive())
                throw new LangException(
                    LangConstants.ERRMSG_EXPR_SYM_NOT_ACTIVE, symS);
            if (sym[cnt] instanceof Var) {
                final VarHyp varHyp = ((Var)sym[cnt]).getActiveVarHyp();
                if (varHyp == null)
                    throw new LangException(
                        LangConstants.ERRMSG_EXPR_VAR_W_O_ACTIVE_VAR_HYP, symS);
                // add varHyp to mandatory hypotheses in hypList
                Formula.accumHypInList(hypList, varHyp);
            }
            cnt++;
        }
    }

    /**
     * This function should be used to collect frequency statistic for some
     * metamath library and create an array of constant symbols.
     *
     * @param frequency the map from constant to its frequency in the metamath
     *            library
     */
    public void collectConstFrequenceAndInitConstList(
        final Map<Cnst, Integer> frequency)
    {
        if (constList != null)
            assert constList == null;
        final Set<Cnst> set = new HashSet<>();
        for (final Sym s : getSym())
            if (s instanceof Cnst) {
                final Cnst c = (Cnst)s;
                if (!set.contains(c)) {
                    set.add(c);
                    if (frequency != null) {
                        final Integer numObj = frequency.get(c);
                        int num = numObj != null ? numObj : 0;
                        num++;
                        frequency.put(c, num);
                    }
                }
            }
        constList = set.toArray(new Cnst[set.size()]);
    }

    /**
     * When the frequency information has been collected this function should be
     * used in order to sort the constant symbols array
     *
     * @param comp the comparator
     */
    public void sortConstList(final Comparator<Cnst> comp) {
        assert constComp == null;
        constComp = comp;
        Arrays.sort(constList, comp);
    }

    /**
     * This function is needed to exclude quickly the incompatible with this
     * formulas.
     *
     * @param other the other formula which should be checked for constant set
     *            inclusion
     * @return true if all constants from this formula are in the other formula
     */
    public boolean preunificationCheck(final Formula other) {
        if (constList == null)
            return true;
        assert constComp != null;

        if (other.constList == null) {
            other.collectConstFrequenceAndInitConstList(null);
            other.sortConstList(constComp);
        }

        if (other.constList.length < constList.length)
            return false;

        int i = 0, k = 0;

        mainLoop: while (i < constList.length) {
            while (k < other.constList.length) {
                if (constList[i] == other.constList[k]) {
                    i++;
                    k++;
                    continue mainLoop;
                }
                k++;
            }
            return false;
        }

        return true;
    }
    /**
     * Return Formula Type Code.
     *
     * @return Formula Type Code (which is sym[0]).
     */
    public Cnst getTyp() {
        return (Cnst)sym[0];
    }

    /**
     * Set Formula Type Code.
     *
     * @param typ Formula Type Code (sym[0]).
     */
    public void setTyp(final Cnst typ) {
        sym[0] = typ;
    }

    /** @return Formula's Expression (sym[1]...sym[cnt - 1]). */
    public Sym[] getExpr() {
        final Sym[] expr = new Sym[cnt - 1];
        System.arraycopy(sym, 1, expr, 0, cnt - 1);
        return expr;
    }

    /** @return Formula's length. */
    public int getCnt() {
        return cnt;
    }

    /** @return Formula's symbol array. */
    public Sym[] getSym() {
        return sym;
    }

    /**
     * Return the Formula's Var (sym[1]), assuming this is a VarHyp formula.
     *
     * @return the Formula's Var (sym[1]).
     */
    public Var getVarHypVar() {
        return (Var)sym[1];
    }

    /**
     * Set Formula Type Code.
     *
     * @param symTbl Symbol Table (Map).
     * @param typS Type Code String identifying a Cnst.
     * @return Type Code symbol.
     * @throws LangException if Type Code is undefined or not defined as a Cnst.
     */
    public Sym setTyp(final Map<String, Sym> symTbl, final String typS)
        throws LangException
    {

        final Sym typC = symTbl.get(typS);
        if (typC == null)
            throw new LangException(LangConstants.ERRMSG_STMT_TYP_UNDEF, typS);
        if (!(typC instanceof Cnst))
            throw new LangException(
                LangConstants.ERRMSG_STMT_TYP_NOT_DEF_AS_CNST, typS);
        return sym[0] = typC;
    }

    /**
     * Builds a "custom" version of an Expression in which an array of
     * ParseNodeHolders is output for use in generating a ParseTree.
     * <p>
     * The key fact about the output ParseNodeHolders is that a Cnst in the
     * Formula Expression just goes into the holder's "mObj" -- it will not be
     * part of the ParseTree and the ParseNode element is null.
     * <p>
     * On the other hand, Variables in the Formula's Expression are converted
     * into ParseNodes with Stmt = the VarHyp; the ParseNodeHolder's "mObj"
     * element is set to the VarHyp Stmt reference -- and this ParseNode will be
     * part of the ultimate Parse Tree. In effect, we're "parsing" VarHyps and
     * creating their output ParseNodes at this time.)
     *
     * @param varHypArray Array of VarHyp for Formula.
     * @return ParseNodeHolder array.
     * @throws IllegalArgumentException if unable to find a VarHyp for one of
     *             the Formula's Var's.
     */
    public ParseNodeHolder[] getParseNodeHolderExpr(
        final VarHyp[] varHypArray)
    {
        final ParseNodeHolder[] parseNodeHolderExpr = new ParseNodeHolder[cnt
            - 1];
        int dest = 0;
        VarHyp vH;

        // start at i = 1 to bypass the Cnst at Formula.sym[0]
        for (int i = 1; i < cnt; i++) {
            if (sym[i] instanceof Var) {
                vH = ((Var)sym[i]).getVarHyp(varHypArray);
                if (vH == null) {
                    if (sym[i] instanceof WorkVar)
                        vH = ((Var)sym[i]).getActiveVarHyp();
                    if (vH == null)
                        throw new IllegalArgumentException(new LangException(
                            LangConstants.ERRMSG_FORMULA_VAR_HYP_NOTFND, sym[i],
                            this));
                }
                parseNodeHolderExpr[dest] = new ParseNodeHolder(vH);
            }
            else
                parseNodeHolderExpr[dest] = new ParseNodeHolder((Cnst)sym[i]);
            dest++;
        }
        return parseNodeHolderExpr;
    }

    /**
     * Builds a "custom" version of an Expression in which an array of
     * ParseNodeHolders is output for use in generating a ParseTree.
     *
     * @param hypArray Array of Hyp for Formula.
     * @return ParseNodeHolder array.
     * @throws IllegalArgumentException if unable to find a VarHyp for one of
     *             the Formula's Var's.
     */
    public ParseNodeHolder[] getParseNodeHolderExpr(final Hyp[] hypArray) {
        final ParseNodeHolder[] parseNodeHolderExpr = new ParseNodeHolder[cnt
            - 1];
        int dest = 0;
        VarHyp vH;

        // start at i = 1 to bypass the Cnst at Formula.sym[0]
        for (int i = 1; i < cnt; i++) {
            if (sym[i] instanceof Var) {
                vH = ((Var)sym[i]).getVarHyp(hypArray);
                if (vH == null) {
                    if (sym[i] instanceof WorkVar)
                        vH = ((Var)sym[i]).getActiveVarHyp();
                    if (vH == null)
                        throw new IllegalArgumentException(new LangException(
                            LangConstants.ERRMSG_FORMULA_VAR_HYP_NOTFND, sym[i],
                            this));
                }
                parseNodeHolderExpr[dest] = new ParseNodeHolder(vH);
            }
            else
                parseNodeHolderExpr[dest] = new ParseNodeHolder((Cnst)sym[i]);
            dest++;
        }
        return parseNodeHolderExpr;
    }

    /**
     * Builds a "rule format" version of the Formula's Expression. Each Cnst in
     * the Expression is output unchanged, while the Type Code (a Cnst) of each
     * Var is output instead of the var -- this requires looking up the Var's
     * VarHyp in the input varHypArray. For example, Expression "( ph -> ps )"
     * is output as "( wff -> wff )".
     *
     * @param varHypArray Array of VarHyp for Formula.
     * @return ruleFormatExpr in an array of Cnst.
     * @throws IllegalArgumentException if unable to find a VarHyp for one of
     *             the Formula's Var's.
     */
    public Cnst[] buildRuleFormatExpr(final VarHyp[] varHypArray) {
        final Cnst[] ruleFormatExpr = new Cnst[cnt - 1];
        int dest = 0;
        VarHyp vH;

        // start at i = 1 to bypass the Cnst at Formula.sym[0]
        for (int i = 1; i < cnt; i++) {
            if (sym[i] instanceof Var) {
                vH = ((Var)sym[i]).getVarHyp(varHypArray);
                if (vH == null)
                    throw new IllegalArgumentException(new LangException(
                        LangConstants.ERRMSG_FORMULA_VAR_HYP_NOTFND, sym[i],
                        this));
                ruleFormatExpr[dest] = vH.getTyp();
            }
            else
                ruleFormatExpr[dest] = (Cnst)sym[i];
            dest++;
        }
        return ruleFormatExpr;
    }

    /**
     * Uses an array of Hyps to build an array of VarHyps containing only the
     * VarHyps needed for the variables actually used in the Formula.
     * <p>
     * Note: if the input array of Hyps does not contain all of the necessary
     * VarHyps, an IllegalArgumentException is thrown! No mercy.
     *
     * @param tempHypArray array of Hyp.
     * @return ruleFormatExpr in an array of Cnst.
     * @throws IllegalArgumentException if unable to find a VarHyp for one of
     *             the Formula's Var's.
     */
    public VarHyp[] buildMandVarHypArray(final Hyp[] tempHypArray) {
        final List<VarHyp> hypList = new ArrayList<>();
        // start at i = 1 to bypass the Cnst at Formula.sym[0]
        for (int i = 1; i < cnt; i++) {
            if (!(sym[i] instanceof Var))
                continue;
            final VarHyp vH = ((Var)sym[i]).getVarHyp(tempHypArray);
            if (vH != null) {
                Formula.accumHypInList(hypList, vH);
                continue;
            }
            throw new IllegalArgumentException(new LangException(
                LangConstants.ERRMSG_FORMULA_VAR_HYP_NOTFND, sym[i], this));
        }
        return hypList.toArray(new VarHyp[hypList.size()]);
    }

    /**
     * Computes hashcode for this Formula.
     *
     * @return hashcode for the Formula
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Compare Expression for equality with another Formula's expr (don't
     * compare Type Codes, in other words).
     * <p>
     * Equal if and only if the Sym strings are equal. and the obj to be
     * compared to this object is not null and is a Formula as well.
     *
     * @param obj Formula whose Expression will be compared to this Formula's
     *            Expression.
     * @return returns true if equal, otherwise false.
     */
    public boolean exprEquals(final Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Formula && cnt == ((Formula)obj).cnt))
            return false;
        for (int i = 1; i < cnt; i++)
            if (sym[i] != ((Formula)obj).sym[i])
                return false;
        return true;
    }

    /**
     * Compare SrcStmt typ and symList to Formula.
     *
     * @param srcStmt from mmj.mmio.Statementizer.java.
     * @return returns true if label and symList are identical to the Formula
     *         otherwise false.
     */
    public boolean srcStmtEquals(final SrcStmt srcStmt) {

        if (cnt != srcStmt.symList.size() + 1
            || sym[0].getId().compareTo(srcStmt.typ) != 0)
            return false;
        for (int i = 1; i < cnt; i++)
            if (sym[i].getId().compareTo(srcStmt.symList.get(i - 1)) != 0)
                return false;
        return true;
    }

    /**
     * Compare for equality with another Formula.
     * <p>
     * Equal if and only if the Sym strings are equal. and the obj to be
     * compared to this object is not null and is a Formula as well.
     *
     * @param obj Formula that will be compared to this Formula.
     * @return returns true if equal, otherwise false.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Formula && cnt == ((Formula)obj).cnt))
            return false;
        for (int i = 0; i < cnt; i++)
            if (sym[i] != ((Formula)obj).sym[i])
                return false;
        return true;
    }

    /**
     * Computes a character string version of Formula for printing.
     * <p>
     * Note: LogicalSystem does not validate for "printable" characters in Sym,
     * and output of certain non-printable characters can cause abrupt
     * termination of DOS windows (though mmj.mmio does extensive validation,
     * LogicalSystem itself is unconcerned about the contents of Sym id
     * strings.)
     *
     * @return String for the Formula
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(sym.length * 3);
        sb.append(sym[0].getId());
        for (int i = 1; i < sym.length; i++) {
            sb.append(' ');
            sb.append(sym[i].getId());
        }
        return sb.toString();
    }

    /**
     * Computes a character string version of the expression portion of the
     * formula.
     * <p>
     * Note: the "expression" here is the 2nd thru nth symbols of the formula --
     * that is, the type code at the start is discarded.
     *
     * @return String for the expression portion of Formula
     */
    public String exprToString() {
        final StringBuilder sb = new StringBuilder(sym.length * 3);
        String ws = "";
        for (int i = 1; i < sym.length; i++) {
            sb.append(ws);
            sb.append(sym[i].getId());
            ws = " ";
        }
        return sb.toString();
    }

    /**
     * Formats formula symbol strings into a column of characters within a text
     * area. Weird because it does not indent on the first line, but the output
     * can be simply "append"ed to the text area, which presumably already has
     * data to the left of "leftColContinuation" on line 1. the first line of
     * text -- to
     *
     * @param sb StringBuilder to append to.
     * @param leftColContinuation the leftmost column in the text area for use
     *            by formulas.
     * @param marginRight the rightmost column for use by formulas.
     * @return nbrLines used by the output formula.
     */
    public int toProofWorksheetStringBuilder(final StringBuilder sb,
        final int leftColContinuation, final int marginRight)
    {

        int nbrLines = 1;

        String s;

        int currCol = leftColContinuation - 1;

        for (final Sym element : sym) {
            s = element.toString();
            currCol += s.length();
            if (currCol > marginRight) {
                sb.append('\n');
                nbrLines++;
                currCol = leftColContinuation + sym[0].getId().length();
                for (int j = 0; j < currCol; j++)
                    sb.append(' ');
            }
            sb.append(s);
            if (currCol < marginRight) {
                sb.append(' ');
                currCol++;
            }
        }
        return nbrLines;
    }

    /**
     * Formats formula into StringBuilder lines in a List.
     *
     * @param list list of StringBuilder lines.
     * @param sb StringBuilder to append to.
     * @param leftColContinuation the leftmost column in the text area for use
     *            by formulas.
     * @param marginRight the rightmost column for use by formulas.
     * @param endToken string such as "$." or "$="
     * @return final StringBuilder line in use.
     */
    public StringBuilder toStringBuilderLineList(final List<StringBuilder> list,
        StringBuilder sb, final int leftColContinuation, final int marginRight,
        final String endToken)
    {

        String s;

        int currCol = leftColContinuation - 1;

        for (final Sym element : sym) {
            s = element.toString();
            currCol += s.length();
            if (currCol > marginRight) {
                list.add(sb);
                sb = new StringBuilder(marginRight);
                currCol = leftColContinuation + sym[0].getId().length();
                for (int j = 0; j < currCol; j++)
                    sb.append(' ');
            }
            sb.append(s);
            if (currCol < marginRight) {
                sb.append(' ');
                currCol++;
            }
        }
        if (endToken != null) {
            if (currCol + endToken.length() > marginRight) {
                list.add(sb);
                sb = new StringBuilder(marginRight);
            }
            sb.append(endToken);
        }
        return sb;
    }

    /**
     * Accumulate unique hypotheses (no duplicates), storing them in an array
     * list in order of their appearance in the database.
     *
     * @param <T> the actual type of the list
     * @param hypList -- List of Hyp's. Is updated with unique variable
     *            hypotheses in the expression. Because the list is maintained
     *            in database statement sequence order, hypList should either be
     *            empty (new) before the call, or already be in that order (see
     *            {@code accumHypInList}.
     * @param hypNew candidate Hyp to be added to hypList if not already there.
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
     * Computes the width in characters of the widest Cnst in the Formula's
     * Expression.
     *
     * @return width in characters of the widest Cnst in the Formula's
     *         Expression -- or -1 if the Expression is null or if it contains
     *         no constants.
     */
    public int computeWidthOfWidestExprCnst() {
        int max = -1;
        int len;
        for (int i = 1; i < cnt; i++)
            if (sym[i] instanceof Cnst) {
                len = sym[i].getId().length();
                if (len > max)
                    max = len;
            }
        return max;
    }
}
