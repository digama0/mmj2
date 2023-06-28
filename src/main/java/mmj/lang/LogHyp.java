//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * LogHyp.java  0.07 11/01/2011
 *
 * Sep-30-2005: change getMandHypArray() to
 *              getMandHypArrayLength().
 * Jan-15-2006: add LogHyp.BuildTempLogHypObject
 *              for Proof Assistant
 *              (see mmj.pa.ProofUnifier.java)
 * Version 0.04:
 *     --> MObj has new attribute, isTempObject.
 *         Updated comment to make this clear.
 *
 * Version 0.05:
 *
 * Aug-27-2006: - added renderParsedSubExpr() for TMFF project
 *
 * Oct-12-2006: - added SymTbl to constructors and modified to
 *                conform to Metamath.pdf spec change of 6-24-2006
 *                prohibiting Stmt label and Sym id namespace
 *                collisions.
 *
 * Version 0.06 - 08/01/2007
 *              - Misc Work Var Enhancements.
 *
 * Version 0.07 - Nov-01-2011:  comment update.
 *     --> Add stmt label to ERRMSG_BAD_PARSE_STMT_1

 */

package mmj.lang;

import java.util.*;

/**
 * LogHyp -- Logical Hypothesis -- corresponds to the Metamath "$e", or
 * "essential hypothesis" statement.
 * <p>
 * Note: there is no rule stipulating that the "Logical Hypothesis" have a Type
 * Code of "|-" or its equivalent in the LogicalSystem under such discussion. In
 * other words, a "syntax hypothesis" could be "snuck in".
 *
 * @see <a href="../../MetamathERNotes.html"> Nomenclature and
 *      Entity-Relationship Notes</a>
 */
public class LogHyp extends Hyp {

    /**
     * varHypArray contains *exactly* the LogHyp's Formula's VarHyps, in
     * database sequence.
     * <p>
     * varHypArray in LogHyp is similar to varHypArray in Assrt.
     */
    private VarHyp[] varHypArray;

    /**
     * Construct LogHyp using sequence number plus label, Type Code and Sym list
     * Strings.
     *
     * @param seq MObj.seq number
     * @param symTbl Symbol Table (Map)
     * @param stmtTbl Statement Table (Map)
     * @param symList list of Syms comprising Expression
     * @param labelS Statement label String.
     * @param typS Type Code id String.
     * @throws LangException if duplicate, etc.
     */
    public LogHyp(final int seq, final Map<String, Sym> symTbl,
        final Map<String, Stmt> stmtTbl, final List<String> symList,
        final String labelS, final String typS) throws LangException
    {
        super(seq, symTbl, stmtTbl, labelS, true); // true = "active"

        final List<Hyp> exprHypList = new ArrayList<>();

        formula = new Formula(symTbl, typS, symList, exprHypList);

        varHypArray = new VarHyp[exprHypList.size()];
        for (int i = 0; i < varHypArray.length; i++)
            varHypArray[i] = (VarHyp)exprHypList.get(i);

    }

    /**
     * Construct temp LogHyp using precomputed values and doing no validation.
     *
     * @param tempLogHypSeq MObj.seq
     * @param tempLogHypLabel Stmt.label
     * @param tempLogHypFormula Stmt.formula
     * @param tempVarHypArray LogHyp.varHypArray
     * @param tempLogHypParseTree Stmt.exprParseTree
     */
    protected LogHyp(final int tempLogHypSeq, final String tempLogHypLabel,
        final Formula tempLogHypFormula, final VarHyp[] tempVarHypArray,
        final ParseTree tempLogHypParseTree)
    {
        super(tempLogHypSeq, tempLogHypLabel, tempLogHypFormula,
            tempLogHypParseTree, true); // true = "active"
        varHypArray = tempVarHypArray;
    }

    /**
     * Construct a LogHyp object that is temporary, in the sense that it will
     * not be added to the Logical System Statement Table or participate in
     * other features of mmj2.
     * <p>
     * Beware: no real validation is performed, except that bogus input may
     * generate IllegalArgumentException or ArrayOutOfBoundsException!
     *
     * @param tempLogHypSeq MObj.seq
     * @param tempLogHypLabel Stmt.label
     * @param tempLogHypFormula Stmt.formula
     * @param tempHypArray array containing VarHyps for formula but perhaps
     *            other Hyps as well
     * @param tempLogHypParseTree Stmt.exprParseTree
     * @return the new LogHyp object
     */
    public static LogHyp BuildTempLogHypObject(final int tempLogHypSeq,
        final String tempLogHypLabel, final Formula tempLogHypFormula,
        final Hyp[] tempHypArray, final ParseTree tempLogHypParseTree)
    {

        final VarHyp[] tempVarHypArray = tempLogHypFormula
            .buildMandVarHypArray(tempHypArray);
        return new LogHyp(tempLogHypSeq, tempLogHypLabel, tempLogHypFormula,
            tempVarHypArray, tempLogHypParseTree);
    }

    /**
     * Return the mandatory VarHyp array for this VarHyp.
     * <p>
     * Simply return varHypArray, the VarHyp's used in the LogHyp's Formula.
     *
     * @return varHyp Array for this LogHyp.
     */
    @Override
    public VarHyp[] getMandVarHypArray() {
        return varHypArray;
    }

    /**
     * Return the mandatory Hyp array length.
     *
     * @return varHyp Array for this LogHyp.
     */
    @Override
    public int getMandHypArrayLength() {
        return varHypArray.length;
    }

    /**
     * Set the VarHyp array for this VarHyp (they're all "mandatory").
     *
     * @param varHypArray array of VarHyp's used in Formula.
     */
    public void setMandVarHypArray(final VarHyp[] varHypArray) {
        this.varHypArray = varHypArray;
    }

    /**
     * Throws an IllegalArgumentException because a ParseTree for a parsed
     * sub-expression should contain only VarHyp and Syntax Axiom nodes.
     *
     * @param sb StringBuilder already initialized for appending characters.
     * @param maxDepth maximum depth of Notation Syntax axioms in sub-tree to be
     *            printed. Set to Integer.MAX_VALUE to turn off depth checking.
     * @param maxLength maximum length of output sub-expression. Set to
     *            Integer.MAX_VALUE to turn off depth checking.
     * @param child array of ParseNode, corresponding to VarHyp nodes to be
     *            substituted into the Stmt.
     * @return nothing
     * @throws IllegalArgumentException always
     */
    @Override
    public int renderParsedSubExpr(final StringBuilder sb, final int maxDepth,
        final int maxLength, final ParseNode[] child)
    {
        throw new IllegalArgumentException(
            new LangException(LangConstants.ERRMSG_BAD_PARSE_STMT, getLabel()));
    }

}
