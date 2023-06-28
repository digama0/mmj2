//********************************************************************/
//* Copyright (C) 2005, 2006                                         */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * Stmt.java  0.06 08/01/2007
 *
 * Sep-30-2005: change getMandHypArray() to
 *              getMandHypArrayLength().
 *
 * Dec-14-2005: - Store parse tree instead of
 *                for exprRPN to improve performance
 *                of ProofAsst. Method getExprRPN()
 *                will perform conversion from tree
 *                to RPN.
 *              - added isLogHyp()
 *              - added logHypsMaxDepth and associated
 *                set/reset routines for use in Proof
 *                Assistant. See Assrt.java for associated
 *                getter routine.
 *              - added logHypsL1HiLoKey and associated
 *                stuff. See Assrt.java.
 *              - added new constructor (protected) for
 *                use in Proof Assistant. Creates "temp"
 *                LogHyp object (which creates temp Hyp,
 *                etc.)
 * Version 0.05:
 *
 * Aug-27-2006: - added renderParsedSubExpr() for TMFF project
 * Oct-12-2006: - added SymTbl to constructor and modified to
 *                conform to Metamath.pdf spec change of 6-24-2006
 *                prohibiting Stmt label and Sym id namespace
 *                collisions.
 *
 * Version 0.06 - 08/01/2007
 *              - Misc Work Var Enhancements.
 */
package mmj.lang;

import java.util.Comparator;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONString;

import mmj.lang.ParseTree.RPNStep;

/**
 * Stmt is the parent class of all Metamath {@code Hyp}s (hypotheses) and
 * {@code Assrt}s.
 * <p>
 * Every Stmt has a label, which uniquely identifies it within the Stmt
 * namespace (no rule against have a Stmt with a label equal to a Sym's id
 * though.)
 * <p>
 * Every Stmt has a Formula, even {@code VarHyp}s.
 * <p>
 * Every Stmt also has a Type Code -- which is the first symbol of the Formula,
 * and must be a Cnst. (This is a key point!) Everything following the Type Code
 * in the Stmt's Formula is referred to in mmj as the "Expression" or the
 * "Formula's Expression". (Make a note -- the nomenclature can be confusing,
 * but Metamath requires every "formula" to have a Type Code, and what other
 * books/works refer to as the "formula" equates to mmj's Expression.)
 * <p>
 * Metamath statements and their correspondence to mmj "Stmt":
 * <ul>
 * <li>$f -- mmj.lang.VarHyp.java (Variable Hypothesis, aka "floating")
 * <li>$e -- mmj.lang.LogHyp.java (Logical Hypothesis)
 * <li>$a -- mmj.lang.Axiom.java (aka "axiomatic assertion")
 * <li>$p -- mmj.lang.Theorem.java (aka "provable assertion")
 * <p>
 * The only goofball Metamath statements that do not form mmj Stmt's, (other
 * than Cnst ("$c") and Var ($v) which inherit from Sym), are:
 * <ul>
 * <li>$d -- mmj.lang.DjVars.java (Disjoint Variable Restriction)
 * <li>$[ -- mmj.mmio.IncludeFile.java (terminating "$]")
 * <li>$( -- Comment, not presently stored in mmj (terminating "$)")
 * <li>${ -- mmj.lang.ScopeDef.java (terminating "$}")
 * </ul>
 *
 * @see <a href="../../MetamathERNotes.html"> Nomenclature and
 *      Entity-Relationship Notes</a>
 */
public abstract class Stmt extends MObj implements JSONString {
    /**
     * note: label must NOT be changed after Stmt added to stmtTbl because
     * stmtTbl is a Map (map behavior undefined)
     */
    private final String label;

    /*
     * Formula is a separate class because of the need to
     * instantiates formula outside of the LogicalSystem,
     * for example, during Proof Verification.
     */
    Formula formula; // "friendly"/package access

    /**
     * exprRPN is the Reverse Polish Notation parse of the expression portion of
     * a statement's formula (the 2nd through nth symbols). Every statement has
     * a non-null exprRPN -- by definition. It is set by the SyntaxVerifier as
     * follows:
     * <ul>
     * <li>VarHyp : label of the VarHyp Stmt itself.</li>
     * <li>Syntax Axiom : labels of the Syntax Axiom's VarHyp's in database
     * sequence followed by the label of the Syntax Axiom itself.</li>
     * <li>other : RPN version of grammatical parse tree for the statement, if a
     * unique valid grammatical parse is found; otherwise, defaults to VarHyp
     * labels in database sequence followed by the label of the statement
     * itself. If multiple parse trees exist, the first found is chosen (and a
     * warning message is generated).
     * </ul>
     */
//  protected Stmt[]   exprRPN;
    protected ParseTree exprParseTree;

    /**
     * This contains the greatest maximum depth among Parse Trees in the
     * Assrt.logHypArray. It is stored here so that the value can be easily
     * reinitialized by Grammar if the statements are reparsed and the parse
     * trees are updated. The value is calculated dynamically, if and when
     * needed in Assrt.
     */
    protected int logHypsMaxDepth = -1;

    /**
     * This contains the labels of the root node Syntax Axiom for the
     * Assrt.logHypArray with lowest and greates sequence numbers. If one of the
     * LogHyp root nodes contains a VarHyp then the HiLoKey is set to the
     * default value. We only want the computed value if every LogHyp has a
     * Syntax Axiom in the root node of the parse tree. The Proof Assistant uses
     * this field to reject candidate assertions from consideration for
     * unification with a proof step. Because the Proof Assistant has to compute
     * the order of LogHyps, we can rely only on the pair of Hi/Lo keys: if the
     * candidate HiLo key is present and not equal to the proof step's LogHyps
     * Hi/Lo key, then unification is impossible (i.e. this is a heuristic.) For
     * example, if we know that there is a "wi" root node in the candidate
     * assertion and the proof step's log hyps do not have a "wi" root node
     * among them, then unification cannot be achieved. It is stored here so
     * that the value can be easily reinitialized by Grammar if the statements
     * are reparsed and the parse trees are updated. The value is calculated
     * dynamically, if and when needed in Assrt.
     */
    protected String logHypsL1HiLoKey = null;

    protected int nbrProofRefs;

    /**
     * Construct using sequence number and id string.
     *
     * @param seq MObj.seq number
     * @param symTbl Symbol Table (Map)
     * @param stmtTbl Statement Table (Map)
     * @param labelS Stmt label string
     * @throws LangException if label is empty string, or is a duplicate of
     *             another label in stmtTbl or an id in the symTbl.
     */
    public Stmt(final int seq, final Map<String, Sym> symTbl,
        final Map<String, Stmt> stmtTbl, final String labelS)
            throws LangException
    {
        super(seq);

        if (labelS.length() == 0)
            throw new IllegalArgumentException(new LangException(
                LangConstants.ERRMSG_STMT_LABEL_STRING_EMPTY));
        if (stmtTbl.containsKey(labelS))
            throw new LangException(LangConstants.ERRMSG_DUP_STMT_LABEL,
                labelS);
        if (symTbl.containsKey(labelS))
            throw new LangException(
                LangConstants.ERRMSG_STMT_LABEL_DUP_OF_SYM_ID, labelS);

        label = labelS;
    }

    /**
     * Construct temp Stmt using precomputed values and doing no validation.
     *
     * @param tempSeq MObj.seq
     * @param tempLabel Stmt.label
     * @param tempFormula Stmt.formula
     * @param tempParseTree Stmt.exprParseTree
     */
    protected Stmt(final int tempSeq, final String tempLabel,
        final Formula tempFormula, final ParseTree tempParseTree)
    {
        super(tempSeq);
        label = tempLabel;
        formula = tempFormula;
        setExprParseTree(tempParseTree);
        setIsTempObject(true);
    }

    /**
     * Is Stmt active?
     * <p>
     * A Stmt defined at the global level -- outside of any scope -- is always
     * active.
     * <p>
     * A statement defined inside a non-global scope level can only be referred
     * to within that scope level or another scope level nested within the
     * first.
     * <p>
     * Note: {@code Stmt}s refer to other {@code Stmt}s only within proofs and
     * exprRPN (grammatical parses), but since Syntax Axioms must be defined at
     * the global level, the question of a Stmt being "active" is only relevant
     * to proofs.
     *
     * @return is Stmt "active"
     */
    public abstract boolean isActive();

    /**
     * Return mandatory VarHyp Array
     *
     * @return mandatory VarHyp Array
     */
    public abstract VarHyp[] getMandVarHypArray();

    /**
     * Return mandatory Hyp Array length Note: the Hyp array is different from
     * the VarHyp array because the Hyp array can also include LogHyp's.
     *
     * @return mandatory Hyp Array Length
     */
    public abstract int getMandHypArrayLength();

    /**
     * Converts a parse sub-tree into a sub-expression which is output into a
     * String Buffer.
     * <p>
     * Note: this will not work for a proof node! The ParseNode's stmt must be a
     * VarHyp or a Syntax Axiom.
     * <p>
     * The output sub-expression is generated into text not to exceed the given
     * maxLength. If the number of output characters exceeds maxLength output
     * terminates, possibly leaving a dirty StringBuilder.
     * <p>
     * The depth of the sub-tree is checked against the input maxDepth
     * parameter, and if the depth exceeds this number, output terminates,,
     * possibly leaving a dirty StringBuilder.
     * <p>
     * Depth is computed as 1 for each Notation Syntax Axiom Node. VarHyp nodes
     * and Nulls Permitted, Type Conversion and NamedTypedConstant Syntax Axiom
     * nodes are assigned depth = 0 for purposes of depth checking.
     *
     * @param sb StringBuilder already initialized for appending characters.
     * @param maxDepth maximum depth of Notation Syntax axioms in sub-tree to be
     *            printed. Set to Integer.MAX_VALUE to turn off depth checking.
     * @param maxLength maximum length of output sub-expression. Set to
     *            Integer.MAX_VALUE to turn off depth checking.
     * @param child array of ParseNode, corresponding to VarHyp nodes to be
     *            substituted into the Stmt.
     * @return length of sub-expression characters appended to the input
     *         StringBuilder -- or -1 if maxDepth or maxLength exceeded.
     */
    public abstract int renderParsedSubExpr(StringBuilder sb, int maxDepth,
        int maxLength, ParseNode[] child);

    /**
     * Return Stmt label.
     *
     * @return Stmt label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Return Stmt Formula.
     *
     * @return Stmt Formula
     */
    public Formula getFormula() {
        return formula;
    }

    /**
     * Return Stmt Type Code.
     *
     * @return Stmt Type Code.
     */
    public Cnst getTyp() {
        return formula.getTyp();
    }

    // note: this one looks a little too dangerous to
    // implement w/out a pressing need and further
    // thought. LogHyp and Assrt both have type-specific
    // setFormula methods if there is a real need to
    // do this.
    // public void setFormula(Formula formula) {
    // this.formula = formula;
    // }

    /**
     * Set Stmt Type Code.
     *
     * @param typ Stmt Type Code.
     */
    public void setTyp(final Cnst typ) {
        formula.setTyp(typ);
    }

    /**
     * Return exprRPN, the statement's parse RPN.
     *
     * @return exprRPN.
     */
    public RPNStep[] getExprRPN() {
        // return exprRPN; 12/14/2005 ProofAsst fix
        if (exprParseTree == null)
            return null;
        return exprParseTree.convertToRPN();
    }

    /**
     * Set exprRPN, the statement's parse RPN.
     *
     * @param exprRPN array of Stmt!
     */
    public void setExprRPN(final RPNStep[] exprRPN) {
//      this.exprRPN = exprRPN; 12/14/2006 fix for ProofAsst
        if (exprRPN == null)
            setExprParseTree(null);
        else
            setExprParseTree(new ParseTree(exprRPN));
    }

    /**
     * Set the computed max depth value for the LogHyps in an Assrt. The
     * set/reset functions are here for convenience of updating via Grammar when
     * statements are reparsed.
     *
     * @param logHypsMaxDepth the new max depth value
     */
    public void setLogHypsMaxDepth(final int logHypsMaxDepth) {
        this.logHypsMaxDepth = logHypsMaxDepth;
    }

    /**
     * Reset the computed max depth value for the LogHyps in an Assrt to the
     * default value.
     */
    public void resetLogHypsMaxDepth() {
        logHypsMaxDepth = -1;
    }

    /**
     * Set the computed Hi/Lo key for an Assertions LogHyps. The set/reset
     * functions are here for convenience of updating via Grammar when
     * statements are reparsed.
     *
     * @param logHypsL1HiLoKey computed value.
     */
    public void setLogHypsL1HiLoKey(final String logHypsL1HiLoKey) {
        this.logHypsL1HiLoKey = logHypsL1HiLoKey;
    }

    /**
     * Reset the computed Hi/Lo key for an Assertions LogHyps to the default
     * value.
     */
    public void resetLogHypsL1HiLoKey() {
        logHypsL1HiLoKey = null;
    }

    /**
     * Get exprParseTree, the statement's parse tree.
     *
     * @return exprParseTree Parse Tree from Grammar
     */
    public ParseTree getExprParseTree() {
        return exprParseTree;
    }

    /**
     * Set exprParseTree, the statement's parse tree.
     *
     * @param parseTree Parse Tree from Grammar
     */
    public void setExprParseTree(final ParseTree parseTree) {
        exprParseTree = parseTree;
        resetLogHypsMaxDepth();
        resetLogHypsL1HiLoKey();
    }

    public int getNbrProofRefs() {
        return nbrProofRefs;
    }

    public void initNbrProofRefs() {
        nbrProofRefs = 0;
    }

    public int incrementNbrProofRefs() {
        return ++nbrProofRefs;
    }

    /**
     * Converts to String.
     * <p>
     * Output is simply Stmt.label.
     *
     * @return returns Stmt string;
     */
    @Override
    public String toString() {
        return label;
    }

    /**
     * Needed for JSONification of Stmts.
     *
     * @return returns Stmt string;
     */
    @Override
    public String toJSONString() {
        return JSONObject.quote(label);
    }

    /*
     * Computes hashcode for this Stmt
     *
     * @return hashcode for the Stmt
     */
    @Override
    public int hashCode() {
        return label.hashCode();
    }

    /*
     * Compare for equality with another Stmt.
     * Equal if and only if the Stmt label strings are equal.
     * and the obj to be compared to this object is not null
     * and is a Stmt as well.
     *
     * @return returns true if equal, otherwise false.
     */
    @Override
    public boolean equals(final Object obj) {
        return this == obj
            || obj instanceof Stmt && label.equals(((Stmt)obj).label);
    }

    /**
     * LABEL sequences by Stmt.label
     */
    public static final Comparator<Stmt> LABEL = new Comparator<Stmt>() {
        public int compare(final Stmt o1, final Stmt o2) {
            return o1.label.compareTo(o2.label);
        }
    };

    public static final Comparator<Stmt> DESC_NBR_PROOF_REFS = new Comparator<Stmt>() {
        public int compare(final Stmt o1, final Stmt o2) {
            final int i = o2.nbrProofRefs - o1.nbrProofRefs;
            return i != 0 ? i : LABEL.compare(o1, o2);
        }

    };
}
